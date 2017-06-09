package com.app.todo.updateactivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteUpdateActivity extends AppCompatActivity implements View.OnClickListener,ColorPickerDialogListener {
    AppCompatEditText fragmentlistview_TitleEditText;
    AppCompatEditText fragmentlistview_DescriptionEditText;
    String uid;
    AppCompatTextView reminderdate;
    String newcolor;
    DatabaseReference mfirebasedatabase;
    AppCompatImageButton backbutton;
    FirebaseAuth firebaseAuth;
    String noteTitle, noteDescription;
    int id;
    TodoHomeDataModel dataModel;
    private int DIALOG_ID = 10;
    private LinearLayout linearlayout;
    private SharedPreferences sharedPreferences;
    private NoteDatabase database;
    private String currentDate;
    private Menu menu;
    String startDate;
    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        }

    };


    public NoteUpdateActivity(TodoHomeDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public NoteUpdateActivity() {

    }

    private void updateLabel() {
        String myFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        reminderdate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noteupdatectivity);
        // this.postponeEnterTransition();
        fragmentlistview_TitleEditText = (AppCompatEditText) findViewById(R.id.fragmentlistview_TitleEditText);
        fragmentlistview_DescriptionEditText = (AppCompatEditText) findViewById(R.id.fragmentlistview_DescriptionEditText);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        reminderdate = (AppCompatTextView) findViewById(R.id.reminderdate);
        backbutton = (AppCompatImageButton) findViewById(R.id.back_button);
        linearlayout = (LinearLayout) findViewById(R.id.layout_update_color);
        backbutton.setOnClickListener(this);
        sharedPreferences = this.getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        firebaseAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            startDate=bundle.getString("startdate");
            noteTitle = (bundle.getString(Constants.Titletext));
            noteDescription = bundle.getString(Constants.Desriptiontext);
            newcolor = bundle.getString(Constants.color);
            if (bundle.containsKey("id"))
                id = (bundle.getInt("id"));
            fragmentlistview_TitleEditText.setText(noteTitle);
            fragmentlistview_DescriptionEditText.setText(noteDescription);
            if (newcolor != null) {
                linearlayout.setBackgroundColor(Integer.parseInt(newcolor));

            }
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
            currentDate = format.format(new Date().getTime());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;

        MenuItem menuItemSearch = menu.findItem(R.id.search);
        menuItemSearch.setVisible(false);

        MenuItem menuItemListToGrid = menu.findItem(R.id.show_as_view);
        menuItemListToGrid.setVisible(false);

       return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_action_save:
                database = new NoteDatabase(this);
                dataModel=new TodoHomeDataModel();
                dataModel.setId(id);
                dataModel.setTitle(fragmentlistview_TitleEditText.getText().toString());
                dataModel.setDescription(fragmentlistview_DescriptionEditText.getText().toString());
                dataModel.setReminderDate(reminderdate.getText().toString());
                dataModel.setColor(newcolor);
                dataModel.setStartdate(startDate);
                database.updateItem(dataModel);
                mfirebasedatabase.child("note_details").child(uid).child(startDate).child(String.valueOf(dataModel.getId())).setValue(dataModel);
                finish();
                return false;
            case R.id.back_button:
                onBackPressed();
                supportFinishAfterTransition();
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
    public void onClick(View view) {
    }
    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        newcolor = String.valueOf(color);
        linearlayout.setBackgroundColor(color);
        Toast.makeText(this, "Selected Color: #" + Integer.toHexString(color),
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}


