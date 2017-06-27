package com.bridgelabz.textrecoginizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    TextRecognizer textRecognizer;
    private static final String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textRecognizer = new TextRecognizer.Builder(MainActivity.this).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(MainActivity.this,"Text Recoginization is not operational",Toast.LENGTH_LONG).show();
        }else{
            Bitmap imageBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.hand_two);
            Frame frame=new Frame.Builder().setBitmap(imageBitmap).build();
            SparseArray<TextBlock> sparseArray=textRecognizer.detect(frame);
            Log.i(TAG, "onCreate: "+sparseArray.get(0).getValue());
            Toast.makeText(MainActivity.this,sparseArray.get(0).getValue(),Toast.LENGTH_LONG).show();
        }
    }
}
