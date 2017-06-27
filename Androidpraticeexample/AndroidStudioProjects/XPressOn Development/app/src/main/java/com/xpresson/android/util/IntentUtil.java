package com.xpresson.android.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.xpresson.android.R;

import android.net.Uri;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;

public class IntentUtil
{
   public static String Tag = XONPropertyInfo.XON_TAG;
   
   public static Uri getImageIntent(Activity act)
   {
      Intent intent = act.getIntent(); 
      if (intent == null) return null;
      Bundle bundle = intent.getExtras(); if (bundle == null) return null;
      if (bundle.containsKey(Intent.EXTRA_STREAM))
         return (Uri)bundle.get(Intent.EXTRA_STREAM);
      return null;
   }
   
   public static void processIntent(Activity act, Class<?> cls)
   {
      Intent intent = new Intent(act, cls);
      // start the new activity
      act.startActivity(intent);
   }
   
   public static void processIntent(Activity act, Class<?> cls, Uri imageUri)
   {
      Map<String, Object> extras = new HashMap<String, Object>();
      extras.put(Intent.EXTRA_STREAM, imageUri);
      processIntent(act, cls, extras);
   }
   
   public static void processIntent(Activity act, Class<?> cls, Map<String, Object> extras)
   {
      Intent intent = new Intent(act, cls);
      String[] keys = extras.keySet().toArray(new String[0]);
      for (int i = 0; i < keys.length; i++)
      {
         if (keys[i].equals(Intent.EXTRA_STREAM)) {
            Uri imageUri = (Uri) extras.get(keys[i]);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
         }
      }
         
      // start the new activity
      act.startActivity(intent);
      
   }
   
   // This method provides a chooser to Choose Files Directly or View Gallery
   public static void getImageContent(Activity act, String title, int reqCode)
   {
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      isIntentAvailable(act, intent);
      act.startActivityForResult(Intent.createChooser(intent, title), reqCode);
   }
   
   // // This method shows only the Gallery
   public static void selectPicture(Activity act, int reqCode)
   {
      String action = Intent.ACTION_PICK;
      Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
      
      Intent intent = new Intent(action, uri);
      intent.setType("image/*");
      act.startActivityForResult(intent, reqCode);
   }

   public static void shareContent(Activity act, Uri imageURI, String imgSharedText)
   {      
      shareContent(act, imageURI, "", imgSharedText, 
                   imgSharedText+": "+imageURI.toString());
   }
   
   public static void shareContent(Activity act, Uri imageURI, String fileName, 
                                   String imgSharedText)
   {      
      shareContent(act, imageURI, "", imgSharedText, imgSharedText+": "+fileName);
   }
   
   public static void shareContent(Activity act, String txt)
   {
      Intent intent = new Intent(Intent.ACTION_SEND); 
      intent.setType("image/*");
      shareContent(act, intent, "", "", txt);
   }
   
   public static void shareContent(Activity act, int resId, String imgSharedText)
   { 
	   Uri imageURI = Uri.parse("android.resource://android.aberplayer/" + resId);
	   shareContent(act, imageURI, "", imgSharedText, imgSharedText);
   }

   public static void shareContent(Activity act, Uri imageURI, String email, 
                                   String sub, String txt)
   {
      Intent intent = new Intent(Intent.ACTION_SEND); 
      intent.putExtra(Intent.EXTRA_STREAM, imageURI);
      intent.setType("text/html");
      shareContent(act, intent, email, sub, txt);
   }
   
   public static void shareMultipleContent(Activity act, ArrayList<Uri> imageURIs, 
                                           String imgSharedText)
   {      
      shareMultipleContent(act, imageURIs, "", imgSharedText, 
                           imgSharedText+": Multiple "+imageURIs.toString());
   }
   
   public static void shareMultipleContent(Activity act, ArrayList<Uri> imageURIs, 
                                           String email, String sub, String txt)
   {
      Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE); 
      intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageURIs);
      intent.setType("image/*");
      shareContent(act, intent, email, sub, txt);
   }
   
   public static void shareContent(Activity act, Intent intent, String email, 
                                   String sub, String txt)
   {
      intent.putExtra(Intent.EXTRA_EMAIL, email);  
      intent.putExtra(Intent.EXTRA_SUBJECT, sub); 
      intent.putExtra(Intent.EXTRA_TEXT, txt); 
      XONUtil.logDebugMesg("Action Send Intent: "+intent);
      isIntentAvailable(act, intent);
      String text = XONPropertyInfo.getString(R.string.share_intent_text);
      act.startActivity(Intent.createChooser(intent, text));
   }
   
   public static void sendEmailText(Activity act, String emailTo, String sub, String txt)
   {
      XONUtil.logDebugMesg("EmailTo: "+emailTo+" Sub: "+sub);
      Intent intent = new Intent(Intent.ACTION_SEND); 
//      Intent intent = new Intent(android.content.Intent.ACTION_SEND, 
//                                 Uri.fromParts("mailto", emailTo, null));       
      intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});  
      intent.putExtra(Intent.EXTRA_SUBJECT, sub); 
      intent.putExtra(Intent.EXTRA_TEXT, txt); 
      intent.setType("plain/text");      
      XONUtil.logDebugMesg("Action Send Intent: "+intent);
      List<Intent> targetedIntents = new ArrayList<Intent>();
      List<String> tgtIntents = getTargettedIntent(act, intent, "mail");  
      XONUtil.logDebugMesg("Tgt Intents are: "+tgtIntents);
      String text = XONPropertyInfo.getString(R.string.share_intent_text);
      for (String pckgNm : tgtIntents) {
         Intent tgtIntent = new Intent(Intent.ACTION_SEND); 
         tgtIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});  
         tgtIntent.setPackage(pckgNm); tgtIntent.putExtra(Intent.EXTRA_SUBJECT, sub); 
         tgtIntent.putExtra(Intent.EXTRA_TEXT, txt); tgtIntent.setType("plain/text");      
         targetedIntents.add(tgtIntent);
      }
      Intent chooserIntent = Intent.createChooser(targetedIntents.remove(0), text);
      chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, 
                             targetedIntents.toArray(new Parcelable[]{}));
      act.startActivity(chooserIntent);
//      isIntentAvailable(act, intent);
//      act.startActivity(Intent.createChooser(intent, text));
   }
   
   public static void share(Activity act, String nameApp, String imagePath) 
   {
      List<Intent> targetedShareIntents = new ArrayList<Intent>();
      Intent share = new Intent(android.content.Intent.ACTION_SEND);
      share.setType("image/jpeg");
      List<ResolveInfo> resInfo = act.getPackageManager().queryIntentActivities(share, 0);
      if (resInfo.isEmpty()) return;
       for (ResolveInfo info : resInfo) {
           Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
           targetedShare.setType("image/jpeg"); // put here your mime type

           if (info.activityInfo.packageName.toLowerCase().contains(nameApp) || 
                   info.activityInfo.name.toLowerCase().contains(nameApp)) {
               targetedShare.putExtra(Intent.EXTRA_TEXT,     "My body of post/email");
               targetedShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePath)) );
               targetedShare.setPackage(info.activityInfo.packageName);
               targetedShareIntents.add(targetedShare);
           }
       }

       String text = XONPropertyInfo.getString(R.string.share_intent_text);
       Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), text);
       chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
       act.startActivity(chooserIntent);
   }   

   public static List<String> getTargettedIntent(Activity act, Intent intent, String nameApp) 
   {
      List<String> targetedIntents = new ArrayList<String>();
      final PackageManager mgr = act.getPackageManager();
      java.util.List<ResolveInfo> list = mgr.queryIntentActivities(intent, 
                                             PackageManager.MATCH_DEFAULT_ONLY);
      ResolveInfo[] resInfo = list.toArray(new ResolveInfo[0]);
      for (int i = 0; i < resInfo.length; i++) {
         XONUtil.logDebugMesg("Avalable Intents Name: "+ i+"-"+resInfo[i].activityInfo.name+
                              " Package Name: "+resInfo[i].activityInfo.packageName);
         if (resInfo[i].activityInfo.packageName.toLowerCase().contains(nameApp) || 
             resInfo[i].activityInfo.name.toLowerCase().contains(nameApp)) 
            targetedIntents.add(resInfo[i].activityInfo.packageName);
      }
      return targetedIntents;
   } 
   
   public static boolean isIntentAvailable(Activity act, Intent intent) 
   {
      final PackageManager mgr = act.getPackageManager();
      java.util.List<ResolveInfo> list = mgr.queryIntentActivities(intent, 
                                             PackageManager.MATCH_DEFAULT_ONLY);
      ResolveInfo[] resInfo = list.toArray(new ResolveInfo[0]);
      for (int i = 0; i < resInfo.length; i++) {
//         XONUtil.logDebugMesg("Avalable Intents "+i+"-"+resInfo[i].toString());
         XONUtil.logDebugMesg("Avalable Intents Name: "+ i+"-"+resInfo[i].activityInfo.name+
               " Package Name: "+resInfo[i].activityInfo.packageName);
      }
      return list.size() > 0;
   } 
   
   public static File takePicture(Activity act, int reqCode)
   {
      String fileName = "image_"+System.currentTimeMillis()+".jpg";
      File file = AndroidUtil.getXONFilePath(act, fileName, true);
//      File file = (XONImageManager.getInstance()).createCameraImageFile();
      Uri imageUri = Uri.fromFile(file);
      XONUtil.logDebugMesg("URI: "+imageUri);
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); 
      if (!isIntentAvailable(act, intent)) return null;
      act.startActivityForResult(intent, reqCode);
      return file;
    }

   public static void takePicture(Activity act, String packageName, int reqCode)
   {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(packageName))); 
      act.startActivityForResult(intent, reqCode);
   }

   public static File getTempFile(String packageName)
   {
      //it will return /sdcard/image.tmp
      File path = new File(Environment.getExternalStorageDirectory(), packageName);
      XONUtil.logDebugMesg("File Abs Path: "+path.getAbsolutePath()+", Path: "+path.getPath());
      if(!path.exists()){
        path.mkdir();
      }
      return new File(path, "image.jpg");
   }
   
   public static void rateXPressOn(Activity act) 
   {
      Uri uri = Uri.parse("market://details?id=" + act.getPackageName());
      Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
      try {
          act.startActivity(myAppLinkToMarket);
      } catch (Exception ex) {
         XONPropertyInfo.setLongToastMessage(R.string.UnableToRate);         
      }
   }   
   
   public static void openWebPage(Activity act, String url) 
   {
      XONUtil.logDebugMesg("URL: "+url);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      act.startActivity(intent);      
   }   
}
