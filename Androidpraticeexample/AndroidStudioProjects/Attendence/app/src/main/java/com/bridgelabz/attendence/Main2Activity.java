package com.bridgelabz.attendence;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import static android.R.id.edit;

public class Main2Activity extends AppCompatActivity implements Animation.AnimationListener {

    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry","WebOS","Ubuntu","Windows7","Max OS X"};
    Animation animation;
    EditText middleEditText,upEditText;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView= (ListView) findViewById(R.id.listView);
        middleEditText= (EditText) findViewById(R.id.edittext);
        upEditText=(EditText)findViewById(R.id.edittext1);


    }

    public void slideUp(View view){
        middleEditText.setVisibility(View.VISIBLE);
        animation= AnimationUtils.loadAnimation(Main2Activity.this,R.anim.move);
        animation.setAnimationListener(this);
        middleEditText.setAnimation(animation);
        /*TranslateAnimation slide = new TranslateAnimation(0, 0, 100,0 );
        slide.setDuration(1000);
        slide.setFillAfter(true);
        editText.startAnimation(slide);*/
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        middleEditText.setVisibility(View.INVISIBLE);
        upEditText.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mobileArray);
        listView.setAdapter(adapter);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
