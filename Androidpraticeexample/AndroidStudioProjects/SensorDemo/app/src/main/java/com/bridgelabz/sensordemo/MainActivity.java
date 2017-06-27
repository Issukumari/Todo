package com.bridgelabz.sensordemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer,senGravity,senGyroscope;
    private static final String TAG=MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senGravity=senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        senGyroscope=senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case  Sensor.TYPE_ACCELEROMETER :
                Log.i(TAG, "Accelerometer : X: "+sensorEvent.values[0]+" Y :"+sensorEvent.values[1]+" Z:"+sensorEvent.values[2]);
                break;
            case Sensor.TYPE_GRAVITY:
                Log.i(TAG, "Gravity: X: "+sensorEvent.values[0]+" Y :"+sensorEvent.values[1]+" Z:"+sensorEvent.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                Log.i(TAG, "Gyroscope : X: "+sensorEvent.values[0]+" Y :"+sensorEvent.values[1]+" Z:"+sensorEvent.values[2]);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        //senSensorManager.registerListener(this, senGravity , SensorManager.SENSOR_DELAY_NORMAL);
        //senSensorManager.registerListener(this, senGyroscope , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
