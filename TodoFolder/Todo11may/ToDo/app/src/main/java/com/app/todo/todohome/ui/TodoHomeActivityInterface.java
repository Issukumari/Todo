package com.app.todo.todohome.ui;

import android.support.design.widget.NavigationView;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 9/5/17.
 */
public interface TodoHomeActivityInterface extends NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, RecyclerAdapter.OnLongClickListener{
        void getNoteSuccess(List<TodoHomeDataModel> noteList);
        void getNoteFailure(String message);
        void showProgressDialog(String message);
        void hideProgressDialog();

        void deleteTodoHomeModelaFailure(String message);

        void deleteTodoHomeaModelSuccess(String message);

        void moveToArchieveFailure(String message);

        void moveToArchieveSuccess(String message);
        }





