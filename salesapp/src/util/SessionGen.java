package util;

import java.util.Date;

import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class SessionGen {
    private static SessionGen _instance;
    private  String idSession = "";
	
    public SessionGen(){
    	try{
    		idSession = DateUtil.stringValue(new Date(),DateUtil.DD_MM_YYYY_HH_mm_ss_WITHOUT_SLASH);
		}catch(Exception e){
		}
    }
    
	public static SessionGen getInstance(){
	  if(_instance ==null){
		  _instance = new SessionGen();
		  //System.out.println("New SessionGen ["+_instance.getIdSession()+"]");
	  }else{
		  //System.out.println("Old SessionGen ["+_instance.getIdSession()+"]");
	  }
	  return _instance;
	}
	
	public  void clearInstance(){
		_instance =  new SessionGen();
	}
	
	public  String getIdSession(){
		return idSession;
	}

}
