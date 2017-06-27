package webservice;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.chhota.databaseexample.AppController;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import CallBackInterface.CallbackCustomMessage;
import Model.CustomMessageModel;
import Model.LoginModle;
import Model.RegisterModel;
import ProjectConstants.Constant;

/**
 * Created by chhota on 16-02-2016.
 */
public class LoginWebService {

    public static void logIn(LoginModle loginModle, final CallbackCustomMessage callback){


        String url= Constant.loginWebService;
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject= new JSONObject(new Gson().toJson(loginModle,LoginModle.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LogInWebService", response.toString());
                        callback.onSuccess(new Gson().fromJson(response.toString(), CustomMessageModel.class));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("LogInWebService", "Error: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, "LogInWeb");
    }


    public static void register(RegisterModel registerModel, final CallbackCustomMessage callback){


        String url= Constant.REGISTER_WEBSERVICE;
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject= new JSONObject(new Gson().toJson(registerModel,RegisterModel.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RegisterModel", response.toString());
                        callback.onSuccess(new Gson().fromJson(response.toString(), CustomMessageModel.class));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("RegisterModel", "Error: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, "LogInWeb");
    }
}
