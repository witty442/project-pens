package com.isecinc.pens.report.salesanalyst.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


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
		try {	
			DBCPConnectionProvider dbSource = new DBCPConnectionProvider();
			_instanceInf = dbSource.getConnection(_instanceInf);
			//logger.debug("Connection:"+_instanceInf);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return _instanceInf;	
	}
	
	
	public  Connection getConnection_(){		
		Connection _instanceInf =null;
		try {	
  
			Configuration hibernateConfig = new Configuration();
			hibernateConfig.configure();
			
			String driver = hibernateConfig.getProperty("connection.driver_class");
			String url = hibernateConfig.getProperty("connection.url");
			String username = hibernateConfig.getProperty("connection.username");
			String password = hibernateConfig.getProperty("connection.password");
			
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
