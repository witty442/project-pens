package com.isecinc.pens.inf.manager.batchwork;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.db.backup.DBBackUpManager;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ExportManager;

/*
 * thread to run MasterImport
 */
public class BatchExportWorker extends BatchWorker {
	public static Logger logger = Logger.getLogger("PENS");
	private BigDecimal transactionId;
	private BigDecimal monitorID;
    private String transType;
	private User userLogin;
	private User userRequest;
    private String requestTable;
    private HttpServletRequest request;
    
	public BatchExportWorker(BigDecimal transactionId,BigDecimal monitorID,String transType,User userLogin,User userRequest,String requestTable,HttpServletRequest request) {
	   this.setTransactionId(transactionId);
	   this.setMonitorID(monitorID);
	   this.setTransType(transType);
	   this.setUserLogin(userLogin);
	   this.setUserRequest(userRequest);
	   this.setRequestTable(requestTable);
	   this.setRequest(request);
	}

	@Override
	public void run() {
		logger.debug("Start Thread:" + Thread.currentThread().getName());
		HttpServletRequest requestB;
		User userB = null;
		try {
			requestB = request;
			userB =(User)request.getSession().getAttribute("user");
			
			if(requestTable != null && !Utils.isNull(requestTable).equals("")){
				logger.debug(" **********Start Export By Request Table ******************");
				startTaskStatus(this.transactionId,this.monitorID);
				(new ExportManager()).exportToTxt(transactionId,monitorID,transType,userLogin,userRequest, requestTable, request);
				endTaskStatus(this.transactionId,this.monitorID);
			}else{
				logger.debug(" **********Start Export Master TXT ******************");
				startTaskStatus(this.transactionId,this.monitorID);
				MonitorBean model = (new ExportManager()).exportToTxt(transactionId,monitorID,transType,userLogin,userRequest, requestTable, request);
				logger.debug("Export Master Result ErrorCode:"+Utils.isNull(model.getErrorCode()));
				
				if( !Utils.isNull(model.getErrorCode()).equalsIgnoreCase("FTPException")){
				    logger.debug(" **********Start Export Transaction TXT ******************");
				    model =(new ExportManager()).exportTransaction(transactionId,userLogin,userRequest, requestTable, request);
				    logger.debug("Export Transaction Result ErrorCode:"+Utils.isNull(model.getErrorCode()));
				}
				endTaskStatus(this.transactionId,this.monitorID);
					
			}

			//DB BackUp DB and Transafer TO Ftp Server
			DBBackUpManager.processToLocal(requestB,userB);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
    
    
	public BigDecimal getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(BigDecimal transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public BigDecimal getMonitorID() {
		return monitorID;
	}

	public void setMonitorID(BigDecimal monitorID) {
		this.monitorID = monitorID;
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
