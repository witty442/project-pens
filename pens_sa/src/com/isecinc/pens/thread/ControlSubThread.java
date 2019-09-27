package com.isecinc.pens.thread;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.Utils;


public class ControlSubThread {
   
	public static String THREAD_PENSA = "WEB_PENSSA";
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void startThread(String threadName,String userName){
		Connection conn = null;
		try{
			 conn = DBConnection.getInstance().getConnection();
			 
			/* System.out.println("Start check url:"+url);
			 boolean isInternetConnect = Utils.isInternetConnect(url);
			 System.out.println("End check url["+url+"] is connected["+isInternetConnect+"]");*/
			 
			 if(true){
				 processThread(conn, threadName, "success");
			 }else{
				 processThread(conn, threadName, "fail");  
			 }
		}catch(Exception e){
			e.printStackTrace();
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
