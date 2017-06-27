package com.xpresson.android.imagefilter;

public abstract class XONFilter
{
   protected int width;
   protected int height;
   
   public int[] filter( int[] src ,int w, int h) 
   {
      int[] inPixels = new int[w];
      int[] outPixels = new int[w*h];
      return filter( src ,w, h, inPixels, outPixels);
   }
   
   public abstract int[] filter( int[] src ,int w, int h, int[] inPixels, int[] outPixels); 
}
