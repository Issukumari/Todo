package fundoohr.com.scrollfragment;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {
    ListView listView1;
    String st[] = {"ram", "suresh", "smita", "saloni"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        listView1 = (ListView) findViewById(R.id.listView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, st);
        listView1.setAdapter(adapter);

        registerForContextMenu(listView1);
    }

   public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("select The Action");
        menu.add(0, view.getId(), 0, "call");//groupId, itemId, order, title
        menu.add(0, view.getId(), 0, "sms");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "call") {
            Toast.makeText(getApplicationContext(), "calling code", Toast.LENGTH_LONG).show();
        } else if (item.getTitle() == "sms") {
            Toast.makeText(getApplicationContext(), "sending sms ", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }
}