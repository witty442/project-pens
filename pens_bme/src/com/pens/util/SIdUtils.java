package com.pens.util;

import java.util.Date;

public class SIdUtils {
    private static SIdUtils _instance;
    private  String idSession = "";
	
    public SIdUtils(){
    	try{
    		idSession = DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
		}catch(Exception e){
		}
    }
    
	public static SIdUtils getInstance(){
	  if(_instance ==null){
		  _instance = new SIdUtils();
		  //System.out.println("New SessionIdUtils ["+_instance.getIdSession()+"]");
	  }else{
		  //System.out.println("Old SessionIdUtils ["+_instance.getIdSession()+"]");
	  }
	  return _instance;
	}
	
	public  void clearInstance(){
		_instance =  new SIdUtils();
	}
	
	public  String getIdSession(){
		return idSession;
	}

}
