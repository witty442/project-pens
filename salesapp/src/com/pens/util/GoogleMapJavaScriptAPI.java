package com.pens.util;


public class GoogleMapJavaScriptAPI {
    private static GoogleMapJavaScriptAPI _instance;
    private  String API_KEY = "";
   // AIzaSyAJUWErumNlehQ6rT4rP52yThSNsYd7bsI
    
    public GoogleMapJavaScriptAPI(){
    	try{
    		API_KEY = EnvProperties.getInstance().getProperty("google.map.api.key");
		}catch(Exception e){
		}
    }
    
	public static GoogleMapJavaScriptAPI getInstance(){
	  if(_instance ==null){
		  _instance = new GoogleMapJavaScriptAPI();
		  //System.out.println("New SessionGen ["+_instance.getIdSession()+"]");
	  }else{
		  //System.out.println("Old SessionGen ["+_instance.getIdSession()+"]");
	  }
	  return _instance;
	}
	
	public static void clearInstance(){
		_instance =  new GoogleMapJavaScriptAPI();
	}
	
	public  String getAPIKey(){
		return API_KEY;
	}

}
