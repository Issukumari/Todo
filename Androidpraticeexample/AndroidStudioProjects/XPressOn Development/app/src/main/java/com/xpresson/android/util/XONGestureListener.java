package com.xpresson.android.util;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class XONGestureListener implements GestureDetector.OnGestureListener,
                                           GestureDetector.OnDoubleTapListener 
{
   XONGestureAPI m_XONGestureView;

   public XONGestureListener(XONGestureAPI view) { m_XONGestureView = view; }
   
   // Notified when a tap occurs with the up MotionEvent that triggered it.
   // Single Tap Up Event is fired first.
   @Override
   public boolean onSingleTapUp(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      return false;
   }

   // Notified when a single-tap occurs.
   @Override
   public boolean onSingleTapConfirmed(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      return m_XONGestureView.onSingleTapEvent(e);
   }
   
   // Implementing methods of OnDoubleTapListener interface
   // Notified when a double-tap occurs. First this method
   // is fired subsequently onDoubleTapEvent is fired
   @Override
   public boolean onDoubleTap(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      return false;
   }

   // Notified when an event within a double-tap gesture occurs, including the down, 
   // move, and up events.
   @Override
   public boolean onDoubleTapEvent(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      return m_XONGestureView.onDoubleTapEvent(e);
   }
   
   // Notified when a tap occurs with the down MotionEvent that triggered it. This 
   // is the first event that is fired whenever any activity happens on the gesture.
   @Override
   public boolean onDown(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      // Interestingly, if the onDown() method does not return true, the scroll 

      return m_XONGestureView.onDown(e);
   }
   
   // Notified when a long press occurs with the initial on down MotionEvent that 
   // trigged it.
   @Override
   public void onLongPress(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      m_XONGestureView.onLongPress(e);
   }
   
   // This method is fired when the user has performed a down MotionEvent and not 
   // performed a move or up yet. This event is fired just before the scroll is done.
   @Override
   public void onShowPress(MotionEvent e)
   {
//      XONUtil.logDebugMesg();
      m_XONGestureView.onShowPress(e);
   }
   
   // Notified when a scroll occurs with the initial on down MotionEvent and the 
   // current move MotionEvent.
   // A scroll event occurs when the user touches the screen and then moves their 
   // finger across it. This gesture is also known as a drag event.
   // distX - The distance along the X axis that has been scrolled since the last 
   //         call to onScroll. This is NOT the distance between e1 and e2.
   // distY - The distance along the Y axis that has been scrolled since the last 
   //         call to onScroll. This is NOT the distance between e1 and e2.      
   @Override
   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX, float distY)
   {
      XONUtil.logDebugMesg("DistX: "+distX+" DistY: "+distY);
      // Both the distance in X and Y is the old point minus the new point, hence 
      // negative of distance is applied for translation.
      return m_XONGestureView.onScroll(e1, e2, distX, distY);
   }
   
   // A fling gesture is, essentially, leaving velocity on an item that was being 
   // dragged across a screen. The fling gesture is detected only after the users 
   // finger is no longer touching the display. This mthd is used for animation.
   // velX   The velocity of this fling measured in pixels per second along the x axis.
   // velY   The velocity of this fling measured in pixels per second along the y axis.
   @Override
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velX, float velY)
   {
      // TODO Auto-generated method stub
      XONUtil.logDebugMesg("velx: "+velX+" velY: "+velY);
      return m_XONGestureView.onFling(e1, e2, velX, velY);
   }
      
}
