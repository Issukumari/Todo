package com.app.todo.todohome.ui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.archievefragment.ui.ArchieveFragment;
import com.app.todo.base.BaseActivity;
import com.app.todo.database.NoteDatabase;
import com.app.todo.login.ui.LoginActivity;
import com.app.todo.reminderfragment.ui.ReminderFragment;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.presenter.TodoHomepresenter;
import com.app.todo.todonoteaddfragment.ui.TodoNoteaddFragment;
import com.app.todo.utils.Constants;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, TodoHomeActivityInterface {

    public FloatingActionButton fab;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();

    DrawerLayout drawer;
    NoteDatabase databse;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    AppCompatTextView Navigationheaderid;
    AppCompatTextView Navigationheadername;
    View hView;
    FirebaseAuth auth;
    CircleImageView circleimageview;
    DatabaseReference mfirebasedatabaseref;
    FirebaseDatabase mfirebaseDatabase;
    NavigationView navigationView;
    ProgressDialog progressDialog;
    ItemTouchHelper itemTouchHelper;
    private String userId;
    private RecyclerView recyclerView;
    private String google_first_name, google_email, google_imageUrl;
    private SharedPreferences sharedPreferences;
    private TodoHomeDataModel todoHomeDataModel;
    private List<TodoHomeDataModel> allnotes;
    private TodoHomepresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        //  NoteDatabase db = new NoteDatabase(this);
        //  datamodels = db.getNote();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mfirebasedatabaseref = mfirebaseDatabase.getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait....");
        progressDialog.show();
        sharedPreferences = this.getSharedPreferences(Constants.keys, MODE_PRIVATE);
        presenter = new TodoHomepresenter(this, this);
        presenter.getTodoNote(userId);
        initView();
        if (sharedPreferences.getBoolean(Constants.fbloginkeys, false)) {
            fbuser();
        } else if (sharedPreferences.getBoolean(Constants.googleloginkeys, false)) {
            googleuser();
        }

    }

   /* private void onLongClick(final TodoHomeDataModel todohomedatamodel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(todohomeactivity);
        builder.setTitle(TodoHomeActivity.getString(R.string.moving_to_note));
        builder.setMessage(TodoHomeActivity.getString(R.string.ask_move_to_note_message));
        builder.setPositiveButton(TodoHomeActivity.getString(R.string.ok_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       moveToNotes(todohomedatamodel);
                    }
                });

        builder.setNegativeButton(TodoHomeActivity.getString(R.string.cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(TodoHomeActivity
                                , TodoHomeActivity.getString(R.string.cancel_message)
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

    private void fbuser() {
        String name = sharedPreferences.getString(getString(R.string.name), "");
        String email = sharedPreferences.getString(getString(R.string.email), "");
        String imageUrl = "http://graph.facebook.com/" + sharedPreferences.getString("id", "") + "/picture?type=large";
        Navigationheadername.setText(name);
        Navigationheaderid.setText(email);
        Glide.with(this).load(imageUrl).into(circleimageview);
    }

    private void googleuser() {
        google_first_name = sharedPreferences.getString(Constants.Name, Constants.values);
        google_email = sharedPreferences.getString(Constants.Email, Constants.values);
        google_imageUrl = sharedPreferences.getString("Pic", "");
        Navigationheadername.setText(google_first_name);
        Navigationheaderid.setText(google_email);
        Glide.with(getApplicationContext()).load(google_imageUrl).into(circleimageview);
    }

    @Override
    public void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance().getInstance();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        circleimageview = (CircleImageView) hView.findViewById(R.id.imagenavigation);
        Navigationheadername = (AppCompatTextView) hView.findViewById(R.id.name);


        Navigationheaderid = (AppCompatTextView) hView.findViewById(R.id.Navigationheaderid);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.Open_navigation_drawer, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(TodoHomeActivity.this, datamodels);
        recyclerView.setAdapter(recyclerAdapter);
        initSwipe();
        setListeners();
        setNotesToRecycler(datamodels);
    }

    @Override
    public void setListeners() {
        fab.setOnClickListener(this);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mfirebasedatabaseref = FirebaseDatabase.getInstance().getReference();
                if (direction == ItemTouchHelper.LEFT) {
                    userId = auth.getCurrentUser().getUid();
                    databse = new NoteDatabase(getApplicationContext());
                    todoHomeDataModel = datamodels.get(position);
                    mfirebasedatabaseref.child("note_details").child(userId).child(todoHomeDataModel.getStartdate())
                            .child(String.valueOf(todoHomeDataModel.getId())).removeValue();
                    databse.removeItem(datamodels.get(position));

                }
                if (direction == ItemTouchHelper.RIGHT) {
                    todoHomeDataModel = allnotes.get(position);
                    userId = auth.getCurrentUser().getUid();
                    todoHomeDataModel.setArchieve(true);

                    mfirebasedatabaseref.child("note_details").child(userId).child(todoHomeDataModel.getStartdate())
                            .child(String.valueOf(todoHomeDataModel.getId())).setValue(todoHomeDataModel);

                    Snackbar snackbar = Snackbar
                            .make(getCurrentFocus(), getString(R.string.note_has_been_Archieved), Snackbar.LENGTH_LONG)
                            .setAction(R.string.UNDO, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    todoHomeDataModel.setArchieve(false);
                                    mfirebasedatabaseref.child("note_details").child(userId).child(todoHomeDataModel.getStartdate())
                                            .child(String.valueOf(todoHomeDataModel.getId())).setValue(todoHomeDataModel);
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                }
            }

        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    ;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();

                ArrayList<TodoHomeDataModel> searchList = new ArrayList<>();

                for (TodoHomeDataModel model :
                        allnotes) {
                    if (model.getTitle().toLowerCase().contains(newText)) {
                        searchList.add(model);
                    }
                }

                recyclerAdapter.setFilter(searchList);

                return true;
            }
        };
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(onQueryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_as_view:
                if (!isGrid) {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    isGrid = true;
                } else {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    isGrid = false;
                    Toast.makeText(this, Constants.item_Selectd, Toast.LENGTH_SHORT).show();
                }
                return false;

            case R.id.search:
                Toast.makeText(this, Constants.item_Selectd, Toast.LENGTH_SHORT).show();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Notes:
                getSupportFragmentManager().popBackStackImmediate();
                setTitle("Notes");
                fab.setVisibility(View.VISIBLE);
                itemTouchHelper.attachToRecyclerView(recyclerView);
                Toast.makeText(TodoHomeActivity.this, "notes  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

            case R.id.Logout:
                deleteAccessToken();
                break;

            case R.id.nav_Reminders:
                getSupportFragmentManager().popBackStackImmediate();
                setTitle("Reminder");
                fab.setVisibility(View.INVISIBLE);

                ReminderFragment reminderFragment = new ReminderFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, reminderFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "reminder", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;
            case R.id.nav_Archive:

                getSupportFragmentManager().popBackStackImmediate();
                setTitle("Archive");
                fab.setVisibility(View.INVISIBLE);

                ArchieveFragment archieveFragment = new ArchieveFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, archieveFragment).
                        addToBackStack(null)
                        .commit();
                Toast.makeText(TodoHomeActivity.this, "Archive  fragment", Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                break;

        }

        return true;
    }

    private void checkLayout() {
        if (isGrid) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }
    }

    private void moveToAgainNotes(TodoHomeDataModel todohomedatamodel) {
        presenter.moveToNotes(todohomedatamodel);
    }

    private void deleteAccessToken() {
        auth.signOut();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putBoolean(Constants.fbloginkeys, false);
        editor.putBoolean(Constants.googleloginkeys, false);
        editor.apply();
        Intent intent = new Intent(TodoHomeActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setNotesToRecycler(List<TodoHomeDataModel> todoHomeDataModel) {
        datamodels = todoHomeDataModel;
        allnotes = getWithoutArchive();

        recyclerAdapter = new RecyclerAdapter(TodoHomeActivity.this, allnotes);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();

     checkLayout();
    }

    private List<TodoHomeDataModel> getWithoutArchive() {
        ArrayList<TodoHomeDataModel> todoHomeDataModel = new ArrayList<>();
        for (TodoHomeDataModel note : datamodels) {
            if (!note.isArchieve()) {
                todoHomeDataModel.add(note);
            }
        }
        return todoHomeDataModel;
    }


    @Override
    public void getNoteFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showProgressDialog(String message) {
        if (!isFinishing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (!isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void deleteTodoHomeModelaFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void deleteTodoHomeaModelSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void moveToArchieveFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void moveToArchieveSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLongClick(TodoHomeDataModel datmodels) {

    }

    @Override
    public void getNotesListSuccess(List<TodoHomeDataModel> modelList) {
        allnotes = modelList;
        ArrayList<TodoHomeDataModel> todoHomeDataModel = new ArrayList<>();
        for (TodoHomeDataModel note : allnotes) {
            if (!note.isArchieve()) {
                todoHomeDataModel.add(note);
                datamodels.add(note);
            }
        }
        recyclerAdapter = new RecyclerAdapter(TodoHomeActivity.this, todoHomeDataModel);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
    }

}


