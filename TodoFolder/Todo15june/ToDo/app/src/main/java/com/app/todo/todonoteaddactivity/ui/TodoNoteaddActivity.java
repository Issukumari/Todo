package com.app.todo.todonoteaddactivity.ui;

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

import com.app.todo.R;
import com.app.todo.notificationalarm.ScheduleClient;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddactivity.presenter.TodoNoteAddPresenter;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TodoNoteaddActivity extends AppCompatActivity implements TodoNoteaddActivityInterface, ColorPickerDialogListener {
    AppCompatEditText Title, Description;
    AppCompatTextView reminderdate;
    String newcolor;
    Bundle bundle;
    AppCompatImageView imageView_color_picker, imageView_reminder, imageView_save, imageView_back_arrow, imageView_remindertime;
    AppCompatTextView remindertime;
    int years, month, day, mint, hour, sec;
    private ScheduleClient scheduleClient;
    TodoHomeActivity todohomeactivity;
    DatabaseReference mfirebasedatabase;
    TimePickerDialog timePickerDialog;
    private String formattedDate;
    SharedPreferences sharedPreferences;
    private String uid;
    private TodoNoteAddPresenter presenter;
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
        presenter = new TodoNoteAddPresenter(this, this);
        reminderdate = (AppCompatTextView) findViewById(R.id.reminderdate);
        remindertime = (AppCompatTextView) findViewById(R.id.textview_remindertime);
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_add_color);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
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
        sharedPreferences = this.getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
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
    public void noteaddsuccess(String message) {
        super.onBackPressed();
        Toast.makeText(this, "note add successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noteaddFailure(String message) {
        Toast.makeText(this, "message", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(String mesage) {
        {
            progressDialog = new ProgressDialog(todohomeactivity);
            progressDialog.setMessage(mesage);

        }
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void CallBackNotes(TodoHomeDataModel model) {
        Intent intent =new Intent();
        intent.putExtras(bundle);
        startActivityForResult(intent,5);

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
                bundle = new Bundle();
                bundle.putString(Constants.Titletext, Title.getText().toString());
                bundle.putString(Constants.Desriptiontext, Description.getText().toString());
                bundle.putString(Constants.reminderKey, reminderdate.getText().toString());
                bundle.putString(Constants.remindertime, remindertime.getText().toString());
                bundle.putString(Constants.color, newcolor);
                Calendar cals = Calendar.getInstance();
                cals.set(years, month, day);
                cals.set(Calendar.HOUR_OF_DAY, hour);
                cals.set(Calendar.MINUTE, mint);
                cals.set(Calendar.SECOND, sec);
                scheduleClient.setAlarmForNotification(bundle, cals);
                presenter.addNoteToFirebase(bundle);
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
                if (!Title.getText().toString().isEmpty()) {
                    bundle = new Bundle();
                    bundle.putString(Constants.Titletext, Title.getText().toString());
                    bundle.putString(Constants.Desriptiontext, Description.getText().toString());
                    bundle.putString(Constants.reminderKey, reminderdate.getText().toString());
                    bundle.putString(Constants.remindertime, remindertime.getText().toString());
                    bundle.putString(Constants.color, newcolor);
                    Calendar cal = Calendar.getInstance();
                    cal.set(years, month, day);
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, mint);
                    cal.set(Calendar.SECOND, sec);
                    scheduleClient.setAlarmForNotification(bundle, cal);
                    presenter.addNoteToFirebase(bundle);
                }
                onBackPressed();
                supportFinishAfterTransition();
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


    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void getColorPicker() {

        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(true)
                .setDialogId(DIALOG_ID)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .show(this);
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        newcolor = String.valueOf(color);
        relativeLayout.setBackgroundColor(color);
        Toast.makeText(this, "Selected Color: #" + Integer.toHexString(color),
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onStop() {
        if (scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }
}

