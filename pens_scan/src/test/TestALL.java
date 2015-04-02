package test;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.cfg.Configuration;

import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.manager.FTPManager;

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
 
 public static String testFTPCon() {
	 String output = "";
	 EnvProperties env =EnvProperties.getInstance();
	 boolean can = true;
	 String errorMsg = "";
	 try{
		 String ip = env.getProperty("ftp.ip.server");
		 String user = env.getProperty("ftp.username");
		 String pwd = env.getProperty("ftp.password");
		 
		 FTPManager ftpManager = new FTPManager(ip,user, pwd );
		 try{
		   ftpManager.canConnectFTPServer();
		 }catch(Exception ee){
			 can = false;
			 errorMsg = ee.getMessage();
		 }
		 output +="FTP SERVER IP : "+ftpManager.server+"\n";
		 output +="FTP SERVER User : "+user+"\n";
		 output +="FTP SERVER Password : "+pwd+"\n";
		 
		 if(can==false){
		    output +="FTP SERVER Result Connection:"+errorMsg+"\n"; 
		 }else{
		    output +="FTP SERVER Result Connection: Success \n";
		 }
	 }catch(Exception e){
		 e.printStackTrace();
		 
	 }finally{
		
	 }
	 return output;
  }
}
