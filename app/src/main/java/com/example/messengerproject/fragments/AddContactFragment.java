package com.example.messengerproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.messengerproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collection;
import java.util.HashMap;

public class AddContactFragment extends DialogFragment {
    private EditText mPhoneNumberEditText;
    private Button mOkayButton;
    private Button mCancelButton;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.framgment_add_contact, container, false);

        init(view);

        mCancelButton.setOnClickListener(view1 -> {
            dismiss();
        });

        mOkayButton.setOnClickListener(v -> {
            // Проверка введенного номера телефона на формат
            String answer = checkForPhoneNumber();
            if (!checkForPhoneNumber().equals("ok")) {
                Toast.makeText(inflater.getContext(), answer, Toast.LENGTH_LONG).show();
                return;
            }

            firebaseDatabase.getReference("Users").get().addOnCompleteListener(command -> {
                if (!command.isSuccessful()) {
                    Toast.makeText(inflater.getContext(), "Упс. Что-то пошло не так", Toast.LENGTH_LONG).show();
                    return;
                }

                // Список всех контактов текущего пользователя
                Iterable<DataSnapshot> contacts = command.getResult()
                        .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                        .child("Contacts")
                        .getChildren();

                // Пробежка по всем контактам текущего пользователя и проверка
                for (DataSnapshot contact : contacts) {
                    if (contact.getKey().equals(mPhoneNumberEditText.getText().toString())) {
                        Toast.makeText(inflater.getContext(), "Пользователь уже есть в списке контактов", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // Пробежка по всем пользователям и проверка на существование
                for (DataSnapshot user : command.getResult().getChildren()) {
                    if (user.getKey().equals(mPhoneNumberEditText.getText().toString())) {
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put(mPhoneNumberEditText.getText().toString(), "");

                        firebaseDatabase.getReference("Users/"
                                + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                                + "/Contacts").setValue(contact);

                        dismiss();
                        return;
                    }
                }

                Toast.makeText(inflater.getContext(), "Пользователь не найден", Toast.LENGTH_LONG).show();
                return;
            });
        });

        return view;
    }

    private void init(View view) {
        mPhoneNumberEditText = view.findViewById(R.id.f_add_contact_phone_number);
        mOkayButton = view.findViewById(R.id.f_add_contact_okay);
        mCancelButton = view.findViewById(R.id.f_add_contact_cancel);
    }

    private String checkForPhoneNumber() {
        if (mPhoneNumberEditText.getText().toString().replace(" ", "").equals("")) {
            return "Поле не должно быть пустым";
        }

        if (mPhoneNumberEditText.getText().toString().length() > 20) {
            return "Неверный формат";
        }

        return "ok";
    }
}
