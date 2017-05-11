package com.isecinc.pens.inf.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts.upload.FormFile;

import com.isecinc.pens.bean.User;

/**
 * @author WITTY
 *
 */

public class MonitorBean implements Serializable{

private static final long serialVersionUID = -990650039740579753L;
   private boolean checked;
   private   BigDecimal transactionId;
   private   BigDecimal monitorId;
   private   BigDecimal monitorItemId;
   private   String name ;
   private   String type ;
   private   String channel ;
   private   String saleRepCode ;
   private   String transactionType ;
   private   Date submitDate ;
   private   int status ;
   private   int batchTaskStatus ;
   private   Date createDate ;
   private   String createUser ;
   private   Date updateDate ;
   private   String updateUser  ;
   private   int fileCount;
   private  int successCount;
   private String errorCode;
   private String errorMsg;
   private String timeInUse;
   private String statusDesc;
   
   private MonitorItemBean monitorItemBean;
   private List monitorItemList;
  
	/** Optional For Search **/
	private String userName;
	private String submitDateFrom;
	private String submitDateTo;
	private String requestTable;
	private String requestExportTable;
	private boolean importAll;
	private String requestUpdateSalesTable;
	private String requestImportUserName;
	private String requestImportUpdateUserName;
	private String requestExportUserName;
	
	private String requestWebMemberTable;
	private String requestImportWebMemberUserName;

    /** Parameter for Run Batch **/
	private Map<String, String> batchParamMap;
	private Map<String, Object> batchParamMapObj;
    private FormFile dataFile;
	private User user;
    
	
   public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
public Map<String, Object> getBatchParamMapObj() {
		return batchParamMapObj;
	}
	public void setBatchParamMapObj(Map<String, Object> batchParamMapObj) {
		this.batchParamMapObj = batchParamMapObj;
	}
public FormFile getDataFile() {
		return dataFile;
	}
	public void setDataFile(FormFile dataFile) {
		this.dataFile = dataFile;
	}
public BigDecimal getMonitorItemId() {
		return monitorItemId;
	}
	public void setMonitorItemId(BigDecimal monitorItemId) {
		this.monitorItemId = monitorItemId;
	}
public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
public Map<String, String> getBatchParamMap() {
		return batchParamMap;
	}
	public void setBatchParamMap(Map<String, String> batchParamMap) {
		this.batchParamMap = batchParamMap;
	}
public String getTimeInUse() {
	return timeInUse;
}
public void setTimeInUse(String timeInUse) {
	this.timeInUse = timeInUse;
}
public int getBatchTaskStatus() {
	return batchTaskStatus;
}
public void setBatchTaskStatus(int batchTaskStatus) {
	this.batchTaskStatus = batchTaskStatus;
}
public String getRequestWebMemberTable() {
	return requestWebMemberTable;
}
public void setRequestWebMemberTable(String requestWebMemberTable) {
	this.requestWebMemberTable = requestWebMemberTable;
}
public String getRequestImportWebMemberUserName() {
	return requestImportWebMemberUserName;
}
public void setRequestImportWebMemberUserName(
		String requestImportWebMemberUserName) {
	this.requestImportWebMemberUserName = requestImportWebMemberUserName;
}
public String getRequestExportUserName() {
		return requestExportUserName;
	}
	public void setRequestExportUserName(String requestExportUserName) {
		this.requestExportUserName = requestExportUserName;
	}

public String getRequestImportUserName() {
		return requestImportUserName;
	}
	public void setRequestImportUserName(String requestImportUserName) {
		this.requestImportUserName = requestImportUserName;
	}
	public String getRequestImportUpdateUserName() {
		return requestImportUpdateUserName;
	}
	public void setRequestImportUpdateUserName(String requestImportUpdateUserName) {
		this.requestImportUpdateUserName = requestImportUpdateUserName;
	}
public String getRequestUpdateSalesTable() {
		return requestUpdateSalesTable;
	}
	public void setRequestUpdateSalesTable(String requestUpdateSalesTable) {
		this.requestUpdateSalesTable = requestUpdateSalesTable;
	}
public String getRequestExportTable() {
		return requestExportTable;
	}
	public void setRequestExportTable(String requestExportTable) {
		this.requestExportTable = requestExportTable;
	}
public boolean isImportAll() {
		return importAll;
	}
	public void setImportAll(boolean importAll) {
		this.importAll = importAll;
	}
public boolean isChecked() {
	return checked;
}
public void setChecked(boolean checked) {
	this.checked = checked;
}
public BigDecimal getTransactionId() {
	return transactionId;
}
public void setTransactionId(BigDecimal transactionId) {
	this.transactionId = transactionId;
}
public String getErrorCode() {
	return errorCode;
}
public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
}

public String getErrorMsg() {
	return errorMsg;
}
public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
}
public int getSuccessCount() {
	return successCount;
}
public void setSuccessCount(int successCount) {
	this.successCount = successCount;
}
public int getFileCount() {
	return fileCount;
}
public void setFileCount(int fileCount) {
	this.fileCount = fileCount;
}
public MonitorItemBean getMonitorItemBean() {
	return monitorItemBean;
}
public void setMonitorItemBean(MonitorItemBean monitorItemBean) {
	this.monitorItemBean = monitorItemBean;
}
public List getMonitorItemList() {
	return monitorItemList;
}
public void setMonitorItemList(List monitorItemList) {
	this.monitorItemList = monitorItemList;
}
public BigDecimal getMonitorId() {
	return monitorId;
}
public void setMonitorId(BigDecimal monitorId) {
	this.monitorId = monitorId;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getChannel() {
	return channel;
}
public void setChannel(String channel) {
	this.channel = channel;
}
public String getSaleRepCode() {
	return saleRepCode;
}
public void setSaleRepCode(String saleRepCode) {
	this.saleRepCode = saleRepCode;
}
public String getTransactionType() {
	return transactionType;
}
public void setTransactionType(String transactionType) {
	this.transactionType = transactionType;
}
public Date getSubmitDate() {
	return submitDate;
}
public void setSubmitDate(Date submitDate) {
	this.submitDate = submitDate;
}

public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}
public Date getCreateDate() {
	return createDate;
}
public void setCreateDate(Date createDate) {
	this.createDate = createDate;
}
public String getCreateUser() {
	return createUser;
}
public void setCreateUser(String createUser) {
	this.createUser = createUser;
}
public Date getUpdateDate() {
	return updateDate;
}
public void setUpdateDate(Date updateDate) {
	this.updateDate = updateDate;
}
public String getUpdateUser() {
	return updateUser;
}
public void setUpdateUser(String updateUser) {
	this.updateUser = updateUser;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getSubmitDateFrom() {
	return submitDateFrom;
}
public void setSubmitDateFrom(String submitDateFrom) {
	this.submitDateFrom = submitDateFrom;
}
public String getSubmitDateTo() {
	return submitDateTo;
}
public void setSubmitDateTo(String submitDateTo) {
	this.submitDateTo = submitDateTo;
}
public String getRequestTable() {
	return requestTable;
}
public void setRequestTable(String requestTable) {
	this.requestTable = requestTable;
}

   
	  
}
