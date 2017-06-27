package com.xpresson.android.util;

import java.io.File;
import java.lang.ref.WeakReference;

import com.xpresson.android.R;
import com.xpresson.android.XON_Main_UI;
import com.xpresson.android.util.XONImageUtil.BitmapResizeLogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * A simple subclass of {@link ImageResizer} that saves images processed using XPressOn App.
 */
public class XONImageHandler extends ImageResizer
{
   public BitmapResizeLogic m_BitmapResizeLogic = BitmapResizeLogic.SetScaledBitmap;
   
   /**
    * Initialize providing a target image width and height for the processing images.
    *
    * @param context
    * @param imageWidth
    * @param imageHeight
    */
   public XONImageHandler(Context context, int imageWidth, int imageHeight) 
   {
      super(context, imageWidth, imageHeight);
      init(context);
   }

   /**
    * Initialize providing a single target image size (used for both width and height);
    *
    * @param context
    * @param imageSize
    */
   public XONImageHandler(Context context, int imageSize) 
   {
       super(context, imageSize);
       init(context);
   }

   // Using the context to retrieve the Cache Dir in the Device or the Memory Card
   private void init(Context context) 
   {
   }
   
   public void saveXONImage(String dataString, Bitmap bitmap)
   {
      // If the bitmap was processed and the image cache is available, then add the processed
      // bitmap to the cache for future use. Note we don't check if the task was cancelled
      // here, if it was, and the thread is still running, we may as well add the processed
      // bitmap to our cache as it might be used again in the future
      if (bitmap != null && mImageCache != null) 
          mImageCache.addBitmapToCache(dataString, bitmap);
   }

   int m_RetryCounter = 0;
   @Override
   protected Bitmap processBitmap(Object data) 
   {
      m_RetryCounter = 0;
      return processBitmap(String.valueOf(data));
   }

   /**
    * The main process method, which will be called by the ImageWorker in the AsyncTask background
    * thread.
    *
    * @param data The data to load the bitmap, in this case, a regular http URL
    * @return The downloaded and resized bitmap
    */
   private Bitmap processBitmap(String filePath) 
   {
      try {
         XONUtil.logDebugMesg("Retrieving Image for: "+filePath);
         File file = new File(filePath); 
         if (!file.exists()) { XONUtil.logDebugMesg("File Does Not Exists"); return null; }
         WeakReference<Bitmap> bitmapRef = null;
         Bitmap bitmap = decodeSampledBitmapFromFile(filePath, mImageWidth, mImageHeight);
         bitmapRef = new WeakReference<Bitmap>(bitmap);
         Bitmap resBitmap = XONImageUtil.getScaledBitmap(bitmapRef.get(), m_BitmapResizeLogic,  
                                                         mImageWidth, mImageHeight, true);
         if (bitmapRef.get() != resBitmap) bitmapRef.get().recycle(); 
         bitmapRef.clear(); 
         return resBitmap;
      } catch(Throwable th) {
         XONUtil.logError("Unable to View Full Image. Retry Attempts: "+m_RetryCounter, th);
         if (m_RetryCounter == 0) {
            System.gc(); XONUtil.put2Sleep(4); m_RetryCounter++;
            XONPropertyInfo.showAckMesg(R.string.InfoTitle, 
                                        R.string.ViewImageTry, null);
            return processBitmap(filePath);
         } else {
            XONPropertyInfo.showAckMesg(R.string.InfoTitle, 
                                        R.string.NoImageShown, null);
            IntentUtil.processIntent(XONPropertyInfo.m_SubMainActivity, XON_Main_UI.class);            
         }
      }
      return null;
   }

   /**
    * Subclasses should override this to define any processing or work that must happen on 
    * Post Execute stage where in the final Bitmap is already produced.
    *
    * @param data The data to identify which image to process, as provided by
    *            {@link ImageWorker#loadImage(Object, ImageView)}
    * @return The processed bitmap
    */
   protected void processPostExecute(String filePath)
   {
//      File file = new File(filePath);
//      if (file.exists())
//         XONUtil.logDebugMesg("File Deleted Successfully: "+file.delete());
   }
   
}
