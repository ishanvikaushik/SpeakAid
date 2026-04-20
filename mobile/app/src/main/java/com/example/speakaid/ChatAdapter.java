package com.example.speakaid;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messages;

    public ChatAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.textView.setText(msg.getText());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.textView.getLayoutParams();
        if (msg.isSent()) {
            params.gravity = Gravity.END;
            holder.textView.setBackgroundResource(R.drawable.duo_button_blue);
            holder.textView.setTextColor(0xFFFFFFFF);
        } else {
            params.gravity = Gravity.START;
            holder.textView.setBackgroundResource(R.drawable.duo_card_white);
            holder.textView.setTextColor(0xFF333333);
        }
        holder.textView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            textView.setPadding(32, 16, 32, 16);
        }
    }
}
