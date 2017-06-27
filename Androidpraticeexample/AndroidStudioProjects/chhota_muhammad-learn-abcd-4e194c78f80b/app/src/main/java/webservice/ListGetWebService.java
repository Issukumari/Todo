package webservice;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.chhota.databaseexample.AppController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import CallBackInterface.CallbackDownloadImage;
import CallBackInterface.CallbackImageList;
import Model.ImageModel;
import ProjectConstants.AppMessage;
import ProjectConstants.Constant;
import Utilities.DownloadImageBitmap;
import Utilities.StringUtilities;
import Utilities.WebClientGet;

/**
 * Created by chhota on 10-02-2016.
 */
public class ListGetWebService {
    /*public static void getImageList(final CallbackImageList callbackImageList){
        WebClientGet webClientGet=null;
        try {
            URL url = new URL(Constant.getlistWebService);
            webClientGet=new WebClientGet(url){
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    if(result!=null) {
                        final Type countryCodeType = new TypeToken<ArrayList<ImageModel>>() {
                        }.getType();
                        ArrayList<ImageModel> modelArrayList = new Gson().fromJson(result, countryCodeType);
                        callbackImageList.onSuccess(modelArrayList);
                    }
                    else{
                        callbackImageList.onError(AppMessage.WEB_SERVICE_RESPONSE_NULL);
                    }
                }
            };
            webClientGet.execute();
        }
        catch (Exception exception){
            Log.e("WS ->ListGetWebService", exception.getMessage());
        }
    }*/


    public static void getImageList(final CallbackImageList callbackImageList){
        String  tag_string_req = "string_req";

        String url = Constant.getlistWebService;
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Log.d(TAG, response.toString());
                        final Type imageModelList = new TypeToken<ArrayList<ImageModel>>() {
                        }.getType();
                        ArrayList<ImageModel> modelArrayList = new Gson().fromJson(response.toString(), imageModelList);
                        callbackImageList.onSuccess(modelArrayList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                callbackImageList.onError(error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req, "tag_json_arry");
    }

    public static void getImageBitma(String imagePath, final CallbackDownloadImage callback)throws  Exception{
             DownloadImageBitmap downloadImageBitmap=null;
            URL url = new URL(Constant.ip+imagePath);
             downloadImageBitmap=new DownloadImageBitmap(url){
            @Override
            protected void onPostExecute(byte[] result){
                if (result!=null){
                    callback.onSuccess(result);
                }
                else{
                    callback.onError(AppMessage.WEB_SERVICE_RESPONSE_NULL);
                }
            }
        };
       downloadImageBitmap.execute();
    }
}
