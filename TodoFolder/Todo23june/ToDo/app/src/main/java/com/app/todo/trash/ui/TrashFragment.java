package com.app.todo.trash.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
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
import com.app.todo.database.NoteDatabase;
import com.app.todo.notes.ui.OnSearchTextChange;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.trash.presenter.Trashpresenter;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TrashFragment extends Fragment implements TrashFragmentInterface, OnSearchTextChange, SearchView.OnQueryTextListener {
    public static final String TAG = "TrashFragment";
    ProgressDialog progressDialog;
    RecyclerView trash_recyclerView;
    TodoHomeDataModel todohomedatamodel;
    RecyclerAdapter trash_recyclerAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String uId;
    AppCompatTextView trash_textView;
    List<TodoHomeDataModel> allnotes = new ArrayList<>();
    ArrayList<TodoHomeDataModel> deleteNoteList = new ArrayList<>();
    AppCompatImageView trash_imageView;
    TodoHomeActivity todoHomeActivity;
    Trashpresenter trashpresenter;
    boolean isGrid = true;
    NoteDatabase database;
    Snackbar snackbar;
    LinearLayout linearLayout;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    private String startdate;
    private DatabaseReference mfirebasedatabaseref;
    private int pos;
    private int index;
    private SharedPreferences sharedpreferences;
    private Menu menu;

    public TrashFragment(TodoHomeActivity todoMainActivity) {
        this.todoHomeActivity = todoMainActivity;
        trashpresenter = new Trashpresenter(todoMainActivity, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Trash");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmenttrash, container, false);
        setHasOptionsMenu(true);
        initView(view);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.note_details);
        database = new NoteDatabase(getActivity());
        getActivity().setTitle("Trash");
        return view;

    }

    private void initView(View view) {
        linearLayout = (LinearLayout) view.findViewById(R.id.root_trash_recycler);
        trash_recyclerView = (RecyclerView) view.findViewById(R.id.deleteItem_recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();
        trashpresenter = new Trashpresenter(getActivity(), this);
        trashpresenter.getDeleteNote(uId);
        trash_imageView = (AppCompatImageView) view.findViewById(R.id.trash_icon);
        trash_textView = (AppCompatTextView) view.findViewById(R.id.trashTextView);
        setHasOptionsMenu(true);
        trash_recyclerAdapter = new RecyclerAdapter(getActivity());
        trash_recyclerView.setAdapter(trash_recyclerAdapter);
        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("isGrid", true)) {
            isGrid = true;
            trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = false;
            trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }

        initSwipe();
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
                    todohomedatamodel = deleteNoteList.get(position);
                    getIndexUpdateNotes(todohomedatamodel, allnotes);
                    snackbar = Snackbar
                            .make(linearLayout, getString(R.string.item_deleted), Snackbar.LENGTH_LONG);
                    snackbar.show();

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
        itemTouchHelper.attachToRecyclerView(trash_recyclerView);
    }

    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());

        if (!getActivity().isFinishing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    @Override
    public void hideDialog() {
        if (!getActivity().isFinishing() && progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void deleteSuccess(List<TodoHomeDataModel> modelList) {
        allnotes.clear();
        allnotes = modelList;
        deleteNoteList.clear();
        for (TodoHomeDataModel todoHomeDataModel : allnotes) {
            if (todoHomeDataModel.isDeleted()) {
                deleteNoteList.add(todoHomeDataModel);
            }
        }
        datamodels = deleteNoteList;
        trash_recyclerAdapter.setList(deleteNoteList);

        if (deleteNoteList.size() != 0) {
            trash_textView.setVisibility(View.INVISIBLE);
            trash_imageView.setVisibility(View.INVISIBLE);
            linearLayout.setGravity(Gravity.START);
        } else {
            trash_textView.setVisibility(View.VISIBLE);
            trash_imageView.setVisibility(View.VISIBLE);
            linearLayout.setGravity(Gravity.CENTER);

        }
    }

    @Override
    public void deleteFailure(String message) {
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
        mfirebasedatabaseref = FirebaseDatabase.getInstance().getReference().child("note_details");
        pos = index;
        if (toDoItemModels.size() == 1) {
            todohomedatamodel = toDoItemModels.get(0);
            todohomedatamodel.setId(0);
            mfirebasedatabaseref.child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(pos + 1)).setValue(null);
        } else if (toDoItemModels.size() == 0) {
            mfirebasedatabaseref.child(uId).child(startdate).child(String.valueOf(pos)).setValue(null);

        } else {
            for (TodoHomeDataModel todoNote : toDoItemModels) {
                try {
                    Log.i(TAG, "setSize: " + pos);
                    todoNote.setId(pos);
                    mfirebasedatabaseref.child(uId).child(todoNote.getStartdate()).child(String.valueOf(pos)).setValue(todoNote);
                    pos = pos + 1;
                } catch (Exception f) {
                    Log.i(TAG, "setData: ");
                }
            }
            mfirebasedatabaseref.child(uId).child(startdate).child(String.valueOf(pos)).setValue(null);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_as_view:
                if (!isGrid) {
                    trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_grid1);
                    isGrid = true;
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putBoolean("isGrid", true);
                    edit.commit();

                } else {
                    trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
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

        trash_recyclerAdapter.setList(noteList);
        return true;
    }


    @Override
    public void OnSearchTextChange(String search) {

    }
    /*private void getDeleteNotes() {
        List<TodoHomeDataModel> trashNotes = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            trashNotes.add(mTrashNotes.get(arrayList.get(i)));
        }
        if (trashNotes.size() != 0) {
            trashNotePresenter.getDeleteMultipleTrashNotes(trashNotes);
        }
    }*/
}





