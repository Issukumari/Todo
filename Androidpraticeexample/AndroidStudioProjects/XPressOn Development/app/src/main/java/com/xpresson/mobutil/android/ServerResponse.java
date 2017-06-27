package com.xpresson.mobutil.android;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class ServerResponse implements Serializable
{
   private static final long serialVersionUID = -962626617128571160L;

   public boolean m_IsSuccess = false;
   public Map<String, Object> m_ResultValues;
   
   public ServerResponse()
   {
      m_ResultValues = new HashMap<String, Object>();
   }
   
   public String toString()
   {
      StringBuffer mesg = new StringBuffer("SERVER RESPONSE: ");
      if (m_IsSuccess) mesg.append("SUCCESSFULLY EXECUTED THE REQUEST");
      else 
      {
         mesg.append("FAILURE IN EXECUTING THE REQUEST");
         return mesg.toString();
      }
      mesg.append("\n" + m_ResultValues.toString());
      return mesg.toString();
   }

   //Serialisation
   public void save(ObjectOutputStream objstream) throws IOException 
   {
      objstream.writeObject(this);
   }
   
   //DeSerialisation
   public static ServerResponse load(ObjectInputStream objstream ) throws Exception 
   {
      ServerResponse respObj = (ServerResponse) objstream.readObject();
      return respObj;
   }
}
