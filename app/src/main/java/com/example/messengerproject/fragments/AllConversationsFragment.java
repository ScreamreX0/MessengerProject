package com.example.messengerproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.ConversationsHelper;
import com.example.messengerproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllConversationsFragment extends Fragment implements ConversationsHelper.IConversationsFragment{
    // Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    DatabaseReference conversationsReference;  // Ссылка на все беседы в базе
    DatabaseReference userConversationsReference;  // Ссылка на все беседы пользователя в базе
    String userPhoneNumber;  // Номер телефона пользователя

    // Elements
    RecyclerView conversationsRecycleView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_conversations, container, false);

        if (!checkForUserAuth(firebaseAuth)) {
            return view;
        }

        init(view);

        displayConversations();

        return view;
    }

    @Override
    public void init(View view) {
        userPhoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber();

        conversationsRecycleView = view.findViewById(R.id.f_all_conversations_recycle_view);

        conversationsReference = FirebaseDatabase.getInstance()
                .getReference("Conversations");
        userConversationsReference = FirebaseDatabase.getInstance()
                .getReference("Users/" + userPhoneNumber + "/Conversations");
    }

    // Проверка на авторизацию пользователя
    @Override
    public boolean checkForUserAuth(FirebaseAuth firebaseAuth) {
        return firebaseAuth.getCurrentUser() != null;
    }

    // Вывод списка диалогов
    @Override
    public void displayConversations() {
        new ConversationsHelper(getContext(),
                conversationsRecycleView,
                userConversationsReference,
                conversationsReference,
                ConversationsHelper.ConversationType.anyType).displayConversations();
    }
}
