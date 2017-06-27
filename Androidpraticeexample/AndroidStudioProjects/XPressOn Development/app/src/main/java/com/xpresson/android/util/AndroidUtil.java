package com.xpresson.android.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

import android.os.Build;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


public class AndroidUtil
{
   public static String Tag = XONPropertyInfo.XON_TAG;
   
   // Get Date is specified Format. If data format is not specified then the 
   // yyyy.MM.dd'-'HH:mm:ss Format is used.
   public static String getCurrentDate(String format)
   {
      if (format == null || format.length() == 0)
         format = "yyyy.MM.dd'-'HH:mm:ss";
      java.text.DateFormat dateFormat = new java.text.SimpleDateFormat(format);
      java.util.Date date = new java.util.Date();
      String currDate = dateFormat.format(date);
      return currDate;
   }
   
   public static String getMyPhoneNumber(Context context)
   {
      TelephonyManager telMgr;
      telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
      return telMgr.getLine1Number();
   }

   public static boolean isExternalStoragePresent() {

      boolean extStorageAvailable = false;
      boolean extStorageWritable = false;
      String state = Environment.getExternalStorageState();

      if (Environment.MEDIA_MOUNTED.equals(state)) {
          // We can read and write the media
          extStorageAvailable = extStorageWritable = true;
      } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
          // We can only read the media
          extStorageAvailable = true;
          extStorageWritable = false;
      } else {
          // Something else is wrong. It may be one of many other states, but
          // all we need
          // to know is we can neither read nor write
          extStorageAvailable = extStorageWritable = false;
      }
      Log.i(Tag, "Ext Storage Avail: "+extStorageAvailable+", Ext Storage Writable: "+
            extStorageWritable);
      return (extStorageAvailable) && (extStorageWritable);
   }
   
   public static File getTempFile(Activity act)
   {
      /** Getting Cache Directory */
      File cDir = act.getBaseContext().getCacheDir();

      /** Getting a reference to temporary file, if created earlier */
      File tempFile = new File(cDir.getPath() + "/" + XONPropertyInfo.XON_DATA_FILE);
      return tempFile;
   }
   
   public static File getXONDir(Activity act, boolean isCameraImage)
   { 
      isExternalStoragePresent(); String dirName = act.getPackageName();
      if (android.os.Environment.getExternalStorageState().
          equals(android.os.Environment.MEDIA_MOUNTED)) {
         if (isCameraImage) dirName = XONPropertyInfo.XON_CAMERA_DIR;
         else dirName = XONPropertyInfo.XON_DIR;
      }
      return createDir(dirName, true);
   }
   
   public static File createDir(String dirName, boolean addNewDirFile)
   {
      Log.i(Tag,"dirName:"+dirName);
      File assetFile = new File(Environment.getExternalStorageDirectory(), dirName);
      Log.i(Tag,"File Set Path:"+assetFile.getPath());
      if (!assetFile.exists()) {
         assetFile.mkdir();
      }
      Log.i(Tag,"Asset File Exists:"+assetFile.exists());
      return assetFile;
   }
   
   public static File getXONFilePath(Activity act, String fileName, boolean isCameraImage)
   {
      return new File(getXONDir(act, isCameraImage), fileName);
   }
      
   public static boolean saveFile(Activity act, Bitmap bitmap, String fileName)
   {
      if (fileName == null) fileName = "xon_tmp_file";
      boolean result = true; byte[] data = null;
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      String filePath = getXONDir(act, false).getPath()+File.pathSeparator+fileName;
      if (bitmap.hasAlpha()) {
         int byteCount = bitmap.getHeight()*bitmap.getWidth()*4;
         // Make sure we're running on Honeycomb or higher to use ceratain features like 
         // the ActionBar APIs
         Log.e(Tag, "Calc ByteCnt: "+byteCount+", Code Name: "+Build.VERSION.CODENAME+
                    ", Release: "+Build.VERSION.RELEASE);
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) 
//         { byteCount = bitmap.getByteCount(); Log.i(Tag, "byteCnt: "+byteCount);  }
         ByteBuffer dst = ByteBuffer.allocate(byteCount);
         bitmap.copyPixelsToBuffer(dst); filePath += ".png"; data = dst.array();         
      } else { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes); 
               data = bytes.toByteArray(); filePath += ".jpg";}
      try {
         writeToFile(filePath, data);
      } catch (Exception e) { result = false; e.printStackTrace(); }      
      return result;
   }
   
   public static File writeToFile(String absFilePath, byte[] data) throws Exception
   {
      File dataFile = new File(absFilePath);
      FileOutputStream out = null;
      if (dataFile.isDirectory()) 
         throw new RuntimeException("Pass File instead of Dir");
      try {
         out = new FileOutputStream(dataFile);
         out.write(data);
         System.out.println("Ind File Size Read: " + data.length + 
                            " Actual File Size: " + dataFile.length());
         out.flush();
         out.close();
      }
      finally {
         if (out != null) out.close();
      }
      return dataFile;
   }
            
}

