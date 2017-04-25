package com.app.todo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.app.todo.R;


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
