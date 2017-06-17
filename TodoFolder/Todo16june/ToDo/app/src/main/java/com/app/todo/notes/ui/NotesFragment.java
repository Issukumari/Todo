package com.app.todo.notes.ui;

import android.app.Activity;
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
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.database.NoteDatabase;
import com.app.todo.notes.presenter.NotesPresenter;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.todonoteaddactivity.ui.TodoNoteaddActivity;
import com.app.todo.todonoteaddfragment.ui.TodoNoteaddFragment;
import com.app.todo.utils.CommonUtils;
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
    NotesPresenter presenter;
    String uId;
    TodoNoteaddFragment todoNoteaddFragment;
    TodoNoteaddActivity todoNoteaddActivity;
    ProgressDialog progressDialog;
    DatabaseReference mfirebasedatabaseref;
    NoteDatabase database;
    View view;
    int index;
    private FloatingActionButton fabbutton;
    private Menu menu;
    private SharedPreferences sharedpreferences;
    private String startdate;
    private int pos;
    private int X, Y;
    private int _xDelta;
    private int _yDelta;
    private CardView cardview;

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Notes");

        if(menu!=null){}

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

        //cardview.setOnTouchListener(new View.OnTouchListener() {

            /*@Override
            public boolean onTouch(View view, MotionEvent event) {
                X = (int) event.getRawX();
                Y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        _xDelta = X - params.leftMargin;
                        _yDelta = Y - params.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        params1.leftMargin = X - _xDelta;
                        params1.topMargin = Y - _yDelta;
                        params1.rightMargin = -250;
                        params1.bottomMargin = -250;
                        view.setLayoutParams(params1);
                        break;
                }
                return true;
            }
*/
        //});
        return view;
    }
    public void initSwipe() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT|ItemTouchHelper.UP
                | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                recyclerAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
                return true;
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
        if (CommonUtils.isNetworkConnected(getActivity())) {
            allnotes.clear();

        }
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
    public void getNotesListFailure(String message) {
//      ghghgnhgnbhgvcnvgbn vgbnv  Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabbutton:
                todoNoteaddActivity =new TodoNoteaddActivity();
                Intent intent=new Intent(getActivity(),TodoNoteaddActivity.class);
                getActivity(). startActivityForResult(intent,2);
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

    public void setcolor(int color) {
        if(todoNoteaddFragment!=null){
            todoNoteaddFragment.setcolor(color);
        }else {
            recyclerAdapter.setColor(color);
        }
    }
    public void fade(){
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

