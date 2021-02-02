package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBOracleConnection {
   
	private static Logger logger = Logger.getLogger("PENS");
	
	private static DBOracleConnection _instance;
	
	public static DBOracleConnection getInstance(){
	  if(_instance ==null)
	    return new DBOracleConnection();
	  return _instance;
	}
	
	public  Connection getConnection(){		
		Connection _instanceInf =null;
		EnvProperties env = EnvProperties.getInstance();
		try {	

			String url = env.getProperty("connection.oracle.url");
			String username = env.getProperty("connection.oracle.username");
			String password = env.getProperty("connection.oracle.password");
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			//props.setProperty(OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT, "2000");

			_instanceInf = DriverManager.getConnection(url, props);
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
