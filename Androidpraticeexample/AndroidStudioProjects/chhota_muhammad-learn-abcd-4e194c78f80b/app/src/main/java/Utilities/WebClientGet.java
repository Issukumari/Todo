package Utilities;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chhota on 10-02-2016.
 */
public class WebClientGet extends AsyncTask<Void,Void,String> {
    URL url;
    public  WebClientGet(URL urlToConnect){
        this.url=urlToConnect;
    }
    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            try {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = StringUtilities.convertStreamToString(in);
            } catch (IOException ioException) {
                Log.e("WebClientGet",ioException.getMessage());
            }
        }
        catch (Exception exception){
            Log.e("WebClientUrlConnection", exception.getMessage());
        }
        finally {
            if (urlConnection!=null)
                urlConnection.disconnect();
        }
        return result;
    }
}
