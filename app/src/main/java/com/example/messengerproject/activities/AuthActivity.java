package com.example.messengerproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messengerproject.DebuggingHelper;
import com.example.messengerproject.Variables;
import com.example.messengerproject.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {
    // Test auth
    private static final String TEST_PHONE_NUMBER = "+79274304921";
    private static final String TEST_SMS_CODE = "123456";
    private static final boolean testMode = true;

    // Debugging
    private String debugCode;
    private Class currentClass;

    // Elements
    private EditText mPhoneNumberTextView;
    private Button mGetCodeButton;
    private EditText mCodeTextView;
    private Button mSendCodeButton;

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    private String userId;
    private final long codeTimeout = 60L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();

        mCallBack = getCallBack();

        if (testMode) {
            sendVerificationCode(TEST_PHONE_NUMBER);

            return;
        }

        // Кнопка для отправки кода верификации
        mGetCodeButton.setOnClickListener(v -> {
            sendVerificationCode(mPhoneNumberTextView.getText().toString());
        });

        // Метод для отправки проверочного кода
        mSendCodeButton.setOnClickListener(v -> {
            if (userId == null) {
                // Пользователь не отправлял код на свой номер телефона
                return;
            }

            String userCode = mCodeTextView.getText().toString();
            if (userCode.length() < 6) {
                DebuggingHelper.log(currentClass, "Wrong code", "code length less than 6");
                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(userCode, userId);
        });
    }

    private void init() {
        // Debugging
        currentClass = this.getClass();
        debugCode = DebuggingHelper.getDebugCode(currentClass);

        // Elements
        mPhoneNumberTextView = findViewById(R.id.a_auth_phone_number);
        mGetCodeButton = findViewById(R.id.a_auth_get_code);
        mCodeTextView = findViewById(R.id.a_auth_code);
        mSendCodeButton = findViewById(R.id.a_auth_send_code);
    }

    // Метод для получения настроек отправки кода
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getCallBack() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(id, token);

                userId = id;
                if (testMode) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, TEST_SMS_CODE);
                    signInWithCredential(credential);

                    return;
                }

                mGetCodeButton.setEnabled(false);
                startTimer();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                DebuggingHelper.log(currentClass, "", "Phone number verified");
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(AuthActivity.this, "Неверный номер телефона", Toast.LENGTH_SHORT).show();
                DebuggingHelper.log(currentClass, "Wrong phone number", e.getMessage());
            }
        };
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(runnable -> {
            if (!runnable.isSuccessful()) {
                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                return;
            }

            DebuggingHelper.log(currentClass, "authorized");

            String initStaticVarsResult = initStaticVariables();
            if (!initStaticVarsResult.equals("ok")) {
                DebuggingHelper.log(currentClass, "Static variables init error", initStaticVarsResult);
                return;
            }

            Variables.usersReference.get().addOnSuccessListener(runnable1 -> {
                boolean isExist = false;
                for (DataSnapshot user : runnable1.getChildren()) {
                    if (user.getKey().equals(TEST_PHONE_NUMBER)) {
                        isExist = true;
                    }
                }

                if (!isExist) {
                    // Установка первичных параметров
                    Variables.currentUserReference.setValue(getUserSettings());
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });
        });
    }

    // Метод для отправки кода
    private void sendVerificationCode(String number) {
        PhoneAuthProvider.verifyPhoneNumber(PhoneAuthOptions
                .newBuilder(mAuth)
                .setPhoneNumber(number)
                .setTimeout(codeTimeout, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build()
        );
    }

    // Метод для верификации с кодом
    private void verifyCode(String codeByUser, String verificationId) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, codeByUser);
        mAuth.signInWithCredential(credential).addOnCompleteListener(runnable -> {
            if (!runnable.isSuccessful()) {
                Toast.makeText(AuthActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                DebuggingHelper.log(currentClass, "Wrong code");
                return;
            }

            DebuggingHelper.log(currentClass, "authorized");

            String initStaticVarsResult = initStaticVariables();
            if (!initStaticVarsResult.equals("ok")) {
                DebuggingHelper.log(currentClass, "Static variables init error", initStaticVarsResult);
                return;
            }

            DebuggingHelper.log(currentClass, "static variables initialized");

            Variables.usersReference.get().addOnSuccessListener(runnable1 -> {
                boolean isExist = false;
                for (DataSnapshot user : runnable1.getChildren()) {
                    if (user.getKey().equals(Variables.currentUserPhoneNumber)) {
                        isExist = true;
                        break;
                    }
                }

                if (!isExist) {
                    // Установка первичных параметров
                    Variables.currentUserReference.setValue(getUserSettings());
                }

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });
        });
    }

    // Метод для получения первичных параметров пользователя
    private HashMap<String, String> getUserSettings() {
        HashMap<String, String> userSettings = new HashMap<>();
        userSettings.put("Contacts", "");
        userSettings.put("Conversations", "");
        userSettings.put("Name", "");
        userSettings.put("Role", "user");

        return userSettings;
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

    // Метод для инициализации статический переменных
    private String initStaticVariables() {
        // User
        Variables.currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (Variables.currentUser == null) {
            return "getting current user";
        }

        Variables.currentUserPhoneNumber = Variables.currentUser.getPhoneNumber();

        // References
        Variables.conversationsReference = FirebaseDatabase.getInstance().getReference("Conversations");
        Variables.usersReference = FirebaseDatabase.getInstance().getReference("Users");

        Variables.currentUserReference = Variables.usersReference
                .child(Variables.currentUserPhoneNumber);
        Variables.currentUserContactsReference = Variables.currentUserReference
                .child("Contacts");
        Variables.currentUserConversationsReference = Variables.currentUserReference
                .child("Conversations");

        return "ok";
    }
}