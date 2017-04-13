package com.app.todo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.Database.DatabaseActivity;
import com.app.todo.Fragment.Fragmentlistviewdetails;
import com.app.todo.Fragment.NotesFragment;
import com.app.todo.Fragment.ReminderFragment;
import com.app.todo.Fragment.TodoNoteaddFragment;
import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.base.BaseActivity;
import com.app.todo.model.DataModel;

import java.util.ArrayList;
import java.util.List;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,CallBackitem {

    public FloatingActionButton fab;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    List<DataModel> datamodels = new ArrayList<>();
    DrawerLayout drawer;
    DatabaseActivity databse;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        DatabaseActivity db = new DatabaseActivity(this);
        datamodels = db.getNote();
        initView();
    }

    @Override
    public void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open_navigation_drawer, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(this, datamodels,this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        initSwipe();
        setListeners();
    }

    @Override
    public void setListeners() {
        fab.setOnClickListener(this);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    databse = new DatabaseActivity(getApplicationContext());
                    databse.removeItem(datamodels.get(position));
                    recyclerAdapter.removeItem(position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void setData(DataModel mode) {
        recyclerAdapter.addItem(mode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                TodoNoteaddFragment todoNoteaddFragment = new TodoNoteaddFragment(this);
                fab.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, todoNoteaddFragment).addToBackStack(null).commit();

                break;
        }
    }

    public void setBackData(DataModel model) {
        recyclerAdapter.addItem(model);
        recyclerView.setAdapter(recyclerAdapter);
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
                if (!isGrid) {
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                    isGrid = true;
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
                    isGrid = false;
                }
                Toast.makeText(this, "item Selectd", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Notes:
                toolbar.setTitle("Notes");
                NotesFragment notesFragment = new NotesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, notesFragment).addToBackStack(null).commit();
                break;
            case R.id.nav_Reminders:
                toolbar.setTitle("Reminder");
                ReminderFragment reminderFragment = new ReminderFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, reminderFragment).addToBackStack(null).commit();
                break;


        }
        return true;


    }

    @Override
    public void onItemClick(DataModel dataModel) {
        Log.i("sm", "onItemClick: ");
            Fragmentlistviewdetails f1 = new Fragmentlistviewdetails(this);
            Bundle bundle = new Bundle();
            bundle.putString("Titletext", dataModel.getTitle());
            bundle.putString("Desriptiontext", dataModel.getDescription());
            f1.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.frame, f1).addToBackStack(null).commit();
        }
    }
