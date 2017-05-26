package com.app.todo.notes.ui;

import android.view.MotionEvent;
import android.view.View;

import com.app.todo.todohome.model.TodoHomeDataModel;

import java.util.List;

/**
 * Created by bridgeit on 15/5/17.
 */
public interface NotesFragmentInterface {
    void showDialog(String message);

    void hideDialog();

    void getNoteListSuccess(List<TodoHomeDataModel> modelList);
    void onLongClick(  View view , MotionEvent event) ;

    void getNotesListFailure(String message);
}
