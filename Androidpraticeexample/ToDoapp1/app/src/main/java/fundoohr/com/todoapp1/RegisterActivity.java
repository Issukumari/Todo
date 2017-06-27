
package fundoohr.com.todoapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fundoohr.com.todoapp1.base.BaseActivity;
import fundoohr.com.todoapp1.utils.Utility;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    AppCompatEditText editTextName_Register;
    AppCompatEditText editTextEmail_Register;
    AppCompatEditText editTextPwd_Register;
    AppCompatEditText editTextMobile_Register;
    AppCompatEditText editTextAddress_Register;
    Button editTextButton_Register;


    
    boolean checkName = false;
    boolean checkMail = false;
    boolean checkPassword = false;
    boolean checkMobNo = false;
    boolean checkAddress = false;

     Matcher mMatcher;
    Pattern mPattern;
      String EMAIL_PATTERN = "^(.+)@(.+)$";
    final String PASSWORD_PATTERN = "\\d+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initView();
    }

    @Override
    public void initView() {
        editTextName_Register = (AppCompatEditText) findViewById(R.id.editTextName_Register);
        editTextEmail_Register = (AppCompatEditText) findViewById(R.id.editTextEmail_Register);
        editTextPwd_Register = (AppCompatEditText) findViewById(R.id.editTextPwd_Register);
        editTextMobile_Register = (AppCompatEditText) findViewById(R.id.editTextMobile_Register);
        editTextAddress_Register = (AppCompatEditText) findViewById(R.id.editTextAddress_Register);
        editTextButton_Register = (Button) findViewById(R.id.editTextButton_Register);

        setClicklistener();
    }

    @Override
    public void setClicklistener() {

        editTextButton_Register.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.editTextButton_Register :
                showProgressDialog("Loading");
                validate();
                break;

        }
    }

    void validate(){
      String  Name = editTextName_Register.getText().toString();
       String email_id = editTextEmail_Register.getText().toString();
        String password = editTextPwd_Register.getText().toString();
        String Mobile = editTextMobile_Register.getText().toString();
        String Adress = editTextAddress_Register.getText().toString();


        if(TextUtils.isEmpty(Name) && TextUtils.isEmpty(email_id) && TextUtils.isEmpty(password)&& TextUtils.isEmpty(Mobile)&& TextUtils.isEmpty(Adress)){
            Toast.makeText(RegisterActivity.this, "field should not be empty", Toast.LENGTH_SHORT).show();
        }
        else if(Utility.isValidEmail(email_id)){

        }
        else if (Mobile.length() != 10) {
            editTextMobile_Register.setError("Mobile no should be  10 digit");
        }
        else if (password.length() != 5) {
            editTextPwd_Register.setError("password should be  5 character");
        }

        else{
            showProgressDialog("Loading");




            SharedPreferences sh = getSharedPreferences("smita", Context.MODE_PRIVATE);
            String N = editTextName_Register.getText().toString();
            String e = editTextEmail_Register.getText().toString();
            String p = editTextPwd_Register.getText().toString();
            String m = editTextMobile_Register.getText().toString();
            String a = editTextAddress_Register.getText().toString();
            SharedPreferences.Editor editor = sh.edit();
            editor.putString("Name", N);
            editor.putString("email_id", e);
            editor.putString("password", p);
            editor.putString("Adress", a);
            editor.putString("Mobile", m);
            editor.commit();


            hideProgressDialog();

            Toast.makeText(getApplicationContext(), "RegisterActivity Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

