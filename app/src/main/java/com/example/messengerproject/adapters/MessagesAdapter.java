package com.example.messengerproject.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private final ArrayList<Items.Message> messages;
    private final Context context;
    private final String currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

    private final androidx.appcompat.widget.Toolbar toolbar;

    DatabaseReference messagesReference;

    private static final int SENDER_POSITION = 0;
    private static final int RECIEVER_POSITION = 1;

    public MessagesAdapter(Context context,
                           ArrayList<Items.Message> messages,
                           DatabaseReference messagesReference,
                           androidx.appcompat.widget.Toolbar toolbar) {
        this.messages = messages;
        this.context = context;
        this.messagesReference = messagesReference;
        this.toolbar = toolbar;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderPhone().equals(currentUserPhoneNumber)) {
            return SENDER_POSITION;
        }
        return RECIEVER_POSITION;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_POSITION) {
            view = LayoutInflater.from(context).inflate(R.layout.item_send_message, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_recieved_message, parent, false);
        }

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Items.Message message = messages.get(position);

        String titleColor = "#ADA3AF";
        if (message.getSenderPhone().equals(currentUserPhoneNumber)) {
            // Инициализация элементов пользователя
            holder.initSender();

            setMessageBackColor(message, holder);

            // Слушатель на нажатие
            holder.itemView.setOnClickListener(view -> {
                message.setSelected(!message.isSelected());
                int selectedMessagesCount = selectedMessagesCount();
                // Edit
                toolbar.getMenu().getItem(0).setVisible(selectedMessagesCount == 1);
                // Delete
                toolbar.getMenu().getItem(1).setVisible(selectedMessagesCount != 0);

                setMessageBackColor(message, holder);
            });
        } else {
            // Инициализация элементов собеседника
            holder.initReciever();
        }

        Spanned text = Html.fromHtml("<font color='" + titleColor + "'>"
            + message.getSenderPhone() + "</font><br />"
            + message.getText());

        holder.mMessage.setText(text);
        holder.mTime.setText(message.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mMessage;
        TextView mTime;
        ConstraintLayout mConstraintLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void initReciever() {
            mMessage = itemView.findViewById(R.id.i_recieved_message_text);
            mTime = itemView.findViewById(R.id.i_recieved_message_time);
            mConstraintLayout = itemView.findViewById(R.id.i_recieved_message_main_constraint);
        }

        void initSender() {
            mMessage = itemView.findViewById(R.id.i_send_message_text);
            mTime = itemView.findViewById(R.id.i_send_message_time);
            mConstraintLayout = itemView.findViewById(R.id.i_send_message_main_constraint);
        }
    }

    private int selectedMessagesCount() {
        int counter = 0;
        for (Items.Message message : messages) {
            if (message.isSelected()) {
                counter++;
            }
        }
        return counter;
    }

    private void setMessageBackColor(Items.Message message, MessageViewHolder holder) {
        // Закраска заднего фона всех выделенных сообщений
        if (message.isSelected()) {
            holder.mConstraintLayout.setBackgroundColor(Color.GRAY);
        } else {
            holder.mConstraintLayout.setBackgroundColor(Color.WHITE);
        }
    }
}
