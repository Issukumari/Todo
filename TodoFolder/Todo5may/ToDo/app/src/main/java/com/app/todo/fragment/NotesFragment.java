package com.app.todo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.adapter.NotesAdapter;

import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment
{
    public static final String TAG = "NotesFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TodoHomeActivity)getActivity()).setTitle("Notes");
    }
}
