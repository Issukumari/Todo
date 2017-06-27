package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fundoohr.com.fragmentactivity.R;


public class Fragment1 extends android.app.Fragment {
    TextView TextViewname;
    EditText EditTextname;
    Button Buttonsave;
    Button Openfragment;



    public static android.app.Fragment netInstance(){
        android.app.Fragment fragment = new android.app.Fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        return  view;


    }


}