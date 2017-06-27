package com.xpresson.android.util;

import android.view.MotionEvent;

public interface XONItemPressedListener
{
   // Notified when a long press occurs with the initial on down MotionEvent that trigged it.
   public void onLongPress(MotionEvent e, long downTime, int pos);

}
