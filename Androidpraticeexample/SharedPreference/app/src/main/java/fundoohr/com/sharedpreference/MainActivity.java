package fundoohr.com.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

         EditText ed1;
         EditText ed2;
         EditText ed3;
        Button b1;
        TextView PreviousExpenses;

        final String MyPREFERENCES = "MyPrefs" ;
         final String Name = "nameKey";
        final String Phone = "phoneKey";
         final String Email = "emailKey";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ed1=(EditText)findViewById(R.id.editText1);
            ed2=(EditText)findViewById(R.id.editText2);
            ed3=(EditText)findViewById(R.id.editText3);
            b1=(Button)findViewById(R.id.button);

            PreviousExpenses=(TextView) findViewById(R.id.textView1);
            final SharedPreferences sh= getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String n  = ed1.getText().toString();
                    String ph  = ed2.getText().toString();
                    String e  = ed3.getText().toString();

                    SharedPreferences.Editor editor = sh.edit();

                    editor.putString(Name, n);
                    editor.putString(Phone, ph);
                    editor.putString(Email, e);
                    editor.commit();
                    Toast.makeText(MainActivity.this,"Thanks",Toast.LENGTH_LONG).show();
                }
            });
        }

    }

