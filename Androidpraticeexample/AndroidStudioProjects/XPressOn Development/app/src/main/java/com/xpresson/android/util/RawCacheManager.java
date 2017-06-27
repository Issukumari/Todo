package com.xpresson.android.util;

import java.lang.reflect.Field;

import com.xpresson.android.R;
import com.xpresson.android.util.XONImageFilterDef.ImageFilter;

public class RawCacheManager
{
   public static String IMAGE_FILTER_FILES = "filter";
   public static String SHAPE_FILES = "shapes";

   private static RawCacheManager m_RawCacheManager;
   
   
   private RawCacheManager()
   {
      buildRawCache();
   }
   
   private void buildRawCache()
   {
      StringBuffer rawFileNames = new StringBuffer("\nPopular Image Filter Files are: ");
      Field[] fields = R.raw.class.getFields();
      TemplateFilterHolder templFltrHldr = null;
      templFltrHldr = TemplateFilterHolder.getInstance(ImageFilter.PopularTemplateFilter);      
      for(int count=0; count < fields.length; count++){
         try {
            String name = fields[count].getName(); int resId = fields[count].getInt(null);
            XONUtil.logDebugMesg("Raw Asset Name: "+name+" Value: "+resId);
            if (name.contains(IMAGE_FILTER_FILES)) {
               int posId = templFltrHldr.buildTemplateFilters(name, resId);
               rawFileNames.append(name+":"+posId+" ");
            }
         } catch (Exception ex) {}
      }      
      XONUtil.logDebugMesg(rawFileNames.toString());
   }
   
   public static synchronized RawCacheManager getInstance()
   {
      if (m_RawCacheManager == null) m_RawCacheManager = new RawCacheManager();
      return m_RawCacheManager;
   }
}
