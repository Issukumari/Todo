package com.app.todo.reminderfragment.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.reminderfragment.presenter.ReminderPresenter;
import com.app.todo.reminderfragment.presenter.ReminderPresenterInterface;
import com.app.todo.todohome.model.TodoHomeDataModel;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReminderFragment extends Fragment implements ReminderFrragmentInterface {
    public static final String TAG = "reminderFragment";
    TodoHomeActivity todohomeactivity;
    RecyclerView reminderRecyclerView;
    RecyclerAdapter recycleradapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    ReminderPresenterInterface presenter;
    ProgressDialog progressDialog;
    private String currentDate;

    public ReminderFragment(TodoHomeActivity todohomeactivity) {
        this.todohomeactivity = todohomeactivity;
        presenter = new ReminderPresenter(todohomeactivity, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TodoHomeActivity)getActivity()).setTitle("Reminder");
    }

    private void initView(View view) {
        reminderRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_reminder_list);

        setHasOptionsMenu(true);

        recycleradapter = new RecyclerAdapter(todohomeactivity);
        reminderRecyclerView.setAdapter(recycleradapter);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (2, StaggeredGridLayoutManager.VERTICAL);
        reminderRecyclerView.setLayoutManager(staggeredGridLayoutManager);
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
                    recycleradapter.setList(reminderlist);

                }
            }
        }}
    @Override
    public void getReminderFailure(String message) {
        ;
        Toast.makeText(todohomeactivity, message, Toast.LENGTH_SHORT).show();

    }
}