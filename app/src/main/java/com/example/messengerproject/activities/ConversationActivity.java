package com.example.messengerproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerproject.ConversationsHelper;
import com.example.messengerproject.Items;
import com.example.messengerproject.MessagesHelper;
import com.example.messengerproject.R;
import com.example.messengerproject.adapters.ConversationsAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ConversationActivity extends AppCompatActivity {
    private static final String DEBUG_CODE = "ConversationActivity";
    private String conversationId;  // id диалога в базе
    private ArrayList<Items.Message> messages;  // Список сообщений
    SimpleDateFormat date;

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

        mSendButton.setOnClickListener(view -> {
            String text = mMessageEditText.getText().toString();

            if (text.replace(" ", "").equals("")) {
                return;
            }

            HashMap<String, Object> message = new HashMap<>();
            message.put("Message", text);
            message.put("Sender", firebaseAuth.getCurrentUser().getPhoneNumber());
            message.put("Time", date.format(new Date()));
            message.put("Type", "text");

            messagesReference.push().setValue(message);

            mMessageEditText.setText("");
        });
    }

    private void init() {
        messages = new ArrayList<>();
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Firebase
        conversationId = getIntent().getExtras().getString(ConversationsAdapter.CONVERSATION_ID);
        messagesReference = FirebaseDatabase.getInstance()
                .getReference("Conversations/" + conversationId + "/Messages");

        // Elements
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.m_conversation_clear) {
            messagesReference.setValue("");
        } else if (item.getItemId() == R.id.m_conversation_exit) {

        }
        return super.onOptionsItemSelected(item);
    }

    // Вывод списка диалогов
    private void displayMessages() {
        new MessagesHelper(getApplicationContext(),
                mMessagesRecycleView,
                messagesReference,
                messages).displayMessages();
    }
}