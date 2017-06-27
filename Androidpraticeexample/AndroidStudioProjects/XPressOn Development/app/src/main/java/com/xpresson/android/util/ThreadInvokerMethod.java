package com.xpresson.android.util;

import java.util.Map;

//This interface is used to start processing on new thread.
public interface ThreadInvokerMethod
{
   // This function is called by the new thread to process request
   public void processThreadRequest(Map<String, Object> requestData) throws Exception;

}
