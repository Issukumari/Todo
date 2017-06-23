package com.app.todo.sharefragment.ui;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.database.NoteDatabase;
import com.app.todo.notes.ui.OnSearchTextChange;
import com.app.todo.sharefragment.presenter.ShareFragmentPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddactivity.ui.TodoNoteaddActivity;
import com.app.todo.todonoteaddfragment.ui.TodoNoteaddFragment;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShareFragment extends Fragment implements ShareFragmentInterface, OnSearchTextChange, SearchView.OnQueryTextListener {
    RecyclerView recyclerViewnotes;
    RecyclerAdapter recyclerAdapter;
    boolean isGrid = true;
    ItemTouchHelper itemTouchHelper;
    TodoHomeDataModel todohomedatamodel;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    List<TodoHomeDataModel> allnotes = new ArrayList<>();
    ShareFragmentPresenter presenter;
    String uId;
    TodoNoteaddFragment todoNoteaddFragment;
    TodoNoteaddActivity todoNoteaddActivity;
    ProgressDialog progressDialog;
    DatabaseReference mfirebasedatabaseref;
    NoteDatabase database;
    View view;
    TodoHomeActivity todoHomeActivity;
    public static final String TAG = "sharefragment";

    private FloatingActionButton fabbutton;
    private Menu menu;
    private SharedPreferences sharedpreferences;
    ShareFragmentPresenter sharefragmentpresenter;
    private CardView cardview;
    private ShareFragment SharingNotes;

    public ShareFragment(TodoHomeActivity todoMainActivity) {
        this.todoHomeActivity = todoMainActivity;
        sharefragmentpresenter = new ShareFragmentPresenter(todoMainActivity, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Share");

        if (menu != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sharefragment, container, false);
        database = new NoteDatabase(getActivity());
        presenter = new ShareFragmentPresenter(getActivity(), this);
        //  fabbutton = (FloatingActionButton) view.findViewById(R.id.fabbutton);
        cardview = (CardView) view.findViewById(R.id.card_view);
        recyclerViewnotes = (RecyclerView) view.findViewById(R.id.shareFragment_recyclerView);
        mfirebasedatabaseref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.note_details));
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getNoteList(uId);
        setHasOptionsMenu(true);

        //  fade();
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("isGrid", true)) {
            isGrid = true;
            recyclerViewnotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = false;
            recyclerViewnotes.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }
        ((TodoHomeActivity) getActivity()).setSearchTagListener(this);
        //fabbutton.setOnClickListener(this);
        return view;
    }


    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        if (isAdded()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void getNoteListSuccess(List<TodoHomeDataModel> modelList) {
        allnotes.clear();
        allnotes = modelList;
        ArrayList<TodoHomeDataModel> todoHomeDataModel = new ArrayList<>();
        for (TodoHomeDataModel note : allnotes) {
            if (!note.isArchieve()) {
                if (!note.isDeleted) {
                    todoHomeDataModel.add(note);
                }
            }
        }
        datamodels = todoHomeDataModel;
        recyclerAdapter = new RecyclerAdapter(getActivity(), datamodels, this);
        recyclerViewnotes.setAdapter(recyclerAdapter);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

        MenuItem menuItemSearch = menu.findItem(R.id.ic_action_save);
        menuItemSearch.setVisible(false);

        MenuItem menuItemListToGrid = menu.findItem(R.id.ic_action_reminder);
        menuItemListToGrid.setVisible(false);
        MenuItem menuItempaint = menu.findItem(R.id.ic_action_color_pick);
        menuItempaint.setVisible(false);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo
                (searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_as_view:
                if (!isGrid) {
                    recyclerViewnotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_grid1);
                    isGrid = true;
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putBoolean("isGrid", true);
                    edit.commit();
                } else {
                    recyclerViewnotes.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_action_list);
                    isGrid = false;
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putBoolean("isGrid", false);
                    edit.commit();
                    //  Toast.makeText(getActivity(), Constants.item_Selectd, Toast.LENGTH_SHORT).show();
                }
                return false;
            case R.id.search:
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        recyclerAdapter.setFilter(noteList);
        return true;
    }

    @Override
    public void OnSearchTextChange(String search) {

    }

    @Override
    public void getNotesListFailure(String message) {

    }

    public void sharenotes(int adapterPosition) {
        TodoHomeDataModel todohomedatamodel = datamodels.get(adapterPosition);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getString(R.string.Title) + todohomedatamodel.getTitle()
                + getActivity().getString(R.string.Description) + todohomedatamodel.getDescription());
        sendIntent.setType("text/plain");
        getActivity().startActivity(sendIntent);

    }
}

