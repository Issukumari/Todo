package com.app.todo.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.model.DataModel;
import com.app.todo.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TodoNoteaddFragment extends Fragment {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatEditText id;
    AppCompatButton save_button;
    TodoHomeActivity todohomeactivity;
    DatabaseReference mfirebasedatabase;
    SharedPreferences sharedPreferences;
    private String uid;

    public TodoNoteaddFragment(TodoHomeActivity todoHomeActivity) {
        this.todohomeactivity = todoHomeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todonoteaddfragment, container, false);
        Title = (AppCompatEditText) view.findViewById(R.id.edittext_title);
        Description = (AppCompatEditText) view.findViewById(R.id.edittext_description);
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference("");
        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        save_button = (AppCompatButton) view.findViewById(R.id.save_button);
        if (savedInstanceState == null) {
        }

        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DataModel dataModel = new DataModel();
              //  NoteDatabase db = new NoteDatabase(getActivity());
                dataModel.setId(uid);
                dataModel.setTitle(Title.getText().toString());
                dataModel.setDescription(Description.getText().toString());
                mfirebasedatabase.child("note_details").child(uid).setValue(dataModel);
                /*db.addItem(dataModel);
                todohomeactivity.setBackData(dataModel);*/
                todohomeactivity.fab.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        });
        return view;
    }

}




