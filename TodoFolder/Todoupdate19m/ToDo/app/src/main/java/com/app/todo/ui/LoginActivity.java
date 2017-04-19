package com.app.todo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    AppCompatEditText editTextEmailLogin;
    AppCompatEditText editTextPasswordLogin;
    AppCompatButton buttonLogin;
    ProgressDialog progressDialog;
    AppCompatButton buttonCreateAccount;
    AppCompatButton facebookbutton;
    AppCompatButton googlebutton;
    Pattern EmailPattern, Passwordpattern;
    Matcher mMatcher, matcher;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        initView();

        if (isNetworkConnected()) {

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(Constants.setTitle)
                    .setMessage(Constants.setMessage)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initView() {
        editTextEmailLogin = (AppCompatEditText) findViewById(R.id.editTextEmaillogin);
        editTextPasswordLogin = (AppCompatEditText) findViewById(R.id.editTextPasswordlogin);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonCreateAccount = (AppCompatButton) findViewById(R.id.buttonCreateAccount);
        facebookbutton=(AppCompatButton)findViewById(R.id.facebook);
        googlebutton=(AppCompatButton)findViewById(R.id.google);
        setListeners();

    }

    @Override
    public void setListeners() {
        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
        facebookbutton.setOnClickListener(this);
        googlebutton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                userLogin();
                break;

            case R.id.buttonCreateAccount:
                Intent intent = new Intent(LoginActivity.this, RegisterationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void userLogin() {
        String email_login = editTextEmailLogin.getText().toString();
        String password_login = editTextPasswordLogin.getText().toString();
        EmailPattern = Pattern.compile(Constants.EMAIL_PATTERN);
        mMatcher = EmailPattern.matcher(editTextEmailLogin.getText().toString());
        Passwordpattern = Pattern.compile(Constants.Password_Pattern);
        matcher = Passwordpattern.matcher(editTextPasswordLogin.getText().toString());

        if (editTextEmailLogin.getText().toString().length() == 0) {
            editTextEmailLogin.setError(getString(R.string.Emailid_should_not_empty));
            editTextEmailLogin.requestFocus();
            return;
        } else if (mMatcher.matches()) {

        } else {
            editTextEmailLogin.setError(getString(R.string.invalid_Emailid));
            editTextEmailLogin.requestFocus();
        }

        if (editTextPasswordLogin.getText().toString().length() == 0) {
            editTextPasswordLogin.setError(getString(R.string.Password_should_not_empty));
            editTextPasswordLogin.requestFocus();
            return;
        } else if (matcher.matches()) {
        } else {
            editTextPasswordLogin.setError(getString(R.string.invalid_Password));
            editTextPasswordLogin.requestFocus();
        }

        auth.signInWithEmailAndPassword(email_login, password_login).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    SharedPreferences sharedPreferences;
                    sharedPreferences=getApplicationContext().getSharedPreferences(Constants.keys,Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("uid",task.getResult().getUser().getUid()).commit();
                    startActivity(new Intent(getApplicationContext(), TodoHomeActivity.class));
                    finish();
                }
            }
        });
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}