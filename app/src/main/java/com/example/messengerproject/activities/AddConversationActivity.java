package com.example.messengerproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

import com.example.messengerproject.R;
import com.google.android.material.appbar.AppBarLayout;

public class AddConversationActivity extends AppCompatActivity {
    // Elements
    private AppBarLayout mAppBar;
    private SearchView mSearchView;
    private Toolbar mToolBar;
    private Toolbar mToolBarBack;
    private EditText mConversationNameEditText;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conversation);
        init();

        setSupportActionBar(mToolBarBack);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.m_add_conversation_okay) {

            }

            return false;
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
    }
}