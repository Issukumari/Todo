package com.xpresson.android.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.xpresson.android.R;

import android.app.Activity;

public class XONShareHandler
{
   public enum ShareAction 
   { 
	  ShowAboutXON, Feedback, GoogleAppReview, ReferFriend, 
	  FollowOnFB, ConnectFB, PostOnFB, 
	  FollowOnTwitter, ConnectTwitter, PostOnTwitter, 
      ConnectFlickr, PostOnFlickr, ConnectPicassa, PostOnPicassa; 
      
      static Vector<String> getShareActionList()
      {
         String[] shareActionArr = XONUtil.objToStringArray(ShareAction.values());
         Vector<String> shareActionLists = XONUtil.createVector(shareActionArr);
         return shareActionLists;
      }
   
   }

   private static Vector<String> m_ShareActionList;
   public static Vector<String> getShareActionList()
   {
      if (m_ShareActionList == null) m_ShareActionList = ShareAction.getShareActionList();
      return m_ShareActionList;
   }
   
   public static boolean containsAction(String action)
   {
      if (m_ShareActionList == null) getShareActionList();
      return m_ShareActionList.contains(action);
   }
   
   private static Map<ShareAction, Map<String, String>> m_XONShareProps;
   public static Map<String, String> getXONShareProps(ShareAction action)
   {
      if (m_XONShareProps != null)
         return m_XONShareProps.get(action);
      m_XONShareProps = new HashMap<ShareAction, Map<String, String>>();
      String[] shareData = XONPropertyInfo.getStringArray(R.array.XONShareProperties);
      for(int i = 0; i < shareData.length; i++) 
      {
         String[] shareDataArr = shareData[i].split("::");
         String actionProp = "";
         Map<String, String> shareActionProp = new HashMap<String, String>();
         for(int j = 0; j < shareDataArr.length; j++) {
            String[] shareDataVal = shareDataArr[j].split(":=");
            if (shareDataVal[0].equals("Action")) actionProp = shareDataVal[1];
            shareActionProp.put(shareDataVal[0], shareDataVal[1]);
         }
         m_XONShareProps.put(ShareAction.valueOf(actionProp), shareActionProp);
      }
      return m_XONShareProps.get(action);
   }
   
   
   public static Object processShareAction(Activity act, String actionVal)
   {
      if (!containsAction(actionVal)) return null;
      ShareAction action = ShareAction.valueOf(actionVal);
      switch(action)
      {
         case ShowAboutXON:
        	XONPropertyInfo.showAboutMesg();
        	break;
      	 case Feedback:
            Map<String, String> shareProps = getXONShareProps(action);
            IntentUtil.sendEmailText(act, shareProps.get("EMailTo"), 
                                     shareProps.get("EmailSub"), "");
            break;
         case GoogleAppReview:
            IntentUtil.rateXPressOn(act);
            break;
         case ReferFriend:
            String referFrTxt = XONPropertyInfo.getString(R.string.refer_friend_mesg);
            IntentUtil.shareContent(act, R.drawable.refer_friends_mesg, referFrTxt);
            break;
         case FollowOnFB:
            String url = XONPropertyInfo.getString(R.string.XONFBPage);
            url = "http://m.facebook.com/xpressonapp";
            IntentUtil.openWebPage(act, url);
            break;
         default: break;
      }
      return null;
   }
}
