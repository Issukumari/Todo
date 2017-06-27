package com.xpresson.android;


import com.xpresson.android.util.Dimension;
import com.xpresson.android.util.IntentUtil;
import com.xpresson.android.util.ScalingFactor;
import com.xpresson.android.util.XONCropView;
import com.xpresson.android.util.XONImageHolder;
import com.xpresson.android.util.XONPropertyInfo;
import com.xpresson.android.util.XONImageHolder.ViewType;
import com.xpresson.android.util.XONObjectCache;
import com.xpresson.android.util.XONUtil;

import android.app.Activity;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class XONImageCropActivity extends Activity 
{
   // Size to display the image in the Canvas View
   Rect m_LayoutRect; Dimension m_LayoutSize;
   
   XONCropView m_XONCropView;
   
   // Image URI and Image Holder Object
   Uri m_ImageUri; XONImageHolder m_XONImage;
   
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
        
      /** Hiding Title bar of this activity screen */
//      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      
      XONUtil.logDebugMesg("Setting Crop Activity View");
      setContentView(R.layout.xon_crop_activity);
      
      XONPropertyInfo.populateResources(this, BuildConfig.DEBUG, false);      
      m_ImageUri = IntentUtil.getImageIntent(this);
      if (m_ImageUri == null) {
         if (XONObjectCache.getObjectForKey("XONImage") != null) {
            m_XONImage = (XONImageHolder) XONObjectCache.getObjectForKey("XONImage");
            m_ImageUri = m_XONImage.m_Uri;
            XONUtil.logDebugMesg("XON View Type: "+m_XONImage.m_ViewType);
         } else {
            IntentUtil.processIntent(this, XON_Main_UI.class);
            return;
         }
      } else XONPropertyInfo.setSuperLongToastMessage(R.string.ShowCropMessage);
      
      XONUtil.logDebugMesg("Image Uri: "+m_ImageUri+" Uri Path: "+m_ImageUri.getPath());
      
      FrameLayout frameLayout = (FrameLayout) findViewById(R.id.xon_graphics_holder);
      m_XONCropView = new XONCropView(this); frameLayout.addView(m_XONCropView);  
      
      initButtons();
      
      // The view tree observer is used to get notifications when global events, like layout, 
      // happens. Listener is attached to get the final width of the GridView and then calc the
      // number of columns and the width of each column. The width of each column is variable
      // as the GridView has stretchMode=columnWidth. The column width is used to set the height
      // of each view so we get nice square thumbnails.
      m_XONCropView.getViewTreeObserver().addOnGlobalLayoutListener(
                                      new ViewTreeObserver.OnGlobalLayoutListener() 
      {
         // Register a callback to be invoked when the global layout state or the visibility of 
         // views within the view tree changes
         @Override
         public void onGlobalLayout() 
         {
            calcFrameLayoutSize();
            XONUtil.logDebugMesg("CropView Layout Size " + m_LayoutSize);
         }
      });
   }
   
   private void goToXON_IM_UI()
   {
      IntentUtil.processIntent(this, XON_IM_UI.class);
      finish(); 
   }
   
   private void initButtons()
   {
      Button button = (Button) findViewById(R.id.crop_ok);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            goToXON_IM_UI();
            return;
         }
      });
      
      button = (Button) findViewById(R.id.crop_cancel);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_XONImage.cancelCropView();
            goToXON_IM_UI();
            return;
         }
      });
      
      button = (Button) findViewById(R.id.crop_reset);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_XONImage.resetCropView();
            goToXON_IM_UI();
            return;
         }
      });
      
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK) {
          XONUtil.logDebugMesg();  m_XONImage.cancelCropView();
          goToXON_IM_UI();
          return true;
       }
       return super.onKeyDown(keyCode, event);
   }   

   private synchronized void calcFrameLayoutSize()
   {
      if (m_LayoutSize != null && m_LayoutRect != null) return;
      FrameLayout frameLayout = (FrameLayout) findViewById(R.id.xon_graphics_holder);
      int left = 0, right = 0, top = 0, bot = 0, layoutWt = 0, layoutHt = 0;
      if (frameLayout != null) {
         left = frameLayout.getLeft(); top = frameLayout.getTop(); 
         right = frameLayout.getRight(); bot = frameLayout.getBottom(); 
         layoutWt = frameLayout.getWidth(); layoutHt = frameLayout.getHeight(); 
      }
      
      if (layoutWt <= 0 || layoutHt <= 0) return;
      
      m_LayoutSize = new Dimension(layoutWt, layoutHt);
      m_LayoutRect = new Rect(); m_LayoutRect.set(left, top, right, bot); 
      XONUtil.logDebugMesg("Layout Wt: "+layoutWt+" Ht: "+layoutHt+
                                              " Layout Rect: "+m_LayoutRect);
      XONUtil.logDebugMesg("Rect Wt: "+m_LayoutRect.width()+" Ht: "+
                                              m_LayoutRect.height());
      m_XONCropView.m_LayoutRect = m_LayoutRect; 
      m_XONCropView.m_LayoutSize = m_LayoutSize;
      if (m_XONImage == null && m_ImageUri != null) {
         XONUtil.logDebugMesg("Creating XONImage for: "+m_ImageUri);
         Dimension thumbSize = XONPropertyInfo.getThumbSize();         
         m_XONImage = new XONImageHolder(m_ImageUri, ViewType.CropView, m_LayoutSize, 
                                         thumbSize.width, thumbSize.height);
         m_XONImage.deriveDeviceCTM(0, null, ScalingFactor.FillLogic.FIT_IMAGE_IN_BBOX);
         m_XONCropView.setXONImage(m_XONImage); m_XONCropView.invalidate();
      } else m_XONImage.setPanelSize(m_LayoutSize, ViewType.CropView);      
   }
   
}
