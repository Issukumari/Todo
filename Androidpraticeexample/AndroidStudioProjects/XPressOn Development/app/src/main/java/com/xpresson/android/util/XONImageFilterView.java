package com.xpresson.android.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.xpresson.android.ColorPickerDialog;
import com.xpresson.android.XON_IM_UI;
import com.xpresson.android.XON_Main_UI;
import com.xpresson.android.ColorPickerDialog.OnColorChangedListener;
import com.xpresson.android.R;
import com.xpresson.android.util.XONImageFilterDef.ImageFilter;
import com.xpresson.android.util.XONImageHolder.ImageType;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class XONImageFilterView implements XONUIAdapterInterface, XONImageProcessor, 
                                           ThreadInvokerMethod, OnColorChangedListener,
                                           XONClickListener
{
   private enum AsyncTaskReq {
      // ApplyFilterOnThumbView - Applies filter on Thumb View
      // ApplyNewFilterOnCanvasView - This task is used if new Filter is added
      // ApplyNewFilterOnCanvasView - This task is used if a filter is removed
      // ShowImageInCanvas - This task is used to show the current Displayable Image
      ApplyFilterOnThumbView, ApplyNewFilterOnCanvasView, ApplyFilterOnCanvasView, 
      ShowImageInCanvas
   }
   
   // The Main Activity that invoked this view
   XON_IM_UI m_MainActivity;
   
   // View on which the image is drawn
   private XONCanvasView m_XONCanvasView;
   
   // Singleton Object and real time based on the Image Filter Grouping the 
   // Views are drawn on the XONScrollView
   private static XONImageFilterView m_XONImageFilterView = null;
   
   // This object is the adapter to the Image Filters shown in the Horz Scroll
   XONHorzListAdapter m_XONImageFilterAdapter;
   
   // Image Filter Grouping identifying the image filters
   private String m_ImageFilterGrouping;
   private String[] m_ImageFilters, m_ImageFiltersDispName;
   
   // Image Selected by the user on which the filter has to apply
   XONImageHolder m_XONImage;
   
   // Map of Image Filter to the corresponding View
   Map<String, View> m_ImageFilterView;
   
   // This Image Process Handler is for the ThumbView
   XONImageProcessHandler m_XONImageProcessHandler;
   XONImageProcessHandler m_XONCanvasImageProcessHandler;
   
   private XONImageFilterView(XON_IM_UI act, XONCanvasView canvas)
   {
      m_MainActivity = act; m_XONCanvasView = canvas; 
      m_ImageFilterView = new HashMap<String, View>();
      m_XONImageProcessHandler = new XONImageProcessHandler(
                                     m_MainActivity.getApplicationContext());
      m_XONImageProcessHandler.setLoadingImage(R.drawable.empty_photo);
      m_XONCanvasImageProcessHandler = new XONImageProcessHandler(
                                           m_MainActivity.getApplicationContext());
      m_XONCanvasImageProcessHandler.setImageFadeIn(false);
      m_XONCanvasView.setImageFilterScrollView(this);
   }
   
   public static XONImageFilterView getInstance() { return m_XONImageFilterView; }
   
   public static XONImageFilterView getInstance(XON_IM_UI act, XONCanvasView canvas, 
                                                XONHorzScrollView imgFltrListView)
   {
      if (m_XONImageFilterView != null) m_XONImageFilterView.resetCache(); reset();
      if (m_XONImageFilterView != null) {
         m_XONImageFilterView.m_XONImageFilterAdapter = null;
      } else { canvas = new XONCanvasView(act);
               m_XONImageFilterView = new XONImageFilterView(act, canvas); }
      XONHorzListAdapter adapter = new XONHorzListAdapter(m_XONImageFilterView, 
                                                          imgFltrListView);
      m_XONImageFilterView.m_XONImageFilterAdapter = adapter;
      return m_XONImageFilterView;
   }  
   
   public static void reset()
   {
      m_XONImageFilterView = null;
   }
   
   public void resetCache()
   {
      m_MainActivity = null; 
      if (m_XONCanvasView != null) m_XONCanvasView.resetCache(); m_XONCanvasView = null; 
      if (m_ImageFilterView != null) m_ImageFilterView.clear(); m_ImageFilterView = null;
      m_XONImageProcessHandler = null; m_XONCanvasImageProcessHandler = null;
   }
   
   public XONCanvasView getCanvasView() { return m_XONCanvasView; }
      
   boolean m_ReActivateView = true; 
   int m_ImageFilterGroupResId;
   int m_MainMenuResId;
   
   public void activateXONImageFilterView(int mainMenu, int imgFltrGrpResId, 
                                          XONImageHolder xonImgHldr)
   {
      m_ImageFilterView.clear(); m_MainMenuResId = mainMenu; 
      m_ImageFilterGroupResId = imgFltrGrpResId;
      XONPropertyInfo.activateProgressBar(true);      
      m_XONImage = xonImgHldr; m_XONImage.resetActiveImageFilter(); 
      String imgFltGrp = m_MainActivity.getResources().getString(imgFltrGrpResId); 
      XONUtil.logDebugMesg("ImgFltGrp: "+imgFltGrp);
      m_ImageFilterGrouping = XONUtil.replaceSpace(imgFltGrp, "");      
      TemplateFilterHolder basicTemplFltr = null, userTemplFltr = null;
      if (m_MainMenuResId == R.id.quick_effects_btn && 
          imgFltrGrpResId == R.string.popular_effects) {
            basicTemplFltr = TemplateFilterHolder.getInstance(ImageFilter.
                                                              PopularTemplateFilter);
            m_ImageFilters = basicTemplFltr.getImageFilters();
            m_ImageFiltersDispName = m_ImageFilters;
      } else if (m_MainMenuResId == R.id.quick_effects_btn && 
                 imgFltrGrpResId == R.string.user_effects) {
            userTemplFltr = TemplateFilterHolder.getInstance(ImageFilter.
                                                             PersonalizedTemplateFilter);
            m_ImageFilters = userTemplFltr.getImageFilters();
            m_ImageFiltersDispName = m_ImageFilters;
            if (m_ImageFilters.length == 0)
               XONPropertyInfo.showAckMesg(R.string.NoSavedEffectsTitle, 
                                           R.string.NoSavedEffectsMesg, null);
      } else { 
         int imgFltrResId = XONImageFilterDef.m_ImageFilterResId.get(imgFltrGrpResId);
         m_ImageFilters = m_MainActivity.getResources().getStringArray(imgFltrResId); 
         imgFltrResId = XONImageFilterDef.m_ImageFilterDispResId.get(imgFltrGrpResId);
         m_ImageFiltersDispName = m_MainActivity.getResources().getStringArray(imgFltrResId); 
         if (m_MainMenuResId == R.id.quick_effects_btn && 
             imgFltrGrpResId == R.string.adv_effects) {      
            if (m_ImageFilters.length == 0) 
               XONPropertyInfo.showAckMesg(R.string.FeatureCommingTitle, 
                               XONPropertyInfo.getString(R.string.adv_effects)+" "+ 
                               XONPropertyInfo.getString(R.string.FeatureCommingMesg), null);
         }
      }
      XONUtil.logDebugMesg("Image Fltr Grp: "+m_ImageFilterGrouping);
      if (m_ImageFilters.length > 0) m_XONImageFilterAdapter.reset();
      else resetView(); 
      
      m_XONCanvasView.setXONImage(m_XONImage); 
//      m_XONCanvasView.setImageBitmap(m_XONImage.m_DisplayImage);
//      m_XONCanvasView.invalidate();
      
//      m_XONCanvasView.applyFilterAndUpdate();
      m_XONCanvasImageProcessHandler.setLoadingImage(m_XONImage.getDisplayImage());      
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("AsyncTaskReq", AsyncTaskReq.ShowImageInCanvas);
      m_XONCanvasImageProcessHandler.processImage(this, data, m_XONCanvasView);

//      m_XONImageFilterAdapter.m_XONHorzScrollView.setVisibility(View.VISIBLE);
      Map<String, Object> reqData = new HashMap<String, Object>();
      ThreadCreator.getInstance().createThread(this, reqData, true);
   }
   
   public void refresh()
   {
      m_XONImageFilterAdapter.m_XONHorzScrollView.invalidate();
   }
   
   public void resetView()
   {
      m_ImageFilterView.clear();
      m_XONImageFilterAdapter.reset();
      m_XONImageFilterAdapter.m_XONHorzScrollView.invalidate();
   }
   
   public boolean isImageFilterValid() { return isImageFilterValid(-1); } 
   public boolean isImageFilterValid(int position)
   {
      boolean isValid = false;      
      if (m_ImageFilterGrouping != null && m_ImageFilters != null && 
          m_ImageFilters.length > 0) isValid = true;
      if (position == -1 || !isValid) return isValid;
      if (m_ImageFilters[position] == null) isValid = false;
      return isValid;
   }
   
   @Override
   public void processThreadRequest(Map<String, Object> data)
         throws Exception
   {
      m_MainActivity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            m_XONImageFilterAdapter.m_XONHorzScrollView.setVisibility(View.VISIBLE);
         }
      });
   }
   
   @Override
   public int getCount()
   {
      int cnt = 0;
      if (isImageFilterValid()) cnt = m_ImageFilters.length;
//      if (cnt == 0) return 0;
      XONUtil.debugLog(Log.VERBOSE, "ImageFilterGrouping: "+m_ImageFilterGrouping+
                       " Image Filter Count: "+cnt);
      return cnt;
   }

   @Override
   public Object getItem(int position)
   {
      String imgFltr = "";
      if (isImageFilterValid(position)) imgFltr = m_ImageFilters[position];
      XONUtil.debugLog(Log.VERBOSE, "ImageFilterGrouping: "+m_ImageFilterGrouping+
                       " Image Filter: "+imgFltr);
      return imgFltr;
   }

   @Override
   public long getItemId(int position)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public View getView(int position)
   {
      String imgFltr = "", imgFltrOpt = null;
      if (isImageFilterValid(position)) imgFltr = m_ImageFilters[position];
      XONUtil.logDebugMesg("ImageFilterGrouping: "+m_ImageFilterGrouping+
                           " Image Filter Name: "+imgFltr);
      if (imgFltr == null || imgFltr.length() <= 0) return null;
      
      String imgFltrKey = imgFltr; 
      imgFltrOpt = XONImageFilterDef.getImageFilter(m_MainMenuResId, m_ImageFilterGrouping, 
                                                    imgFltr);
      XONUtil.logDebugMesg("Image Filter: "+imgFltrOpt);
      if (imgFltrOpt != null) imgFltrKey = imgFltrOpt.toString();
      View fltrView = m_ImageFilterView.get(imgFltrKey);
      if(fltrView != null) return fltrView; 
      // To inflate a view you use the LayoutInflater class.
      LayoutInflater inflater = m_MainActivity.getLayoutInflater();
      // To actually create the view object you use the inflate() method of the 
      // LayoutInflater. Here the inflate method is used to create each list item of
      // the Horizontal List View
      fltrView = inflater.inflate(R.layout.xon_horz_list_view, null);
      if (imgFltrOpt != null && m_XONImage.isImageFilterActive(imgFltrOpt)) 
         setImageFilterBG(fltrView, true); 
      ImageView imgView = (ImageView) fltrView.findViewById(R.id.image);
      if (m_XONImage.m_ImageType.equals(ImageType.Potrait))
         imgView.setScaleType(ScaleType.CENTER_CROP);
//      Bitmap fltrImg = m_XONImage.m_ThumbviewImage;  
      if (imgFltrOpt != null) {
         Map<String, Object> data = new HashMap<String, Object>();
         data.put("AsyncTaskReq", AsyncTaskReq.ApplyFilterOnThumbView);
         data.put("ImageFilterOption", imgFltrOpt);
         m_XONImageProcessHandler.processImage(this, data, imgView);
//         fltrImg = m_XONImage.applyImageFilter(imgFltrOpt);
      }
//      imgView.setImageBitmap(fltrImg);
      TextView title = (TextView) fltrView.findViewById(R.id.title);
      title.setText(m_ImageFiltersDispName[position]);
//      m_ImageFilterView.put(imgFltrKey, fltrView);
      return fltrView;
   }

   private int m_SelectedFilterPos = -1;
   
   // Notified when a long press occurs with the initial on down MotionEvent that trigged it.
   @Override
   public void onLongPress(MotionEvent e, long downTime, int position)
   {
      m_SelectedFilterPos = position;
      String userImgEfctsGrp = XONUtil.replaceSpace(XONPropertyInfo.
                                                    getString(R.string.user_effects), ""); 
      if (!m_ImageFilterGrouping.equals(userImgEfctsGrp))return;      
      long diffTime = System.currentTimeMillis()-downTime;
      XONUtil.logDebugMesg("Diff Time: "+diffTime);
      if (diffTime > XONPropertyInfo.getIntRes(R.string.popup_scroll_time)) {
         UIUtil.createSingleChoiceDialog(XONPropertyInfo.m_SubMainActivity, 
                R.drawable.alert_dialog_icon, R.string.user_effects_actions, 
                R.array.user_effects_action_list, this).show();         
      }
   }
   
   View m_SelectedView; String m_SelImgFltrOpt = "";
   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int position, long rowId)
   {
      if (!m_MainActivity.equals(XONPropertyInfo.m_SubMainActivity)) 
         XONPropertyInfo.m_SubMainActivity = m_MainActivity;
      String imgFltr = ""; String imgFltrOpt = null; 
      boolean showColorDialog = false, setActive = true;
      m_XONImage.resetDisplayImage(false);
      if (isImageFilterValid(position)) imgFltr = m_ImageFilters[position];
      XONUtil.logDebugMesg("ImageFilterGrouping: "+m_ImageFilterGrouping+
                           " Image Filter: "+imgFltr);
      imgFltrOpt = XONImageFilterDef.getImageFilter(m_MainMenuResId, m_ImageFilterGrouping, 
                                                    imgFltr);
      XONUtil.logDebugMesg("ImageFilterGrouping: "+m_ImageFilterGrouping+
                           " Image Fltr Name: "+imgFltr+" Image Filter: "+imgFltrOpt);      
//      View fltrView = this.getView(position); 
      AsyncTaskReq task = AsyncTaskReq.ApplyNewFilterOnCanvasView;
      if (this.m_MainActivity.getClass().getSimpleName().equals("XON_IM_UI"))
         ((XON_IM_UI)this.m_MainActivity).highlightButton(false);
      if (m_XONImage.isImageFilterActive(imgFltrOpt)) {
         setActive = false; m_XONImage.removeImageFilter(imgFltrOpt);
         task = AsyncTaskReq.ApplyFilterOnCanvasView;
         XONPropertyInfo.setToastMessage(imgFltr+XONPropertyInfo.
                                                 getString(R.string.ImageFilterDeSelMesg));
         
      } else {
         Vector<String> negateFltrs = new Vector<String>();
         setActive = m_XONImage.addImageFilter(imgFltrOpt, negateFltrs);
         XONUtil.logDebugMesg("Negate Filters: "+negateFltrs);
         if (negateFltrs != null && negateFltrs.size() > 0) resetView(negateFltrs);
         if (!setActive) XONPropertyInfo.setToastMessage(R.string.FilterNotApplied);
         else XONPropertyInfo.setToastMessage(imgFltr+XONPropertyInfo.
                                                      getString(R.string.ImageFilterSelMesg));
      }
      if (m_XONImage.isImageFilterParamActive()) {
         XONUtil.logDebugMesg("Image Filter Param is set: "+imgFltrOpt);
         if (ImageFilter.showColorPickerDialog(imgFltrOpt)) {
            XONUtil.logDebugMesg("Show Color Dialog: "+imgFltrOpt);
            int color = m_XONImage.getFilterColorParam(); 
            if (color != -1) { showColorDialog = true;
                               new ColorPickerDialog(m_MainActivity, this, color).show(); }
         }
         if (m_XONImage.isCircularImpactFilter())
            XONPropertyInfo.showCircularFilterSlideMesg();
         else XONPropertyInfo.showSlideMesg();
      }
      setImageFilterBG(view, setActive); m_SelectedView = view; m_SelImgFltrOpt = imgFltrOpt;
      if (!showColorDialog) processImageFilter(task, true);
//      m_XONCanvasView.applyFilterAndUpdate();
   }
   
   private void processImageFilter(AsyncTaskReq task, boolean uithread)
   {
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("AsyncTaskReq", task);
      m_XONCanvasImageProcessHandler.processImage(this, data, m_XONCanvasView);
      if (uithread) XONPropertyInfo.activateProgressBar(true);
      else XONPropertyInfo.activateProgressBarOnUIThread(true);
   }
   
   private void resetView(Vector<String> negateFltrs)
   {
      XONUtil.logDebugMesg("ImgFltrGrp: "+m_ImageFilterGrouping+" Negate Filters: "+negateFltrs);
      for (int i = 0; i < negateFltrs.size(); i++)
      {
         String imgFltrOpt = negateFltrs.elementAt(i);
         if (imgFltrOpt.contains(m_ImageFilterGrouping)) {
            if (m_SelImgFltrOpt.equals(imgFltrOpt)) {
               if(m_SelectedView != null) setImageFilterBG(m_SelectedView, false); 
            } else {
               View fltrView = m_ImageFilterView.get(imgFltrOpt);
               XONUtil.logDebugMesg("ImgFltr: "+imgFltrOpt+" view: "+fltrView);
               if(fltrView != null) setImageFilterBG(fltrView, false); 
            }
         }
      }
   }
   
   // Resetting the Background for all the Image Filters
   private void setImageFilterBG(View fltrView, boolean isActive)
   {
      TextView title = (TextView) fltrView.findViewById(R.id.title);
      if (isActive) title.setBackgroundResource(R.color.XONSelBackground);
      else { title.setBackgroundResource(R.color.XONTextSelColor);
             XONUtil.debugLog(Log.VERBOSE, "Fltr Title: "+title); }
   }
   
   @Override
   public void colorChanged(int color) 
   {
      XONUtil.logDebugMesg("Color: "+color+" RGB val: "+XONImageUtil.printRGBPixel(color));
      m_XONImage.setFilterColorParam(color);
      processImageFilter(AsyncTaskReq.ApplyNewFilterOnCanvasView, false);      
//      XONPropertyInfo.activateProgressBar(true);
//      m_XONCanvasView.applyLastFilterAndUpdate();
   }
   

   @Override
   public void onNothingSelected(AdapterView<?> parent)
   {
      XONUtil.debugLog(Log.VERBOSE, "ImageFilterGrouping: "+m_ImageFilterGrouping);      
   }

   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position,
         long rowId)
   {
//      String imgFltr = "", imgFltrOpt = null; boolean setActive = true;
//      if (isImageFilterValid(position)) imgFltr = m_ImageFilters[position];
//      XONUtil.debugLog(Log.VERBOSE, "ImageFilterGrouping: "+m_ImageFilterGrouping+
//                       " Image Filter: "+imgFltr);            
//      imgFltrOpt = XONImageFilterDef.getImageFilter(m_MainMenuResId, m_ImageFilterGrouping, 
//                                                    imgFltr);
//      if (m_XONImage.isImageFilterActive(imgFltrOpt)) setActive = false; 
//      int statusMseg = setActive ? R.string.ImageFilterSelMesg : 
//                                   R.string.ImageFilterDeSelMesg;
//      setToastMessage(imgFltr+XONPropertyInfo.getString(statusMseg));
   }
   
   @Override
   public Bitmap processImage(Map<String, Object> data)
   {
      AsyncTaskReq asyncTaskReq = (AsyncTaskReq) data.get("AsyncTaskReq");   
      try {
         switch(asyncTaskReq)
         {
            case ApplyFilterOnThumbView :
               String imgFltrOpt = (String)data.get("ImageFilterOption");
               Bitmap fltrImg = m_XONImage.m_ThumbviewImage;  
               fltrImg = m_XONImage.applyImageFilter(imgFltrOpt);
               return fltrImg;
            case ApplyNewFilterOnCanvasView :
               m_XONImage.applyNewFilter();
               m_XONCanvasView.resetMemParams();
               return m_XONImage.getDisplayImage();
            case ApplyFilterOnCanvasView :
               m_XONImage.applyFilter();
               m_XONCanvasView.resetMemParams();
               return m_XONImage.getDisplayImage();
            case ShowImageInCanvas:
               m_XONCanvasView.resetMemParams();
               return m_XONImage.getDisplayImage();
            default:
               break;      
         }
      } catch(Throwable ex) {
         XONUtil.logDebugMesg("Mem Cache Used: "+m_XONImage.getMemUsage());
         XONUtil.logError("Error while applying task: "+asyncTaskReq, ex);
         XONPropertyInfo.showErrorAndRoute(R.string.NoImageFound, this);
      }
//      XONPropertyInfo.activateProgressBar(false);
      return m_XONImage.getDisplayImage();
   }
   
   public void setToastMessage(String mesg)
   {
      Toast.makeText(m_MainActivity,  mesg, Toast.LENGTH_SHORT).show();         
   }

   private void deleteUserEffect() 
   {
      String imgFltr = ""; String imgFltrOpt = null; int position = m_SelectedFilterPos;
      if (isImageFilterValid(position)) imgFltr = m_ImageFilters[position];
      imgFltrOpt = XONImageFilterDef.getImageFilter(m_MainMenuResId, m_ImageFilterGrouping, 
                                                    imgFltr);
      XONUtil.logDebugMesg("ImageFilterGrouping: "+m_ImageFilterGrouping+
                           " Image Fltr Name: "+imgFltr+" Image Filter: "+imgFltrOpt);      
      boolean result = TemplateFilterHolder.delTemplateFilterImpl(imgFltrOpt);
      if (result) {
         activateXONImageFilterView(m_MainMenuResId, m_ImageFilterGroupResId, m_XONImage);
      }
   }
   
   private String m_UserEffectsAction = null;
   
   @Override
   public void onClick(int action) 
   {
      String[] userEffectsActions = XONPropertyInfo.getStringArray(R.array.user_effects_action_list);
      m_UserEffectsAction = userEffectsActions[action];
      XONPropertyInfo.setToastMessage("Selected User Effect Action: "+m_UserEffectsAction);      
   }

   @Override
   public void onOK(int dialogTitleResId, View customView) 
   {
      if (dialogTitleResId == R.string.user_effects_actions) {
         String[] userEffectsActions = XONPropertyInfo.getStringArray(R.array.user_effects_action_list);
         if (m_UserEffectsAction == null) m_UserEffectsAction = userEffectsActions[0];
         if (m_UserEffectsAction.equals(userEffectsActions[0])) deleteUserEffect();
         return;
      }
      IntentUtil.processIntent(XONPropertyInfo.m_SubMainActivity, XON_Main_UI.class);
   }

   @Override
   public void onCancel(int dialogTitleResId) {}
   
}
