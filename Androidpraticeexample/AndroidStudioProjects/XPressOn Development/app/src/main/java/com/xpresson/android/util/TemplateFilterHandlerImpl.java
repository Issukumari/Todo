package com.xpresson.android.util;

import java.util.Map;
import java.util.Vector;

import android.graphics.Bitmap;
import android.util.Log;

import com.xpresson.android.util.XONImageFilterDef.ImageFilter;

public class TemplateFilterHandlerImpl implements ImageFilterHandler
{
   private static final long serialVersionUID = 8594410719825961281L;
   
   public int m_FilterPosId;
   public String m_ImageFilterName;
   public String m_TemplateSerFile;
   public ImageFilter m_ImageFilter;
   public Vector<ImageFilterHandler> m_ImageFilterHdlrs;
   public SetImageFilter m_ImageFilterStatus = SetImageFilter.OFF;
   
   // This is the Image Pixel set for every Image before Filter is applied
   public transient int m_AvgImagePixel;
   
   public TemplateFilterHandlerImpl(int posId, String fltr, Vector<ImageFilterHandler> hdlrs)
   {
      m_FilterPosId = posId;
      m_ImageFilter = ImageFilter.PersonalizedTemplateFilter;
      m_ImageFilterName = fltr; m_ImageFilterHdlrs = hdlrs;
   }

   @Override
   public void setAvgImagePixel(int avgImgPixel)
   {
      m_AvgImagePixel = avgImgPixel;
   }

   @Override
   public String getAction()
   {
      return XONImageFilterDef.getImageFilter(m_ImageFilter, m_ImageFilterName);
   }

   @Override
   public Vector<String> getUserSetFields(boolean getFirstElem)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getFieldType(String fldName)
   {
      return "";
   }

   @Override
   public Object getFieldValue(String fldName, boolean getDispVal)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object getFieldValue(Map<String, String> imgFltrValMaps,
         String fldName, boolean getDispVal)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Object getFieldValue(Map<String, String> imgFltrValMaps,
         String fldName, String paramFld, String paramFldExt, boolean getDispVal)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setColorParam(int color)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public Map<String, String> setFieldValue(String fldName, String val,
         boolean reFactor)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, String> setFieldValue(Map<String, String> imgFltrValMaps,
         String fldName, String val, boolean reFactor)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, String> setFieldValue(Map<String, String> imgFltrValMaps,
         String fldName, String paramFld, String fldExt, String val,
         boolean reFactor)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getValue(String fldName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, String> getFieldParam(String fldName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public SetImageFilter setImageFilterParam(String action, String val)
   {
      ImageFilter imgFltr = null; String[] filterParams = action.split("_");
      if (filterParams[1].equals("On")) 
         imgFltr = ImageFilter.valueOf(m_ImageFilter+"_On");
      else imgFltr = ImageFilter.valueOf(m_ImageFilter+"_Off");
      return setImageFilterParam(imgFltr, val);
   }

   @Override
   public SetImageFilter setImageFilterParam(ImageFilter imgFltr, String val)
   {
      ImageFilterHandler imgFilterHdlr = null; 
      String[] filterParams = imgFltr.toString().split("_");
      if (filterParams[1].equals("On")) m_ImageFilterStatus = SetImageFilter.ON;                                          
      else if (filterParams[1].equals("Active")) 
         m_ImageFilterStatus = SetImageFilter.ACTIVATED; 
      else m_ImageFilterStatus = SetImageFilter.OFF;
      for (int i = 0; i < m_ImageFilterHdlrs.size(); i++)
      {
         imgFilterHdlr = m_ImageFilterHdlrs.elementAt(i);
         imgFilterHdlr.setImageFilterParam(imgFltr, val);
      }
      return m_ImageFilterStatus;      
   }

   @Override
   public boolean canApplyImageFilter(String imageFltr) 
   { 
      ImageFilterHandler imgFilterHdlr = null; String appImgFltr = "";
      Map<String, Vector<String>> imgFltrRules = XONImageFilterDef.m_ImageFilterRules;
      for (int i = 0; i < m_ImageFilterHdlrs.size(); i++)
      {
         imgFilterHdlr = m_ImageFilterHdlrs.elementAt(i);
         if (!imgFilterHdlr.canApplyImageFilter(imageFltr)) return false;
         appImgFltr = imgFilterHdlr.getAction().toString();
//         if (imageFltr.equals(appImgFltr)) return false;
         Vector<String> negateFltrs = imgFltrRules.get(appImgFltr);
         if (negateFltrs == null) continue;
         if (negateFltrs.contains(imageFltr)) return false;
      }      
      return true;
   }
   
   @Override
   public Bitmap applyFilter(Bitmap srcImg, boolean useNewThread)
   {
      try {
         return applyFilter(m_ImageFilter, srcImg, useNewThread);
      } catch(Exception ex) { XONUtil.logError("", ex); }
      return srcImg; 
   }

   @Override
   public int[] applyFilter(int[] pixels, int wt, int ht)
   {
      return applyFilter(m_ImageFilter, pixels, wt, ht, null, null);
   }

   @Override
   public int[] applyFilter(int[] pixels, int wt, int ht, int[] inPixels,
                            int[] outPixels)
   {
      return applyFilter(m_ImageFilter, pixels, wt, ht, inPixels, outPixels);
   }
   
   private Bitmap applyFilter(ImageFilter imgFltr, Bitmap srcImg, boolean useNewThread)
   {      
      if (m_ImageFilterStatus == SetImageFilter.OFF) return srcImg;
//      String[] presetValArr = null; 
//      float amt = 0.0F, angle = 0.0F, cntrX = 0.0F, cntrY = 0.0F; int index = 0;
      int wt = srcImg.getWidth(), ht = srcImg.getHeight();  
      int[] pixels = XONImageUtil.getBitmapPixels(srcImg); 
      return XONImageUtil.createBitmap(applyFilter(imgFltr, pixels, wt, ht, null, null), wt, ht);
   }
   
   private int[] applyFilter(ImageFilter imgFltr, int[] pixels, int wt, int ht, 
                             int[] inPixels, int[] outPixels)
   {
      ImageFilterHandler imgFilterHdlr = null; ImageFilterController cntrlr = null;  
      cntrlr = (ImageFilterController) XONObjectCache.
                                       getObjectForKey("ImageFilterController");
      int avgImgPixel = ((Integer) XONObjectCache.getObjectForKey("AvgImagePixel")).
                                   intValue();
      for (int i = 0; i < m_ImageFilterHdlrs.size(); i++)
      {
         imgFilterHdlr = m_ImageFilterHdlrs.elementAt(i);
         if (imgFilterHdlr.getAction().equals(ImageFilter.AutoFix2Enhance) && 
             avgImgPixel < 96) 
            imgFilterHdlr = ImageFilter.createImageFilterHandler(
                                        ImageFilter.AutoFix1Enhance.toString());
         if (imgFilterHdlr.getAction().contains("Enhance")) {
            if (cntrlr.getImageFilterHandler(imgFilterHdlr.getAction()) != null) continue;
            if (imgFilterHdlr.getAction().contains("AutoFix")){
               if (cntrlr.getImageFilterHandler(
                          ImageFilter.AutoFix1Enhance.toString()) != null) continue;               
               if (cntrlr.getImageFilterHandler(
                          ImageFilter.AutoFix2Enhance.toString()) != null) continue;               
            }
         }
         XONUtil.debugLog(Log.VERBOSE,"Applying Filter using: "+imgFilterHdlr.getAction());
         pixels = imgFilterHdlr.applyFilter(pixels, wt, ht, inPixels, outPixels); 
      }
      return pixels;
   }
   
   @Override
   public String toString()
   {
      StringBuffer mesg = new StringBuffer("Template Filter Name: "+m_ImageFilterName);
      for (int i = 0; i < m_ImageFilterHdlrs.size(); i++)
         mesg.append(" Filter At: "+(i+1)+" is "+m_ImageFilterHdlrs); 
      return mesg.toString();
   }

}
