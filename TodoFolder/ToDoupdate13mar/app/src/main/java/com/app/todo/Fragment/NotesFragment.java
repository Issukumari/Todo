package com.app.todo.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.model.DataModel;
import com.app.todo.ui.TodoHomeActivity;

import java.util.ArrayList;

public class NotesFragment extends Fragment {
    RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;

    //TodoHomeActivity todoHomeActivity;
   /* public NotesFragment(TodoHomeActivity todoHomeActivity)
    {
        this.todoHomeActivity=todoHomeActivity;
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // todoHomeActivity.fab.setVisibility(View.VISIBLE);
       /* ArrayList<DataModel> dataModels = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(this,dataModels);*/
        recyclerView.setAdapter(recyclerAdapter);
        View view= inflater.inflate(R.layout.recycler_view, container, false);
        return view;
    }
}
