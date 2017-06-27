package com.xpresson.android;

import com.xpresson.android.util.XONPropertyInfo;
import com.xpresson.android.util.XONUtil;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class XONViewPager extends ViewPager 
{
   public XONViewPager(Context context) {
      super(context);
   }

   public XONViewPager(Context context, AttributeSet attrs) {
      super(context, attrs); 
   }
   
   long m_MotionDownTime = -1; float m_StartPtX = 0.0f, m_StartPtY = 0.0f;
   @Override
   public boolean onTouchEvent(MotionEvent e) 
   {
      XONPropertyInfo.m_SubMainActivity.onTouchEvent(e);
//      if (dist < 20.0 && diffTime > 500) 
//      if (m_MotionDownTime == -1) { 
//         m_MotionDownTime = e.getDownTime();
//         m_TouchPt = new Point(Math.round(e.getX()), Math.round(e.getY()));
//      } else if (action == MotionEvent.ACTION_UP) {}
//      XONUtil.logDebugMesg();
      return super.onTouchEvent(e);
   }
}