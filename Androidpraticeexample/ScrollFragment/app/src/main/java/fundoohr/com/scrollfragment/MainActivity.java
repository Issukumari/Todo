package fundoohr.com.scrollfragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.PopupMenu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fundoohr.com.scrollfragment.Fragment1;
import fundoohr.com.scrollfragment.Fragment2;
import fundoohr.com.scrollfragment.Fragment3;


public class MainActivity extends AppCompatActivity {
    Button Openbutton;
    TextView textView1;
    Button button1;
    Button button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Openbutton = (Button) findViewById(R.id.button1main);
        button1 = (Button) findViewById(R.id.button2);
        button2 = (Button) findViewById(R.id.button3);

        textView1 = (TextView) findViewById(R.id.textview1main);


        Openbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment1 f1 = new Fragment1();

                getSupportFragmentManager().beginTransaction().replace(R.id.layout1, f1).commit();

            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment2 f2 = new Fragment2();

                getSupportFragmentManager().beginTransaction().replace(R.id.layout1, f2).commit();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment3 f3 = new Fragment3();
                getSupportFragmentManager().beginTransaction().replace(R.id.layout1, f3).commit();
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menutest, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_book:
                Toast.makeText(getApplicationContext(), "Bookmark", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
                return  true;
            case R.id.menu_music:
                Toast.makeText(getApplicationContext(), "music", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this,Main5Activity.class);
                startActivity(i);
                return  true;

            case R.id.menu_setting:
                Toast.makeText(getApplicationContext(), "setting", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(MainActivity.this,Main3Activity.class);
                startActivity(in);
                return  true;

            case R.id.video:
                Toast.makeText(getApplicationContext(), "video", Toast.LENGTH_SHORT).show();
                Intent it=new Intent(MainActivity.this,Main4Activity.class);
                startActivity(it);
                return  true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
