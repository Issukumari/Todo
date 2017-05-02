package com.app.todo.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragmentlistviewdetails extends Fragment implements View.OnClickListener {
    AppCompatEditText fragmentlistview_TitleEditText;
    AppCompatEditText fragmentlistview_DescriptionEditText;
    String uid;
    AppCompatTextView reminder;

    DatabaseReference mfirebasedatabase;
    AppCompatButton Update_button;
    TodoHomeActivity todoHomeActivity;
    Task<Void> index;
    FirebaseAuth firebaseAuth;
    String noteTitle, noteDescription;
    int id;
    private SharedPreferences sharedPreferences;
    private NoteDatabase database;
    private TodoHomeDataModel todoHomeDataModel;
    private String currentDate;

    public Fragmentlistviewdetails(TodoHomeActivity todoHomeActivity) {
        this.todoHomeActivity = todoHomeActivity;
    }

    public Fragmentlistviewdetails() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listviewdetails, container, false);
        fragmentlistview_TitleEditText = (AppCompatEditText) view.findViewById(R.id.fragmentlistview_TitleEditText);
        fragmentlistview_DescriptionEditText = (AppCompatEditText) view.findViewById(R.id.fragmentlistview_DescriptionEditText);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        Update_button = (AppCompatButton) view.findViewById(R.id.Update_buton);
        reminder = (AppCompatTextView) view.findViewById(R.id.reminder);
        reminder.setOnClickListener(this);

        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "");
        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();

        Update_button.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Update_buton:

                database = new NoteDatabase(getActivity());
                todoHomeDataModel = new TodoHomeDataModel();
                todoHomeDataModel.setTitle(fragmentlistview_TitleEditText.getText().toString());
                todoHomeDataModel.setDescription(fragmentlistview_DescriptionEditText.getText().toString());
                todoHomeDataModel.setId(id);
                database.updateItem(todoHomeDataModel);
                mfirebasedatabase.child("note_details").child(uid).child(currentDate).child(String.valueOf(todoHomeDataModel.getId())).setValue(todoHomeDataModel);
                getActivity().getFragmentManager().popBackStackImmediate();
                break;
        }
    }
}