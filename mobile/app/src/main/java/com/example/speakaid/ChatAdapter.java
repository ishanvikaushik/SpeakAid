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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.txtMessage.setText(msg.getText());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.layoutMessageBubble.getLayoutParams();
        if (msg.isSent()) {
            params.gravity = Gravity.END;
            holder.layoutMessageBubble.setBackgroundResource(R.drawable.duo_button_blue);
            holder.txtMessage.setTextColor(0xFFFFFFFF);
            holder.txtSender.setVisibility(View.GONE);
        } else {
            params.gravity = Gravity.START;
            holder.layoutMessageBubble.setBackgroundResource(R.drawable.duo_card_white);
            holder.txtMessage.setTextColor(0xFF333333);
            holder.txtSender.setText(msg.getSenderName());
            holder.txtSender.setVisibility(View.VISIBLE);
        }
        holder.layoutMessageBubble.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtSender;
        LinearLayout layoutMessageBubble;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtSender = itemView.findViewById(R.id.txtSender);
            layoutMessageBubble = itemView.findViewById(R.id.layoutMessageBubble);
        }
    }
}
