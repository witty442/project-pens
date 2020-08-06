package com.pens.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;


public class DBConnectionApps {
   
	private static Logger logger = Logger.getLogger("PENS");
	private  static DBConnectionApps _instance;
	
	public static  DBConnectionApps getInstance(){
	  if(_instance ==null)
		  _instance = new DBConnectionApps();
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
	
	public   Connection getConnectionModel(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	
			String driver = env.getProperty("connection.driver_class");
			String url = env.getProperty("connection.url");
			String username = env.getProperty("connection.apps.username");
			String password = env.getProperty("connection.apps.password");
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			Class.forName(driver);
			_instanceInf = DriverManager.getConnection(url,username,password);
			//logger.debug("Connection:"+_instanceInf);
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	
	public static void close(Connection conn,PreparedStatement ps,ResultSet rs){
		try{
			close(conn);
			close(ps);
			close(rs);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
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
	
	public void closeConn(Connection conn,PreparedStatement ps ,ResultSet rs) {
		try{
			if(conn != null){
				conn.close();conn=null;
			}
			if(ps != null){
				ps.close();ps=null;
			}
			if(rs != null){
				rs.close();rs=null;
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}

}
