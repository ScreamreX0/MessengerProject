package com.example.messengerproject;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.adapters.ConversationsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ConversationsHelper {
    Context context;
    ConversationType conversationType;
    ArrayList<Items.Conversation> conversations;
    ConversationsAdapter conversationsAdapter;

    // Elements
    RecyclerView conversationsRecyclerView;

    // Firebase
    DatabaseReference userConversationsReference;
    DatabaseReference conversationsReference;

    public ConversationsHelper(Context context,
                               ArrayList<Items.Conversation> conversations, RecyclerView conversationsRecycleView,
                               DatabaseReference userConversationsReference,
                               DatabaseReference conversationsReference,
                               ConversationType conversationType) {
        this.context = context;
        this.conversations = conversations;
        this.conversationsRecyclerView = conversationsRecycleView;
        this.userConversationsReference = userConversationsReference;
        this.conversationsReference = conversationsReference;
        this.conversationType = conversationType;
    }

    // Метод для вывода бесед
    public void displayConversations() {
        conversationsAdapter = getConversationAdapter();
        conversationsRecyclerView = getConversationsRecycleView(this.conversationsRecyclerView);

        userConversationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversations.clear();

                if (snapshot.getChildrenCount() == 0) {
                    conversationsAdapter.notifyDataSetChanged();
                    return;
                }

                conversationsReference.get().addOnSuccessListener(runnable -> {
                    if (!runnable.hasChildren()) {
                        return;
                    }

                    for (DataSnapshot userConversationSnapshot : snapshot.getChildren()) {
                        DataSnapshot conversation = runnable.child(userConversationSnapshot.getKey());
                        if (checkConversation(conversationType, conversation.child("Members"))) {
                            addConversationToArray(conversation);
                        }
                    }
                    conversationsAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private ConversationsAdapter getConversationAdapter() {
        return new ConversationsAdapter(context, conversations);
    }

    // Метод для проверки типа беседы
    private boolean checkConversation(ConversationType conversationType, DataSnapshot conversationSnapshot) {
        if (conversationType == ConversationType.anyType) {
            return true;
        } else if (conversationType == ConversationType.dialog
                && conversationSnapshot.getChildrenCount() == 2) {
            return true;
        } else if (conversationType == ConversationType.group
                && conversationSnapshot.getChildrenCount() > 2) {
            return true;
        } else if (conversationType == ConversationType.personal
                && conversationSnapshot.getChildrenCount() == 1) {
            return true;
        }
        return false;
    }

    private RecyclerView getConversationsRecycleView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(conversationsAdapter);

        return recyclerView;
    }

    private DataSnapshot getLastElement(Iterable<DataSnapshot> iterable) {
        Iterator<DataSnapshot> iterator = iterable.iterator();
        DataSnapshot lastSnapshot = iterator.next();
        while (iterator.hasNext()) {
            lastSnapshot = iterator.next();
        }

        return lastSnapshot;
    }

    private void addConversationToArray(DataSnapshot conversationSnapshot) {
        Iterable<DataSnapshot> messages = conversationSnapshot
                .child("Messages").getChildren();
        long messagesCount = conversationSnapshot.child("Messages").getChildrenCount();

        String lastMessage = "";
        String lastMessageTime = "";
        if (messagesCount > 0) {
            DataSnapshot lastMessageSnapshot = getLastElement(messages);
            lastMessage = lastMessageSnapshot.child("Message").getValue().toString();
            lastMessageTime = lastMessageSnapshot.child("Time").getValue().toString();
        }

        conversations.add(new Items.Conversation(
                conversationSnapshot.getKey(),
                conversationSnapshot.child("Name").getValue().toString(),
                lastMessage,
                lastMessageTime,
                conversationType));
    }

    // Тип беседы
    public enum ConversationType {
        anyType,  // 1 - ..
        personal,  // 1
        dialog,  // 2
        group  // 3 - ..
    }

    // Интерфейс для бесед
    public interface IConversationsFragment {
        void init(View view);
        void displayConversations();
    }
}



