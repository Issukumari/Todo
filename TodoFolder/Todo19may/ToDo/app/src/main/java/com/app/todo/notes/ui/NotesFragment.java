package com.app.todo.notes.ui;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.database.NoteDatabase;
import com.app.todo.notes.presenter.NotesPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddfragment.ui.TodoNoteaddFragment;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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
    List<TodoHomeDataModel> filteredNotes = new ArrayList<>();
    NotesPresenter presenter;
    String uId;
    ProgressDialog progressDialog;
    DatabaseReference mfirebasedatabaseref;
    TodoHomeDataModel dataModel = new TodoHomeDataModel();
    NoteDatabase database;
    TodoHomeActivity todohomeactivity;
    Snackbar snackbar;
    View view;
    private FloatingActionButton fabbutton;
    private StaggeredGridLayoutManager layoutManager;
    private String userId;
    private NoteDatabase databse;
    private RecyclerView recyclerView;
    private String searchTag;
    private FirebaseAuth auth;
    private Menu menu;

   /* public NotesFragment(TodoHomeActivity todoHomeActivity) {
        this.todohomeactivity = todohomeactivity;
        presenter = new NotesPresenter(todohomeactivity, this);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentnotes, container, false);
        database = new NoteDatabase(getActivity());
        presenter = new NotesPresenter(getContext(), this);

        fabbutton = (FloatingActionButton) view.findViewById(R.id.fabbutton);
        recyclerViewnotes = (RecyclerView) view.findViewById(R.id.notesrecyclerview);
        mfirebasedatabaseref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.note_details));
        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        presenter.getNoteList(uId);
        setHasOptionsMenu(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewnotes.setLayoutManager(layoutManager);

        initSwipe();
        ((TodoHomeActivity) getActivity()).setSearchTagListener(this);
        fabbutton.setOnClickListener(this);

        return view;

    }

    public void initSwipe() {
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
                    databse = new NoteDatabase(getActivity().getApplicationContext());
                    todohomedatamodel = allnotes.get(position);
                    mfirebasedatabaseref.child("note_details").child(uId).child(todohomedatamodel.getStartdate())
                            .child(String.valueOf(todohomedatamodel.getId())).removeValue();
                    databse.removeItem(datamodels.get(position));

                }
                if (direction == ItemTouchHelper.RIGHT) {
                    datamodels = getWithoutArchive();
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

    private List<TodoHomeDataModel> getWithoutArchive() {
        ArrayList<TodoHomeDataModel> todoHomeDataModel = new ArrayList<>();
        for (TodoHomeDataModel note : allnotes) {
            if (!note.isArchieve()) {
                todoHomeDataModel.add(note);
            }
        }
        return todoHomeDataModel;
    }

    @Override
    public void showDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
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
                todoHomeDataModel.add(note);

            }
        }
        datamodels = todoHomeDataModel;
        // checkLayout();

        recyclerAdapter = new RecyclerAdapter(getActivity(), datamodels);
        recyclerViewnotes.setAdapter(recyclerAdapter);

    }


    @Override
    public void getNotesListFailure(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabbutton:

                TodoNoteaddFragment todoNoteaddFragment = new TodoNoteaddFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, todoNoteaddFragment).addToBackStack(null).commit();


                break;

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

        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_as_view:
                if (!isGrid) {
                    recyclerViewnotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    isGrid = true;
                } else {
                    recyclerViewnotes.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                    isGrid = false;
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
}


