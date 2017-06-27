package com.xpresson.android.imagefilter;

/**
 * A filter which uses the brightness of each pixel to lookup a color from a colormap. 
 */
public class LookupFilter extends PointFilter 
{  
   private Colormap colormap;   

   /**
    * Construct a LookupFilter.
    */
   public LookupFilter() {
     canFilterIndexColorModel = true;
     this.colormap = new SpectrumColormap();
   }

   /**
    * Construct a LookupFilter.
    * @param colormap the color map
    */
   public LookupFilter(Colormap colormap) 
   {
     canFilterIndexColorModel = true;
     this.colormap = colormap;
   }

   /**
    * Set the colormap to be used for the filter.
    * @param colormap the colormap
    * @see #getColormap
    */
   public void setColormap(Colormap colormap) {
     this.colormap = colormap;
   }

   /**
    * Get the colormap to be used for the filter.
    * @return the colormap
    * @see #setColormap
    */
   public Colormap getColormap() {
     return colormap;
   }

   public int filterRGB(int x, int y, int rgb) {
//   int a = rgb & 0xff000000;
     int alpha = (rgb>>24) & 0xff;
     if (alpha == 0) return 0;
     int r = (rgb >> 16) & 0xff;
     int g = (rgb >> 8) & 0xff;
     int b = rgb & 0xff;
     rgb = (r + g + b) / 3;
     return colormap.getColor(rgb/255.0f);
   }

   public String toString() {
      return "Colors/Lookup...";
   }

}

