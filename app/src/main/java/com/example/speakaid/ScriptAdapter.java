package com.example.speakaid;

import android.content.Context;
import android.content.Intent;
import com.example.speakaid.Script;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScriptAdapter extends RecyclerView.Adapter<ScriptAdapter.ViewHolder> {

    Context context;
    ArrayList<Script> scriptList;

    public ScriptAdapter(Context context, ArrayList<Script> scriptList) {
        this.context = context;
        this.scriptList = scriptList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_script, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Script script = scriptList.get(position);

        holder.txtTitle.setText(script.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScriptPlayerActivity.class);
            intent.putExtra("scriptId", script.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return scriptList.size();
    }
}