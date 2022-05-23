package com.example.messengerproject;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.adapters.ConversationsAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationsHelper {
    Context context;
    RecyclerView conversationsRecycleView;
    DatabaseReference userConversationsReference;
    DatabaseReference conversationsReference;
    ConversationType conversationType;

    public ConversationsHelper(Context context,
                               RecyclerView conversationsRecycleView,
                               DatabaseReference userConversationsReference,
                               DatabaseReference conversationsReference,
                               ConversationType conversationType) {
        this.context = context;
        this.conversationsRecycleView = conversationsRecycleView;
        this.userConversationsReference = userConversationsReference;
        this.conversationsReference = conversationsReference;
        this.conversationType = conversationType;
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
        boolean checkForUserAuth(FirebaseAuth firebaseAuth);
        void displayConversations();
    }

    // Метод для вывода бесед
    public void displayConversations() {
        ArrayList<Items.Conversation> conversations = new ArrayList<>();
        ConversationsAdapter conversationsAdapter = new ConversationsAdapter(context, conversations);
        conversationsRecycleView.setHasFixedSize(true);
        conversationsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        conversationsRecycleView.setAdapter(conversationsAdapter);

        userConversationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conversationsReference.get().addOnSuccessListener(runnable -> {
                    if (!runnable.hasChildren()) {
                        return;
                    }

                    for (DataSnapshot userConversationSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot conversationSnapshot : runnable.getChildren()) {
                            if (userConversationSnapshot.getKey().equals(conversationSnapshot.getKey())) {
                                if (checkConversation(conversationType, conversationSnapshot)) {
                                    conversations.add(new Items.Conversation(
                                            conversationSnapshot.getKey(),
                                            conversationSnapshot.child("Name").getValue().toString()));
                                }
                            }
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
}



