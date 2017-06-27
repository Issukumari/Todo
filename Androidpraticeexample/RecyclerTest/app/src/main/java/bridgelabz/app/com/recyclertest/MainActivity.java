package bridgelabz.app.com.recyclertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    boolean isGrid = true;
    GridView gridview;
    ImageAdapter imageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view=new View(getApplicationContext());
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(2);

        imageAdapter = new ImageAdapter(this);

        gridview.setAdapter(imageAdapter);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){
                // Send intent to SingleViewActivity
                Intent i = new Intent(getApplicationContext(), SingleViewActivity.class);
                // Pass image index
                i.putExtra("id", position);
                startActivity(i);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

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
}
