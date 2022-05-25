package com.example.messengerproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.fragments.AddContactFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private ArrayList<Items.Contact> contacts;

    // Elements
    private FrameLayout mContainerView;
    private AppBarLayout mAppBarLayout;
    private androidx.appcompat.widget.Toolbar mToolbar;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        init();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFloatingActionButton.setOnClickListener(view -> {
            fragmentManager.beginTransaction().add(new AddContactFragment(), "").commit();
        });

    }

    private void init() {
        fragmentManager = getSupportFragmentManager();
        contacts = getContacts();

        // Elements
        mAppBarLayout = findViewById(R.id.a_contacts_app_bar);
        mToolbar = findViewById(R.id.a_contacts_toolbar);
        mFloatingActionButton = findViewById(R.id.a_contacts_add_contact);
    }

    private ArrayList<Items.Contact> getContacts() {
        ArrayList<Items.Contact> contacts = new ArrayList<>();

        return contacts;
    }
}