package com.xpresson.android.util;

import java.util.Hashtable;
import java.util.Vector;

public class XONObjectCache
{
   private static XONObjectCache m_XONObjectCache;
   private Hashtable<String, Object> m_MemoryCache;
   private Vector<String> m_PersitentMemoryCache;

   private XONObjectCache() 
   {
       m_MemoryCache = new Hashtable<String, Object>();
       m_PersitentMemoryCache = new Vector<String>();
   }

   private static XONObjectCache getInstance() 
   {
       if(m_XONObjectCache==null) {
          m_XONObjectCache = new XONObjectCache();
       }
       return m_XONObjectCache;
   }

   public static void addObject(String key, Object object, boolean isPersistent) 
   {
      XONObjectCache objCache = getInstance();
      objCache.m_MemoryCache.put(key, object);
      if (isPersistent) objCache.m_PersitentMemoryCache.add(key);
   }

   public static Object getObjectForKey(String key) 
   {
      return getObjectForKey(key, false);
   }
   
   public static Object removePersistentObject(String key) 
   {
      return getObjectForKey(key, true);
   }  
   
   private static Object getObjectForKey(String key, boolean removePersistentObj) 
   {
      XONObjectCache objCache = getInstance();
      Object data = objCache.m_MemoryCache.get(key);
      if (removePersistentObj || !objCache.m_PersitentMemoryCache.contains(key))
      { XONUtil.logDebugMesg("Removing Cache: "+key); objCache.m_MemoryCache.remove(key); 
        objCache.m_PersitentMemoryCache.remove(key); }
      return data;
   }  
   
}
