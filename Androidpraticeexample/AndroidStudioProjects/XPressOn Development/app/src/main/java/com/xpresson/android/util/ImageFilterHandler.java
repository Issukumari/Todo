package com.xpresson.android.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

import android.graphics.Bitmap;

// This interface is used to set and process Image Filter.

public interface ImageFilterHandler extends Serializable
{
   // This enum specifies the set and unset options for ImageFilterHandler.
   public static enum SetImageFilter { ON, OFF, ACTIVATED }
   
   // Returns the Action Component associated with the ImageFilterHandler
   public void setAvgImagePixel(int pixel);
   
   // Returns the Action Component associated with the ImageFilterHandler
   public String getAction();

   // Returns the Fields that can be set by uses
   public Vector<String> getUserSetFields(boolean getFirstElem);

   // Returns the type associated with the Field
   public String getFieldType(String fldName);
   
   // Returns the value associated with the Field. Here the param fld is set to 
   // Amt and ext to x
   public Object getFieldValue(String fldName, boolean getDispVal);
   
   // Returns the value associated with the Field. Here the param fld is set to 
   // Amt and ext to x
   public Object getFieldValue(Map<String, String> imgFltrValMaps, String fldName, 
                               boolean getDispVal);
   
   // Returns the value associated with the Field and the Param Field
   public Object getFieldValue(Map<String, String> imgFltrValMaps, String fldName, 
                               String paramFld, String paramFldExt, boolean getDispVal);
   
   // Set Color Param Value
   public void setColorParam(int color);
   
   // Returns the value associated with the Field. Here the param fld is default to 
   // Amt and ext to x.
   public Map<String, String> setFieldValue(String fldName, String val, boolean reFactor);
   
   // Returns the value associated with the Field. Here the param fld is default to 
   // Amt and ext to x.
   public Map<String, String> setFieldValue(Map<String, String> imgFltrValMaps, 
                                            String fldName, String val, boolean reFactor);
   
   // Returns the value associated with the Field and the Param Field
   public Map<String, String> setFieldValue(Map<String, String> imgFltrValMaps, 
                                            String fldName, String paramFld, 
                                            String fldExt, String val, boolean reFactor);
   
   // Returns the value associated with the ImageFilterHandler
   public String getValue(String fldName);

   // Returns the HashMap associated with the ImageFilterHandler
   public Map<String, String> getFieldParam(String fldName);

   // This function is called to set Image Filter Data. The Action Param is composed of
   // ImageFilterActions, MethodName(Increase/Decrease/On/Of) and optional param name
   // e.g. SharpenFilter_On,  SharpenFilter_Off, SharpenFilter_Def, SharpenFilter_Cancel
   // ContrastFilter_Inc, ContrastFilter_Dec, ContrastFilter_Def, ContrastFilter_Cancel,
   public SetImageFilter setImageFilterParam(String action, String val);
   public SetImageFilter setImageFilterParam(XONImageFilterDef.ImageFilter imgFltr, String val);
   public boolean canApplyImageFilter(String imageFltr);
   
   // Apply Filter
   public Bitmap applyFilter(Bitmap src, boolean useNewThread);   
   public int[] applyFilter(int[] pixels, int wt, int ht);
   public int[] applyFilter(int[] pixels, int wt, int ht, int[] inPixels, int[] outPixels);
}