package fundoohr.com.datepicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;


public class DatePickActivity extends AppCompatActivity {
    TextView textview1;
    Button DisplayDate;
    DatePicker datePicker;
    Button button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        textview1 = (TextView) findViewById(R.id.textview1);
        DisplayDate = (Button) findViewById(R.id.Button1);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        button2 = (Button) findViewById(R.id.button2);

        textview1.setText(getCurrentDate());

        DisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textview1.setText(getCurrentDate());


            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatePickActivity.this, TimePick.class);
                startActivity(intent);

            }
        });
    }


    public String getCurrentDate(){
        StringBuilder builder=new StringBuilder();
        builder.append("Current Date: ");
        builder.append((datePicker.getMonth() + 1)+"/");//month is 0 based
        builder.append(datePicker.getDayOfMonth()+"/");
        builder.append(datePicker.getYear());
        return builder.toString();
    }
}
