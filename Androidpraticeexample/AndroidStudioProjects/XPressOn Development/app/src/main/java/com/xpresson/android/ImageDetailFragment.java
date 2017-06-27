package com.xpresson.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.xpresson.android.R;
import com.xpresson.android.util.ImageWorker;
import com.xpresson.android.util.XONImageHandler;
import com.xpresson.android.util.XONUtil;

/**
 * This fragment will populate the children of the ViewPager from {@link XONViewMainActivity}.
 */
public class ImageDetailFragment  extends Fragment 
{
   private static final String IMAGE_DATA_EXTRA = "extra_image_data";
   private String mImageUrl;
   private ImageView mImageView;
   private XONImageHandler m_FullImageHandler;
   
   /**
    * Empty constructor as per the Fragment documentation
    */
   public ImageDetailFragment() {}

   /**
    * Factory method to generate a new instance of the fragment given an image number.
    *
    * @param imageUrl The image url to load
    * @return A new instance of ImageDetailFragment with imageNum extras
    */
   public static ImageDetailFragment newInstance(String imageUrl) 
   {
       final ImageDetailFragment f = new ImageDetailFragment();
       final Bundle args = new Bundle();
       args.putString(IMAGE_DATA_EXTRA, imageUrl);
       f.setArguments(args);
       return f;
   }

   /**
    * Populate image using a url from extras, use the convenience factory method
    * {@link ImageDetailFragment#newInstance(String)} to create this fragment.
    */
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) 
   {
      // Inflate and locate the main ImageView
      final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
      mImageView = (ImageView) v.findViewById(R.id.imageView);
      return v;
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState) 
   {
      super.onActivityCreated(savedInstanceState);

      // Use the parent activity to load the image asynchronously into the ImageView (so a single
      // cache can be used over all pages in the ViewPager
      if (XONFullImageActivity.class.isInstance(getActivity())) {
         m_FullImageHandler = ((XONFullImageActivity) getActivity()).getXONImageHandler();
         m_FullImageHandler.loadImage(mImageUrl, mImageView);
       }

       // Pass clicks on the ImageView to the parent activity to handle
       if (OnClickListener.class.isInstance(getActivity()) && XONUtil.hasHoneycomb()) {
           mImageView.setOnClickListener((OnClickListener) getActivity());
       }
   }
      
   @Override
   public void onDestroy() 
   {
      super.onDestroy();
      XONUtil.logDebugMesg();
      if (mImageView != null) {
         // Cancel any pending image work
         ImageWorker.cancelWork(mImageView);
         mImageView.setImageDrawable(null);
      }
   }

}
