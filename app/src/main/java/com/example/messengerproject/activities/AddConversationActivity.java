package com.example.messengerproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.adapters.AddConversationAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddConversationActivity extends AppCompatActivity {
    // Elements
    private AppBarLayout mAppBar;
    private SearchView mSearchView;
    private Toolbar mToolBar;
    private Toolbar mToolBarBack;
    private EditText mConversationNameEditText;
    private RecyclerView mRecyclerView;
    private Button mAddConversationButton;

    private ArrayList<Items.Contact> contacts;

    // Firebase
    private DatabaseReference contactsReference;
    private DatabaseReference conversationsReference;
    private DatabaseReference usersReference;
    private String currentUserPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conversation);
        init();

        setSupportActionBar(mToolBarBack);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        displayContacts();

        mToolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.m_add_conversation_okay) {
                String name = mConversationNameEditText.getText().toString();
                if (name.length() == 0
                        || name.replace(" ", "").equals("")) {
                    Toast.makeText(this, "Имя не должно быть пустым", Toast.LENGTH_SHORT).show();
                    return false;
                }

                ArrayList<Items.Contact> selectedContacts = getSelectedContacts();
                if (selectedContacts.size() == 0) {
                    Toast.makeText(this, "Выберите собеседника(-ков)", Toast.LENGTH_SHORT).show();
                    return false;
                }

                setUpConversation(name, selectedContacts);
                startActivity(new Intent(this, MainActivity.class));
                return false;
            }
            return false;
        });

        mAddConversationButton.setOnClickListener(view -> {
            startActivity(new Intent(this, ContactsActivity.class));
        });
    }

    private void init() {
        // Elements
        mAppBar = findViewById(R.id.a_add_conversation_app_bar);
        mSearchView = findViewById(R.id.a_add_conversation_search_view);
        mToolBar = findViewById(R.id.a_add_conversation_tool_bar);
        mToolBarBack = findViewById(R.id.a_add_conversation_tool_bar_back);
        mConversationNameEditText = findViewById(R.id.a_add_conversation_conversation_name);
        mRecyclerView = findViewById(R.id.a_add_conversation_recycler_view);
        mAddConversationButton = findViewById(R.id.a_add_conversation_add_contacts);

        // Firebase
        currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        contacts = new ArrayList<>();
        contactsReference = FirebaseDatabase.getInstance().getReference("Users/" + currentUserPhoneNumber + "/Contacts");
        conversationsReference = FirebaseDatabase.getInstance().getReference("Conversations");
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void displayContacts() {
        AddConversationAdapter contactsAdapter = new AddConversationAdapter(this, contacts);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(contactsAdapter);

        contactsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot contactsSnapshot : snapshot.getChildren()) {
                    contacts.add(new Items.Contact(contactsSnapshot.getKey()));
                }

                contactsAdapter.notifyDataSetChanged();

                if (contacts.size() == 0) {
                    mAddConversationButton.setVisibility(View.VISIBLE);
                } else {
                    mAddConversationButton.setVisibility(View.GONE);
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

    private void setUpConversation(String name, ArrayList<Items.Contact> contacts) {
        usersReference.get().addOnSuccessListener(runnable -> {
            // Создание беседы и ее настройка
            HashMap<String, Object> settings = new HashMap<>();

            HashMap<String, Object> members = new HashMap<>();
            for (Items.Contact contact : contacts) {
                members.put(contact.getPhoneNumber(), "user");
            }

            settings.put("Members", members);
            settings.put("Messages", "");
            settings.put("Name", name);

            DatabaseReference pushedConversationReference = conversationsReference.push();
            pushedConversationReference.setValue(settings);

            // Настройка админа
            Items.Contact conversationCreator = new Items.Contact(currentUserPhoneNumber);
            usersReference.child(conversationCreator.getPhoneNumber())
                    .child("Conversations")
                    .child(pushedConversationReference.getKey())
                    .child("Role")
                    .setValue("admin");

            // Настройка пользователей
            for (DataSnapshot user : runnable.getChildren()) {
                for (Items.Contact contact : contacts) {
                    // Проверка на то что номер телефона состоит в списке и он не является создателем
                    if (user.getKey().equals(contact.getPhoneNumber())
                            && !(user.getKey().equals(conversationCreator.getPhoneNumber()))) {
                        usersReference.child(user.getKey())
                                .child("Conversations")
                                .child(pushedConversationReference.getKey())
                                .child("Role")
                                .setValue("user");
                    }
                }
            }
        });
    }
}