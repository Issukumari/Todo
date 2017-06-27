package com.app.todo.archievefragment.ui;

import android.view.View;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 9/5/17.
 */
public interface ArchieveFragmentInterface extends View.OnLongClickListener{
    void onLongClick( TodoHomeDataModel todoHomeDataModel) ;
    void getNoteListSuccess(List<TodoHomeDataModel> noteList);
    void getNoteListFailure(String message);
    void showProgressDialog(String message);
    void hideProgressDialog();


}
