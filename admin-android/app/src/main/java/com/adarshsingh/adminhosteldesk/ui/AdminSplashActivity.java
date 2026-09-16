package com.adarshsingh.adminhosteldesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.adarshsingh.adminhosteldesk.data.local.SessionManager;
import com.adarshsingh.adminhosteldesk.databinding.ActivityAdminSplashBinding;

public class AdminSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAdminSplashBinding binding = ActivityAdminSplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = SessionManager.getInstance(this);
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(AdminSplashActivity.this, AdminMainActivity.class));
            } else {
                startActivity(new Intent(AdminSplashActivity.this, AdminLoginActivity.class));
            }
            finish();
        }, 1200);
    }
}
