package com.xpresson.android.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.xpresson.android.R;
import com.xpresson.android.util.XONImageFilterDef.ImageFilter;
import android.util.SparseArray;

public class TemplateFilterHolder
{
   public ImageFilter m_TemplateImageFilter;
   private int m_TemplateFilterCounter = 0;   
   private static Map<ImageFilter, TemplateFilterHolder> m_TemplateFilterHolder;
   private Vector<Integer> m_TemplateFilterSortedPos;
   private SparseArray<String> m_TemplateFiltersPos;
   private Map<String, TemplateFilterHandlerImpl> m_TemplateFilters;
   private Map<String, String> m_PopularFilterNameMappings;
   private Map<String, Integer> m_PopularFilterNamePos;
   
   static {
      m_TemplateFilterHolder = new HashMap<ImageFilter, TemplateFilterHolder>();      
   }
   
   private TemplateFilterHolder(ImageFilter type) 
   { 
      m_TemplateFilterCounter = 0; m_TemplateImageFilter = type;
      m_TemplateFiltersPos = new SparseArray<String>(); 
      m_TemplateFilters =new HashMap<String, TemplateFilterHandlerImpl>(); 
   }
   
   public static TemplateFilterHolder getInstance(ImageFilter imgFltr)
   {
      if (m_TemplateFilterHolder.get(imgFltr) == null)
         m_TemplateFilterHolder.put(imgFltr, new TemplateFilterHolder(imgFltr));
      return m_TemplateFilterHolder.get(imgFltr);
   }
   
   private void buildPopularFilterNameSettings()
   {
      try {
         m_PopularFilterNameMappings = new HashMap<String, String>();
         String[] popularFltrMappings = XONPropertyInfo.
                                        getStringArray(R.array.PopularFilterNameMappings);
         for (int i = 0; i < popularFltrMappings.length; i++)
         {
            XONUtil.logDebugMesg("Mappings: "+popularFltrMappings[i]);
            String[] popularFltrMaps = popularFltrMappings[i].split("::");
            if (popularFltrMaps.length != 2) continue;
            m_PopularFilterNameMappings.put(popularFltrMaps[0], popularFltrMaps[1]);
         }
         XONUtil.logDebugMesg("PopularFilterNameMappings: "+m_PopularFilterNameMappings);
         
         String[] popularFltrPos = XONPropertyInfo.
                                   getStringArray(R.array.PopularFilterPositions);
         m_PopularFilterNamePos = new HashMap<String, Integer>();
         for (int i = 0; i < popularFltrPos.length; i++)
         {
            XONUtil.logDebugMesg("Mapping Pos: "+popularFltrPos[i]);
            String[] popFltrPosItem = popularFltrPos[i].split("::");
            if (popFltrPosItem.length != 2) continue;
            m_PopularFilterNamePos.put(popFltrPosItem[0], 
                                       Integer.valueOf(popFltrPosItem[1]).intValue());
         }
         XONUtil.logDebugMesg("PopularFilterNamePos: "+m_PopularFilterNamePos);
      } catch(Throwable th) { 
         XONUtil.logError("Exception while creating popular mappings: ", th); }
   }
   
   Vector<Integer> m_PopUnSortedPositions;
   public int buildTemplateFilters(String name, int resId)
   {
      TemplateFilterHandlerImpl templFltr = null; 
      if (!m_TemplateImageFilter.toString().
          equals(ImageFilter.PopularTemplateFilter.toString())) return -1;
      if (m_PopularFilterNameMappings == null) buildPopularFilterNameSettings();
      XONUtil.logDebugMesg("Construct Template Filter: "+name+" for: "+m_TemplateImageFilter);
      if (m_PopUnSortedPositions == null) m_PopUnSortedPositions = new Vector<Integer>();
      int posId = m_PopularFilterNamePos.get(name); String templFltrName = null;
      try {
         templFltr = (TemplateFilterHandlerImpl) 
                     XONUtil.getSerializeObject(XONPropertyInfo.m_MainActivity, resId);
         m_TemplateFilterCounter++;
         XONUtil.logDebugMesg("XON Popular Filter: "+templFltr.toString());
         m_PopUnSortedPositions.add(posId); templFltr.m_FilterPosId = posId; 
         templFltr.m_ImageFilter = m_TemplateImageFilter;
         String newFltrNm = m_PopularFilterNameMappings.get(templFltr.m_ImageFilterName);
         if (newFltrNm != null) templFltr.m_ImageFilterName = newFltrNm;
         templFltrName = XONImageFilterDef.getImageFilter(m_TemplateImageFilter, 
                                                          templFltr.m_ImageFilterName);
         m_TemplateFilters.put(templFltrName, templFltr);
         m_TemplateFiltersPos.put(templFltr.m_FilterPosId, templFltrName);  
         XONUtil.logDebugMesg("Added Template Fltr: "+templFltrName+" in pos: "+
                              templFltr.m_FilterPosId);
      } catch(Throwable th) { 
         XONUtil.logError("Unable to get System ser obj: "+name, th); 
      }
      sortTemplaterFilterPos(m_PopUnSortedPositions);
      return posId;
   }
   
   public void buildTemplateFilters(File savedTemplateFltrDir)
   {
      if (!m_TemplateImageFilter.equals(ImageFilter.PersonalizedTemplateFilter)) return;
      TemplateFilterHandlerImpl templFltr = null; Vector<Integer> posIds = null;
      File[] files = savedTemplateFltrDir.listFiles();
      if (files == null || files.length == 0) return;
      posIds = new Vector<Integer>(); String templFltrName = null;
      for (int i = 0; i < files.length; i++) 
      { 
         XONUtil.logDebugMesg("Personalized File "+i+1+" "+files[i]);
         try {
            templFltr = (TemplateFilterHandlerImpl) 
                        XONUtil.getSerializeObject(files[i].getAbsolutePath());            
            m_TemplateFilterCounter++;
            if (templFltr.m_TemplateSerFile == null)
               templFltr.m_TemplateSerFile = files[i].getAbsolutePath();
            XONUtil.logDebugMesg("XON Images URI: "+templFltr.toString());
            posIds.add(templFltr.m_FilterPosId);
            templFltrName = XONImageFilterDef.getImageFilter(m_TemplateImageFilter, 
                                                             templFltr.m_ImageFilterName);
            m_TemplateFilters.put(templFltrName, templFltr);
            m_TemplateFiltersPos.put(templFltr.m_FilterPosId, templFltrName); 
            
         } catch(Throwable th) { 
            XONUtil.logError("Unable to get ser obj fle: "+files[i].getName(), th); 
         }
      }
      sortTemplaterFilterPos(posIds);
   }
   
   private void sortTemplaterFilterPos(Vector<Integer> fltrPosIds)
   {
      int[] posIds = XONUtil.toArray(fltrPosIds);
      Arrays.sort(posIds); m_TemplateFilterSortedPos = XONUtil.createVector(posIds);
   }

   public void addTemplateFilter(File templFltrSerFile)
   {
      TemplateFilterHandlerImpl templFltr = null; String templFltrName = null; 
      XONUtil.logDebugMesg("Templ Ser File: "+templFltrSerFile);
      try {
         templFltr = (TemplateFilterHandlerImpl) 
                     XONUtil.getSerializeObject(templFltrSerFile.getAbsolutePath());            
         templFltrName = XONImageFilterDef.getImageFilter(m_TemplateImageFilter, 
                                                          templFltr.m_ImageFilterName);
         templFltr.m_TemplateSerFile = templFltrSerFile.getAbsolutePath();
         m_TemplateFilterSortedPos.add(templFltr.m_FilterPosId);
         m_TemplateFilters.put(templFltrName, templFltr);
         m_TemplateFiltersPos.put(templFltr.m_FilterPosId, templFltrName);            
      } catch(Exception ex) { 
         XONUtil.logError("Unable to get ser obj fle: "+templFltrSerFile.getName(), ex); 
      }
   }
   
   public int getCounter()
   {
      return m_TemplateFilterCounter;
   }
   
   public int getNextCounter()
   {
      m_TemplateFilterCounter++;
      return m_TemplateFilterCounter;
   }
   
   public static TemplateFilterHolder getTemplateFilterHolder(String imgFltr)
   {
      Object[] templImgFltrs = m_TemplateFilterHolder.keySet().toArray();
      XONUtil.logDebugMesg("Image Filter: "+imgFltr+" Template Filters: "+m_TemplateFilterHolder);
      for (int i = 0; i < templImgFltrs.length; i++) {
         if (imgFltr.contains(templImgFltrs[i].toString())) {
            return m_TemplateFilterHolder.get((ImageFilter)templImgFltrs[i]);
         }
      }
      return null;
   }
   
   public static TemplateFilterHandlerImpl getTemplateFilterImpl(String imgFltr)
   {
      TemplateFilterHolder templFltrHldr = getTemplateFilterHolder(imgFltr);
      if (templFltrHldr != null) return templFltrHldr.getTemplateFilter(imgFltr);
      return null;
   }
   
   public static boolean delTemplateFilterImpl(String imgFltr)
   {
      XONUtil.logDebugMesg("Del Img Fltr: "+imgFltr);   
      TemplateFilterHolder templFltrHldr = getTemplateFilterHolder(imgFltr);      
      if (templFltrHldr == null) return false; 
      TemplateFilterHandlerImpl templFltrHndlr = templFltrHldr.getTemplateFilter(imgFltr);
      File file = new File(templFltrHndlr.m_TemplateSerFile); if(!file.exists()) return false;   
      XONUtil.logDebugMesg("Del File: "+file);   
      XONUtil.deleteFiles(file); if(file.exists()) return false;
      String templFltrName = XONImageFilterDef.getImageFilter(templFltrHldr.m_TemplateImageFilter, 
                                                              templFltrHndlr.m_ImageFilterName);
      XONUtil.logDebugMesg("Del Templ Fltr Cache: "+templFltrName);   
      templFltrHldr.m_TemplateFilterSortedPos.remove(Integer.valueOf(templFltrHndlr.
                                                                     m_FilterPosId));
      templFltrHldr.m_TemplateFilters.remove(templFltrName);
      templFltrHldr.m_TemplateFiltersPos.remove(templFltrHndlr.m_FilterPosId);            
      return true;
   }
   
   public TemplateFilterHandlerImpl getTemplateFilter(String imgFltr)
   {
      XONUtil.logDebugMesg("Img Fltr: "+imgFltr);
      if (!imgFltr.contains(m_TemplateImageFilter.toString())) 
         imgFltr = XONImageFilterDef.getImageFilter(m_TemplateImageFilter, imgFltr);
      return m_TemplateFilters.get(imgFltr);
   }
   
   public String[] getImageFilters()
   {
      if (m_TemplateFilterSortedPos == null || m_TemplateFilterSortedPos.size() == 0)
         return new String[0];
      String[] imgFltrs = new String[m_TemplateFilterSortedPos.size()];
      for (int i = 0; i < m_TemplateFilterSortedPos.size(); i++) {
         String fltrName = m_TemplateFiltersPos.get(m_TemplateFilterSortedPos.elementAt(i));
         TemplateFilterHandlerImpl templFltr = m_TemplateFilters.get(fltrName);
         if (templFltr != null) imgFltrs[i] = templFltr.m_ImageFilterName;
      }
      return imgFltrs;
   }
}
