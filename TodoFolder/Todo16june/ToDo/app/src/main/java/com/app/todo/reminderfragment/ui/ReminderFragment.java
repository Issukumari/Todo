package com.app.todo.reminderfragment.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.notes.ui.OnSearchTextChange;
import com.app.todo.reminderfragment.presenter.ReminderPresenter;
import com.app.todo.reminderfragment.presenter.ReminderPresenterInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReminderFragment extends Fragment implements ReminderFrragmentInterface,OnSearchTextChange, SearchView.OnQueryTextListener  {
    public static final String TAG = "reminderFragment";
    TodoHomeActivity todohomeactivity;
    RecyclerView reminderRecyclerView;
    RecyclerAdapter Reminder_recyclerAdapter;
    ReminderPresenterInterface presenter;
    ProgressDialog progressDialog;
    private String currentDate;
    private Menu menu;
    private SharedPreferences sharedpreferences;
    boolean isGrid = true;
    RecyclerAdapter recyclerAdapter;

    List<TodoHomeDataModel> datamodels = new ArrayList<>();

    public ReminderFragment(TodoHomeActivity todohomeactivity) {
        this.todohomeactivity = todohomeactivity;
        presenter = new ReminderPresenter(todohomeactivity, this);
    }

    public ReminderFragment() {


    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity)getActivity()).setTitle("Reminder");

        }

    private void initView(View view) {
        reminderRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_reminder_list);

        setHasOptionsMenu(true);

        Reminder_recyclerAdapter = new RecyclerAdapter(getActivity(),datamodels,this);
        reminderRecyclerView.setAdapter(Reminder_recyclerAdapter);
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("isGrid", true)) {
            isGrid = true;
            reminderRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = false;
            reminderRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().setTitle("Reminder");
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        initView(view);

        setHasOptionsMenu(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        presenter.getReminderList(uid);

        return view;
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(todohomeactivity);
        if (!todohomeactivity.isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (!todohomeactivity.isFinishing() && progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void getReminderSuccess(List<TodoHomeDataModel> noteList) {
        ArrayList<TodoHomeDataModel> reminderlist = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        currentDate = format.format(new Date().getTime());

        for (TodoHomeDataModel note : noteList) {
            if (!note.isArchieve()) {
                if (!note.isDeleted) {
                    if (note.getReminderDate().equals(currentDate)) {
                        reminderlist.add(note);

                    }
                    datamodels=reminderlist;
                    Reminder_recyclerAdapter.setList(reminderlist);

                }
            }
        }}
    @Override
    public void getReminderFailure(String message) {
        ;
        Toast.makeText(todohomeactivity, message, Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo
                (searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        MenuItem menuItemReminder = menu.findItem(R.id.ic_action_reminder);
        menuItemReminder.setVisible(false);

        MenuItem menuItemColorPick = menu.findItem(R.id.ic_action_color_pick);
        menuItemColorPick.setVisible(false);

        MenuItem menuItemSave = menu.findItem(R.id.ic_action_save);
        menuItemSave.setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();

        List<TodoHomeDataModel> noteList = new ArrayList<>();

        for (TodoHomeDataModel model :
                datamodels) {
            if (model.getTitle().toLowerCase().contains(newText)) {
                noteList.add(model);
            }
        }

        Reminder_recyclerAdapter.setList(noteList);
        return true;
    }


    @Override
    public void OnSearchTextChange(String search) {

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_as_view:
                if (!isGrid) {
                    reminderRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_grid1);
                    isGrid = true;
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putBoolean("isGrid", true);
                    edit.commit();
                } else {
                    reminderRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_action_list);
                    isGrid = false;
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putBoolean("isGrid", false);
                    edit.commit();
                    Toast.makeText(getActivity(), Constants.item_Selectd, Toast.LENGTH_SHORT).show();
                }
                return false;
            case R.id.search:
                Toast.makeText(getActivity(), Constants.item_Selectd, Toast.LENGTH_SHORT).show();
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setcolor(int color) {
        Reminder_recyclerAdapter.setColor(color);

    }

    public void add(TodoHomeDataModel todohomedatamodel) {
        datamodels.add(todohomedatamodel);
        getReminderSuccess(datamodels);

    }
}