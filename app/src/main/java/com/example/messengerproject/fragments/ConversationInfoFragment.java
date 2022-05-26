package com.example.messengerproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.adapters.ConversationMembersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationInfoFragment extends DialogFragment {
    // Elements
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mUsersCount;
    private ImageButton mAddMemberButton;
    private TextView mAddMemberText;
    private RecyclerView mRecyclerView;
    private ImageButton mCloseButton;

    private String conversationId;
    private String conversationName;
    private ArrayList<Items.Contact> contacts;
    private FragmentManager fragmentManager;
    private Context context;
    ConversationMembersAdapter conversationMembersAdapter;

    // Firebase
    private DatabaseReference membersReference;
    private DatabaseReference conversationReference;

    public ConversationInfoFragment(Context context,
                                    String conversationId,
                                    String conversationName,
                                    FragmentManager fragmentManager) {
        this.context = context;
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_info, container, false);
        init(view);

        displayMembers();

        mTitle.setText(conversationName);

        mCloseButton.setOnClickListener(view1 -> {
            dismiss();
        });

        mAddMemberButton.setOnClickListener(view1 -> {
            addMember();
        });

        mAddMemberText.setOnClickListener(view1 -> {
            addMember();
        });

        return view;
    }

    private void init(View view) {
        this.mIcon = view.findViewById(R.id.f_conversations_info_icon);
        this.mTitle = view.findViewById(R.id.f_conversations_info_title);
        this.mUsersCount = view.findViewById(R.id.f_conversations_info_users_count);
        this.mAddMemberButton = view.findViewById(R.id.f_conversations_info_add_member_button);
        this.mAddMemberText = view.findViewById(R.id.f_conversations_info_add_member_text_view);
        this.mRecyclerView = view.findViewById(R.id.f_conversations_info_recycler_view);
        this.mCloseButton = view.findViewById(R.id.f_conversations_info_close);

        conversationReference = FirebaseDatabase.getInstance().getReference()
                .child("Conversations")
                .child(conversationId);

        membersReference = conversationReference
                .child("Members");

        contacts = new ArrayList<>();
    }

    private void displayMembers() {
        conversationMembersAdapter = getConversationMembersAdapter();
        mRecyclerView = getRecyclerView(mRecyclerView);

        membersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contacts.clear();

                for (DataSnapshot contact : snapshot.getChildren()) {
                    contacts.add(new Items.Contact(contact.getKey()));
                }

                conversationMembersAdapter.notifyDataSetChanged();
                showContactsCount(contacts.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ConversationMembersAdapter getConversationMembersAdapter() {
        return new ConversationMembersAdapter(getContext(), contacts, fragmentManager, conversationId);
    }

    private RecyclerView getRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(conversationMembersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        return recyclerView;
    }

    private void showContactsCount(int size) {
        mUsersCount.setText(size + " участников");
    }

    private void addMember() {
        new AddMemberFragment(context, conversationId, conversationName, fragmentManager).show(fragmentManager, "");
        dismiss();
    }
}
