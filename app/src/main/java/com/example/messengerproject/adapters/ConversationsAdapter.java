package com.example.messengerproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.activities.ConversationAdminActivity;
import com.example.messengerproject.activities.ConversationUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder> {
    private final Context context;
    private final ArrayList<Items.Conversation> conversations;
    public final static String CONVERSATION_ID_KEY = "ConversationId";
    public final static String CONVERSATION_NAME_KEY = "ConversationName";

    public ConversationsAdapter(Context context, ArrayList<Items.Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);

        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Items.Conversation conversation = conversations.get(position);
        holder.name.setText(conversation.getName());
        holder.lastMessage.setText(conversation.getLastMessage());
        holder.lastMessageTime.setText(conversation.getLastMessageTime());

        holder.itemView.setOnClickListener(view -> {
            FirebaseDatabase.getInstance()
                    .getReference("Conversations/" + conversation.getId() + "/Members")
                    .get().addOnSuccessListener(runnable -> {
                        if (runnable.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).getValue().equals("admin")) {
                            Intent intent = new Intent(context, ConversationAdminActivity.class);
                            intent.putExtra(CONVERSATION_ID_KEY, conversation.getId());
                            intent.putExtra(CONVERSATION_NAME_KEY, conversation.getName());
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, ConversationUserActivity.class);
                            intent.putExtra(CONVERSATION_ID_KEY, conversation.getId());
                            intent.putExtra(CONVERSATION_NAME_KEY, conversation.getName());
                            context.startActivity(intent);
                        }
                    });

        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        TextView lastMessageTime;
        
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            name = itemView.findViewById(R.id.i_conversation_name);
            lastMessage = itemView.findViewById(R.id.i_conversation_last_message);
            lastMessageTime = itemView.findViewById(R.id.i_conversation_time);
        }
    }
}