package com.xpresson.android.util;

import java.util.Map;

public interface XONEventListener
{
   public enum XONEventType {
      GlobalLayoutEvent
   }
   
   public void processXONEvent(XONEventType event, Map<String, Object> data);
}
