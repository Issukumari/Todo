package com.app.todo.archievefragment.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.archievefragment.presenter.ArchievePresenter;
import com.app.todo.archievefragment.presenter.ArchievepresenterInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class ArchieveFragment extends Fragment implements ArchieveFragmentInterface {
    public static final String TAG = "ArchieveFragment";
    RecyclerView archievedRecyclerView;
    RecyclerAdapter recyclerAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    boolean isGrid = true;

    TodoHomeActivity todoHomeActivity;

    ArchievepresenterInterface presenter;
    /* @Override
     public void onLongClick(final TodoHomeDataModel todoHomeDataModel) {
         AlertDialog.Builder builder = new AlertDialog.Builder(todoHomeActivity);
         builder.setTitle(todoHomeActivity.getString(R.string.moving_to_note));
         builder.setMessage(todoHomeActivity.getString(R.string.ask_move_to_note_message));
         builder.setPositiveButton(todoHomeActivity.getString(R.string.ok_button),
                 new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         todoHomeActivity.presenter.moveToNotes(todoHomeDataModel);
                     }
                 });

         builder.setNegativeButton(todoHomeActivity.getString(R.string.cancel_button),
                 new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialogInterface, int i) {
                         Toast.makeText(todoHomeActivity
                                 , todoHomeActivity.getString(R.string.cancel_message)
                                 , Toast.LENGTH_SHORT).show();
                     }
                 });
         builder.show();
     }*/
    Menu menu;
    ProgressDialog progressDialog;

    public ArchieveFragment(TodoHomeActivity todoHomeActivity) {
        this.todoHomeActivity = todoHomeActivity;
        presenter = new ArchievePresenter(todoHomeActivity, this);
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

        presenter.getNoteList(uid);

        return view;
    }

    /*@Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
    }*/

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
}








