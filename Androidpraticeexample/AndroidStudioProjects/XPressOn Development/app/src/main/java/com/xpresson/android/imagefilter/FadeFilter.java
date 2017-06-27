package com.xpresson.android.imagefilter;

import java.lang.ref.WeakReference;
import java.util.Vector;

import android.graphics.Color;
import android.graphics.Region;

public class FadeFilter extends XONFilter
{
   float m_FadePerc = 0.0f;
   int m_MinFadeRegion = 10;
   int m_MaxFadeRegion = 20;
   float m_FadeRegionSize = 10.0f;
   
   public enum FadeType { Min, Uniform, Box; }
   public static enum ShapeType { Rect, Oval, Path }
   public static enum MergeType { Fading, Color }
   
   FadeType m_FadeType = FadeType.Uniform;
   ShapeType m_ShapeType = ShapeType.Rect;
   MergeType m_MergeType = MergeType.Fading;
   
   int m_Color = Color.BLACK;

   public FadeFilter(float fadePerc) { m_FadePerc = fadePerc; }
   
   public FadeFilter(float fadePerc, String fadeType) 
   { m_FadePerc = fadePerc; m_FadeType = FadeType.valueOf(fadeType); }
   
   public FadeFilter(float fadePerc, String fadeType, String shapeType) 
   { 
      m_FadePerc = fadePerc; 
      m_FadeType = FadeType.valueOf(fadeType); 
      m_ShapeType = ShapeType.valueOf(shapeType);
   }
   
   public void setColor(int color) { m_Color = color; }
   public void setFadeType(String fadeType) {  m_FadeType = FadeType.valueOf(fadeType); }
   
   public void setMergeType(String mergeType) 
   {  m_MergeType = MergeType.valueOf(mergeType); }
   
   public void setShapeType(String shapeType) 
   { m_ShapeType = ShapeType.valueOf(shapeType); }
   
   public void setFadeParam(int minFadeReg, int maxFadeReg, float fadeRegSize)
   {
      m_MinFadeRegion = minFadeReg;
      m_MaxFadeRegion = maxFadeReg;
      m_FadeRegionSize = fadeRegSize;
   }
   
   @Override
   public int[] filter(int[] src, int imgWt, int imgHt, int[] inPixels, int[] outPixels)
   {
      double x = 0.0, y = 0.0, w = 0.0, h = 0.0, fadeIterPerc = 0.0;
      double fadeWt = m_FadePerc*imgWt, fadeHt = m_FadePerc*imgHt;
      double dxFade = fadeWt/2.0D, dyFade = fadeHt/2.0D; 
      int fadeRegions = (int)Math.round(dxFade);
      if (outPixels == null) outPixels = new int[imgWt*imgHt];
      switch(m_FadeType)
      {
         case Uniform :  
            if (dyFade > dxFade) fadeRegions = (int)Math.round(dyFade);
            if (fadeRegions < m_MinFadeRegion) fadeRegions = m_MinFadeRegion;
            if (fadeRegions > m_MaxFadeRegion) fadeRegions = m_MaxFadeRegion;
            break;
         case Box:
            fadeRegions = (int)Math.round(dxFade/m_FadeRegionSize);
            if (dyFade > dxFade) fadeRegions = (int)Math.round(dyFade/m_FadeRegionSize);
            if (fadeRegions < m_MinFadeRegion) fadeRegions = m_MinFadeRegion;
            if (fadeRegions > m_MaxFadeRegion) fadeRegions = m_MaxFadeRegion;
            break;
         default : fadeRegions = m_MinFadeRegion;
      }
      
      int fadeIter = fadeRegions;
      WeakReference<Vector<Region>> fadeShapeRef = null; 
      Vector<Region> fadeShape = new Vector<Region>(fadeRegions); 
      fadeShapeRef = new WeakReference<Vector<Region>>(fadeShape);
      while (fadeIter != 0)
      {         
         fadeIterPerc = (double)fadeIter/(double)fadeRegions;
         x = fadeIterPerc*dxFade; y = fadeIterPerc*dyFade; 
         w = (double)imgWt-2*fadeIterPerc*dxFade; h = (double)imgHt-2*fadeIterPerc*dyFade;         
         if (fadeIter == 1) { x = 0.0; y = 0.0; w = (double)imgWt; h = (double)imgHt; } 
         int left = (int) Math.round(x), top = (int)Math.round(y); 
         int right = (int) Math.round(x+w), bot = (int) Math.round(y+h);
         switch(m_ShapeType)
         {
            case Rect :
               fadeShape.addElement(new Region(left, top, right, bot)); break;
            default :
               fadeShape.addElement(new Region(left, top, right, bot)); break;
         }
         fadeIter--;
      }
      
      int index = 0, color = 0, alpha = 0;
      for (int irow = 0; irow < imgHt; irow++)
      {
         for (int icol = 0; icol < imgWt; icol++)
         {
            fadeIter = AndroidUtils.contains(fadeShape, icol, irow);            
            index = irow*imgWt+icol; color = src[index]; alpha = color >> 24 & 0xFF; 
            switch(m_MergeType)
            {
               case Fading :
                  if (fadeIter != -1)  { fadeIter = fadeRegions-fadeIter; 
                                         fadeIterPerc = (double)fadeIter/(double)fadeRegions; 
                                         alpha = (int)Math.round(alpha*fadeIterPerc); }
                  outPixels[index] = PixelUtils.setAlpha(color, alpha); 
                  break;
               case Color :
                  if (fadeIter <= 0) { outPixels[index] = color; continue; }
                  fadeIter = fadeRegions-fadeIter; 
                  fadeIterPerc = (double)fadeIter/(double)fadeRegions; 
                  outPixels[index] = PixelUtils.mergeColor(color, m_Color, fadeIterPerc);
                  break;
            
            }
         }
      }
      
      fadeShapeRef.clear(); fadeShapeRef = null; 
      return outPixels;
   }

}
