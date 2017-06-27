package fundoohr.com.fragactivity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import layout.Fragment1;
import layout.Fragment2;
import layout.Fragment3;

public class MainActivity extends AppCompatActivity {
    Button Openbutton;
    TextView textView1;
    Button button1;
    Button button2;
    View fragment1;
    View fragment2;
    View fragment3;
    Fragment1 f1;
    Fragment2 f2;
    Fragment3 f3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment1 = findViewById(R.id.fragment34);
        fragment2 = findViewById(R.id.fragment35);
        fragment3 = findViewById(R.id.fragment36);

        fragment1.setVisibility(View.INVISIBLE);
        fragment2.setVisibility(View.INVISIBLE);
        fragment3.setVisibility(View.INVISIBLE);


        /*fragment1.setVisibility(View.GONE);
        fragment2.setVisibility(View.GONE);
        fragment3.setVisibility(View.GONE);*/

        Openbutton=(Button) findViewById(R.id.button1main);
        button1=(Button) findViewById(R.id.button2);
        button2=(Button) findViewById(R.id.button3);

        textView1=(TextView) findViewById(R.id.textview1main);


        Openbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              f1 =new Fragment1();
              fragment2.setVisibility(View.GONE);
                fragment3.setVisibility(View.GONE);
                fragment1.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment34,f1).commit();

             }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 f2=new Fragment2();

             fragment1.setVisibility(View.GONE);
                fragment3.setVisibility(View.GONE);

                fragment2.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment35,f2).commit();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment1.setVisibility(View.GONE);
                fragment2.setVisibility(View.GONE);
                f3=new Fragment3();
                fragment3.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment36,f3).commit();
            }
        });
    }
}