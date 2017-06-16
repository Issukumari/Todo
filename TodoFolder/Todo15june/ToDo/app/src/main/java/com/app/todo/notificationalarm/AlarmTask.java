package com.app.todo.notificationalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;




public class AlarmTask implements Runnable{
    private final Calendar date;
    private final AlarmManager am;
    private final Context context;
    Bundle bundle;

    public AlarmTask(Context context, Bundle bun, Calendar date) {
        this.bundle=bun;
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
    }

    @Override
    public void run() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
    }
}