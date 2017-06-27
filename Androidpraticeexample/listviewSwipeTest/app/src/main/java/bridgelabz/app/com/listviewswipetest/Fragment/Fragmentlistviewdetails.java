package bridgelabz.app.com.listviewswipetest.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bridgelabz.app.com.listviewswipetest.R;

public class Fragmentlistviewdetails extends Fragment  {
    AppCompatEditText fragmentlistview_EdittextView;
    AppCompatEditText fragmentlistviews_EdittextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_listviewdetails, container, false);
        fragmentlistview_EdittextView = (AppCompatEditText)view.findViewById(R.id.fragmentlistview_EdittextView);
        fragmentlistviews_EdittextView = (AppCompatEditText)view.findViewById(R.id.fragmentlistviews_EdittextView);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String data = (bundle.getString("text"));
            String data2 = bundle.getString("text1");
            fragmentlistview_EdittextView.setText(data);
            fragmentlistviews_EdittextView.setText(data2);
        }
        return view;
    }
}
