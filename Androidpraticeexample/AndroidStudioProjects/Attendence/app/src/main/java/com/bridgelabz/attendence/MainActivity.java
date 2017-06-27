package com.bridgelabz.attendence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void submitButton(View view){
        EditText mobileNumber= (EditText) findViewById(R.id.mobileNumber);
        if(mobileNumber.getText().toString().length()!=10)
            mobileNumber.setError("Enter 10 digit mobile Number");
        else
            startActivity(new Intent(MainActivity.this,Main2Activity.class));

    }
}
