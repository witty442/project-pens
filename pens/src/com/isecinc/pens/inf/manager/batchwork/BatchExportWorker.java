package com.isecinc.pens.inf.manager.batchwork;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.bean.MonitorBean;
import com.isecinc.pens.inf.helper.Constants;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.ExportManager;
import com.isecinc.pens.inf.manager.process.ExternalProcess;

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
		logger.debug("Start Thread:" + Thread.currentThread().getName()+"request:"+request);
		HttpServletRequest requestB;
		try {
			requestB = request;

			if(requestTable != null && !Utils.isNull(requestTable).equals("")){
				logger.debug(" **********Start Export By Request Table ******************");
				
				startTaskStatus(Constants.TYPE_EXPORT,this.transactionId,this.monitorID);
				
				(new ExportManager()).exportToTxt(transactionId,monitorID,transType,userLogin,userRequest, requestTable, request);
				
				//Stamp End task to success
				endTaskStatus(Constants.TYPE_EXPORT,this.transactionId,this.monitorID);
			}else{
				//Process Post Export
				new ExternalProcess().processExportBefore(requestB, userLogin);
				
				logger.info(" **********Start Export Master TXT ******************");
				startTaskStatus(Constants.TYPE_EXPORT,this.transactionId,this.monitorID);
				MonitorBean model = (new ExportManager()).exportToTxt(transactionId,monitorID,transType,userLogin,userRequest, requestTable, request);
				logger.info(" **********END Export Master Result ErrorCode:"+Utils.isNull(model.getErrorCode())+"*****************");
				
				if( !Utils.isNull(model.getErrorCode()).equalsIgnoreCase("FTPException")){
				    logger.info(" **********Start Export Transaction TXT ******************");
				    model =(new ExportManager()).exportTransaction(transactionId,userLogin,userRequest, requestTable, request);
				    logger.info(" **********END Export Transaction Result ErrorCode:"+Utils.isNull(model.getErrorCode())+"******************");
				}
				
				//Process Post Export
				new ExternalProcess().processExportAfter(requestB, userLogin);
				
				//Stamp Batch status to Success
				endTaskStatus(Constants.TYPE_EXPORT,this.transactionId,this.monitorID);

			}

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
