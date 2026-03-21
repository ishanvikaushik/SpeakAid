package com.example.speakaid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    List<Routine> routineList;
    OnItemClickListener listener;

    // Interface for click handling
    public interface OnItemClickListener {
        void onClick(Routine routine);
    }

    // Constructor
    public RoutineAdapter(List<Routine> routineList, OnItemClickListener listener) {
        this.routineList = routineList;
        this.listener = listener;
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoutine;

        public ViewHolder(View itemView) {
            super(itemView);
            txtRoutine = itemView.findViewById(R.id.txtRoutine);
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
        Routine routine = routineList.get(position);
        holder.txtRoutine.setText(routine.title);

        // Click handling
        holder.itemView.setOnClickListener(v -> {
            listener.onClick(routine);
        });
    }

    @Override
    public int getItemCount() {
        return routineList.size();
    }
}