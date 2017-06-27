package com.xpresson.android.util;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.xpresson.android.R;
import com.xpresson.android.XON_IM_UI.XONIMViewType;
import com.xpresson.android.util.XONPropertyInfo.ImageOrientation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

// The purpose of this class is drawing on the Canvas object. An easy way to get a Canvas 
// object to drawing on is by overriding the onDraw() method of a View object. Conveniently, 
// this method has a single parameter: the Canvas object. Drawing a Bitmap graphic on a 
// Canvas object is as easy as calling the drawBitmap() method of the Canvas object. 
public class XONCanvasView extends ImageView implements XONGestureAPI, ThreadInvokerMethod, 
                                                        XONImageProcessor, XONClickListener
{
   // Started is initiated on Mouse Down, 
   // InProgress is initiated onScroll
   // In
   private enum ScrollStatus {
      Started, InProgress, Finished 
   }
   private ScrollStatus m_ScrollStatus;
   private long m_ScrollAccessTime;
      
   private enum AsyncTaskReq {
      ApplyFilterOnCanvasView, ShowFilteredImageInCanvas, 
      ShowImageInCanvas, ReApplyFilterOnCanvasView
   }
   
   private enum ThreadProcessReq {
      ProcessFilterUpdate, ProcessFilterMonitor
   }
   
   public static final int PROCESS_FILTER_UPDATE = 0;   
   XONImageHolder m_XONImage;        
   private GestureDetector m_UserGestures;
   
   // Indicates the Point where the screen gesture started
   private Point m_GestureStartPt;
   
   // Indicates the Distance Scrolled it also includes the distance to be scrolled
   private int m_TotalScrolledDistX, m_TotalScrolledDistY; 

   // This holds the Layout Position and Size of the Canvas View
   public Rect m_LayoutRect; 
   
   // The Main Activity that invoked this view
   Activity m_MainActivity;
   
   // Temporary Image till the User Gesture is active from OnDown to OnFling Event.
   // This image will be used for display and further application of Filter params 
   // for the complete onScroll Event
   WeakReference<Bitmap> m_CanvasDisplayImage; 
   WeakReference<Bitmap> m_CanvasFilteredImage;
   float[] m_CanvasDisplayCTM;
   
   // Image Process Handler to apply ImageFilter asynchonously
   XONImageProcessHandler m_XONImageProcessHandler;
   
   public XONCanvasView(Context context)
   {
      super(context);
   }
   
   public XONCanvasView(Activity act) 
   {
      super(act);
      m_CanvasDisplayImage = null; m_CanvasFilteredImage = null; m_MainActivity = act;
      m_UserGestures = new GestureDetector(m_MainActivity, new XONGestureListener(this));
      m_XONImageProcessHandler = new XONImageProcessHandler(
                                     m_MainActivity.getApplicationContext());
      m_XONImageProcessHandler.setImageFadeIn(false);
      m_CanvasDisplayCTM = new float[9];
   }
   
   public void resetCache()
   {
      this.setImageDrawable(null);
      m_CanvasDisplayImage = null; m_CanvasFilteredImage = null; m_MainActivity = null;
      m_UserGestures = null; m_XONImageProcessHandler = null; m_CanvasDisplayCTM = null;
   }
   
   XONImageFilterView m_XONImageFilterView;
   public void setImageFilterScrollView(XONImageFilterView imgFltrScrollView)
   {
      m_XONImageFilterView = imgFltrScrollView;
   }
   
   public void startImageFilterActivity()
   {
      createCanvasFilteredImage();
      m_ScrollStatus = ScrollStatus.Started; 
      Map<String, Object> requestData = new HashMap<String, Object>();
      requestData.put("ThreadProcessReq", ThreadProcessReq.ProcessFilterMonitor);
      XONUtil.logDebugMesg("Request Data: "+requestData);
      m_ScrollAccessTime = XONUtil.getCurrentTime();
      ThreadCreator.getInstance().createThread(this, requestData, true);         
      m_ScrollStatus = ScrollStatus.Started; 
   }
   
   public void resetMemParams()
   {
      m_ScrollStatus = null; m_CanvasDisplayImage = null; m_CanvasFilteredImage = null;
   }
   
   private void createCanvasFilteredImage()
   {
      if (m_CanvasFilteredImage != null && getCanvasFilterImage() != null) return;
      Bitmap bitmap = m_XONImage.getFilteredImage(), newBitmap = null; 
      m_XONImage.m_ScreenCTM.getValues(m_CanvasDisplayCTM);
      XONUtil.logDebugMesg("Original Scale: "+m_XONImage.m_ScreenCTMValues[Matrix.MSCALE_X]);
      newBitmap = XONImageUtil.reducePixelDensity(bitmap);
      if (m_XONImage.m_ScreenCTMValues[Matrix.MSCALE_X] < 1.0f) {
         m_CanvasFilteredImage = new WeakReference<Bitmap>(newBitmap);
         float scale = m_XONImage.getScale(newBitmap, 0, null, null);
         m_CanvasDisplayCTM[Matrix.MSCALE_X] = scale; 
         m_CanvasDisplayCTM[Matrix.MSCALE_Y] = scale;
      } else {
         m_CanvasFilteredImage = new WeakReference<Bitmap>(newBitmap);
         m_CanvasDisplayCTM[Matrix.MSCALE_X] = (float) m_XONImage.m_SelImageSize.width/
                                               (float) newBitmap.getWidth();
         m_CanvasDisplayCTM[Matrix.MSCALE_Y] = (float) m_XONImage.m_SelImageSize.height/
                                               (float) newBitmap.getHeight();
      }
      XONUtil.logDebugMesg("Sel Image Size: "+m_XONImage.m_SelImageSize+
                           " Canvas Filtered Img Wt: "+ newBitmap.getWidth()+" Ht: "+ 
                           newBitmap.getWidth());
      XONUtil.logDebugMesg("Canvas ScaleX: "+m_CanvasDisplayCTM[Matrix.MSCALE_X]+
                           " ScaleY: "+m_CanvasDisplayCTM[Matrix.MSCALE_Y]);
   }
   
   public void setXONImage(XONImageHolder xonImg) 
   {  
      XONUtil.logDebugMesg("XONImage using Util: "+xonImg);
      m_XONImage = xonImg; resetMemParams(); 
   }
   
   public void applyFilterAndUpdate()
   {
      handleScrollGesture(null, AsyncTaskReq.ShowImageInCanvas);
   }
   
   public void applyLastFilterAndUpdate()
   {
      handleScrollGesture(null, AsyncTaskReq.ShowFilteredImageInCanvas);
   }
   
   private void handleScrollGesture(int distX, int distY, boolean useCanvasImage)
   {
      int endX = m_GestureStartPt.x+distX; 
      int endY = m_GestureStartPt.y+distY;
      final Point endPt = new Point(endX, endY);
      AsyncTaskReq asyncTaskReq = AsyncTaskReq.ShowFilteredImageInCanvas;
      if (useCanvasImage) asyncTaskReq = AsyncTaskReq.ApplyFilterOnCanvasView;
      handleScrollGesture(endPt, asyncTaskReq);
   }
   
   private void handleScrollGesture(Point endPt, AsyncTaskReq asyncTaskReq)
   {
      Map<String, Object> requestData = new HashMap<String, Object>();
      requestData.put("ThreadProcessReq", ThreadProcessReq.ProcessFilterUpdate);
      requestData.put("AsyncTaskReq", asyncTaskReq);
      if (endPt != null) requestData.put("EndPt", endPt);
      XONUtil.logDebugMesg("Request Data: "+requestData);
//      m_XONImageProcessHandler.processImage(this, requestData, this);
      ThreadCreator.getInstance().createThread(this, requestData, true);
//      if (asyncTaskReq.equals(AsyncTaskReq.ShowFilteredImageInCanvas))
//         XONPropertyInfo.activateProgressBar(true);      
   }
   
   private Bitmap getCanvasFilterImage()
   {
      if (m_CanvasFilteredImage == null) return null;
      return m_CanvasFilteredImage.get();
   }
   
   public void setCanvasDisplayImage(Bitmap dispImg, float[] dispCTM)
   {
      m_CanvasDisplayImage = new WeakReference<Bitmap>(dispImg);
      m_CanvasDisplayCTM = dispCTM;
   }
   
   private Bitmap getCanvasDisplayImage()
   {
      if (m_CanvasDisplayImage == null) return null;
      return m_CanvasDisplayImage.get();
   }
   
   @Override
   public void processThreadRequest(Map<String, Object> data)
         throws Exception
   {
      ThreadProcessReq reqId = ThreadProcessReq.ProcessFilterMonitor;
      if (data != null) reqId = (ThreadProcessReq) data.get("ThreadProcessReq");
      XONUtil.logDebugMesg("Request: "+reqId+" Scroll Status: "+m_ScrollStatus);
      switch(reqId)
      {
         case ProcessFilterUpdate: 
            final ImageOrientation imgOrient = (ImageOrientation) 
                                               data.get("ImageOrientation"); 
            final AsyncTaskReq asyncTaskReq = (AsyncTaskReq) data.get("AsyncTaskReq");
            if (data.containsKey("EndPt")) {
               Point endPt = (Point) data.get("EndPt");
               m_XONImage.setImageFilterParam(m_GestureStartPt, endPt); 
            }             
            m_MainActivity.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                  Bitmap bitmap = null;
                  switch(asyncTaskReq)
                  {
                     case ApplyFilterOnCanvasView : 
                        if (getCanvasFilterImage() == null) {
                           XONUtil.logDebugMesg("Canvas Filter Image is null");
                           return;
                        }
                        if (getCanvasDisplayImage() != null){
                           if (!getCanvasDisplayImage().equals(getCanvasFilterImage())) {
                              getCanvasDisplayImage().recycle(); 
                              m_CanvasDisplayImage.clear(); 
                           }
                        }
                        bitmap = m_XONImage.applyLastFilter(getCanvasFilterImage());
//                        Dimension imgSz = m_XONImage.m_SelImageSize;
//                        m_CanvasDisplayImage = Bitmap.createScaledBitmap(m_CanvasDisplayImage, 
//                                                      imgSz.width, imgSz.height, false); 
                        m_CanvasDisplayImage = new WeakReference<Bitmap>(bitmap); 
                        break;
                     case ShowFilteredImageInCanvas:
                        m_XONImage.applyLastFilter();
                        XONPropertyInfo.activateProgressBar(false);
                        invalidate();
                     case ReApplyFilterOnCanvasView:                        
                        if (imgOrient != null) {
                           m_XONImage.setOrientation(imgOrient);
                           m_XONImage.applyFilter();
                           XONPropertyInfo.activateProgressBar(false);
                           m_XONImageFilterView.resetView();
                           invalidate();
                        }
                        break;
                     case ShowImageInCanvas:
                        bitmap = m_XONImage.getDisplayImage();
                     default : bitmap = m_XONImage.getDisplayImage();
                  }
//                  XONCanvasView.this.setImageBitmap(bitmap);      
                  XONCanvasView.this.invalidate();
               }
            });
            break;
         case ProcessFilterMonitor :
            boolean applyFilterUpdate = false; 
            int scrollMonTime = XONPropertyInfo.ScrollMonitorTime;
            if (m_ScrollStatus == null) return;
            if (m_ScrollStatus == ScrollStatus.Finished) applyFilterUpdate = true;
            else if (m_ScrollStatus == ScrollStatus.InProgress && 
                     XONUtil.getCurrentTime()-m_ScrollAccessTime >= scrollMonTime) 
               applyFilterUpdate = true;
            if (!applyFilterUpdate) { XONUtil.put2Sleep();
                                      processThreadRequest(data); }
            else {               
               XONUtil.logDebugMesg("Applying Filter on Original Image");
               m_MainActivity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     if (isScrollActive()) { 
                        XONUtil.put2Sleep();
                        try { processThreadRequest(null); } catch(Exception ex) {} 
                     } else { 
                        m_ScrollStatus = null; m_CanvasDisplayImage = null;
                        m_XONImage.applyLastFilter(); XONCanvasView.this.invalidate();
                        XONUtil.logDebugMesg("The Monitor Thread has finished and returned");
                     }
                  }
               });
            }
            break;
         default : break;
      }
      
   }
   
   private boolean isScrollActive()
   {
      int scrollMonTime = XONPropertyInfo.ScrollMonitorTime;
      if (m_ScrollStatus != null && m_ScrollStatus == ScrollStatus.InProgress && 
          XONUtil.getCurrentTime()-m_ScrollAccessTime <= scrollMonTime) return true;
      return false;      
   }

   @Override
   public Bitmap processImage(Map<String, Object> data)
   {
      AsyncTaskReq asyncTaskReq = (AsyncTaskReq) data.get("AsyncTaskReq");
      XONUtil.logDebugMesg("Data: "+data);
      switch(asyncTaskReq)
      {
         case ApplyFilterOnCanvasView :
            if (!data.containsKey("EndPt")) break;
            Point endPt = (Point) data.get("EndPt");
            m_XONImage.setImageFilterParam(m_GestureStartPt, endPt);
            Bitmap bitmap = getCanvasFilterImage();
            if (bitmap == null) {
               XONUtil.logDebugMesg("CanvasFilterImage is null");
               bitmap = m_XONImage.getSelectedImage();
            }
            Bitmap newBitmap = m_XONImage.applyLastFilter(bitmap);
            m_CanvasDisplayImage = new WeakReference<Bitmap>(newBitmap);             
            return newBitmap;
         case ShowFilteredImageInCanvas:
            m_XONImage.applyLastFilter();
            return m_XONImage.getDisplayImage();
         case ShowImageInCanvas:
            return m_XONImage.getDisplayImage();
         default:
            break;      
      }
      return m_XONImage.getDisplayImage();
   }
   
   // This event is fired when the user touches the screen. Wiring up the GestureDetector 
   // obj so that it receives the motion data it needs to do its gesture recognising magic
   @Override
   public boolean onTouchEvent(MotionEvent event) 
   {
       return m_UserGestures.onTouchEvent(event);
   }
   
   // Called when the view should render its content.   
   @Override
   protected void onDraw(Canvas canvas) 
   {
      if (m_XONImage == null || m_XONImage.getDisplayImage() == null) return;
//      XONUtil.logDebugMesg();
      try {
         Bitmap dispImg = m_XONImage.getDisplayImage(); 
         if (m_XONImage.m_ShowOriginalImage) { canvas.drawBitmap(dispImg, 0.0f, 0.0f, null); 
                                               return; }
         Matrix dispMat = m_XONImage.m_ScreenCTM;
         if (getCanvasDisplayImage() != null) {
   //         int scale = XONPropertyInfo.FilteredImageScale; 
            dispImg = getCanvasDisplayImage();          
            dispMat.setValues(m_CanvasDisplayCTM);
         } // else { XONUtil.logDebugMesg("Canvas Image: "+dispImg); }
         canvas.drawBitmap(dispImg, dispMat, null);
         if (m_XONImage.isCircularImpactFilter()) { 
            Rect rect = m_XONImage.getImpactRect();
            UIUtil.drawRectPath(canvas, rect);          
         }
         dispMat.setValues(m_XONImage.m_ScreenCTMValues);
      } catch(Throwable th){
         XONUtil.logError("Error to draw Image on Canvas", th);
      }
   }
   
   private boolean m_IsImageFileActive = false;
   // XONGestureAPI Implementations
   // Notified when a tap occurs with the down MotionEvent that triggered it. This 
   // is the first event that is fired whenever any activity happens on the gesture.
   public boolean onDown(MotionEvent e)
   {
      if (m_LayoutRect == null || m_XONImage == null)  
      { XONUtil.logDebugMesg("Canvas View is not initialized"); return false; }
      try { m_XONImage.getSelectedImage(); }
      catch (Throwable th) { XONUtil.logDebugMesg("No Image Selected"); return false; }
      m_IsImageFileActive = false;
      if (!m_XONImage.isImageFilterActive()) 
      { XONPropertyInfo.setToastMessage(R.string.NoImageFilterSel); return false; }
      if (!m_XONImage.isImageFilterParamActive())
      { XONPropertyInfo.setToastMessage(R.string.NoImageFilterParam); return true; }
      if (XONObjectCache.getObjectForKey("XONIMViewType") != null) {
         XONIMViewType xonIMViewType = (XONIMViewType) XONObjectCache.
                                                       getObjectForKey("XONIMViewType");
         if (xonIMViewType.equals(XONIMViewType.Basic)) return true;
      }
      m_IsImageFileActive = true;
      Point startPt = new Point(Math.round(e.getX()), Math.round(e.getY()));  
      if (!m_LayoutRect.contains(startPt.x, startPt.y)) return false;
      m_GestureStartPt = startPt; m_TotalScrolledDistX = m_TotalScrolledDistY = 0; 
      XONUtil.logDebugMesg("Start Pt: "+m_GestureStartPt);
      createCanvasFilteredImage();
      XONUtil.logDebugMesg("Canvas Filter Image initiated: "+getCanvasFilterImage());
      if (m_XONImage.isCircularImpactSet(m_GestureStartPt)) 
         XONUtil.logDebugMesg("Circular Impact is Set");
//      m_XONImageProcessHandler.setLoadingImage(m_CanvasFilteredImage);
//      m_CanvasFilteredImage = XONImageUtil.compressImage(m_XONImage.getFilteredImage(true));
//      m_XONImage.isCircularImpactSet(m_GestureStartPt, m_GestureStartPt);
      return true;
   }

   // Notified when a single-tap occurs. This method is called when Single Tap is confirmed.
   public boolean onSingleTapEvent(MotionEvent e)
   {
      if (!m_IsImageFileActive) return false;
      XONUtil.logDebugMesg();
      handleScrollGesture(m_TotalScrolledDistX, m_TotalScrolledDistY, false);   
      m_ScrollStatus = ScrollStatus.Finished;
      return false;
   }

   // Notified when an event within a double-tap gesture occurs, including the down, 
   // move, and up events. This method is called when Double Tap is confirmed.
   public boolean onDoubleTapEvent(MotionEvent e)
   {
      XONUtil.logDebugMesg();
      return false;
   }

   // Notified when a long press occurs with the initial on down MotionEvent that 
   // trigged it.
   public void onLongPress(MotionEvent e)
   {
      XONUtil.logDebugMesg();
      if (m_IsImageFileActive) {
         Point endPt = new Point(Math.round(e.getX()), Math.round(e.getY()));      
         m_ScrollStatus = ScrollStatus.Finished;
         handleScrollGesture(endPt, AsyncTaskReq.ShowFilteredImageInCanvas);   
         XONPropertyInfo.activateProgressBar(true);      
      } else {
//         if (!XONPropertyInfo.m_DevelopmentMode)
//            UIUtil.createSingleChoiceDialog(m_MainActivity, R.drawable.alert_dialog_icon, 
//                   R.string.image_action_text, R.array.image_action_list, this).show();
      }
   }


   // This method is fired when the user has performed a down MotionEvent and not 
   // performed a move or up yet. This event is fired just before the scroll is done.
   public void onShowPress(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
   }


   // Notified when a scroll occurs with the initial on down MotionEvent and the 
   // current move MotionEvent.
   // NOTE  - Both the distance in X and Y is the old point minus the new point, hence 
   //         negative of distance is applied for translation.
   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX, float distY)
   {
      if (!m_IsImageFileActive) return false;
      XONUtil.logDebugMesg("distX: "+distX+" distY: "+distY);      
      synchronized(this){
         if (m_ScrollStatus == null ||  m_ScrollStatus == ScrollStatus.Finished) 
            startImageFilterActivity();
         m_ScrollStatus = ScrollStatus.InProgress; 
         m_ScrollAccessTime = XONUtil.getCurrentTime();
         m_TotalScrolledDistX += (int)-distX; 
         if (m_XONImage.isCircularImpactSet() || m_XONImage.isCircularImpactFilter()) 
            m_TotalScrolledDistY += (int)-distY;      
         else m_TotalScrolledDistY += (int)-distY; 
         handleScrollGesture(m_TotalScrolledDistX, m_TotalScrolledDistY, true);
      }
      return true;
   }


   // A fling gesture is, essentially, leaving velocity on an item that was being 
   // dragged across a screen. The fling gesture is detected only after the users 
   // finger is no longer touching the display. This mthd is used for animation.
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velX, float velY)
   {
      if (!m_IsImageFileActive) return false;
      XONUtil.logDebugMesg();
      m_ScrollStatus = ScrollStatus.Finished;
      handleScrollGesture(m_TotalScrolledDistX, m_TotalScrolledDistY, false);
//      invalidate();
      return true;
   }
   
   private String m_ImageAction = null;

   @Override
   public void onClick(int action)
   {
      String[] imgActions = XONPropertyInfo.getStringArray(R.array.image_action_list);
      m_ImageAction = imgActions[action];
      XONPropertyInfo.setToastMessage("Selected Image Action: "+m_ImageAction);
   }

   @Override
   public void onOK(int dialogTitleResId, View customView)
   {
      String[] imgActions = XONPropertyInfo.getStringArray(R.array.image_action_list);
      if (m_ImageAction == null) m_ImageAction = imgActions[0];
      // Rotate CW
      if (m_ImageAction.equals(imgActions[0])) {
         ImageOrientation imgOrient = XONPropertyInfo.getNext(m_XONImage.m_ImageOrientation);
         setImageOrientation(imgOrient);
      } else if (m_ImageAction.equals(imgActions[1])) { // Rotate CCW 
         ImageOrientation imgOrient = XONPropertyInfo.getPrev(m_XONImage.m_ImageOrientation);
         setImageOrientation(imgOrient);
      } else if (m_ImageAction.equals(imgActions[2])) showOriginalImage();
      m_ImageAction = null;
   }
   
   public void showOriginalImage()
   {
      m_XONImage.resetDisplayImage(true);
//      UIUtil.showMesgDialog(m_MainActivity, R.string.ShowOriginal);
      handleScrollGesture(null, AsyncTaskReq.ShowImageInCanvas);
   }

   public void setImageOrientation(ImageOrientation imgOrient)
   {
      Map<String, Object> requestData = new HashMap<String, Object>();
      requestData.put("ThreadProcessReq", ThreadProcessReq.ProcessFilterUpdate);
      requestData.put("ImageOrientation", imgOrient);
      requestData.put("AsyncTaskReq", AsyncTaskReq.ReApplyFilterOnCanvasView);
      XONUtil.logDebugMesg("Request Data: "+requestData);
//      m_XONImageProcessHandler.processImage(this, requestData, this);
      ThreadCreator.getInstance().createThread(this, requestData, true);
      XONPropertyInfo.activateProgressBar(true);      
   }
   
   
   @Override
   public void onCancel(int dialogTitleResId)
   {
      XONPropertyInfo.setToastMessage(R.string.NoActionMesg);
   }
   
}
