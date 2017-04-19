package com.app.todo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.app.todo.R;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        authentication = FirebaseAuth.getInstance().getInstance();

        getSplashScreen();
    }

    private void getSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (authentication.getCurrentUser() != null) {
                    finish();
                    Intent intent = new Intent(SplashActivity.this, TodoHomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, Constants.Timeout);
    }
}
