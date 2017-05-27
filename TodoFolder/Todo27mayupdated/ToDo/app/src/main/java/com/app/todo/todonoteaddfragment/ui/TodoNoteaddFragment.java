package com.app.todo.todonoteaddfragment.ui;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatEditText;
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
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddfragment.presenter.Todonoteaddpresenter;
import com.app.todo.todonoteaddfragment.presenter.TodonoteaddpresenterInterface;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class TodoNoteaddFragment extends Fragment implements TodonoteaddFragmentInterface,ColorPickerDialogListener {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatTextView reminderdate;
    String newcolor;
    TodoHomeActivity todohomeactivity;
    TodoNoteaddFragment todonoteadd;
    DatabaseReference mfirebasedatabase;
    SharedPreferences sharedPreferences;
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
    private String uid;
    private TodonoteaddpresenterInterface presenter;
    private ProgressDialog progressDialog;
    private Menu menu;
    private String noteColor;
    private RelativeLayout relativeLayout;
    private int DIALOG_ID=10;
    private LinearLayout linearlayout;
    private TodoNoteaddFragment todonoteaddfragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.todonoteaddfragment, container, false);
        presenter = new Todonoteaddpresenter(todohomeactivity, this);

        Title = (AppCompatEditText) view.findViewById(R.id.edittext_title);
        Description = (AppCompatEditText) view.findViewById(R.id.edittext_description);
        reminderdate = (AppCompatTextView) view.findViewById(R.id.reminderdate);
        linearlayout=(LinearLayout) view.findViewById(R.id.cardlayout);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        reminderdate.setOnClickListener(this);

        setHasOptionsMenu(true);

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
    public void onClick(View view) {
       /* new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();*/
    }

    @Override
    public void noteaddsuccess(String message) {
        getFragmentManager().popBackStack();
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

        if(menu!=null){
            MenuItem menuItemSearch = menu.findItem(R.id.search);
            menuItemSearch.setVisible(false);

            MenuItem menuItemListToGrid = menu.findItem(R.id.show_as_view);
            menuItemListToGrid.setVisible(false);
        }

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.ic_action_save:
                Bundle bundle = new Bundle();
                bundle.putString(Constants.Titletext, Title.getText().toString());
                bundle.putString(Constants.Desriptiontext, Description.getText().toString());
                bundle.putString(Constants.reminderKey, reminderdate.getText().toString());
                bundle.putString(Constants.color,newcolor);
                presenter.addNoteToFirebase(bundle);
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

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
      //  noteColor= String.valueOf(color);
      //  linearlayout.setBackgroundColor(color);
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
        newcolor= String.valueOf(color);
        linearlayout.setBackgroundColor(color);
    }
}