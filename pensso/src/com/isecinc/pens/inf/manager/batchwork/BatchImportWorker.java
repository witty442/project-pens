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
import com.isecinc.pens.inf.manager.process.ExternalProcess;
import com.pens.util.ControlCode;

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
    private boolean importAll;
    
	public BatchImportWorker(BigDecimal transactionId,BigDecimal monitorId,String transType,User userLogin,User userRequest,String requestTable,boolean importAll) {
	   this.setTransactionId(transactionId);
	   this.setMonitorId(monitorId);
	   this.setTransType(transType);
	   this.setUserLogin(userLogin);
	   this.setUserRequest(userRequest);
	   this.setRequestTable(requestTable);
	   this.setImportAll(importAll);
	}
	
	@Override
	public void run() {
		System.out.println("Start Thread:" + Thread.currentThread().getName());
		try {
			logger.debug("requestTable:"+requestTable);
			if(requestTable != null && !requestTable.isEmpty()){
				startTaskStatus(Constants.TYPE_IMPORT,this.transactionId,this.monitorId);
				
				if(Constants.TRANSACTION_UTS_TRANS_TYPE.equals(this.transType)){
					logger.debug(" **********Start Import Update Transaction Sales By Request Table ******************");
					MonitorBean monitorModel =(new UpdateSalesManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, importAll);
					logger.debug(" **********Result Import Control Transaction  By Request Table :"+monitorModel.getStatus()+" ******************");
				}else{
					logger.debug(" **********Start Import Master ,Tranasaction  By Request Table ******************");
					MonitorBean monitorModel =(new ImportManager()).importFileToDB(transactionId,monitorId,transType, userLogin,userRequest, requestTable, importAll);
					logger.debug(" **********Result Import Master ,Tranasaction  By Request Table :"+monitorModel.getStatus()+" ******************");
				}	
			
				//Stamp Task to Success
				endTaskStatus(Constants.TYPE_IMPORT,this.transactionId,this.monitorId);
			}
			
			/** Import All Transaction  **/ 
			/*  1.Import/Update Sales Transaction 
			 ** */ 
			
			else {
				
				/** Process run Script Before Import **/
				//new ExternalProcess().processImportBefore(request, userLogin);

				logger.debug(" **********Start Import Update Transaction Sales ******************");
					
				MonitorBean monitorModel =(new ImportManager()).importTxtByUpdateSalesType(transactionId,Constants.TRANSACTION_UTS_TRANS_TYPE, userLogin,userRequest, requestTable, importAll);
				logger.debug(" **********Result Import Control Transaction :"+monitorModel.getStatus()+" ******************");
	            logger.debug("Export Import Update Transaction Sales Result ErrorCode:"+Utils.isNull(monitorModel.getErrorCode()));
	
				/** Process Run Script After Import **/
				//new ExternalProcess().processImportAfter(request,userLogin);
				
				//Stamp task to Success
				endTaskStatus(Constants.TYPE_IMPORT,this.transactionId,this.monitorId);
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

}
