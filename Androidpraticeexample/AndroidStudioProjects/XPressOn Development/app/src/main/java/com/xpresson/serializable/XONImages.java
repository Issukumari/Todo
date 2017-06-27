package com.xpresson.serializable;

import java.io.File;
import java.io.Serializable;
import java.util.Vector;

import com.xpresson.android.util.XONImageHandler;
import com.xpresson.android.util.XONUtil;

import android.net.Uri;

public class XONImages implements Serializable
{
   private static final long serialVersionUID = 4737655416992502793L;
   
   public int m_FileCounter = 0;
   public Vector<String> m_XONImages, m_XONThumbImages, m_XONCameraImages;
   
   public XONImages()
   {
      m_FileCounter = 0;
      m_XONImages = new Vector<String>();
      m_XONThumbImages = new Vector<String>();
      m_XONCameraImages = new Vector<String>();
   }
   
   public int nextCounter()
   {
      m_FileCounter++; return m_FileCounter;
   }
   
   public void addXONCameraImageData(String path)
   {
      m_XONCameraImages.addElement(path); 
   }
   
   public void addXONImageData(String uri, String thumbUri)
   {
      m_XONImages.addElement(uri); m_XONThumbImages.addElement(thumbUri);
      XONUtil.logDebugMesg("Saving Full Image: "+m_XONImages.lastElement()+
                           "\nThumb Image: "+m_XONThumbImages.lastElement());
   }
      
   public int getCount() { return m_XONImages.size(); }
   public int getThumbCount() { return m_XONThumbImages.size(); }
   
   public String getLastCameraImage() { return m_XONCameraImages.lastElement(); }
   public String getFullImageData(int pos) { return m_XONImages.get(pos); }
   public String getThumbImageData(int pos) { return m_XONThumbImages.get(pos); }
   
   public boolean deleteXONImage(int pos, XONImageHandler fullImgHdlr)
   {
      String path = m_XONImages.elementAt(pos); XONUtil.logDebugMesg("Path "+path);
      File file = new File(path); if(!file.exists()) return false;
      XONUtil.deleteFiles(file); fullImgHdlr.deleteImageFile(path);
      delImageFile(pos, generatePath(m_XONImages.remove(pos), "_full_", "_thumb_"), 
                   m_XONThumbImages);
      return true;
   }
   
   public boolean sinkImageCache()
   {
      boolean fullImageSink = checkFileExists(null);
      boolean thImageSink = checkThumbFileExists(null);
      if (fullImageSink || thImageSink ) return true; 
      return false;
   }
   
   public boolean checkFileExists(XONImageHandler fullImgHdlr)
   {
      File file = null; boolean isFileExist = true, reset = false; String path = null;
      for(int i=0; i<m_XONImages.size(); i++)
      {
         isFileExist = true; path = m_XONImages.elementAt(i);
         file = new File(path); if(!file.exists()) isFileExist = false;
         if (isFileExist) continue;
         reset = true; XONUtil.deleteFiles(file); m_XONImages.remove(i);          
         if (fullImgHdlr != null) fullImgHdlr.deleteImageFile(path);
//         delImageFile(i, generatePath(m_XONImages.remove(i), "_full_", "_thumb_"), 
//                      m_XONThumbImages);
      }
      return reset;
   }
   
   public boolean checkThumbFileExists(XONImageHandler thumbImgHdlr)
   {
      File file = null; boolean isFileExist = true, reset = false; String path = null;
      for(int i=0; i<m_XONThumbImages.size(); i++)
      {
         isFileExist = true; path = m_XONThumbImages.elementAt(i);
         file = new File(path); if(!file.exists()) isFileExist = false;
         if (isFileExist) continue;
         reset = true; XONUtil.deleteFiles(file); m_XONThumbImages.remove(i);
         if (thumbImgHdlr != null) thumbImgHdlr.deleteImageFile(path);
//         delImageFile(i, generatePath(m_XONThumbImages.remove(i), "_thumb_", "_full_"), 
//                      m_XONImages);
      }
      return reset;
   }
   
   public String generatePath(String origPath, String origSplitter, String newSplitter)
   {
      String [] imgFileParts = origPath.split(origSplitter);
      String newPath = imgFileParts[0]+newSplitter+imgFileParts[1];
      XONUtil.logDebugMesg("Orig Path: "+origPath+" New Path: "+newPath);
      return newPath;
   } 
   
   public boolean checkImageExists()
   {
      if (m_XONImages.size() == 0 && m_XONThumbImages.size() == 0) return false;
      return true;
   }
   
   public boolean checkImageExists(int pos)
   {
//      if (pos >= m_XONImages.size() || pos >= m_XONThumbImages.size()) return false;
//      String path = m_XONImages.elementAt(pos), thPath = m_XONThumbImages.elementAt(pos);
//      if (path.equals(thPath)) return true;    
      return true;
   }
   
   private void delImageFile(int pos, String filePath, Vector<String> images)
   {
      if (pos >= images.size()) return;
      String path = images.elementAt(pos);
      XONUtil.logDebugMesg("Path: "+path+" File Path: "+filePath);
      XONUtil.deleteFiles(new File(path));      
//      if (path.equals(filePath)) {
//         XONUtil.logDebugMesg("Deleting File: "+path);
//         XONUtil.deleteFiles(new File(path));      
//      }
   }
   
   public Uri getUri(int pos)
   {
      String uriStr = m_XONImages.get(pos);
      if (uriStr == null) return null;
      return Uri.parse(uriStr);
   }
   
   public String toString()
   {
      StringBuffer mesg = new StringBuffer("XON Full Images: ");
      mesg.append(m_XONImages+"\nThumb Images: "+m_XONThumbImages);
      return mesg.toString();
   }

}
