package com.example.messengerproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.adapters.ContactsAdapter;
import com.example.messengerproject.fragments.AddContactFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ArrayList<Items.Contact> contacts;

    ContactsAdapter contactsAdapter;
    String currentUserMobilePhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

    // Elements
    private AppBarLayout mAppBarLayout;
    private androidx.appcompat.widget.Toolbar mToolbar;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        init();

        displayContacts();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFloatingActionButton.setOnClickListener(view -> {
            fragmentManager.beginTransaction().add(new AddContactFragment(), "").commit();
        });
    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        contacts = new ArrayList<>();

        // Elements
        mRecyclerView = findViewById(R.id.a_contacts_recycler_view);
        mAppBarLayout = findViewById(R.id.a_contacts_app_bar);
        mToolbar = findViewById(R.id.a_contacts_toolbar);
        mFloatingActionButton = findViewById(R.id.a_contacts_add_contact);
    }

    private void displayContacts() {
        contactsAdapter = getContactsAdapter();
        mRecyclerView = getRecyclerView(mRecyclerView);

        databaseReference.child(currentUserMobilePhone).child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contacts.clear();

                if (snapshot.getChildrenCount() == 0) {
                    contactsAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot contact : snapshot.getChildren()) {
                    contacts.add(new Items.Contact(contact.getKey()));
                }

                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ContactsAdapter getContactsAdapter() {
        return new ContactsAdapter(this, contacts, getSupportFragmentManager());
    }

    private RecyclerView getRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactsAdapter);

        return recyclerView;
    }
}