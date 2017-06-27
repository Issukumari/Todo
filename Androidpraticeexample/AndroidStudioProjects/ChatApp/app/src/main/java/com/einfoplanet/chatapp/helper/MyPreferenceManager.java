package com.einfoplanet.chatapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.einfoplanet.chatapp.model.User;

/**
 * Created by bridgeit007 on 15/6/16.
 */

public class MyPreferenceManager {
    private String TAG=MyPreferenceManager.class.getSimpleName();

    //SharedPreference
    SharedPreferences preferences;

    //SharedPreference Editor
    SharedPreferences.Editor  editor;

    //Context
    Context context;

    //SharedPreference Mode
    int PRIVATE_MODE = 0;

    //Shared Preference file mode
    private static final String PREF_NAME = "chat_app_pref";

    //All shared preferences Keys
    private static final String  KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_NOTIFICATIONS = "notifications";

    //Constructor
    public MyPreferenceManager(Context context){
        this.context=context;
        preferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=preferences.edit();
    }

    public void storeUser(User user){
        editor.putString(KEY_USER_ID,user.getId());
        editor.putString(KEY_USER_EMAIL,user.getEmail());
        editor.putString(KEY_USER_NAME,user.getName());
        editor.commit();

        Log.e(TAG,"User is stored in sharedPreference. "+user.getName()+", "+user.getEmail());
    }

    public User getUser(){
        if (preferences.getString(KEY_USER_ID,null) != null){
            String id,name,email;
            id=preferences.getString(KEY_USER_ID,null);
            name=preferences.getString(KEY_USER_EMAIL,null);
            email=preferences.getString(KEY_USER_NAME,null);

            User user=new User(id,name,email);
            return user;
        }
        return null;
    }

    public void addNotification(String notification){
        //get old notifications
        String oldNotifications =getNotifications();
        if (oldNotifications != null){
            oldNotifications += "|"+notification;
        }else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS,oldNotifications);
        editor.commit();
    }

    public String  getNotifications(){
        return preferences.getString(KEY_NOTIFICATIONS,null);
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }

}
