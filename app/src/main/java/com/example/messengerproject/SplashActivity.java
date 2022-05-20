package com.example.messengerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;

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