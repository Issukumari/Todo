
package bridgelabz.app.com.listviewswipetest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;

import bridgelabz.app.com.listviewswipetest.R;


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



    }}}