package com.xpresson.android.util;

import java.util.Map;

import android.util.Log;

//import android.util.Log;

public class ThreadWorker implements Runnable
{
   int m_ThreadPriority;
   Map<String, Object> m_RequestData;
   ThreadInvokerMethod m_ThreadInvokerMethod;
   static int m_NumOfThreadWorked = 0;
   
   // This Constructor is used if called from ThreadPool
   public ThreadWorker(ThreadInvokerMethod threadInvoker, Map<String, Object> data)
   {
      m_RequestData = data;
      m_ThreadInvokerMethod = threadInvoker;
   }
   
   public ThreadWorker(int priority, ThreadInvokerMethod threadInvoker, Map<String, Object> data)
   {
      m_RequestData = data;
      m_ThreadPriority = priority;
      m_ThreadInvokerMethod = threadInvoker;
   }
   
   public void startThreadInvocation() 
   {
      /* Construct an instance of Thread, passing the current class (i.e. the Runnable) as an argument. */
      Thread t = new Thread(this);
      t.setPriority(m_ThreadPriority);
      t.start(); 
   }    
   
   public void run() 
   {
      try 
      {
         m_NumOfThreadWorked++; 
         m_ThreadInvokerMethod.processThreadRequest(m_RequestData);
         XONUtil.debugLog(Log.DEBUG, "Num Of Thread Worked: "+m_NumOfThreadWorked+
                                      " Req Obj: "+m_RequestData);      
      } catch (Exception ex) { ex.printStackTrace(); }
   }
   
   public String toString()
   {
      StringBuffer mesg = new StringBuffer("Thread Request Hdlr: ");
      mesg.append(m_ThreadInvokerMethod.getClass().getName());
      mesg.append("\nReq Data: "+m_RequestData);
      return mesg.toString();
   }

}
