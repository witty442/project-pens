package com.isecinc.pens.inf.manager.batchwork;


import java.math.BigDecimal;
import java.sql.Connection;

import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 * 
 * @author WITTY
 *
 */
public abstract class BatchWorker extends Thread {

	
	public void startTaskStatus(BigDecimal transactionId,BigDecimal monitorId) {
		updateTaskStatus(transactionId,monitorId,"0"); //Start BatchTask = 0;
	}
	
	public void endTaskStatus(BigDecimal transactionId,BigDecimal monitorId) {
		updateTaskStatus(transactionId,monitorId,"1");//finish BatchTask = 1	
	}
	
    public void updateTaskStatus(BigDecimal transactionId,BigDecimal monitorId,String status) {
    	Connection conn = null;
    	try {
           InterfaceDAO dao = new InterfaceDAO();
           conn = DBConnection.getInstance().getConnection();
           dao.updateControlStatusMonitor(conn,transactionId,monitorId,status);
        } catch (Exception e) {
        	e.printStackTrace();
        }finally{
        	try{
	        	if(conn != null){
	        		conn.close();conn=null;
	        	}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
    }
    
   
}
