package com.xpresson.android;

import com.xpresson.android.BuildConfig;
import com.xpresson.android.util.IntentUtil;
import com.xpresson.android.util.XONPropertyInfo;
import com.xpresson.android.util.XONUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

//A Fragment represents a behavior or a portion of user interface in an Activity. You 
//can combine multiple fragments in a single activity to build a multi-pane UI and reuse 
//a fragment in multiple activities.

public class XONImageGridActivity extends FragmentActivity
{
   private static final String TAG = "XONImageGridActivity";
   private ImageGridFragment m_ImageGridFragment;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) 
   {
      XONUtil.logDebugMesg();
       if (BuildConfig.DEBUG) {
           XONUtil.enableStrictMode();
       }
       super.onCreate(savedInstanceState);
       this.setTheme(R.style.XONInnerBodyBackground); 
       this.setTitleColor(Color.parseColor(getString(R.string.XONWidgetBackground)));
       if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
          m_ImageGridFragment = new ImageGridFragment();
          final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          ft.add(android.R.id.content, m_ImageGridFragment, TAG);
          ft.commit();
      }
  }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK) {
          XONUtil.logDebugMesg();
          IntentUtil.processIntent(this, XON_Main_UI.class);
          if (m_ImageGridFragment != null) m_ImageGridFragment.resetCache();
          finish();
          return true;
       }
       return super.onKeyDown(keyCode, event);
   }   

   
}
