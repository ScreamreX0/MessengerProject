package com.example.messengerproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.activities.ContactsActivity;
import com.example.messengerproject.adapters.AddConversationAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddMemberFragment extends DialogFragment {
    // Elements
    private AppBarLayout mAppBar;
    private ImageButton mBackButton;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private Button mAddContact;

    private FragmentManager fragmentManager;
    private String conversationId;
    private String conversationName;
    private ArrayList<Items.Contact> contacts;
    private Context context;

    // Firebase
    private DatabaseReference contactsReference;
    private DatabaseReference conversationsReference;
    private String currentUserPhoneNumber;

    public AddMemberFragment(Context context, String conversationId, String conversationName, FragmentManager fragmentManager) {
        this.context = context;
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_member, container, false);
        init(view);

        displayContacts();

        mBackButton.setOnClickListener(view1 -> {
            new ConversationInfoFragment(context, conversationId, conversationName, fragmentManager).show(fragmentManager, "");
            dismiss();
        });

        mAddContact.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), ContactsActivity.class));
        });

        mToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.m_add_conversation_okay) {
                ArrayList<Items.Contact> selectedContacts = getSelectedContacts();

                if (selectedContacts.size() == 0) {
                    Toast.makeText(context, "Выберите собеседников", Toast.LENGTH_SHORT).show();
                    return false;
                }

                for (Items.Contact contact : selectedContacts) {
                    conversationsReference.child(conversationId)
                            .child("Members")
                            .child(contact.getPhoneNumber())
                            .setValue("user");
                }

                dismiss();
            }

            return false;
        });

        return view;
    }

    private void init(View view) {
        contacts = new ArrayList<>();

        mAppBar = view.findViewById(R.id.f_add_member_app_bar);
        mBackButton = view.findViewById(R.id.f_add_member_back_button);
        mSearchView = view.findViewById(R.id.f_add_member_search_view);
        mToolbar = view.findViewById(R.id.f_add_member_tool_bar);
        mRecyclerView = view.findViewById(R.id.f_add_member_recycler_view);
        mAddContact = view.findViewById(R.id.f_add_member_add_contacts);

        currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        contactsReference = FirebaseDatabase.getInstance().getReference("Users/" + currentUserPhoneNumber + "/Contacts");
        conversationsReference = FirebaseDatabase.getInstance().getReference("Conversations");
    }

    private void displayContacts() {
        AddConversationAdapter contactsAdapter = new AddConversationAdapter(getContext(), contacts);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(contactsAdapter);

        contactsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contacts.clear();

                for (DataSnapshot contactsSnapshot : snapshot.getChildren()) {
                    contacts.add(new Items.Contact(contactsSnapshot.getKey()));
                }

                contactsAdapter.notifyDataSetChanged();

                if (contacts.size() == 0) {
                    mAddContact.setVisibility(View.VISIBLE);
                } else {
                    mAddContact.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<Items.Contact> getSelectedContacts() {
        ArrayList<Items.Contact> selectedContacts = new ArrayList<>();
        for (Items.Contact contact : contacts) {
            if (contact.isSelected()) {
                selectedContacts.add(contact);
            }
        }

        return selectedContacts;
    }
}
