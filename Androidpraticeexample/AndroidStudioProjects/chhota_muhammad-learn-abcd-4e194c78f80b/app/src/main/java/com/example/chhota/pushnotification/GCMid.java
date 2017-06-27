package com.example.chhota.pushnotification;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.chhota.databaseexample.AppController;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import CallBackInterface.CallBackGCMid;
import Model.CustomMessageModel;
import Model.GCMModel;
import Model.LoginModle;
import ProjectConstants.Constant;

/**
 * Created by chhota on 11-02-2016.
 */
public class GCMid {
    Context context;
    private GoogleCloudMessaging googleCloudMessaging;
    static String registrationId;
    SharedPreferences sharedpreferences;
    public GCMid(Context context){
        this.context=context;
        sharedpreferences = context.getSharedPreferences(Constant.LoginSharedPref, Context.MODE_PRIVATE);
    }
    public String getRegistrationId(){
        registerInBackground(new CallBackGCMid() {
            @Override
            public void onSuccess(String regId) {

                subscribeToPushNotifications(regId);
                storeRegistrationId(regId);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context,error,Toast.LENGTH_LONG).show();
            }
        });
        return registrationId;
    }

    private void subscribeToPushNotifications(String regId) {
        if(!regId.equals(sharedpreferences.getString(Constant.GCM_Id, "Id"))){
            String url= Constant.GCM_ID_STORE;
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject= new JSONObject(new Gson().toJson(new GCMModel(sharedpreferences.getString(Constant.EMAIL,"EMAIL"),
                        sharedpreferences.getString(Constant.NAME,"NAME"),sharedpreferences.getString(Constant.GCM_Id,regId)
                ), GCMModel.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjReq, "LogInWeb");
        }
    }

    private void storeRegistrationId(String regId) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.GCM_Id, regId);
        editor.commit();
    }

    private void registerInBackground(final CallBackGCMid callBack) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "",regId=null;

                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = googleCloudMessaging.register(Constant.PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP, so it
//                    // can use GCM/HTTP or CCS to send messages to your app.
//                    Handler h = new Handler(activity.getMainLooper());
//                    h.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            subscribeToPushNotifications(regId);
//                        }
//                    });

                    callBack.onSuccess(regId);
                    //getRegistartionIdInterface.getRegistrationId(regId, true);

                    //storeRegistrationId(regId);
                    // subscribeToPushNotifications(regId);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    callBack.onError(ex.getMessage());
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //Log.i(TAG, msg + "\n");
            }
        }.execute(null, null, null);
    }
}
