package com.example.messengerproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.messengerproject.R;

public class AddContactFragment extends DialogFragment {
    private EditText mPhoneNumberEditText;
    private Button mOkayButton;
    private Button mCancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgment_add_contact, container, false);

        init(view);

        mCancelButton.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }

    private void init(View view) {
        mPhoneNumberEditText = view.findViewById(R.id.f_add_contact_phone_number);
        mOkayButton = view.findViewById(R.id.f_add_contact_okay);
        mCancelButton = view.findViewById(R.id.f_add_contact_cancel);
    }
}
