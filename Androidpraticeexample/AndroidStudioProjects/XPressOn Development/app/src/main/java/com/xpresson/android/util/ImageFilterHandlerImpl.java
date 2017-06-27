package com.xpresson.android.util;

import java.util.Map;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.xpresson.android.R;
import com.xpresson.android.imagefilter.*;

import com.xpresson.android.util.XONImageFilterDef.ImageFilter;

public class ImageFilterHandlerImpl implements ImageFilterHandler
{
   private static final long serialVersionUID = -1608555438314208353L;
   
   String m_Value = null; int m_Color = Color.BLACK;
   public XONImageFilterDef.ImageFilter m_ImageFilter;
   public SetImageFilter m_ImageFilterStatus = SetImageFilter.OFF;
   public Vector<Map<String, String>> m_FilterParamValues = null;  

   // This is the Image Pixel set for every Image before Filter is applied
   public transient int m_AvgImagePixel;
   
   public ImageFilterHandlerImpl(String imgFltr)
   {
      m_ImageFilter = ImageFilter.valueOf(imgFltr);
      m_FilterParamValues = XONImageFilterDef.getImageFilterRes(m_ImageFilter.toString());
      if (ImageFilter.showColorPickerDialog(m_ImageFilter.toString()))
      {
         Map<String, String> imgFltrValMaps = getFieldParam("Color");
         m_Color = Color.parseColor(imgFltrValMaps.get("Value"));
      }
      setDefaultParamValues(); 
   }
   
   private void setDefaultParamValues() {}
      
   @Override
   public void setAvgImagePixel(int avgImgPixel)
   {
      m_AvgImagePixel = avgImgPixel;
   }

   @Override
   public String getAction()
   {
      return m_ImageFilter.toString();
   }

   // Returns the Fields that can be set by uses
   @Override
   public Vector<String> getUserSetFields(boolean getFirstElem)
   {
      Vector<String> userSetFlds = new Vector<String>(); 
      for(int iter = 0; iter < m_FilterParamValues.size(); iter++)
      {
         Map<String, String> imgFltrValMaps = m_FilterParamValues.elementAt(iter);
         if (imgFltrValMaps.size() == 0) continue;
         String paramFldName = imgFltrValMaps.get("Name");
         String paramFldType = imgFltrValMaps.get("Type");
         XONUtil.logDebugMesg("Fld Name: "+paramFldName+" FldType: "+paramFldType);
         if (paramFldType.equals("PresetValue")) continue;
//         if (fldType.equals("Intensity")) { if (paramFldType.contains(fldType)) 
//                                              userSetFlds.addElement(paramFldName); }
         userSetFlds.addElement(paramFldName);
         if (getFirstElem) return userSetFlds;;
      }
      return userSetFlds;
   }

   public void setValue(String val)
   {
      m_Value = val;
   }
   
   @Override
   public String getValue(String fldName)
   {
      return m_Value;
   }

   @Override
   public void setColorParam(int color) 
   { 
      XONUtil.logDebugMesg("Old Color: "+m_Color+" New Color: "+color);
      m_Color = color; 
   }
   
   @Override
   public SetImageFilter setImageFilterParam(String imgFltr, String val)
   {
      return setImageFilterParam(ImageFilter.valueOf(imgFltr), val);
   }

   @Override
   public SetImageFilter setImageFilterParam(ImageFilter imgFltr, String val)
   {
      m_Value = val; String[] filterParams = imgFltr.toString().split("_");
      if (filterParams[1].equals("On")) m_ImageFilterStatus = SetImageFilter.ON;                                          
      else if (filterParams[1].equals("Active")) 
         m_ImageFilterStatus = SetImageFilter.ACTIVATED; 
      else m_ImageFilterStatus = SetImageFilter.OFF;
      return m_ImageFilterStatus;
   }

   // Returns the type associated with the Field
   @Override
   public String getFieldType(String fldName)
   {
      String paramFldName = "", fldType = "";
      for(int iter = 0; iter < m_FilterParamValues.size(); iter++)
      {
         Map<String, String> imgFltrValMaps = m_FilterParamValues.elementAt(iter);
         paramFldName = imgFltrValMaps.get("Name"); fldType = imgFltrValMaps.get("Type");
         if (paramFldName.equals(fldName)) return fldType;
      }
      return fldType;
   }
   
   private float getFieldValue(String fldName, String subFld)
   {
      return getFieldValue(null, fldName, subFld, "X");
   }
   
   private float getFieldValue(Map<String, String> imgFltrValMaps, String fldName, 
                               String subFld, String paramFldExt)
   {
      if (imgFltrValMaps == null) imgFltrValMaps = getFieldParam(fldName);
      String subFldVal = imgFltrValMaps.get(subFld+paramFldExt);
      return Float.valueOf(subFldVal).floatValue();
   }
   
   private float getProperFldVal(String fldName)
   {
      float flVal = ((Float)getFieldValue(fldName, false)).floatValue();
      float minVal = getFieldValue(fldName, "Min");
      float maxVal = getFieldValue(fldName, "Max");
      if (flVal < minVal) flVal = minVal;
      else if (flVal > maxVal) flVal = maxVal;
      return flVal;
   }

   public boolean getPresetBoolValue(String fldName)
   {
      return Boolean.valueOf(getPresetValue(null, fldName)).booleanValue();
   }
   
   public float getPresetFloatValue(String fldName)
   {
      return Float.valueOf(getPresetValue(null, fldName)).floatValue();
   }
   
   public int getPresetIntValue(String fldName)
   {
      return Integer.valueOf(getPresetValue(null, fldName)).intValue();
   }
   
   public String getPresetValue(String fldName)
   {
      return getPresetValue(null, fldName);
   }
   
   public String getPresetValue(Map<String, String> imgFltrValMaps, String fldName)
   {
      if (imgFltrValMaps == null) imgFltrValMaps = getFieldParam(fldName);
      if (imgFltrValMaps == null) return "";
      String fldType = imgFltrValMaps.get("Type");
      if (!fldType.equals("PresetValue")) return "";
      return imgFltrValMaps.get("Value");   
   }
   
   @Override
   public Object getFieldValue(String fldName, boolean getDispVal)
   {
      return getFieldValue(null, fldName, getDispVal);
   }
   
   // This method is called for Field Types SetIntensity and SetParamValue
   @Override
   public Object getFieldValue(Map<String, String> imgFltrValMaps, String fldName, 
                               boolean getDispVal)
   {
      return getFieldValue(imgFltrValMaps, fldName, "Amt", "X", getDispVal);
   }

   public float getFloatFieldValue(Map<String, String> imgFltrValMaps, String fldName, 
                                   String paramFld, String paramFldExt, boolean getDispVal)
   {
      return ((Float) getFieldValue(imgFltrValMaps, fldName, paramFld, paramFldExt, 
                                    getDispVal)).floatValue();
   }
   
   @Override
   public Object getFieldValue(Map<String, String> imgFltrValMaps, String fldName, 
                               String paramFld, String paramFldExt, boolean getDispVal)
   {
      String fldVal = "";
      if (imgFltrValMaps == null) imgFltrValMaps = getFieldParam(fldName);
      String fldType = imgFltrValMaps.get("Type");
      if (fldType.contains("Intensity") || fldType.equals("CircularImpact")) {
         fldVal = imgFltrValMaps.get(paramFld+paramFldExt);
         if (getDispVal) return Float.valueOf(fldVal);
         if (!imgFltrValMaps.containsKey("Factor"+paramFldExt) ||
              fldName.equals("Center")) return Float.valueOf(fldVal); 
         String factor = imgFltrValMaps.get("Factor"+paramFldExt); 
         float factorVal = Float.valueOf(factor).floatValue(); 
         float floatVal = Float.valueOf(fldVal).floatValue();
         float finalFldVal = floatVal*factorVal;
         return Float.valueOf(finalFldVal);
      } 
      return fldVal;
   }
   
   // Returns the value associated with the Field. Here the param fld is default to 
   // Amt and ext to x.
   public Map<String, String> setFieldValue(String fldName, String val, boolean reFactor)
   {
      return setFieldValue(null, fldName, val, reFactor);
   }
   
   // Returns the value associated with the Field. Here the param fld is default to 
   // Amt and ext to x.
   public Map<String, String> setFieldValue(Map<String, String> imgFltrValMaps, 
                              String fldName, String val, boolean reFactor)
   {
      return setFieldValue(imgFltrValMaps, fldName, "Amt", "X", val, reFactor);
   }
   
   // Returns the value associated with the Field and the Param Field
   public Map<String, String> setFieldValue(Map<String, String> imgFltrValMaps, 
                                            String fldName, String paramFld,  
                                            String fldExt, String val, boolean reFactor)
   {
      if (imgFltrValMaps == null) imgFltrValMaps = getFieldParam(fldName);
      if (!reFactor) { imgFltrValMaps.put(paramFld+fldExt, val);
                       return imgFltrValMaps; }
      String fldType = imgFltrValMaps.get("Type");
      if (!fldType.contains("Intensity") || !fldType.equals("CircularImpact")) 
      {
         if (!imgFltrValMaps.containsKey("Factor"+fldExt) ||
             fldName.equals("Center")) { imgFltrValMaps.put(paramFld+fldExt, val);
                                          return imgFltrValMaps; }
         String factor = imgFltrValMaps.get("Factor"+fldExt); 
         float factorVal = Float.valueOf(factor).floatValue(); 
         float floatVal = Float.valueOf(val).floatValue();
         float finalFldVal = floatVal/factorVal;
         imgFltrValMaps.put(paramFld+fldExt, Float.valueOf(finalFldVal).toString());
      }
      return imgFltrValMaps;
   }
   
   // Returns the HashMap associated with the ImageFilterHandler
   @Override
   public Map<String, String> getFieldParam(String fldName)
   {
      for(int iter = 0; iter < m_FilterParamValues.size(); iter++)
      {
         Map<String, String> imgFltrValMaps = m_FilterParamValues.elementAt(iter);
         String paramFldName = imgFltrValMaps.get("Name");
         if (paramFldName.equals(fldName)) return imgFltrValMaps;
      }
      return null;
   }
   
   @Override
   public boolean canApplyImageFilter(String imageFltr) { return true; }
   
   @Override
   public Bitmap applyFilter(Bitmap srcImg, boolean useNewThread)
   {
      try {
         return applyFilter(m_ImageFilter, srcImg, useNewThread);
      } catch(Exception ex) { XONUtil.logError("", ex); }
      return srcImg; 
   }

//   private Bitmap applyFilter(String imgFltr, Bitmap srcImg, boolean useNewThread)
//   {
//      return applyFilter(ImageFilter.valueOf(imgFltr), srcImg, useNewThread);
//   }
   
   private Bitmap applyFilter(ImageFilter imgFltr, Bitmap srcImg, boolean useNewThread)
   {      
      if (m_ImageFilterStatus == SetImageFilter.OFF) return srcImg;
//      String[] presetValArr = null; 
//      float amt = 0.0F, angle = 0.0F, cntrX = 0.0F, cntrY = 0.0F; int index = 0;
      int wt = srcImg.getWidth(), ht = srcImg.getHeight();  
      int[] pixels = XONImageUtil.getBitmapPixels(srcImg); 
      return XONImageUtil.createBitmap(applyFilter(imgFltr, pixels, wt, ht, null, null), wt, ht);
   }
   
   @Override
   public int[] applyFilter(int[] pixels, int wt, int ht)
   {
      return applyFilter(m_ImageFilter, pixels, wt, ht, null, null);
   }
   
   @Override
   public int[] applyFilter(int[] pixels, int wt, int ht, int[] inPixels, int[] outPixels)
   {
      return applyFilter(m_ImageFilter, pixels, wt, ht, inPixels, outPixels);
   }
   
   public int[] applyUniformFadeFilter(int[] pixels, int wt, int ht, float fadeMar, 
                                        int[] inPixels, int[] outPixels)
   {
      float borFadePer = XONPropertyInfo.getFloatRes(R.string.FadeBorderPercent);
      String strVal = Float.valueOf(fadeMar*borFadePer).toString();
      ImageFilterHandler hdlr = null;
      hdlr = ImageFilter.createImageFilterHandler(ImageFilter.UniformFadeEffects.toString());
      hdlr.setFieldValue("Uniform", strVal, true);
      return hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
   }
   
   boolean m_SetColorParam = false;
   private ImageFilterHandler populateImageFilterHdlr(String imgFltr, int counter, 
                                                      float fadePerc)
   {
      ImageFilterHandler hdlr = ImageFilter.createImageFilterHandler(imgFltr);
      Vector<String> origUserSetFlds = getUserSetFields(true);
      String origParamNm = origUserSetFlds.firstElement();
      if (origUserSetFlds == null || origUserSetFlds.size() == 0) return hdlr;      
            
      Vector<String> userSetFlds = hdlr.getUserSetFields(true); 
      if (userSetFlds == null || userSetFlds.size() == 0) return hdlr;      
      String paramNm = userSetFlds.firstElement(); 
      String fldType = hdlr.getFieldType(paramNm);
      if (fldType == null || fldType.length() == 0) return hdlr;
      Map<String, String> imgFldMap = hdlr.getFieldParam(paramNm);
      if (fldType.contains("Intensity"))
      {
         float flVal = (((Float) getFieldValue(origParamNm, true)).floatValue())*
                       (1.0f-(counter*fadePerc));
         hdlr.setFieldValue(imgFldMap, paramNm, String.valueOf(flVal), false);
         XONUtil.logDebugMesg("Show Color Picker: "+ImageFilter.showColorPickerDialog(imgFltr));
         if (ImageFilter.showColorPickerDialog(imgFltr)) {
            XONUtil.logDebugMesg("Setting Color Param: "+imgFltr+" val: "+m_Color+
                                 " SetColorParam: "+m_SetColorParam+" Clr Strgnth: "+
                                 XONImageUtil.getColorStrength(m_Color));
            if (!m_SetColorParam) { m_SetColorParam = true; hdlr.setColorParam(m_Color); }
            else { 
               m_SetColorParam = false; boolean useInverse = false; 
               Map<String, String> imgFltrValMaps = getFieldParam("UseInverseColor");               
               if (imgFltrValMaps != null) 
                  useInverse = Boolean.valueOf(imgFltrValMaps.get("Value"));
               hdlr.setColorParam(XONImageUtil.getInverseColor(m_Color, useInverse)); 
            }
         }
         XONUtil.debugLog(Log.DEBUG, "ImgFltrParam Vals: "+imgFldMap);
      }      
      return hdlr;
   }
   
   private int[] applyFilter(ImageFilter imgFltr, int[] pixels, int wt, int ht, 
                             int[] inPixels, int[] outPixels)
   {
      float flVal = 0.0f, flValY = 0.0f, flValXY = 0.0f, minVal = 0.0f, margin = 0.0f; 
      int intVal = 0, index = 0; float centX = 0.0F, centY = 0.0f, radX = 0.0f, ang = 0.0f;  
      Map<String, String> imgFltrValMaps = null; CrystallizeFilter crystalFltr = null;
      int[] colOpt = XONPropertyInfo.m_ColorOptions; LightFilter lightFltr = null; 
      ImageFilterHandler hdlr = null; String strVal = null, borFltr = null, borFld = null;
      String[] borFltrs = null, borFlds = null; boolean reFactor = true;
      int avgImgPixel = ((Integer) XONObjectCache.getObjectForKey("AvgImagePixel")).
                        intValue();
      String framesGrpName = XONPropertyInfo.getString(R.string.frame_effects, true);
      XONUtil.logDebugMesg("ImageFilter: "+imgFltr+" frameEfctsGrp: "+framesGrpName);
      if (imgFltr.toString().contains(framesGrpName)) { 
         String fadePercStr = getPresetValue("FadePerc"); float fadePerc = 0.15f;
         if (fadePercStr.length() > 0) fadePerc = Float.valueOf(fadePercStr).floatValue();
         String fltrName = XONImageFilterDef.getFilterName(imgFltr.toString(), 
                                                           framesGrpName);
         String multiFltr = XONPropertyInfo.getString(fltrName+"_Filters");
         XONUtil.logDebugMesg("Fltr Name: "+fltrName+" MultiFltr: "+multiFltr);
         String[] fltrs = multiFltr.split("::");
         ImageFilterController cntrlr = null; m_SetColorParam = false; 
         cntrlr = (ImageFilterController) XONObjectCache.
                                          getObjectForKey("ImageFilterController");         
         for (int i = 0; i < fltrs.length; i++) 
         {
            if (fltrs[i].contains("Enhance")) {
               if (cntrlr.getImageFilterHandler(fltrs[i]) != null) continue;
               hdlr = ImageFilter.createImageFilterHandler(fltrs[i]);
            } else hdlr = populateImageFilterHdlr(fltrs[i], i, fadePerc);
            XONUtil.logDebugMesg("Apply Fltr: "+hdlr.getAction());
            pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
         }
         return pixels;
      }
      switch(imgFltr)
      {
         // Apply Image Enhance Filters
         case AutoFix1Enhance : 
            Vector<Float> settings = XONPropertyInfo.getFixImageSettings(avgImgPixel);
            hdlr = ImageFilter.createImageFilterHandler(ImageFilter.BrightnessEnhance.toString());
            hdlr.setFieldValue("Brightness", 
                               String.valueOf(settings.elementAt(0).floatValue()), false);
            pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
            hdlr = ImageFilter.createImageFilterHandler(ImageFilter.ContrastEnhance.toString());
            hdlr.setFieldValue("Contrast", 
                               String.valueOf(settings.elementAt(1).floatValue()), false);
            pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
            hdlr = ImageFilter.createImageFilterHandler(ImageFilter.BiasEnhance.toString());
            flVal = ((Float)getFieldValue("Bias", false)).floatValue();
            float biasStngs = settings.elementAt(2).floatValue(); reFactor = true;
            if (flVal < 0) { flVal = biasStngs; reFactor = false; }
            hdlr.setFieldValue("Bias", String.valueOf(flVal), reFactor);
            pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
            break;
         case FixMthd2Enhance : case AutoFix2Enhance :
            Vector<Float> gainStngs = XONPropertyInfo.getFixImageGainSettings(avgImgPixel);
            hdlr = ImageFilter.createImageFilterHandler(ImageFilter.GainEnhance.toString());
            XONUtil.logDebugMesg("Fltr Hdlr: "+hdlr+" Settings: "+gainStngs);
            flVal = gainStngs.elementAt(0).floatValue(); XONUtil.logDebugMesg("Val: "+flVal);
            hdlr.setFieldValue("Gain", String.valueOf(flVal), false);
            pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
            hdlr = ImageFilter.createImageFilterHandler(ImageFilter.BrightnessEnhance.toString());
            flVal = ((Float)getFieldValue("Brightness", false)).floatValue();
            float brightStng = gainStngs.elementAt(1).floatValue(); reFactor = true;
            if (flVal < 0) { flVal = brightStng; reFactor = false; }
            XONUtil.logDebugMesg("Brightness Val: "+flVal);
            hdlr.setFieldValue("Brightness", String.valueOf(flVal), reFactor);
            pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
            break;
         case BrightnessEnhance : 
            flVal = ((Float)getFieldValue("Brightness", false)).floatValue();
            XONUtil.logDebugMesg("Brightness Val: "+flVal);
            ContrastFilter briFltr = new ContrastFilter(); briFltr.setBrightness(flVal); 
            pixels = briFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case ContrastEnhance : 
            flVal = ((Float)getFieldValue("Contrast", false)).floatValue();
            ContrastFilter conFltr = new ContrastFilter(); conFltr.setContrast(flVal); 
            pixels = conFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case BiasEnhance : 
            flVal = ((Float)getFieldValue("Bias", false)).floatValue();
            GainFilter biasFltr = new GainFilter(); biasFltr.setBias(flVal); 
            pixels = biasFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case ExposureEnhance : 
            flVal = ((Float)getFieldValue("Exposure", false)).floatValue();
            ExposureFilter expFltr = new ExposureFilter(); expFltr.setExposure(flVal); 
            pixels = expFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case GainEnhance : 
            flVal = ((Float)getFieldValue("Gain", false)).floatValue();
            GainFilter gainFltr = new GainFilter(); gainFltr.setGain(flVal); 
            pixels = gainFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case GlowEnhance : case GlowLightEffects : 
            flVal = ((Float)getFieldValue("Glow", false)).floatValue();
            GlowFilter glowFltr = new GlowFilter();
            glowFltr.setAmount(flVal); 
            pixels = glowFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case HueEnhance : 
            flVal = ((Float)getFieldValue("Hue", false)).floatValue();
            minVal = getFieldValue("Hue", "Min");
            flVal = (minVal+flVal)*100;
            HSBAdjustFilter hueFltr = new HSBAdjustFilter(); hueFltr.setHFactor(flVal); 
            pixels = hueFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case SaturationEnhance : 
            flVal = ((Float)getFieldValue("Saturation", false)).floatValue();
            minVal = getFieldValue("Saturation", "Min");
            flVal = (minVal+flVal);
            HSBAdjustFilter satFltr = new HSBAdjustFilter(); satFltr.setSFactor(flVal); 
            pixels = satFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case ColorBrightEnhance : 
            flVal = ((Float)getFieldValue("ColorBright", false)).floatValue();
            minVal = getFieldValue("ColorBright", "Min");            
            HSBAdjustFilter clrBrightFltr = new HSBAdjustFilter();
            flVal = (minVal+flVal); clrBrightFltr.setBFactor(flVal); 
            pixels = clrBrightFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;            
         
         // Apply Image Color Filters
         case GreyColorFilters : 
            pixels = new GrayscaleFilter().filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case GreyOutColorFilters : 
            pixels = new GrayFilter().filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case InvertColorFilters : 
            pixels = new InvertFilter().filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case TemperatureColorFilters :
            flVal = getProperFldVal("Temperature");
            TemperatureFilter tempFltr = new TemperatureFilter(); 
            tempFltr.setTemperature(flVal); 
            pixels = tempFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case GammaColorFilters  :
            imgFltrValMaps = getFieldParam("Gamma");
            flVal = ((Float)getFieldValue(imgFltrValMaps, "Gamma", false)).floatValue();
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Gamma", 
                                           "Amt", "Y", false)).floatValue();
            flValXY = ((Float)getFieldValue(imgFltrValMaps, "Gamma", 
                                            "Ang", "XY", false)).floatValue();
            GammaFilter gammaFltr = new GammaFilter(flVal, flValY, flValXY);
            pixels = gammaFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case HSBColorFilters :
            imgFltrValMaps = getFieldParam("HSB");
            flVal = ((Float)getFieldValue(imgFltrValMaps, "HSB", false)).floatValue();
            minVal = getFieldValue(imgFltrValMaps, "HSB", "Min", "X"); 
            flVal = (minVal+flVal)*100;
            minVal = getFieldValue(imgFltrValMaps, "HSB", "Min", "Y"); 
            flValY = minVal+((Float)getFieldValue(imgFltrValMaps, "HSB", 
                                                  "Amt", "Y", false)).floatValue();
            minVal = getFieldValue(imgFltrValMaps, "HSB", "Min", "XY"); 
            flValXY = minVal+((Float)getFieldValue(imgFltrValMaps, "HSB", 
                                                   "Ang", "XY", false)).floatValue();
            HSBAdjustFilter hsbFltr = new HSBAdjustFilter(flVal, flValY, flValXY);
            pixels = hsbFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case SwizzleColorFilters : 
            flVal = ((Float)getFieldValue("Swizzle", false)).floatValue();
            String[] matrixOpts = getPresetValue("MatrixOptions").split("#");
            index = Math.round(flVal*matrixOpts.length);  
            if (index >= matrixOpts.length) index = matrixOpts.length-1;
            SwizzleFilter swizzleFilter = new SwizzleFilter(); 
            int[] matrix = swizzleFilter.getMatrix(); 
            String[] mIndex = matrixOpts[index].split(",");
            for (int i = 0; i < mIndex.length; i++) 
               matrix[Integer.valueOf(mIndex[i]).intValue()] = 1;
            pixels = swizzleFilter.filter(pixels, wt, ht, inPixels, outPixels);            
         
         // Apply Funny Image Filters
         case PinchFunnyEffects: 
            imgFltrValMaps = getFieldParam("Pinch");
            centX = ((Float)getFieldValue(imgFltrValMaps, "Pinch", "Center",
                                          "X", false)).floatValue();
            centY = ((Float)getFieldValue(imgFltrValMaps, "Pinch", "Center",
                                          "Y", false)).floatValue();
            radX = ((Float)getFieldValue(imgFltrValMaps, "Pinch", "Rad",
                                         "X", false)).floatValue()*wt;
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Pinch", "Amt",
                                          "Y", false)).floatValue();
            ang = ((Float)getFieldValue(imgFltrValMaps, "Pinch", 
                                        "Ang", "XY", false)).floatValue();            
            PinchFilter pinchFltr = new PinchFilter(ang, centX, centY, radX, flValY); 
            pixels = pinchFltr.filter(pixels, wt, ht, outPixels);         
            break;
         case BulgeFunnyEffects: 
            imgFltrValMaps = getFieldParam("Bulge");
            centX = ((Float)getFieldValue(imgFltrValMaps, "Bulge", "Center",
                                          "X", false)).floatValue();
            centY = ((Float)getFieldValue(imgFltrValMaps, "Bulge", "Center",
                                          "Y", false)).floatValue();
            radX = ((Float)getFieldValue(imgFltrValMaps, "Bulge", "Rad",
                                         "X", false)).floatValue()*wt;
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Bulge", "Amt",
                                          "Y", false)).floatValue();
            ang = ((Float)getFieldValue(imgFltrValMaps, "Bulge", 
                                        "Ang", "XY", false)).floatValue();            
            PinchFilter bulgeFltr = new PinchFilter(ang, centX, centY, radX, -flValY); 
            pixels = bulgeFltr.filter(pixels, wt, ht, outPixels);         
            break;
         case CircleFunnyEffects: 
            CircleFilter cirFltr = new CircleFilter(); 
            imgFltrValMaps = getFieldParam("Circle");
            flVal = ((Float)getFieldValue(imgFltrValMaps, "Circle", "Amt", 
                                          "X", false)).floatValue();
            cirFltr.setSpreadAngle((float)Math.toRadians(flVal));
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Circle", "Amt", 
                                           "Y", false)).floatValue();
            cirFltr.setRadius(0.0f); cirFltr.setAngle(flValY); 
            if (wt > ht) cirFltr.setHeight(Math.round(ht/2));
            else cirFltr.setHeight(Math.round(wt/2));
            pixels = cirFltr.filter(pixels, wt, ht, outPixels);         
            break;
         case SphereFunnyEffects: 
            imgFltrValMaps = getFieldParam("Sphere");
            centX = ((Float)getFieldValue(imgFltrValMaps, "Sphere", "Center",
                                          "X", false)).floatValue();
            centY = ((Float)getFieldValue(imgFltrValMaps, "Sphere", "Center",
                                          "Y", false)).floatValue()/2;
            radX = ((Float)getFieldValue(imgFltrValMaps, "Sphere", "Rad",
                                         "X", false)).floatValue()*wt;
            if (ht > wt) radX = ((Float)getFieldValue(imgFltrValMaps, "Sphere", "Rad",
                                                      "X", false)).floatValue()*ht;
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Sphere", "Amt",
                                          "Y", false)).floatValue()+1;
            flValY = 1.5f; // Fixing the value as the variable is not user friendly
            SphereFilter sphFltr = new SphereFilter(centX, centY, radX, flValY);
            pixels = sphFltr.filter(pixels, wt, ht, outPixels);         
            break;
         case TwirlFunnyEffects: 
            imgFltrValMaps = getFieldParam("Twirl");
            centX = ((Float)getFieldValue(imgFltrValMaps, "Twirl", "Center",
                                          "X", false)).floatValue();
            centY = ((Float)getFieldValue(imgFltrValMaps, "Twirl", "Center",
                                          "Y", false)).floatValue();
            radX = ((Float)getFieldValue(imgFltrValMaps, "Twirl", "Rad",
                                         "X", false)).floatValue()*wt;
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Twirl", "Amt",
                                          "Y", false)).floatValue();
            ang = ((Float)getFieldValue(imgFltrValMaps, "Twirl", 
                                        "Ang", "XY", false)).floatValue();            
            ang = flValY+1.0f;
            XONUtil.logDebugMesg("CentX: "+centX+" centY: "+centY+" radX: "+radX+" ang: "+ang);
            TwirlFilter twirlFltr = new TwirlFilter(ang, centX, centY, radX); 
            pixels = twirlFltr.filter(pixels, wt, ht, outPixels);         
            break;
         
         // Color Effects
         case BWColorEffects :
            intVal = Math.round(((Float)getFieldValue("BW", false)).floatValue());
            ThresholdFilter threshFltr = new ThresholdFilter();
            if (intVal < 127) threshFltr.setLowerThreshold(intVal); 
            else threshFltr.setUpperThreshold(intVal); 
            pixels = threshFltr.filter(pixels, wt, ht, inPixels, outPixels); 
            break;
         case ColorfulColorEffects : 
            pixels = new LookupFilter().filter(pixels, wt, ht);
            break;
         case PosterizeColorEffects :
            intVal = Math.round(((Float)getFieldValue("Posterize", false)).floatValue());
            if (intVal < 2) intVal = 2;
            pixels = new PosterizeFilter(intVal).filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case EmbossColorEffects :
            flVal = ((Float)getFieldValue("Emboss", false)).floatValue();
            EmbossFilter embFltr = new EmbossFilter(); embFltr.setElevation(flVal);
            embFltr.setBumpHeight(Float.valueOf(getPresetValue("BumpHt")).floatValue());
            pixels = embFltr.filter(pixels, wt, ht);
            break;
         case OutlineColorEffects :
            flVal = ((Float)getFieldValue("Outline", false)).floatValue();
            DoGFilter outFltr = new DoGFilter(); outFltr.setRadius2(flVal);
            outFltr.setRadius1(Float.valueOf(getPresetValue("Rad1")).floatValue());
            outFltr.setNormalize(true); outFltr.setInvert(true);
            pixels = outFltr.filter(pixels, wt, ht, outPixels);
            break;
         case SketchColorEffects :
            LightFilter sketchFltr = new LightFilter(); 
            sketchFltr.getLights().removeAllElements();
            LightFilter.DistantLight embLight = sketchFltr.new DistantLight(); 
            sketchFltr.addLight(embLight); 
            sketchFltr.setBumpHeight(Float.valueOf(getPresetValue("BumpHt")).floatValue()); 
            sketchFltr.setBumpSoftness(Float.valueOf(getPresetValue("BumpSoft")).floatValue());
            pixels = sketchFltr.filter(pixels, wt, ht);
            break;
         case ColorSketchColorEffects :
            LightFilter clrSketchFltr = new LightFilter(); 
            clrSketchFltr.getLights().removeAllElements();  
            LightFilter.DistantLight clrSketchLight = clrSketchFltr.new DistantLight(); 
            flVal = ((Float)getFieldValue("ColorSketch", false)).floatValue();
            float elev = Float.valueOf(getPresetValue("Elevation")).floatValue();
            clrSketchLight.setElevation(elev); clrSketchFltr.addLight(clrSketchLight);
            float bumpSft = Float.valueOf(getPresetValue("BumpSoft")).floatValue();
            clrSketchFltr.setBumpHeight(flVal); clrSketchFltr.setBumpSoftness(bumpSft);
            pixels = clrSketchFltr.filter(pixels, wt, ht);
            break;
         case BurstedColorEffects :
            flVal = 3.0f-((Float)getFieldValue("Bursted", false)).floatValue();
            ShapeFilter burstedFilter = new ShapeFilter(); burstedFilter.setMerge(true);
            burstedFilter.setUseAlpha(false); burstedFilter.setType(ShapeFilter.LINEAR);
            burstedFilter.setFactor(flVal); pixels = burstedFilter.filter(pixels, wt, ht);
            break;
         case NegativeColorEffects :
            pixels = applyFilter(ImageFilter.InvertColorFilters, pixels, wt, ht, 
                                 inPixels, outPixels);
            ShapeFilter negFltr = new ShapeFilter(); 
            negFltr.setUseAlpha(false); negFltr.setType(ShapeFilter.LINEAR);
            negFltr.setFactor(0.0f); pixels = negFltr.filter(pixels, wt, ht);
            break;
         case ColorDotsColorEffects : 
            flVal = ((Float)getFieldValue("ColorDots", false)).floatValue();
            crystalFltr = new CrystallizeFilter(); crystalFltr.setEdgeColor(m_Color);
            crystalFltr.setEdgeThickness(flVal); 
//            crystalFltr.setEdgeThickness(getPresetFloatValue("EdgeThickness"));
            crystalFltr.setGridType(Integer.valueOf(getPresetValue("GridType")));
            crystalFltr.setScale(getPresetFloatValue("Scale"));
            crystalFltr.setAmount(getPresetFloatValue("Amount"));
            pixels = crystalFltr.filter(pixels, wt, ht);
            break;
         
         // Light Effects
         case SolarizeLightEffects :
            pixels = new SolarizeFilter().filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case CircleLightEffects :
            imgFltrValMaps = getFieldParam("Circle");
            centX = ((Float)getFieldValue(imgFltrValMaps, "Circle", "Center",
                                          "X", false)).floatValue();
            centY = ((Float)getFieldValue(imgFltrValMaps, "Circle", "Center",
                                          "Y", false)).floatValue();
            radX = ((Float)getFieldValue(imgFltrValMaps, "Circle", "Rad",
                                         "X", false)).floatValue()*wt;
            flValY = ((Float)getFieldValue(imgFltrValMaps, "Circle", "Amt",
                                           "Y", false)).floatValue();
            lightFltr = new LightFilter(); lightFltr.getLights().removeAllElements();
            lightFltr.setBumpSource(LightFilter.BUMPS_FROM_IMAGE_ALPHA);
            LightFilter.SpotLight crclSpotLight = lightFltr.new SpotLight(); 
            lightFltr.addLight(crclSpotLight); crclSpotLight.setElevation(1.0f);
            crclSpotLight.setCentreX(centX); crclSpotLight.setCentreY(centY);
            crclSpotLight.setDistance(radX); crclSpotLight.setFocus(flValY);
            crclSpotLight.setConeAngle(getPresetFloatValue("ConeAngle"));    
            pixels = lightFltr.filter(pixels, wt, ht);
            break;
         case FlareLightEffects :
            imgFltrValMaps = getFieldParam("Flare");
            centX = getFloatFieldValue(imgFltrValMaps, "Flare", "Center", "X", false);
            centY = getFloatFieldValue(imgFltrValMaps, "Flare", "Center", "Y", false);
            radX = getFloatFieldValue(imgFltrValMaps, "Flare", "Rad", "X", false)*wt;
            flValY = getFloatFieldValue(imgFltrValMaps, "Flare", "Amt", "Y", false);
            FlareFilter flFltr = new FlareFilter(); flFltr.setCentre(centX, centY);
            flFltr.setRadius(radX); flFltr.setRayAmount(flValY);
            flFltr.setRingAmount(getPresetFloatValue("RingAmount"));
            flFltr.setBaseAmount(getPresetFloatValue("BaseAmount"));
            flFltr.setRingWidth(getPresetFloatValue("RingWidth"));
            pixels = flFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case RingLightEffects :
            imgFltrValMaps = getFieldParam("Ring");
            centX = getFloatFieldValue(imgFltrValMaps, "Ring", "Center", "X", false);
            centY = getFloatFieldValue(imgFltrValMaps, "Ring", "Center", "Y", false);
            radX = getFloatFieldValue(imgFltrValMaps, "Ring", "Rad", "X", false)*wt/2;
            flValY = getFloatFieldValue(imgFltrValMaps, "Ring", "Amt", "Y", false);
            FlareFilter ringFltr = new FlareFilter(); ringFltr.setCentre(centX, centY);
            ringFltr.setRadius(radX); //ringFltr.setRingAmount(flValY);
            ringFltr.setRingAmount(getPresetFloatValue("RingAmount"));
            ringFltr.setRayAmount(getPresetFloatValue("RayAmount"));
            ringFltr.setBaseAmount(getPresetFloatValue("BaseAmount"));
            ringFltr.setRingWidth(getPresetFloatValue("RingWidth"));
            pixels = ringFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case RaysLightEffects :
            imgFltrValMaps = getFieldParam("Rays");
            centX = getFloatFieldValue(imgFltrValMaps, "Rays", "Center", "X", false);
            centY = getFloatFieldValue(imgFltrValMaps, "Rays", "Center", "Y", false);
            flVal = getFloatFieldValue(imgFltrValMaps, "Rays", "Rad", "X", false);
            flValY = getFloatFieldValue(imgFltrValMaps, "Rays", "Amt", "Y", false);
            FlareFilter raysFltr = new FlareFilter(); raysFltr.setCentre(centX, centY);
            raysFltr.setRadius((float)wt/2.0f); //raysFltr.setRingAmount(flValY);
            raysFltr.setBaseAmount(getPresetFloatValue("BaseAmount"));
            raysFltr.setRingAmount(getPresetFloatValue("RingAmount"));
            raysFltr.setRingWidth(getPresetFloatValue("RingWidth"));
            raysFltr.setRayAmount(flVal); 
            pixels = raysFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case SparkleLightEffects :
            imgFltrValMaps = getFieldParam("Sparkle");
            centX = getFloatFieldValue(imgFltrValMaps, "Sparkle", "Center", "X", false);
            centY = getFloatFieldValue(imgFltrValMaps, "Sparkle", "Center", "Y", false);
            radX = getFloatFieldValue(imgFltrValMaps, "Sparkle", "Rad", "X", false)*wt/2;
            flValY = getFloatFieldValue(imgFltrValMaps, "Sparkle", "Amt", "Y", false);
            SparkleFilter sparkleFltr = new SparkleFilter(); 
            sparkleFltr.setPercCentreX(centX); sparkleFltr.setPercCentreY(centY);
            sparkleFltr.setRadius((int)radX); // sparkleFltr.setRays(Math.round(flValY));
            sparkleFltr.setRandomness((int)getPresetFloatValue("Randomness"));
            pixels = sparkleFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         
            // Apply Image Borders Filters
            case GreyBorderFilters : 
               flVal = ((Float)getFieldValue("Grey", false)).floatValue();
               GrayscaleFilter greyFltr = new GrayscaleFilter(); greyFltr.margin = flVal; 
               pixels = greyFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            case InvertBorderFilters : 
               flVal = ((Float)getFieldValue("Invert", false)).floatValue();
               XONUtil.logDebugMesg("Margin: "+flVal);
               InvertFilter invFltr = new InvertFilter(); invFltr.margin = flVal; 
               pixels = invFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            case ColorfulBorderFilters : 
               flVal = ((Float)getFieldValue("Colorful", false)).floatValue();
               LookupFilter lookupFltr = new LookupFilter(); lookupFltr.margin = flVal; 
               pixels = lookupFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            case PosterizeBorderFilters : 
               imgFltrValMaps = getFieldParam("Posterize");
               flVal = ((Float)getFieldValue(imgFltrValMaps, "Posterize", false)).floatValue();
               flValY = ((Float)getFieldValue(imgFltrValMaps, "Posterize", 
                                              "Amt", "Y", false)).floatValue();
               intVal = Math.round(flValY); if (intVal < 2) intVal = 2;
               intVal = 2; // Setting to 2 as it gives the best border effects
               PosterizeFilter posFltr = new PosterizeFilter(intVal); posFltr.margin = flVal;  
               pixels = posFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            case BurstedBorderFilters :
               flVal = ((Float)getFieldValue("Bursted", false)).floatValue();
               margin = flVal; float burstVal = 3.0f*flVal;
               ShapeFilter burstBrdrFilter = new ShapeFilter(); 
               burstBrdrFilter.setMerge(true); burstBrdrFilter.margin = margin;
               burstBrdrFilter.setUseAlpha(false); burstBrdrFilter.setType(ShapeFilter.LINEAR);
               burstBrdrFilter.setFactor(burstVal); pixels = burstBrdrFilter.filter(pixels, wt, ht);
               break;
            case ColorDotsBorderFilters : 
               imgFltrValMaps = getFieldParam("ColorDots");
               flVal = ((Float)getFieldValue(imgFltrValMaps, "ColorDots", false)).floatValue();
               flValY = ((Float)getFieldValue(imgFltrValMaps, "ColorDots", 
                                              "Amt", "Y", false)).floatValue();
               intVal = Math.round(flValY); if (intVal < 2) intVal = 2;
               intVal = 2; // Setting to 2 as it gives the best border effects
               ColorHalftoneFilter halfToneFltr = new ColorHalftoneFilter(flValY); 
               halfToneFltr.margin = flVal;  
               pixels = halfToneFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            case CrystalBorderFilters : case HexCrystalBorderFilters : 
            case OctaCrystalBorderFilters :
               imgFltrValMaps = getFieldParam("Crystal");
               flVal = ((Float)getFieldValue(imgFltrValMaps, "Crystal", false)).floatValue();
               flValY = ((Float)getFieldValue(imgFltrValMaps, "Crystal", 
                                              "Amt", "Y", false)).floatValue();
               crystalFltr = new CrystallizeFilter(); crystalFltr.margin = flVal;
               crystalFltr.setEdgeThickness(flValY); crystalFltr.setEdgeColor(m_Color);
               crystalFltr.setGridType(Integer.valueOf(getPresetValue("GridType")));
               crystalFltr.setScale(getPresetFloatValue("Scale"));
               crystalFltr.setAmount(getPresetFloatValue("Amount"));
               pixels = crystalFltr.filter(pixels, wt, ht);
               break;
            case ColorCrystalBorderFilters : case HexColorCrystalBorderFilters :
            case OctaColorCrystalBorderFilters :
               imgFltrValMaps = getFieldParam("Crystal");
               flVal = ((Float)getFieldValue(imgFltrValMaps, "Crystal", false)).floatValue();
               flValY = ((Float)getFieldValue(imgFltrValMaps, "Crystal", 
                                              "Amt", "Y", false)).floatValue();
               crystalFltr = new CrystallizeFilter(); crystalFltr.margin = flVal;
               crystalFltr.setEdgeColor(colOpt[Math.round(flValY*colOpt.length)]);
               crystalFltr.setEdgeThickness(getPresetFloatValue("EdgeThickness"));
               crystalFltr.setGridType(Integer.valueOf(getPresetValue("GridType")));
               crystalFltr.setScale(getPresetFloatValue("Scale"));
               crystalFltr.setAmount(getPresetFloatValue("Amount"));
               pixels = crystalFltr.filter(pixels, wt, ht);
               break;
            case ColorFadeBorderFilters :
               flVal = ((Float)getFieldValue("ColorFade", false)).floatValue();
               FadeFilter clrFadeFltr = new FadeFilter(flVal); 
               clrFadeFltr.setColor(m_Color); 
               clrFadeFltr.setFadeType(getPresetValue("FadeType")); 
               clrFadeFltr.setMergeType(getPresetValue("MergeType")); 
               clrFadeFltr.setShapeType(getPresetValue("ShapeType"));             
               pixels = clrFadeFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            case NoiseBorderFilters :
               flVal = ((Float)getFieldValue("Noise", false)).floatValue();
               NoiseFilter noiseFltr = new NoiseFilter(getPresetIntValue("Amount"));
               noiseFltr.margin = flVal; 
               pixels = noiseFltr.filter(pixels, wt, ht, inPixels, outPixels);
               break;
            
         // Distort Filters
         case ColorDotsDistortFilters :
            flVal = ((Float)getFieldValue("ColorDots", false)).floatValue();
            if (flVal == 0.0f) flVal = 1.0f;
            pixels = (new ColorHalftoneFilter(flVal)).filter(pixels, wt, ht, 
                                                             inPixels, outPixels);
            break;
         case MosaicDistortFilters :
            flVal = ((Float)getFieldValue("Mosaic", false)).floatValue();
            if (flVal == 0.0f) flVal = 1.0f;
            pixels = (new BlockFilter(Math.round(flVal))).filter(pixels, wt, ht, outPixels);
            break;
         case FlipDistortFilters :
            flVal = ((Float)getFieldValue("Flip", false)).floatValue();
            if (flVal == 0.0f) flVal = 1.0f;
            pixels = (new FlipFilter(Math.round(flVal))).filter(pixels, wt, ht, outPixels);
            break;
         case RandomShapeDistortFilters : case HexShapeDistortFilters : 
         case OctShapeDistortFilters :
            flVal = ((Float)getFieldValue("Crystal", false)).floatValue();
            crystalFltr = new CrystallizeFilter();
            crystalFltr.setScale(flVal);
            crystalFltr.setEdgeColor(Color.parseColor(getPresetValue("EdgeColor")));
            crystalFltr.setGridType(Integer.valueOf(getPresetValue("GridType")));
            crystalFltr.setEdgeThickness(getPresetFloatValue("EdgeThickness"));
            crystalFltr.setFadeEdges(getPresetBoolValue("FadeEdges"));
            crystalFltr.setAmount(getPresetFloatValue("Amount"));
            pixels = crystalFltr.filter(pixels, wt, ht);
            break;
         
         // Blur Filters
         case NoiseBlurFilters :
            flVal = ((Float)getFieldValue("Noise", false)).floatValue();
            pixels = (new NoiseFilter(Math.round(flVal))).filter(pixels, wt, ht, 
                                                                 inPixels, outPixels);
            break;
         case GaussBlurFilters :
            flVal = ((Float)getFieldValue("Gauss", false)).floatValue();
            pixels = (new GaussianFilter(Math.round(flVal))).filter(pixels, wt, ht, 
                                                                    inPixels, outPixels);
            break;
         case OilBlurFilters :
            flVal = ((Float)getFieldValue("Oil", false)).floatValue();
            intVal = getPresetIntValue("Level");
            pixels = (new OilFilter(Math.round(flVal), intVal)).filter(pixels, wt, ht);
            break;
         case SimpleBlurFilters :
            intVal = getPresetIntValue("Simple");
            for(index = 0; index < intVal; index++) 
               pixels = (new BlurFilter()).filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case BumpBlurFilters :
            intVal = getPresetIntValue("Bump");
            for(index = 0; index < intVal; index++) 
               pixels = (new BumpFilter()).filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case SharpenBlurFilters :
            intVal = getPresetIntValue("Sharpen");
            for(index = 0; index < intVal; index++) 
               pixels = (new SharpenFilter()).filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case MaxPixelBlurFilters :
            intVal = getPresetIntValue("MaxPixel");
            for(index = 0; index < intVal; index++) 
               pixels = (new MaximumFilter()).filter(pixels, wt, ht);
            break;
         case CrystalBlurFilters : case HexCrystalBlurFilters : case OctCrystalBlurFilters :
            flVal = ((Float)getFieldValue("Crystal", false)).floatValue();
            crystalFltr = new CrystallizeFilter();
            crystalFltr.setEdgeThickness(flVal); crystalFltr.setEdgeColor(m_Color);
            crystalFltr.setGridType(Integer.valueOf(getPresetValue("GridType")));
            crystalFltr.setScale(getPresetFloatValue("Scale"));
            crystalFltr.setFadeEdges(getPresetBoolValue("FadeEdges"));
            crystalFltr.setAmount(getPresetFloatValue("Amount"));
            pixels = crystalFltr.filter(pixels, wt, ht);
            break;
         case ColorCrystalBlurFilters : case HexColorCrystalBlurFilters :
         case OctColorCrystalBlurFilters :  
            flVal = ((Float)getFieldValue("Crystal", false)).floatValue(); 
            crystalFltr = new CrystallizeFilter();
            crystalFltr.setEdgeColor(colOpt[Math.round(flVal*colOpt.length)]);
            crystalFltr.setEdgeThickness(getPresetFloatValue("EdgeThickness"));
            crystalFltr.setGridType(Integer.valueOf(getPresetValue("GridType")));
            crystalFltr.setScale(getPresetFloatValue("Scale"));
            crystalFltr.setAmount(getPresetFloatValue("Amount"));
            pixels = crystalFltr.filter(pixels, wt, ht);
            break;
            
         // Fade Effects
         case UniformFadeEffects :
            flVal = ((Float)getFieldValue("Uniform", false)).floatValue();
            FadeFilter fadeFltr = new FadeFilter(flVal); 
            fadeFltr.setFadeType(getPresetValue("FadeType")); 
            fadeFltr.setMergeType(getPresetValue("MergeType")); 
            fadeFltr.setShapeType(getPresetValue("ShapeType")); 
            pixels = fadeFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case LinearFadeEffects :
            flVal = ((Float)getFieldValue("Linear", false)).floatValue();
            LinearFadeFilter.FadeType fadeTypes[] = LinearFadeFilter.FadeType.values();
//            index = Math.round(fadeTypes.length*flVal); 
            index = Math.round(flVal)%3; float fadePerc = getPresetFloatValue("FadePerc");
            LinearFadeFilter linearFadeFltr = new LinearFadeFilter(fadePerc, 
                                                                   fadeTypes[index]); 
            pixels = linearFadeFltr.filter(pixels, wt, ht, inPixels, outPixels);
            break;
         case BarFadeEffects :
            flVal = ((Float)getFieldValue("BorderMargin", false)).floatValue();
            BarFadeFilter barFadeFltr = new BarFadeFilter(); 
            barFadeFltr.setAngle(getPresetFloatValue("Angle")); 
            barFadeFltr.setSides(getPresetIntValue("Sides"));             
            barFadeFltr.setFadeWidth(getPresetFloatValue("FadeWidth"));             
            barFadeFltr.setFadeStart(getPresetFloatValue("FadeStart"));             
            pixels = barFadeFltr.filter(pixels, wt, ht, inPixels, outPixels);
            pixels = applyUniformFadeFilter(pixels, wt, ht, flVal, inPixels, outPixels);            
            break;
         case InvertFadeEffects : case ColorFadeFadeEffects : case ColorfulFadeEffects :
         case ColorDotsFadeEffects : case CrystalFadeEffects : case ColorCrystalFadeEffects : 
         case NoiseFadeEffects : 
            flVal = ((Float)getFieldValue("BorderMargin", false)).floatValue();
            strVal = Float.valueOf(flVal).toString(); 
            XONUtil.logDebugMesg("Fltrs to be used: "+getPresetValue("BorderFilter"));
            borFltrs = getPresetValue("BorderFilter").split(":N:"); 
            borFlds = getPresetValue("BorderField").split(":N:");
            XONUtil.logDebugMesg("ImgFltr is: "+imgFltr+" Fltrs to be used: "+
                                  getPresetValue("BorderFilter")+
                                 " Num Of Fltrs: "+borFltrs.length);
            for (index = 0; index < borFltrs.length; index++) {
               borFltr = borFltrs[index]; borFld = borFlds[index];
               XONUtil.logDebugMesg("Fltr Applied: "+borFltr+" Fld: "+borFld);
               hdlr = ImageFilter.createImageFilterHandler(borFltr);
               if (borFltr.equals(ImageFilter.ColorFadeBorderFilters.toString())) 
                  hdlr.setColorParam(m_Color); 
               hdlr.setFieldValue(borFld, strVal, true);
               pixels = hdlr.applyFilter(pixels, wt, ht, inPixels, outPixels);
            }
            pixels = applyUniformFadeFilter(pixels, wt, ht, flVal, inPixels, outPixels);            
            break;
         default: break;
      }
      return pixels;
   }
   
   public String toString()
   {
      StringBuffer mesg = new StringBuffer("Image Filter Hdlr for "+m_ImageFilter);
      mesg.append(" Status: "+m_ImageFilterStatus);
//      mesg.append("\nImage Filter Params: "+m_FilterParamValues);
      return mesg.toString();
   }
   
}
