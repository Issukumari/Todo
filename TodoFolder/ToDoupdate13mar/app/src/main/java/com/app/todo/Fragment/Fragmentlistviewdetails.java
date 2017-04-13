package com.app.todo.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.todo.Database.DatabaseActivity;
import com.app.todo.R;
import com.app.todo.adapter.RecyclerAdapter;
import com.app.todo.model.DataModel;
import com.app.todo.ui.TodoHomeActivity;

public class Fragmentlistviewdetails extends Fragment implements View.OnClickListener {
    AppCompatEditText fragmentlistview_TitleEditText;
    AppCompatEditText fragmentlistview_DescriptionEditText;
    AppCompatButton Update_button;
TodoHomeActivity todoHomeActivity;
    public Fragmentlistviewdetails(TodoHomeActivity todoHomeActivity) {
        this.todoHomeActivity=todoHomeActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listviewdetails, container, false);
        fragmentlistview_TitleEditText = (AppCompatEditText) view.findViewById(R.id.fragmentlistview_TitleEditText);
        fragmentlistview_DescriptionEditText = (AppCompatEditText) view.findViewById(R.id.fragmentlistview_DescriptionEditText);
        Update_button = (AppCompatButton) view.findViewById(R.id.Update_buton);
        Update_button.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String data = (bundle.getString("Titletext"));
            String data2 = bundle.getString("Desriptiontext");
            fragmentlistview_TitleEditText.setText(data);
            fragmentlistview_DescriptionEditText.setText(data2);
        }
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Update_buton:
                DataModel dataModel = new DataModel(fragmentlistview_TitleEditText.getText().toString(), fragmentlistview_DescriptionEditText.getText().toString());
                DatabaseActivity db = new DatabaseActivity(getActivity());
                db.updateItem(dataModel);
                todoHomeActivity.setBackData(dataModel);
                getActivity().getFragmentManager().popBackStackImmediate();
                break;
        }
    }
}