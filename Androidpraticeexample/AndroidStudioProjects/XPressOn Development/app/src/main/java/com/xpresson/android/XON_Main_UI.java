package com.xpresson.android;

import com.xpresson.android.util.*;

import java.io.File;
//import java.util.List;

import android.app.Dialog;
import android.view.View;
import android.widget.ImageView;
//import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.view.View.OnClickListener;

import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore.Images.Media;

public class XON_Main_UI extends Activity 
{
   Button m_CameraBtn, m_GalleryBtn, m_MyXONBtn, m_CreateXONBtn;
   public static String Tag = XONPropertyInfo.XON_TAG;

   private MainCanvasView m_MainCanvasView;
   private XONImageManager m_XONImageManager;
   private Uri m_ImageCaptureUri;
   private static final int PICK_FROM_CAMERA = 1;
   private static final int PICK_FROM_CAMERA_AND_STORE = 2;
   private static final int PICK_FROM_FILE = 3;

   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
        
      /** Hiding Title bar of this activity screen */
//      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
      /** Making this activity, full screen */
//    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//                         WindowManager.LayoutParams.FLAG_FULLSCREEN);
      XONPropertyInfo.resetMainAct(); XONPropertyInfo.resetSubMainAct(); 
            
      XONUtil.logDebugMesg("Creating New Instance of XON UI");
      XONPropertyInfo.populateResources(this, BuildConfig.DEBUG);
      // Instantiating the Image Cache
      m_XONImageManager = XONImageManager.getInstance();
      if (m_XONImageManager.getXONImage().sinkImageCache()) 
         m_XONImageManager.serXONImage();
      
      Uri imageUri = IntentUtil.getImageIntent(this);
      if (imageUri != null) { processImage(imageUri); return; }
      
      setContentView(R.layout.activity_xon__main__ui);
      
      FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_dislay_view);
      m_MainCanvasView = new MainCanvasView(this);
      frameLayout.addView(m_MainCanvasView);    
      
      RelativeLayout f = (RelativeLayout) findViewById(R.id.layout_root);
      XONUtil.logDebugMesg("width: "+f.getMeasuredWidth()+", height " + f.getMeasuredHeight());        
      addButtonListener();
   }
   
   @Override
   protected void onStop() 
   {
       super.onStop();
       XONUtil.logDebugMesg();
       m_MainCanvasView.setThreadActivity(false);
   }
   
   @Override
   public void onWindowFocusChanged (boolean hasFocus)
   {
      super.onWindowFocusChanged(hasFocus);
      XONUtil.logDebugMesg("hasFocus: "+hasFocus);
      m_MainCanvasView.setThreadActivity(hasFocus);
   }
   
   private void addButtonListener() 
   {
      Button abtBtn = (Button) findViewById(R.id.xon_about_btn); 
      abtBtn.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
        	XONPropertyInfo.showAboutMesg();
         }
      });
	      
      m_CameraBtn = (Button) findViewById(R.id.camera_btn); 
      m_CameraBtn.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            UIUtil.showShortMessage(XON_Main_UI.this, R.string.camera_tip);
//            setButtonBG(R.id.camera_btn);
            takePicture();
         }
      });
      
      m_GalleryBtn = (Button) findViewById(R.id.gallery_btn);
      m_GalleryBtn.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View arg0) {
             UIUtil.showShortMessage(XON_Main_UI.this, R.string.gallery_tip);
//             setButtonBG(R.id.gallery_btn);
//             IntentUtil.getImageContent(XON_Main_UI.this, "Pick Image", PICK_FROM_FILE);
             IntentUtil.selectPicture(XON_Main_UI.this, PICK_FROM_FILE);
          }
      });
      
      m_MyXONBtn = (Button) findViewById(R.id.myXON_btn);
      m_MyXONBtn.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            if (!m_XONImageManager.getXONImage().checkImageExists()) {
               XONPropertyInfo.showAckMesg(R.string.InfoTitle, R.string.NoSavedImage, null);
               return;
            }
            UIUtil.showShortMessage(XON_Main_UI.this, R.string.myXON_tip);
//            setButtonBG(R.id.myXON_btn);
            IntentUtil.processIntent(XON_Main_UI.this, XONImageGridActivity.class); 
         }
      });
      
//      m_CreateXONBtn = (Button) findViewById(R.id.create_btn);
//      m_CreateXONBtn.setOnClickListener(new OnClickListener() {
//         @Override
//         public void onClick(View arg0) {
//            UIUtil.showMesgDialog(XON_Main_UI.this, getString(R.string.createXON_tip));
//            setButtonBG(R.id.create_btn);
//         }
//      });
   }
   
/*   public void setButtonBG(int selBtn)
   {
      String bgClr = getString(R.string.XONWidgetBackground);
//      String selBGClr = getString(R.string.XONWidgetSelBackground);     
      int selBGColor = R.style.XONTransperantBackground;
//      int bgColor = R.style.XONHomeBtnBackground;
      int bgColor = Color.parseColor(bgClr);
//      m_CameraBtn.setBackgroundColor(bgColor);
//      m_GalleryBtn.setBackgroundColor(bgColor);
//      m_MyXONBtn.setBackgroundColor(bgColor);
//      m_CreateXONBtn.setBackgroundColor(bgColor);
      if (selBtn == R.id.camera_btn) 
         m_CameraBtn.setBackgroundColor(selBGColor);
      else if (selBtn == R.id.gallery_btn) 
         m_GalleryBtn.setBackgroundColor(selBGColor);
      else if (selBtn == R.id.myXON_btn) 
         m_MyXONBtn.setBackgroundColor(selBGColor);
//      else if (selBtn == R.id.create_btn) 
//         m_CreateXONBtn.setBackgroundColor(selBGColor);
   }*/
       
   public void setToastMessage(int rid)
   {
      Toast.makeText(XON_Main_UI.this,  getText(rid), Toast.LENGTH_SHORT).show();         
   }
   
   private void takePicture()
   {
//    IntentUtil.takePicture(XON_Main_UI.this, XON_Main_UI.this.getPackageName(), 
//    PICK_FROM_CAMERA);
      File file = IntentUtil.takePicture(this, PICK_FROM_CAMERA_AND_STORE);
      m_XONImageManager.saveXONCameraImage(file);
      m_ImageCaptureUri = Uri.fromFile(file);
      XONUtil.logDebugMesg("Image Capture URI: "+m_ImageCaptureUri);
   }
      
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) 
   {
      if (resultCode != RESULT_OK) return;
     
      Bitmap bitmap = null; String path = "";
      if (requestCode == PICK_FROM_FILE) {
         try {
            Uri imageUri = data.getData();            
//            InputStream stream = getContentResolver().openInputStream(data.getData());
//            bitmap = BitmapFactory.decodeStream(stream);
//            stream.close();
            processImage(imageUri);
            return;
         } catch(Exception ex) { ex.printStackTrace(); return; }
      } else if(requestCode == PICK_FROM_CAMERA_AND_STORE){
         if (data != null) XONUtil.logDebugMesg("Data URI: "+data.getData());
         if (m_ImageCaptureUri == null) 
         { 
            boolean processImg = false;
            path = (XONImageManager.getInstance()).getXONImage().
                                                   getLastCameraImage();
            if (path != null) {
               File file = new File(path);
               if (file.exists()) { processImg = true; 
                                    m_ImageCaptureUri = Uri.fromFile(file); }
            }
            if (!processImg) { XONUtil.logErrorMesg("Image Capture ERROR - URI IS NULL"); 
                               return; } 
         }
         XONUtil.logDebugMesg("Image Capture URI Set: "+m_ImageCaptureUri+
                              " Data: "+data);
         processImage(m_ImageCaptureUri);
//         path  = m_ImageCaptureUri.getPath();
//         bitmap  = BitmapFactory.decodeFile(path);
//         UIUtil.showImageDialog(this, bitmap);
      } else if (requestCode == PICK_FROM_CAMERA){
         try {
            File file = IntentUtil.getTempFile(this.getPackageName());
            bitmap = Media.getBitmap(getContentResolver(), Uri.fromFile(file) );
            UIUtil.showImageDialog(this, bitmap);
         } catch(Exception ex) { Log.e(Tag,"Image Capture ERROR"); ex.printStackTrace(); }
      } 
   }
  
   public void processImage(Uri imageUri)
   {
//      IntentUtil.shareContent((Activity)this, imageUri, 
//            (String)getText(R.string.ImageSharedText));
//      IntentUtil.processIntent(this, XON_IM_UI.class, imageUri);   
      IntentUtil.processIntent(this, XONImageCropActivity.class, imageUri);   
   }
   
   public void showImageDialog(Bitmap bitmap)
   {
      Dialog dialog = new Dialog(this);
      dialog.setContentView(R.layout.xon_image_dialog);
      dialog.setTitle(getString(R.string.image_dialog));

      ImageView image = (ImageView) dialog.findViewById(R.id.image_dialog);
      image.setImageBitmap(bitmap);      
      dialog.show();
      
   }

//   @Override
//   public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_xon__main__ui, menu);
//        return true;
//   }

}
