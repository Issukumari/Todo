package com.example.bridgeit.card;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public FloatingActionButton fabbutton;
    private RecyclerView recyclerViewnotes;
    CardView cardview;
    private TodoNoteAddActivity todoNoteaddActivity;
    RecyclerAdapter recyclerAdapter;
    private ArrayList arrlist=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentnotes);
        fabbutton = (FloatingActionButton) findViewById(R.id.fabbutton);
        //cardview = (CardView) findViewById(R.id.card_view);
        recyclerViewnotes = (RecyclerView) findViewById(R.id.notesrecyclerview);
        arrlist.add(1);
        arrlist.add(2);
        arrlist.add(3);
        arrlist.add(4);
        arrlist.add(5);

        recyclerAdapter = new RecyclerAdapter(this,arrlist);
        recyclerViewnotes.setLayoutManager(new GridLayoutManager(this,2));

        recyclerViewnotes.setAdapter(recyclerAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabbutton:
                todoNoteaddActivity = new TodoNoteAddActivity();
                Intent intent = new Intent(this, TodoNoteAddActivity.class);
                startActivity(intent);
        }
    }

}
