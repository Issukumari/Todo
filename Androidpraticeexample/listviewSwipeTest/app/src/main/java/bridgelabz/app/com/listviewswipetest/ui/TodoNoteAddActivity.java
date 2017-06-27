package bridgelabz.app.com.listviewswipetest.ui;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Toast;

import bridgelabz.app.com.listviewswipetest.R;
import bridgelabz.app.com.listviewswipetest.base.BaseActivity;

public class TodoNoteAddActivity extends BaseActivity {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatButton save_button;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_note_add);
        if (savedInstanceState == null) {
            initView();

        }
    }

    @Override
    public void initView() {
        Title = (AppCompatEditText) findViewById(R.id.edittext_title);
        Description = (AppCompatEditText) findViewById(R.id.edittext_description);
        save_button = (AppCompatButton) findViewById(R.id.save_button);
        // setListeners();
        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String titleData = Title.getText().toString();
                String DescriptiontData = Description.getText().toString();
                //String recentTimeData = timeTextView.getText().toString();
                Intent intent = new Intent(TodoNoteAddActivity.this, TodoHomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Title", titleData);
                bundle.putString("Description", DescriptiontData);
                // bundle.putString("date_data", recentTimeData);
                intent.putExtra("bundle", bundle);
                setResult(RESULT_OK, intent);
                finish();
                Toast.makeText(TodoNoteAddActivity.this, "Saved..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setListeners() {

    }
}