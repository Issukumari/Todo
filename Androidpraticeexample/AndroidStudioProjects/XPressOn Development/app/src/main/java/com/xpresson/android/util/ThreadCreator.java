package com.xpresson.android.util;

//The thread pool Executors class provides several methods for creating different types 
//of thread pools. 
import java.util.Map;
import java.util.concurrent.Executors;

//When the thread pool finishes executing a thread, the return value from the thread 
//execution is obtained through the Future object.      
//import java.util.concurrent.Future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import android.util.Log;

public class ThreadCreator
{
   int m_NumOfThreadCreated = 0;
   
   // Singleton Class providing constructors for different thread pool execution
   private static ThreadCreator m_ThreadCreator;
   
   private static ExecutorService m_ThreadExecutor;
   
   // This constructor is for Fixed Thread Pool
   private ThreadCreator()
   {
      // Uses Fixed Thread Pool Executor and sets thread pool size
      m_ThreadExecutor = Executors.newFixedThreadPool(XONPropertyInfo.ThreadPoolSize);
   }
   
   private ThreadCreator(int threadPoolSize)
   {
      // Uses Fixed Thread Pool Executor and sets thread pool size to threadPoolSize
      m_ThreadExecutor = Executors.newFixedThreadPool(threadPoolSize);
   }
   
   public static ThreadCreator getInstance(int threadPoolSize)
   {
      if (m_ThreadCreator == null)
         m_ThreadCreator = new ThreadCreator(threadPoolSize);
      return m_ThreadCreator;
   }

   public static ThreadCreator getInstance()
   {
      if (m_ThreadCreator == null)
         m_ThreadCreator = new ThreadCreator();
      return m_ThreadCreator;
   }
   
   public ThreadWorker createThread(ThreadInvokerMethod threadInvoker, 
                       Map<String, Object> data, boolean startExec)
   {
      ThreadWorker thread = new ThreadWorker(threadInvoker, data);
      m_NumOfThreadCreated++;      
      
      if (startExec) {
         try { 
            m_ThreadExecutor.submit(thread); 
            XONUtil.debugLog(Log.DEBUG, "Num Of Thread Created: "+m_NumOfThreadCreated+
                                        " Req Data: "+data);
         }
         catch (RejectedExecutionException ex) 
         { XONUtil.logError("RejectedExecutionException: "+ ex.getMessage(), ex); } 
         catch (Exception ex) 
         { XONUtil.logError("Exception: "+ ex.getMessage(), ex);  } 
      }
      return thread;
   }

}
