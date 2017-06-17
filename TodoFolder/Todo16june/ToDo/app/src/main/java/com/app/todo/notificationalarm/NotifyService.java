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
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.app.todo.R;
import com.app.todo.utils.Constants;

public class NotifyService extends Service {
    private static final int REQUEST_CODE = 0;
    long date = System.currentTimeMillis();
    Bundle bundle;

    private String TAG = "NotifyService";

    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    private static final int NOTIFICATION = 123;
    public static final String INTENT_NOTIFY = "com.blundell.tut.service.INTENT_NOTIFY";
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        bundle = intent.getExtras();
        if (intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    private void showNotification() {
        CharSequence title = bundle.getString(Constants.Titletext);
        int icon = R.drawable.ic_notes;
        CharSequence text = bundle.getString(Constants.Desriptiontext);
        Log.i(TAG, "showNotification: " + text + title);
        long time = System.currentTimeMillis();
        Notification notification = new Notification(icon, text, time);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this);
        Intent resultIntent = new Intent(this, ReminderActivityNotification.class);
        resultIntent.putExtras(bundle);
        resultIntent.setAction("Action1");
        builder.setStyle(new NotificationCompat.InboxStyle());
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.alarmbell);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification = builder.setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon).setWhen(date)
                .setTicker(text)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(contentIntent)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSound(soundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setLargeIcon(bmp)
                .build();
        mNM.notify(NOTIFICATION, notification);
        stopSelf();
    }
}