package com.app.todo.notificationalarm;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;

import com.app.todo.R;
import com.app.todo.utils.Constants;

import java.util.Calendar;

/**
 * Created by bridgeit on 6/6/17.
 */
public class DummyActivity extends AppCompatActivity {
    AppCompatEditText titletextView, contenttextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy);
        titletextView = (AppCompatEditText) findViewById(R.id.edittext_titlea);
        contenttextView = (AppCompatEditText) findViewById(R.id.edittext_descriptiona);
        Bundle bundle = getIntent().getExtras();
        titletextView.setText(bundle.getString(Constants.Titletext) );
        contenttextView.setText(bundle.getString(Constants.Desriptiontext));
        if (bundle != null) {
            titletextView.setText(bundle.getString(Constants.Titletext) );
            contenttextView.setText(bundle.getString(Constants.Desriptiontext));
        }

    }

}
