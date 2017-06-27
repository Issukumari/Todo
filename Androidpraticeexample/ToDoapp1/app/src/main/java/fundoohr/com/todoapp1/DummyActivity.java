package fundoohr.com.todoapp1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by bridgeit on 25/3/17.
 */
public class DummyActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // initView();
  /*  }

    private void initView() {
        EditText editText = (EditText) findViewById(R.id.edit3);
        editText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit3:
                Toast.makeText(DummyActivity.this, "sdbsjahfckj", Toast.LENGTH_SHORT).show();
                break;
        }*/
    }

    @Override
    public void onClick(View view) {

    }
}
