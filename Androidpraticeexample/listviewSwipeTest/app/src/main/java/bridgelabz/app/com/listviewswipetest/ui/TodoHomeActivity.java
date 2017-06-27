package bridgelabz.app.com.listviewswipetest.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bridgelabz.app.com.listviewswipetest.database.DatabaseActivity;
import bridgelabz.app.com.listviewswipetest.Fragment.TodoNoteaddFragment;
import bridgelabz.app.com.listviewswipetest.R;
import bridgelabz.app.com.listviewswipetest.adapter.RecyclerAdapter;
import bridgelabz.app.com.listviewswipetest.base.BaseActivity;
import bridgelabz.app.com.listviewswipetest.model.DataModel;

public class TodoHomeActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static int navItemIndex = 0;
    RecyclerView.LayoutManager layoutManager;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;
    List<DataModel> datamodels = new ArrayList<>();
    FloatingActionButton fab;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    private RecyclerView recyclerView;
    private int edit_position;
    private boolean add = false;
    private AppCompatEditText discription;
    private AppCompatEditText notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        DatabaseActivity db=new DatabaseActivity(this);
        datamodels= db.getNote();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            initView();
           // List<DataModel> data=new ArrayList<>();
        }
    }

    @Override
    public void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
        recyclerAdapter = new RecyclerAdapter(this, datamodels);
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
                    recyclerAdapter.removeItem(position);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
        public  void setData(DataModel mode){
            recyclerAdapter.addItem(mode);
        }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                TodoNoteaddFragment f3 = new TodoNoteaddFragment(this);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, f3).addToBackStack(null).commit();
                /*Intent intent = new Intent(TodoHomeActivity.this, TodoNoteAddActivity.class);
                startActivityForResult(intent, 2);*/
                break;
        }
    }
public void setBackData(DataModel model)
{
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
            recyclerAdapter = new RecyclerAdapter(this, temp);
            recyclerView.setAdapter(recyclerAdapter);
            toolbar.setTitle("Notes");
        } else if (itemId == R.id.nav_Reminders) {
            temp = getData(false);
            recyclerAdapter = new RecyclerAdapter(this, temp);
            recyclerView.setAdapter(recyclerAdapter);
            toolbar.setTitle("Reminder");
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


}