package com.app.todo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;
import com.app.todo.todohome.ui.TodoHomeActivity;

/**
 * Created by bridgeit on 2/5/17.
 */
public class ArchiveFragment extends android.support.v4.app.Fragment {
    public static final String TAG = "archiveFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((TodoHomeActivity)getActivity()).setTitle("ArchiveFragment");
    }
}
