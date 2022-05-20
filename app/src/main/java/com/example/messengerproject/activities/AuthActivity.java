package com.example.messengerproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messengerproject.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    // Elements
    private EditText mPhoneNumberTextView;
    private Button mGetCodeButton;
    private EditText mCodeTextView;
    private Button mSendCodeButton;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private static final String DEBUG_CODE = "Auth";
    private String userId;
    private final long codeTimeout = 30L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();

        authWithTestPhoneNumber();

        mCallBack = getCallBack();

        mGetCodeButton.setOnClickListener(v -> {
            sendVerificationCode(mPhoneNumberTextView.getText().toString());
        });

        mSendCodeButton.setOnClickListener(v -> {
            if (userId == null) {
                return;
            }

            if (mCodeTextView.getText().toString().length() < 6) {
                return;
            }

            String code = mCodeTextView.getText().toString();

            if (code.isEmpty() || code.length() < 6) {
                Log.d(DEBUG_CODE, "\nError:" + "Неверный код");
                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code, userId);
        });
    }

    // Метод инициализации
    private void init() {
        mPhoneNumberTextView = findViewById(R.id.a_auth_phone_number);
        mGetCodeButton = findViewById(R.id.a_auth_get_code);
        mCodeTextView = findViewById(R.id.a_auth_code);
        mSendCodeButton = findViewById(R.id.a_auth_send_code);
    }

    // Метод для получения настроек отправки кода
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getCallBack() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // Отправка кода
            @Override
            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(id, token);

                mGetCodeButton.setEnabled(false);

                userId = id;

                startTimer();
            }

            // Код успешно отправлен
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
            }

            // Ошибка отправки кода
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(AuthActivity.this, "Неверный номер телефона", Toast.LENGTH_SHORT).show();
                Log.d(DEBUG_CODE, "Error: " + e.getMessage());
            }
        };
    }

    // Метод для отправки кода
    private void sendVerificationCode(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)
                .setTimeout(codeTimeout, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Метод для верификации с кодом
    private void verifyCode(String codeByUser, String verificationId) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeByUser);

        mAuth.signInWithCredential(credential).addOnCompleteListener(runnable -> {
            if (!runnable.isSuccessful()) {
                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                return;
            }

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });
    }

    // Метод для отключения кнопки повторной отправки кода и запуск таймера
    private void startTimer() {
        CountDownTimer timer = new CountDownTimer(codeTimeout * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                mGetCodeButton.setText("Получить код (" + (l / 1000) + " сек)");
            }

            @Override
            public void onFinish() {
                mGetCodeButton.setText("Получить код");
                mGetCodeButton.setEnabled(true);
            }
        };

        timer.start();
    }

    // TODO: Убрать на проде
    private void authWithTestPhoneNumber() {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(id, token);

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, "123456");

                mAuth.signInWithCredential(credential).addOnCompleteListener(runnable -> {
                    if (!runnable.isSuccessful()) {
                        Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                });
            }
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(AuthActivity.this, "Неверный номер телефона", Toast.LENGTH_SHORT).show();
                Log.d(DEBUG_CODE, "Error: " + e.getMessage());
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+79274304923")
                .setTimeout(10L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callback)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}