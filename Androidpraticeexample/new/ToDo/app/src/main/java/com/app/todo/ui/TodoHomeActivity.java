package com.app.todo.ui;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.base.BaseActivity;
import java.util.ArrayList;
import java.util.List;

public class TodoHomeActivity extends BaseActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    boolean isrecyclerview = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);
        initView();
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        List<String> stringsList = new ArrayList<>();
        stringsList.add("Android Development plans");
        stringsList.add("basic Assignment  ");
        stringsList.add("Pinup");
        stringsList.add("Jayesh CA followUp");
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(this, stringsList);
        recyclerView.setAdapter(recyclerAdapter);

    }
    @Override
    public void initView() {
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        setListeners();
    }
    @Override
    public void setListeners() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_as_view:
                if(!isrecyclerview){
                  recyclerView.setLayoutManager(new GridLayoutManager(this,1));
                    isrecyclerview = true;
                }
                else{
                    recyclerView.setLayoutManager(new GridLayoutManager(this,2));
                    isrecyclerview = false;
                }
                Toast.makeText(this, "item Selectd", Toast.LENGTH_SHORT).show();
              return true;
            case R.id.action_settings:
                Toast.makeText(this, "item Selectd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_skip:
                Toast.makeText(this, "item Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){


        }

    }
}
