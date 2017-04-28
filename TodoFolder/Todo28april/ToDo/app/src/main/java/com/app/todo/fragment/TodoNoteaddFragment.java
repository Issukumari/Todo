package com.app.todo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TodoNoteaddFragment extends Fragment {
    AppCompatEditText Title;
    AppCompatEditText Description;
    AppCompatEditText id;
    AppCompatButton save_button;
    TodoHomeActivity todohomeactivity;
    DatabaseReference mfirebasedatabase;
    SharedPreferences sharedPreferences;
    NoteDatabase database;
    TodoHomeDataModel todoHomeDataModel;
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
        mfirebasedatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getActivity().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", "null");
        save_button = (AppCompatButton) view.findViewById(R.id.save_button);
        if (savedInstanceState == null) {
        }

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database =new NoteDatabase(getActivity());
                todoHomeDataModel=new TodoHomeDataModel();
                todoHomeDataModel.setId(0);//setId(0)
                todoHomeDataModel.setTitle(Title.getText().toString());
                todoHomeDataModel.setDescription(Description.getText().toString());
                database.addItem(todoHomeDataModel);
                getIndex(todoHomeDataModel);
                /*TodoHomeDataModel dataModel = new TodoHomeDataModel();
                //  NoteDatabase db = new NoteDatabase(getActivity());
                dataModel.setId(uid);
                dataModel.setTitle(Title.getText().toString());
                dataModel.setDescription(Description.getText().toString());
                mfirebasedatabase.child("note_details").child(uid).setValue(dataModel);
                *//*db.addItem(dataModel);
                todohomeactivity.setBackData(dataModel);*//*
                todohomeactivity.fab.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().popBackStackImmediate();*/


            }


        });
        return view;
    }

    private void getIndex(final TodoHomeDataModel todoHomeDataModel) {
        todoHomeDataMode=todoHomeDataModel;
        final boolean[] flag={true};
        mfirebasedatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(todoHomeDataMode!=null){
                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                    String currentDate = format.format(new Date().getTime());
                    if (dataSnapshot.child("note_details").child(uid).child(currentDate).exists()){

                        int index =(int)dataSnapshot.child("note_details").child(uid).child(currentDate).getChildrenCount();
                        putdata(index,todoHomeDataModel);
                        todoHomeDataMode=null;
                    }
                    else {
                        putdata(0,todoHomeDataMode);
                        todoHomeDataMode=null;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void putdata(int index, TodoHomeDataModel todoHomeDataModel) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String currentDate = format.format(new Date().getTime());
        todoHomeDataModel.setId(index);
        mfirebasedatabase.child("note_details").child(uid).child(currentDate).child(String.valueOf(index)).setValue(todoHomeDataModel);
        getFragmentManager().popBackStack();

    }

}


