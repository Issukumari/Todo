package fundoohr.com.secondactivity;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void expmethod(View v)
    {
        Intent i=getPackageManager().getLaunchIntentForPackage("fundoohr.com.splashscreen");
        startActivity(i);
    }

    public void implmethod(View v)
    {
        Intent i=new Intent();
        i.setAction(Intent.ACTION_DIAL);
        startActivity(i);
    }
}

