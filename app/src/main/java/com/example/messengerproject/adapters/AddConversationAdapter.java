package com.example.messengerproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AddConversationAdapter extends RecyclerView.Adapter<AddConversationAdapter.ContactViewHolder> {
    ArrayList<Items.Contact> contacts;
    Context context;

    public AddConversationAdapter(ArrayList<Items.Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            TextView mName = itemView.findViewById(R.id.i_contact_name);
        }
    }
}
