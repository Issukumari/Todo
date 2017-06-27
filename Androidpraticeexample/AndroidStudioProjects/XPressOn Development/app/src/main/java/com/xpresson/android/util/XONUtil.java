package com.xpresson.android.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import com.xpresson.android.R;
import com.xpresson.android.util.XONPropertyInfo.ImageOrientation;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;


public class XONUtil
{
   public static String DEBUG_TAG = XONPropertyInfo.XON_TAG;
   public static int POINT_SIZE = 80;
   
   @TargetApi(11)
   public static void enableStrictMode() 
   {
      if (XONUtil.hasGingerbread()) {
         StrictMode.ThreadPolicy.Builder threadPolicyBuilder = null;
         threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
         StrictMode.VmPolicy.Builder vmPolicyBuilder = null;
         vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();
         if (XONUtil.hasHoneycomb()) { 
            threadPolicyBuilder.penaltyFlashScreen();
//            vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class, 1).
//                            setClassInstanceLimit(ImageDetailActivity.class, 1);
            
         }         
         StrictMode.setThreadPolicy(threadPolicyBuilder.build());
         StrictMode.setVmPolicy(vmPolicyBuilder.build());
      }
   }
   
   public static boolean hasFroyo() 
   {
      // Can use static final constants like FROYO, declared in later versions
      // of the OS since they are inlined at compile time. This is guaranteed behavior.
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
   }

   public static boolean hasGingerbread() 
   {
         return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
   }

   public static boolean hasHoneycomb() 
   {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
   }

   public static boolean hasHoneycombMR1() 
   {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
   }

   public static boolean hasJellyBean() 
   {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
   }

   public static boolean hasJellyBeanMR1() 
   {
      return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
   }

   public static int getDeviceMemory(Context context) 
   {
      int memClass = ((ActivityManager) context.getSystemService(
                                        Context.ACTIVITY_SERVICE)).getMemoryClass();
      return memClass * 1024 * 1024;
   }

   /**
    * A hashing method that changes a string (like a URL) into a hash suitable for using as a
    * disk filename.
    */
   public static String hashKeyForDisk(String key) 
   {
       String cacheKey;
       try {
           final MessageDigest mDigest = MessageDigest.getInstance("MD5");
           mDigest.update(key.getBytes());
           cacheKey = bytesToHexString(mDigest.digest());
       } catch (NoSuchAlgorithmException e) {
           cacheKey = String.valueOf(key.hashCode());
       }
       return cacheKey;
   }

   private static String bytesToHexString(byte[] bytes) 
   {
       // http://stackoverflow.com/questions/332079
       StringBuilder sb = new StringBuilder();
       for (int i = 0; i < bytes.length; i++) {
           String hex = Integer.toHexString(0xFF & bytes[i]);
           if (hex.length() == 1) {
               sb.append('0');
           }
           sb.append(hex);
       }
       return sb.toString();
   }

   /**
    * Get the size in bytes of a bitmap.
    * @param bitmap
    * @return size in bytes
    */
   @TargetApi(12)
   public static int getBitmapSize(Bitmap bitmap) 
   {
       if (XONUtil.hasHoneycombMR1()) {
           return bitmap.getByteCount();
       }
       // Pre HC-MR1
       return bitmap.getRowBytes() * bitmap.getHeight();
   }

   /**
    * Get a usable cache directory (external if available, internal otherwise).
    *
    * @param context The context to use
    * @param uniqueName A unique directory name to append to the cache dir
    * @return The cache dir
    */
   @SuppressWarnings("null")
   public static File getDiskCacheDir(Context context, String uniqueName) 
   {
       // Check if media is mounted or storage is built-in, if so, try and use external cache dir
       // otherwise use internal cache dir
       String cachePath = null;
       if (hasJellyBeanMR1()) cachePath = context.getCacheDir().getPath();
       else cachePath = (Environment.MEDIA_MOUNTED.
    		   			 			 equals(Environment.getExternalStorageState()) 
                       	 || !isExternalStorageRemovable()) ? 
                       	 getExternalCacheDir(context).getPath() : 
                         context.getCacheDir().getPath();

       if (uniqueName != null || uniqueName.length() > 0) 
          return new File(cachePath + File.separator + uniqueName);
       return new File(cachePath);
   }

   /**
    * Check if external storage is built-in or removable.
    *
    * @return True if external storage is removable (like an SD card), false
    *         otherwise.
    */
   @TargetApi(9)
   public static boolean isExternalStorageRemovable() 
   {
       if (hasGingerbread()) return Environment.isExternalStorageRemovable();
       return true;
   }

   /**
    * Get the external app cache directory.
    *
    * @param context The context to use
    * @return The external cache dir
    */
   @TargetApi(8)
   public static File getExternalCacheDir(Context context) 
   {
       if (hasFroyo()) return context.getExternalCacheDir();

       // Before Froyo we need to construct the external cache dir ourselves
//       final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
//       return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
       return getDiskCacheDir(context, null);
   }

   /**
    * Check how much usable space is available at a given path.
    *
    * @param path The path to check
    * @return The space available in bytes
    */
   @TargetApi(9)
   public static long getUsableSpace(File path) 
   {
       if (hasGingerbread()) return path.getUsableSpace();
       final StatFs stats = new StatFs(path.getPath());
       return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
   }

   // Fetch screen height and width
   public static Dimension getScreenDimension(Activity act)
   {
      final DisplayMetrics displayMetrics = new DisplayMetrics();
      act.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      final int height = displayMetrics.heightPixels;
      final int width = displayMetrics.widthPixels;
      return new Dimension(width, height);
   }
   
   public static String getFilePath(Uri uri)
   {
      return getFilePath(XONPropertyInfo.m_MainActivity, uri);
   }
   
   public static String getFilePath(Activity act, Uri uri)
   {
      String filePath = "";
      // OI FILE Manager
      String filemanagerstring = uri.getPath();
      // MEDIA GALLERY
      String selectedImagePath = getPath(act, uri);
      if (selectedImagePath != null) filePath = selectedImagePath;
      else if (filemanagerstring != null) filePath = filemanagerstring;
      return filePath;
   }
   
   @SuppressWarnings("deprecation")
   public static String getPath(Activity act, Uri uri) 
   {
      String imgProj = MediaStore.Images.Media.DATA; String[] projection = { imgProj };
      Cursor cursor = null;
      if (hasHoneycomb()) {
         cursor = act.getApplicationContext().
                      getContentResolver().query(uri, projection, null, null, null);
      }
      else cursor = act.managedQuery(uri, projection, null, null, null);
      if (cursor == null) return null;
      // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
      // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
      int column_index = cursor.getColumnIndexOrThrow(imgProj);
      cursor.moveToFirst();
      return cursor.getString(column_index);
   }

   public static ImageOrientation getOrientation(String filePath) 
   {
      try {
         ExifInterface exif = new ExifInterface(filePath); 
         int orient = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 
                                           ExifInterface.ORIENTATION_NORMAL);   
         ImageOrientation imgOrient = ImageOrientation.Normal;
         if (orient == ExifInterface.ORIENTATION_ROTATE_90) 
            imgOrient = ImageOrientation.Rot90;
         else if (orient == ExifInterface.ORIENTATION_ROTATE_180)
            imgOrient = ImageOrientation.Rot180;
         else if (orient == ExifInterface.ORIENTATION_ROTATE_270)
            imgOrient = ImageOrientation.Rot270;
         return imgOrient;
      } catch(Exception ex) { return ImageOrientation.Normal; }
   }
   
   public static ImageOrientation getOrientation(Uri photoUri) 
   {
      return getOrientation(XONPropertyInfo.m_MainActivity.getApplicationContext(), 
                            photoUri);
   }
   
   public static ImageOrientation getOrientation(Context context, Uri photoUri) 
   {
      String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };
      /* it's on the external media. */
      Cursor cursor = context.getContentResolver().
                              query(photoUri, projection, null, null, null);
      if (cursor.getCount() != 1) return ImageOrientation.Normal;
      
      cursor.moveToFirst();
      int orient = cursor.getInt(0); ImageOrientation imgOrient = ImageOrientation.Normal;
      if (orient == ExifInterface.ORIENTATION_ROTATE_90) 
         imgOrient = ImageOrientation.Rot90;
      else if (orient == ExifInterface.ORIENTATION_ROTATE_180)
         imgOrient = ImageOrientation.Rot180;
      else if (orient == ExifInterface.ORIENTATION_ROTATE_270)
         imgOrient = ImageOrientation.Rot270;
      return imgOrient;
   }   
   
   public static void setExifInfo(String origFilePath, String newFilePath, 
                                  int newWt, int newHt, int orientation)
   {
      try {
         // copy paste exif information from original file to new file
         ExifInterface oldexif = new ExifInterface(origFilePath);
         ExifInterface newexif = new ExifInterface(newFilePath);
         
         int build = Build.VERSION.SDK_INT;
        
         // From API 11
         if (build >= 11) {
            if (oldexif.getAttribute("FNumber") != null) 
               newexif.setAttribute("FNumber", oldexif.getAttribute("FNumber"));
            if (oldexif.getAttribute("ExposureTime") != null) 
               newexif.setAttribute("ExposureTime", oldexif.getAttribute("ExposureTime"));
            if (oldexif.getAttribute("ISOSpeedRatings") != null) 
               newexif.setAttribute("ISOSpeedRatings", 
                                    oldexif.getAttribute("ISOSpeedRatings"));
         }
         // From API 9
         if (build >= 9) {
            if (oldexif.getAttribute("GPSAltitude") != null) 
               newexif.setAttribute("GPSAltitude", oldexif.getAttribute("GPSAltitude"));
            if (oldexif.getAttribute("GPSAltitudeRef") != null) 
               newexif.setAttribute("GPSAltitudeRef", 
                                    oldexif.getAttribute("GPSAltitudeRef"));
         }
         // From API 8
         if (build >= 8) {
            if (oldexif.getAttribute("FocalLength") != null) 
               newexif.setAttribute("FocalLength", oldexif.getAttribute("FocalLength"));
            if (oldexif.getAttribute("GPSDateStamp") != null) 
               newexif.setAttribute("GPSDateStamp", oldexif.getAttribute("GPSDateStamp"));
            if (oldexif.getAttribute("GPSProcessingMethod") != null) 
               newexif.setAttribute("GPSProcessingMethod",
                                    oldexif.getAttribute("GPSProcessingMethod"));
            if (oldexif.getAttribute("GPSTimeStamp") != null) 
               newexif.setAttribute("GPSTimeStamp", 
                                    ""+oldexif.getAttribute("GPSTimeStamp"));
         }  
         if (oldexif.getAttribute("DateTime") != null) 
            newexif.setAttribute("DateTime", oldexif.getAttribute("DateTime"));
         if (oldexif.getAttribute("Flash") != null) 
            newexif.setAttribute("Flash", oldexif.getAttribute("Flash"));
         if (oldexif.getAttribute("GPSLatitude") != null) 
            newexif.setAttribute("GPSLatitude", oldexif.getAttribute("GPSLatitude"));
         if (oldexif.getAttribute("GPSLatitudeRef") != null) 
            newexif.setAttribute("GPSLatitudeRef", 
                                 oldexif.getAttribute("GPSLatitudeRef"));
         if (oldexif.getAttribute("GPSLongitude") != null) 
            newexif.setAttribute("GPSLongitude", oldexif.getAttribute("GPSLongitude"));
         if (oldexif.getAttribute("GPSLatitudeRef") != null) 
            newexif.setAttribute("GPSLongitudeRef", 
                                 oldexif.getAttribute("GPSLongitudeRef"));
         //Need to update it, with your new height width
         newexif.setAttribute("ImageLength", Integer.toString(newWt));
         newexif.setAttribute("ImageWidth", Integer.toString(newHt));
   
         if (oldexif.getAttribute("Make") != null) 
            newexif.setAttribute("Make", oldexif.getAttribute("Make"));
         if (oldexif.getAttribute("Model") != null) 
            newexif.setAttribute("Model", oldexif.getAttribute("Model"));
//         if (oldexif.getAttribute("Orientation") != null) 
            newexif.setAttribute("Orientation", String.valueOf(orientation)); 
         if (oldexif.getAttribute("WhiteBalance") != null) 
            newexif.setAttribute("WhiteBalance", oldexif.getAttribute("WhiteBalance"));
         newexif.saveAttributes();
      } catch (Exception ex) {
         logError("Unabe to save image attributes", ex);
      }
   }
   
   public static void logDebugMesg()
   {
      if (XONPropertyInfo.DEBUG) debugLog(Log.DEBUG, 2, "");
   }
   
   public static void logDebugMesg(String logMesg)
   {
      if (XONPropertyInfo.DEBUG) debugLog(Log.DEBUG, 2, logMesg);
   }
   
   public static void logErrorMesg(String logMesg)
   {
      debugLog(Log.ERROR, 2, logMesg);
   }
   
   public static void logError(String logMesg, Throwable ex)
   {
      StringBuffer mesg = new StringBuffer(logMesg+" Exception Mesg: "+ex.getMessage());
      mesg.append("\n"+stackTraceToString(ex));
      debugLog(Log.ERROR, 2, mesg.toString());
   }
   
   public static String stackTraceToString(Throwable ex)
   {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(byteStream);
      ex.printStackTrace(printStream);
      printStream.flush();
      String stackTrace = byteStream.toString();
      printStream.close();
      return stackTrace;
   }
 
   public static void debugLog(int priority, String logMesg)
   {
      debugLog(priority, 2, logMesg);
   }
   
   public static void debugLog(int priority, int classIndex, String logMesg)
   {
      final Throwable t = new Throwable();
      final StackTraceElement[] methodCallers = t.getStackTrace();      
      String fname = methodCallers[classIndex].getFileName(); 
      String mthName = methodCallers[classIndex].getMethodName();
      int index = fname.lastIndexOf('.');
      fname = fname.substring(0,index);      
      String mesg = fname + "." + mthName;
      if (logMesg.length() > 0) mesg = mesg + ": " + logMesg;
      Log.println(priority, DEBUG_TAG, mesg);
   }
   
   public static String replaceSpace(String original, String delim)
   {
      StringBuffer httpTxt = new StringBuffer();
      String[] words = original.split(" ");
      for (int i = 0; i < words.length; i++) {        
         httpTxt.append(words[i]);
         if (i < words.length-1) httpTxt.append(delim);
      }
      return httpTxt.toString();
   }
    
   public static String[] objToStringArray(Object[] objValues)
   {
      return objToStringArray(objValues, null);
   }
   
   public static String[] objToStringArray(Object[] objValues, String ignoreVal)
   {
      String strVal = ""; Vector<String> strValueList = new Vector<String>(); 
      for (int i = 0; i<objValues.length; i++) {
         strVal = objValues[i].toString();
         if (ignoreVal == null) { strValueList.addElement(strVal); continue; }
         if (strVal.contains(ignoreVal)) continue; 
         strValueList.addElement(strVal); 
      }
      return strValueList.toArray(new String[0]);
   }
   
   public static Vector<String> createVector(String[] array) 
   { 
      return (new Vector<String>(Arrays.asList(array)));
   }
   
   public static void put2Sleep()
   {
      put2Sleep(1);
   }
   
   public static int put2Sleep(int multiples)
   {
      int sleepTime = multiples*XONPropertyInfo.ThreadSleepTime;
      try { Thread.sleep(sleepTime); } 
      catch(Exception ex) {}
      return sleepTime;
   }
   
   public static long getCurrentTime()
   {
      return Calendar.getInstance().getTime().getTime();
   }
   
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
      debugLog(Log.VERBOSE, "Ext Storage Avail: "+extStorageAvailable+", Ext Storage Writable: "+
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
      debugLog(Log.VERBOSE, "dirName:"+dirName);
      File assetFile = new File(Environment.getExternalStorageDirectory(), dirName);
      debugLog(Log.VERBOSE, "File Set Path:"+assetFile.getPath());
      if (!assetFile.exists()) {
         assetFile.mkdir();
      }
      debugLog(Log.VERBOSE, "Asset File Exists:"+assetFile.exists());
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
         debugLog(Log.ERROR, "Calc ByteCnt: "+byteCount+", Code Name: "+Build.VERSION.CODENAME+
                    ", Release: "+Build.VERSION.RELEASE);
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) 
//         { byteCount = bitmap.getByteCount(); debugLog(Log.VERBOSE,  "byteCnt: "+byteCount);  }
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
   
   public static Rect createRect(int centX, int centY)
   {
      int size = XONPropertyInfo.getIntRes(R.string.CircularImpactSize);
      return createRect(centX, centY, size, size);
   }
   
   public static Rect createRect(int centX, int centY, int width, int height)
   {
      logDebugMesg("centX: "+centX+" centY: "+centY+" Wt: "+width+" Ht:"+height);
      int left = centX-Math.round(width/2), top = centY-Math.round(height/2);
      return new Rect(left, top, left+width, top+height);
   }
   
   // Return angle in radians
   public static double getAngle(Point vertPt, Point edgePt) 
   {
      // if loop is 1st and 4th qudrant, else loop is 2nd and 3rd quadrant
      double dx = Math.abs(edgePt.x - vertPt.x), dy = Math.abs(edgePt.y - vertPt.y);
      if (dx == 0.0D) {
         if (edgePt.y > vertPt.y) return Math.PI/2.0D; else return 3*Math.PI/2.0D;
      } else if (dy == 0.0D) { if(edgePt.x > vertPt.x) return 0.0D; else return Math.PI; }
      if(edgePt.x > vertPt.x) {
         // If loop is 1st Quadrant and else is 4th Quadrant
         if (edgePt.y > vertPt.y) return Math.atan(dy/dx);
         else return (3*Math.PI/2.0D + Math.atan(dy/dx));
      } else {
         // If loop is 2nd Quadrant and else is 3rd Quadrant
         if (edgePt.y > vertPt.y) return (Math.PI/2.0D + Math.atan(dx/dy));
         else return (Math.PI + Math.atan(dy/dx));
      }
   }
   
   public static Point transformPoint(Matrix matrix, Point origPt)
   {
      float[] pts = { origPt.x, origPt.y };
      // Use the Matrix to map the points
      matrix.mapPoints(pts);
      // Now, create a new Point from our new coordinates
      Point newPoint = new Point(Math.round(pts[0]), Math.round(pts[1]));
      return newPoint;
   }
   
   /**
   * Add the Rotation to our Transform matrix.
   * 
   * A new point, with the rotated coordinates will be returned
   * @param degrees
   * @return
   */
   public static Point rotate(Matrix transform, Rect rect, Point origPt, float degrees)
   {
       // This is to rotate about the Rectangles center
      if (transform == null) transform = new Matrix();
      transform.setRotate(degrees, rect.exactCenterX(), rect.exactCenterY());

      // Create new float[] to hold the rotated coordinates
      float[] pts = new float[2];

      // Initialize the array with our Coordinate
      pts[0] = origPt.x;
      pts[1] = origPt.y;

      // Use the Matrix to map the points
      transform.mapPoints(pts);

      // NOTE: pts will be changed by transform.mapPoints call
      // after the call, pts will hold the new cooridnates

      // Now, create a new Point from our new coordinates
      Point newPoint = new Point(Math.round(pts[0]), Math.round(pts[1]));

      // Return the new point
      return newPoint;
   }

   // Serializes the Object. 
   public static void serializeObject(String absFilePath, Object obj) throws Exception
   {
      FileOutputStream fileStream = null;
      ObjectOutputStream objStream = null;
      try {
         fileStream = new FileOutputStream(absFilePath);
         objStream = new ObjectOutputStream(fileStream); 
         objStream.writeObject(obj);
      }
      finally
      {
         if(objStream != null) objStream.close();
         if (fileStream != null) fileStream.close();
      }
   }
   
   // Retrieve Object from Serialized File
   public static Object getSerializeObject(String absFilePath) throws Exception
   {
      Object obj = null;
      FileInputStream fileStream = null;
      ObjectInputStream objStr = null;
      try {
         fileStream =  new FileInputStream(absFilePath);
         objStr = new ObjectInputStream(fileStream);
         obj = objStr.readObject();
      }
      finally
      {
         if(objStr != null) objStr.close();
         if (fileStream != null) fileStream.close();
      }
      return obj;
   }   
   
   // Retrieve Object from Serialized File
   public static Object getSerializeObject(Activity act, int resId) throws Exception
   {
      Object obj = null;
      InputStream inputStream = null; ObjectInputStream objStr = null;
      try {
         inputStream = act.getResources().openRawResource(resId);
         objStr = new ObjectInputStream(inputStream);
         obj = objStr.readObject();
      }
      finally
      {
         XONUtil.logDebugMesg("Closing Streams for res: "+resId);
         if(objStr != null) objStr.close();
         if (inputStream != null) { inputStream.close(); }
      }
      return obj;
   }   
   
   // This function takes the file Name with extension and returns file name 
   // without extension
   public static String getFileName(String file) 
   {
      String fileName = file;
      int i = file.lastIndexOf('.');
      if (i > 0 &&  i < file.length() - 1) {
         fileName = file.substring(0, i);
      }
      int j = fileName.lastIndexOf("/");
      if (j > 0 &&  j < fileName.length() - 1) {
         fileName = fileName.substring(j+1, fileName.length());
      }
      return fileName;
   }
   
   public static int getColor(String colStr)
   {
      // This will convertString string = "#FFFF0000" to int color = 0xFFFF0000;
      // The 16 means it is hexadecimal and not your regular 0-9. 
      int color = Integer.parseInt(colStr.replaceFirst("^#",""), 16);
      return color;
   }
   
   public static void deleteFiles(File file) 
   {
      if (file == null || !file.exists()) return; 
      file.delete(); if (!file.exists()) return;
      logDebugMesg("Delete exec Runtime Command");
      String deleteCmd = "rm -r " + file.getAbsolutePath();
      Runtime runtime = Runtime.getRuntime();
      try { runtime.exec(deleteCmd); } catch (Exception e) {}
   }
   
   public static double getDistance(float x1, float y1, float x2, float y2)
   {
      double dx = x2-x1, dy = y2-y1;
      logDebugMesg("dx: "+dx+" dy: "+dy);
      return Math.sqrt( dx*dx + dy*dy );
   }
   
   public static int[] toArray(Vector<Integer> vector) 
   {
      return toIntArray(Arrays.asList(vector.toArray(new Integer[0])));
   }
   
   public static int[] toIntArray(java.util.List<Integer> list)  
   {
      int[] ret = new int[list.size()]; int i = 0;
      for (Integer e : list)  ret[i++] = e.intValue();
      return ret;
   }   
   
   public static Vector<Integer> createVector(int[] array) 
   { 
      return (new Vector<Integer>(intArrayAsList(array)));
   }
   
   //Helper method to convert int arrays into Lists
   public static java.util.List<Integer> intArrayAsList(final int[] a) 
   {
      if(a == null) throw new NullPointerException();
      return new AbstractList<Integer>() {
           @Override
           public Integer get(int i) {
               return a[i];//autoboxing
           }
           @Override
           public Integer set(int i, Integer val) {
               final int old = a[i];
               a[i] = val;//auto-unboxing
               return old;//autoboxing
           }
           @Override
           public int size() {
               return a.length;
           }
       };
   }   

   public static int contains(Vector<Point> pts, int x, int y)
   {
      Point pt = null; Rect ptRect = null; int index = -1;
      for (int i = 0; i < pts.size(); i++) {
         pt = pts.elementAt(i); ptRect = createRect(pt.x, pt.y, POINT_SIZE, POINT_SIZE);
         if (ptRect.contains(x, y)) { index = i; break; }
         pt = null;
      }
      return index;
   }
   
   // Creates a new Rect by accommodating the translation in x and y dirn
   public static Rect createRect(Vector<Point> pts, int distX, int distY)
   {
      if (pts.size() != 4) return null;
      Point topPt = pts.elementAt(0), botPt = pts.elementAt(2);
      int left = topPt.x+distX, top = topPt.y+distY; 
      int right = botPt.x+distX, bot = botPt.y+distY; 
      return new Rect(left, top, right, bot);
   }
   
   // Creates a new Rect by accommodating the scaling of a pt in x and y dirn
   public static Rect createRect(Vector<Point> pts, int selIndex, int distX, int distY)
   {
      if (pts.size() != 4) return null;
      Point topPt = pts.elementAt(0), botPt = pts.elementAt(2);
      int left = topPt.x, top = topPt.y, right = botPt.x, bot = botPt.y; 
      if (selIndex == 0) {
         left = topPt.x+distX; top = topPt.y+distY;
      } else if (selIndex == 1) {
         right = botPt.x+distX; top = topPt.y+distY;
      } else if (selIndex == 3) {
         left = topPt.x+distX;  bot = botPt.y+distY; 
      } else { right = botPt.x+distX; bot = botPt.y+distY; }
      return new Rect(left, top, right, bot);
   }
   
   // Clears the Data
   @SuppressWarnings("rawtypes")
   public static void clear(HashMap data)
   {
      if (data != null) data.clear(); data = null;
   }
   
}

