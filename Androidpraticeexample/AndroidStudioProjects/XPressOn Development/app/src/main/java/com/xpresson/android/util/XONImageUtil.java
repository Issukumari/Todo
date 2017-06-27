package com.xpresson.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Vector;

import com.xpresson.android.R;
import com.xpresson.android.util.XONPropertyInfo.ImageOrientation;

import android.net.Uri;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;

public class XONImageUtil
{
   static int RED    = 0;
   static int GREEN  = 1;
   static int BLUE   = 2;
   static int ALPHA  = 3;  // ignored in RGB
   
   public static Bitmap getXONImage(Activity act, Uri imageUri)
   {
      InputStream stream = null; Bitmap bitmap = null;
      try {
         stream = act.getContentResolver().openInputStream(imageUri);
         bitmap = BitmapFactory.decodeStream(stream);         
         return bitmap;
      } catch(Exception ex) {
         XONUtil.logError("Exception process image uri: "+imageUri, ex);
      } finally {
         try { stream.close(); } catch(Exception ex) {}
      }      
      return null;
   } 
   
   public static Bitmap getBitmap(Resources res, int id, boolean modPixel)
   {
      Bitmap bmp = BitmapFactory.decodeResource(res, id);
      //Guarantees that the image is decoded in the ARGB8888 format
      bmp = bmp.copy(Bitmap.Config.ARGB_8888, modPixel);
      return bmp;
   }
   
   public static Bitmap createBitmapCopy(Bitmap bitmap)
   {
//      int[] pixels = getBitmapPixels(bitmap); 
//      int wt = bitmap.getWidth(), ht = bitmap.getHeight();
//      return createBitmap(pixels, wt, ht);
      return bitmap.copy(Bitmap.Config.ARGB_8888, true);   
   }
   
   public static int[] getBitmapPixels(Bitmap bitmap) 
   {
      int bitmapWidth = bitmap.getWidth(), bitmapHeight = bitmap.getHeight();
      int[] pixels = new int[bitmapWidth * bitmapHeight];
      bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
      return pixels;
   }
   
   public static int[][][] getBitmapARGBPixels(Bitmap bitmap)
   {
      int wt = bitmap.getWidth(), ht = bitmap.getHeight();
      return getBitmapARGBPixels(getBitmapPixels(bitmap), wt, ht);
   }
   
   public static int[][][] getBitmapARGBPixels(int[] pixel, int imgCols, int imgRows)
   {
      //Create the One Dimensional  array of type int to be populated with pixel data, one int value
      // per pixel, with four color and alpha bytes  per int value.
      
      int[][][] pixel_rgb = new int[imgRows][imgCols][4];
      for(int row = 0;row < imgRows;row++)
      {
         for(int col = 0;col < imgCols;col++)
         {
            int element = row * imgCols + col;
            //Alpha data
            pixel_rgb[row][col][ALPHA] = (pixel[element] >> 24) & 0xFF;
            //Red data
            pixel_rgb[row][col][RED] = (pixel[element] >> 16) & 0xFF;
            //Green data
            pixel_rgb[row][col][GREEN] = (pixel[element] >> 8)  & 0xFF;
            //Blue data
            pixel_rgb[row][col][BLUE] = (pixel[element]) & 0xFF;
         }
      }
      return pixel_rgb;
   }
   
   public static enum ColorStrength { LIGHT, DARK }
   public static ColorStrength getColorStrength(int pixel)
   {
      int[] argb = getARGBPixel(pixel);
      float avgPixel = (float)(argb[RED]+argb[GREEN]+argb[BLUE])/3.0f;
      if (avgPixel < 100) return ColorStrength.DARK;
      return ColorStrength.LIGHT;
   }
   
   public static int getInverseColor(int color, boolean useInverse)
   {
      if (useInverse) {
         int[] argb = getARGBPixel(color);
         return getPixel(argb[ALPHA], 255-argb[RED], 255-argb[GREEN], 255-argb[BLUE]);         
      }
      ColorStrength clrStrngth = getColorStrength(color);
      if (clrStrngth.equals(ColorStrength.DARK)) {
         XONUtil.logDebugMesg("Clr Strgth: "+clrStrngth);
         return Color.WHITE;
      }
      XONUtil.logDebugMesg("Clr Strgth Outside: "+clrStrngth);
      return Color.BLACK;
   }  
   
   public static int[] getARGBPixel(int pixel)
   {      
      int[] argb = new int[4];
      argb[ALPHA] = (pixel >> 24) & 0xFF; //Alpha data
      argb[RED] = (pixel >> 16) & 0xFF; //Red data
      argb[GREEN] = (pixel >> 8)  & 0xFF; //Green data
      argb[BLUE] = (pixel) & 0xFF; //Blue data
      return argb;
   }
      
   public static int getPixel(int a, int r, int g, int b)
   {      
      int[] argb = {a, r, g, b};
      return getPixel(argb);
   }
   
   public static int getPixel(int[] argb)
   {      
      int pixel = ((argb[ALPHA] << 24) & 0xFF000000) | 
                  ((argb[RED] << 16) & 0x00FF0000) | 
                  ((argb[GREEN] << 8) & 0x0000FF00) | 
                  ((argb[BLUE]) & 0x000000FF);
      return pixel;
   }
   
   public static int setAlpha(int pixel, int newAlpha)
   {     
      int[] argb = getARGBPixel(pixel); argb[ALPHA] = newAlpha;
      int newPixel = ((argb[ALPHA] << 24) & 0xFF000000) | 
                     ((argb[RED] << 16) & 0x00FF0000) | 
                     ((argb[GREEN] << 8) & 0x0000FF00) | 
                     ((argb[BLUE]) & 0x000000FF);
      return newPixel;
   }
   
   public static Bitmap createBitmap(int[][][] pixel_rgb)
   {
      Dimension size = new Dimension();
      int[] pixels = getBitmapPixels(pixel_rgb, size);
      return createBitmap(pixels, size.width, size.height);
   }
   
   public static int[] getBitmapPixels(int[][][] pixel_rgb, Dimension size) 
   {
      if (size == null) size = new Dimension();
      int imgRows = pixel_rgb.length; size.height = imgRows; 
      int imgCols = pixel_rgb[0].length; size.width = imgCols;
       
      int[] pixel = new int[imgCols * imgRows];
      for(int row = 0,cnt = 0 ; row < imgRows  ; row++)
      {
         for(int col = 0;  col < imgCols ;   col++)
         {
            pixel[cnt] = ((pixel_rgb[row][col][ALPHA] << 24) & 0xFF000000) | 
                         ((pixel_rgb[row][col][RED] << 16) & 0x00FF0000) | 
                         ((pixel_rgb[row][col][GREEN] << 8) & 0x0000FF00) | 
                         ((pixel_rgb[row][col][BLUE]) & 0x000000FF);
            cnt++;
         }
      }
      return pixel;
   }
      
   public static Bitmap createBitmap(int[] pixels, int wt, int ht)
   {
      return Bitmap.createBitmap(pixels, wt, ht, Bitmap.Config.ARGB_8888);
   }
   
   public static int getAvgPixel(Bitmap bimg)
   {
      float avg = 0;
      int max=0;
      
      int maxCols = bimg.getWidth();
      int maxRows = bimg.getHeight();
      int[][][]pixel_rgb = getBitmapARGBPixels(bimg);
      
      for (int row=0 ; row< maxRows ; row++) 
      {
         for (int col=0 ; col<maxCols ; col++) 
         {
            max = (pixel_rgb[row][col][RED] + pixel_rgb[row][col][BLUE] + pixel_rgb[row][col][GREEN]) ;
             avg = (avg + (float)max/3f) / 2f ;
         }
      }
      
      XONUtil.logDebugMesg("Avg Pixel Value: "+avg);
      printRGBPixel(Math.round(avg));
      return Math.round(avg);
   }
   
   public static String printRGBPixel(int pixel)
   {
      int[] argb = getARGBPixel(pixel);
      String rgbPixelStr = "Avg Pixel: "+pixel+" R Pixel: "+argb[RED]+" G Pixel: "+
                           argb[GREEN]+" And B Pixel: "+argb[BLUE];
      XONUtil.logDebugMesg(rgbPixelStr);
      return rgbPixelStr;
   }
   
   public static Bitmap reducePixelDensity(Bitmap srcImg)
   {
      int imgWt = srcImg.getWidth(), imgHt = srcImg.getHeight();
      float scale = XONPropertyInfo.FilteredImageScale/(float)100;
      int scImgWt = Double.valueOf(imgWt*scale).intValue(); 
      int scImgHt = Double.valueOf(imgHt*scale).intValue();
      Bitmap scImg = Bitmap.createScaledBitmap(srcImg, scImgWt, scImgHt, true);
      return scImg;
//      return Bitmap.createScaledBitmap(scImg, imgWt, imgHt, true);
//      Bitmap newBitmap = Bitmap.createBitmap(imgWt, imgHt, Bitmap.Config.ARGB_8888);
//      Canvas newCanvas = new Canvas(); newCanvas.setBitmap(newBitmap);
//      newCanvas.scale(1.0f/scale, 1.0f/scale); newCanvas.drawBitmap(scImg, 0, 0, null);
//      return newBitmap;
   }
   
   public static boolean rotateBitmapNeeded(ImageOrientation imgOrient)
   {
      boolean rotNeeded = false;
      switch(imgOrient)
      {
         case Rot90 : case Rot180:  case Rot270: rotNeeded = true; break;
         default : break;
      }
      // create a new bitmap from the original using the matrix to transform the result
      return rotNeeded;
   }
   
   public static Bitmap rotateBitmap(Bitmap bitmap, ImageOrientation imgOrient)
   {
      Matrix matrix = new Matrix();
      int wt = bitmap.getWidth(), ht = bitmap.getHeight();
      switch(imgOrient)
      {
         case Rot90: matrix.postRotate(90); break;
         case Rot180: matrix.postRotate(180); break;
         case Rot270: matrix.postRotate(270); break;
         default : return bitmap;
      }
      // create a new bitmap from the original using the matrix to transform the result
      return Bitmap.createBitmap(bitmap , 0, 0, wt, ht, matrix, true);
   }
   
   public static Bitmap createBitmap(Bitmap orig, Bitmap fin, Dimension size)
   {
      Vector<Bitmap> bitmaps = new Vector<Bitmap>(2); bitmaps.add(orig); bitmaps.add(fin); 
//      float[] szWtRat = new float[2]; szWtRat[0] = 1.0f; szWtRat[1] = 1.0f;
//      float[] szHtRat = new float[2]; szHtRat[0] = origRat; szHtRat[1] = 1.0f - origRat;
      return createBitmap(bitmaps, size, 1, 2, 1.0f, 0.5f);
   }
   
   public static Bitmap createBitmap(Vector<Bitmap> bitmaps, Dimension size, int numCols, 
                                     int numRows, float wtRatio, float htRatio)
   {
      Bitmap finBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888); 
//      int wt = bitmaps.firstElement().getWidth(), ht = bitmaps.firstElement().getHeight();
      int col = 0, row = 0; float startX = 0f, startY = 0f;
      int dispHt = XONPropertyInfo.getIntRes(R.string.img_disp_gap); 
      int finWt = Math.round(size.width*wtRatio), finHt = Math.round(size.height*htRatio)-dispHt;
      Canvas canvas = new Canvas(finBitmap); 
      for (int i = 0; i < bitmaps.size(); i++)
      {
         Bitmap temp = bitmaps.elementAt(i); 
         Bitmap scImg = getScaledBitmap(temp, BitmapResizeLogic.SetScaledBitmap, 
                                        finWt, finHt, true);
         int scWt = scImg.getWidth(), scHt = scImg.getHeight();
         float totHGap = (float)(size.width*wtRatio-scWt); 
         float totVGap = (float)(size.height*htRatio-scHt);
         float hGap = totHGap/2f, vGap = totVGap/2f; startX += hGap; startY += vGap;
         XONUtil.logDebugMesg("Row: "+row+" Col: "+col+" stX: "+startX+" stY: "+startY+
                              " wt: "+finWt+" ht: "+finHt);
         canvas.drawBitmap(scImg, startX, startY, null); col++; 
         if (col == numCols) { col = 0; startX = 0; startY += finHt; row++; } 
         else { startX += finWt; }
      }
      return finBitmap;
   }

   public static Bitmap createBitmap(Vector<Bitmap> bitmaps, Dimension size, int numCols, 
                                     int numRows, float[] wtRatio, float[] htRatio)
   {
      int numImgs = bitmaps.size(); 
      if (numImgs != wtRatio.length || numImgs != htRatio.length) return null;
      Bitmap finBitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888); 
      int wt = bitmaps.firstElement().getWidth(), ht = bitmaps.firstElement().getHeight();
      float totHGap = (float)size.width-(float)(numCols*wt);
      float totVGap = (float)size.height-(float)(numRows*ht);
      XONUtil.logDebugMesg("Size: "+size+" wt: "+wt+" ht: "+ht+" totHGap: "+totHGap+
                           " totVGap: "+totVGap);
      float hGap = totHGap/(float)(numCols+1), startX = hGap; 
      float vGap = totVGap/(float)(numRows+1), startY = vGap;
      int col = 0, row = 0; Canvas canvas = new Canvas(finBitmap); 
      for (int i = 0; i < bitmaps.size(); i++)
      {
         Bitmap temp = bitmaps.elementAt(i); 
         int finWt = Math.round(wt*wtRatio[i]), finHt = Math.round(ht*htRatio[i]);
         XONUtil.logDebugMesg("Row: "+row+" Col: "+col+" stX: "+startX+" stY: "+startY+
                              " wt: "+finWt+" ht: "+finHt);
         Bitmap scImg = getScaledBitmap(temp, BitmapResizeLogic.SetScaledBitmap, finWt, finHt, true);
         canvas.drawBitmap(scImg, startX, startY, null); col++; 
         if (col == numCols) { col = 0; startX = hGap; startY += finHt+vGap; row++; } 
         else { startX += finWt+hGap; }
      }
      return finBitmap;
   }
   
   public enum BitmapResizeLogic {
      SetScaledBitmap, SetMaxSizeBitmap, SetExactSizeBitmap, NoResize
   }
   
   public static Bitmap getScaledBitmap(Bitmap srcImg, BitmapResizeLogic resizeLogic, 
                        int dispWt, int dispHt, boolean preserveAspectRatio)
   {
      if (!preserveAspectRatio) 
         return Bitmap.createScaledBitmap(srcImg, dispWt, dispHt, false);
      switch(resizeLogic)
      {
         case SetScaledBitmap :
            return getScaledBitmap(srcImg, dispWt, dispHt, preserveAspectRatio);
         case SetExactSizeBitmap :
            return createExactSizeBitmap(srcImg, dispWt, dispHt);
         case SetMaxSizeBitmap :
            return createMaxSizeBitmap(srcImg, dispWt, dispHt, preserveAspectRatio);
         case NoResize : 
            return srcImg;
      }      
      return getScaledBitmap(srcImg, dispWt, dispHt, preserveAspectRatio);
   }
   
   public static Bitmap createExactSizeBitmap(Bitmap srcImg, int dispWt, int dispHt)
   {
      Bitmap newImg = createMaxSizeBitmap(srcImg, dispWt, dispHt, true);
      int[] inPixel = getBitmapPixels(newImg);
      int[] outPixel = new int[dispWt*dispHt]; int index = 0;
      for(int irow = 0; irow < newImg.getHeight(); irow++) {
         if (irow >= dispHt) continue;
         for(int icol = 0; icol < newImg.getWidth(); icol++) {
            if (icol >= dispWt) continue;
            outPixel[index] = inPixel[index];
            index++;
         }
      }
      return createBitmap(outPixel, dispWt, dispHt);
   }
      
   public static Bitmap createMaxSizeBitmap(Bitmap srcImg, int dispWt, int dispHt,
                                            boolean preserveAspectRatio)
   {
      if (!preserveAspectRatio) 
         return Bitmap.createScaledBitmap(srcImg, dispWt, dispHt, false);
      int imgWt = srcImg.getWidth(), imgHt = srcImg.getHeight();
      double scImgWt = 0, scImgHt = 0;
      double aspectRatio =  ((double)imgHt / (double) imgWt);
      XONUtil.logDebugMesg("Orig Img Wt: " + imgWt + " ImgHt: " + imgHt + 
                           " AspectRat: " + aspectRatio);      
      if (imgWt > imgHt) { scImgHt = dispHt; scImgWt = scImgHt / aspectRatio; }
      else { scImgWt = dispWt; scImgHt = scImgWt * aspectRatio; }
      XONUtil.logDebugMesg("Creating Bitmap of wt: "+scImgWt+" ht: "+scImgHt);
      return Bitmap.createScaledBitmap(srcImg, Double.valueOf(scImgWt).intValue(), 
                                       Double.valueOf(scImgHt).intValue(), false);
   }
   
   public static Bitmap getScaledBitmap(Bitmap srcImg, Dimension size, 
                                        boolean preserveAspectRatio)
   {
      return getScaledBitmap(srcImg, size.width, size.height, preserveAspectRatio);
   }
   
   public static Bitmap getScaledBitmap(Bitmap srcImg, Dimension size, int percScale,
                                        boolean preserveAspectRatio)
   {
      size.width= Math.round(size.width*percScale/100); 
      size.height = Math.round(size.height*percScale/100);
      return getScaledBitmap(srcImg, size.width, size.height, preserveAspectRatio);
   }

   public static Bitmap getScaledBitmap(Bitmap srcImg, int dispWt, int dispHt, 
                                        boolean preserveAspectRatio)
   {
      int imgWt = srcImg.getWidth(), imgHt = srcImg.getHeight();
      if (imgWt < dispWt && imgHt < dispHt) return srcImg;
      if (!preserveAspectRatio) 
         return Bitmap.createScaledBitmap(srcImg, dispWt, dispHt, false);
      double scImgWt = 0, scImgHt = 0;
      double aspectRatio =  ((double)imgHt / (double) imgWt);
      XONUtil.logDebugMesg("Orig Img Wt: " + imgWt + " ImgHt: " + imgHt + 
                           " AspectRat: " + aspectRatio);      
      if (imgWt < imgHt) {   
         scImgWt = dispWt;
         scImgHt = scImgWt * aspectRatio;
         if (scImgHt > dispHt) {
            scImgHt = dispHt;
            scImgWt = scImgHt / aspectRatio;
         }
      }
      else {
         scImgHt = dispHt;
         scImgWt = scImgHt / aspectRatio;
         XONUtil.logDebugMesg("Inside scImgWt: " + scImgWt + " scImgHt: " + scImgHt);
         if (scImgWt > dispWt) {
            scImgWt = dispWt;
            scImgHt = scImgWt * aspectRatio;;
            XONUtil.logDebugMesg("Inside2 scImgWt: " + scImgWt + " scImgHt: " + scImgHt);
         }
      }
      return Bitmap.createScaledBitmap(srcImg, Double.valueOf(scImgWt).intValue(), 
                                       Double.valueOf(scImgHt).intValue(), false);
   }
   
   /**
    * Get the size in bytes of a bitmap.
    * @param bitmap
    * @return size in bytes
    */
   @TargetApi(12)
   public static int getBitmapSize(Bitmap bitmap) 
   {
       if (XONUtil.hasHoneycombMR1()) {
           return bitmap.getByteCount();
       }
       // Pre HC-MR1
       return bitmap.getRowBytes() * bitmap.getHeight();
   }

   public static Bitmap compressImage(Bitmap srcImg)
   {
      return compressImage(srcImg, XONPropertyInfo.DEFAULT_COMPRESS_QUALITY);
   }
   
   public static Bitmap compressToSize(Bitmap srcImg)
   {
      return compressToSize(srcImg, XONPropertyInfo.MAX_BITMAP_SIZE);
   }
   
   public static Bitmap compressToSize(Bitmap srcImg, int size)
   {
      WeakReference<Bitmap> srcImgRef = null;
      int percDec = 5, percComp = 100, srcImgSize = getBitmapSize(srcImg), cntr = 0;
      int origImgSize = srcImgSize, newImgSize = srcImgSize; Bitmap resImg = null;
      while(true)
      {
         if (srcImgSize < size) break;
         cntr++; newImgSize = srcImgSize; percComp -= percDec;
         if (srcImgRef == null) resImg = compressImage(srcImg, percComp);
         else { resImg = compressImage(srcImgRef.get(), percComp); 
                if (srcImgRef.get() != resImg) srcImgRef.get().recycle(); 
                srcImgRef.clear(); srcImgRef = null; }
         srcImgRef = new WeakReference<Bitmap>(resImg); 
         srcImgSize = getBitmapSize(srcImgRef.get());
         if (percComp < 60 || newImgSize == srcImgSize) break; 
      }
      XONUtil.logDebugMesg("origImgSize: "+origImgSize+" compImgSz: "+srcImgSize+
                           " compPerc: "+percComp+" NumTimesCompressed: "+cntr);
      if (srcImgRef != null) return srcImgRef.get();
      return srcImg;
   }
   
   public static Bitmap compressImage(Bitmap srcImg, int quality)
   {
      ByteArrayOutputStream outStream = null; ByteArrayInputStream inputStream = null;
      try {
         // The output will be a ByteArrayOutputStream (in memory) 
         int wt = srcImg.getWidth(), ht = srcImg.getHeight();
         XONUtil.logDebugMesg("Bfro Wt: "+wt+" Ht: "+ht+" Img Density: "+
                              srcImg.getDensity()+" quality: "+quality+" size: "+
                              getBitmapSize(srcImg));
         outStream = new ByteArrayOutputStream();
         srcImg.compress(XONPropertyInfo.DEFAULT_COMPRESS_FORMAT, quality, outStream);
         // From the ByteArrayOutputStream create a RenderedImage.   
         inputStream = new ByteArrayInputStream(outStream.toByteArray());
         if (inputStream != null) {
            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            wt = bitmap.getWidth(); ht = bitmap.getHeight();
            XONUtil.logDebugMesg("After Wt: "+wt+" Ht: "+ht+" Image Density: "+
                                 bitmap.getDensity()+" size: "+getBitmapSize(bitmap));
            return bitmap;
         }
      } catch (Exception ex) {
         XONUtil.logError("Error in Compressing Image", ex);
      } finally {
         try { if (outStream != null) outStream.close(); 
               if (inputStream != null) inputStream.close(); } 
         catch (IOException e) {}
      }
      return null;      
   }

   /**
    * Decode and sample down a bitmap from resources to the requested width and height.
    *
    * @param res The resources object containing the image data
    * @param resId The resource id of the image data
    * @param size The requested width and height of the resulting bitmap
    * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
    *         that are equal to or greater than the requested width and height
    */
   public static Bitmap decodeBitmapFromResource(Resources res, int resId, Dimension size, 
                                                 Dimension origImgSize) 
   {
       // First decode with inJustDecodeBounds=true to check dimensions
       final BitmapFactory.Options options = new BitmapFactory.Options();
       options.inJustDecodeBounds = true;
       BitmapFactory.decodeResource(res, resId, options);

       // Calculate inSampleSize
       options.inSampleSize = calculateInSampleSize(options, size, origImgSize);

       // Decode bitmap with inSampleSize set
       options.inJustDecodeBounds = false;
       return BitmapFactory.decodeResource(res, resId, options);
   }

   public static Bitmap decodeBitmapFromUri(Uri uri) 
   {
      InputStream stream = null; Activity act = XONPropertyInfo.m_MainActivity;
      try {
         stream = act.getContentResolver().openInputStream(uri);
         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = false;
         return BitmapFactory.decodeStream(stream, null, options);
      } catch(Exception ex) {
         XONUtil.logError("Exception process image uri: "+uri, ex);
      } finally {
         try { stream.close(); } catch(Exception ex) {}
      } 
      return null;
   }
   
   public static Bitmap decodeBitmapFromUri(Uri uri, Dimension size, 
                                            Dimension origImgSize) 
   {
      InputStream stream = null, dupStream = null; Activity act = XONPropertyInfo.m_MainActivity;
      try {
         stream = act.getContentResolver().openInputStream(uri);
         dupStream = act.getContentResolver().openInputStream(uri);
         Bitmap bitmap = decodeBitmapFromStream(stream, dupStream, size, origImgSize);
         XONUtil.logDebugMesg("Bitmap Gen: "+bitmap);
         if (bitmap != null) return bitmap;
      } catch(Exception ex) {
         XONUtil.logError("Exception process image uri: "+uri, ex);
      } finally {
         try { stream.close(); dupStream.close(); } catch(Exception ex) {}
      }      
      return decodeBitmapFromResource(act.getResources(), R.drawable.image, size, 
                                      origImgSize);
   }
   
   /**
    * Decode and sample down a bitmap from a file input stream to the requested width and height.
    *
    * @param stream The input stream to read from
    * @param dupStream Duplicate Stream to finally Decode the Bitmap. This is needed because 
    *        after the first decode to calc size the original stream is not accessible.
    * @param size The requested width and height of the resulting bitmap
    * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
    *         that are equal to or greater than the requested width and height
    */
   public static Bitmap decodeBitmapFromStream(InputStream stream, InputStream dupStream, 
                                               Dimension size, Dimension origImgSize) 
   {
      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(stream, null, options);

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, size, origImgSize);
      XONUtil.logDebugMesg("Sample Size: "+options.inSampleSize+
                           " Orig Image Size: "+origImgSize);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      Bitmap bitmap = BitmapFactory.decodeStream(dupStream, null, options);
      XONUtil.logDebugMesg("Bitmap Gen: "+bitmap);
      return bitmap;
   }

   /**
    * Decode and sample down a bitmap from a file to the requested width and height.
    *
    * @param filename The full path of the file to decode
    * @param size The requested width and height of the resulting bitmap
    * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
    *         that are equal to or greater than the requested width and height
    */
   public static Bitmap decodeBitmapFromFile(String filename, Dimension size) 
   {
      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(filename, options);

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, size);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      return BitmapFactory.decodeFile(filename, options);
   }

   /**
    * Decode and sample down a bitmap from a file input stream to the requested width and height.
    *
    * @param fileDescriptor The file descriptor to read from
    * @param size The requested width and height of the resulting bitmap
    * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
    *         that are equal to or greater than the requested width and height
    */
   public static Bitmap decodeBitmapFromDescriptor(FileDescriptor fileDescriptor, 
                                                   Dimension size) 
   {
      // First decode with inJustDecodeBounds=true to check dimensions
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, size);

      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
   }

   public static Bitmap decodeFile(String filePath) 
   {
      // The new size we want to scale to 
      final int REQUIRED_SIZE = 1024;     
      Dimension size = new Dimension(REQUIRED_SIZE);
      return decodeFile(filePath, size, null);
   }
   
   public static Bitmap decodeFile(String filePath, Dimension size, 
                                   Dimension origImgSize) 
   {
      // Decode image size 
      final BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(filePath, options);
      
      // Calculate inSampleSize
      options.inSampleSize = calculateInSampleSize(options, size, origImgSize);
      
      // Decode bitmap with inSampleSize set
      options.inJustDecodeBounds = false;
      return BitmapFactory.decodeFile(filePath, options);
   }
   
   
   /**
    * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
    * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
    * the closest inSampleSize that will result in the final decoded bitmap having a width and
    * height equal to or larger than the requested width and height. This implementation does not
    * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
    * results in a larger bitmap which isn't as useful for caching purposes.
    *
    * @param options An options object with out* params already populated (run through a decode*
    *            method with inJustDecodeBounds==true
    * @param size The requested width and height of the resulting bitmap
    * @return The value to be used for inSampleSize
    */
   public static int calculateInSampleSize(BitmapFactory.Options options, Dimension size)                                            
   {
      return calculateInSampleSize(options, size, null);
   }
      
   
   public static int calculateInSampleSize(BitmapFactory.Options options, Dimension size,
                                           Dimension origImgSize)                                            
   {
      // Raw height and width of image
      int reqWidth = size.width, reqHeight = size.height;
      final int height = options.outHeight;
      final int width = options.outWidth;
      if (origImgSize != null) { origImgSize.width = width; origImgSize.height = height; }
      int inSampleSize = 1;

      if (height > reqHeight || width > reqWidth) 
      {
         if (width > height) inSampleSize = Math.round((float) height / (float) reqHeight);
         else inSampleSize = Math.round((float) width / (float) reqWidth);

         // This offers some additional logic in case the image has a strange
         // aspect ratio. For example, a panorama may have a much larger
         // width than height. In these cases the total pixels might still
         // end up being too large to fit comfortably in memory, so we should
         // be more aggressive with sample down the image (=larger
         // inSampleSize).

         final float totalPixels = width * height;

         // Anything more than 2x the requested pixels we'll sample down
         // further.
         final float totalReqPixelsCap = reqWidth * reqHeight * 2;

         while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) 
         { inSampleSize++; }
      }
      return inSampleSize;
   }

   public static boolean saveImage(Bitmap bitmap, File file)
   {
      try {
         FileOutputStream fos = new FileOutputStream(file);         
         if (bitmap.hasAlpha()) bitmap.compress(CompressFormat.PNG, 100, fos); 
         else bitmap.compress(CompressFormat.PNG, 100, fos); 
         return true;
      } catch(Exception ex) { XONUtil.logError("Unable to save file: "+file.getPath(), ex); }
      return false;
   }

   public static Rect getOrigCropRect(Dimension origImgSz, Dimension scImgSz,  Rect cropRect)
   {
      return getOrigCropRect(origImgSz, scImgSz, cropRect.left, cropRect.top, 
                             cropRect.right, cropRect.bottom);
   }
   
   public static Rect getOrigCropRect(Dimension origImgSz, Dimension scImgSz, int startx, 
                                      int starty, int lastx, int lasty)
   {
      int x, y, w, h;
      x = Math.min(startx, lastx);
      y = Math.min(starty, lasty);
      w = Math.abs(startx - lastx);
      h = Math.abs(starty - lasty);

      int scwd = scImgSz.width, scht = scImgSz.height;
      int origwd = origImgSz.width, oright = origImgSz.height;

      XONUtil.logDebugMesg("Sc Wd: " + scwd + " Sc Ht: " + scht + 
                           " OrWd: " + origwd + " OrHt: " + oright);
      double scalex = 0.0, scaley = 0.0;

      scalex = (double) origwd/(double) scwd;
      scaley = (double)oright/(double)scht;

      XONUtil.logDebugMesg("Scale X: " + scalex + " scaley: " + scaley);

      int start_offsetx = 0, start_offsety = 0; 
      int image_startx = x - start_offsetx, image_starty = y - start_offsety;

      int origx = (int) (image_startx * scalex), origy = (int) (image_starty * scaley);
      int origw = (int) (w*scalex), origh = (int) (h*scaley);

      XONUtil.logDebugMesg("Orig x: "+origx+" y: "+origy+" w: "+origw+" h: "+origh);
      return new Rect(origx, origy, origx+origw, origy+origh);

   }
   
}
