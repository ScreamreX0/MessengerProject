package com.example.messengerproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.messengerproject.R;
import com.example.messengerproject.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class EditNameFragment extends DialogFragment {
    private EditText mEditText;
    private Button mOkayButton;
    private Button mCancelButton;
    private Context context;
    private TextView mProfileName;

    public EditNameFragment(Context context, TextView mProfileName) {
        this.context = context;
        this.mProfileName = mProfileName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_name, container, false);
        init(view);

        mOkayButton.setOnClickListener(view1 -> {
            if (mEditText.getText().toString().replace(" ", "").equals("")) {
                dismiss();
                return;
            }

            if (mEditText.getText().toString().length() > MainActivity.NICKNAME_MAX_SIZE) {
                Toast.makeText(context, "Длина никнейма не должна быть длиннее "
                        + MainActivity.NICKNAME_MAX_SIZE
                        + " символов", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase.getInstance().getReference("Users/"
                    + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                    + "/Name").setValue(mEditText.getText().toString());

            mProfileName.setText(mEditText.getText().toString());
            dismiss();
        });

        mCancelButton.setOnClickListener(view1 -> {
            dismiss();
        });

        return view;
    }

    private void init(View view) {
        mEditText = view.findViewById(R.id.f_add_contact_phone_number);
        mOkayButton = view.findViewById(R.id.f_add_contact_okay);
        mCancelButton = view.findViewById(R.id.f_add_contact_cancel);
    }
}
