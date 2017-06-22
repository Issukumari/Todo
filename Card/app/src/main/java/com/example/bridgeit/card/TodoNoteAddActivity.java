package com.example.bridgeit.card;



import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TodoNoteAddActivity extends AppCompatActivity implements View.OnClickListener {
    AppCompatEditText Title, Description;
    AppCompatTextView reminderdate;
    String newcolor;
    Bundle bundle;
    AppCompatImageView imageView_color_picker, imageView_reminder, imageView_save, imageView_back_arrow, imageView_remindertime;
    AppCompatTextView remindertime;
    int years, month, day, mint, hour, sec;
    TimePickerDialog timePickerDialog;
    private String formattedDate;
    SharedPreferences sharedPreferences;
    private String uid;
    private ProgressDialog progressDialog;
    private Menu menu;
    private int DIALOG_ID = 10;
    private RelativeLayout relativeLayout;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            years = year;
            month = monthOfYear;
            day = dayOfMonth;
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todonoteaddactivity);
        Title = (AppCompatEditText) findViewById(R.id.title);
        Description = (AppCompatEditText) findViewById(R.id.edittext_description);
        reminderdate = (AppCompatTextView) findViewById(R.id.reminderdate);
        remindertime = (AppCompatTextView) findViewById(R.id.textview_remindertime);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_add_color);
        imageView_color_picker = (AppCompatImageView) findViewById(R.id.imageView_color_picker);
        imageView_reminder = (AppCompatImageView) findViewById(R.id.imageView_reminder);
        imageView_save = (AppCompatImageView) findViewById(R.id.imageView_save);
        imageView_back_arrow = (AppCompatImageView) findViewById(R.id.imageView_back_arrow);
        imageView_remindertime = (AppCompatImageView) findViewById(R.id.imageView_remindertime);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        formattedDate = df.format(c.getTime());
        hour = c.getTime().getHours();
        mint = c.getTime().getMinutes();
        sec = c.getTime().getSeconds();
        remindertime.setText(formattedDate);
        sharedPreferences = this.getSharedPreferences("smita", Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        imageView_color_picker.setOnClickListener(this);
        imageView_reminder.setOnClickListener(this);
        imageView_save.setOnClickListener(this);
        imageView_remindertime.setOnClickListener(this);
        imageView_back_arrow.setOnClickListener(this);
        if (savedInstanceState == null) {
        }
    }

    private void updateLabel() {
        String myFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        reminderdate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        MenuItem menuItemSearch = menu.findItem(R.id.search);
        menuItemSearch.setVisible(false);
        MenuItem menuItemListToGrid = menu.findItem(R.id.show_as_view);
        menuItemListToGrid.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle("Notes");
        if (menu != null) {
            MenuItem menuItemSearch = menu.findItem(R.id.search);
            menuItemSearch.setVisible(false);

            MenuItem menuItemListToGrid = menu.findItem(R.id.show_as_view);
            menuItemListToGrid.setVisible(false);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_save:

                break;

            case R.id.imageView_reminder:
                new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.imageView_remindertime:
                setRemindertime();
                break;
            case R.id.imageView_back_arrow:


                break;
            case R.id.imageView_color_picker:
                getColorPicker();
            default:
                return;
        }
    }

    private void setRemindertime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        mint = minute;
                        hour = hourOfDay;
                        remindertime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }




    private void getColorPicker() {


    }
}

