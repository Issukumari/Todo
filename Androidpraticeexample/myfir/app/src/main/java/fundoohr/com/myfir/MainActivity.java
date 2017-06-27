package fundoohr.com.myfir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity                //implements View.OnClickListener
{
    //private static final String TAG = "MainActivity";
    TextView TextViewname;
    EditText EditTextname;
    Button Buttonsave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        Buttonsave=(Button) findViewById(R.id.buttonsave);
        EditTextname=(EditText) findViewById(R.id.nameedit);
        TextViewname=(TextView) findViewById(R.id.nametext);

        /*buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextname.setText("O android");//just
                textViewname.setText(editTextname.getText());
                Intent intent =new Intent(MainActivity.this,SumOfTWoNo.class);
                startActivity(intent);
            }
        });*/
      //  buttonsave.setOnClickListener(this);
        Buttonsave.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              EditTextname.setText(EditTextname.getText());//just
                                              TextViewname.setText(EditTextname.getText());
                                              Intent intent =new Intent(MainActivity.this,SumOfTWoNo.class);
                                              startActivity(intent);

                                          }});
    }
}
  /*  @Override
    protected void onStart() {
        super.onStart();
       Log.i(TAG, "onStart: ");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }*/

    //@Override
  //  public void onClick(View view) {
      //  switch (view.getId()){
        //    case R.id.buttonsave:
       //         editTextname.setText(getString(R.string.android));//just
         //       textViewname.setText(editTextname.getText());
         //       Intent intent =new Intent(MainActivity.this,SumOfTWoNo.class);
         //       startActivity(intent);
           //     break;
       // }
    //}
//}
