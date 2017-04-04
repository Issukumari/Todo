
package com.app.todo.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;

import com.app.todo.R;

public class NoteActivity extends AppCompatActivity {
    AppCompatEditText noteedittextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        noteedittextView = (AppCompatEditText) findViewById(R.id.noteEdittextView);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("name");

        if (bundle != null) {

            String data = (String) bundle.get("text");
            noteedittextView.setText(data);



       // ((Activity) context).getFragmentManager().beginTransaction().replace(R.id.frame, new Fragmentlistview()).addToBackStack(null).commit();
    }}}