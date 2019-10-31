package com.isecinc.pens.web.batchtask;


import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;

/**
 * 
 * @author WITTY
 *
 */
public abstract class BatchTaskWorker extends Thread {
	public static Logger logger = Logger.getLogger("PENS");
	
	public void startTaskStatus(String taskName,BigDecimal transactionId,BigDecimal monitorId) {
		logger.info("startTaskStatus transactionId["+transactionId+"]monitorId["+monitorId+"]");
		updateTaskStatus(transactionId,monitorId,"0"); //Start BatchTask = 0;
		
		//control uniq task
		BatchTaskDAO dao = new BatchTaskDAO();
		dao.updateControlMonitor(transactionId,taskName);
	}
	public void startTaskStatusCaseFail(String taskName,BigDecimal transactionId,BigDecimal monitorId) {
		logger.info("startTaskStatus transactionId["+transactionId+"]monitorId["+monitorId+"]");
		//control unique task
		BatchTaskDAO dao = new BatchTaskDAO();
		dao.updateControlMonitor(transactionId,taskName);
	}
	
	public void endTaskStatus(String taskName,BigDecimal transactionId,BigDecimal monitorId) {
		logger.info("endTaskStatus transactionId["+transactionId+"]monitorId["+monitorId+"]");
		updateTaskStatus(transactionId,monitorId,"1");//finish BatchTask = 1	
		
		//control uniq task set transaction_id = 0 for run next batch
		BatchTaskDAO dao = new BatchTaskDAO();
		dao.updateControlMonitor(new BigDecimal(0),taskName);
		
		/** Stamp Monitor Task Sales**/
		//MonitorSales.monitorSales(transactionId);
	}
	public void endTaskStatusCaseFail(String taskName,BigDecimal transactionId,BigDecimal monitorId) {
		logger.info("endTaskStatus transactionId["+transactionId+"]monitorId["+monitorId+"]");
		updateTaskStatus(transactionId,monitorId,"1");//finish BatchTask = 1	
	}
	
    public void updateTaskStatus(BigDecimal transactionId,BigDecimal monitorId,String status) {
    	Connection conn = null;
    	try {
    		BatchTaskDAO dao = new BatchTaskDAO();
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
