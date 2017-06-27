package com.app.todo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.adapter.NotesAdapter;

import com.app.todo.model.DataModel;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {
    NotesAdapter notesAdapter;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<DataModel> datamodels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(layoutManager);
        notesAdapter = new NotesAdapter(this,datamodels);
        recyclerView.setAdapter(notesAdapter);
       // notesAdapter.notifyDataSetChanged();
        return view;
    }
}
