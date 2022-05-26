package com.example.messengerproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.fragments.KickMemberFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ConversationMembersAdapter extends RecyclerView.Adapter<ConversationMembersAdapter.ConversationMemberHolder>{
    Context context;
    ArrayList<Items.Contact> contacts;
    FragmentManager fragmentManager;
    String conversationId;

    public ConversationMembersAdapter(Context context,
                                      ArrayList<Items.Contact> contacts,
                                      FragmentManager fragmentManager,
                                      String conversationId) {
        this.context = context;
        this.contacts = contacts;
        this.fragmentManager = fragmentManager;
        this.conversationId = conversationId;
    }

    @NonNull
    @Override
    public ConversationMemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dialog_member, parent, false);

        return new ConversationMemberHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationMemberHolder holder, int position) {
        holder.mName.setText(contacts.get(position).getPhoneNumber());

        // Открытие подтверждения удаления
        holder.itemView.setOnLongClickListener(view -> {
            // Проверка на текущего пользователя
            String currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            String clickedPhoneNumber = contacts.get(position).getPhoneNumber();

            if (currentUserPhoneNumber.equals(clickedPhoneNumber)) {
                return false;
            }

            // Проверка на админа
            DatabaseReference membersReference = FirebaseDatabase
                    .getInstance()
                    .getReference("Conversations")
                    .child(conversationId)
                    .child("Members");
            membersReference.get().addOnSuccessListener(runnable -> {
                if (runnable.child(clickedPhoneNumber).getValue().equals("admin")) {
                    return;
                }

                new KickMemberFragment(contacts.get(position), conversationId).show(fragmentManager, "");
            });

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ConversationMemberHolder extends RecyclerView.ViewHolder {
        ImageView mIcon;
        TextView mName;

        public ConversationMemberHolder(@NonNull View itemView) {
            super(itemView);
            init();
        }

        private void init() {
            mIcon = itemView.findViewById(R.id.i_dialog_member_icon);
            mName = itemView.findViewById(R.id.i_dialog_member_name);
        }
    }
}
