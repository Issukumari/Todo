package com.app.todo.trash.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.archievefragment.presenter.ArchievepresenterInterface;
import com.app.todo.archievefragment.ui.ArchieveFragmentInterface;
import com.app.todo.database.NoteDatabase;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.trash.presenter.TrashPresenterInterface;
import com.app.todo.trash.presenter.Trashpresenter;
import com.app.todo.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bridgeit on 24/5/17.
 */
public class TrashFragment extends Fragment implements TrashFragmentInterface {
    public static final String TAG = "TrashFragment";
    ProgressDialog progressDialog;
    TrashPresenterInterface presenter;
    RecyclerView trash_recyclerView;
    TodoHomeDataModel todohomedatamodel;
    RecyclerAdapter trash_recyclerAdapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    private String startdate;

    String uId;
    AppCompatTextView trash_textView;
    List<TodoHomeDataModel> datamodels = new ArrayList<>();
    List<TodoHomeDataModel> allnotes = new ArrayList<>();
    ArrayList<TodoHomeDataModel> deleteNoteList = new ArrayList<>();
    AppCompatImageView trash_imageView;
    TodoHomeActivity todoHomeActivity;
    Trashpresenter trashpresenter;
    boolean isGrid = true;

    TodoHomeDataModel todoHomeDataModel = new TodoHomeDataModel();
    RecyclerAdapter recyclerAdapter;
    NoteDatabase database;
    Snackbar snackbar;
    LinearLayout linearLayout;
    private DatabaseReference mfirebasedatabaseref;
    private int pos;
    private int index;
    private SharedPreferences sharedpreferences;

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
        linearLayout= (LinearLayout) view.findViewById(R.id.root_trash_recycler);
        trash_recyclerView = (RecyclerView) view.findViewById(R.id.deleteItem_recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        uId = firebaseAuth.getCurrentUser().getUid();
        trashpresenter=new Trashpresenter(getContext(),this);
        trashpresenter.getDeleteNote(uId);
        trash_imageView = (AppCompatImageView) view.findViewById(R.id.trash_icon);
        trash_textView = (AppCompatTextView) view.findViewById(R.id.trashTextView);
        setHasOptionsMenu(true);

        sharedpreferences = getActivity().getApplicationContext().getSharedPreferences(Constants.keys, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("isGrid", true)) {
            isGrid = true;
            trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            isGrid = false;
            trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }
       // trash_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
       // recyclerAdapter = new RecyclerAdapter(getActivity(), filteredNotes, this);

        initSwipe();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

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
        for (TodoHomeDataModel todoHomeDataModel : allnotes) {
            if (todoHomeDataModel.isDeleted()) {
                deleteNoteList.add(todoHomeDataModel);
            }
        }

        trash_recyclerAdapter = new RecyclerAdapter(getActivity(), deleteNoteList);
        trash_recyclerView.setAdapter(trash_recyclerAdapter);

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
        Toast.makeText(todoHomeActivity, message, Toast.LENGTH_SHORT).show();

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
            mfirebasedatabaseref.child(uId).child(todohomedatamodel.getStartdate()).child(String.valueOf(todohomedatamodel.getId())).setValue(todohomedatamodel);
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
        }}}


   /* public static final String TAG = "TrashFragment";
    RecyclerView archievedRecyclerView;
    RecyclerAdapter recyclerAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    boolean isGrid = true;

    TodoHomeActivity todoHomeActivity;

    TrashPresenterInterface presenter;
    Menu menu;
    ProgressDialog progressDialog;
    public TrashFragment(TodoHomeActivity todoHomeActivity) {
        this.todoHomeActivity = todoHomeActivity;
        presenter = new Trashpresenter(todoHomeActivity, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity) getActivity()).setTitle("Archieve");
    }

    private void initView(View view) {
        archievedRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_archieved_list);

        setHasOptionsMenu(true);

        recyclerAdapter = new RecyclerAdapter(todoHomeActivity);
        archievedRecyclerView.setAdapter(recyclerAdapter);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (2, StaggeredGridLayoutManager.VERTICAL);
        archievedRecyclerView.setLayoutManager(staggeredGridLayoutManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_archieve, container, false);
        initView(view);

        setHasOptionsMenu(true);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //getActivity().setTitle("Archieve");

        presenter.getNoteList(uid);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_as_view) {
            Log.d("menu select", "onOptionsItemSelected: toggle");
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.show_as_view);

        if (isGrid) {
            staggeredGridLayoutManager.setSpanCount(1);
            item.setIcon(R.drawable.ic_action_list);
            item.setTitle("Show as list");
            isGrid = false;
        } else {
            staggeredGridLayoutManager.setSpanCount(2);
            item.setIcon(R.drawable.ic_action_grid);
            item.setTitle("Show as grid");
            isGrid = true;
        }
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
    public void getNoteList(String uid) {

    }
}
*/





