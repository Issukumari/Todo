package fundoohr.com.datepicker;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import  android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
public class TimePick extends AppCompatActivity {
    TextView textview1;
    android.widget.TimePicker timepicker1;
    Button changetime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);


        textview1=(TextView)findViewById(R.id.textViewpicker1);
        timepicker1=(android.widget.TimePicker)findViewById(R.id.timePicker1);
        //Uncomment the below line of code for 24 hour view
        timepicker1.setIs24HourView(true);
        changetime=(Button)findViewById(R.id.buttonpicker1);

        textview1.setText(getCurrentTime());


    }

    @TargetApi(Build.VERSION_CODES.M)
    public String getCurrentTime(){
        String currentTime="Current Time: "+timepicker1.getHour() +":"+timepicker1.getMinute();
        return currentTime;
    }




}
