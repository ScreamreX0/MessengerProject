package com.example.messengerproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.messengerproject.R;

public class SplashActivity extends AppCompatActivity {
    private static final String DEBUG_CODE = "Splash";

    @Override
    @SuppressLint("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ProgressBar progressBar = findViewById(R.id.a_splash_progress_bar);
        progressBar.setMax(2);
        progressBar.incrementSecondaryProgressBy(1);

        new Handler().postDelayed(() -> {
                startActivity(new Intent(this, AuthActivity.class));
        }, 1500);
    }
}