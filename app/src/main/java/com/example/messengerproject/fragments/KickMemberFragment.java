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

import com.example.messengerproject.Items;
import com.example.messengerproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class KickMemberFragment extends DialogFragment {
    private final Items.Contact kickedUser;
    private final String conversationId;

    // Elements
    private Button mOkButton;
    private Button mCancelButton;
    private TextView mTitle;

    public KickMemberFragment(Items.Contact kickedUser, String conversationId) {
        this.kickedUser = kickedUser;
        this.conversationId = conversationId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kick_member, container, false);
        init(view);

        mTitle.setText("Выгнать " + kickedUser.getPhoneNumber() + "?");

        mOkButton.setOnClickListener(view1 -> {
            DatabaseReference conversationRef = FirebaseDatabase.getInstance()
                    .getReference("Conversations")
                    .child(conversationId);

            conversationRef.child("Members").child(kickedUser.getPhoneNumber()).removeValue();
            dismiss();
        });

        mCancelButton.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }

    private void init(View view) {
        mTitle = view.findViewById(R.id.f_kick_member_title);
        mOkButton = view.findViewById(R.id.f_kick_member_ok);
        mCancelButton = view.findViewById(R.id.f_kick_member_cancel);
    }
}
