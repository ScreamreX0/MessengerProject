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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    // Elements
    private EditText mPhoneNumberTextView;
    private Button mGetCodeButton;
    private EditText mCodeTextView;
    private Button mSendCodeButton;
    private static final String TEST_PHONE_NUMBER = "+79274304921";
    private static String phoneNumber;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;  // Настройки
    private static final String DEBUG_CODE = "Auth";
    private String userId;  // id пользователя при отправке кода на телефон (id номера телефона)
    private final long codeTimeout = 60L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();

        // Попытка авторизации с помощью тестового телефонного номера
        // TODO: Убрать на проде
        authWithTestPhoneNumber();

//        // Получение настроек
//        mCallBack = getCallBack();
//
//        // Слушатель нажатия на кнопку получения кода
//        mGetCodeButton.setOnClickListener(v -> {
//            // Метод для отправки кода верификации
//            phoneNumber = mPhoneNumberTextView.getText().toString();
//            sendVerificationCode(phoneNumber);
//        });
//
//        // Метод для отправки проверочного кода
//        mSendCodeButton.setOnClickListener(v -> {
//            if (userId == null) {
//                // Пользователь не отправлял код на свой номер телефона
//                return;
//            }
//
//            if (mCodeTextView.getText().toString().length() < 6) {
//                // Пользователь ввел меньше 6 символов кода
//                Log.d(DEBUG_CODE, "\nError:" + "Неверный код");
//                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Код который ввел пользователь
//            String code = mCodeTextView.getText().toString();
//
//            verifyCode(code, userId);
//        });
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

                // Отключение кнопки отправки кода
                mGetCodeButton.setEnabled(false);

                userId = id;
                startTimer();
            }

            // Код успешно отправлен
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
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

            FirebaseDatabase.getInstance().getReference("Users").get().addOnSuccessListener(runnable1 -> {
                boolean isExist = false;
                for (DataSnapshot user : runnable1.getChildren()) {
                    if (user.getKey().equals(phoneNumber)) {
                        isExist = true;
                    }
                }

                if (!isExist) {
                    HashMap<String, String> userSettings = new HashMap<>();
                    userSettings.put("Contacts", "");
                    userSettings.put("Conversations", "");
                    userSettings.put("Name", "");
                    userSettings.put("Role", "user");

                    FirebaseDatabase.getInstance()
                            .getReference("Users/" + phoneNumber)
                            .setValue(userSettings);
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });

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

                    FirebaseDatabase.getInstance().getReference("Users").get().addOnSuccessListener(runnable1 -> {
                        boolean isExist = false;
                        for (DataSnapshot user : runnable1.getChildren()) {
                            if (user.getKey().equals(TEST_PHONE_NUMBER)) {
                                isExist = true;
                            }
                        }

                        if (!isExist) {
                            HashMap<String, String> userSettings = new HashMap<>();
                            userSettings.put("Contacts", "");
                            userSettings.put("Conversations", "");
                            userSettings.put("Name", "");
                            userSettings.put("Role", "user");

                            FirebaseDatabase.getInstance()
                                    .getReference("Users/" + TEST_PHONE_NUMBER)
                                    .setValue(userSettings);
                        }
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    });
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
                .setPhoneNumber(TEST_PHONE_NUMBER)
                .setTimeout(10L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callback)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}