package com.pens.util;

import java.util.Date;

import com.isecinc.pens.inf.helper.Utils;

public class SIdUtils {
    private static SIdUtils _instance;
    private  String idSession = "";
	
    public SIdUtils(){
    	try{
    		idSession = Utils.stringValue(new Date(),Utils.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
		}catch(Exception e){
		}
    }
    
	public static SIdUtils getInstance(){
	  if(_instance ==null){
		  _instance = new SIdUtils();
		  //System.out.println("New SessionGen ["+_instance.getIdSession()+"]");
	  }else{
		  //System.out.println("Old SessionGen ["+_instance.getIdSession()+"]");
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
