package fundoohr.com.myfir;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SumOfTWoNo extends AppCompatActivity implements OnClickListener {
    private EditText edittext1, edittext2;
   // private Button buttonSum;
private Button add,sub,mul,div;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sum_of_two_no);
        edittext1 = (EditText) findViewById(R.id.editText1);
        edittext2 = (EditText) findViewById(R.id.editText2);
        add = (Button) findViewById(R.id.add);
        sub = (Button) findViewById(R.id.bSub);
        mul=(Button)  findViewById(R.id.mul);
        div=(Button)  findViewById(R.id.div);

        add.setOnClickListener(this);
        sub.setOnClickListener(this);
        mul.setOnClickListener(this);
        div.setOnClickListener(this);

    }
   @Override
   public void onClick(View view) {
       String value1 = edittext1.getText().toString();
       String value2 = edittext2.getText().toString();
       int a,b,result=0;

         switch (view.getId())
       {
           case R.id.add:

                a = Integer.parseInt(value1);
                b = Integer.parseInt(value2);
               //  int value=Integer.parseInt(value3);

               result = a + b;

               Toast.makeText(SumOfTWoNo.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
               break;
           case R.id.bSub:

                a = Integer.parseInt(value1);
                b = Integer.parseInt(value2);
               //  int value=Integer.parseInt(value3);


               result = a - b;
               Toast.makeText(SumOfTWoNo.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
               break;

           case R.id.mul:

               a = Integer.parseInt(value1);
               b = Integer.parseInt(value2);
               //  int value=Integer.parseInt(value3);


               result = a * b;
               Toast.makeText(SumOfTWoNo.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
               break;

           case R.id.div:

               a = Integer.parseInt(value1);
               b = Integer.parseInt(value2);
               //  int value=Integer.parseInt(value3);


               result = a / b;
               Toast.makeText(SumOfTWoNo.this, String.valueOf(result), Toast.LENGTH_SHORT).show();
               break;
           default:

               break;


       }
    

       }





    }


