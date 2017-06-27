package fundoohr.com.todoapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    AppCompatEditText id;
    AppCompatEditText pass;
    AppCompatButton login;
    AppCompatButton Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id = (AppCompatEditText) findViewById(R.id.edittext1);
        pass = (AppCompatEditText) findViewById(R.id.edittext2);
        login = (AppCompatButton) findViewById(R.id.btn1);
        Register = (AppCompatButton) findViewById(R.id.btn2);

        login.setOnClickListener(this);
        Register.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                SharedPreferences sharedPreferences = getSharedPreferences("smita", Context.MODE_PRIVATE);
                String id1 = sharedPreferences.getString("email_id", "value");
                String pass1 = sharedPreferences.getString("password", "value");
                if (id.getText().toString().equalsIgnoreCase(id1) && pass.getText().toString().equalsIgnoreCase(pass1)) {
                    Toast.makeText(LoginActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "invalid id or password", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn2 :
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
}