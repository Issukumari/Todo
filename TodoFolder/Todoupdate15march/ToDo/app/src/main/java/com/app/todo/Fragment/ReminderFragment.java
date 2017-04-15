package com.app.todo.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.app.todo.R;
import com.app.todo.model.DataModel;
import com.app.todo.ui.TodoHomeActivity;

import java.util.ArrayList;
import java.util.List;


public class ReminderFragment extends Fragment {
    DatePicker datePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
      //  datePicker = (DatePicker)view.findViewById(R.id.datePicker);

        return view;
    }
}
