package com.xpresson.android.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.xpresson.android.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

//The purpose of this class is drawing on the Canvas object. An easy way to get a Canvas 
//object to drawing on is by overriding the onDraw() method of a View object. Conveniently, 
//this method has a single parameter: the Canvas object. Drawing a Bitmap graphic on a 
//Canvas object is as easy as calling the drawBitmap() method of the Canvas object. 
public class MainCanvasView  extends ImageView implements XONGestureAPI, ThreadInvokerMethod
{
   // The Main Activity that invoked this view
   Activity m_MainActivity;
   
   // Size of the Canvas View
   Dimension m_CanvasSize;
   
   // List of Resource to be displayed
   private static Vector<MainCanvasData> m_MainCanvasData;
   
   // Canvas view Position being displayed
   private int m_CanvasDataPos = 0;
   private boolean m_NewDataActivated = false;
   private long m_DataActivatedTime;
   
   // De Activate Thread
   public boolean m_DeActivateThread = false, m_ThreadActivated = false;
   
   private GestureDetector m_UserGestures;
   
   public MainCanvasView(Context context)
   {
      super(context);
   }

   public MainCanvasView(Activity act) 
   {
      super(act);
      m_MainActivity = act; 
      populateCanvasData();
      m_UserGestures = new GestureDetector(m_MainActivity, new XONGestureListener(this));
      // The view tree observer is used to get notifications when global events, like layout, 
      // happens. Listener is attached to get the final width of the GridView and then calc the
      // number of columns and the width of each column. The width of each column is variable
      // as the GridView has stretchMode=columnWidth. The column width is used to set the height
      // of each view so we get nice square thumbnails.
      getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() 
      {
         // Register a callback to be invoked when the global layout state or the visibility of 
         // views within the view tree changes
         @Override
         public void onGlobalLayout() 
         {
            m_CanvasSize = new Dimension(getWidth(), getHeight());
            XONUtil.logDebugMesg("Canvas Size: "+m_CanvasSize);
            setThreadActivity(true);
         }
      });
   }
   
   public void resetCache()
   {
      m_UserGestures = null; m_MainActivity = null;
      m_DeActivateThread = true; m_ThreadActivated = false;
      m_MainCanvasData.clear(); m_MainCanvasData = null;
   }
   
   public void reDraw()
   {
      m_MainActivity.runOnUiThread(new Runnable() {
         @Override
         public void run() {
            MainCanvasView.this.invalidate();
         }
      });
   }
   
   public void setThreadActivity(boolean hasFocus)
   {
      if (!hasFocus) { m_DeActivateThread = true; m_ThreadActivated = false; return; }
      if (m_ThreadActivated) return;
      activateCanvasView();
   }
   
   private void activateCanvasView()
   {
      if (m_MainCanvasData.size() == 1) { reDraw(); return; }
      m_CanvasDataPos = 0;  m_NewDataActivated = true; 
      m_ThreadActivated = true; m_DeActivateThread = false; 
      Map<String, Object> requestData = new HashMap<String, Object>();
      requestData.put("CanvasPosId", m_CanvasDataPos);
      XONUtil.logDebugMesg("Thread Activated Request Data: "+requestData);
      ThreadCreator.getInstance().createThread(this, requestData, true);         
   }
   
   private void populateCanvasData()
   {
      if (m_MainCanvasData != null) return;
      m_MainCanvasData = new Vector<MainCanvasData>();
      String[] viewData = XONPropertyInfo.getStringArray(R.array.MainViewData);
      for (int i = 0; i < viewData.length; i++)
         m_MainCanvasData.add(new MainCanvasData(viewData[i]));
   }
   
   private class MainCanvasData
   {
      int m_ResId; String m_Text, m_Action;
      XONImageHolder m_XONImage;
      
      public MainCanvasData(String data)
      {
         m_ResId = -1; m_Text = null; m_Action = null;
         String[] dataArr = data.split("::");
         for (int i = 0; i < dataArr.length; i++) {
            String[] dataVal = dataArr[i].split(":=");
            if (dataVal[0].equals("ImgRes"))
               m_ResId = XONPropertyInfo.getDrawableRes(dataVal[1]);
            else if (dataVal[0].equals("TextRes"))
               m_Text = XONPropertyInfo.getString(dataVal[1]);
            else if (dataVal[0].equals("Action")) m_Action = dataVal[1];
         }
      }
      
      public XONImageHolder getDisplayImage()
      {
         if (m_XONImage != null) return m_XONImage;
         m_XONImage = new XONImageHolder(m_MainActivity.getResources(), m_ResId, 
                                         m_CanvasSize);
         return m_XONImage;
      }
   }

   // Called when the view should render its content.   
   @Override
   protected void onDraw(Canvas canvas) 
   {
      if (m_MainCanvasData == null) return;
      MainCanvasData data = m_MainCanvasData.elementAt(m_CanvasDataPos);
      XONImageHolder xonImg = data.getDisplayImage();
      canvas.drawBitmap(xonImg.m_DisplayImage, xonImg.m_ScreenCTM, null);
      if (m_DataActivatedTime == -1) m_DataActivatedTime = XONUtil.getCurrentTime();
   }
   
   @Override
   public void processThreadRequest(Map<String, Object> requestData)
         throws Exception
   {
      XONUtil.logDebugMesg();
      if (m_NewDataActivated) { m_DataActivatedTime = -1; reDraw(); }
      int sleepTime = XONUtil.put2Sleep(XONPropertyInfo.
                                        getIntRes(R.string.main_view_display_multiple)); 
      if (m_DeActivateThread) { m_ThreadActivated = false; return; }
      if (m_DataActivatedTime != -1 && 
          (XONUtil.getCurrentTime() - m_DataActivatedTime) <= sleepTime) {
         setCanvasDataPos();
         m_NewDataActivated = true; requestData.put("CanvasPosId", m_CanvasDataPos);
      } else m_NewDataActivated = false; 
      processThreadRequest(requestData);       
   }
   
   public synchronized void setCanvasDataPos()
   {
      if (m_CanvasDataPos == m_MainCanvasData.size()-1) m_CanvasDataPos = 0;
      else m_CanvasDataPos++;      
   }

   // This event is fired when the user touches the screen. Wiring up the GestureDetector 
   // obj so that it receives the motion data it needs to do its gesture recognising magic
   @Override
   public boolean onTouchEvent(MotionEvent event) 
   {
       return m_UserGestures.onTouchEvent(event);
   }
   
   // XONGestureAPI Implementations
   // Notified when a tap occurs with the down MotionEvent that triggered it. This 
   // is the first event that is fired whenever any activity happens on the gesture.
   @Override
   public boolean onDown(MotionEvent e)
   {
      if (m_MainCanvasData.size() > 1) return true;
      return false;
   }

   // Notified when a single-tap occurs. This method is called when Single Tap 
   // is confirmed.
   @Override
   public boolean onSingleTapEvent(MotionEvent e)
   {
      XONUtil.logDebugMesg();
      MainCanvasData data = m_MainCanvasData.elementAt(m_CanvasDataPos);
      if (data.m_Action == null || data.m_Action.length() == 0) {
         setCanvasDataPos(); m_DataActivatedTime = -1; reDraw();
         return true;
      } 
      XONUtil.logDebugMesg("Action set: "+data.m_Action);
      XONPropertyInfo.setLongToastMessage(data.m_Text);
      Object res = XONShareHandler.processShareAction(m_MainActivity, data.m_Action);
      if (res != null) return true;
      return false;
   }

   @Override
   public boolean onDoubleTapEvent(MotionEvent e)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void onLongPress(MotionEvent e)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void onShowPress(MotionEvent e)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX,
         float distY)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velX, float velY)
   {
      XONUtil.logDebugMesg();
      setCanvasDataPos(); m_DataActivatedTime = -1; reDraw();
      return true;
   }
   
}
