package com.app.todo.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.baseclass.BaseActivity;
import com.app.todo.database.DataBaseUtility;
import com.app.todo.fragment.FragmentDatePicker;
import com.app.todo.model.NotesModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotesAddActivity extends BaseActivity implements View.OnClickListener {
    AppCompatImageButton imageButton;
    AppCompatTextView timeTextView;
    AppCompatEditText titleEdittext, contentEdittext;
    public List<NotesModel> data = new ArrayList<>();
    DataBaseUtility database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notesadd);


        initView();
        Date date = new Date();
        CharSequence sequence = DateFormat.format("E dd.MM.yyyy, hh:mm a", date.getTime());
        timeTextView.setText(sequence);

    }

    @Override
    public void initView() {

        imageButton = (AppCompatImageButton) findViewById(R.id.back_button);
        timeTextView = (AppCompatTextView) findViewById(R.id.recenttime_textView);
        titleEdittext = (AppCompatEditText) findViewById(R.id.title_edittext);
        contentEdittext = (AppCompatEditText) findViewById(R.id.content_edittext);
        setClicklistener();
    }

    @Override
    public void setClicklistener() {
        imageButton.setOnClickListener(this);
        timeTextView.setOnClickListener(this);
        titleEdittext.setOnClickListener(this);
        contentEdittext.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_item_details_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_pushpin:

                Toast.makeText(this, "pined..", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

            case R.id.action_reminder:
                DialogFragment newFragment = new FragmentDatePicker();
                newFragment.show(getFragmentManager(), "DatePicker");
                return super.onOptionsItemSelected(item);

            case R.id.action_save:
                database=new DataBaseUtility(this);
                NotesModel model=new NotesModel();
                String titleData = titleEdittext.getText().toString();
                String contentData = contentEdittext.getText().toString();
                String recentTimeData = timeTextView.getText().toString();
                model.setTitle(titleData);
                model.setContent(contentData);
                model.setDate(recentTimeData);
                Intent intent = new Intent(NotesAddActivity.this, TodoNotesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Title_data", titleData);
                bundle.putString("Content_data", contentData);
                bundle.putString("date_data", recentTimeData);
                intent.putExtra("bundle", bundle);
                setResult(RESULT_OK, intent);
                database.addNotes(model);
                finish();
                Toast.makeText(this, "Saved..", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                Log.i("abc", "onClick: ");
                Intent intent = new Intent(this, TodoNotesActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }
}
