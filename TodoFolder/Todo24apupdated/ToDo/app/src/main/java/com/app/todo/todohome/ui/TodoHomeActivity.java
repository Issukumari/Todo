package com.app.todo.todohome.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.app.todo.fragment.TodoNoteaddFragment;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public FloatingActionButton fab;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    DrawerLayout drawer;
    NoteDatabase databse;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    FirebaseAuth auth;
    DatabaseReference mfirebasedatabaseref;
    FirebaseDatabase mfirebaseDatabase;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        //  NoteDatabase db = new NoteDatabase(this);
        //  datamodels = db.getNote();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mfirebasedatabaseref = mfirebaseDatabase.getReference().child("note_details");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait....");
        progressDialog.show();

        mfirebasedatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<TodoHomeDataModel>  datamodels=new ArrayList<TodoHomeDataModel>();
                GenericTypeIndicator<ArrayList<TodoHomeDataModel>> t = new GenericTypeIndicator<ArrayList<TodoHomeDataModel>>() {
                };
                for (DataSnapshot post : dataSnapshot.child("Pu2jGDmu8idRoHUPDfeKacu6r5m2").getChildren()) {
                    ArrayList<TodoHomeDataModel> data=new ArrayList<TodoHomeDataModel>();
                    data = post.getValue(t);
                    datamodels.addAll(data);
                }
                recyclerAdapter = new RecyclerAdapter(TodoHomeActivity.this, datamodels);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        initView();
    }

    @Override
    public void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance().getInstance();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open_navigation_drawer, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
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

    public void setData(TodoHomeDataModel mode) {
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

    public void setBackData(TodoHomeDataModel model) {
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
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager( 2,StaggeredGridLayoutManager.VERTICAL));
                    isGrid = true;
                } else {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager( 1,StaggeredGridLayoutManager.VERTICAL));
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
        ArrayList<TodoHomeDataModel> temp = new ArrayList<>();
        if (itemId == R.id.nav_Notes) {
            temp = getData(true);
            recyclerAdapter = new RecyclerAdapter(this, temp);
            recyclerView.setAdapter(recyclerAdapter);
            toolbar.setTitle("Notes");
            fab.setVisibility(View.INVISIBLE);
            drawer.closeDrawers();
        } else if (itemId == R.id.nav_Reminders) {
            temp = getData(false);
            recyclerAdapter = new RecyclerAdapter(this, temp);
            recyclerView.setAdapter(recyclerAdapter);
            toolbar.setTitle("Reminder");
            drawer.closeDrawers();
        } else if (itemId == R.id.Logout) {
            auth.signOut();
            finish();
            Intent intent = new Intent(TodoHomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        return false;
    }

    private ArrayList<TodoHomeDataModel> getData(boolean i) {

        ArrayList<TodoHomeDataModel> temp = new ArrayList<>();
        if (i) {
            for (TodoHomeDataModel str : datamodels) {
                if (!str.equals("")) {
                    temp.add(str);
                }

            }
        } else {
            for (TodoHomeDataModel str : datamodels) {
                if (str.equals("")) {
                    temp.add(str);
                }

            }
        }

        return temp;
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}