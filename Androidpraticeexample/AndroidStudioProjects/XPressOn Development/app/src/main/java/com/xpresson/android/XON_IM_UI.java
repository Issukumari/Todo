package com.xpresson.android;

import com.xpresson.android.BuildConfig;
import com.xpresson.android.R;
import com.xpresson.android.util.*;
import com.xpresson.android.util.XONImageFilterDef.ImageFilter;
import com.xpresson.android.util.XONImageHolder.ViewType;
import com.xpresson.android.util.XONPropertyInfo.ImageOrientation;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;

public class XON_IM_UI extends Activity implements ThreadInvokerMethod, 
                                                   XONClickListener
{
   public Map<Integer, Button> m_ImageProcessButtons;
   public Map<Integer, Button> m_ImageSubProcessButtons;
   public Map<Integer, Button> m_ImageActionButtons;
   public Map<Integer, ScrollView> m_ScrollMenu; 
   public int m_ActiveMainMenu = -1, m_ActiveSubMenu = -1;
   public ScrollView m_ActiveScrollView;
   
   // Basic and Advanced View
   public enum XONIMViewType { Basic, Advanced }
   public XONIMViewType m_XONIMViewType;
   
   // Clean up type local or complete. In Local only the cache of UI Elements are 
   // destroyed while in Complete, the complete cache is destroyed
   private enum XONCleanType { Local, Complete }
   private XONCleanType m_XONCleanType = XONCleanType.Complete;
   
   private static final int[] 
      m_BasicButtonResIds = { R.id.basic_popular_effects_btn, R.id.basic_frame_effects_btn,
                              R.id.basic_user_effects_btn, R.id.basic_adv_effects_btn,
                              R.id.basic_image_save_share_btn };

   private static final int[] 
      m_AdvButtonResIds = { R.id.image_enhance_btn, R.id.quick_effects_btn,
                            R.id.image_effects_btn, R.id.image_filters_btn,
                            R.id.image_save_share_btn };
   
   // Size to display the image in the Canvas View
   Rect m_LayoutRect; Dimension m_LayoutSize;
   
   // Image URI and Image Holder Object
   Uri m_ImageUri; XONImageHolder m_XONImage;
   
   // Canvas View to view Image and Scroll View of type XONImageFilterView to view 
   // and apply Image Filters
   XONCanvasView m_XONCanvasView;
   XONImageFilterView m_XONImageFilterView;
   
   // This is a singleton object used for saving and viewing XON Images
   XONImageManager m_XONImageManager;
   
   // This is set to true when this Activity is completly loaded and Image Filters can
   // be applied. This is used to perform button click as soon as the Activity is loaded
   private boolean m_ImageFilterActivated = false;
   
   // Indicates if Scroll Menu is On/Off
   private boolean m_ScrollMenuOn = false;
   
   public static Vector<Integer> m_MenuAction, m_SubMenuAction;
   static {
      m_MenuAction = new Vector<Integer>(); 
      m_SubMenuAction = new Vector<Integer>();
   }
   
   @SuppressLint({ "UseSparseArrays", "UseSparseArrays" })
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
        
      /** Hiding Title bar of this activity screen */
//      getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      
      setContentView(R.layout.activity_xon__im__ui);
      
      m_LayoutRect = null; m_LayoutSize = null; m_XONImage = null;
      setXONIMViewType(false);
      m_ImageUri = IntentUtil.getImageIntent(this);
      if (m_ImageUri == null) {
         if (XONObjectCache.getObjectForKey("XONImage") != null) {
            m_XONImage = (XONImageHolder) XONObjectCache.getObjectForKey("XONImage");
            m_ImageUri = m_XONImage.m_Uri;
            XONUtil.logDebugMesg("XON View Type: "+m_XONImage.m_ViewType);
         } else {
            IntentUtil.processIntent(XON_IM_UI.this, XON_Main_UI.class);
            finish();
            return;
         }
      }
//      Intent intent = getIntent();
//      Bundle bundle = intent.getExtras();
//      m_ImageUri = (Uri)bundle.get(Intent.EXTRA_STREAM);      
      XONUtil.logDebugMesg("Image Uri: "+m_ImageUri+" Uri Path: "+m_ImageUri.getPath());
      
      m_XONImageFilterView = null; m_ImageFilterActivated = false;
      m_XONCleanType = XONCleanType.Complete;
      m_ScrollMenu = new HashMap<Integer, ScrollView>();
      m_ImageProcessButtons = new HashMap<Integer, Button>();
      m_ImageSubProcessButtons = new HashMap<Integer, Button>();      
      m_ImageActionButtons = new HashMap<Integer, Button>();      
      
      XONPropertyInfo.populateResources(this, BuildConfig.DEBUG, true);
//      XONUtil.logDebugMesg("File Path: "+XONUtil.getFilePath(m_ImageUri));
//      XONUtil.logDebugMesg("Orientation URI: "+XONUtil.getOrientation(m_ImageUri));
//      XONUtil.logDebugMesg("Orientation Path: "+XONUtil.getOrientation(
//                                                        XONUtil.getFilePath(m_ImageUri)));
      
      m_XONImageManager = XONImageManager.getInstance(); 
//      if (m_XONImageManager.getXONImage().sinkImageCache()) m_XONImageManager.serXONImage();
      initXONIMButtons();
            
      XONHorzScrollView listview = (XONHorzScrollView) findViewById(R.id.filter_listview);
      // Reg XONImageFilterView object as an XON Adapter to process call backs from XONHorzScroll
      m_XONImageFilterView =  XONImageFilterView.getInstance(this, m_XONCanvasView, listview);

      FrameLayout frameLayout = (FrameLayout) findViewById(R.id.xon_graphics_holder);
      m_XONCanvasView = m_XONImageFilterView.getCanvasView();
      frameLayout.addView(m_XONCanvasView);    
      
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("Request", "CheckFilterActivatedReq");
      ThreadCreator.getInstance().createThread(this, data, true);
      
      // Building up the Template Filters in Background Thread
//      data = new HashMap<String, Object>();
//      data.put("Request", "BuildTemplateFiltersReq");
//      ThreadCreator.getInstance().createThread(this, data, true);
   }
   
   @Override
   public void onLowMemory() 
   {
      XONUtil.logDebugMesg("System is running low in Memory");
   }
   
   // This is called when the overall system is running low on memory, and would like 
   // actively running process to try to tighten their belt. From interface 
   // android.content.ComponentCallbacks2
   @Override
   public void onTrimMemory(int level) 
   {
      XONUtil.logDebugMesg("System is running low in Memory and will like to trim level: "+
                           level);      
   }
   
   @Override
   public void onDestroy()
   {
      XONUtil.logDebugMesg("This Activity is getting Destroyed....");   
      if (m_XONCleanType.equals(XONCleanType.Local)) localCleanUp(); else cleanUp(); 
      super.onDestroy();
   }
   
   @SuppressWarnings("rawtypes")
   public void localCleanUp()
   {      
      XONUtil.clear((HashMap) m_ScrollMenu);
      XONUtil.clear((HashMap) m_ImageProcessButtons);
      XONUtil.clear((HashMap) m_ImageSubProcessButtons);
      XONUtil.clear((HashMap) m_ImageActionButtons);
   }
   
   public void cleanUp()
   {
      localCleanUp(); XONImageFilterView.reset(); 
      if (m_XONImage != null) m_XONImage.resetMemoryCache();
      m_XONImageFilterView = null; m_XONCanvasView = null;       
   }
   
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK) {
          if (m_XONIMViewType != null) {
             if (m_XONIMViewType.equals(XONIMViewType.Advanced)) {
                resetXONIMViewType();
                IntentUtil.processIntent(this, XON_IM_UI.class);
                m_XONCleanType = XONCleanType.Local; finish();
                return true;
             }
          }
          resetXONIMViewType(); 
          IntentUtil.processIntent(this, XON_Main_UI.class);
          finish();
          return true;
       }
       return super.onKeyDown(keyCode, event);
   }   

   private void resetXONIMViewType()
   {
      XONUtil.logDebugMesg();
      XONObjectCache.removePersistentObject("XONIMViewType");
   }
   
   private void setButtonVisibility(int[] resIds, int visibility)
   {
      if (m_ImageProcessButtons == null) return;
      for (int i = 0; i < resIds.length; i++)
      {
         Button button = m_ImageProcessButtons.get(resIds[i]);
         if (button != null) button.setVisibility(visibility);
      }
   }
   
   private void setXONIMViewType(boolean showAdvView)
   {
      if (showAdvView) {
         m_XONIMViewType = XONIMViewType.Advanced;
         XONObjectCache.addObject("XONIMViewType", m_XONIMViewType, true);
         setButtonVisibility(m_BasicButtonResIds, View.INVISIBLE);
         setButtonVisibility(m_AdvButtonResIds, View.VISIBLE);
         return;
      }
      
      if (XONObjectCache.getObjectForKey("XONIMViewType") != null) {
         m_XONIMViewType = (XONIMViewType) XONObjectCache.
                                           getObjectForKey("XONIMViewType");
         return;
      }
      
      m_XONIMViewType = XONIMViewType.Basic;
      XONUtil.logDebugMesg("Set XON View Type: "+m_XONIMViewType);
      setButtonVisibility(m_AdvButtonResIds, View.INVISIBLE);
      setButtonVisibility(m_BasicButtonResIds, View.VISIBLE);
      XONObjectCache.addObject("XONIMViewType", m_XONIMViewType, true);
   }
   
   private void initXONIMButtons() 
   {
      initXONActions();
      
      if (m_XONIMViewType.equals(XONIMViewType.Advanced)) 
         initXONIMAdvButtons();
      else initXONIMBasicButtons();
   }
   
   private void initXONActions()
   {
//      final String selTip = (String)  getString(R.string.selected_txt);
      Button button = (Button) findViewById(R.id.home_btn);      
      m_ImageProcessButtons.put(R.id.home_btn, button);
      ScrollView scrollView = (ScrollView) findViewById(R.id.home_btn_scroll);      
      m_ScrollMenu.put(R.id.home_btn, scrollView);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            boolean showScrollMenu = true;
            if (m_ActiveMainMenu == R.id.home_btn && m_ScrollMenuOn) showScrollMenu = false;
            else { m_ActiveMainMenu = R.id.home_btn; m_ActiveSubMenu = -1; }
            highlightButton(showScrollMenu);
         }
      });
            
      button = (Button) findViewById(R.id.xon_main_btn);
      m_ImageSubProcessButtons.put(R.id.xon_main_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.home_btn; 
            m_ActiveSubMenu = R.id.xon_main_btn; highlightButton(false);
            resetXONIMViewType(); finish();
            IntentUtil.processIntent(XON_IM_UI.this, XON_Main_UI.class);
         }
      });
      
      button = (Button) findViewById(R.id.xon_orig_btn);
      m_ImageSubProcessButtons.put(R.id.xon_orig_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.home_btn; 
            m_ActiveSubMenu = R.id.xon_orig_btn; highlightButton(false);
            XON_IM_UI.this.m_XONCanvasView.showOriginalImage();
         }
      });
      
      button = (Button) findViewById(R.id.xon_rot_btn);
      m_ImageSubProcessButtons.put(R.id.xon_rot_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.home_btn; 
            m_ActiveSubMenu = R.id.xon_rot_btn; highlightButton(false);
            ImageOrientation imgOrient = XONPropertyInfo.getNext(m_XONImage.m_ImageOrientation);
            XON_IM_UI.this.m_XONCanvasView.setImageOrientation(imgOrient);
         }
      });
      
      button = (Button) findViewById(R.id.xon_save_btn);
      m_ImageSubProcessButtons.put(R.id.xon_save_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            if (!m_XONImage.isImageFilterSpecified()) {
               XONPropertyInfo.showAckMesg(R.string.InfoTitle, 
                                           R.string.CannotSaveEffects, null);
               return;
            }
            m_ActiveMainMenu = R.id.home_btn; 
            m_ActiveSubMenu = R.id.xon_save_btn; highlightButton(false);
            UIUtil.createCustomViewDialog(XONPropertyInfo.m_SubMainActivity, 
                  R.drawable.alert_dialog_icon, R.string.save_effects_title, 
                  R.layout.save_effects_dialog, XON_IM_UI.this).show();               
         }
      });
      button = (Button) findViewById(R.id.xon_crop_btn);
      m_ImageSubProcessButtons.put(R.id.xon_crop_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.home_btn; 
            m_ActiveSubMenu = R.id.xon_crop_btn; highlightButton(false);
            XONUtil.logDebugMesg("Pressed Crop View");
            IntentUtil.processIntent(XON_IM_UI.this, XONImageCropActivity.class);
            m_XONCleanType = XONCleanType.Local; finish();
         }
      });
   }
   
   private void saveAndShareXONImage()
   {
      if (m_XONImage == null) {
         XONPropertyInfo.setToastMessage(R.string.NoImageSel);
         return;
      }
      resetXONIMViewType();  
      XONUtil.logDebugMesg("Check Activity Status: "+XONPropertyInfo.checkActExists());
      m_XONImageManager.saveXONImage(m_XONCanvasView, m_XONImage);
   }
   
   private void initXONIMBasicButtons()
   {
      final String selTip = (String) getString(R.string.selected_txt);
      Button button = (Button) findViewById(R.id.basic_popular_effects_btn);      
      m_ImageProcessButtons.put(R.id.basic_popular_effects_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.basic_popular_effects_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                   selTip+" "+getString(R.string.popular_effects));
            activateXONImageView(R.id.quick_effects_btn, R.string.popular_effects);
         }
      });
   
      button = (Button) findViewById(R.id.basic_frame_effects_btn);
      m_ImageProcessButtons.put(R.id.basic_frame_effects_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.basic_frame_effects_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.frame_effects));
            activateXONImageView(R.id.quick_effects_btn, R.string.frame_effects);
         }
      });
      
      button = (Button) findViewById(R.id.basic_user_effects_btn);
      m_ImageProcessButtons.put(R.id.basic_user_effects_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.basic_user_effects_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.user_effects));
            activateXONImageView(R.id.quick_effects_btn, R.string.user_effects);
         }
      });
      
      button = (Button) findViewById(R.id.basic_adv_effects_btn);
      m_ImageProcessButtons.put(R.id.basic_adv_effects_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.basic_user_effects_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.adv_effects));
            setXONIMViewType(true);  
            IntentUtil.processIntent(XON_IM_UI.this, XON_IM_UI.class);
            m_XONCleanType = XONCleanType.Local; finish();
         }
      });
      
      button = (Button) findViewById(R.id.basic_image_save_share_btn);      
      m_ImageProcessButtons.put(R.id.basic_image_save_share_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.basic_image_save_share_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            saveAndShareXONImage();
         }
      });
   }
   
   private void initXONIMAdvButtons()
   {
      Button button = (Button) findViewById(R.id.image_enhance_btn);      
      m_ImageProcessButtons.put(R.id.image_enhance_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_enhance_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            activateXONImageView(R.string.image_enhance);
         }
      });
      
      button = (Button) findViewById(R.id.image_save_share_btn);      
      m_ImageProcessButtons.put(R.id.image_save_share_btn, button);
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_save_share_btn; m_ActiveSubMenu = -1;
            highlightButton(false);
            saveAndShareXONImage();
         }
      });
      
      initQuickEffects();
      initImageEffects();
      initImageFilters();      
   }
   
   private void initQuickEffects()
   {
      final String selTip = (String) getString(R.string.selected_txt);
      Button button = (Button) findViewById(R.id.quick_effects_btn);
      ScrollView scrollView = (ScrollView) findViewById(R.id.quick_effects_scroll);      
      m_ScrollMenu.put(R.id.quick_effects_btn, scrollView);
      m_ImageProcessButtons.put(R.id.quick_effects_btn, button);
      XONUtil.logDebugMesg("Processing Button: "+getString(R.string.quick_effects));
      button.setVisibility(View.VISIBLE);      
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            boolean showScrollMenu = true;
            if (m_ActiveMainMenu == R.id.quick_effects_btn && m_ScrollMenuOn) 
               showScrollMenu = false;
            else if (m_ActiveMainMenu != R.id.quick_effects_btn)
            { 
               m_ActiveMainMenu = R.id.quick_effects_btn; 
               m_ActiveSubMenu = R.id.popular_effects_btn; 
               UIUtil.showShortMessage(XON_IM_UI.this, 
                                       selTip+" "+getString(R.string.popular_effects));
               activateXONImageView(R.string.popular_effects);
            }
            m_ActiveMainMenu = R.id.quick_effects_btn; highlightButton(showScrollMenu);
         }
      });
      
      button = (Button) findViewById(R.id.popular_effects_btn);
      m_ImageSubProcessButtons.put(R.id.popular_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.quick_effects_btn; 
            m_ActiveSubMenu = R.id.popular_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.popular_effects));
            activateXONImageView(R.string.popular_effects);
         }
      });
      
      button = (Button) findViewById(R.id.frame_effects_btn);
      m_ImageSubProcessButtons.put(R.id.frame_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.quick_effects_btn; 
            m_ActiveSubMenu = R.id.frame_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.frame_effects));
            activateXONImageView(R.string.frame_effects);
         }
      });
      
      button = (Button) findViewById(R.id.adv_effects_btn);
      m_ImageSubProcessButtons.put(R.id.adv_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.quick_effects_btn; 
            m_ActiveSubMenu = R.id.adv_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.adv_effects));
            activateXONImageView(R.string.adv_effects);
         }
      });
      
      button = (Button) findViewById(R.id.user_effects_btn);
      m_ImageSubProcessButtons.put(R.id.user_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.quick_effects_btn; 
            m_ActiveSubMenu = R.id.user_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.user_effects));
            activateXONImageView(R.string.user_effects);
         }
      });
   }
   
   private void initImageEffects()
   {
      final String selTip = (String)  getString(R.string.selected_txt);
      Button button = (Button) findViewById(R.id.image_effects_btn);
      ScrollView scrollView = (ScrollView) findViewById(R.id.image_effects_scroll);      
      m_ScrollMenu.put(R.id.image_effects_btn, scrollView);
      m_ImageProcessButtons.put(R.id.image_effects_btn, button);
      button.setVisibility(View.VISIBLE);      
      button .setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            boolean showScrollMenu = true;
            if (m_ActiveMainMenu == R.id.image_effects_btn && m_ScrollMenuOn) 
               showScrollMenu = false;
            else if (m_ActiveMainMenu != R.id.image_effects_btn)
            { 
               m_ActiveMainMenu = R.id.image_effects_btn; 
               m_ActiveSubMenu = R.id.fade_effects_btn; 
               UIUtil.showShortMessage(XON_IM_UI.this, 
                                       selTip+" "+getString(R.string.fade_effects));
               activateXONImageView(R.string.fade_effects);
            }
            m_ActiveMainMenu = R.id.image_effects_btn; highlightButton(showScrollMenu);
         }
      });
      
      button = (Button) findViewById(R.id.fade_effects_btn);
      m_ImageSubProcessButtons.put(R.id.fade_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_effects_btn; 
            m_ActiveSubMenu = R.id.fade_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.fade_effects));
            activateXONImageView(R.string.fade_effects);
         }
      });
      
      button = (Button) findViewById(R.id.color_effects_btn);
      m_ImageSubProcessButtons.put(R.id.color_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_effects_btn; 
            m_ActiveSubMenu = R.id.color_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.color_effects));
            activateXONImageView(R.string.color_effects);
         }
      });

      button = (Button) findViewById(R.id.funny_effects_btn);
      m_ImageSubProcessButtons.put(R.id.funny_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_effects_btn; 
            m_ActiveSubMenu = R.id.funny_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.funny_effects));
            activateXONImageView(R.string.funny_effects);
         }
      });
      
      button = (Button) findViewById(R.id.light_effects_btn);
      m_ImageSubProcessButtons.put(R.id.light_effects_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_effects_btn; 
            m_ActiveSubMenu = R.id.light_effects_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.light_effects));
            activateXONImageView(R.string.light_effects);
         }
      });
   }
   
   private void initImageFilters()
   {
      final String selTip = (String)  getString(R.string.selected_txt);
      Button button = (Button) findViewById(R.id.image_filters_btn);
      ScrollView scrollView = (ScrollView) findViewById(R.id.image_filters_scroll);      
      m_ScrollMenu.put(R.id.image_filters_btn, scrollView);
      m_ImageProcessButtons.put(R.id.image_filters_btn, button);
      button.setVisibility(View.VISIBLE);      
      button .setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            boolean showScrollMenu = true;
            if (m_ActiveMainMenu == R.id.image_filters_btn && m_ScrollMenuOn) 
               showScrollMenu = false;
            else if (m_ActiveMainMenu != R.id.image_filters_btn)
            { 
               m_ActiveMainMenu = R.id.image_filters_btn; 
               m_ActiveSubMenu = R.id.border_filters_btn; highlightButton(true);
               UIUtil.showShortMessage(XON_IM_UI.this, 
                                       selTip+" "+getString(R.string.border_filters));
               activateXONImageView(R.string.border_filters);
            }
            m_ActiveMainMenu = R.id.image_filters_btn; highlightButton(showScrollMenu);
//            Button button = (Button) findViewById(R.id.border_filters_btn);      
//            button.performClick();
         }
      });
      
      button = (Button) findViewById(R.id.border_filters_btn);
      m_ImageSubProcessButtons.put(R.id.border_filters_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_filters_btn; 
            m_ActiveSubMenu = R.id.border_filters_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.border_filters));
            activateXONImageView(R.string.border_filters);
         }
      });
      
      button = (Button) findViewById(R.id.color_filters_btn);
      m_ImageSubProcessButtons.put(R.id.color_filters_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_filters_btn; 
            m_ActiveSubMenu = R.id.color_filters_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.color_filters));
            activateXONImageView(R.string.color_filters);
         }
      });

      button = (Button) findViewById(R.id.distort_filters_btn);
      m_ImageSubProcessButtons.put(R.id.distort_filters_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_filters_btn; 
            m_ActiveSubMenu = R.id.distort_filters_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+getString(R.string.distort_filters));
            activateXONImageView(R.string.distort_filters);
         }
      });
      
      button = (Button) findViewById(R.id.blur_filters_btn);
      m_ImageSubProcessButtons.put(R.id.blur_filters_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            m_ActiveMainMenu = R.id.image_filters_btn; 
            m_ActiveSubMenu = R.id.blur_filters_btn; highlightButton(false);
            UIUtil.showShortMessage(XON_IM_UI.this, 
                                    selTip+" "+" "+getString(R.string.blur_filters));
            activateXONImageView(R.string.blur_filters);
         }
      });
   }
      
   public void highlightButton(boolean showScrollMenu)
   {
      int bgSelResId = R.color.XONTitleSelBackground;
//      int bgMenuResId = R.color.XONTitleBackground;
//      int bgSubMenuResId = R.color.XONSubTitleBackground;
      
      m_ScrollMenuOn = showScrollMenu;
      Collection<Button> buttons = m_ImageProcessButtons.values();
      Iterator<Button> buttonIter = buttons.iterator();
//      while (buttonIter.hasNext()) buttonIter.next().setBackgroundResource(bgMenuResId);
      while (buttonIter.hasNext()) buttonIter.next().setBackgroundColor(Color.TRANSPARENT);
      Button homeBtn = m_ImageProcessButtons.get(R.id.home_btn);
      homeBtn.setBackgroundResource(R.color.XONHomeButtonBG); 
      
      buttons = m_ImageSubProcessButtons.values();
      buttonIter = buttons.iterator();
//      while (buttonIter.hasNext()) buttonIter.next().setBackgroundResource(bgSubMenuResId);
      while (buttonIter.hasNext()) buttonIter.next().setBackgroundColor(Color.TRANSPARENT);
      
      Collection<ScrollView> scrollMenus = m_ScrollMenu.values();
      Iterator<ScrollView> scrollIter = scrollMenus.iterator();
      while (scrollIter.hasNext()) scrollIter.next().setVisibility(View.INVISIBLE);
      
      Button button = m_ImageProcessButtons.get(m_ActiveMainMenu);
//      int selBGColor = R.style.XONTransperantBackground;
//      if (button != null) button.setBackgroundResource(bgSelResId);
      if (m_ActiveMainMenu == R.id.home_btn) homeBtn.setTextColor(Color.BLACK);
      else homeBtn.setTextColor(Color.WHITE);
      if (button != null) button.setBackgroundColor(
                                 Color.parseColor(XONPropertyInfo.getString(bgSelResId)));
      
      button = m_ImageSubProcessButtons.get(m_ActiveSubMenu);
//      if (button != null) button.setBackgroundResource(bgSelResId);
      if (button != null) button.setBackgroundColor(
            Color.parseColor(XONPropertyInfo.getString(bgSelResId)));
//      if (button != null) button.setBackgroundColor(selBGColor);
      
      ScrollView scrollMenu = null;
      if (showScrollMenu) {
         scrollMenu = m_ScrollMenu.get(m_ActiveMainMenu);      
         if (scrollMenu != null) { 
            scrollMenu.bringToFront(); 
            scrollMenu.setVisibility(View.VISIBLE);
//            if (m_ActiveMainMenu == R.id.home_btn) {            
//               if (!m_XONImage.isImageFilterSpecified()) 
//                  m_ImageSubProcessButtons.get(R.id.xon_save_btn).setClickable(false);
//            }
         }
      } 
      m_ActiveScrollView = scrollMenu;
      if (m_XONImage != null) m_XONImage.resetActiveImageFilter();
   }
       
   private void activateXONImageView(int imgFltrResId)
   {
      activateXONImageView(m_ActiveMainMenu, imgFltrResId);
   }
   
   private void activateXONImageView(int mainMenu, int imgFltrResId)
   {
      calcFrameLayoutSize(); 
      if (m_LayoutSize == null && m_LayoutRect == null) {
         XONUtil.logDebugMesg("Unable Apply Image "+XONPropertyInfo.getString(imgFltrResId));
         return;
      }
      XONUtil.logDebugMesg("Apply Image "+XONPropertyInfo.getString(imgFltrResId));
      if (m_XONImage == null) {
         XONUtil.logDebugMesg("Creating XONImage for: "+m_ImageUri);
         Dimension thumbSize = XONPropertyInfo.getThumbSize();         
         m_XONImage = new XONImageHolder(m_ImageUri, ViewType.CanvasView, m_LayoutSize, 
                                         thumbSize.width, thumbSize.height);
         m_XONImage.deriveDeviceCTM(0, null, ScalingFactor.FillLogic.FIT_IMAGE_IN_BBOX);
      } else {
         XONUtil.logDebugMesg("View Type: "+m_XONImage.m_ViewType);
         if (!m_XONImage.m_ViewType.equals(ViewType.CanvasView)) 
            m_XONImage.setPanelSize(m_LayoutSize, ViewType.CanvasView);
      }
         
      if (m_XONImageFilterView == null) {
         XONHorzScrollView listview = (XONHorzScrollView) findViewById(R.id.filter_listview);
         // Reg XONImageFilterView object as an XON Adapter to process call backs from XONHorzScroll
         m_XONImageFilterView =  XONImageFilterView.getInstance(this, m_XONCanvasView, listview);
      }
      m_XONImage.resetDisplayImage(false);
      m_XONImageFilterView.activateXONImageFilterView(mainMenu, imgFltrResId, m_XONImage);
      
      // Building up the Template Filters in Background Thread
      if (!m_ImageFilterActivated ) {
         Map<String, Object> data = new HashMap<String, Object>();
         data.put("Request", "BuildTemplateFiltersReq");
         ThreadCreator.getInstance().createThread(this, data, true);
      }
      m_ImageFilterActivated = true;
   }
   
   private void calcFrameLayoutSize()
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
      XONUtil.debugLog(Log.VERBOSE, "Layout Wt: "+layoutWt+" Ht: "+layoutHt+
                                              " Layout Rect: "+m_LayoutRect);
      XONUtil.debugLog(Log.VERBOSE, "Rect Wt: "+m_LayoutRect.width()+" Ht: "+
                                              m_LayoutRect.height());
      m_XONCanvasView.m_LayoutRect = m_LayoutRect;
   }
   
//   @Override
//   public boolean onCreateOptionsMenu(Menu menu) 
//   {
//      getMenuInflater().inflate(R.menu.activity_xon__im__ui, menu);
//      return true;
//   }

   @Override
   public void processThreadRequest(Map<String, Object> data)
   {
      String req = (String) data.get("Request");
      XONUtil.logDebugMesg("Req is: "+req);
      if (req.equals("BuildTemplateFiltersReq")) {
         RawCacheManager.getInstance();
         if (m_XONIMViewType.equals(XONIMViewType.Basic)) {
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Button button = (Button) findViewById(
                                           R.id.basic_popular_effects_btn);      
                  button.performClick();
               }
            });
         }
         m_XONImageManager.buildTemplateFilters();
      } else if (req.equals("CheckFilterActivatedReq")) {
         while(!m_ImageFilterActivated) {
            XONUtil.put2Sleep();
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Button button = (Button) findViewById(R.id.basic_popular_effects_btn);      
                  if (m_XONIMViewType.equals(XONIMViewType.Advanced))
                     button = (Button) findViewById(R.id.image_enhance_btn);
                  button.performClick();
               }
            });
         }
      }
   }

   @Override
   public void onClick(int actionBut)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void onOK(int dialogTitleResId, View customView)
   {
      TemplateFilterHolder templFltrHldr = null; TemplateFilterHandlerImpl templFltr = null;
      EditText editTxtView = (EditText) customView.findViewById(R.id.save_effects_name);
      String efctsName = editTxtView.getText().toString(); 
      XONUtil.logDebugMesg("Effects Name Set by User: "+efctsName);
      int efcsSize = XONPropertyInfo.getIntRes(R.string.num_effects_char);
      if (efctsName.length() > efcsSize) efctsName = efctsName.substring(0, efcsSize);
      XONUtil.logDebugMesg("Effects Name Resized: "+efctsName);
      templFltrHldr = TemplateFilterHolder.getInstance(ImageFilter.PersonalizedTemplateFilter);
      if (templFltrHldr.getTemplateFilter(efctsName) != null) {
         String ackMesg = efctsName+" "+XONPropertyInfo.
                                        getString(R.string.ImageEffectsNotSavedMesg);
         XONPropertyInfo.showAckMesg(R.string.ImageEffectsNotSaved, ackMesg, null);
         return;
      }
      Vector<ImageFilterHandler> usdImgEfcts = m_XONImage.getUsedImageEffects();
      templFltr = new TemplateFilterHandlerImpl(templFltrHldr.getNextCounter(), efctsName, 
                                                usdImgEfcts);
      File templFltrSerFile = m_XONImageManager.serXONFilter(efctsName, templFltr);
      if (templFltrSerFile != null) templFltrHldr.addTemplateFilter(templFltrSerFile);
   }

   @Override
   public void onCancel(int dialogTitleResId)
   {
      // TODO Auto-generated method stub
      
   }
}
