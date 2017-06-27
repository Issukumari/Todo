
package fundoohr.com.todoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    EditText fullname6;
    EditText email3;
    EditText passwordd4;
    EditText Mobile5;
    EditText Address8;
    Button save;
     String Name;
     String email_id;
     String password;
     String Mobile;
     String Adress;

    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";
    private static final String PASSWORD_PATTERN = "\\d+";

    private Matcher mMatcher;
    private Pattern mPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }
    public void save(View view) {
        fullname6 = (EditText) findViewById(R.id.edit6);
        email3 = (EditText) findViewById(R.id.edit3);
        passwordd4 = (EditText) findViewById(R.id.edit4);
        Mobile5 = (EditText) findViewById(R.id.edit5);
        Address8 = (EditText) findViewById(R.id.edit8);
        save = (Button) findViewById(R.id.save);
        boolean checkName = false;
        boolean checkMail = false;
        boolean checkPassword = false;
        boolean checkMobNo = false;
        boolean checkAddress = false;
        Name = fullname6.getText().toString();
        email_id = email3.getText().toString();
        password = passwordd4.getText().toString();
        Mobile = Mobile5.getText().toString();
        Adress = Address8.getText().toString();
        if (fullname6.length() == 0) {
            fullname6.setError("First name not entered");
        } else {
            checkName = true;
        }


        if (email_id.length() == 0) {
            email3.setError("email should not empty");
        } else if (!isValidEmail(email_id)) {
            email3.setError("invalid email");
        } else {
            checkMail = true;

            if (Mobile5.length() == 0) {
                Mobile5.setError("Enter your mob number");
            } else if (Mobile.length() != 10) {
                Mobile5.setError("Mobile no should be  10 digit");
            } else {
                checkMobNo = true;
            }
            if (Adress.length() == 0) {
                Address8.setError("Enter your Address ");
            } else {
                checkAddress = true;
            }

            if (password.length() == 0) {
                passwordd4.setError("Password should not be blank");
            } else if (password.length() != 5) {
                passwordd4.setError("password should be  5 character");
            } else {
                checkPassword = true;
            }


            if (checkName && checkMail && checkMobNo && checkPassword && checkAddress) {
                SharedPreferences sh = getSharedPreferences("smita", Context.MODE_PRIVATE);
                String N = fullname6.getText().toString();
                String e = email3.getText().toString();
                String p = passwordd4.getText().toString();
                String m = Mobile5.getText().toString();
                String a = Address8.getText().toString();
                SharedPreferences.Editor editor = sh.edit();
                editor.putString("Name", N);
                editor.putString("email_id", e);
                editor.putString("password", p);
                editor.putString("Adress", a);
                editor.putString("Mobile", m);
                editor.commit();

                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }
    private boolean isValidEmail(String email_id) {
        return Patterns.EMAIL_ADDRESS.matcher(email_id).matches();
    }


    }




/*
        if (fullname6.getText().toString().length() >= 10) {
            fullname6.setError("Full name not entered");
            fullname6.requestFocus();
        }
        if (email3.getText().toString().length() == 0) {
            email3.setError("Email should not be empty");
            email3.requestFocus();
        }

        if (passwordd4.getText().toString().length() == 0) {
            passwordd4.setError("Password should mot be empty");
            passwordd4.requestFocus();
        } else {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
        }
        if (Mobile5.getText().toString().length() != 10) {
            Mobile5.setError("Mobile no should not be empty");
            Mobile5.requestFocus();


        } else {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
        }
        if (Address8.getText().toString().length() == 0) {
            Address8.setError(" Address should not be empty");
            Address8.requestFocus();
        }
        Intent intent=new Intent(Registration.this,MainActivity.class);
        finish();
*/

