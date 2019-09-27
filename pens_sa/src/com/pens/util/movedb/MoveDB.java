package com.pens.util.movedb;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

public class MoveDB {

	private static Logger logger = Logger.getLogger("PENS");

	static Connection connSource = null;
	static Connection connDest = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startMerg();
	}
	
	public  static Connection getConnectionPROD(){		
		try {	
			if(connDest != null){
				return connDest;
			}
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@//192.168.37.185:1521/PENS";
			String username = "pensbi";
			String password = "pensbi";
			
			logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			 connDest = DriverManager.getConnection(url,username,password);	
			logger.debug("Connection:"+connDest);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connDest;	
	}
	
	public  static Connection getConnectionTEST(){		
		try {	
			if(connSource != null){
				return connSource;
			}
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@//192.168.38.186:1529/TEST";
			String username = "pensbi";
			String password = "pensbi";
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			 Class.forName(driver);
			//DriverManager.setLoginTimeout(600);
			 connSource = DriverManager.getConnection(url,username,password);	
			//logger.debug("Connection:"+connSource);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);			
		}
		return connSource;	
	}
	
	public static void startMerg(){

		try{	
			logger.debug("----Start----");
			
			connSource = getConnectionPROD();
			connDest = getConnectionTEST();
			
			
			
			
			logger.debug("----Success----");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(connSource != null){
					connSource.close();connSource=null;
				}
				if(connDest != null){
					connDest.close();connDest=null;
				}
			}catch(Exception e){}
		}
	}
	
	
}
