package com.app.todo.todonoteaddfragment.ui;

import android.view.View;

/**
 * Created by bridgeit on 6/5/17.
 */
public interface TodonoteaddFragmentInterface extends View.OnClickListener {
    void noteaddsuccess(String message);

    void noteaddFailure(String message);

    void showProgressDialog(String mesage);

    void hideProgressDialog();

}
