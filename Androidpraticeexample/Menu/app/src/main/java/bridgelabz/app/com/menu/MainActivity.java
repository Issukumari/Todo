package bridgelabz.app.com.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        *//*switch (item.getItemId()) {

            case R.id.action_viewchange:

                if(isGrid){
                    gridview.setNumColumns(1);
                    imageAdapter.notifyDataSetChanged();
                    isGrid = false;
                }
                else{
                    gridview.setNumColumns(2);
                    imageAdapter.notifyDataSetChanged();
                    isGrid = true;
                }

                return true;
            case R.id.action_settings:
                Toast.makeText(this, "item Selectd", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_skip:
                Toast.makeText(this, "item Selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}*//*
    }*/
}
