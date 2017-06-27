package com.xpresson.android;

import com.xpresson.android.util.XONImageHandler;
import com.xpresson.android.util.XONImageManager;
import com.xpresson.android.util.XONImageUtil.BitmapResizeLogic;
import com.xpresson.android.util.XONPropertyInfo;
import com.xpresson.android.util.XONUtil;
import com.xpresson.serializable.XONImages;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
public class ImageGridFragment  extends Fragment implements AdapterView.OnItemClickListener
{
   private int mImageThumbSize;
   private int mImageThumbSpacing;
   
   private ImageAdapter m_Adapter;
   private XONImages m_XONImages;
   private XONImageManager m_XONImageManager;
   private XONImageHandler m_ThumbImageHandler;
   
   /**
    * Empty constructor as per the Fragment documentation
    */
   public ImageGridFragment() {}

   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      setHasOptionsMenu(true);
      
      XONPropertyInfo.populateResources(getActivity(), BuildConfig.DEBUG);
      mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
      mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
      XONUtil.logDebugMesg("Thumb Sz: "+mImageThumbSize+" Spacing: "+mImageThumbSpacing);
      
      m_Adapter = new ImageAdapter(getActivity());
      m_XONImageManager = XONImageManager.getInstance(); 
      m_XONImages = m_XONImageManager.getXONImage(); 
      m_ThumbImageHandler = m_XONImageManager.getThumbImageHandler(getActivity());
      m_ThumbImageHandler.m_BitmapResizeLogic = BitmapResizeLogic.NoResize;
      boolean reset = m_XONImages.checkThumbFileExists(m_ThumbImageHandler);
      if(reset) { // m_XONImageManager.serXONImage(); 
                  m_ThumbImageHandler.clearCache(); } 
   }
   
   public void resetCache()
   {
      m_ThumbImageHandler.clearCache();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                            Bundle savedInstanceState) 
   {
      XONUtil.logDebugMesg();
      final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
      final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
      mGridView.setAdapter(m_Adapter);
      mGridView.setOnItemClickListener(this);
      mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(AbsListView absListView, int scrollState) 
         {
            // Pause fetcher to ensure smoother scrolling when flinging
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) 
               m_ThumbImageHandler.setPauseWork(true);
            else m_ThumbImageHandler.setPauseWork(false);
         }

         @Override
         public void onScroll(AbsListView absListView, int firstVisibleItem,
                              int visibleItemCount, int totalItemCount) {}
      });

      // The view tree observer is used to get notifications when global events, like layout, 
      // happens. Listener is attached to get the final width of the GridView and then calc the
      // number of columns and the width of each column. The width of each column is variable
      // as the GridView has stretchMode=columnWidth. The column width is used to set the height
      // of each view so we get nice square thumbnails.
      mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                                      new ViewTreeObserver.OnGlobalLayoutListener() 
      {
         // Register a callback to be invoked when the global layout state or the visibility of 
         // views within the view tree changes
         @Override
         public void onGlobalLayout() 
         {
            if (m_Adapter.getNumColumns() != 0) return;
            int viewSpaceReq = mImageThumbSize + mImageThumbSpacing;
            XONUtil.logDebugMesg("ImageThumbSize: "+mImageThumbSize+
                                 " ImageThumbSpacing: "+mImageThumbSpacing);
            XONUtil.logDebugMesg("Orig Num Cols: "+( mGridView.getWidth()/viewSpaceReq)+
                                 " Wt: "+mGridView.getWidth()+" spaceReq: "+viewSpaceReq);
            final int numColumns = (int) Math.floor( mGridView.getWidth()/viewSpaceReq);
            if (numColumns <= 0) return; 
            final int columnWidth = (mGridView.getWidth() / numColumns) - 
                                    mImageThumbSpacing;
            m_Adapter.setNumColumns(numColumns);
            m_Adapter.setItemHeight(columnWidth);
            XONUtil.logDebugMesg("onCreateView - numColumns set to " + numColumns);
         }
      });

      return v;
   }

   @Override
   public void onResume() 
   {
      super.onResume();
      m_ThumbImageHandler.setExitTasksEarly(false);
      m_Adapter.notifyDataSetChanged();
   }

   @Override
   public void onPause() 
   {
      super.onPause();
      m_ThumbImageHandler.setExitTasksEarly(true);
      m_ThumbImageHandler.flushCache();
   }

   @Override
   public void onDestroy() 
   {
      super.onDestroy();
      m_ThumbImageHandler.closeCache();
   }

   @TargetApi(16)
   @Override
   public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
   {
      if (!m_XONImages.checkImageExists(position)) 
      { XONPropertyInfo.setToastMessage(R.string.NoImageExists); return; }
      final Intent i = new Intent(getActivity(), XONFullImageActivity.class);
      i.putExtra(XONFullImageActivity.EXTRA_IMAGE, (int) id);
      if (XONUtil.hasJellyBean()) 
      {
         // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
         // show plus the thumbnail image in GridView is cropped. so using
         // makeScaleUpAnimation() instead.
         ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, 0, 0, 
                                                   v.getWidth(), v.getHeight());
         getActivity().startActivity(i, options.toBundle());
      } else startActivity(i);
   }

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
   {
      inflater.inflate(R.menu.main_menu, menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
//      switch (item.getItemId()) {
//         case R.id.clear_cache:
//            m_ThumbImageHandler.clearCache();
//            Toast.makeText(getActivity(), R.string.clear_cache_complete_toast,
//                           Toast.LENGTH_SHORT).show();
//            return true;
//       }
       return super.onOptionsItemSelected(item);
   }

      
   /**
    * The main adapter that backs the GridView. This is fairly standard except the number of
    * columns in the GridView is used to create a fake top row of empty views as we use a
    * transparent ActionBar and don't want the real top row of images to start off covered by it.
    */
   private class ImageAdapter extends BaseAdapter 
   {
      private final Context mContext;
      private int mItemHeight = 0;
      private int mNumColumns = 0;
      private int mActionBarHeight = 0;
      private GridView.LayoutParams mImageViewLayoutParams;

      public ImageAdapter(Context context) 
      {
         super();
         mContext = context;
         mImageViewLayoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, 
                                                            LayoutParams.MATCH_PARENT);
         // Calculate ActionBar height
         TypedValue tv = new TypedValue();
         if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) 
         {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, 
                                          context.getResources().getDisplayMetrics());
         }
      }

      @Override
      public int getCount() 
      {
         // Size + number of columns for top empty row
         int cnt = m_XONImages.getThumbCount() + mNumColumns;
//         Utils.logDebugMesg("Num of Cols: "+cnt);
         return cnt;
      }

      @Override
      public Object getItem(int position) 
      {
//         Utils.logDebugMesg("Pos: "+position);
         return position < mNumColumns ? null : m_XONImages.getThumbImageData(position - mNumColumns);
      }

      @Override
      public long getItemId(int position) 
      {
         int itemId = position < mNumColumns ? 0 : position - mNumColumns;
//         Utils.logDebugMesg("ItemId: "+itemId);
         return itemId;
      }

      @Override
      public int getViewTypeCount() 
      {
         // Two types of views, the normal ImageView and the top row of empty views
         return 2;
      }

      @Override
      public int getItemViewType(int position) 
      {
         int itemVwTyp = (position < mNumColumns) ? 1 : 0;
//         Utils.logDebugMesg("ItemViewTyp for Pos: "+position+" is: "+itemVwTyp);
         return itemVwTyp;
      }

      @Override
      public boolean hasStableIds() 
      {
         return true;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup container) 
      {
         // First check if this is the top row
         if (position < mNumColumns) {
             if (convertView == null) convertView = new View(mContext);
             // Set empty view with height of ActionBar
             convertView.setLayoutParams(new AbsListView.LayoutParams(
                         ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
             return convertView;
         }

         XONUtil.logDebugMesg("Retriving View for pos: "+position+" View: "+convertView);
         // Now handle the main ImageView thumbnails
         ImageView imageView;
         if (convertView == null) { // if it's not recycled, instantiate and initialize
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(mImageViewLayoutParams);
         } else { // Otherwise re-use the converted view
            imageView = (ImageView) convertView;
         }

         // Check the height matches our calculated column width
         if (imageView.getLayoutParams().height != mItemHeight) {
             imageView.setLayoutParams(mImageViewLayoutParams);
         }

//         Utils.logDebugMesg("Loading Image: "+Images.imageThumbUrls[position - mNumColumns]);
         // Finally load the image asynchronously into the ImageView, this also takes care of
         // setting a placeholder image while the background thread runs
         int pos = position-mNumColumns;
         m_ThumbImageHandler.loadImage(m_XONImages.getThumbImageData(pos), imageView);
         return imageView;
      }

      /**
       * Sets the item height. Useful for when we know the column width so the height can be set
       * to match.
       *
       * @param height
       */
      public void setItemHeight(int height) 
      {
         if (height == mItemHeight) return;
         mItemHeight = height;
         mImageViewLayoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, 
                                                            mItemHeight);
         m_ThumbImageHandler.setImageSize(height);
         notifyDataSetChanged();
      }

      public void setNumColumns(int numColumns) 
      {
         mNumColumns = numColumns;
      }

      public int getNumColumns() 
      {
         return mNumColumns;
      }
   }
}
