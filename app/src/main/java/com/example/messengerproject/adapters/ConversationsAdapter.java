package com.example.messengerproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.activities.ConversationActivity;

import java.util.ArrayList;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder> {
    private final Context context;
    private final ArrayList<Items.Conversation> conversations;
    public final static String CONVERSATION_ID = "ConversationId";

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

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra(CONVERSATION_ID, conversation.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            name = itemView.findViewById(R.id.i_conversation_name);
        }
    }
}