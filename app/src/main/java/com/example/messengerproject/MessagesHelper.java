package com.example.messengerproject;

import android.content.Context;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
    androidx.appcompat.widget.Toolbar toolBar;
    MessagesAdapter messagesAdapter;

    public MessagesHelper(Context applicationContext,
                          RecyclerView mMessagesRecycleView,
                          DatabaseReference messagesReference,
                          ArrayList<Items.Message> messages,
                          androidx.appcompat.widget.Toolbar mToolBar) {
        this.context = applicationContext;
        this.messagesRecycleView = mMessagesRecycleView;
        this.messagesReference = messagesReference;
        this.messages = messages;
        this.toolBar = mToolBar;
    }

    // Тип сообщения
    public enum MessageType {
        text,
        image
    }

    // Метод для вывода сообщений
    public void displayMessages() {
        this.messagesAdapter = getMessagesAdapter();
        this.messagesRecycleView = getMessagesRecycleView(this.messagesRecycleView);



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

    private MessagesAdapter getMessagesAdapter() {
        return new MessagesAdapter(context, messages, messagesReference, toolBar);

    }

    private RecyclerView getMessagesRecycleView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(messagesAdapter);

        return recyclerView;
    }
}
