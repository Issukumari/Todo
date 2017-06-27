package com.xpresson.android.util;

import android.view.View;

public interface XONClickListener
{
   public void onClick(int actionBut);
   public void onOK(int dialogTitleResId, View customView);
   public void onCancel(int dialogTitleResId);
}
