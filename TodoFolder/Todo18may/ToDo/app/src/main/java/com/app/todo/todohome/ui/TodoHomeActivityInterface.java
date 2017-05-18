package com.app.todo.todohome.ui;

import android.support.design.widget.NavigationView;
import android.view.View;

import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 9/5/17.
 */
public interface TodoHomeActivityInterface extends NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, RecyclerAdapter.OnLongClickListener {
    void getNoteFailure(String message);

    void showProgressDialog(String message);

    void hideProgressDialog();

    void getNotesListSuccess(List<TodoHomeDataModel> modelList);

    void deleteTodoHomeaModelSuccess(String message);
}





