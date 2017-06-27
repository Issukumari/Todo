package com.xpresson.android.imagefilter;

import java.util.Random;

import com.xpresson.android.util.XONUtil;

public class LinearFadeFilter extends XONFilter
{
   float m_FadePerc = 0.0f;
   public enum FadeType { Horz, Vert, Diag, Rand, Gauss; }
   public FadeType m_FadeType = FadeType.Horz;

   public LinearFadeFilter(float fadePerc, FadeType fadeType) 
   { m_FadePerc = fadePerc; m_FadeType = fadeType; 
     XONUtil.logDebugMesg("Fade Perc: "+m_FadePerc+" FadeType: "+m_FadeType); }
   
   @Override
   public int[] filter(int[] src, int imgWt, int imgHt, int[] inPixels, int[] outPixels)
   {
      double fadeIterPerc = 0.0;
      double fadeWt = m_FadePerc*imgWt, fadeHt = m_FadePerc*imgHt, pixelDelta = 0.0;
      double newWt = (double)imgWt - fadeWt, newHt = (double)imgHt - fadeHt;
      double dxFade = fadeWt/2.0D, dyFade = fadeHt/2.0D, splitFade = (double)imgHt/2.0D;
      if (m_FadeType.equals(FadeType.Vert)) splitFade = (double)imgWt/2.0D;
      double delta = Math.sqrt((Math.pow(((double)imgWt/2.0D),2))+
                               (Math.pow(((double)imgHt/2.0D),2)));
      
      if (outPixels == null) outPixels = new int[imgWt*imgHt];
      Random rand = null; int index = 0, color = 0, alpha = 0;
      for (int irow = 0; irow < imgHt; irow++)
      {
         for (int icol = 0; icol < imgWt; icol++)
         {
            if ((irow > dyFade && icol > dxFade) && 
                  (irow < (dyFade+newHt) && icol < (dxFade+newWt))) continue;
            index = irow*imgWt+icol; color = src[index]; alpha = color >> 24 & 0xFF;
            if (m_FadeType.equals(FadeType.Horz)) {
               if (irow < splitFade) fadeIterPerc = irow/splitFade;
               else if (irow > splitFade) fadeIterPerc = 1-((double)irow-splitFade)/splitFade;
            } else if (m_FadeType.equals(FadeType.Vert)) {
               if (icol < splitFade) fadeIterPerc = icol/splitFade;
               else if (icol > splitFade) fadeIterPerc = 1-((double)icol-splitFade)/splitFade;
            } else if (m_FadeType.equals(FadeType.Diag)) {
               pixelDelta = Math.sqrt((Math.pow(((double)icol/2.0D),2))+
                                      (Math.pow(((double)irow/2.0D),2)));
               if (pixelDelta < delta) fadeIterPerc = pixelDelta/delta;
               else fadeIterPerc = 1-(pixelDelta-delta)/delta;
            } else if (m_FadeType.equals(FadeType.Rand)) {
               if (rand == null) rand = new Random();
               fadeIterPerc = rand.nextDouble();
            } else if (m_FadeType.equals(FadeType.Gauss)) {
               if (rand == null) rand = new Random();
               fadeIterPerc = rand.nextGaussian();
            }
            alpha = (int)Math.round(alpha*fadeIterPerc); 
            outPixels[index] = PixelUtils.setAlpha(color, alpha);
         }
      }
      return outPixels;
   }

}
