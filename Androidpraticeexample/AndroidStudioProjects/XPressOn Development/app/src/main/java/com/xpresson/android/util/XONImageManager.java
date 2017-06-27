package com.xpresson.android.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.xpresson.android.R;
import com.xpresson.android.XONFullImageActivity;
import com.xpresson.android.XON_IM_UI;
import com.xpresson.android.XON_Main_UI;
import com.xpresson.android.util.XONImageFilterDef.ImageFilter;
import com.xpresson.android.util.XONImageUtil.BitmapResizeLogic;
import com.xpresson.serializable.XONImages;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class XONImageManager implements XONImageProcessor, ThreadInvokerMethod, 
                                        XONClickListener
{
   public final static String XON_THUMBS_DIR = "xon_thumbs";
   public final static String XON_IMAGES_DIR = "xon_images";
//   public final static String XON_CAMERA_DIR = "xon_camera_images";
   public final static String XON_FULL_IMAGES_DIR = "xon_big_images";
   public final static String XON_DATA_DIR = "xon_data";
   public final static String XON_SAVED_EFFECTS_DIR = "xon_efects";
   public final static String XON_IMAGE_DATA_SER_FILE = "xon_images.ser";
   
   
   private enum XONImageProcRequest {
      GetOrigImage, ApplyChangesOnImage, SaveThumbImage, SaveFullImage, 
      LoadFullImage, ShareXONImage;
   }
      
   private XONImages m_XONImages;
   private File m_XONImageSerFile, m_XONImagesDir, m_XONSavedEffectsDir;
   private static XONImageManager m_XONImageManager;   
   private XONImageHandler m_ThumbImageHandler, m_FullImageHandler;
   
   private XONImageManager()
   {
      boolean serXONImgFile = false;
      Activity act = XONPropertyInfo.m_MainActivity; 
      m_XONImagesDir = XONUtil.getDiskCacheDir(act, XON_FULL_IMAGES_DIR);
      if (!m_XONImagesDir.exists()) {
         XONUtil.logDebugMesg("Created Data Dir: "+m_XONImagesDir.mkdirs());
         XONUtil.logDebugMesg("Check if XON Data File Exists:"+m_XONImagesDir.exists());
      }
      File dataCacheDir = XONUtil.getDiskCacheDir(act, XON_DATA_DIR);
      if (!dataCacheDir.exists()) {
         XONUtil.logDebugMesg("Created Data Dir: "+dataCacheDir.mkdirs());
         XONUtil.logDebugMesg("Check if XON Data File Exists:"+dataCacheDir.exists());
      }
      m_XONImageSerFile = new File(dataCacheDir, XON_IMAGE_DATA_SER_FILE);
      XONUtil.logDebugMesg("XON Image Ser File: "+m_XONImageSerFile);
      if (m_XONImageSerFile.exists())
      {
         try {
            String[] files = m_XONImageSerFile.list();
            if (files != null) {
               for (int i = 0; i < files.length; i++) 
                  XONUtil.logDebugMesg("File "+i+1+" "+files[i]);
            }
            m_XONImages = (XONImages) XONUtil.getSerializeObject(m_XONImageSerFile.
                                                                 getAbsolutePath());
            XONUtil.logDebugMesg("XON Images URI: "+m_XONImages.m_XONImages);
         } catch (Exception ex) { 
            XONUtil.logError("Unable to retrieve serialized XON Image Object", ex);
            serXONImgFile = true;
         }
      } else serXONImgFile = true;      
      if (serXONImgFile) { m_XONImages = new XONImages(); serXONImage(); }
      
      m_XONSavedEffectsDir = new File(dataCacheDir, XON_SAVED_EFFECTS_DIR);
      if (!m_XONSavedEffectsDir.exists()) {
         XONUtil.logDebugMesg("Created Saved Effects Dir: "+m_XONSavedEffectsDir.mkdirs());
         XONUtil.logDebugMesg("Check Saved Efects Exists:"+m_XONSavedEffectsDir.exists());
      } 
   }
      
   public static XONImageManager getInstance()
   {
      if (m_XONImageManager == null) 
         m_XONImageManager = new XONImageManager();
      return m_XONImageManager;
   }
   
   public XONImages getXONImage() { return m_XONImages; }
   
   public void buildTemplateFilters()
   {
      TemplateFilterHolder templFltrHldr = null;
      templFltrHldr = TemplateFilterHolder.getInstance(ImageFilter.
                                                       PersonalizedTemplateFilter);
      templFltrHldr.buildTemplateFilters(m_XONImageManager.m_XONSavedEffectsDir);
   }
   
   public void serXONImage()
   {
      try {
         XONUtil.serializeObject(m_XONImageSerFile.getAbsolutePath(), m_XONImages);
      } catch (Exception ex) { 
         XONUtil.logError("Unable to serialize XON Image Object", ex);
      }
   }   
   
   public File serXONFilter(String tmplFltrName, TemplateFilterHandlerImpl templFltr)
   {
      TemplateFilterHolder templFltrHldr = null;
      templFltrHldr = TemplateFilterHolder.getInstance(ImageFilter.
                                                       PersonalizedTemplateFilter);
      int uniqueNum = templFltrHldr.getCounter();
      XONUtil.logDebugMesg("Saving New Templ Fltr: "+tmplFltrName+" in Dir: "+
                           m_XONSavedEffectsDir);
      File templFltrSerFile = new File(m_XONSavedEffectsDir, 
                                       tmplFltrName+"_filter_"+uniqueNum+".ser");      
      XONUtil.logDebugMesg("Ser Templ Fltr Path: "+templFltrSerFile);
      try {
         XONUtil.serializeObject(templFltrSerFile.getAbsolutePath(), templFltr);
      } catch (Exception ex) { 
         XONUtil.logError("Unable to serialize Template Filter Object", ex);
         return null;
      }
      return templFltrSerFile;
   }   
   
   public XONImageHandler getThumbImageHandler(FragmentActivity act)
   {
      if (m_ThumbImageHandler == null)
         createThumbImageHandler(act);
      return m_ThumbImageHandler;
   }
   
   public XONImageHandler getFullImageHandler(FragmentActivity act, int wt, int ht)
   {
      if (m_FullImageHandler == null)
         createFullImageHandler(act, wt, ht);
      return m_FullImageHandler;
   }
   
   private void createThumbImageHandler(FragmentActivity fragAct)
   {
      // Creating Image cache parameters.
      int size = fragAct.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
      XONUtil.logDebugMesg("Thumb Image Cache: "+size);
      m_ThumbImageHandler = createXONImageHandler(fragAct, true, XON_THUMBS_DIR, size, size);      
      m_ThumbImageHandler.setLoadingImage(R.drawable.empty_photo);
//      m_ThumbImageHandler.addImageCache(act.getSupportFragmentManager(), cacheParams);
   }
   
   private void createFullImageHandler(FragmentActivity act, int wt, int ht)
   {
      // Creating Image cache parameters.
      m_FullImageHandler = createXONImageHandler(act, true, XON_IMAGES_DIR, wt, ht);      
      m_FullImageHandler.setImageFadeIn(false);
   }
   
   private XONImageHandler createXONImageHandler(Activity act, boolean isFragAct, 
                                                 String dir, int wt, int ht)
   {
      // Creating Image cache parameters.
      ImageCache.ImageCacheParams cacheParams = null; 
      cacheParams = new ImageCache.ImageCacheParams(act, dir);
      // Set memory cache to one fifth(20%) of the device memory
      cacheParams.setMemCacheSizePercent(act, XONPropertyInfo.MEM_CACHE_PERC); 
      
      // The ImageFetcher takes care of loading images into our ImageView children 
      // asynchronously
      XONImageHandler xonImgHdlr = new XONImageHandler(act, wt, ht);
      XONUtil.logDebugMesg("Activity Class Name: "+act.getClass().getName());
      if (isFragAct) {
         FragmentActivity fragAct = (FragmentActivity) act;
         xonImgHdlr.addImageCache(fragAct.getSupportFragmentManager(), cacheParams);
      } else xonImgHdlr.addImageCache(null, cacheParams);
      return xonImgHdlr;
   }
   
   public void shareXONImage(String imageData)
   {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("Request", XONImageProcRequest.ShareXONImage);
      data.put("ImageData", imageData);
      ThreadCreator.getInstance().createThread(this, data, true);
//      XONPropertyInfo.processAsyncTack(this, data);
      XONPropertyInfo.activateProgressBar(true);
   }
   
   public void loadXONImage(String imageData)
   {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("Request", XONImageProcRequest.LoadFullImage);
      data.put("ImageData", imageData);
      XONUtil.logDebugMesg("Data: "+data);
      ThreadCreator.getInstance().createThread(this, data, true);
//      XONPropertyInfo.processAsyncTack(this, data);
      XONPropertyInfo.activateProgressBar(true);
   }
   
   public void saveXONImage(XONCanvasView view, XONImageHolder xonImg)
   {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("Request", XONImageProcRequest.GetOrigImage);
      data.put("XONImage", xonImg);
      data.put("View", view);
      XONPropertyInfo.processAsyncTack(this, data);
      XONPropertyInfo.activateProgressBar(true);
      XONUtil.logDebugMesg("Check Activity Status: "+XONPropertyInfo.checkActExists());
   }
   
   public void saveXONCameraImage(File pictureFile)
   {
      m_XONImages.addXONCameraImageData(pictureFile.getAbsolutePath());             
      serXONImage();
   }

   @Override
   public void processThreadRequest(Map<String, Object> data)
         throws Exception
   {
      Activity act = XONPropertyInfo.m_MainActivity; 
      XONImageProcRequest req = (XONImageProcRequest) data.get("Request");
      switch(req) 
      {
         case ShareXONImage :
            String shareData = (String) data.get("ImageData");
            File shareFile = new File(shareData);
//            File shareFile = m_FullImageHandler.getImageFile(shareData);
            XONUtil.logDebugMesg("Data: "+shareData+" Shared Image File: "+shareFile);
            IntentUtil.shareContent(act, Uri.fromFile(shareFile), shareFile.getName(), 
                                    XONPropertyInfo.getString(R.string.ImageSharedText));            
            XONPropertyInfo.activateProgressBarOnUIThread(false);
            break;
         case LoadFullImage :
            String imgData = (String) data.get("ImageData");
            File file = new File(imgData);
//            File file = m_FullImageHandler.getImageFile(imgData);
            XONUtil.logDebugMesg("Data: "+imgData+" Loading File: "+file);
            IntentUtil.processIntent(act, XON_IM_UI.class, Uri.fromFile(file));
            XONPropertyInfo.activateProgressBarOnUIThread(false);
            break;
         default : break;
      }
   }
   
   @Override
   public Bitmap processImage(Map<String, Object> data)
   {
      Bitmap bitmap = null, thumbImg = null; int size = 0; XONImageHolder xonImg = null;
      Activity act = XONPropertyInfo.m_MainActivity; 
      XONImageProcRequest req = (XONImageProcRequest) data.get("Request");
      try {
         switch(req) 
         {
            case ApplyChangesOnImage :            
               break;         
            case GetOrigImage : 
               xonImg = (XONImageHolder) data.get("XONImage"); 
               data.put("Request", XONImageProcRequest.SaveFullImage);
               data.put("OrigImg", xonImg.getFinalOrigImg());
               XONPropertyInfo.processAsyncTack(this, data);
               XONUtil.logDebugMesg("Check Activity Status: "+XONPropertyInfo.checkActExists());
               break;
            case SaveFullImage : 
               int uniqueNum =m_XONImages.nextCounter();
               WeakReference<Bitmap> origRefImg = null;
               xonImg = (XONImageHolder) data.get("XONImage");
               origRefImg = new WeakReference<Bitmap>((Bitmap) data.remove("OrigImg"));
               String fName = XONUtil.getFileName(xonImg.m_FilePath), ext = ".png";
               bitmap = xonImg.getFinalFilteredImage(origRefImg);
               if (origRefImg.get() != null && 
                   origRefImg.get() != bitmap) origRefImg.get().recycle(); 
               origRefImg.clear(); XONUtil.logDebugMesg("fName: "+fName+" ext: "+ext);
               File fullImgFile = null; 
               if (fName.contains("_full_"))
                  fullImgFile = new File(m_XONImagesDir, fName+"_"+uniqueNum+ext);
               else fullImgFile = new File(m_XONImagesDir, fName+"_full_"+uniqueNum+ext);
               if (!XONImageUtil.saveImage(bitmap, fullImgFile)) return null;
               size = act.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
               XONUtil.logDebugMesg("ThumbNail Size: "+size);
               thumbImg = XONImageUtil.getScaledBitmap(bitmap, 
                                       BitmapResizeLogic.SetMaxSizeBitmap, size, size, true);
               File thumbImgFile = null;
               if (fName.contains("_full_")) {
                  String thumbFile = m_XONImages.generatePath(fName, "_full_", "_thumb_");
                  thumbImgFile = new File(m_XONImagesDir, thumbFile+"_"+uniqueNum+ext);
               } else thumbImgFile = new File(m_XONImagesDir, fName+"_thumb_"+uniqueNum+ext);
               XONUtil.logDebugMesg("Full Img File: "+fullImgFile.getAbsolutePath()+
                                    " Thumb Img File: "+thumbImgFile.getAbsolutePath());
               if (!XONImageUtil.saveImage(thumbImg, thumbImgFile)) return null;
   //            XONUtil.setExifInfo(xonImg.m_FilePath, thumbImgFile.getAbsolutePath(), 
   //                                thumbImg.getWidth(), thumbImg.getHeight(), orient);
               m_XONImages.addXONImageData(fullImgFile.getAbsolutePath(), 
                                           thumbImgFile.getAbsolutePath());             
               serXONImage(); if (bitmap != null) bitmap.recycle(); bitmap = null;
               if (thumbImg != null) thumbImg.recycle(); thumbImg = null;
   //            IntentUtil.processIntent(XONPropertyInfo.m_MainActivity, XONImageGridActivity.class); 
   //            IntentUtil.shareContent(XONPropertyInfo.m_MainActivity, 
   //                                    Uri.fromFile(fullImgFile), 
   //                                    XONPropertyInfo.getString(R.string.ImageSharedText));            
               
               XONUtil.logDebugMesg("Check Activity Status: "+XONPropertyInfo.checkActExists());
               XONPropertyInfo.activateProgressBarOnUIThread(false);
               xonImg.resetMemoryCache();
               startFullImageView(m_XONImages.getCount()-1);            
               break;
            case SaveThumbImage :
               break;
            default : break; 
         }
      } catch (Throwable ex) {
         XONUtil.logError("Error while saving the Image", ex);
         if (data.containsKey("XONImage")) { xonImg = (XONImageHolder) data.get("XONImage");
                                             if (xonImg != null) xonImg.resetMemoryCache(); }
         XONPropertyInfo.showErrorAndRoute(R.string.NoImageSaved, this);
      }
      return null;
   }
   
   public void startFullImageView(int id)
   {
      XONUtil.logDebugMesg("Check Activity Status1 : "+XONPropertyInfo.checkActExists());
      final Intent i = new Intent(XONPropertyInfo.m_MainActivity, XONFullImageActivity.class);
      i.putExtra(XONFullImageActivity.EXTRA_IMAGE, (int) id);
      i.putExtra(XONFullImageActivity.Caller, (int) XONFullImageActivity.CALL_FROM_IMAGE_MAKER);
      XONPropertyInfo.activateProgressBarOnUIThread(false);
      XONPropertyInfo.m_MainActivity.startActivity(i);
      XONUtil.logDebugMesg("Check Activity Status2: "+XONPropertyInfo.checkActExists());
//      if (XONPropertyInfo.m_SubMainActivity != null)
//         XONPropertyInfo.m_SubMainActivity.finish();
//      XONPropertyInfo.m_SubMainActivity = null;
   }

   @Override
   public void onOK(int dialogTitleResId, View customView)
   {
      IntentUtil.processIntent(XONPropertyInfo.m_SubMainActivity, XON_Main_UI.class);
//      if (XONPropertyInfo.m_SubMainActivity != null)
//         XONPropertyInfo.m_SubMainActivity.finish();
//      XONPropertyInfo.m_SubMainActivity = null;
   }
   
   @Override
   public void onClick(int actionBut) { }
   @Override
   public void onCancel(int dialogTitleResId) {}
}
