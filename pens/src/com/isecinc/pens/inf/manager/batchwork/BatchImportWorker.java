package com.isecinc.pens.inf.manager.batchwork;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ImportManager;
import com.isecinc.pens.inf.manager.UpdateSalesManager;
import com.isecinc.pens.inf.manager.WebMemberManager;

/**
 * @author WITTY
 *
 */
public class BatchImportWorker extends BatchWorker {
	protected Logger logger = Logger.getLogger("PENS");
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
			logger.debug("requestTable:"+requestTable);
			if(requestTable != null && !requestTable.isEmpty()){
				startTaskStatus(this.transactionId,this.monitorId);
				
				if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(this.transType)){
					logger.debug(" **********Start Import Update Transaction Sales By Request Table ******************");
					MonitorBean monitorModel =(new UpdateSalesManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, request, importAll);
					logger.debug(" **********Result Import Control Transaction  By Request Table :"+monitorModel.getStatus()+" ******************");
				}else if(Constants.TRANSACTION_WEB_MEMBER_TYPE.equals(this.transType)){
					logger.debug(" **********Start Import Web Member  By Request Table ******************");
					MonitorBean monitorModel =(new WebMemberManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, request, importAll);
					logger.debug(" **********Result Import Web Member  By Request Table :"+monitorModel.getStatus()+" ******************");
				}else{
					logger.debug(" **********Start Import Master ,Tranasaction  By Request Table ******************");
					MonitorBean monitorModel =(new ImportManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, request, importAll);
					logger.debug(" **********Result Import Master ,Tranasaction  By Request Table :"+monitorModel.getStatus()+" ******************");
				}	
				
				endTaskStatus(this.transactionId,this.monitorId);
			}
			
			/** Import All Transaction  **/ 
			/** 1.Import Update Sales Transaction **/
			/** 2.Import Master **/
			/** 3.Import Transaction (onhand) **/
			/** WAIT ..4.Import Web Member **/
			
			/** Pasuwat Wang-arrayagul
			 *  ReSequence Import Process
			 *  1.Import Master 
			 *  2.Import Transaction (Credit Note)
			 *  3.Import/Update Sales Transaction 
			 ** */ 
			
			else {
				logger.debug(" **********Start Import Master Table ******************");
				MonitorBean monitorModel =(new ImportManager()).importTxtByTransType(transactionId,Constants.TRANSACTION_MASTER_TYPE, userLogin,userRequest, requestTable, request, importAll);
				logger.debug(" **********Result Import Master Table :"+monitorModel.getStatus()+"******************");
				
                startTaskStatus(this.transactionId,this.monitorId);
                
				if( !Utils.isNull(monitorModel.getErrorCode()).equalsIgnoreCase("FTPException")){
					logger.debug(" **********Start Import Transaction Table ******************");
					monitorModel =(new ImportManager()).importTxtByTransType(transactionId,Constants.TRANSACTION_TRANS_TYPE, userLogin,userRequest, requestTable, request, importAll);
					logger.debug(" **********Result Import Transaction Table :"+monitorModel.getStatus()+" ******************");
					
					if(monitorModel.getStatus() == Constants.STATUS_SUCCESS){
						logger.debug(" **********Start Import Update Transaction Sales ******************");
						monitorModel =(new UpdateSalesManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, request, importAll);
						logger.debug(" **********Result Import Control Transaction :"+monitorModel.getStatus()+" ******************");
		                logger.debug("Export Import Update Transaction Sales Result ErrorCode:"+Utils.isNull(monitorModel.getErrorCode()));
					}
					
					/** WIT: Wait for requirement **/
					//logger.debug(" **********Start Import Web Member Table ******************");
					//monitorModel =(new WebMemberManager()).importTxt(userLogin,userRequest, requestTable, request, importAll,transactionId);
					//logger.debug(" **********Result Import Web Member Table  :"+monitorModel.getStatus()+" ******************");
				}
				
				endTaskStatus(this.transactionId,this.monitorId);
			} 
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
