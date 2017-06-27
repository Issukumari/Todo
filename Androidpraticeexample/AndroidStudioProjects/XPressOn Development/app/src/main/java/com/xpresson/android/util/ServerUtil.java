package com.xpresson.android.util;

import com.xpresson.mobutil.android.*;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import android.util.Log;


public class ServerUtil
{
   public static String Tag = XONPropertyInfo.XON_TAG;
   
   public static InputStream makeServerRequest(String url, String servletName, 
                                               XONServerRequestHolder serverReq)
   {
      URL serverURL = null, urlContext = null;
      try
      {
         urlContext = new URL(url);
         serverURL = new URL(urlContext, servletName);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         throw new RuntimeException("Unable to create to the Server URL");
      }
      //RGPTLogger.logToFile("Server URL Stream: " + serverURL);
      return makeServerRequest(serverURL, serverReq);
   }
   
   // This is to Retry Server Calls once an exception is thrown from Server. 
   private static boolean m_RetryServerCall = false;
   // This is the Wait Time in Milliseconds before Retry 
   private static int m_RetryWaitTime = 5000;
   
   public static InputStream makeServerRequest(URL serverURL, 
                                               XONServerRequestHolder serverReq)
   {
      try
      {
         Log.i(Tag, "Server Request: " + serverReq.toString());
         Log.i(Tag,"Server URL Derived: " + serverURL.toString());
         URLConnection serverConn = serverURL.openConnection();
         serverConn.setDoInput(true);
         serverConn.setDoOutput(true);
         serverConn.setConnectTimeout(0);
         serverConn.setUseCaches(false);
         serverConn.setReadTimeout(0);
         serverConn.setRequestProperty(
            "Content-Type",
            "application/x-java-serialized-object");
         
         // Serialise Server Request and write it to the Server Connection Stream
         ObjectOutputStream objStream = null;
         objStream = new ObjectOutputStream(serverConn.getOutputStream()); 
         serverReq.save(objStream);
         objStream.close();
         
         // Response Steam from the server
         InputStream serverRespStream = serverConn.getInputStream();
         
         // Resetting the Server Call
         m_RetryServerCall = false;
         
         // Read the Response from the server connection stream
         Log.i(Tag, "Server Response Received.");
         return serverRespStream;
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         if (!m_RetryServerCall)
         {
            m_RetryServerCall = true;
            try {
               Thread.sleep(m_RetryWaitTime);
               return makeServerRequest(serverURL, serverReq);
            }
            catch (Exception e) {}
         }
         else m_RetryServerCall = false;
         throw new RuntimeException("Unable to retrieve response from Server " +
                                    ex.getMessage()); 
      }
   }
}
