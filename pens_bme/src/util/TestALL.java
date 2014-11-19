package util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.cfg.Configuration;


public class TestALL {
   
	
 public static String testDBCon() {
	 String output = "";
	 Connection conn = null;
	 try{
			Configuration hibernateConfig = new Configuration();
			hibernateConfig.configure();
			
			String driver = hibernateConfig.getProperty("connection.driver_class");
			String url = hibernateConfig.getProperty("connection.url");
			String username = hibernateConfig.getProperty("connection.username");
			String password = hibernateConfig.getProperty("connection.password");
			
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
