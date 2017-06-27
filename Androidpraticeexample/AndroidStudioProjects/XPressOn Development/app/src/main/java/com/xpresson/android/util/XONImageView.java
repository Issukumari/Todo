package com.xpresson.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class XONImageView  extends ImageView 
{
   Bitmap m_Bitmap;        
   Canvas m_Canvas;    

   public XONImageView(Context context)
   {
      super(context);
   }
   
   public XONImageView(Context context, AttributeSet attrs)
   {
      super(context, attrs);
   }

   public XONImageView(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
   }

   @Override
   public void setImageBitmap(Bitmap bm) 
   {  
      XONHorzScrollView.debugLog(Log.VERBOSE, "Bitmap Set: "+bm);
      m_Bitmap = bm;        
      super.setImageBitmap(bm);           
   }               

   @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) 
   {
      XONHorzScrollView.debugLog(Log.VERBOSE, "Bitmap Set is: "+m_Bitmap);
      if (m_Bitmap == null) 
      { XONHorzScrollView.debugLog(Log.VERBOSE, "Bitmap Set is NULL"); return; }
      int curW = m_Bitmap != null ? m_Bitmap.getWidth() : 0;
      int curH = m_Bitmap != null ? m_Bitmap.getHeight() : 0;
      if (curW >= w && curH >= h) return;            
      if (curW < w) curW = w; if (curH < h) curH = h;
      Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
      Canvas newCanvas = new Canvas();
      newCanvas.setBitmap(newBitmap);          
      if (m_Bitmap != null) {
         newCanvas.drawBitmap(m_Bitmap, 0, 0, null);
      }
      m_Bitmap = newBitmap;
      m_Canvas = newCanvas;      
   }
   
   RelativeLayout m_ImageViewLayout;
   public void setImageLayout(RelativeLayout imgLayout)
   {
      m_ImageViewLayout = imgLayout;
   }
   
   public static String getCanvasInfo(Canvas canvas)
   {
      if (canvas == null) return "";
      StringBuffer mesg = new StringBuffer("Canvas Info: Density = "+canvas.getDensity());
      mesg.append(" Wt = "+canvas.getWidth()+" Ht = "+canvas.getHeight());
//      mesg.append(" Max Wt = "+canvas.getMaximumBitmapWidth());
//      mesg.append(" MaxHt = "+canvas.getMaximumBitmapHeight());
      return mesg.toString();
   }
   
   @Override
   protected void onDraw(Canvas canvas) 
   {
      if (m_Bitmap == null || m_ImageViewLayout == null) return;
      int wt = 0, ht = 0, layoutWt = 0, layoutHt = 0, left = 0, top = 0;
      wt = m_Bitmap.getWidth(); ht = m_Bitmap.getHeight();
      left = m_ImageViewLayout.getLeft(); top = m_ImageViewLayout.getTop();
      layoutWt = m_ImageViewLayout.getWidth(); layoutHt = m_ImageViewLayout.getHeight(); 
      XONHorzScrollView.debugLog(Log.VERBOSE, "Bitmap Wt: "+wt+" Ht: "+ht+" XONView Wt: "+
                                 getWidth()+" ht: "+getHeight()+" "+getCanvasInfo(canvas)+
                                 " ImgLayout Wt: "+layoutWt+" Ht: "+layoutHt+
                                 " ImgLayout Left: "+left+" Top: "+top);
      canvas.drawBitmap(m_Bitmap, 0, 0, null);
      int x = (int)((layoutWt-wt)/2), y = top+10;
      XONHorzScrollView.debugLog(Log.VERBOSE, "x = "+x+" y = "+y);
//      canvas.drawBitmap(m_Bitmap, x, y, null);
      y = canvas.getHeight()-layoutHt+ht;
      XONHorzScrollView.debugLog(Log.VERBOSE, "New y = "+y);
//      canvas.drawBitmap(m_Bitmap, x, y, null);
      m_Canvas = canvas;                      
   }       
}
