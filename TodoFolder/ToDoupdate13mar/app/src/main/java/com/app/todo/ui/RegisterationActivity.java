package com.app.todo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class RegisterationActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RegistrationActivity";
    AppCompatEditText editTextNameRegisteration;
    AppCompatEditText editTextEmailRegisteration;
    AppCompatEditText editTextPwdRegisteration;
    AppCompatEditText editTextMobileRegisteration;
    AppCompatEditText editTextAddressRegisteration;
    AppCompatButton saveButton;
    AppCompatButton AlreadyAccountButton;
    Matcher mMatcher;
    Pattern Pattern;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();
    }

    @Override
    public void initView() {
        editTextNameRegisteration = (AppCompatEditText)findViewById(R.id.editTextName_Registeration);
        editTextEmailRegisteration = (AppCompatEditText)findViewById(R.id.editTextEmail_Registeration);
        editTextPwdRegisteration = (AppCompatEditText)findViewById(R.id.editTextPwd_Registeration);
        editTextMobileRegisteration = (AppCompatEditText)findViewById(R.id.editTextMobile_Registeration);
        editTextAddressRegisteration = (AppCompatEditText)findViewById(R.id.editTextAddress_Registeration);
        saveButton = (AppCompatButton) findViewById(R.id.SaveButton);
        AlreadyAccountButton = (AppCompatButton) findViewById(R.id.AlreadyAccountButton);
        setListeners();
    }

    @Override
    public void setListeners() {

        saveButton.setOnClickListener(this);
        AlreadyAccountButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SaveButton:
                validate();
                break;
            case R.id.AlreadyAccountButton:
                Intent intent = new Intent(RegisterationActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }
    }

    public void validate() {
        boolean checkemail = false, checkname = false, checkpassword = false, checkMobile = false, checkAddress = false;

        Pattern mPattern = Pattern.compile(Constants.Password_Pattern);
        mMatcher = mPattern.matcher(editTextPwdRegisteration.getText().toString());

        SharedPreferences sh = getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        String name = editTextNameRegisteration.getText().toString();
        String email = editTextEmailRegisteration.getText().toString();
        String password = editTextPwdRegisteration.getText().toString();
        String Mobile = editTextMobileRegisteration.getText().toString();
        String Adress = editTextAddressRegisteration.getText().toString();

        if (name.isEmpty()) {
            editTextNameRegisteration.setError(getString(R.string.name_should_not_empty));

        } else {
            checkname = true;
        }


        if (email.isEmpty()) {
            editTextEmailRegisteration.setError(getString(R.string.Emailid_should_not_empty));

        } else if (!isValidEmail(email)) {
            editTextEmailRegisteration.setError(getString(R.string.invalid_Email));
        } else {
            checkemail = true;
        }


        if (password.isEmpty()) {
            editTextPwdRegisteration.setError(getString(R.string.password_should_not_empty));

        } else if (password.length() < 5) {
            editTextPwdRegisteration.setError(getString(R.string.password_should_be_minimum_5_character));

        } else if (mMatcher.matches()) {
            checkpassword = true;
        } else {
            editTextPwdRegisteration.setError(getString(R.string.wrong_format_of_password));
            editTextPwdRegisteration.requestFocus();

        }

        if (Mobile.isEmpty()) {
            editTextMobileRegisteration.setError(getString(R.string.mobile_no_should_not_be_empty));
        } else if (Mobile.length() < 10) {
            editTextMobileRegisteration.setError(getString(R.string.mobile_no_should_be_10_digit));
        } else {
            checkMobile = true;
        }

        if (Adress.isEmpty()) {
            editTextAddressRegisteration.setError(getString(R.string.Address_should_not_empty));

        } else {
            checkAddress = true;
        }
        if (checkname && checkMobile && checkAddress && checkemail && checkpassword) {
            SharedPreferences.Editor editor = sh.edit();
            editor.putString("Name", name);
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putString("Mobile", Mobile);
            editor.putString("Adress", Adress);
            editor.putBoolean("islogin", false);
            editor.commit();
            Toast.makeText(getApplicationContext(), R.string.registration_successful, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        }

    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}

