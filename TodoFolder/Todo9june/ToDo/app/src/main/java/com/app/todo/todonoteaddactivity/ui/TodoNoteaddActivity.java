package com.app.todo.todonoteaddactivity.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.notificationalarm.ScheduleClient;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddactivity.presenter.TodoNoteAddPresenter;
import com.app.todo.todonoteaddactivity.presenter.TodoNoteAddPresenterInterface;
import com.app.todo.todonoteaddfragment.presenter.Todonoteaddpresenter;
import com.app.todo.todonoteaddfragment.presenter.TodonoteaddpresenterInterface;
import com.app.todo.todonoteaddfragment.ui.TodonoteaddFragmentInterface;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TodoNoteaddActivity extends AppCompatActivity implements TodoNoteaddActivityInterface, ColorPickerDialogListener {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatTextView reminderdate;
    String newcolor;
    Bundle bundle;
    int years, month, day, mint, hour, sec;
    private ScheduleClient scheduleClient;
    TodoHomeActivity todohomeactivity;
    DatabaseReference mfirebasedatabase;
    SharedPreferences sharedPreferences;
    private String uid;
    private TodoNoteAddPresenter presenter;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todonoteaddactivity);

        presenter = new TodoNoteAddPresenter(this,this);
        Title = (AppCompatEditText) findViewById(R.id.edittext_title);
        Description = (AppCompatEditText) findViewById(R.id.edittext_description);
        reminderdate = (AppCompatTextView)findViewById(R.id.reminderdate);
        linearlayout = (LinearLayout) findViewById(R.id.cardlayout);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = this.getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        //reminderdate.setOnClickListener(this);
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();
        //setHasOptionsMenu(true);
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

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_action_save:
                bundle = new Bundle();
                bundle.putString(Constants.Titletext, Title.getText().toString());
                bundle.putString(Constants.Desriptiontext, Description.getText().toString());
                bundle.putString(Constants.reminderKey, reminderdate.getText().toString());
                //bundle.putString(Constants.remindertime,remindertime.getText().toString());
                bundle.putString(Constants.color, newcolor);
                Calendar cals = Calendar.getInstance();
                cals.set(years, month, day);
                cals.set(Calendar.HOUR_OF_DAY, hour);
                cals.set(Calendar.MINUTE, mint + 1);
                cals.set(Calendar.SECOND, sec);
                scheduleClient.setAlarmForNotification(bundle, cals);
                presenter.addNoteToFirebase(bundle);
                return false;

            case R.id.ic_action_reminder:
                new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                return false;
            case R.id.ic_action_color_pick:
                getColorPicker();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   /* @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
    */

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
                linearlayout.setBackgroundColor(color);
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(this, "Selected Color: #" + Integer.toHexString(color),
                        Toast.LENGTH_SHORT).show();


        }


    /*public void setcolor(int color) {
        newcolor = String.valueOf(color);
        linearlayout.setBackgroundColor(color);
    }
*/
    @Override
    public void onStop() {
        if (scheduleClient != null)
            scheduleClient.doUnbindService();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
    }

}
