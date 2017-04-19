package com.app.todo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
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

public  class Fragmentlistviewdetails extends Fragment implements View.OnClickListener {
    AppCompatEditText fragmentlistview_TitleEditText;
    AppCompatEditText fragmentlistview_DescriptionEditText;
    String id;
    DatabaseReference mfirebasedatabase;
    AppCompatButton Update_button;
    TodoHomeActivity todoHomeActivity;
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
        mfirebasedatabase= FirebaseDatabase.getInstance().getReference();
        Update_button = (AppCompatButton) view.findViewById(R.id.Update_buton);
        Update_button.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String Titledata = (bundle.getString(Constants.Titletext));
            String Descriptiondata = bundle.getString(Constants.Desriptiontext);
            id=bundle.getString("ids");
            fragmentlistview_TitleEditText.setText(Titledata);
            fragmentlistview_DescriptionEditText.setText(Descriptiondata);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Update_buton:
                DataModel dataModel = new DataModel(fragmentlistview_TitleEditText.getText().toString(), fragmentlistview_DescriptionEditText.getText().toString(),id);
                NoteDatabase db = new NoteDatabase(getActivity());
                db.updateItem(dataModel);
                mfirebasedatabase.child(id).setValue(dataModel);
                todoHomeActivity.setBackData(dataModel);
                getActivity().getFragmentManager().popBackStackImmediate();
                break;
        }
    }
}