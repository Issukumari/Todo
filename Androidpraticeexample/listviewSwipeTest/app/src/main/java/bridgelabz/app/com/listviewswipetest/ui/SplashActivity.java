package bridgelabz.app.com.listviewswipetest.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


import bridgelabz.app.com.listviewswipetest.R;
import bridgelabz.app.com.listviewswipetest.utils.Constants;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    SharedPreferences sharedPreferences;
    boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences(Constants.keys,MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("islogin",false);
        getSplashScreen();
    }

    private void getSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLogin) {
                    Intent intent = new Intent(SplashActivity.this, TodoHomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, Constants.Timeout);
    }
}
