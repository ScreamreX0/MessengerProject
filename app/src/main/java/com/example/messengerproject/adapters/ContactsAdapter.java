package com.example.messengerproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.example.messengerproject.fragments.DeleteContactFragment;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    Context context;
    ArrayList<Items.Contact> contacts;
    FragmentManager fragmentManager;

    public ContactsAdapter(Context context, ArrayList<Items.Contact> contacts, FragmentManager fragmentManager) {
        this.context = context;
        this.contacts = contacts;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.phoneNumber.setText(contacts.get(position).getPhoneNumber());

        holder.itemView.setOnLongClickListener(view -> {
            new DeleteContactFragment(contacts.get(position).getPhoneNumber()).show(fragmentManager, "");

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView phoneNumber;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            init();
        }

        private void init() {
            phoneNumber = itemView.findViewById(R.id.i_contact_name);
        }
    }
}
