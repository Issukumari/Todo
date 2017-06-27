package com.xpresson.android.util;

// Weak references are useful for mappings that should have their entries removed 
// automatically once they are not referenced any more (from outside). The difference 
// between a SoftReference and a WeakReference is the point of time at which the decision 
// is made to clear and enqueue the reference:
// A SoftReference should be cleared and enqueued as late as possible, that is, in case 
// the VM is in danger of running out of memory.
// A WeakReference may be cleared and enqueued as soon as is known to be weakly-referenced.
import java.io.File;
import java.io.FileDescriptor;
import java.lang.ref.WeakReference;

import com.xpresson.android.BuildConfig;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public abstract class ImageWorker
{
   private static final String TAG = "ImageWorker";
   private static final int FADE_IN_TIME = 200;

   // Image Chache Object that holds the Images in the Memory as well as Disk
   protected ImageCache mImageCache;
   
   // Image Chache Parameters
   private ImageCache.ImageCacheParams mImageCacheParams;
   
   // Set placeholder bitmap that shows when the the background thread is running.
   private Bitmap mLoadingBitmap;
   
   private File m_DisplayImageFile;
   
   private boolean mFadeInBitmap = true;
   private boolean mExitTasksEarly = false;
   protected boolean mPauseWork = false;
   private final Object mPauseWorkLock = new Object();

   protected Resources mResources;

   private static final int MESSAGE_CLEAR = 0;
   private static final int MESSAGE_INIT_DISK_CACHE = 1;
   private static final int MESSAGE_FLUSH = 2;
   private static final int MESSAGE_CLOSE = 3;

   protected ImageWorker(Context context) 
   {
      mResources = context.getResources();
   }

   /**
    * Subclasses should override this to define any processing or work that must happen to produce
    * the final bitmap. This will be executed in a background thread and be long running. For
    * example, you could resize a large bitmap here, or pull down an image from the network.
    *
    * @param data The data to identify which image to process, as provided by
    *            {@link ImageWorker#loadImage(Object, ImageView)}
    * @return The processed bitmap
    */
   protected abstract Bitmap processBitmap(Object data);
   
   /**
    * Subclasses should override this to define any processing or work that must happen on 
    * Post Execute stage where in the final Bitmap is already produced.
    *
    * @param data The data to identify which image to process, as provided by
    *            {@link ImageWorker#loadImage(Object, ImageView)}
    * @return The processed bitmap
    */
   protected void processPostExecute(Object data) {}
   

   /**
    * Load an image specified by the data parameter into an ImageView (override
    * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk
    * cache will be used if an {@link ImageCache} has been set using
    * {@link ImageWorker#setImageCache(ImageCache)}. If the image is found in the memory cache, it
    * is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the
    * bitmap.
    *
    * @param data The URL of the image to download.
    * @param imageView The ImageView to bind the downloaded image to.
    */
   public void loadImage(Object data, ImageView imageView) 
   {
      if (data == null) return;
      Bitmap bitmap = null;
      m_DisplayImageFile = getImageFile(data);
      XONUtil.logDebugMesg("Image File: "+m_DisplayImageFile);
      if (mImageCache != null) 
         bitmap = mImageCache.getBitmapFromMemCache(String.valueOf(data));
      
      // Bitmap found in memory cache
      if (bitmap != null) { XONUtil.logDebugMesg("Bitmap found in memory cache: "+data);
                            imageView.setImageBitmap(bitmap); return; }
      
      // If the Image to be loaded is different from the current image being loaded, then 
      // the current loading of the image is cancelled
      if (cancelPotentialWork(data, imageView)) 
      {
         XONUtil.logDebugMesg("Retriving Image for: "+data);
         final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
         final AsyncDrawable asyncDrawable =
                 new AsyncDrawable(mResources, mLoadingBitmap, task);
         imageView.setImageDrawable(asyncDrawable);

         // NOTE: This uses a custom version of AsyncTask that has been pulled from the
         // framework and slightly modified. Refer to the docs at the top of the class
         // for more info on what was changed.
         task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data);         
      } else XONUtil.logDebugMesg("Image is being Retrived from: "+data);
   }

   /**
    * Load an image specified by the data parameter into an ImageView (override
    * {@link ImageWorker#processBitmap(Object)} to define the processing logic). A memory and disk
    * cache will be used if an {@link ImageCache} has been set using
    * {@link ImageWorker#setImageCache(ImageCache)}. If the image is found in the memory cache, it
    * is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the
    * bitmap.
    *
    * @param data The URL of the image to download.
    * @param imageView The ImageView to bind the downloaded image to.
    */
   public FileDescriptor getImageFD(Object data) 
   {
      if (data == null || mImageCache == null) return null;
      return mImageCache.getFDFromDiskCache(String.valueOf(data));
   }

   public void deleteImageFile(Object data) 
   {
      File file = getImageFile(data);
      if (file != null) XONUtil.deleteFiles(file);
   }
   
   public File getImageFile(Object data) 
   {
//      if (m_DisplayImageFile != null) return m_DisplayImageFile;
      if (data == null || mImageCache == null) return null;
      return mImageCache.getFileFromDiskCache(String.valueOf(data));
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
    * Adds an {@link ImageCache} to this worker in the background (to prevent disk access on UI
    * thread).
    * @param fragmentManager - Uses FragmentManager for interacting with 
    *                          fragments associated with this activity.
    * @param cacheParams
    */
   public void addImageCache(FragmentManager fragmentManager, 
                             ImageCache.ImageCacheParams cacheParams) 
   {
      XONUtil.logDebugMesg(); 
      mImageCacheParams = cacheParams;
      setImageCache(ImageCache.findOrCreateCache(fragmentManager, mImageCacheParams));
      new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
   }

   /**
    * Sets the {@link ImageCache} object to use with this ImageWorker. Usually you will not need
    * to call this directly, instead use {@link ImageWorker#addImageCache} which will create and
    * add the {@link ImageCache} object in a background thread (to ensure no disk access on the
    * main/UI thread).
    *
    * @param imageCache
    */
   public void setImageCache(ImageCache imageCache) 
   {
       mImageCache = imageCache;
   }

   /**
    * If set to true, the image will fade-in once it has been loaded by the background thread.
    */
   public void setImageFadeIn(boolean fadeIn) 
   {
       mFadeInBitmap = fadeIn;
   }

   public void setExitTasksEarly(boolean exitTasksEarly) 
   {
       mExitTasksEarly = exitTasksEarly;
   }

   /**
    * Cancels any pending work attached to the provided ImageView.
    * @param imageView
    */
   public static void cancelWork(ImageView imageView) 
   {
      final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
      if (bitmapWorkerTask != null) {
         bitmapWorkerTask.cancel(true);
         if (BuildConfig.DEBUG) {
            final Object bitmapData = bitmapWorkerTask.data;
            Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
         }
      }
   }

   /**
    * Returns true if the current work has been canceled or if there was no work in
    * progress on this image view.
    * Returns false if the work in progress deals with the same data. The work is not
    * stopped in that case.
    */
   public static boolean cancelPotentialWork(Object data, ImageView imageView) 
   {
      final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
      // The same work is already in progress.
      if (bitmapWorkerTask == null) return true; 
      final Object bitmapData = bitmapWorkerTask.data;
      // If the bitmapData is same as the data to be loaded i.e. same work is already in 
      // progress, then cancelPotentialWork is set to false
      if (bitmapData != null && bitmapData.equals(data)) return false;
      // Cancelling the current work
      bitmapWorkerTask.cancel(true);
      XONUtil.logDebugMesg("cancelPotentialWork - cancelled work for " + data);
      return true;
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
   
   public void setPauseWork(boolean pauseWork) 
   {
      synchronized (mPauseWorkLock) {
          mPauseWork = pauseWork;
          if (!mPauseWork) mPauseWorkLock.notifyAll();
      }
   }

   /**
    * @param imageView Any imageView
    * @return Retrieve the currently active work task (if any) associated with this imageView.
    * null if there is no such task.
    */
   private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) 
   {
      if (imageView == null) return null;
       
      final Drawable drawable = imageView.getDrawable();
      if (drawable instanceof AsyncDrawable) {
         final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
         return asyncDrawable.getBitmapWorkerTask();
      }
      
      return null;
   }
   
   protected void initDiskCacheInternal() 
   {
      if (mImageCache != null) mImageCache.initDiskCache();
   }

   protected void clearCacheInternal() 
   {
      if (mImageCache != null) mImageCache.clearCache();
   }

   protected void flushCacheInternal() 
   {
      if (mImageCache != null) mImageCache.flush();
   }

   protected void closeCacheInternal() 
   {
      if (mImageCache != null) { mImageCache.close(); mImageCache = null; }
   }

   public void clearCache() 
   {
      new CacheAsyncTask().execute(MESSAGE_CLEAR);
   }

   public void flushCache() 
   {
      new CacheAsyncTask().execute(MESSAGE_FLUSH);
   }

   public void closeCache() 
   {
      new CacheAsyncTask().execute(MESSAGE_CLOSE);
   }   

   protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> 
   {
      @Override
      protected Void doInBackground(Object... params) {
          switch ((Integer)params[0]) {
              case MESSAGE_CLEAR:
                  clearCacheInternal();
                  break;
              case MESSAGE_INIT_DISK_CACHE:
                  initDiskCacheInternal();
                  break;
              case MESSAGE_FLUSH:
                  flushCacheInternal();
                  break;
              case MESSAGE_CLOSE:
                  closeCacheInternal();
                  break;
          }
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
      private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

      public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) 
      {
         super(res, bitmap);
         bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
      }

      public BitmapWorkerTask getBitmapWorkerTask() 
      {
         return bitmapWorkerTaskReference.get();
      }
   }

   /**
    * The actual AsyncTask that will asynchronously process the image.
    */
   private class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> 
   {
      private Object data;
      private final WeakReference<ImageView> imageViewReference;
      
      public BitmapWorkerTask(ImageView imageView) 
      {
         imageViewReference = new WeakReference<ImageView>(imageView);
      }

      /**
       * Background processing.
       */
      @Override
      protected Bitmap doInBackground(Object... params) 
      {
         data = params[0];
         XONUtil.logDebugMesg("doInBackground - starting work "+data);
         final String dataString = String.valueOf(data);
         Bitmap bitmap = null;

         // Wait here if work is paused and the task is not cancelled
         synchronized (mPauseWorkLock) 
         {
            while (mPauseWork && !isCancelled()) {
               try { mPauseWorkLock.wait(); } catch (InterruptedException e) {}
            }
         }
            
         // If this task has not been cancelled by another thread and the ImageView 
         // that was originally bound to this task is still bound back to this task 
         // and "exit early" flag is not set then try to retrieve the Image from either
         // Cache or the Directory
         if (!isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) 
         {
            // If the image cache is available then try and fetch the bitmap from the cache
            if (mImageCache != null) 
               bitmap = mImageCache.getBitmapFromDiskCache(dataString);
            // If the bitmap was not found in the cache then call the main process 
            // method to retrieve the Bitmap
            if (bitmap == null) bitmap = processBitmap(params[0]);
         }
         
         // If the bitmap was processed and the image cache is available, then add the processed
         // bitmap to the cache for future use. Note we don't check if the task was cancelled
         // here, if it was, and the thread is still running, we may as well add the processed
         // bitmap to our cache as it might be used again in the future
         if (bitmap != null && mImageCache != null) 
             mImageCache.addBitmapToCache(dataString, bitmap);
         XONUtil.logDebugMesg("doInBackground - finished work "+data);
         return bitmap;            
      }

      /**
       * Once the image is processed, associates it to the imageView
       */
      @Override
      protected void onPostExecute(Bitmap bitmap) 
      {
         // if cancel was called on this task or the "exit early" flag is set then we're done
         if (isCancelled() || mExitTasksEarly) bitmap = null;

         final ImageView imageView = getAttachedImageView();
         if (bitmap != null && imageView != null) {
            XONUtil.logDebugMesg("onPostExecute - setting bitmap");
            processPostExecute(data);
            setImageBitmap(imageView, bitmap);
         }
      }

      @Override
      protected void onCancelled(Bitmap bitmap) 
      {
          super.onCancelled(bitmap);
          synchronized (mPauseWorkLock) {
              mPauseWorkLock.notifyAll();
          }
      }

      /**
       * Returns the ImageView associated with this task as long as the ImageView's task still
       * points to this task as well. Returns null otherwise.
       */
      private ImageView getAttachedImageView() 
      {
          final ImageView imageView = imageViewReference.get();
          final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
          if (this == bitmapWorkerTask) return imageView;
          return null;
      }
      
   }

}
