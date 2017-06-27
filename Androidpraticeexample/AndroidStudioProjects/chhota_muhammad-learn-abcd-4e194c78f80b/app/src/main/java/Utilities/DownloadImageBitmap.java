package Utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by chhota on 11-02-2016.
 */
public class DownloadImageBitmap extends AsyncTask<Void,Void,byte[]> {
    URL url;
    public  DownloadImageBitmap(URL urlToConnect){
        this.url=urlToConnect;
    }
    @Override
    protected byte[]  doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        byte[] result = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            try {

                //Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(urlConnection.getInputStream()));
                result = getBitmapAsByteArray(BitmapFactory.decodeStream(new BufferedInputStream(urlConnection.getInputStream())));
                //result = StringUtilities.convertStreamToString(in);
            } catch (IOException ioException) {
                Log.e("WebClientGet", ioException.getMessage());
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

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
