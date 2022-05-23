package com.example.messengerproject.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerproject.ConversationsHelper;
import com.example.messengerproject.MessagesHelper;
import com.example.messengerproject.R;
import com.example.messengerproject.adapters.ConversationsAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConversationActivity extends AppCompatActivity {
    private static final String DEBUG_CODE = "ConversationActivity";
    private String conversationId;

    // Elements
    private AppBarLayout mAppBar;
    private Toolbar mToolBar;
    private ImageView mIcon;
    private TextView mName;

    private RecyclerView mMessagesRecycleView;
    private EditText mMessageEditText;
    private ImageButton mSendButton;

    // Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference messagesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        init();

        displayMessages();

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void init() {
        conversationId = getIntent().getExtras().getString(ConversationsAdapter.CONVERSATION_ID);
        messagesReference = FirebaseDatabase.getInstance()
                .getReference("Conversations/" + conversationId + "/Messages");

        mAppBar = findViewById(R.id.a_conversation_app_bar);
        mToolBar = findViewById(R.id.a_conversation_tool_bar);
        mIcon = findViewById(R.id.a_conversation_icon);
        mName = findViewById(R.id.a_conversation_conversation_name);
        mMessagesRecycleView = findViewById(R.id.a_conversation_recycle_view);
        mMessageEditText = findViewById(R.id.a_conversation_edit_text);
        mSendButton = findViewById(R.id.a_conversation_send_button);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Вывод списка диалогов
    private void displayMessages() {
        new MessagesHelper(getApplicationContext(),
                mMessagesRecycleView,
                messagesReference).displayMessages();
    }
}