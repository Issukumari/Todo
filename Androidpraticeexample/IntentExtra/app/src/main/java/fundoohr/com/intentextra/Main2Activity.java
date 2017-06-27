package fundoohr.com.intentextra;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    RelativeLayout rl;
    Button btn1;
    EditText e1;
    EditText e2;
    Button add;
    Button sub;
    Button mul;
    Button div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout of the Activity
        setContentView(R.layout.activity_main2);
        rl = (RelativeLayout) findViewById(R.id.layout);
        btn1 = (Button) findViewById(R.id.openActivityone);
        e1 = (EditText) findViewById(R.id.editText1);
        e2 = (EditText) findViewById(R.id.editText2);
        add = (Button) findViewById(R.id.add);
        sub = (Button) findViewById(R.id.bSub);
        mul = (Button) findViewById(R.id.mul);
        div = (Button) findViewById(R.id.div);

        // get the intent that we have passed from ActivityOne
        Intent intent = getIntent();
        // get the extra value
        String color = intent.getStringExtra("color");
        changeBackground(color);


    }

    public void openActivityone(View v) {

        Intent intent = new Intent(this, MainActivity.class);


        startActivity(intent);
        finish();
    }

    public void changeBackground(String color) {

        // depending the extra value String, choose a background color

        if (color.equals("red")) {

            rl.setBackgroundColor(Color.RED);

        } else if (color.equals("green")) {

            rl.setBackgroundColor(Color.GREEN);

        } else if (color.equals("blue")) {

            rl.setBackgroundColor(Color.BLUE);

        }
    }


    public void ADD(View v) {
        String value1 = e1.getText().toString();
        String value2 = e2.getText().toString();

        int a, b, result = 0;
        a = Integer.parseInt(value1);
        b = Integer.parseInt(value2);
        //  int value=Integer.parseInt(value3);
        result = a + b;

        String s1 = String.valueOf(result);
        //  Toast.makeText(Main2Activity.this, s1, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        intent.putExtra("sum", s1);
        setResult(2, intent);
        finish();
    }

    public void SUB(View v) {
        String value1 = e1.getText().toString();
        String value2 = e2.getText().toString();

        int a, b, result = 0;
        a = Integer.parseInt(value1);
        b = Integer.parseInt(value2);
        //  int value=Integer.parseInt(value3);
        result = a - b;

        String s1 = String.valueOf(result);
        //  Toast.makeText(Main2Activity.this, s1, Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(Main2Activity.this, MainActivity.class);
        intent2.putExtra("sum", s1);
        setResult(2, intent2);
        finish();
    }

    public void Mul(View v) {
        String value1 = e1.getText().toString();
        String value2 = e2.getText().toString();

        int a, b, result = 0;
        a = Integer.parseInt(value1);
        b = Integer.parseInt(value2);
        //  int value=Integer.parseInt(value3);
        result = a * b;

        String s1 = String.valueOf(result);
        //  Toast.makeText(Main2Activity.this, s1, Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(Main2Activity.this, MainActivity.class);
        intent2.putExtra("sum", s1);
        setResult(2, intent2);
        finish();
    }

    public void Div(View v) {
        String value1 = e1.getText().toString();
        String value2 = e2.getText().toString();

        int a, b, result = 0;
        a = Integer.parseInt(value1);
        b = Integer.parseInt(value2);
        //  int value=Integer.parseInt(value3);
        result = a / b;

        String s1 = String.valueOf(result);
        //  Toast.makeText(Main2Activity.this, s1, Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(Main2Activity.this, MainActivity.class);
        intent2.putExtra("sum", s1);
        setResult(2, intent2);
            finish();
    }

}