package test;

import java.sql.Connection;
import java.sql.DriverManager;

import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.manager.FTPManager;

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
 
 public static String testFTPPensCon() {
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
		 output +="FTP SERVER(PENS) IP : "+ftpManager.server+"\n";
		 output +="FTP SERVER(PENS) User : "+user+"\n";
		 output +="FTP SERVER(PENS) Password : "+pwd.toCharArray()+"\n";
		 
		 if(can==false){
		    output +="FTP(PENS) SERVER Result Connection:"+errorMsg+"\n"; 
		 }else{
		    output +="FTP(PENS) SERVER Result Connection: Success \n";
		 }
	 }catch(Exception e){
		 e.printStackTrace();
		 
	 }finally{
		
	 }
	 return output;
  }
 
 public static String testFTPICCCon() {
	 String output = "";
	 EnvProperties env =EnvProperties.getInstance();
	 boolean can = true;
	 String errorMsg = "";
	 try{
		 String ip = env.getProperty("ftp.icc.ip.server");
		 String user = env.getProperty("ftp.icc.username");
		 String pwd = env.getProperty("ftp.icc.password");
		 
		 FTPManager ftpManager = new FTPManager(ip,user, pwd );
		 try{
		   ftpManager.canConnectFTPServer();
		 }catch(Exception ee){
			 can = false;
			 errorMsg = ee.getMessage();
		 }
		 output +="FTP(ICC) SERVER IP : "+ftpManager.server+"\n";
		 output +="FTP(ICC) SERVER User : "+user+"\n";
		 output +="FTP(ICC) SERVER Password : "+pwd.toCharArray()+"\n";
		 
		 if(can==false){
		    output +="FTP(ICC) SERVER Result Connection:"+errorMsg+"\n"; 
		 }else{
		    output +="FTP(ICC) SERVER Result Connection: Success \n";
		 }
	 }catch(Exception e){
		 e.printStackTrace();
		 
	 }finally{
		
	 }
	 return output;
  }
 
 public static String testDataSource(){
	 try{
	    javax.naming.Context initContext = new javax.naming.InitialContext();
		javax.naming.Context envContext  = (javax.naming.Context)initContext.lookup("java:/comp/env");
		javax.sql.DataSource ds = (javax.sql.DataSource)envContext.lookup("jdbc/pensbi");
		Connection conn = ds.getConnection();
		return "DB Result Test Connection DataSource: Success ["+conn+"]\n";
    }catch(Exception e){
    	e.printStackTrace();
    }
	 return"";
 }
 
}
