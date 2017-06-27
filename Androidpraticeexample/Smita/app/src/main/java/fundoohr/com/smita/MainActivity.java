package fundoohr.com.smita;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView  textViewname;
    EditText editTextname;
    Button buttonsave;

    private static final String EXTRA_MESSAGE = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


    /** Called when the user clicks the Send button */
   // public void sendMessage(View view) {
       // Intent intent = new Intent(this, DisplayMessageActivity.class);
        buttonsave=(Button) findViewById(R.id.buttonsave);
        editTextname=(EditText) findViewById(R.id.nameedit);
        textViewname=(TextView) findViewById(R.id.nametext);
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextname.setText("android");
              //  textViewname.setText(editTextname.getText());
            }
        });}

}

