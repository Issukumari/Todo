package com.app.todo.todonoteaddactivity.ui;

import android.view.View;

/**
 * Created by bridgeit on 9/6/17.
 */
public interface TodoNoteaddActivityInterface extends View.OnClickListener{
    void noteaddsuccess(String message);

    void noteaddFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

}
