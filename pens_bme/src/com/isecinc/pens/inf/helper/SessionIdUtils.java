package com.isecinc.pens.inf.helper;

import java.util.Date;

public class SessionIdUtils {
    private static SessionIdUtils _instance;
    private static String idSession = "";
	
    public SessionIdUtils(){
    	try{
    		idSession = Utils.stringValue(new Date(),Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
		}catch(Exception e){
		}
    }
    
	public static SessionIdUtils getInstance(){
	  if(_instance ==null){
		  _instance = new SessionIdUtils();
		  //System.out.println("New SessionIdUtils ["+_instance.getIdSession()+"]");
	  }else{
		  //System.out.println("Old SessionIdUtils ["+_instance.getIdSession()+"]");
	  }
	  return _instance;
	}
	
	public static void clearInstance(){
		_instance =  new SessionIdUtils();
	}
	
	public static String getIdSession(){
		return idSession;
	}

}
