package com.xpresson.android.util;

import java.util.Vector;

import com.xpresson.android.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.content.DialogInterface;

public class UIUtil
{
   public static Paint m_GPathPaint, m_GPathPointPaint;
   public static int POINT_SIZE = 80, PAINT_POINT_SIZE = 20;
   static {
      m_GPathPaint = new Paint(); 
      m_GPathPaint.setColor(Color.BLACK);      
      m_GPathPaint.setStrokeWidth(3);
      m_GPathPaint.setStyle(Paint.Style.STROKE);      
      m_GPathPointPaint = new Paint(); 
      m_GPathPointPaint.setStrokeWidth(PAINT_POINT_SIZE);
      m_GPathPointPaint.setColor(Color.BLACK);      
      m_GPathPointPaint.setStyle(Paint.Style.FILL);      
   }
   
   public static void showImageDialog(Activity act, Bitmap bitmap)
   {
      Dialog dialog = new Dialog(act);
      dialog.setContentView(R.layout.xon_image_dialog);
      dialog.setTitle(act.getString(R.string.image_dialog));
      ImageView image = (ImageView) dialog.findViewById(R.id.image_dialog);
      image.setImageBitmap(bitmap);      
      dialog.show();      
   }

   public static void showShortMessage(Activity act, int rid)
   {
      showShortMessage(act, (String) act.getText(rid));
   }

   public static void showShortMessage(Activity act, String text)
   {
      Toast.makeText(act,  text, Toast.LENGTH_SHORT).show();
   }
   
   public static void activateGlobalLayout(ImageView imageView, final XONEventListener listener)
   {
      // The view tree observer is used to get notifications when global events, like layout, 
      // happens. Listener is attached to get the final width and height of the Canvas View 
      // and to activate the default filter
      imageView.getViewTreeObserver().
                      addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() 
      {
         // Register a callback to be invoked when the global layout state or the visibility of 
         // views within the view tree changes
         @Override
         public void onGlobalLayout() 
         {
            listener.processXONEvent(XONEventListener.XONEventType.GlobalLayoutEvent, null);
         }
      });
      
   }

   public static void drawRectPath(Canvas canvas, Rect rect)
   {
      drawRectPath(canvas, rect, null, false);
   }
   
   public static Vector<Point> drawRectPath(Canvas canvas, Rect rect, Vector<Point> pts, 
                                            boolean drawPt)
   {
      if (rect == null) return null;
      if (pts == null) pts = new Vector<Point>();
      else pts.removeAllElements();
      pts.add(new Point(rect.left, rect.top));
      pts.add(new Point(rect.right, rect.top));
      pts.add(new Point(rect.right, rect.bottom));
      pts.add(new Point(rect.left, rect.bottom));
      drawLine(canvas, pts, true, drawPt);
      return pts;
   }

   public static void drawLine(Canvas canvas, Vector<Point> pts, boolean closePath, boolean drawPt)
   {
      Path path = new Path(); Point pt = null;
      for (int i = 0; i < pts.size(); i++) {
         pt = pts.elementAt(i);
         if (i == 0) path.moveTo(pt.x, pt.y);
         else path.lineTo(pt.x, pt.y);
         if (drawPt) canvas.drawPoint((float)pt.x, (float) pt.y, m_GPathPointPaint);
      }
      if (closePath) path.close();
      canvas.drawPath(path, m_GPathPaint);      
   }
   
   // This adds a custom layout to an AlertDialog
   public static Dialog createCustomViewDialog(Activity act, int iconResId, final int titResId, 
                        int viewLayoutId, final XONClickListener listner)
   {
      LayoutInflater factory = LayoutInflater.from(act);
      final View customView = factory.inflate(viewLayoutId, null);
      return new AlertDialog.Builder(act)
                 .setIcon(iconResId) .setTitle(titResId) .setView(customView)
                 .setPositiveButton(R.string.alert_dialog_ok, 
                                    new DialogInterface.OnClickListener() 
                 { 
                    public void onClick(DialogInterface dialog, int whichButton) 
                    { listner.onOK(titResId, customView); }
                 })
                 .setNegativeButton(R.string.alert_dialog_cancel, 
                                    new DialogInterface.OnClickListener() 
                 {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    { listner.onCancel(titResId); }
                 })
                 .create();
   }
   
   public static Dialog createSingleChoiceDialog(Activity act, int iconResId, final int titResId, 
                                                 int choiceResId, final XONClickListener listner)
   {
      return new AlertDialog.Builder(act)
                 .setIcon(iconResId) .setTitle(titResId)
                 .setSingleChoiceItems(choiceResId, 0, 
                                       new DialogInterface.OnClickListener() 
                 {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    { listner.onClick(whichButton); }
                 })
                 .setPositiveButton(R.string.alert_dialog_ok, 
                                    new DialogInterface.OnClickListener() 
                 { 
                    public void onClick(DialogInterface dialog, int whichButton) 
                    { listner.onOK(titResId, null); }
                 })
                 .setNegativeButton(R.string.alert_dialog_cancel, 
                                    new DialogInterface.OnClickListener() 
                 {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    { listner.onCancel(titResId); }
                 })
                 .create();
   }
   
   public static void showMesgDialog(int mesgResId)
   {
      showMesgDialog(XONPropertyInfo.m_SubMainActivity, XONPropertyInfo.getString(mesgResId));
   }
   
   public static void showMesgDialog(Activity act, int mesgResId)
   {
      showMesgDialog(act, XONPropertyInfo.getString(mesgResId));
   }
   
   public static void showMesgDialog(Activity act, String mesg)
   {
      new AlertDialog.Builder(act).setMessage(mesg).show();      
   }
      
   public static void showAckMesgDialog(Activity act, int iconResId, final int titleResId,  
                                        String mesg, final XONClickListener listner)
   {
      new AlertDialog.Builder(act)
          .setIcon(iconResId) .setTitle(titleResId) .setMessage(mesg)
          .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                 if (listner != null) listner.onOK(titleResId, null);
              }
          })
          .create().show();      
   }

}
