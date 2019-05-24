package util;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class SessionUtils {
	
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void clearSessionUnusedForm(HttpServletRequest request,String curSessionFormName){
		if(ControlCode.canExecuteMethod("SessionUtils", "clearSessionUnusedFormModel")){
			clearSessionUnusedFormModel(request, curSessionFormName);
		}
	}
	
	private static void clearSessionUnusedFormModel(HttpServletRequest request,String curSessionFormName){
      try{
    	  HttpSession session = request.getSession();
    	  Enumeration mySession = session.getAttributeNames();
    	  while (mySession.hasMoreElements()) {
	    	  String mySessionVarName = (String)mySession.nextElement();
	    	 // logger.debug("Session Attr Name:"+mySessionVarName);
	    	  if(mySessionVarName.indexOf("Form") != -1 && !mySessionVarName.equals(curSessionFormName)){
	    		  session.removeAttribute(mySessionVarName);
	    	      //logger.info("Remove Attr SessionName="+mySessionVarName);
	    	  }
    	  }
      }catch(Exception e){
    	 logger.error(e.getMessage(),e);
      }
	    
	}
	
	public static String listSessionAttribute(HttpServletRequest request){
		String allAttrbute = "";
	      try{
	    	  HttpSession session = request.getSession();
	    	  Enumeration mySession = session.getAttributeNames();
	    	  while (mySession.hasMoreElements()) {
		    	  String mySessionVarName = (String)mySession.nextElement();
		    	  allAttrbute  += mySessionVarName+" | ";
	    	  }
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
		   return allAttrbute;
	}
}
