package fundoohr.com.intentextra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result=(TextView)findViewById(R.id.textview);
    }
    public void redBackground(View v) {

        // Here we create an Intent

        // and we put an extra in order to pass it to the ActivityTwo

        Intent intent = new Intent(this, Main2Activity.class);
        //the extra is a String that tell the background color choice
        intent.putExtra("color","red");
        //we start ActivityTwo with the above extra value
        startActivityForResult(intent,2);
       // finish();

    }



        public void greenBackground(View v) {

        Intent intent = new Intent(this, Main2Activity.class);

        intent.putExtra("color","green");

        startActivityForResult(intent,2);

      //  finish();

     }



     public void blueBackground(View v) {

        Intent intent = new Intent(this, Main2Activity.class);

        intent.putExtra("color","blue");
        startActivityForResult(intent,2);

    //    finish();

     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            String sumValue = data.getStringExtra("sum");
            result.setText(sumValue);
        }
    }
}



