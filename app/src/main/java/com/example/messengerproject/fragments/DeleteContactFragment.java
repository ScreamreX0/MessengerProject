package com.example.messengerproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.messengerproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteContactFragment extends DialogFragment {
    private final String phoneNumber;

    // Elements
    private TextView mTitle;
    private Button mOkButton;
    private Button mCancelButton;

    // Firebase
    private String currentUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    private DatabaseReference currentUserContactsRef;

    public DeleteContactFragment(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_contact, container, false);
        init(view);

        mTitle.setText("Удалить " + phoneNumber + "?");

        mOkButton.setOnClickListener(view1 -> {
            currentUserContactsRef.get().addOnSuccessListener(runnable -> {
                if (runnable.getChildrenCount() == 1) {
                    currentUserContactsRef.setValue("");
                } else {
                    currentUserContactsRef.child(phoneNumber).removeValue();
                }
                dismiss();
            });
        });

        mCancelButton.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }

    private void init(View view) {
        // Elements
        mTitle = view.findViewById(R.id.f_delete_contact_title);
        mOkButton = view.findViewById(R.id.f_delete_contact_ok);
        mCancelButton = view.findViewById(R.id.f_delete_contact_cancel);

        // Firebase
        currentUserContactsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUserPhoneNumber)
                .child("Contacts");
    }
}
