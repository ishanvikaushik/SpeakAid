package com.example.speakaid;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    List<Routine> routineList;
    OnItemClickListener listener;
    DBHelper db;

    public interface OnItemClickListener {
        void onClick(Routine routine);
    }

    public RoutineAdapter(List<Routine> routineList, DBHelper db, OnItemClickListener listener) {
        this.routineList = routineList;
        this.db = db;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoutine, txtStepProgress;
        LinearProgressIndicator itemProgressIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            txtRoutine = itemView.findViewById(R.id.txtRoutine);
            txtStepProgress = itemView.findViewById(R.id.txtStepProgress);
            itemProgressIndicator = itemView.findViewById(R.id.itemProgressIndicator);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Routine routine = routineList.get(position);
        holder.txtRoutine.setText(routine.title);
        
        int totalSteps = 0;
        try (Cursor cursor = db.getSteps(routine.id)) {
            totalSteps = cursor.getCount();
        }

        // Get primary color from theme using the safe appcompat attribute
        TypedValue typedValue = new TypedValue();
        holder.itemView.getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        if (routine.completedDate != null) {
            holder.txtStepProgress.setText(context.getString(R.string.completed_today));
            holder.txtStepProgress.setTextColor(Color.parseColor("#58CC02")); // Duo Green
            holder.itemProgressIndicator.setProgress(100);
            holder.itemProgressIndicator.setIndicatorColor(Color.parseColor("#58CC02"));
            holder.itemView.setAlpha(0.8f);
        } else if (totalSteps > 0) {
            holder.itemView.setAlpha(1.0f);
            holder.itemProgressIndicator.setIndicatorColor(colorPrimary);
            holder.txtStepProgress.setTextColor(Color.parseColor("#AFAFAF"));
            
            if (routine.lastStep == 0) {
                holder.txtStepProgress.setText(context.getString(R.string.not_started));
                holder.itemProgressIndicator.setProgress(0);
            } else {
                int displayProgress = (int) (((float) routine.lastStep / totalSteps) * 100);
                String progressText = context.getString(R.string.step_progress,
                        (routine.lastStep + 1),
                        totalSteps,
                        displayProgress);

                holder.txtStepProgress.setText("Step " + (routine.lastStep + 1) + " of " + totalSteps + " (" + displayProgress + "%)");
                holder.itemProgressIndicator.setProgress(displayProgress, true);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            listener.onClick(routine);
        });
    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }
}
