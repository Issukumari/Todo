package com.xpresson.android.imagefilter;

public class ImageFunction2D implements Function2D 
{

   public final static int ZERO = 0;
   public final static int CLAMP = 1;
   public final static int WRAP = 2;
   
   protected int[] pixels;
   protected int width;
   protected int height;
   protected int edgeAction = ZERO;
   protected boolean alpha = false;

   public ImageFunction2D(int[] pixels, int width, int height, int edgeAction, boolean alpha) {
      init(pixels, width, height, edgeAction, alpha);
   }
   
   public void init(int[] pixels, int width, int height, int edgeAction, boolean alpha) {
      this.pixels = pixels;
      this.width = width;
      this.height = height;
      this.edgeAction = edgeAction;
      this.alpha = alpha;
   }
   
   @Override
   public float evaluate(float x, float y) {
      int ix = (int)x;
      int iy = (int)y;
      if (edgeAction == WRAP) {
         ix = ImageMath.mod(ix, width);
         iy = ImageMath.mod(iy, height);
      } else if (ix < 0 || iy < 0 || ix >= width || iy >= height) {
         if (edgeAction == ZERO)
            return 0;
         if (ix < 0)
            ix = 0;
         else if (ix >= width)
            ix = width-1;
         if (iy < 0)
            iy = 0;
         else if (iy >= height)
            iy = height-1;
      }
      return alpha ? ((pixels[iy*width+ix] >> 24) & 0xff) / 255.0f : PixelUtils.brightness(pixels[iy*width+ix]) / 255.0f;
   }
   
   public void setEdgeAction(int edgeAction) {
      this.edgeAction = edgeAction;
   }

   public int getEdgeAction() {
      return edgeAction;
   }

   public int getWidth() {
      return width;
   }
   
   public int getHeight() {
      return height;
   }
   
   public int[] getPixels() {
      return pixels;
   }
   

}
