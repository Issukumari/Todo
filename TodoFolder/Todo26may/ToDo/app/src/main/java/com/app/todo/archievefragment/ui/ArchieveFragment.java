package com.app.todo.archievefragment.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.archievefragment.presenter.ArchievePresenter;
import com.app.todo.archievefragment.presenter.ArchievepresenterInterface;
import com.app.todo.notes.ui.OnSearchTextChange;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ArchieveFragment extends Fragment implements ArchieveFragmentInterface, OnSearchTextChange, SearchView.OnQueryTextListener {
    public static final String TAG = "ArchieveFragment";
    RecyclerView archievedRecyclerView;
    RecyclerAdapter recyclerAdapter;
    boolean isGrid = true;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    String uId;
    LinearLayout linearLayout;
    DatabaseReference databaseReference;
    ArrayList<TodoHomeDataModel> deleteNoteList = new ArrayList<>();
    List<TodoHomeDataModel> allnotes = new ArrayList<>();
    TodoHomeActivity todoHomeActivity;
    TodoHomeDataModel todohomedatamodel;
    ArchievepresenterInterface presenter;
    Menu menu;
    ProgressDialog progressDialog;
    private SharedPreferences sharedpreferences;
    private int pos;
    private String startdate;
    private int index;
    private Snackbar snackbar;

    public ArchieveFragment(TodoHomeActivity todoHomeActivity) {
        this.todoHomeActivity = todoHomeActivity;
        presenter = new ArchievePresenter(todoHomeActivity, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Archieve");
    }

    private void initView(View view) {
        archievedRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_archieved_list);

        setHasOptionsMenu(true);

        recyclerAdapter = new RecyclerAdapter(getActivity());
        archievedRecyclerView.setAdapter(recyclerAdapter);
        setHasOptionsMenu(true);

        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("isGrid", true)) {
            isGrid = true;
            archievedRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = false;
            archievedRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_archieve, container, false);
        initView(view);
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.note_details);
        initSwipe();

        setHasOptionsMenu(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getNoteList(uid);
        ((TodoHomeActivity) getActivity()).setSearchTagListener(this);
        //  initSwipe();

        return view;
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    try {
                        todohomedatamodel = deleteNoteList.get(position);
                        getIndexUpdateNotes(todohomedatamodel, allnotes);
                        snackbar = Snackbar
                                .make(linearLayout, getString(R.string.item_deleted), Snackbar.LENGTH_LONG);
                        snackbar.show();

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    try {
                        todohomedatamodel = deleteNoteList.get(position);

                        databaseReference.child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(todohomedatamodel.getId())).child("isDeleted").setValue(false);
                        Snackbar snackbar = Snackbar
                                .make(getActivity().getCurrentFocus(), getString(R.string.note_has_been_Archieved), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.UNDO), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        todohomedatamodel.setDeleted(true);
                                        databaseReference.child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(todohomedatamodel.getId())).child("isDeleted").setValue(true);

                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(archievedRecyclerView);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_as_view:
                if (!isGrid) {
                    archievedRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_action_grid);
                    isGrid = true;
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putBoolean("isGrid", true);
                    edit.commit();

                } else {
                    archievedRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
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


    @Override
    public void onLongClick(TodoHomeDataModel todoHomeDataModel) {

    }

    @Override
    public void getNoteListSuccess(List<TodoHomeDataModel> noteList) {
        ArrayList<TodoHomeDataModel> archievedList = new ArrayList<>();
        for (TodoHomeDataModel todoHomeDataModel : noteList) {
            if (todoHomeDataModel.isArchieve()) {
                archievedList.add(todoHomeDataModel);
            }
        }
        recyclerAdapter.setList(archievedList);
    }

    @Override
    public void getNoteListFailure(String message) {
        Toast.makeText(todoHomeActivity, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(todoHomeActivity);
        if (!todoHomeActivity.isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }

    }

    @Override
    public void hideProgressDialog() {
        if (!todoHomeActivity.isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public boolean onLongClick(View view) {
        return false;
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

        recyclerAdapter.setFilter(noteList);
        return true;
    }


    @Override
    public void OnSearchTextChange(String search) {

    }

    public void getIndexUpdateNotes(TodoHomeDataModel doItemModel, List<TodoHomeDataModel> toDoItemModel) {
        List<TodoHomeDataModel> toDoItemModels = new ArrayList<>();
        startdate = doItemModel.getStartdate();
        index = doItemModel.getId();
        for (TodoHomeDataModel todo : toDoItemModel) {
            if (todo.getStartdate().equals(startdate) && todo.getId() > index) {
                toDoItemModels.add(todo);
            }
        }

        if (toDoItemModels != null) {
            removeData(toDoItemModels, index);
        }
    }

    private void removeData(List<TodoHomeDataModel> toDoItemModels, int index) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("note_details");
        pos = index;
        if (toDoItemModels.size() == 1) {
            todohomedatamodel = toDoItemModels.get(0);
            todohomedatamodel.setId(0);
            databaseReference.child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(pos + 1)).setValue(null);
        } else if (toDoItemModels.size() == 0) {
            databaseReference.child(uId).child(startdate).child(String.valueOf(pos)).setValue(null);

        } else {
            for (TodoHomeDataModel todoNote : toDoItemModels) {
                try {
                    Log.i(TAG, "setSize: " + pos);
                    todoNote.setId(pos);
                    databaseReference.child(uId).child(todoNote.getStartdate()).child(String.valueOf(pos)).setValue(todoNote);
                    pos = pos + 1;
                } catch (Exception f) {
                    Log.i(TAG, "setData: ");
                }
            }
            databaseReference.child(uId).child(startdate).child(String.valueOf(pos)).setValue(null);
        }
    }
}








