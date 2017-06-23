package com.app.todo.notes.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.database.NoteDatabase;
import com.app.todo.notes.presenter.NotesPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddactivity.ui.TodoNoteaddActivity;
import com.app.todo.todonoteaddfragment.ui.TodoNoteaddFragment;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotesFragment extends Fragment implements NotesFragmentInterface, OnSearchTextChange, View.OnClickListener, SearchView.OnQueryTextListener {

    public static final String TAG = "NotesFragment";
    RecyclerView recyclerViewnotes;
    RecyclerAdapter recyclerAdapter;
    boolean isGrid = true;
    ItemTouchHelper itemTouchHelper;
    TodoHomeDataModel todohomedatamodel;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    List<TodoHomeDataModel> allnotes = new ArrayList<>();
    NotesPresenter presenter;
    String uId;
    TodoNoteaddFragment todoNoteaddFragment;
    TodoNoteaddActivity todoNoteaddActivity;
    ProgressDialog progressDialog;
    DatabaseReference mfirebasedatabaseref;
    NoteDatabase database;
    View view;
    private FloatingActionButton fabbutton;
    private Menu menu;
    private SharedPreferences sharedpreferences;

    private CardView cardview;
    int from = -1, to = -1;

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Notes");

        if (menu != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentnotes, container, false);
        database = new NoteDatabase(getActivity());
        presenter = new NotesPresenter(getActivity(), this);
        fabbutton = (FloatingActionButton) view.findViewById(R.id.fabbutton);
        cardview = (CardView) view.findViewById(R.id.card_view);
        recyclerViewnotes = (RecyclerView) view.findViewById(R.id.notesrecyclerview);
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
        initSwipe();
        ((TodoHomeActivity) getActivity()).setSearchTagListener(this);
        fabbutton.setOnClickListener(this);
        return view;
    }

    public void initSwipe() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP
                | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                from = viewHolder.getAdapterPosition();
                to = target.getAdapterPosition();
                recyclerAdapter.onTodoNoteMove(from, to);
                datamodels = recyclerAdapter.getallnotesdata();
                return true;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (from != -1 && to != -1) {
                    updateSrNo();
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mfirebasedatabaseref = FirebaseDatabase.getInstance().getReference();
                if (direction == ItemTouchHelper.LEFT) {
                    todohomedatamodel = datamodels.get(position);
                    todohomedatamodel.setDeleted(true);
                    mfirebasedatabaseref.child("note_details").child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(todohomedatamodel.getId())).setValue(todohomedatamodel);
                    recyclerAdapter.deleteditem(position);
                    recyclerViewnotes.setAdapter(recyclerAdapter);
                    Snackbar snackbar = Snackbar
                            .make(getActivity().getCurrentFocus(), getString(R.string.item_deleted), Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
                if (direction == ItemTouchHelper.RIGHT) {

                    todohomedatamodel = datamodels.get(position);
                    todohomedatamodel.setArchieve(true);
                    mfirebasedatabaseref.child("note_details").child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(todohomedatamodel.getId())).setValue(todohomedatamodel);
                    recyclerAdapter.archieveitem(position);
                    recyclerViewnotes.setAdapter(recyclerAdapter);
                    Snackbar snackbar = Snackbar
                            .make(getActivity().getCurrentFocus(), getString(R.string.note_has_been_Archieved), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.UNDO), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    todohomedatamodel.setArchieve(false);
                                    mfirebasedatabaseref.child("note_details").child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(todohomedatamodel.getId())).setValue(todohomedatamodel);

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

        itemTouchHelper.attachToRecyclerView(recyclerViewnotes);
    }

    private void updateSrNo() {
        for (TodoHomeDataModel datamodel :
                datamodels) {
            mfirebasedatabaseref.child(uId).child(datamodel.getStartdate())
                    .child(String.valueOf(datamodel.getId())).child("srNo")
                    .setValue(datamodels.indexOf(datamodel));
        }
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
        Collections.sort(todoHomeDataModel, new Comparator<TodoHomeDataModel>() {
            @Override
            public int compare(TodoHomeDataModel t1, TodoHomeDataModel t2) {
                if (t1.getSrNo() > t2.getSrNo())
                    return 1;
                if (t1.getSrNo() < t2.getSrNo())
                    return -1;
                return 0;
            }
        });

        datamodels = todoHomeDataModel;
        recyclerAdapter = new RecyclerAdapter(getActivity(), datamodels, this);
        recyclerViewnotes.setAdapter(recyclerAdapter);

    }


    @Override
    public void getNotesListFailure(String message) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabbutton:
                todoNoteaddActivity = new TodoNoteaddActivity();
                Intent intent = new Intent(getActivity(), TodoNoteaddActivity.class);
                getActivity().startActivityForResult(intent, 2);
        }
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

    public void setcolor(int color) {
        if (todoNoteaddFragment != null) {
            todoNoteaddFragment.setcolor(color);
        } else {
            recyclerAdapter.setColor(color);
        }
    }

    public void fade() {
        Animation animation1 =
                AnimationUtils.loadAnimation(getActivity(),
                        R.anim.fade);
        cardview.startAnimation(animation1);
    }

    public void add(TodoHomeDataModel todohomedatamodel) {
        allnotes.add(todohomedatamodel);
        getNoteListSuccess(allnotes);
    }


}

