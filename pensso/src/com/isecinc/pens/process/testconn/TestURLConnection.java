package com.isecinc.pens.process.testconn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import util.ControlCode;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.FTPManager;

public class TestURLConnection {
   
	public static String THREAD_PENSA = "WEB_PENSSA";
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void testURLConnection(String threadName,String url){
		Connection conn = null;
		FTPManager ftpManager = null;
		EnvProperties env = EnvProperties.getInstance();
		try{
			conn = DBConnection.getInstance().getConnection();
			
			if(ControlCode.canExecuteMethod("TestURLConnection", "testURLConnection")){
				logger.info("Start Test By FTP Connection");
					
				//initial FTP Manager
				ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
			    //test connection
			    ftpManager.canConnectFTPServer();
					  
				logger.info("End Test By FTP Connection");
			}else{
				logger.info("ByPass No TestURLConnection ");
			}
			
		    processThread(conn, threadName, "success");  
		}catch(Exception e){
			logger.debug("FTP Error Cannot Connect");
			try{
			   processThread(conn, threadName, "fail");  
			}catch(Exception eee){
				eee.printStackTrace();
			}
		}finally{
			try{
				conn.close();
			}catch(Exception ee){}
		}
	}
	
	public static void testURLConnectionBK(String threadName,String url){
		Connection conn = null;
		try{
			 conn = DBConnection.getInstance().getConnection();
			 
			logger.info("Start check url:"+url);
			 boolean isInternetConnect = Utils.isInternetConnect(url);
			 logger.info("End check url["+url+"] is connected["+isInternetConnect+"]");
			 
			 if(isInternetConnect){
				 processThread(conn, threadName, "success");
			 }else{
				 processThread(conn, threadName, "fail");  
				 
				 //try connection by FTP
				 /*FTPManager ftpManager = null;
				 try{
					 logger.info("Start Test By FTP Connection");
					  EnvProperties env = EnvProperties.getInstance();
					  //initial FTP Manager
					  ftpManager = new FTPManager(env.getProperty("ftp.ip.server"), env.getProperty("ftp.username"), env.getProperty("ftp.password"));
				      //test connection
					  ftpManager.canConnectFTPServer();
					  
					  logger.info("End Test By FTP Connection");
					  
					  processThread(conn, threadName, "success");  
				 }catch(Exception ee){
					 ee.printStackTrace();
					  processThread(conn, threadName, "fail");  
				 }finally{
					 
				 }*/
			 }
		}catch(Exception e){
			e.printStackTrace();
			try{
			   processThread(conn, threadName, "fail");  
			}catch(Exception eee){
				eee.printStackTrace();
			}
		}finally{
			try{
				conn.close();
			}catch(Exception ee){}
		}
	}
	public static void processThread(String threadName,String status) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			processThread(conn, threadName, status);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			conn.close();
		}
	}
	public static void processThread(Connection conn,String threadName,String status) throws Exception {
		int r = updateStatusThread(conn, threadName, status);
		if(r==0){
			insertThread(conn, threadName, status);
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param threadName
	 * @return  success|fail
	 * @throws Exception
	 */
	public static String getStatusOfThread(Connection conn ,String threadName) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String status ="";
		try{
			String sql ="select *  from pens.c_control_thread ";
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				status = rst.getString("status");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			
		}
		return status;
	}
	
	public static int insertThread(Connection conn,String threadName,String status) throws Exception {
		Statement stmt = null;
		int r =0;
		try{
			String sql ="insert into pens.c_control_thread(thread_name,status)values('"+threadName+"','"+status+"')";
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			r = stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {}
			
		}
		return r;
	}
	
	public static int updateStatusThread(Connection conn ,String threadName,String status) throws Exception {
		Statement stmt = null;
		int r =0;
		try{
			String sql ="update pens.c_control_thread set status ='"+status+"' where thread_name ='"+threadName+"'";
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			r = stmt.executeUpdate(sql);
			logger.debug("result update:"+r);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e2) {}
		
		}
		return r;
	}
}
