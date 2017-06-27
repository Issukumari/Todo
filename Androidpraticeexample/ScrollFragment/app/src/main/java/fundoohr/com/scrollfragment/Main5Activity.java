package fundoohr.com.scrollfragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main5Activity extends AppCompatActivity {
    EditText Food;
    EditText Shopping;
    EditText Fuel;
    EditText Phn_no;
    Button button1;
    SharedPreferences  sh;
TextView PreviousExpenses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        Food=(EditText) findViewById(R.id.editText1);
        Shopping=(EditText)findViewById(R.id.editText2);
        Fuel=(EditText)findViewById(R.id.editText3);
        Phn_no=(EditText)findViewById(R.id.editText4);
         button1=(Button)findViewById(R.id.button1);
        PreviousExpenses=(TextView)findViewById(R.id.textView5);
           sh= getSharedPreferences("Result", Context.MODE_PRIVATE);
        PreviousExpenses.setText(String.valueOf(sh.getInt("Result",0)));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int expenses1= Integer.parseInt(Food.getText().toString() );
                int expenses2= Integer.parseInt(Shopping.getText().toString());
                int expenses3 = Integer.parseInt(Fuel.getText().toString());
                int expenses4=  Integer.parseInt(Phn_no.getText().toString());
                int Result= expenses1+ expenses2 +expenses3 +expenses4;
                Toast.makeText(Main5Activity.this,"Enter your expenses"+ String.valueOf(Result),Toast.LENGTH_LONG).show();

                SharedPreferences.Editor  edit=sh.edit();
                edit.putInt("Result",Result);
                edit.commit();
                PreviousExpenses.setText("your total expenses was "+ String.valueOf(Result));

            }
        });

        }



    }

