package com.example.messengerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    // Elements
    private EditText mPhoneNumber;
    private Button mSendCode;
    private EditText mCode;
    private Button mEnterButton;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private static final String DEBUG_CODE = "Auth";
    private String verificationCodeBySystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();

        mCallBack = getCallBack();

        mSendCode.setOnClickListener(v -> {
            if (mPhoneNumber.getText().length() != 11) {
                Toast.makeText(AuthActivity.this, "Неверный формат номера телефона", Toast.LENGTH_SHORT).show();
                return;
            }

            sendVerificationCode("+" + mPhoneNumber.getText().toString());
        });

        mEnterButton.setOnClickListener(v -> {
            if (mCode.getText().toString().length() < 6) {
                return;
            }

            verifyCode(mCode.getText().toString());
//            String code = phoneAuthCredential.getSmsCode();
//
//            if (code.isEmpty() || code.length() < 6) {
//                Log.d(DEBUG_CODE, "\nError:" + "Неверный код");
//                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            verifyCode(code);
        });


    }

    // Метод инициализации
    private void init() {
        mPhoneNumber = findViewById(R.id.a_auth_phone_number);
        mSendCode = findViewById(R.id.a_auth_send_code);
        mCode = findViewById(R.id.a_auth_code);
        mEnterButton = findViewById(R.id.a_auth_enter_button);
    }

    // Метод для получения настроек отправки кода
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getCallBack() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(id, token);

                verificationCodeBySystem = id;

                Log.d(DEBUG_CODE, "\nid=" + id + "\ntoken=" + token);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                AuthActivity.this.verificationCodeBySystem = phoneAuthCredential.getSmsCode();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(AuthActivity.this, "Ошибка верификации", Toast.LENGTH_SHORT).show();
                Log.d(DEBUG_CODE, "Error: " + e.getMessage());
            }
        };
    }

    // Метод для отправки кода
    private void sendVerificationCode(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Метод для верификации с кодом
    private void verifyCode(String codeByUser) {
        if (verificationCodeBySystem == null) {
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);

        mAuth.signInWithCredential(credential).addOnCompleteListener(runnable -> {
            if (!runnable.isSuccessful()) {
                Log.d(DEBUG_CODE, String.valueOf(runnable.getException()));
                return;
            }

            String code = credential.getSmsCode();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}