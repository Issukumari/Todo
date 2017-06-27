package com.xpresson.mobutil.android;

import java.io.Serializable;
import java.util.Map;

public class ServerTestRequest extends ServerRequest implements Serializable
{
   private static final long serialVersionUID = 2866520618693047144L;
   public static enum RequestKeys { 
      IMAGE;      
   }
   public Map<String, Object> m_RequestData;
}
