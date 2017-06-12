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
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    AppCompatImageView imageView_color_picker,imageView_reminder,imageView_save;
    AppCompatImageView imageView_back_arrow;
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
    private RelativeLayout relativeLayout;
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
        fragmentlistview_TitleEditText = (AppCompatEditText) findViewById(R.id.fragmentlistview_TitleEditText);
        fragmentlistview_DescriptionEditText = (AppCompatEditText) findViewById(R.id.fragmentlistview_DescriptionEditText);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        reminderdate = (AppCompatTextView) findViewById(R.id.reminderdate);
        imageView_color_picker=(AppCompatImageView)findViewById(R.id.imageView_color_picker);
        imageView_reminder=(AppCompatImageView)findViewById(R.id.imageView_reminder);
        imageView_save=(AppCompatImageView)findViewById(R.id.imageView_save);
        imageView_back_arrow=(AppCompatImageView)findViewById(R.id.imageView_back_arrow) ;
        relativeLayout = (RelativeLayout) findViewById(R.id.layout_update_color1);
        sharedPreferences = this.getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        firebaseAuth = FirebaseAuth.getInstance();
        imageView_color_picker.setOnClickListener(this);
        imageView_reminder.setOnClickListener(this);
        imageView_save.setOnClickListener(this);
        imageView_back_arrow.setOnClickListener(this);
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
                relativeLayout.setBackgroundColor(Integer.parseInt(newcolor));

            }
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
            currentDate = format.format(new Date().getTime());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_update,menu);
        this.menu = menu;
       return true;
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imageView_save:
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
               break;
            case R.id.imageView_back_arrow:
                if(!fragmentlistview_TitleEditText.getText().toString().isEmpty()) {
                    database = new NoteDatabase(this);
                    dataModel = new TodoHomeDataModel();
                    dataModel.setId(id);
                    dataModel.setTitle(fragmentlistview_TitleEditText.getText().toString());
                    dataModel.setDescription(fragmentlistview_DescriptionEditText.getText().toString());
                    dataModel.setReminderDate(reminderdate.getText().toString());
                    dataModel.setColor(newcolor);
                    dataModel.setStartdate(startDate);
                    database.updateItem(dataModel);
                    mfirebasedatabase.child("note_details").child(uid).child(startDate).child(String.valueOf(dataModel.getId())).setValue(dataModel);
                }
                onBackPressed();
                supportFinishAfterTransition();
                 break;
            case R.id.imageView_reminder:
                new DatePickerDialog(this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                  break;

            case R.id.imageView_color_picker:
                getColorPicker();
            default:
                return;
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
    public void onColorSelected(int dialogId, @ColorInt int color) {
        newcolor = String.valueOf(color);
        relativeLayout.setBackgroundColor(color);
        Toast.makeText(this, "Selected Color: #" + Integer.toHexString(color),
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
    }



