package com.app.todo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.base.BaseActivity;
import com.app.todo.database.NoteDatabase;
import com.app.todo.fragment.Fragmentlistviewdetails;
import com.app.todo.fragment.TodoNoteaddFragment;
import com.app.todo.model.DataModel;

import java.util.ArrayList;
import java.util.List;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, CallBackItemInterface {

    public FloatingActionButton fab;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    List<DataModel> datamodels = new ArrayList<>();
    DrawerLayout drawer;
    NoteDatabase databse;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        NoteDatabase db = new NoteDatabase(this);
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
        recyclerAdapter = new RecyclerAdapter(this, datamodels, this);
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
                    databse = new NoteDatabase(getApplicationContext());
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
        int itemId = item.getItemId();
        ArrayList<DataModel> temp = new ArrayList<>();
        if (itemId == R.id.nav_Notes) {
            temp = getData(true);
            recyclerAdapter = new RecyclerAdapter(this, temp, this);
            recyclerView.setAdapter(recyclerAdapter);
            toolbar.setTitle("Notes");
        } else if (itemId == R.id.nav_Reminders) {
            temp = getData(false);
            recyclerAdapter = new RecyclerAdapter(this, temp, this);
            recyclerView.setAdapter(recyclerAdapter);
            toolbar.setTitle("Reminder");
        } else if (itemId == R.id.Logout) {
            Intent intent = new Intent(TodoHomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        return false;
    }

    private ArrayList<DataModel> getData(boolean i) {

        ArrayList<DataModel> temp = new ArrayList<>();
        if (i) {
            for (DataModel str : datamodels) {
                if (!str.equals("")) {
                    temp.add(str);
                }

            }
        } else {
            for (DataModel str : datamodels) {
                if (str.equals("")) {
                    temp.add(str);
                }

            }
        }

        return temp;
    }

    @Override
    public void onItemClick(DataModel dataModel) {
        Fragmentlistviewdetails fragmentlistview = new Fragmentlistviewdetails(this);
        Bundle bundle = new Bundle();
        bundle.putString("Titletext", dataModel.getTitle());
        bundle.putString("Desriptiontext", dataModel.getDescription());
        fragmentlistview.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.frame, fragmentlistview).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
