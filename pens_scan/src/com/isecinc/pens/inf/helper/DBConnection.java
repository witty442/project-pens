package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import com.mysql.jdbc.Driver;

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
			
			/*Enumeration<Driver> drivers = DriverManager.getDrivers();
            ArrayList<Driver> driversToUnload=new ArrayList<Driver>();
            while (drivers.hasMoreElements()) {
                    Driver driver = drivers.nextElement();
                    if (driver.getClass().getClassLoader().equals(getClass().getClassLoader())) {
                            driversToUnload.add(driver);
                    }
            }
            for (Driver driver : driversToUnload) {
                DriverManager.deregisterDriver(driver);
            }*/
            
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
