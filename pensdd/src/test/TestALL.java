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
			
			output +="DB IP:"+url+"\n";
			output +="DB User:"+username+"\n";
			output +="DB Password:"+password+"\n";
			
			Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			conn = DriverManager.getConnection(url,username,password);	
			 
			output +="DB Result Conn:"+conn+"\n";
	 }catch(Exception e){
		 e.printStackTrace();
		 output +="DB Result Conn:"+e.getMessage()+"\n";
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
	 try{
		 String ip = env.getProperty("ftp.ip.server");
		 String user = env.getProperty("ftp.username");
		 String pwd = env.getProperty("ftp.password");
	 
		 output +="FTP SERVER  IP:"+ip+"\n";
		 output +="FTP SERVER User:"+user+"\n";
		 output +="FTP SERVER Password:"+pwd+"\n";
		 
		 FTPManager ftpManager = new FTPManager(ip,user, pwd );
		 ftpManager.canConnectFTPServer();
		 
		 output +="FTP SERVER Result Conn:Success \n";
	 }catch(Exception e){
		 e.printStackTrace();
		 output +="FTP SERVER Result Conn:"+e.getMessage()+"\n";
	 }finally{
		
	 }
	 return output;
  }
}
