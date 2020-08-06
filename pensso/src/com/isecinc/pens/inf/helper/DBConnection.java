package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.pens.util.EnvProperties;

public class DBConnection {
   
	private static Logger logger = Logger.getLogger("PENS");
	
	private static DBConnection _instance;
	
	public static DBConnection getInstance(){
	  if(_instance ==null)
	    return new DBConnection();
	  return _instance;
	}
	public   Connection getConnection(){		
		Connection _instanceInf =null;
		try{
		   _instanceInf = getConnectionModel();
		}catch(Exception e){
			// Retry count 1
			try{
				logger.info("Retry Conn 1 time");
			   _instanceInf = getConnectionModel();
			}catch(Exception ee){
				logger.error(ee.getMessage(),ee);
				logger.info("Retry Conn 2 time");
				try{
				   _instanceInf = getConnectionModel();
				}catch(Exception eee){
					logger.error(eee.getMessage(),eee);
				}
			}
		}
		return _instanceInf;
	}
	
	public  Connection getConnectionModel(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = env.getProperty("connection.url");
			String username = env.getProperty("connection.username");
			String password = env.getProperty("connection.password");
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			//logger.debug("Connection:"+_instanceInf);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	
	public  Connection getConnectionApps(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = env.getProperty("connection.url");
			String username = "apps";
			String password = "apps";
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);	
			//logger.debug("Connection:"+_instanceInf);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	public static void close(Connection conn){
		try{
			if(conn !=null){
				conn.close();
				conn =null;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void close(PreparedStatement conn){
		try{
			if(conn !=null){
				conn.close();
				conn =null;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static void close(Statement conn){
		try{
			if(conn !=null){
				conn.close();
				conn =null;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void close(ResultSet conn){
		try{
			if(conn !=null){
				conn.close();
				conn =null;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	

}
