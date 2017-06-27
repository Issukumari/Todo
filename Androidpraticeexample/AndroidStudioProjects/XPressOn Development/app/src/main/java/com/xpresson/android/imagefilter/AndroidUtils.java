package com.xpresson.android.imagefilter;

import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

public class AndroidUtils
{
  public static int dipTopx(float dip, Context context)
  {
    int num = (int)TypedValue.applyDimension(
      1, dip, 
      context.getResources().getDisplayMetrics());
    return num;
  }

  public static float pxTodip(int px, Context context)
  {
    float num = px / context.getResources().getDisplayMetrics().density;
    return num;
  }

  public static Bitmap drawableToBitmap(Drawable drawable)
  {
    int width = drawable.getIntrinsicWidth();
    int height = drawable.getIntrinsicHeight();
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);
    return bitmap;
  }

  public static int[] drawableToIntArray(Drawable drawable) {
    Bitmap bitmap = drawableToBitmap(drawable);

    int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();

    int[] colors = new int[bitmapWidth * bitmapHeight];
    bitmap.getPixels(colors, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

    return colors;
  }

  public static int[] bitmapToIntArray(Bitmap bitmap) {
    int bitmapWidth = bitmap.getWidth();
    int bitmapHeight = bitmap.getHeight();

    int[] colors = new int[bitmapWidth * bitmapHeight];
    bitmap.getPixels(colors, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

    return colors;
  }

  public static int getBitmapOfWidth(Resources res, int id)
  {
    try {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeResource(res, id, options);
      return options.outWidth; } catch (Exception e) {
    }
    return 0;
  }

  public static int getBitmapOfHeight(Resources res, int id)
  {
    try
    {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeResource(res, id, options);
      return options.outHeight; } catch (Exception e) {
    }
    return 0;
  }
  
  public static int contains(Vector<Region> shapes, int x, int y)
  {
     Region region = null;
     for (int i = 0; i < shapes.size(); i++) {
        region = shapes.elementAt(i); if (region.contains(x, y)) return i;
     }
     return -1;
  }
  
}
