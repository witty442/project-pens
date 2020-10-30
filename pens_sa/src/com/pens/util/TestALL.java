package com.pens.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class TestALL {
   
	
 public static String testDBCon() {
	 String output = "";
	 Connection conn = null;
	 try{
		    EnvProperties env = EnvProperties.getInstance();
			String driver = env.getProperty("db.driver_class");
			String url = env.getProperty("db.url");
			String username = env.getProperty("db.username");
			String password = env.getProperty("db.password");
			
			output +="DB IP  : "+url+"\n";
			output +="DB User : "+username+"\n";
			output +="DB Password : "+password+"\n";
			
			Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			conn = DriverManager.getConnection(url,username,password);	
			 
			output +="DB Result Test Connection : Success ["+conn+"]\n";
	 }catch(Exception e){
		 e.printStackTrace();
		 output +="DB Result Test Connection:Error ["+e.getMessage()+"]\n";
	 }finally{
		 try{
			 if(conn != null){
				 conn.close();conn=null;
			 }
		 }catch(Exception e){}
	 }
	 return output;
  }
 
 
}
