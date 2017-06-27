package com.xpresson.android.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseIntArray;

import com.xpresson.android.R;

public class XONImageFilterDef
{
   public static Map<String, Vector<String>> m_ImageFilterRules;
   public static Map<String, Vector<String>> m_ImageFilterResources;
   
   public static SparseIntArray m_ImageFilterResId, m_ImageFilterDispResId;
   static {
      m_ImageFilterResources = new HashMap<String, Vector<String>>();
      // This map specifies the Image Filter Resource
      m_ImageFilterResId = new SparseIntArray();
      m_ImageFilterResId.put(R.string.image_enhance, R.array.ImageEnhance);
      m_ImageFilterResId.put(R.string.frame_effects, R.array.Frames);
      m_ImageFilterResId.put(R.string.adv_effects, R.array.Advanced);
      m_ImageFilterResId.put(R.string.fade_effects, R.array.FadeEffects);
      m_ImageFilterResId.put(R.string.color_effects, R.array.ColorEffects);
      m_ImageFilterResId.put(R.string.funny_effects, R.array.FunnyEffects);
      m_ImageFilterResId.put(R.string.light_effects, R.array.LightEffects);
      m_ImageFilterResId.put(R.string.border_filters, R.array.BorderFilters);
      m_ImageFilterResId.put(R.string.color_filters, R.array.ColorFilters);
      m_ImageFilterResId.put(R.string.distort_filters, R.array.DistortFilters);
      m_ImageFilterResId.put(R.string.blur_filters, R.array.BlurFilters);
      // This map specifies the Image Filter Resource Name to be Displayed
      m_ImageFilterDispResId = new SparseIntArray();
      m_ImageFilterDispResId.put(R.string.image_enhance, R.array.ImageEnhanceDispName);
      m_ImageFilterDispResId.put(R.string.frame_effects, R.array.FramesDispName);
      m_ImageFilterDispResId.put(R.string.adv_effects, R.array.AdvancedDispName);
      m_ImageFilterDispResId.put(R.string.fade_effects, R.array.FadeEffectsDispName);
      m_ImageFilterDispResId.put(R.string.color_effects, R.array.ColorEffectsDispName);
      m_ImageFilterDispResId.put(R.string.funny_effects, R.array.FunnyEffectsDispName);
      m_ImageFilterDispResId.put(R.string.light_effects, R.array.LightEffectsDispName);
      m_ImageFilterDispResId.put(R.string.border_filters, R.array.BorderFiltersDispName);
      m_ImageFilterDispResId.put(R.string.color_filters, R.array.ColorFiltersDispName);
      m_ImageFilterDispResId.put(R.string.distort_filters, R.array.DistortFiltersDispName);
      m_ImageFilterDispResId.put(R.string.blur_filters, R.array.BlurFiltersDispName);
   }
   
   public static enum ImageFilter 
   {   
      
      // Template Image Filter - This is either user defined or system defined
      PopularTemplateFilter, PopularTemplateFilter_On, PopularTemplateFilter_Off,
      AdvTemplateFilter, AdvTemplateFilter_On, AdvTemplateFilter_Off,
      PersonalizedTemplateFilter, PersonalizedTemplateFilter_On, PersonalizedTemplateFilter_Off,
      
      // Frame Effects
      ColorFadeFrames, ColorFadeFrames_On, ColorFadeFrames_Off,
      SolidFrames, SolidFrames_On, SolidFrames_Off,
      MistyFrames, MistyFrames_On, MistyFrames_Off,
      CrystalFrames, CrystalFrames_On, CrystalFrames_Off,
      ShieldFrames, ShieldFrames_On, ShieldFrames_Off,
      ColorfulFrames, ColorfulFrames_On, ColorfulFrames_Off,
      ColorDotsFrames, ColorDotsFrames_On, ColorDotsFrames_Off,
      MixMatchFrames, MixMatchFrames_On, MixMatchFrames_Off,
      InverseFrames, InverseFrames_On, InverseFrames_Off,
      NoisyFrames, NoisyFrames_On, NoisyFrames_Off,
      MultiFrames, MultiFrames_On, MultiFrames_Off,
      
      
      // Enhance Filters
      AutoFix1Enhance, AutoFix1Enhance_On, AutoFix1Enhance_Off,
      AutoFix2Enhance, AutoFix2Enhance_On, AutoFix2Enhance_Off,
      FixMthd2Enhance, FixMthd2Enhance_On, FixMthd2Enhance_Off,
      ContrastEnhance, ContrastEnhance_On, ContrastEnhance_Off, 
      BrightnessEnhance, BrightnessEnhance_On, BrightnessEnhance_Off,
      ExposureEnhance, ExposureEnhance_On, ExposureEnhance_Off,
      GainEnhance, GainEnhance_On, GainEnhance_Off,
      GlowEnhance, GlowEnhance_On, GlowEnhance_Off,
      HueEnhance, HueEnhance_On, HueEnhance_Off,
      SaturationEnhance, SaturationEnhance_On, SaturationEnhance_Off,
      ColorBrightEnhance, ColorBrightEnhance_On, ColorBrightEnhance_Off,
      BiasEnhance, BiasEnhance_On, BiasEnhance_Off,
      
      // Fade Effects
      UniformFadeEffects, UniformFadeEffects_On, UniformFadeEffects_Off,
      LinearFadeEffects, LinearFadeEffects_On, LinearFadeEffects_Off,
      InvertFadeEffects, InvertFadeEffects_On, InvertFadeEffects_Off, 
      BarFadeEffects, BarFadeEffects_On, BarFadeEffects_Off,
      NoiseFadeEffects, NoiseFadeEffects_On, NoiseFadeEffects_Off,
      ColorfulFadeEffects, ColorfulFadeEffects_On, ColorfulFadeEffects_Off,
      ColorDotsFadeEffects, ColorDotsFadeEffects_On, ColorDotsFadeEffects_Off,
      CrystalFadeEffects, CrystalFadeEffects_On, CrystalFadeEffects_Off,
      ColorCrystalFadeEffects, ColorCrystalFadeEffects_On, ColorCrystalFadeEffects_Off,
      ColorFadeFadeEffects, ColorFadeFadeEffects_On, ColorFadeFadeEffects_Off,
      
      // Color Effects
      BWColorEffects, BWColorEffects_On, BWColorEffects_Off,
      ColorfulColorEffects, ColorfulColorEffects_On, ColorfulColorEffects_Off,
      PosterizeColorEffects, PosterizeColorEffects_On, PosterizeColorEffects_Off,
      EmbossColorEffects, EmbossColorEffects_On, EmbossColorEffects_Off,
      SketchColorEffects, SketchColorEffects_On, SketchColorEffects_Off,
      OutlineColorEffects, OutlineColorEffects_On, OutlineColorEffects_Off,
      ColorSketchColorEffects, ColorSketchColorEffects_On, ColorSketchColorEffects_Off,
      BurstedColorEffects, BurstedColorEffects_On, BurstedColorEffects_Off,
      NegativeColorEffects, NegativeColorEffects_On, NegativeColorEffects_Off,
      ColorDotsColorEffects, ColorDotsColorEffects_On, ColorDotsColorEffects_Off,
      
      // Funny Effects
      PinchFunnyEffects, PinchFunnyEffects_On, PinchFunnyEffects_Off,
      BulgeFunnyEffects, BulgeFunnyEffects_On, BulgeFunnyEffects_Off,
      CircleFunnyEffects, CircleFunnyEffects_On, CircleFunnyEffects_Off,
      SphereFunnyEffects, SphereFunnyEffects_On, SphereFunnyEffects_Off,
      TwirlFunnyEffects, TwirlFunnyEffects_On, TwirlFunnyEffects_Off,
      
      // Light Effects
      SolarizeLightEffects, SolarizeLightEffects_On, SolarizeLightEffects_Off,
      CircleLightEffects, CircleLightEffects_On, CircleLightEffects_Off, 
      FlareLightEffects, FlareLightEffects_On, FlareLightEffects_Off, 
      RingLightEffects, RingLightEffects_On, RingLightEffects_Off, 
      RaysLightEffects, RaysLightEffects_On, RaysLightEffects_Off, 
      GlowLightEffects, GlowLightEffects_On, GlowLightEffects_Off,
      SparkleLightEffects, SparkleLightEffects_On, SparkleLightEffects_Off, 
      
      // Border Filters
      ColorFadeBorderFilters, ColorFadeBorderFilters_On, ColorFadeBorderFilters_Off,
      GreyBorderFilters, GreyBorderFilters_On, GreyBorderFilters_Off, 
      InvertBorderFilters, InvertBorderFilters_On, InvertBorderFilters_Off, 
      ColorfulBorderFilters, ColorfulBorderFilters_On, ColorfulBorderFilters_Off,
      PosterizeBorderFilters, PosterizeBorderFilters_On, PosterizeBorderFilters_Off,
      BurstedBorderFilters, BurstedBorderFilters_On, BurstedBorderFilters_Off,
      NoiseBorderFilters, NoiseBorderFilters_On, NoiseBorderFilters_Off,
      ColorDotsBorderFilters, ColorDotsBorderFilters_On, ColorDotsBorderFilters_Off,
      CrystalBorderFilters, CrystalBorderFilters_On, CrystalBorderFilters_Off,
      HexCrystalBorderFilters, HexCrystalBorderFilters_On, HexCrystalBorderFilters_Off,
      OctaCrystalBorderFilters, OctaCrystalBorderFilters_On, OctaCrystalBorderFilters_Off,
      ColorCrystalBorderFilters, ColorCrystalBorderFilters_On, ColorCrystalBorderFilters_Off,
      HexColorCrystalBorderFilters, HexColorCrystalBorderFilters_On, HexColorCrystalBorderFilters_Off,
      OctaColorCrystalBorderFilters, OctaColorCrystalBorderFilters_On, OctaColorCrystalBorderFilters_Off,
      
      // Color Image Filters
      InvertColorFilters, InvertColorFilters_On, InvertColorFilters_Off,
      TemperatureColorFilters, TemperatureColorFilters_On, TemperatureColorFilters_Off,
      GammaColorFilters, GammaColorFilters_On, GammaColorFilters_Off,
      HSBColorFilters, HSBColorFilters_On, HSBColorFilters_Off,
      SwizzleColorFilters, SwizzleColorFilters_On, SwizzleColorFilters_Off, 
      GreyOutColorFilters, GreyOutColorFilters_On, GreyOutColorFilters_Off,
      GreyColorFilters, GreyColorFilters_On, GreyColorFilters_Off,
      
      // Distort Filters
      FlipDistortFilters, FlipDistortFilters_On, FlipDistortFilters_Off,
      MosaicDistortFilters, MosaicDistortFilters_On, MosaicDistortFilters_Off,
      RandomShapeDistortFilters, RandomShapeDistortFilters_On, RandomShapeDistortFilters_Off,
      HexShapeDistortFilters, HexShapeDistortFilters_On, HexShapeDistortFilters_Off,
      OctShapeDistortFilters, OctShapeDistortFilters_On, OctShapeDistortFilters_Off,
      ColorDotsDistortFilters, ColorDotsDistortFilters_On, ColorDotsDistortFilters_Off,
      
      // Blur Filters
      BumpBlurFilters, BumpBlurFilters_On, BumpBlurFilters_Off,
      OilBlurFilters, OilBlurFilters_On, OilBlurFilters_Off,
      GaussBlurFilters, GaussBlurFilters_On, GaussBlurFilters_Off,
      NoiseBlurFilters, NoiseBlurFilters_On, NoiseBlurFilters_Off,
      SharpenBlurFilters, SharpenBlurFilters_On, SharpenBlurFilters_Off,
      SimpleBlurFilters, SimpleBlurFilters_On, SimpleBlurFilters_Off,
      MaxPixelBlurFilters, MaxPixelBlurFilters_On, MaxPixelBlurFilters_Off,
      CrystalBlurFilters, CrystalBlurFilters_On, CrystalBlurFilters_Off,
      HexCrystalBlurFilters, HexCrystalBlurFilters_On, HexCrystalBlurFilters_Off,
      OctCrystalBlurFilters, OctCrystalBlurFilters_On, OctCrystalBlurFilters_Off,
      ColorCrystalBlurFilters, ColorCrystalBlurFilters_On, ColorCrystalBlurFilters_Off,
      HexColorCrystalBlurFilters, HexColorCrystalBlurFilters_On, HexColorCrystalBlurFilters_Off,
      OctColorCrystalBlurFilters, OctColorCrystalBlurFilters_On, OctColorCrystalBlurFilters_Off;
            
      // Based on the Image Filter, this method creates and returns the Image Filter Handler
      static ImageFilterHandler createImageFilterHandler(String imgFltr)
      {
         XONUtil.logDebugMesg("Creating Image Fltr Hdlr for: "+imgFltr);
         if (!imgFltr.contains("TemplateFilter"))
            return new ImageFilterHandlerImpl(imgFltr);
         return TemplateFilterHolder.getTemplateFilterImpl(imgFltr);
      }
      
      static Vector<String> getImageFilterList()
      {
         String[] imgFltrsArr = XONUtil.objToStringArray(ImageFilter.values(), "_");
         Vector<String> imageFltrLists = XONUtil.createVector(imgFltrsArr);
         return imageFltrLists;
      }
      
      static boolean showColorPickerDialog(String imgFltr)
      {
         if (imgFltr.equals(ImageFilter.ColorFadeBorderFilters.toString()) ||
             imgFltr.equals(ImageFilter.CrystalBorderFilters.toString()) ||
             imgFltr.equals(ImageFilter.HexCrystalBorderFilters.toString()) ||
             imgFltr.equals(ImageFilter.OctaCrystalBorderFilters.toString()) || 
             imgFltr.equals(ImageFilter.CrystalBlurFilters.toString()) || 
             imgFltr.equals(ImageFilter.HexCrystalBlurFilters.toString()) ||  
             imgFltr.equals(ImageFilter.OctCrystalBlurFilters.toString()) || 
             imgFltr.equals(ImageFilter.ColorDotsColorEffects.toString()) ||
             imgFltr.equals(ImageFilter.ColorFadeFadeEffects.toString()) ||
             imgFltr.equals(ImageFilter.ColorCrystalFadeEffects.toString())) 
         {
            XONUtil.logDebugMesg("Show Dialog for: "+imgFltr);
            return true;
         }
         String framesGrpName = XONPropertyInfo.getString(R.string.frame_effects, true);
         if (imgFltr.toString().contains(framesGrpName)) return true;
         return false;
      }
   }
   
   private static Vector<String> m_ImageFilterList;
   public static Vector<String> getImageFilterList()
   {
      if (m_ImageFilterList == null) m_ImageFilterList = ImageFilter.getImageFilterList();
      return m_ImageFilterList;
   }
   
   public static boolean containsImageFilter(String imgFltr)
   {
      if (m_ImageFilterList == null) getImageFilterList();
      return m_ImageFilterList.contains(imgFltr);
   }
   
   public static String getFilterName(String imgFltr, String imgFltrGrp)
   {
      if (!imgFltr.contains(imgFltrGrp)) return null;
      int index = imgFltr.lastIndexOf(imgFltrGrp);
      return imgFltr.substring(0, index);
   }
   
   public static String getImageFilter(ImageFilter templImageFltr, String imgFltrNm)
   {
      return templImageFltr.toString()+"::"+imgFltrNm;
   }
   
   public static String getImageFilter(int mainMenuFltrRes, String imgFltrGrp, 
                                       String imgFltrNm)
   {
      if (mainMenuFltrRes == R.id.quick_effects_btn) {
         if (imgFltrGrp.equals(XONUtil.replaceSpace(
             XONPropertyInfo.getString(R.string.popular_effects), "")))
            return getImageFilter(ImageFilter.PopularTemplateFilter, imgFltrNm);
//         if (imgFltrGrp.equals(XONUtil.replaceSpace(
//               XONPropertyInfo.getString(R.string.Frame_effects), "")))
//            return getImageFilter(ImageFilter.FrameTemplateFilter, imgFltrNm);
         if (imgFltrGrp.equals(XONUtil.replaceSpace(
                               XONPropertyInfo.getString(R.string.user_effects), "")))
            return getImageFilter(ImageFilter.PersonalizedTemplateFilter, imgFltrNm);
      }
      String imgFltr = XONUtil.replaceSpace(imgFltrNm, "")+
                       XONUtil.replaceSpace(imgFltrGrp, "");
      if (!containsImageFilter(imgFltr)) return null;
      return imgFltr;
   }
   
   public static void populateImageFilterResource(Activity act)
   {
      int keyResId, valResId; String imgFltrGrp = ""; String[] imgFltrs;
      String packName = act.getPackageName(); Resources res = act.getResources();
      m_ImageFilterRules = new HashMap<String, Vector<String>>(); populateFilterRules(res);
      for(int index = 0; index < m_ImageFilterResId.size(); index++)
      {
         keyResId = m_ImageFilterResId.keyAt(index);  
         valResId = m_ImageFilterResId.valueAt(index);  
         imgFltrGrp = res.getString(keyResId); imgFltrs = res.getStringArray(valResId);         
         for (int iter = 0; iter < imgFltrs.length; iter++)
         {
            String imgFltrOpt = getImageFilter(-1, imgFltrGrp, imgFltrs[iter]);
            if (imgFltrOpt == null) {
               XONUtil.debugLog(Log.DEBUG, "Image Filter not defined for: "+imgFltrs[iter]);
               continue;
            }
//            XONUtil.debugLog(Log.DEBUG, "Image Filter Values defined for: "+imgFltrOpt);
            String imgFltr = imgFltrOpt.toString();
            int imgFltrResid = res.getIdentifier(imgFltr+"_Val", "array", packName);
            String[] imgFltrSetVals = res.getStringArray(imgFltrResid);
            Vector<String> imgFltrSetList = new Vector<String>(Arrays.asList(imgFltrSetVals)); 
            m_ImageFilterResources.put(imgFltr, imgFltrSetList);
         }
      }
   }
   
   private static void populateFilterRules(Resources res)
   {
      String[] imgFltrRules = res.getStringArray(R.array.ImageFilterRules);
      for (int i = 0; i < imgFltrRules.length; i++)
      {
         String[] imgFltrRule = imgFltrRules[i].split("::");
         String usedFltr = (imgFltrRule[0].split(":="))[1];
         String negateFltr = (imgFltrRule[1].split(":="))[1];
         String[] negateFltrArr = negateFltr.split(":N:");
         Vector<String> negateFltrs = new Vector<String>();
         for (int iter = 0; iter < negateFltrArr.length; iter++) 
            negateFltrs.add(negateFltrArr[iter]);
         m_ImageFilterRules.put(usedFltr, negateFltrs);
      }
      XONUtil.logDebugMesg("ImageFilterRules: "+m_ImageFilterRules);
   }
   
   public static Vector<Map<String, String>> getImageFilterRes(String imgFltr)
   {
      
      Vector<String> imgFltrSetList = m_ImageFilterResources.get(imgFltr);
      if (imgFltrSetList == null) {
         XONUtil.debugLog(Log.ERROR, "ImageFilter Values not defn for: "+imgFltr);
      }
      Vector<Map<String, String>> imgFltrValMapList = new Vector<Map<String, String>>();
      for (int iter = 0; iter < imgFltrSetList.size(); iter++)
      {
         String imgFltrValStr = imgFltrSetList.elementAt(iter);
         if (!imgFltrValStr.contains("::")) continue;
         String[] imgFltrValues = imgFltrValStr.split("::");
         Map<String, String> imgFltrValMaps = new HashMap<String, String>();
//         Map<String, String> imgFltrValMaps = initImageFilterVals(imgFltrValues);
         for (int i = 0; i < imgFltrValues.length; i++)
         {
            if (!imgFltrValues[i].contains(":=")) continue;
            String[] imgFltrKeyVal = imgFltrValues[i].split(":=");
            imgFltrValMaps.put(imgFltrKeyVal[0], imgFltrKeyVal[1]);
         }
         imgFltrValMapList.addElement(imgFltrValMaps);
      }
      return imgFltrValMapList;
   }
   
//   private static Map<String, String> initImageFilterVals(String[] imgFltrValues)
//   {
//      Map<String, String> imgFltrValMaps = new HashMap<String, String>();
//      for (int i = 0; i < imgFltrValues.length; i++)
//      {
//         if (!imgFltrValues[i].contains(":=")) continue;
//         String[] imgFltrKeyVal = imgFltrValues[i].split(":=");
//         if (!imgFltrKeyVal[0].equals("Type")) continue;
//         if (imgFltrKeyVal[1].equals("PresetValue")) return imgFltrValMaps;
//         String imgFltrDefValStr = XONPropertyInfo.getString(R.string.def_filter_vals);
//         String[] imgFltrDefValues = imgFltrDefValStr.split("::");
//         for (int j = 0; j < imgFltrDefValues.length; j++)
//         {
//            if (!imgFltrDefValues[j].contains(":=")) continue;
//            String[] imgFltrDefKeyVal = imgFltrDefValues[j].split(":=");
//            imgFltrValMaps.put(imgFltrDefKeyVal[0], imgFltrDefKeyVal[1]);
//         }
//         return imgFltrValMaps;
//      }
//      return imgFltrValMaps;
//   }
   
}
