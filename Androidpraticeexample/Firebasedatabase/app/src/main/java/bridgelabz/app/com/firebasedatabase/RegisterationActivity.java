package bridgelabz.app.com.firebasedatabase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bridgelabz.app.com.firebasedatabase.base.BaseActivity;
import bridgelabz.app.com.firebasedatabase.utils.Constants;


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
    private FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterationActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_registration);
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
                // validate();
                UserRegister();
                break;
            case R.id.AlreadyAccountButton:
                Intent intent = new Intent(RegisterationActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void UserRegister() {
        Pattern mPattern = Pattern.compile(Constants.Password_Pattern);
        mMatcher = mPattern.matcher(editTextPwdRegisteration.getText().toString());
        boolean checkemail = false, checkname = false, checkpassword = false, checkMobile = false, checkAddress = false;
        String name = editTextNameRegisteration.getText().toString();
        String email = editTextEmailRegisteration.getText().toString();
        String password = editTextPwdRegisteration.getText().toString();
        String Mobile = editTextMobileRegisteration.getText().toString();
        String Adress = editTextAddressRegisteration.getText().toString();

        if (name.isEmpty()) {
            editTextNameRegisteration.setError(getString(R.string.name_should_not_empty));
            return;
        }
        if (email.isEmpty()) {
            editTextEmailRegisteration.setError(getString(R.string.Emailid_should_not_empty));
            return;
        } else if (!isValidEmail(email)) {
            editTextEmailRegisteration.setError(getString(R.string.invalid_Email));
            return;
        }


        if (password.isEmpty()) {
            editTextPwdRegisteration.setError(getString(R.string.password_should_not_empty));

        } else if (password.length() < 5) {
            editTextPwdRegisteration.setError(getString(R.string.password_should_be_minimum_5_character));

        } else if (mMatcher.matches()) {
            return;
        } else {
            editTextPwdRegisteration.setError(getString(R.string.wrong_format_of_password));
            editTextPwdRegisteration.requestFocus();

        }

        if (Mobile.isEmpty()) {
            editTextMobileRegisteration.setError(getString(R.string.mobile_no_should_not_be_empty));
        } else if (Mobile.length() < 10) {
            editTextMobileRegisteration.setError(getString(R.string.mobile_no_should_be_10_digit));
            return;
        }


        if (Adress.isEmpty()) {
            editTextAddressRegisteration.setError(getString(R.string.Address_should_not_empty));
            return;
        }
        progressDialog.setMessage("please wait");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
                else{
                    Toast.makeText(RegisterationActivity.this, "register while error", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });


    }

   // public void validate() {
   /*     boolean checkemail = false, checkname = false, checkpassword = false, checkMobile = false, checkAddress = false;

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

*/


    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}

