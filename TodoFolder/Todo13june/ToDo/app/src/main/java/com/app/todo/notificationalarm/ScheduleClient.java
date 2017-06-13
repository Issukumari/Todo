package com.app.todo.notificationalarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import java.util.Calendar;

public class ScheduleClient {
    private ScheduleService mBoundService;
    private Context mContext;
    private boolean mIsBound;

    public ScheduleClient(Context context) {
        mContext = context;
    }
    public void doBindService() {
        mContext.bindService(new Intent(mContext, ScheduleService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((ScheduleService.ServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };
    public void setAlarmForNotification(Bundle bundle, Calendar c){
        mBoundService.setAlarm(bundle,c);
    }
    public void doUnbindService() {
        if (mIsBound) {
            mContext.unbindService(mConnection);
            mIsBound = false;
        }
    }
}