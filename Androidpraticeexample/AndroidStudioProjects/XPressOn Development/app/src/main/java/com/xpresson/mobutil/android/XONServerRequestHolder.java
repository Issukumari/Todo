package com.xpresson.mobutil.android;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

public class XONServerRequestHolder implements Serializable
{
   private static final long serialVersionUID = 1507768507912434797L;
   
   public static enum RequestType { 
      SERVER_TEST;      
   }

   public String m_RequestIdentifier;
   public String m_RequestType;
   public ServerRequest m_ServerRequest;

   public XONServerRequestHolder(String reqIdentifier, String reqType) 
   {
      m_RequestIdentifier = reqIdentifier;
      m_RequestType = reqType;
   }
   
   //Serialisation
   public void save(ObjectOutputStream objstream) throws IOException 
   {
      objstream.writeObject(this);
   }
   
   //De-Serialisation
   public static XONServerRequestHolder load(ObjectInputStream objstream ) throws Exception 
   {
      XONServerRequestHolder reqObj = (XONServerRequestHolder) objstream.readObject();
      return reqObj;
   }
   
   public String toString()
   {
      StringBuffer mesg = new StringBuffer();
      mesg.append("Request Identifier: " + m_RequestIdentifier + " ");
      mesg.append(" Request Type: " + m_RequestType + " ");
      mesg.append(m_ServerRequest.toString());
      return mesg.toString();
   }
   
}
