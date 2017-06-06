package com.app.todo.notificationalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.todo.R;
import com.app.todo.reminderfragment.ui.ReminderFragment;
import com.app.todo.todohome.ui.TodoHomeActivity;
import com.app.todo.utils.Constants;

public class NotifyService extends Service {
    private static final int REQUEST_CODE = 0;
    Bundle bundle;

    private String TAG="NotifyService";
    ReminderFragment reminderFragment;
    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }
    // Unique id to identify the notification.
    private static final int NOTIFICATION = 123;
    // Name of an intent extra we can use to identify if this service was started to create a notification
    public static final String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
    // The system notification manager
    private NotificationManager mNM;
    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        bundle  =intent.getExtras();
        // If this service was started by out AlarmTask intent then we want to show our notification
        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();
        // We don't care if this service is stopped as we have already delivered our notification
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    // This is the object that receives interactions from clients
    private final IBinder mBinder = new ServiceBinder();
    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification() {

        // This is the 'title' of the notification
        CharSequence title = bundle.getString(Constants.Titletext);
        // This is the icon to use on the notification
        int icon = R.drawable.ic_notes;
        // This is the scrolling text of the notification
        CharSequence text = bundle.getString(Constants.Desriptiontext);
        Log.i(TAG, "showNotification: "+text+title);
        // What time to show on the notification
        long time = System.currentTimeMillis();
        Notification notification = new Notification(icon, text, time);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Intent resultIntent = new Intent(this, DummyActivity.class);
        resultIntent.putExtras(bundle);
        resultIntent.setAction("Action1");

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
        Bitmap bmp= BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_action_notify);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification=builder.setContentIntent(contentIntent)
                .setSmallIcon(icon)
                .setTicker(text)
                .setWhen(time)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(bmp)
                .setSound(soundUri)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000, 3000)
                .build();
        // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Send the notification to the system.
        mNM.notify(NOTIFICATION, notification);
        // Stop the service when we are finished
        stopSelf();
    }
}