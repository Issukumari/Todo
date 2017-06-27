package com.xpresson.android.util;

import java.util.Map;
import java.util.Vector;

import android.util.DisplayMetrics;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xpresson.android.BuildConfig;
import com.xpresson.android.R;

public class XONPropertyInfo
{
   public final static String XON_TAG = "XPressOn";
   public final static String IMAGE_CAPTURE_PREFIX = "xon_capture_";
   public final static String XON_DIR = "XPressOn";
   public final static String XON_CAMERA_DIR = "XONCamera";
   public final static String XON_DATA_FILE = "xon_data_file.ser";
   
   // Default memory cache size
   public static final float MEM_CACHE_PERC = 0.2f;
   public static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
   public static final int MDPI_MARGIN_BOT = 45;   
   public static final int LDPI_MARGIN_BOT = 30;
   
   // Density Screens
   public static enum DensityScreenType {
      DENSITY_LOW, DENSITY_MEDIUM, DENSITY_HIGH, DENSITY_XHIGH
   }
   public static DensityScreenType m_DensityScreenType;
   
   // Screens Resolution
   public static enum ScreenResolutionType {
      RESOLUTION_ULTRA_LOW, RESOLUTION_LOW, RESOLUTION_MEDIUM, RESOLUTION_HIGH, 
      RESOLUTION_XHIGH
   }
   public static ScreenResolutionType m_ScreenResolutionType;
   
   // Compression settings when writing images to disk cache
   public final static CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
   public static int DEFAULT_COMPRESS_QUALITY = 70;
   
   public final static int MAX_BITMAP_SIZE = 1024 * 1024 * 2; // 2MB
   
   public static ProgressBar m_ProgressBar;
   public static int FilteredImageScale = 25;
   public static int IntensityAdjHt = 75;
   
   public static int[] m_ColorOptions, m_BorderColorOptions;
   public static int ThreadPoolSize = 10;
   public static int ThreadSleepTime = 500;
   public static int ScrollMonitorTime = 1000;
   
   public static boolean DEBUG = BuildConfig.DEBUG;
   public static boolean m_DevelopmentMode = false;
   public static Activity m_MainActivity, m_SubMainActivity;
   
   public static float MAX_IMAGE_SCALE = 1.5f;
   public static Dimension m_MaxImageSize;
   public static final float PERC_SIZE_RED_4_1MB = 0.2f;
   
   // This Asyncy Process Handler
   public static XONImageProcessHandler m_AsyncProcessHandler;
   
   public static enum ImageOrientation {
      Normal, Rot90, Rot180, Rot270
   }
   
   public static Vector<ImageOrientation> m_ImageOrientationPos;
   
   static {
      m_ImageOrientationPos = new Vector<ImageOrientation>();
      m_ImageOrientationPos.add(ImageOrientation.Normal);
      m_ImageOrientationPos.add(ImageOrientation.Rot90);
      m_ImageOrientationPos.add(ImageOrientation.Rot180);
      m_ImageOrientationPos.add(ImageOrientation.Rot270);
   }

   public static ImageOrientation getNext(ImageOrientation currImgOrient)
   {
      int pos = -1;
      if (m_ImageOrientationPos.lastElement().equals(currImgOrient)) pos = 0;
      else { pos = m_ImageOrientationPos.indexOf(currImgOrient); pos = pos+1; }
      return m_ImageOrientationPos.elementAt(pos);
   }
   
   public static ImageOrientation getPrev(ImageOrientation currImgOrient)
   {
      int pos = -1;
      if (m_ImageOrientationPos.firstElement().equals(currImgOrient)) 
         pos = m_ImageOrientationPos.size()-1;
      else { pos = m_ImageOrientationPos.indexOf(currImgOrient); pos = pos-1; }
      return m_ImageOrientationPos.elementAt(pos);
   }
   
   public static void resetMainAct()
   {
      if (m_MainActivity == null) return;
      m_MainActivity.finish(); m_MainActivity = null;
   }
   
   public static void resetSubMainAct()
   {
      if (m_SubMainActivity == null) return;
      m_SubMainActivity.finish(); m_SubMainActivity = null;
   }
   
   public static void populateResources(Activity act, boolean debug)
   {
      populateResources(act, debug, false);
   }
   
   public static void populateResources(Activity act, boolean debug, boolean popFltrRes)
   {      
      if (m_MainActivity == null) { 
         m_MainActivity = act;  
         XONUtil.logDebugMesg("New Activity Setup: "+ act.getCallingPackage() ); 
         setDisplayMetrics();
      }
      m_SubMainActivity = act; m_SlideMesgShown = false; m_CircularSlideMesgShown = false;
      DEBUG = debug; populateXONProperty(popFltrRes);
      m_ProgressBar = (ProgressBar) act.findViewById(R.id.progress_bar);
      if (popFltrRes) XONImageFilterDef.populateImageFilterResource(m_MainActivity);
      m_AsyncProcessHandler = null; // If mem allocated then nullify the same for GC
      m_AsyncProcessHandler = new XONImageProcessHandler(act.getApplicationContext());
      m_AsyncProcessHandler.setImageFadeIn(false);
   }
   
   private static void setDisplayMetrics()
   {
      Dimension screenSize = XONUtil.getScreenDimension(m_MainActivity);
      // Get the screen's density scale
      DisplayMetrics dispMetrics =  m_MainActivity.getResources().getDisplayMetrics();
      int density = dispMetrics.densityDpi;   
      m_MaxImageSize = new Dimension(Math.round(screenSize.width*MAX_IMAGE_SCALE), 
                                     Math.round(screenSize.height*MAX_IMAGE_SCALE));
      
      m_DensityScreenType = DensityScreenType.DENSITY_HIGH; 
      if (density > DisplayMetrics.DENSITY_HIGH) {
         DEFAULT_COMPRESS_QUALITY = 70;
         m_DensityScreenType = DensityScreenType.DENSITY_XHIGH;
      } else if (density == DisplayMetrics.DENSITY_HIGH) {
         DEFAULT_COMPRESS_QUALITY = 70;
         m_DensityScreenType = DensityScreenType.DENSITY_HIGH;
      } else if (density >= DisplayMetrics.DENSITY_MEDIUM) 
         m_DensityScreenType = DensityScreenType.DENSITY_MEDIUM;
      else if (density >= DisplayMetrics.DENSITY_LOW) 
         m_DensityScreenType = DensityScreenType.DENSITY_LOW;
      
      m_ScreenResolutionType = ScreenResolutionType.RESOLUTION_HIGH;
      if (screenSize.width > 500) 
         m_ScreenResolutionType = ScreenResolutionType.RESOLUTION_XHIGH;
      else if (screenSize.width >= 400)
         m_ScreenResolutionType = ScreenResolutionType.RESOLUTION_HIGH;
      else if (screenSize.width >= 320)
         m_ScreenResolutionType = ScreenResolutionType.RESOLUTION_MEDIUM;
      else if (screenSize.width >= 240 && screenSize.height < 400)
         m_ScreenResolutionType = ScreenResolutionType.RESOLUTION_ULTRA_LOW;
      else if (screenSize.width >= 240)
         m_ScreenResolutionType = ScreenResolutionType.RESOLUTION_LOW;
   
      XONUtil.logDebugMesg("Screen Size: "+screenSize+" density: "+density+
                           " Display Metrics: "+dispMetrics+" DensityScreenType: "
                           +m_DensityScreenType+" ScreenResolutionType: "+
                           m_ScreenResolutionType);
      
   }
   
   public static int getMainDispViewStartPt()
   {
      if (m_ScreenResolutionType == ScreenResolutionType.RESOLUTION_LOW ||
          m_ScreenResolutionType == ScreenResolutionType.RESOLUTION_ULTRA_LOW) return 5;
      if (m_ScreenResolutionType == ScreenResolutionType.RESOLUTION_XHIGH) return 50;
      return 25;
   }
   
   public static void processAsyncTack(XONImageProcessor taskProc, Map<String, Object> data)
   {
      m_AsyncProcessHandler.invokeAsyncTask(taskProc, data);
   }
   
   private static void populateXONProperty(boolean popFltrRes)
   {
      ThreadPoolSize = getIntRes(R.string.thread_pool_size);
      ThreadSleepTime = getIntRes(R.string.thread_sleep_time);
      ScrollMonitorTime = getIntRes(R.string.scroll_monitor_time);
      IntensityAdjHt = getIntRes(R.string.intensity_adj_ht);
      m_DevelopmentMode = getBoolRes(R.string.DevelopmentMode);
      if (popFltrRes) {
         if (m_ColorOptions == null) 
            m_ColorOptions = getColorOptions(R.string.colors);
         if (m_BorderColorOptions == null)
            m_BorderColorOptions = getColorOptions(R.string.border_colors);         
         XONUtil.logDebugMesg("Num of Color Options: "+m_ColorOptions.length);
         XONUtil.logDebugMesg("Num of Border Color Options: "+m_BorderColorOptions.length);
      }
   }
   
   private static SparseArray<Vector<Float>> m_FixImageSettings;
   
   private static void buildFixImageSettings()
   {
      m_FixImageSettings = new SparseArray<Vector<Float>>();
      int avgPixel = -1; 
      String[] fixImgSetProps = getStringArray(R.array.quick_fix_image_settings);
      for(int i = 0; i < fixImgSetProps.length; i++) {
         Vector<Float> settings = new Vector<Float>();
         String[] fixImgSetLists = fixImgSetProps[i].split("::");
         for(int j = 0; j < fixImgSetLists.length; j++) {
            String[] fixImgSet = fixImgSetLists[j].split(":=");
            String key = fixImgSet[0], val = fixImgSet[1];
            if (key.equals("AvgPixel")) avgPixel = Integer.valueOf(val);
            else settings.addElement(Float.valueOf(val));
         }
         m_FixImageSettings.put(avgPixel, settings);
      }
   }
   
   public static Dimension getThumbSize()
   {
      int iconWt = XONPropertyInfo.getIntRes(R.string.icon_width); 
      int iconHt = XONPropertyInfo.getIntRes(R.string.icon_height); 
      if (m_ScreenResolutionType.equals(ScreenResolutionType.RESOLUTION_XHIGH))
      { iconWt = 60; iconHt = 60; }
      Dimension thumbSize = new Dimension(iconWt, iconHt);
      return thumbSize;
   }
   
   public static Vector<Float> getFixImageSettings(int avgPixel)
   {
      if (m_FixImageSettings == null) buildFixImageSettings();
      XONUtil.logDebugMesg("Avg Img Pixel: "+avgPixel);
      int avgarry[] = {0, 32, 64, 128, 160, 192, 224, 255};
      for (int i =1 ; i < avgarry.length; i++) {
         if (avgPixel < avgarry[i]) { avgPixel = avgarry[i-1]; break; } 
      }
      Vector<Float> settings = m_FixImageSettings.get(avgPixel);
      XONUtil.logDebugMesg("Calc Avg Img Pixel: "+avgPixel+" Settings: "+settings);
      return settings;
   }
   
   private static SparseArray<Vector<Float>> m_FixImageGainSettings;
   
   private static void buildFixImageGainSettings()
   {
      m_FixImageGainSettings = new SparseArray<Vector<Float>>();
      int avgPixel = -1; 
      String[] fixImgSetProps = getStringArray(R.array.quick_fix_image_gain_settings);
      for(int i = 0; i < fixImgSetProps.length; i++) {
         Vector<Float> settings = new Vector<Float>();
         String[] fixImgSetLists = fixImgSetProps[i].split("::");
         for(int j = 0; j < fixImgSetLists.length; j++) {
            String[] fixImgSet = fixImgSetLists[j].split(":=");
            String key = fixImgSet[0], val = fixImgSet[1];
            if (key.equals("AvgPixel")) avgPixel = Integer.valueOf(val);
            else settings.addElement(Float.valueOf(val));
         }
         m_FixImageGainSettings.put(avgPixel, settings);
      }
   }
   
   public static Vector<Float> getFixImageGainSettings(int avgPixel)
   {
      if (m_FixImageGainSettings == null) buildFixImageGainSettings();
      XONUtil.logDebugMesg("Avg Img Pixel: "+avgPixel);
      int avgarry[] = {0, 32, 64, 128, 160, 192, 224, 255};
      for (int i =1 ; i < avgarry.length; i++) {
         if (avgPixel < avgarry[i]) { avgPixel = avgarry[i-1]; break; } 
      }
      Vector<Float> settings = m_FixImageGainSettings.get(avgPixel);
      XONUtil.logDebugMesg("Calc Avg Img Pixel: "+avgPixel+" Settings: "+settings);
      return settings;
   }
   
   private static int[] getColorOptions(int resId)
   {
      String[] colOpt = getString(resId).split("::"); 
      int[] colorOptions = new int[colOpt.length];
      for(int i = 0; i<colOpt.length; i++) {
//         XONUtil.logDebugMesg("Parsing Color: "+colOpt[i]);
         colorOptions[i] = Color.parseColor(colOpt[i]);
      }
      return colorOptions;
   }
   
   public static void activateProgressBar(boolean activate)
   {
      if (m_ProgressBar == null) 
         m_ProgressBar = (ProgressBar) m_SubMainActivity.findViewById(R.id.progress_bar);
      // This can happen if the Activity is changed before this thread is executed. In
      // such cases nothing is done
      if (m_ProgressBar == null) return;
      if (activate) {
         m_ProgressBar.setVisibility(View.VISIBLE);      
         m_ProgressBar.bringToFront(); 
      } else m_ProgressBar.setVisibility(View.GONE); 
      m_ProgressBar.invalidate();
   }
   
   public static void activateProgressBarOnUIThread(final boolean activate)
   {
      if (m_ProgressBar == null) 
         m_ProgressBar = (ProgressBar) m_SubMainActivity.findViewById(R.id.progress_bar);
      if (m_ProgressBar == null || m_SubMainActivity == null) return;
      m_SubMainActivity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            if (activate) {
               m_ProgressBar.setVisibility(View.VISIBLE);      
               m_ProgressBar.bringToFront(); 
            } else m_ProgressBar.setVisibility(View.GONE); 
            m_ProgressBar.invalidate();
         }
      });
   }
   
   public static boolean checkActExists()
   {
      if (m_SubMainActivity == null) return false;
      return true;
   }
   
   public static float getFloatRes(int resId)
   {
      return Float.valueOf(getString(resId)).floatValue();
   }
   
   public static int getIntRes(int resId)
   {
      return Integer.valueOf(getString(resId)).intValue();
   }
   
   public static boolean getBoolRes(int resId)
   {
      return Boolean.valueOf(getString(resId)).booleanValue();
   }
   
   public static String getString(int resId)
   {
      Resources res = m_MainActivity.getResources();
      return res.getString(resId);
   }
   
   public static String getString(int resId, boolean removeSpace)
   {
      Resources res = m_MainActivity.getResources(); String val = res.getString(resId);
      if (!removeSpace) return val;
      return XONUtil.replaceSpace(val, "");
   }
   
   public static String getString(String resName)
   {
      Resources res = m_MainActivity.getResources();
      String packName = m_MainActivity.getPackageName();
      int resId = res.getIdentifier(resName, "string", packName);
      return res.getString(resId);
   }
   
   public static int getDrawableRes(String resName)
   {
      Resources res = m_MainActivity.getResources();
      String packName = m_MainActivity.getPackageName();
      return res.getIdentifier(resName, "drawable", packName);
   }
   
   public static String[] getStringArray(int resId)
   {
      Resources res = m_MainActivity.getResources();
      return res.getStringArray(resId);
   }
   
   public static String[] getStringArray(String resName)
   {
      Resources res = m_MainActivity.getResources();
      String packName = m_MainActivity.getPackageName();
      int resId = res.getIdentifier(resName, "array", packName);
      return res.getStringArray(resId);
   }
   
   public static boolean m_SlideMesgShown = false;
   public static boolean m_CircularSlideMesgShown = false;
   
   public static void showCircularFilterSlideMesg()
   {
      if (!m_CircularSlideMesgShown) 
         showAckMesg(R.string.InfoTitle, R.string.CircularFilterSlideMesg, null);
      else setToastMessage(R.string.CircularFilterSlideMesg);
      m_CircularSlideMesgShown = true;
   }
   
   public static void showSlideMesg()
   {
      if (!m_SlideMesgShown) 
         showAckMesg(R.string.InfoTitle, R.string.ImageFilterSlideMesg, null);
      else setLongToastMessage(R.string.ImageFilterSlideMesg);
      m_SlideMesgShown = true;
   }

   public static void setToastMessage(int mesgRes)
   {
      setToastMessage(getString(mesgRes));
   }
      
   public static void setToastMessage(String mesg)
   {
      Toast.makeText(m_MainActivity,  mesg, Toast.LENGTH_SHORT).show();         
   }
   
   
   public static final int LONG_TOAST_DELAY = 3500;
   public static void setLongToastMessage(int mesgRes)
   {
      setLongToastMessage(getString(mesgRes), LONG_TOAST_DELAY);
   }
      
   public static void setLongToastMessage(String mesg)
   {
      setLongToastMessage(mesg, LONG_TOAST_DELAY);
   }
      
   public static void setSuperLongToastMessage(int mesgRes)
   {
      setLongToastMessage(getString(mesgRes), 2*LONG_TOAST_DELAY);
   }
      
   public static void setLongToastMessage(int mesgRes, int duration)
   {
      setLongToastMessage(getString(mesgRes), duration);
   }
      
   public static void setLongToastMessage(String mesg, int duration)
   {
      int iter = Math.round((float)(duration-LONG_TOAST_DELAY)/(float)LONG_TOAST_DELAY);
      if (iter <= 1) iter = 1;
      for (int i = 0; i < iter; i++)
         Toast.makeText(m_MainActivity,  mesg, Toast.LENGTH_LONG).show();         
   }

   public static void showErrorAndRoute(final int mesgResId,
                                        final XONClickListener listener)
   {
      m_MainActivity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            showAckMesg(R.string.ErrorTitle,  getString(mesgResId)+
                        " Cache Size: "+getMemoryCace(), listener);  
            //setToastMessage(mesgResId) ;
         }
      });
   }
   
   public static void showAckMesg(int titleResId, String mesgRes, XONClickListener listener)
   {
      UIUtil.showAckMesgDialog(m_SubMainActivity, R.drawable.alert_dialog_icon, titleResId, 
                               mesgRes, listener);
   }
   
   public static void showAckMesg(int titleResId, int mesgResId, XONClickListener listener)
   {
      UIUtil.showAckMesgDialog(m_SubMainActivity, R.drawable.alert_dialog_icon, titleResId, 
                               getString(mesgResId), listener);
   }
      
   public static void showAboutMesg()
   {
      UIUtil.showAckMesgDialog(m_SubMainActivity, R.drawable.ic_launcher, R.string.AboutXON, 
                               getString(R.string.AboutXONMesg), null);
   }
      
   public static int getMemoryCace()
   {
      // Get memory class of this device, exceeding this amount will throw an
      // OutOfMemory exception.
      int cacheSize = Math.round(MEM_CACHE_PERC * XONUtil.getDeviceMemory(m_MainActivity));
      XONUtil.logDebugMesg("Cache Size: "+cacheSize);
      return cacheSize;
   }
}


