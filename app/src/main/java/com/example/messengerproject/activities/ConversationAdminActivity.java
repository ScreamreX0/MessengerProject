package com.example.messengerproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.messengerproject.Items;
import com.example.messengerproject.MessagesHelper;
import com.example.messengerproject.R;
import com.example.messengerproject.adapters.ConversationsAdapter;
import com.example.messengerproject.fragments.ConversationInfoFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ConversationAdminActivity extends AppCompatActivity {
    private static final String DEBUG_CODE = "ConversationAdminActivity";
    private String conversationId;  // id диалога в базе
    private String conversationName;
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
    private ImageButton mEditButton;

    // Firebase
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference messagesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_admin);

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

        mToolBar.setOnClickListener(view -> {
            new ConversationInfoFragment(
                    getApplicationContext(),
                    conversationId,
                    conversationName,
                    getSupportFragmentManager()
            ).show(getSupportFragmentManager(), "");
        });
    }

    private void init() {
        messages = new ArrayList<>();
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Firebase
        conversationId = getIntent().getExtras().getString(ConversationsAdapter.CONVERSATION_ID_KEY);
        conversationName = getIntent().getExtras().getString(ConversationsAdapter.CONVERSATION_NAME_KEY);
        messagesReference = FirebaseDatabase.getInstance()
                .getReference("Conversations/" + conversationId + "/Messages");

        // Elements
        mAppBar = findViewById(R.id.a_conversation_admin_app_bar);
        mToolBar = findViewById(R.id.a_conversation_admin_tool_bar);
        mIcon = findViewById(R.id.a_conversation_admin_icon);
        mName = findViewById(R.id.a_conversation_admin_name);
        mMessagesRecycleView = findViewById(R.id.a_conversation_admin_recycle_view);
        mMessageEditText = findViewById(R.id.a_conversation_admin_edit_text);
        mSendButton = findViewById(R.id.a_conversation_admin_send_button);
        mEditButton = findViewById(R.id.a_conversation_admin_edit_button);

        mName.setText(conversationName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversation_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.m_conversation_admin_clear) {
            clearConversation();
        } else if (item.getItemId() == R.id.m_conversation_admin_delete) {
            deleteSelectedMessages();
        } else if (item.getItemId() == R.id.m_conversation_admin_edit) {
            editSelectedMessage();
        } else if (item.getItemId() == R.id.m_conversation_admin_delete_conversation) {
            deleteConversation();
        }
        return super.onOptionsItemSelected(item);
    }

    // Вывод списка диалогов
    private void displayMessages() {
        new MessagesHelper(getApplicationContext(),
                mMessagesRecycleView,
                messagesReference,
                messages,
                mToolBar).displayMessages();
    }

    // Мето для очистки беседы от сообщений
    private void clearConversation() {
        FirebaseDatabase.getInstance().getReference(
                "Conversations/" + conversationId + "/Messages").setValue("");
    }

    // Метод для удаления беседы
    private void deleteConversation() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.get().addOnSuccessListener(runnable -> {
            for (DataSnapshot user : runnable.child("Conversations")
                    .child(conversationId)
                    .child("Members").getChildren()) {

                DatabaseReference userConversationReference = databaseReference
                        .child("Users")
                        .child(user.getKey())
                        .child("Conversations");

                if (runnable.child("Users").child(user.getKey()).child("Conversations").getChildrenCount() == 1) {
                    userConversationReference.setValue("");
                } else {
                    userConversationReference.child(conversationId).removeValue();
                }
            }

            if (runnable.child("Conversations").getChildrenCount() == 1) {
                databaseReference.child("Conversations").setValue("");
            } else {
                databaseReference.child("Conversations").child(conversationId).removeValue();
            }

            startActivity(new Intent(this, MainActivity.class));
        });
    }

    // Метод для удаления всех выделенных сообщений
    private void deleteSelectedMessages() {
        messagesReference.get().addOnSuccessListener(runnable -> {
            if (runnable.getChildrenCount() <= 1) {
                messagesReference.setValue("");
            } else {
                for (Items.Message message : messages) {
                    if (message.isSelected()) {
                        messagesReference.child(message.getName()).removeValue();
                    }
                }
            }
        });
    }

    // Метод для редактирования выделенного сообщения
    private void editSelectedMessage() {
        Items.Message message = getSelectedMessage();

        if (message == null) {
            return;
        }

        mEditButton.setVisibility(View.VISIBLE);
        mMessageEditText.setText(message.getText());
        mEditButton.setOnClickListener(view -> {
            if (mMessageEditText.getText().toString().replace(" ", "").equals("")) {
                message.setSelected(false);
                return;
            }

            if (mMessageEditText.getText().toString().equals(message.getText())) {
                return;
            }

            messagesReference
                    .child(message.getName())
                    .child("Message")
                    .setValue(mMessageEditText.getText().toString());

            message.setSelected(false);

            mMessageEditText.setText("");
            mEditButton.setVisibility(View.GONE);

            mToolBar.getMenu().getItem(0).setVisible(false);
            mToolBar.getMenu().getItem(1).setVisible(false);
        });
    }

    private Items.Message getSelectedMessage() {
        for (Items.Message message : messages) {
            if (message.isSelected()) {
                return message;
            }
        }
        return null;
    }
}