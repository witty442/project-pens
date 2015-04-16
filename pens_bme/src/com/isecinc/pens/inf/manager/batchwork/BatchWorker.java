package com.isecinc.pens.inf.manager.batchwork;


import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.dao.InterfaceDAO;
import com.isecinc.pens.inf.helper.DBConnection;

/**
 * 
 * @author WITTY
 *
 */
public abstract class BatchWorker extends Thread {
	public static Logger logger = Logger.getLogger("PENS");
	
	public void startTaskStatus(String type,BigDecimal transactionId,BigDecimal monitorId) {
		logger.info("startTaskStatus transactionId["+transactionId+"]monitorId["+monitorId+"]");
		updateTaskStatus(transactionId,monitorId,"0"); //Start BatchTask = 0;
		
		//control uniq task
		InterfaceDAO dao = new InterfaceDAO();
		dao.updateControlMonitor(transactionId,type);
	}
	
	public void endTaskStatus(String type,BigDecimal transactionId,BigDecimal monitorId) {
		logger.info("endTaskStatus transactionId["+transactionId+"]monitorId["+monitorId+"]");
		updateTaskStatus(transactionId,monitorId,"1");//finish BatchTask = 1	
		
		//control uniq task set transaction_id = 0 for run next batch
		InterfaceDAO dao = new InterfaceDAO();
		dao.updateControlMonitor(new BigDecimal(0),type);
		
		/** Stamp Monitor Task Sales**/
		//MonitorSales.monitorSales(transactionId);
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
