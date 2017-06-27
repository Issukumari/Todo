package com.xpresson.android.util;

public class Dimension
{
   public int width = 0;
   public int height = 0;
   
   public Dimension() { width = 0; height = 0; }
   public Dimension(int size) { width = size; height = size; }
   public Dimension(int w, int h) { width = w; height = h; }
   public int getArea() { return width*height; }
   public String toString() { return "Width: "+width+" Height: "+height; }
}
