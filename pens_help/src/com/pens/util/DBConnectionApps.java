package com.pens.util;

import java.sql.Connection;
import java.sql.DriverManager;



public class DBConnectionApps {
   
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
			e.printStackTrace();
		}
		return _instanceInf;
	}
	
	public   Connection getConnectionModel(){		
		Connection _instanceInf =null;
		try {	
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@//192.168.37.185/PENS";
			String username = "apps";
			String password = "apps";
			
			//logger.debug("Try GetConnection DB:"+url+","+username+","+password);
			
			Class.forName(driver);
			_instanceInf = DriverManager.getConnection(url,username,password);
			//logger.debug("Connection:"+_instanceInf);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return _instanceInf;	
	}
	
	

}
