package com.xpresson.android;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.xpresson.android.R;
import com.xpresson.android.BuildConfig;
import com.xpresson.android.util.Dimension;
import com.xpresson.android.util.IntentUtil;
import com.xpresson.android.util.UIUtil;
import com.xpresson.android.util.XONClickListener;
import com.xpresson.android.util.XONGestureAPI;
import com.xpresson.android.util.XONGestureListener;
import com.xpresson.android.util.XONImageHandler;
import com.xpresson.android.util.XONImageHolder;
import com.xpresson.android.util.XONImageManager;
import com.xpresson.android.util.XONObjectCache;
import com.xpresson.android.util.XONPropertyInfo;
import com.xpresson.android.util.XONUtil;
import com.xpresson.serializable.XONImages;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
//import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class XONFullImageActivity  extends FragmentActivity 
                                   implements OnClickListener, XONClickListener
//                                   implements OnClickListener, XONGestureAPI, 
//                                              XONClickListener
{
   public static final String EXTRA_IMAGE = "extra_image";
   public static final String Caller = "caller";
   
   public static final int CALL_FROM_GRID_VIEW = 0;
   public static final int CALL_FROM_IMAGE_MAKER = 1;
   public int m_CallerId = CALL_FROM_GRID_VIEW;
   
   private ViewPager mPager;
   private int m_ImageViewPos;
   private XONImages m_XONImages;
   private ImagePagerAdapter mAdapter;
   private XONImageManager m_XONImageManager;
   private XONImageHandler m_FullImageHandler;
   public Map<Integer, Button> m_ImageProcessButtons;
//   private GestureDetector m_UserGestures;

   @SuppressLint("UseSparseArrays")
   @TargetApi(11)
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      if (BuildConfig.DEBUG) XONUtil.enableStrictMode();
      super.onCreate(savedInstanceState);
      XONUtil.logDebugMesg();
      setContentView(R.layout.image_view_activity);
      
      // Fetch screen height and width, to use as our max size when loading images as this
      // activity runs full screen
      XONUtil.logDebugMesg("XONImageHolder Obj: "+XONObjectCache.
                                                  getObjectForKey("XONImage"));
      Dimension dimension = XONUtil.getScreenDimension(this);
      XONUtil.logDebugMesg("Screen Dimension: "+dimension);
      final int height = dimension.height;
      final int width = dimension.width;
      
      // Set the current item based on the extra passed in to this activity
      int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
      if (extraCurrentItem == -1) extraCurrentItem = 0;
      m_ImageViewPos = extraCurrentItem; 
      XONUtil.logDebugMesg("XON Image Pos: "+m_ImageViewPos);  
      
      m_CallerId = getIntent().getIntExtra(Caller, -1);
      if (extraCurrentItem == -1) extraCurrentItem = CALL_FROM_GRID_VIEW;
      // Finishing the XON_IM_UI Activity if the new Image is saved from Image Maker
      if (m_CallerId == CALL_FROM_IMAGE_MAKER) XONPropertyInfo.resetSubMainAct();
      
      XONPropertyInfo.populateResources(this, BuildConfig.DEBUG);
      m_XONImageManager = XONImageManager.getInstance(); 
      m_XONImages = m_XONImageManager.getXONImage(); 
      XONUtil.logDebugMesg("XON Images: "+m_XONImages);
      m_ImageProcessButtons = new HashMap<Integer, Button>();
      m_FullImageHandler = m_XONImageManager.getFullImageHandler(this, width, height);
      boolean reset = m_XONImages.checkFileExists(m_FullImageHandler);
      if(reset) { //m_XONImageManager.serXONImage(); 
                  m_FullImageHandler.clearCache(); }
      try { XONPropertyInfo.activateProgressBar(false); } catch(Throwable th) {}
      // Creates the GestureDetector Object
//      m_UserGestures = new GestureDetector(this, new XONGestureListener(this));
      
      // Set up ViewPager and backing adapter
      mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), m_XONImages.getCount());
      mPager = (ViewPager) findViewById(R.id.pager);
      mPager.setAdapter(mAdapter);
      mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
      mPager.setOffscreenPageLimit(2);
      mPager.setCurrentItem(m_ImageViewPos);
      
      // Set up activity to go full screen
      getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

      // Enable some additional newer visibility and ActionBar features to create a more
      // immersive photo viewing experience
//      if (XONUtil.hasHoneycomb()) 
//      {
//         final ActionBar actionBar = getActionBar();
//
//         // Hide title text and set home as up
//         actionBar.setDisplayShowTitleEnabled(false);
//         actionBar.setDisplayHomeAsUpEnabled(true);
//
//         // Hide and show the ActionBar as the visibility changes
//         mPager.setOnSystemUiVisibilityChangeListener(
//            new View.OnSystemUiVisibilityChangeListener() {
//               @Override
//               public void onSystemUiVisibilityChange(int vis) {
//                  if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) actionBar.hide();
//                  else actionBar.show();
//               }
//         });
//
//         // Start low profile mode and hide ActionBar
//         mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
//         actionBar.hide();
//      }
   
      addButtonListener();      
   }
   
   public void addButtonListener() 
   {
      Button button = (Button) findViewById(R.id.xon_home_btn);      
      m_ImageProcessButtons.put(R.id.xon_home_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            initOnButtonClick(R.id.xon_home_btn);
            IntentUtil.processIntent(XONFullImageActivity.this, XON_Main_UI.class);
            finish();
         }
      });
      
      button = (Button) findViewById(R.id.my_xpresson_btn);      
      m_ImageProcessButtons.put(R.id.my_xpresson_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            initOnButtonClick(R.id.my_xpresson_btn); 
            IntentUtil.processIntent(XONFullImageActivity.this, XONImageGridActivity.class); 
            finish();
         }
      });
      
      button = (Button) findViewById(R.id.enhance_image_btn);      
      m_ImageProcessButtons.put(R.id.enhance_image_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            initOnButtonClick(R.id.enhance_image_btn); 
            m_ImageViewPos = mPager.getCurrentItem();
            XONUtil.logDebugMesg("Loading XON Image Enhancer Act: "+m_ImageViewPos);
            m_XONImageManager.loadXONImage(m_XONImages.getFullImageData(m_ImageViewPos));
            finish();
         }
      });
      
      button = (Button) findViewById(R.id.image_share_btn);      
      m_ImageProcessButtons.put(R.id.image_share_btn, button);
      button.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View arg0) {
            initOnButtonClick(R.id.image_share_btn); 
            m_ImageViewPos = mPager.getCurrentItem();
            XONUtil.logDebugMesg("Sharing XON Image: "+m_ImageViewPos);
            m_XONImageManager.shareXONImage(m_XONImages.getFullImageData(m_ImageViewPos));
         }
      });
   }
   
   public void initOnButtonClick(int selBtn)
   {
      int bgSelResId = R.color.XONTitleSelBackground;
      int bgMenuResId = R.color.XONTitleBackground;
      
      Collection<Button> buttons = m_ImageProcessButtons.values();
      Iterator<Button> buttonIter = buttons.iterator();
//      while (buttonIter.hasNext()) buttonIter.next().setBackgroundResource(bgMenuResId);
      while (buttonIter.hasNext()) buttonIter.next().setBackgroundColor(Color.TRANSPARENT);
      Button homeBtn = m_ImageProcessButtons.get(R.id.xon_home_btn);
      homeBtn.setBackgroundResource(R.color.XONHomeButtonBG);
      homeBtn.setBackgroundColor(
              Color.parseColor(XONPropertyInfo.getString(R.color.XONHomeButtonBG)));
      
//      int selBGColor = R.style.XONTransperantBackground;
//      Button button = m_ImageProcessButtons.get(selBtn);
//      if (button != null) button.setBackgroundResource(bgSelResId);
//      if (button != null) button.setBackgroundColor(selBGColor);
//      if (button != null) button.setBackgroundResource(bgSelResId);
//      if (selBtn == R.id.xon_home_btn) homeBtn.setTextColor(Color.BLACK);
//      else homeBtn.setTextColor(Color.WHITE);
   
   }
       
   
   // This method is called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), 
   // for the activity to start interacting with the user. This is a good place to begin 
   // animations, open exclusive-access devices (such as the camera), etc. 
   @Override
   public void onResume() 
   {
       super.onResume();
       m_FullImageHandler.setExitTasksEarly(false);
   }

   // Called as part of the activity lifecycle when an activity is going into the 
   // background, but has not (yet) been killed. The counterpart to onResume(). 
   @Override
   protected void onPause() 
   {
       super.onPause();
       m_FullImageHandler.setExitTasksEarly(true);
       m_FullImageHandler.flushCache();
   }
   
   long m_MotionDownTime = -1; float m_StartPtX = 0.0f, m_StartPtY = 0.0f;
   @Override
   public boolean onTouchEvent(MotionEvent e) {
      // TODO Auto-generated method stub
      int action = e.getAction(); 
      if (action == MotionEvent.ACTION_DOWN) { m_StartPtX = e.getX(); m_StartPtY = e.getY(); 
                                               m_MotionDownTime = e.getDownTime(); }
      if (action == MotionEvent.ACTION_UP) {
         double dist = XONUtil.getDistance(m_StartPtX, m_StartPtY, e.getX(), e.getY());
         long diffTime = e.getEventTime()-m_MotionDownTime;
         if (dist < XONPropertyInfo.getIntRes(R.string.popup_scroll_dist) && 
             diffTime > XONPropertyInfo.getIntRes(R.string.popup_scroll_time)) {
            UIUtil.createSingleChoiceDialog(this, R.drawable.alert_dialog_icon, 
                   R.string.image_action_text, R.array.image_view_action_list, this).show();         
         }
      }
      return super.onTouchEvent(e);
   }   
   
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) 
   {
       if (keyCode == KeyEvent.KEYCODE_BACK) {
          XONUtil.logDebugMesg();          
          if (m_CallerId == CALL_FROM_IMAGE_MAKER) {
             IntentUtil.processIntent(this, XONImageGridActivity.class);
             finish();
             return true;
          }
       }
       finish();
       XONUtil.logDebugMesg("isLongPress: "+event.isLongPress());
       return super.onKeyDown(keyCode, event);
   }   

   // Perform any final cleanup before an activity is destroyed. This can happen either 
   // because the activity is finishing (someone called finish() on it, or because the 
   // system is temporarily destroying this instance of the activity to save space.
   @Override
   protected void onDestroy() 
   {
       super.onDestroy();
       XONUtil.logDebugMesg();
       m_FullImageHandler.closeCache();
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           case android.R.id.home:
               NavUtils.navigateUpFromSameTask(this);
               return true;
           case R.id.delete_image:
              deleteImage();
              return true;
       }
       return super.onOptionsItemSelected(item);
   }

   public void deleteImage()
   {
      m_ImageViewPos = mPager.getCurrentItem();
      boolean reset = m_XONImages.deleteXONImage(m_ImageViewPos, m_FullImageHandler);
      if(reset) { m_FullImageHandler.clearCache(); }
      Toast.makeText(
               this, R.string.clear_image_complete_toast,Toast.LENGTH_SHORT).show();
      IntentUtil.processIntent(XONFullImageActivity.this, XONImageGridActivity.class); 
      finish();
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
       getMenuInflater().inflate(R.menu.main_menu, menu);
       return true;
   }

   /**
    * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
    * when the ImageView is touched.
    */
   @TargetApi(11)
   @Override
   public void onClick(View v) 
   {
       XONUtil.logDebugMesg("View: "+v);
       final int vis = mPager.getSystemUiVisibility();
       if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
           mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
       } else {
           mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
       }
   }
   
   /**
    * Called by the ViewPager child fragments to load images via the one XONImageHandler
    */
   public XONImageHandler getXONImageHandler() 
   {
       return m_FullImageHandler;
   }

   /**
    * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
    * could be a large number of items in the ViewPager and we don't want to retain them all in
    * memory at once but create/destroy them on the fly.
    */
   private class ImagePagerAdapter extends FragmentStatePagerAdapter 
   {
      private final int mSize;

      public ImagePagerAdapter(FragmentManager fm, int size) 
      {
         super(fm);
         mSize = size;
      }

      @Override
      public int getCount() 
      {
         return mSize;
      }

      @Override
      public Fragment getItem(int position) 
      {
         String imgData = m_XONImages.getFullImageData(position);
         XONUtil.logDebugMesg("Position: "+position+" Data: "+imgData+" pager item: "+
                               mPager.getCurrentItem());
         return ImageDetailFragment.newInstance(imgData);
      }
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
      if (m_ImageAction.equals(imgActions[0])) deleteImage();
   }

   @Override
   public void onCancel(int dialogTitleResId)
   {
      XONPropertyInfo.setToastMessage(R.string.NoActionMesg);
   }

}
