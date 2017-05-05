package com.app.todo.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TodoNoteaddFragment extends Fragment implements View.OnClickListener {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatTextView reminderdate;
    AppCompatButton save_button;
    TodoHomeActivity todohomeactivity;
    DatabaseReference mfirebasedatabase;
    SharedPreferences sharedPreferences;
    NoteDatabase database;
    TodoHomeDataModel todoHomeDataModel;
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
    private TodoHomeDataModel todoHomeDataMode;

    public TodoNoteaddFragment(TodoHomeActivity todoHomeActivity) {
        this.todohomeactivity = todoHomeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todonoteaddfragment, container, false);
        Title = (AppCompatEditText) view.findViewById(R.id.edittext_title);
        Description = (AppCompatEditText) view.findViewById(R.id.edittext_description);
        reminderdate = (AppCompatTextView) view.findViewById(R.id.reminderdate);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");

        reminderdate.setOnClickListener(this);

        save_button = (AppCompatButton) view.findViewById(R.id.save_button);
        if (savedInstanceState == null) {
        }

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = new NoteDatabase(getActivity());
                todoHomeDataModel = new TodoHomeDataModel();
                todoHomeDataModel.setId(0);//setId(0)
                todoHomeDataModel.setTitle(Title.getText().toString());
                todoHomeDataModel.setDescription(Description.getText().toString());
                todoHomeDataModel.setReminderDate(reminderdate.getText().toString());
                database.addItem(todoHomeDataModel);

                getIndex(todoHomeDataModel);
            }
        });
        return view;
    }

    private void getIndex(final TodoHomeDataModel todoHomeDataModel) {
        todoHomeDataMode = todoHomeDataModel;
        final boolean[] flag = {true};
        final GenericTypeIndicator<ArrayList<TodoHomeDataModel>> typeIndicator = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
        };
        try {

            mfirebasedatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int index = 0;
                    ArrayList<TodoHomeDataModel> todoHomeDataModels_ArrayList = new ArrayList<TodoHomeDataModel>();
                    if (dataSnapshot.child("note_details").hasChild(uid)) {
                        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                        String currentDate = format.format(new Date().getTime());
                        todoHomeDataModels_ArrayList.addAll(dataSnapshot.child("note_details").child(uid)
                                .child(currentDate)
                                .getValue(typeIndicator));
                    }
                    index = todoHomeDataModels_ArrayList.size();
                    if (todoHomeDataMode != null) {
                        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                        String currentDate = format.format(new Date().getTime());
                        todoHomeDataMode.setStartdate(currentDate);
                        if (dataSnapshot.child("note_details").child(uid).child(currentDate).exists()) {
                            putdata(index, todoHomeDataModel);
                            todoHomeDataMode = null;
                        } else {
                            putdata(0, todoHomeDataMode);
                            todoHomeDataMode = null;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {

        }
    }

    private void putdata(int index, TodoHomeDataModel todoHomeDataModel) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = format.format(new Date().getTime());
        todoHomeDataModel.setId(index);
        mfirebasedatabase.child("note_details").child(uid).child(currentDate).child(String.valueOf(index)).setValue(todoHomeDataModel);
        todohomeactivity.fab.setVisibility(View.VISIBLE);
        getFragmentManager().popBackStack();

    }

    private void updateLabel() {

        String myFormat = "MMMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        reminderdate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View view) {
        new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}