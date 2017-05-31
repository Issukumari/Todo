package com.app.todo.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragmentlistviewdetails extends Fragment implements View.OnClickListener {
    AppCompatEditText fragmentlistview_TitleEditText;
    AppCompatEditText fragmentlistview_DescriptionEditText;
    String uid;
    AppCompatTextView reminderdate;
    String newcolor;

    DatabaseReference mfirebasedatabase;
    AppCompatButton Update_button;
    AppCompatImageButton backbutton;
    Task<Void> index;
    FirebaseAuth firebaseAuth;
    String noteTitle, noteDescription;
    int id;
    TodoHomeDataModel dataModel;
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
    private int DIALOG_ID = 10;
    private LinearLayout linearlayout;
    private SharedPreferences sharedPreferences;
    private NoteDatabase database;
    private String currentDate;
    private Menu menu;

    public Fragmentlistviewdetails(TodoHomeDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public Fragmentlistviewdetails() {

    }

    private void updateLabel() {
        String myFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        reminderdate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listviewdetails, container, false);
        setHasOptionsMenu(true);
        fragmentlistview_TitleEditText = (AppCompatEditText) view.findViewById(R.id.fragmentlistview_TitleEditText);
        fragmentlistview_DescriptionEditText = (AppCompatEditText) view.findViewById(R.id.fragmentlistview_DescriptionEditText);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        reminderdate = (AppCompatTextView) view.findViewById(R.id.reminderdate);
        backbutton = (AppCompatImageButton) view.findViewById(R.id.back_button);
        reminderdate.setOnClickListener(this);
        linearlayout = (LinearLayout) view.findViewById(R.id.layout_update_color);
        backbutton.setOnClickListener(this);
        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
/*
        Update_button.setOnClickListener(this);
*/
        Bundle bundle = getArguments();

        if (bundle != null)
        {
            noteTitle = (bundle.getString(Constants.Titletext));
            noteDescription = bundle.getString(Constants.Desriptiontext);
            if (bundle.containsKey("id"))
                id = (bundle.getInt("id"));
            fragmentlistview_TitleEditText.setText(noteTitle);
            fragmentlistview_DescriptionEditText.setText(noteDescription);
            SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
            currentDate = format.format(new Date().getTime());
        }
        return view;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

        MenuItem menuItemSearch = menu.findItem(R.id.search);
        menuItemSearch.setVisible(false);

        MenuItem menuItemListToGrid = menu.findItem(R.id.show_as_view);
        menuItemListToGrid.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_action_save:
                database = new NoteDatabase(getActivity());
                dataModel.setTitle(fragmentlistview_TitleEditText.getText().toString());
                dataModel.setDescription(fragmentlistview_DescriptionEditText.getText().toString());
                dataModel.setReminderDate(reminderdate.getText().toString());
                dataModel.setColor(newcolor);
                database.updateItem(dataModel);
                mfirebasedatabase.child("note_details").child(uid).child(dataModel.getStartdate()).child(String.valueOf(dataModel.getId())).setValue(dataModel);
                getActivity().getFragmentManager().popBackStackImmediate();
                return false;
            case R.id.back_button:
                getActivity().getFragmentManager().popBackStackImmediate();
                return false;
            case R.id.ic_action_reminder:
                new DatePickerDialog(getActivity(), date, myCalendar
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
                .show(getActivity());
    }

    @Override
    public void onClick(View view) {
    }

    public void setColor(int color) {
        newcolor = String.valueOf(color);
        linearlayout.setBackgroundColor(color);

    }
}



































