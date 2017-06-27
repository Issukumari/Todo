package com.xpresson.android.util;

import java.lang.ref.WeakReference;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.Gravity;
import android.widget.ImageView;

public class XONImageProcessHandler
{   
   // Set placeholder bitmap that shows when the the background thread is running.
   private Bitmap mLoadingBitmap;
   private boolean mFadeInBitmap = true;
   private static final int FADE_IN_TIME = 200;
   
   protected Resources mResources;

   protected XONImageProcessHandler(Context context) 
   {
      mResources = context.getResources();
   }

   /**
    * Set placeholder bitmap that shows when the the background thread is running.
    *
    * @param bitmap
    */
   public void setLoadingImage(Bitmap bitmap) 
   {
       mLoadingBitmap = bitmap;
   }

   /**
    * Set placeholder bitmap that shows when the the background thread is running.
    *
    * @param resId
    */
   public void setLoadingImage(int resId) 
   {
       mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
   }

   /**
    * If set to true, the image will fade-in once it has been loaded by the background thread.
    */
   public void setImageFadeIn(boolean fadeIn) 
   {
       mFadeInBitmap = fadeIn;
   }

   public void processImage(XONImageProcessor imgProc, Map<String, Object> data, 
                            ImageView imageView)
   {
      if (imgProc == null) return;
      XONUtil.logDebugMesg("Data: "+data);
      final ImageProcessTask task = new ImageProcessTask(imgProc, imageView);
      final AsyncDrawable asyncDrawable =
              new AsyncDrawable(mResources, mLoadingBitmap, task);
      asyncDrawable.setGravity(Gravity.CENTER);
      imageView.setImageDrawable(asyncDrawable);

      // NOTE: This uses a custom version of AsyncTask that has been pulled from the
      // framework and slightly modified. Refer to the docs at the top of the class
      // for more info on what was changed.
      task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);      
   }
   
   /**
    * Called when the processing is complete and the final bitmap should be set on the ImageView.
    *
    * @param imageView
    * @param bitmap
    */
   @SuppressWarnings("deprecation")
   private void setImageBitmap(ImageView imageView, Bitmap bitmap) 
   {
      if (!mFadeInBitmap) { imageView.setImageBitmap(bitmap); return; }
      
      // TransitionDrawable is intended to cross-fade between multiple Drawable Arrays.
      // Here it is first with a transparent drawbale color and then the final bitmap
      Drawable[] drawable = new Drawable[] { 
                                new ColorDrawable(android.R.color.transparent),
                                new BitmapDrawable(mResources, bitmap) };
      final TransitionDrawable td = new TransitionDrawable(drawable);
      
      // Set background to loading bitmap
      if (XONUtil.hasJellyBean()) 
         this.setBackground(imageView, new BitmapDrawable(mResources, mLoadingBitmap));
      else imageView.setBackgroundDrawable(new BitmapDrawable(mResources, mLoadingBitmap));
      imageView.setImageDrawable(td);
      // Start the Transition
      td.startTransition(FADE_IN_TIME);
   }
   
   @TargetApi(16)
   private void setBackground(ImageView imageView, BitmapDrawable drawable)
   {
      imageView.setBackground(drawable);
   }
   
   /**
    * @param imageView Any imageView
    * @return Retrieve the currently active work task (if any) associated with this imageView.
    * null if there is no such task.
    */
   private static ImageProcessTask getImageProcessTask(ImageView imageView) 
   {
      if (imageView == null) return null;
       
      final Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
         final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
         return asyncDrawable.getImageProcessTask();
      }
      
      return null;
   }
   
   public void invokeAsyncTask(XONImageProcessor taskProc, Map<String, Object> data)
   {
      if (taskProc == null) return;
      XONUtil.logDebugMesg("Data: "+data);
      AsyncTaskInvoker task = new AsyncTaskInvoker();
      task.execute(data, taskProc);            
   }
   
   protected class AsyncTaskInvoker extends AsyncTask<Object, Void, Void> 
   {
      @Override
      protected Void doInBackground(Object... params) 
      {
         @SuppressWarnings("unchecked")
         Map<String, Object> data = (Map<String, Object>) params[0];
         XONUtil.logDebugMesg("doInBackground - starting work");
         XONImageProcessor taskProc = (XONImageProcessor) params[1];
         taskProc.processImage(data);
         return null;
      }
   }


   /**
    * A custom Drawable that will be attached to the imageView while the work is in progress.
    * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
    * required, and makes sure that only the last started worker process can bind its result,
    * independently of the finish order.
    */
    private static class AsyncDrawable extends BitmapDrawable 
    {
       private final WeakReference<ImageProcessTask> imageProcTaskRef;

       public AsyncDrawable(Resources res, Bitmap bitmap, ImageProcessTask imgProcTask) 
       {
          super(res, bitmap);
          imageProcTaskRef = new WeakReference<ImageProcessTask>(imgProcTask);
       }

       public ImageProcessTask getImageProcessTask() 
       {
          return imageProcTaskRef.get();
       }
    }

   /**
    * The actual AsyncTask that will asynchronously process the image.
    */
   private class ImageProcessTask extends AsyncTask<Object, Void, Bitmap> 
   {
      private final WeakReference<XONImageProcessor> m_XONImageProcessorRef; 
      private final WeakReference<ImageView> imageViewReference;
      
      public ImageProcessTask(XONImageProcessor imgProc, ImageView imageView) 
      {
         imageViewReference = new WeakReference<ImageView>(imageView);
         m_XONImageProcessorRef = new WeakReference<XONImageProcessor>(imgProc);
      }

      /**
       * Background processing.
       */
      @Override
      protected Bitmap doInBackground(Object... params) 
      {
         XONImageProcessor imgProc = m_XONImageProcessorRef.get();
         @SuppressWarnings("unchecked")
         Map<String, Object> data = (Map<String, Object>) params[0];
         XONUtil.logDebugMesg("doInBackground - starting work");
         Bitmap bitmap = imgProc.processImage(data);
         XONUtil.logDebugMesg("doInBackground - finished work ");
         return bitmap;            
      }

      /**
       * Once the image is processed, associates it to the imageView
       */
      @Override
      protected void onPostExecute(Bitmap bitmap) 
      {
         final ImageView imageView = getAttachedImageView();
         if (bitmap != null && imageView != null) {
            XONUtil.logDebugMesg("onPostExecute - setting bitmap");
            setImageBitmap(imageView, bitmap);
         }
         XONPropertyInfo.activateProgressBar(false);
      }

      @Override
      protected void onCancelled(Bitmap bitmap) 
      {
          super.onCancelled(bitmap);
          XONPropertyInfo.activateProgressBar(false);
      }

      /**
       * Returns the ImageView associated with this task as long as the ImageView's task still
       * points to this task as well. Returns null otherwise.
       */
      private ImageView getAttachedImageView() 
      {
          final ImageView imageView = imageViewReference.get();
          final ImageProcessTask imgProcTask = getImageProcessTask(imageView);
          if (this == imgProcTask) return imageView;
          return null;
      }
      
   }

}
