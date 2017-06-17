package com.app.todo.todonoteaddfragment.ui;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.notificationalarm.ScheduleClient;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddfragment.presenter.TodoNoteAddPresenter;
import com.app.todo.todonoteaddfragment.presenter.TodoNoteAddPresenterInterface;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TodoNoteaddFragment extends Fragment implements TodoNoteAddFragmentInterface, ColorPickerDialogListener {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatTextView reminderdate;
    String newcolor;
    AppCompatImageView imageView_color_picker, imageView_reminder, imageView_save, imageView_back_arrow, imageView_remindertime;
    Bundle bundle;
    private String formattedDate;
    int years, month, day, mint, hour, sec;
    private ScheduleClient scheduleClient;
    TodoHomeActivity todohomeactivity;
    DatabaseReference mfirebasedatabase;
    SharedPreferences sharedPreferences;
    private String uid;
    AppCompatTextView remindertime;
    private TodoNoteAddPresenterInterface presenter;
    private ProgressDialog progressDialog;
    private Menu menu;
    private int DIALOG_ID = 10;
    private LinearLayout linearlayout;

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
    private RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todonoteaddfragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        presenter = new TodoNoteAddPresenter(todohomeactivity, this);
        Title = (AppCompatEditText) view.findViewById(R.id.title);
        Description = (AppCompatEditText) view.findViewById(R.id.edittext_description);
        reminderdate = (AppCompatTextView) view.findViewById(R.id.reminderdate);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.layout_add_colorfragment);
        remindertime = (AppCompatTextView) view.findViewById(R.id.textview_remindertime);
        imageView_color_picker = (AppCompatImageView) view.findViewById(R.id.imageView_color_picker);
        imageView_reminder = (AppCompatImageView) view.findViewById(R.id.imageView_reminder);
        imageView_save = (AppCompatImageView) view.findViewById(R.id.imageView_save);
        imageView_back_arrow = (AppCompatImageView) view.findViewById(R.id.imageView_back_arrow);
        imageView_remindertime = (AppCompatImageView) view.findViewById(R.id.imageView_remindertime);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        formattedDate = df.format(c.getTime());
        hour = c.getTime().getHours();
        mint = c.getTime().getMinutes();
        sec = c.getTime().getSeconds();
        remindertime.setText(formattedDate);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        reminderdate.setOnClickListener(this);
        scheduleClient = new ScheduleClient(getActivity());
        scheduleClient.doBindService();
        setHasOptionsMenu(true);
        imageView_color_picker.setOnClickListener(this);
        imageView_reminder.setOnClickListener(this);
        imageView_save.setOnClickListener(this);
        imageView_remindertime.setOnClickListener(this);
        imageView_back_arrow.setOnClickListener(this);
        if (savedInstanceState == null) {
        }
        return view;
    }


    private void updateLabel() {
        String myFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        reminderdate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void noteaddsuccess(String message) {
        getFragmentManager().popBackStack();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        Toast.makeText(getActivity(), "note add successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noteaddFailure(String message) {
        Toast.makeText(getActivity(), "message", Toast.LENGTH_SHORT).show();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

        MenuItem menuItemSearch = menu.findItem(R.id.search);
        menuItemSearch.setVisible(false);

        MenuItem menuItemListToGrid = menu.findItem(R.id.show_as_view);
        menuItemListToGrid.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Notes");

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
                new DatePickerDialog(getActivity(), date, myCalendar
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
                break;

            case R.id.imageView_color_picker:
                getColorPicker();
                break;
        }
    }

    private void setRemindertime() {

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
    public void onColorSelected(int dialogId, @ColorInt int color) {
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
                .show(getActivity());
    }

    public void setcolor(int color) {
        newcolor = String.valueOf(color);
        relativeLayout.setBackgroundColor(color);
    }

    @Override
    public void onStop() {
        if (scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }


}