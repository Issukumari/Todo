package com.app.todo.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.todo.R;

/**
 * Created by bridgeit on 6/4/17.
 */

public class AboutFragment extends Fragment {
    FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.activity_aboutfragment, container, false);
        /*floatingActionButton= (FloatingActionButton) view.findViewById(R.id.fab_button);
        floatingActionButton.setVisibility(View.INVISIBLE);*/
        return view;

    }

}
