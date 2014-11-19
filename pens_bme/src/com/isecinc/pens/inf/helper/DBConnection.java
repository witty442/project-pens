package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import util.DBCPConnectionProvider;

public class DBConnection {
   
	private static Logger logger = Logger.getLogger("PENS");
	
	private static DBConnection _instance;
	
	public static DBConnection getInstance(){
	  if(_instance ==null)
	    return new DBConnection();
	  return _instance;
	}
	
	public  Connection getConnection(){		
		Connection _instanceInf =null;
		try{
		_instanceInf = new  DBCPConnectionProvider().getConnection(_instanceInf);
		}catch(Exception e){
		  logger.error(e.getMessage(),e);
		}
		return _instanceInf;
	}
	public  Connection getConnection1(){		
		Connection _instanceInf =null;
		try {	

			Configuration hibernateConfig = new Configuration();
			hibernateConfig.configure();
			
			String driver = hibernateConfig.getProperty("connection.driver_class");
			String url = hibernateConfig.getProperty("connection.url");
			String username = hibernateConfig.getProperty("connection.username");
			String password = hibernateConfig.getProperty("connection.password");
			
			logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			_instanceInf = DriverManager.getConnection(url,username,password);
			
			//_instanceInf = new DBCPConnectionProvider().getConnection(_instanceInf);
			logger.debug("Connection:"+_instanceInf);
			
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
