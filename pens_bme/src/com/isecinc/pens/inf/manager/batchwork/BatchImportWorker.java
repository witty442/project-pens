package com.isecinc.pens.inf.manager.batchwork;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.process.ExternalProcess;
import com.pens.util.Constants;
import com.pens.util.Utils;

/**
 * @author WITTY
 *
 */
public class BatchImportWorker extends BatchWorker {
	public static Logger logger = Logger.getLogger("PENS");
	private BigDecimal reGenTransactionId;
	private BigDecimal transactionId;
	private BigDecimal monitorId;
    private String transType;
	private User userLogin;
	private User userRequest;
    private String requestTable;
    private HttpServletRequest request;
    private boolean importAll;
    
	public BatchImportWorker(BigDecimal transactionId,BigDecimal monitorId,String transType,User userLogin,User userRequest,String requestTable,HttpServletRequest request,boolean importAll) {
	   this.setTransactionId(transactionId);
	   this.setMonitorId(monitorId);
	   this.setTransType(transType);
	   this.setUserLogin(userLogin);
	   this.setUserRequest(userRequest);
	   this.setRequestTable(requestTable);
	   this.setRequest(request);
	   this.setImportAll(importAll);
	}
	
	 
	@Override
	public void run() {
		System.out.println("Start Thread:" + Thread.currentThread().getName());
		try {
			/** Process run Script Before Import **/
			new ExternalProcess().processImportBefore(request, userLogin);
			
            startTaskStatus(Constants.TYPE_IMPORT,this.transactionId,this.monitorId);
            
            logger.debug(" **********Start Import Update Transaction Sales ******************");
           
            MonitorBean monitorModel = (new ImportManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, request, importAll);
			
            logger.debug(" **********Result Import Control Transaction :"+monitorModel.getStatus()+" ******************");
			logger.debug("Import Update Transaction Sales Result ErrorCode:"+Utils.isNull(monitorModel.getErrorCode()));
            
			/** Process Run Script After Import **/
			new ExternalProcess().processImportAfter(request,userLogin);
			
			//Stamp task to Success
			endTaskStatus(Constants.TYPE_IMPORT,this.transactionId,this.monitorId);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	
	public boolean isImportAll() {
		return importAll;
	}

	public void setImportAll(boolean importAll) {
		this.importAll = importAll;
	}

	public BigDecimal getReGenTransactionId() {
		return reGenTransactionId;
	}

	public void setReGenTransactionId(BigDecimal reGenTransactionId) {
		this.reGenTransactionId = reGenTransactionId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}


	public BigDecimal getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(BigDecimal monitorId) {
		this.monitorId = monitorId;
	}

	

	public BigDecimal getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(BigDecimal transactionId) {
		this.transactionId = transactionId;
	}

	
    
	public User getUserLogin() {
		return userLogin;
	}



	public void setUserLogin(User userLogin) {
		this.userLogin = userLogin;
	}



	public User getUserRequest() {
		return userRequest;
	}



	public void setUserRequest(User userRequest) {
		this.userRequest = userRequest;
	}



	public String getRequestTable() {
		return requestTable;
	}

	public void setRequestTable(String requestTable) {
		this.requestTable = requestTable;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	
	
}
