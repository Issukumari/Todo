package com.xpresson.android.util;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.xpresson.android.BuildConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * This class holds our bitmap caches (memory and disk).
 */
public class ImageCache
{
   private static final String TAG = "ImageCache";

   // Default memory cache size
   private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB

   // Default disk cache size
   private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

   // Compression settings when writing images to disk cache
   private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
   private static final int DEFAULT_COMPRESS_QUALITY = 70;
   private static final int DISK_CACHE_INDEX = 0;

   // Constants to easily toggle various caches
   private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
   private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
   private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;
   private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false;

   // The cache stores its data in a directory on the filesystem. This directory must be 
   // exclusive to the cache; the cache may delete or overwrite files from its directory. 
   // It is an error for multiple processes to use the same cache dir at the same time.
   private DiskLruCache mDiskLruCache;
   
   // A cache that holds strong references to a limited number of values in mem. Each time 
   // a value is accessed, it is moved to the head of a queue. When a value is added to a
   // full cache, the value at the end of that queue is evicted and may become eligible  
   // for garbage collection.
   private LruCache<String, Bitmap> mMemoryCache;
   
   // A holder class that contains cache parameters.
   private ImageCacheParams mCacheParams;
   
   // This object is used to schchronize threads.   
   private final Object mDiskCacheLock = new Object();
   
   // This indicates the DiskCache is either removed or yet to be created
   private boolean mDiskCacheStarting = true;

   /**
    * Creating a new ImageCache object using the specified parameters.
    *
    * @param cacheParams The cache parameters to use to initialize the cache
    */
   public ImageCache(ImageCacheParams cacheParams) 
   {
       init(cacheParams);
   }

   /**
    * Creating a new ImageCache object using the default parameters.
    *
    * @param context The context to use
    * @param uniqueName A unique name that will be appended to the cache directory
    */
   public ImageCache(Context context, String uniqueName) 
   {
       init(new ImageCacheParams(context, uniqueName));
   }

   /**
    * Find and return an existing ImageCache stored in a {@link RetainFragment}, if not found a new
    * one is created using the supplied params and saved to a {@link RetainFragment}.
    *
    * @param fragmentManager The fragment manager to use when dealing with the retained fragment.
    * @param cacheParams The cache parameters to use if creating the ImageCache
    * @return An existing retained ImageCache object or a new one if one did not exist
    */
   public static ImageCache findOrCreateCache(FragmentManager fragmentManager, 
                                              ImageCacheParams cacheParams) 
   {
      ImageCache imageCache = null; RetainFragment retainFragment = null;
      if (fragmentManager != null) {
         // Search for, or create an instance of the non-UI RetainFragment
         retainFragment = findOrCreateRetainFragment(fragmentManager);
         // See if we already have an ImageCache stored in RetainFragment
         imageCache = (ImageCache) retainFragment.getObject();
      }
      // No existing ImageCache, create one and store it in RetainFragment
      if (imageCache == null) {
           imageCache = new ImageCache(cacheParams);
           if (retainFragment != null) retainFragment.setObject(imageCache);
       }

       return imageCache;
   }

   /**
    * Initialize the cache, providing all parameters.
    *
    * @param cacheParams The cache parameters to initialize the cache
    */
   private void init(ImageCacheParams cacheParams) 
   {
       mCacheParams = cacheParams;
       
       // Set up memory cache
       if (mCacheParams.memoryCacheEnabled) 
       {
           if (BuildConfig.DEBUG) 
               Log.d(TAG, "Memory cache created (size = " + mCacheParams.memCacheSize + ")");
           
           mMemoryCache = new LruCache<String, Bitmap>(mCacheParams.memCacheSize) {
               /**
                * Measure item size in bytes rather than units which is more practical
                * for a bitmap cache
                */
               @Override
               protected int sizeOf(String key, Bitmap bitmap) {
                   return XONUtil.getBitmapSize(bitmap);
               }
           };
       }

       // By default the disk cache is not initialized here as it should be initialized
       // on a separate thread due to disk access.
       if (cacheParams.initDiskCacheOnCreate) {
           // Set up disk cache
           initDiskCache();
       }
   }

   /**
    * Initializes the disk cache.  Note that this includes disk access so this should not be
    * executed on the main/UI thread. By default an ImageCache does not initialize the disk
    * cache when it is created, instead you should call initDiskCache() to initialize it on a
    * background thread.
    */
   public void initDiskCache() 
   {
       // Set up disk cache
      XONUtil.logDebugMesg();
       synchronized (mDiskCacheLock) 
       {
           if (mDiskLruCache == null || mDiskLruCache.isClosed()) 
           {
               File diskCacheDir = mCacheParams.diskCacheDir;
               if (mCacheParams.diskCacheEnabled && diskCacheDir != null) 
               {
                   if (!diskCacheDir.exists()) 
                      XONUtil.logDebugMesg("Disk Cace Dir Created: "+diskCacheDir.mkdirs());
                   if (XONUtil.getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) 
                   {
                       try {
                           mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, 
                                                             mCacheParams.diskCacheSize);
                           XONUtil.logDebugMesg("Disk cache initialized");
                       } catch (final IOException e) {
                           mCacheParams.diskCacheDir = null;
                           XONUtil.logError("initDiskCache - ", e);
                       }
                   }
               } // if (mCacheParams.diskCacheEnabled && diskCacheDir != null)
           } // if (mDiskLruCache == null || mDiskLruCache.isClosed()) 
           mDiskCacheStarting = false;
           mDiskCacheLock.notifyAll();
       }
   }

   /**
    * Adds a bitmap to both memory and disk cache.
    * @param data Unique identifier for the bitmap to store
    * @param bitmap The bitmap to store
    */
   public void addBitmapToCache(String data, Bitmap bitmap) 
   {
      if (data == null || bitmap == null) return;
      // Add to memory cache
      XONUtil.logDebugMesg("Bitmap being Cached: "+data);
      if (mMemoryCache != null && mMemoryCache.get(data) == null) 
          mMemoryCache.put(data, bitmap);      
      // Add to Disk Cache
      synchronized (mDiskCacheLock) 
      {
         // If null then not stored in disk
         if (mDiskLruCache == null) return;
         final String key = XONUtil.hashKeyForDisk(data);
         OutputStream out = null;
         try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot == null) 
            {
                final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    out = editor.newOutputStream(DISK_CACHE_INDEX);
                    bitmap.compress(
                            mCacheParams.compressFormat, mCacheParams.compressQuality, out);
                    editor.commit();
                    out.close();
                }
            } else snapshot.getInputStream(DISK_CACHE_INDEX).close();
         } catch (final IOException e) {
            Log.e(TAG, "addBitmapToCache - " + e);
         } catch (Exception e) {
            Log.e(TAG, "addBitmapToCache - " + e);
         } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {}
         }
      } // synchronized (mDiskCacheLock)
   }
   
   /**
    * Get from memory cache.
    *
    * @param data Unique identifier for which item to get
    * @return The bitmap if found in cache, null otherwise
    */
   public Bitmap getBitmapFromMemCache(String data) 
   {
      if (mMemoryCache == null) return null;
      final Bitmap memBitmap = mMemoryCache.get(data);
      if (BuildConfig.DEBUG) Log.d(TAG, "Memory cache hit");
      return memBitmap;
   }

   /**
    * Get from disk cache.
    *
    * @param data Unique identifier for which item to get
    * @return The bitmap if found in cache, null otherwise
    */
   public Bitmap getBitmapFromDiskCache(String data) 
   {
      final String key = XONUtil.hashKeyForDisk(data);
      XONUtil.logDebugMesg("Key: "+key);
      synchronized (mDiskCacheLock) 
      {
         while (mDiskCacheStarting) {
            try {  mDiskCacheLock.wait(); } catch (InterruptedException e) {}
         }
         if (mDiskLruCache == null) return null;
         InputStream inputStream = null;
         try {
            final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            XONUtil.logDebugMesg("Image Snapshot: "+snapshot);
            if (snapshot == null) return null;
            if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache hit");
            inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
            if (inputStream != null) {
               final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
               return bitmap;
            }
         } catch (final IOException e) {
            Log.e(TAG, "getBitmapFromDiskCache - " + e);
         } finally {
            try { if (inputStream != null) inputStream.close();
            } catch (IOException e) {}
         }
      } // synchronized (mDiskCacheLock)
      return null;
   }

   /**
    * Get from disk cache.
    *
    * @param data Unique identifier for which item to get
    * @return The bitmap if found in cache, null otherwise
    */
   public FileDescriptor getFDFromDiskCache(String data) 
   {
      final String key = XONUtil.hashKeyForDisk(data);
      XONUtil.logDebugMesg("Key: "+key);
      synchronized (mDiskCacheLock) 
      {
         while (mDiskCacheStarting) {
            try {  mDiskCacheLock.wait(); } catch (InterruptedException e) {}
         }
         if (mDiskLruCache == null) return null;
         InputStream inputStream = null;
         try {
            final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            XONUtil.logDebugMesg("Image Snapshot: "+snapshot);
            if (snapshot == null) return null;
            if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache hit");
            inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
            if (inputStream != null) {
               FileInputStream fileStream = (FileInputStream) inputStream;
               return fileStream.getFD();
            }
         } catch (final IOException e) {
            Log.e(TAG, "getBitmapFromDiskCache - " + e);
         } finally {
            try { if (inputStream != null) inputStream.close();
            } catch (IOException e) {}
         }
      } // synchronized (mDiskCacheLock)
      return null;
   }

   /**
    * Get from disk cache.
    *
    * @param data Unique identifier for which item to get
    * @return The bitmap if found in cache, null otherwise
    */
   public File getFileFromDiskCache(String data) 
   {
      final String key = XONUtil.hashKeyForDisk(data);
      XONUtil.logDebugMesg("Key: "+key);
      synchronized (mDiskCacheLock) 
      {
         while (mDiskCacheStarting) {
            try {  mDiskCacheLock.wait(); } catch (InterruptedException e) {}
         }
         if (mDiskLruCache == null) return null;
         try {
            return mDiskLruCache.getEntryFile(key);
         } catch (final IOException e) {
            XONUtil.logError("Unable to retrieve File: ", e);
         } 
      } // synchronized (mDiskCacheLock)
      return null;
   }

   /**
    * Clears both the memory and disk cache associated with this ImageCache object. Note that
    * this includes disk access so this should not be executed on the main/UI thread.
    */
   public void clearCache() 
   {
      if (mMemoryCache != null) {
         mMemoryCache.evictAll();
         if (BuildConfig.DEBUG) Log.d(TAG, "Memory cache cleared");
      }

      synchronized (mDiskCacheLock) 
      {
         mDiskCacheStarting = true;
         if (mDiskLruCache != null && !mDiskLruCache.isClosed()) 
         {
            try {
               mDiskLruCache.delete();
               if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache cleared");
            } catch (IOException e) { Log.e(TAG, "clearCache - " + e); }
            mDiskLruCache = null;
            initDiskCache();
         }
      } // synchronized (mDiskCacheLock)
   }

   /**
    * Flushes the disk cache associated with this ImageCache object. Note that this includes
    * disk access so this should not be executed on the main/UI thread.
    */
   public void flush() 
   {
      synchronized (mDiskCacheLock) 
      {
         if (mDiskLruCache != null) {
            try {
               mDiskLruCache.flush();
               if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache flushed");
            } catch (IOException e) { Log.e(TAG, "flush - " + e); }
         }
      } // synchronized (mDiskCacheLock)
   }
   
   /**
    * Closes the disk cache associated with this ImageCache object. Note that this includes
    * disk access so this should not be executed on the main/UI thread.
    */
   public void close() 
   {
      synchronized (mDiskCacheLock) 
      {
         if (mDiskLruCache == null) return; 
         try 
         {
            if (mDiskLruCache.isClosed()) return;
            mDiskLruCache.close();
            mDiskLruCache = null;
            if (BuildConfig.DEBUG) Log.d(TAG, "Disk cache closed");
         } catch (IOException e) { Log.e(TAG, "close - " + e); }
      } // synchronized (mDiskCacheLock)
   }

   /**
    * Locate an existing instance of this Fragment or if not found, create and
    * add it using FragmentManager.
    *
    * @param fm The FragmentManager manager to use.
    * @return The existing instance of the Fragment or the new instance if just
    *         created.
    */
   public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) 
   {
       // Check to see if we have retained the worker fragment.
       RetainFragment retainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

       // If not retained (or first time running), we need to create and add it.
       if (retainFragment == null) {
           retainFragment = new RetainFragment();
           fm.beginTransaction().add(retainFragment, TAG).commitAllowingStateLoss();
       }

       return retainFragment;
   }


   /**
    * A holder class that contains cache parameters.
    */
   public static class ImageCacheParams 
   {
       public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
       public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
       public File diskCacheDir;
       public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
       public int compressQuality = DEFAULT_COMPRESS_QUALITY;
       public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
       public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
       public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;
       public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

       public ImageCacheParams(Context context, String uniqueName) 
       {
           diskCacheDir = XONUtil.getDiskCacheDir(context, uniqueName);
           XONUtil.logDebugMesg("Disk Path: "+diskCacheDir.getAbsolutePath());
       }

       public ImageCacheParams(File diskCacheDir) 
       {
           this.diskCacheDir = diskCacheDir;
       }

       /**
        * Sets the memory cache size based on a percentage of the device memory class.
        * Eg. setting percent to 0.2 would set the memory cache to one fifth of the device memory
        * class. Throws {@link IllegalArgumentException} if percent is < 0.05 or > .8.
        *
        * This value should be chosen carefully based on a number of factors
        * Refer to the corresponding Android Training class for more discussion:
        * http://developer.android.com/training/displaying-bitmaps/
        *
        * @param context Context to use to fetch memory class
        * @param percent Percent of memory class to use to size memory cache
        */
       public void setMemCacheSizePercent(Context context, float percent) 
       {
           if (percent < 0.05f || percent > 0.8f) {
               throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                       + "between 0.05 and 0.8 (inclusive)");
           }
           memCacheSize = Math.round(percent * XONUtil.getDeviceMemory(context));
           XONUtil.logDebugMesg("Mem Cache Size set: "+memCacheSize);
       }

   }

   /**
    * A simple non-UI Fragment that stores a single Object in this case ImageCache Object & 
    * is retained over configuration changes. It will be used to retain the ImageCache object.
    */
   // A Fragment represents a behavior or a portion of user interface in an Activity. You 
   // can combine multiple fragments in a single activity to build a multi-pane UI and reuse 
   // a fragment in multiple activities.
   // 
   public static class RetainFragment extends Fragment {
       private Object mObject;

       /**
        * Empty constructor as per the Fragment documentation
        */
       public RetainFragment() {}

       @Override
       public void onCreate(Bundle savedInstanceState) 
       {
           super.onCreate(savedInstanceState);
           // Make sure this Fragment is retained over a configuration change
           setRetainInstance(true);
       }

       /**
        * Store a single object in this Fragment.
        *
        * @param object The object to store
        */
       public void setObject(Object object) {
           mObject = object;
       }

       /**
        * Get the stored object.
        *
        * @return The stored object
        */
       public Object getObject() {
           return mObject;
       }
   }

}
