package com.example.messengerproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.adapters.ConversationsAdapter;
import com.example.messengerproject.adapters.MessagesAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagesHelper {
    Context context;
    RecyclerView messagesRecycleView;
    DatabaseReference messagesReference;
    ArrayList<Items.Message> messages;

    public MessagesHelper(Context context,
                          RecyclerView messagesRecycleView,
                          DatabaseReference messagesReference,
                          ArrayList<Items.Message> messages) {
        this.context = context;
        this.messagesRecycleView = messagesRecycleView;
        this.messagesReference = messagesReference;
        this.messages = messages;
    }

    // Тип сообщения
    public enum MessageType {
        text,
        image
    }

    // Метод для вывода сообщений
    public void displayMessages() {
        MessagesAdapter messagesAdapter = new MessagesAdapter(context, messages);
        messagesRecycleView.setHasFixedSize(true);
        messagesRecycleView.setLayoutManager(new LinearLayoutManager(context));
        messagesRecycleView.setAdapter(messagesAdapter);

        messagesReference.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                if (snapshot.getChildrenCount() == 0) {
                    messagesAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot messagesSnapshot : snapshot.getChildren()) {
                    messages.add(new Items.Message(
                            messagesSnapshot.getKey(),
                            messagesSnapshot.child("Sender").getValue().toString(),
                            messagesSnapshot.child("Time").getValue().toString(),
                            MessageType.text,
                            messagesSnapshot.child("Message").getValue().toString()));
                }

                messagesAdapter.notifyDataSetChanged();
                messagesRecycleView.smoothScrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                if (snapshot.getChildrenCount() == 0) {
                    messagesAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot messagesSnapshot : snapshot.getChildren()) {
                    messages.add(new Items.Message(
                            messagesSnapshot.getKey(),
                            messagesSnapshot.child("Sender").getValue().toString(),
                            messagesSnapshot.child("Time").getValue().toString(),
                            MessageType.text,
                            messagesSnapshot.child("Message").getValue().toString()));
                }
                messagesAdapter.notifyDataSetChanged();
                messagesRecycleView.smoothScrollToPosition(messages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
