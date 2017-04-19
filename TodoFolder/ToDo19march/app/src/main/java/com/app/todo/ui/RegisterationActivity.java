package com.app.todo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.base.BaseActivity;
import com.app.todo.model.UserDatamodel;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    java.util.regex.Pattern Pattern;
    ProgressDialog progressDialog;
    DatabaseReference mfirebasedatabase;
    UserDatamodel dataModel = new UserDatamodel();
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        initView();
    }

    @Override
    public void initView() {

        editTextNameRegisteration = (AppCompatEditText) findViewById(R.id.editTextName_Registeration);
        editTextEmailRegisteration = (AppCompatEditText) findViewById(R.id.editTextEmail_Registeration);
        editTextPwdRegisteration = (AppCompatEditText) findViewById(R.id.editTextPwd_Registeration);
        editTextMobileRegisteration = (AppCompatEditText) findViewById(R.id.editTextMobile_Registeration);
        editTextAddressRegisteration = (AppCompatEditText) findViewById(R.id.editTextAddress_Registeration);
        saveButton = (AppCompatButton) findViewById(R.id.SaveButton);
        AlreadyAccountButton = (AppCompatButton) findViewById(R.id.AlreadyAccountButton);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference("");
        setListeners();
    }

    @Override
    public void setListeners() {
        saveButton.setOnClickListener(this);
        AlreadyAccountButton.setOnClickListener(this);

    }

    private void UserRegister() {
        Pattern mPattern = Pattern.compile(Constants.Password_Pattern);
        final String NameRegistration = editTextNameRegisteration.getText().toString();
        final String EmailRegistration = editTextEmailRegisteration.getText().toString();
        final String PasswordRegistration = editTextPwdRegisteration.getText().toString();
        final String MobileRegistration = editTextMobileRegisteration.getText().toString();
        final String addressResgistration = editTextAddressRegisteration.getText().toString();
        mMatcher = mPattern.matcher(editTextPwdRegisteration.getText().toString());
        Bundle args = new Bundle();
        args.putString(Constants.Email, dataModel.getEmailRegistration());
        args.putString(Constants.password, dataModel.getPasswordRegistration());
        args.putString(Constants.Name, dataModel.getNameRegistration());
        args.putString(Constants.Mobile, dataModel.getMobileRegistration());
        args.putString(Constants.address, dataModel.getaddressResgistration());
        if (NameRegistration.isEmpty()) {
            editTextNameRegisteration.setError(getString(R.string.name_should_not_be_empty));

            return;
        }
        if (EmailRegistration.isEmpty()) {
            editTextEmailRegisteration.setError(getString(R.string.Emailid_should_not_be_empty));

            return;

        } else if (!isValidEmail(EmailRegistration)) {
            editTextEmailRegisteration.setError(getString(R.string.invalid_Email));
        }
        if (PasswordRegistration.isEmpty()) {
            editTextPwdRegisteration.setError(getString(R.string.password_should_not_empty));
            return;
        } else if (PasswordRegistration.length() < 8) {
            editTextPwdRegisteration.setError(getString(R.string.password_should_be_minimum_8_character));
            return;

        } else if (mMatcher.matches()) {
        } else {
            editTextPwdRegisteration.setError(getString(R.string.wrong_format_of_password));
            editTextPwdRegisteration.requestFocus();
        }
        if (MobileRegistration.isEmpty()) {
            editTextMobileRegisteration.setError(getString(R.string.mobile_no_should_not_be_empty));
            return;
        } else if (MobileRegistration.length() < 10) {
            editTextMobileRegisteration.setError(getString(R.string.mobile_no_should_be_10_digit));

        }
        if (addressResgistration.isEmpty()) {
            editTextAddressRegisteration.setError(getString(R.string.Address_should_not_be_empty));
        }
        progressDialog.setMessage("please wait");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(EmailRegistration, PasswordRegistration).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dataModel.setEmailRegistration(EmailRegistration);
                    dataModel.setPasswordRegistration(PasswordRegistration);
                    dataModel.setNameRegistration(NameRegistration);
                    dataModel.setMobileRegistration(MobileRegistration);
                    dataModel.setaddressResgistration(addressResgistration);
                    String id = mfirebasedatabase.push().getKey();
                    mfirebasedatabase.child(id).setValue(dataModel);
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterationActivity.this, "error while register", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SaveButton:
                UserRegister();
                break;
            case R.id.AlreadyAccountButton:
                Intent intent = new Intent(RegisterationActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}

