package com.example.messengerproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final ArrayList<Items.Message> messages;
    private final Context context;

    public MessagesAdapter(Context context, ArrayList<Items.Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);

        return new MessagesAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Items.Message message = messages.get(position);
        holder.mSenderName.setText(message.getSenderPhone());
        holder.mMessage.setText(message.getText());
        holder.mTime.setText(message.getMessageTime().toString());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mSenderName;
        TextView mMessage;
        TextView mTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            init();

        }

        private void init() {
            mSenderName = itemView.findViewById(R.id.i_message_sender);
            mMessage = itemView.findViewById(R.id.i_message_text);
            mTime = itemView.findViewById(R.id.i_message_time);
        }
    }
}
