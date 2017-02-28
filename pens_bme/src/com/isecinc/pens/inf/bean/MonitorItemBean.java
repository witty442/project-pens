package com.isecinc.pens.inf.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author WITTY
 *
 */



public class MonitorItemBean implements Serializable{
   /**
	 * 
	 */
   private static final long serialVersionUID = -5543198782059403439L;
   private int row;
   private   BigDecimal transactionId;
   private   BigDecimal id;
   private   BigDecimal monitorId;
   private   String tableName;
   private   String fileName;
   private   Date submitDate ;
   private   int status ;
   private String statusDesc;
   private   String errorMsg ;
   private   String errorCode ;
   private String source;
   private String destination;
   private int dataCount ;
   private int successCount ;
   private int failCount ;
   private int totalQty;
   private String fileSize;
   private String groupName;
   private StringBuffer outputFile;   
   private List<MonitorItemResultBean> successList;
   private List<MonitorItemResultBean> failList;
   private List<MonitorItemResultBean> salesInList;
   private List<MonitorItemResultBean> returnList;
   
   
   
public List<MonitorItemResultBean> getSalesInList() {
	return salesInList;
}
public void setSalesInList(List<MonitorItemResultBean> salesInList) {
	this.salesInList = salesInList;
}
public List<MonitorItemResultBean> getReturnList() {
	return returnList;
}
public void setReturnList(List<MonitorItemResultBean> returnList) {
	this.returnList = returnList;
}
public BigDecimal getTransactionId() {
	return transactionId;
}
public void setTransactionId(BigDecimal transactionId) {
	this.transactionId = transactionId;
}
public int getTotalQty() {
	return totalQty;
}
public void setTotalQty(int totalQty) {
	this.totalQty = totalQty;
}
public String getStatusDesc() {
	return statusDesc;
}
public void setStatusDesc(String statusDesc) {
	this.statusDesc = statusDesc;
}
public int getRow() {
	return row;
}
public void setRow(int row) {
	this.row = row;
}
public List<MonitorItemResultBean> getSuccessList() {
	return successList;
}
public void setSuccessList(List<MonitorItemResultBean> successList) {
	this.successList = successList;
}
public List<MonitorItemResultBean> getFailList() {
	return failList;
}
public void setFailList(List<MonitorItemResultBean> failList) {
	this.failList = failList;
}
public StringBuffer getOutputFile() {
	return outputFile;
}
public void setOutputFile(StringBuffer outputFile) {
	this.outputFile = outputFile;
}
public int getFailCount() {
	return failCount;
}
public void setFailCount(int failCount) {
	this.failCount = failCount;
}
public String getErrorCode() {
	return errorCode;
}
public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
}
public int getSuccessCount() {
	return successCount;
}
public void setSuccessCount(int successCount) {
	this.successCount = successCount;
}
public String getGroupName() {
	return groupName;
}
public void setGroupName(String groupName) {
	this.groupName = groupName;
}
public BigDecimal getId() {
	return id;
}
public void setId(BigDecimal id) {
	this.id = id;
}
public BigDecimal getMonitorId() {
	return monitorId;
}
public void setMonitorId(BigDecimal monitorId) {
	this.monitorId = monitorId;
}
public String getTableName() {
	return tableName;
}
public void setTableName(String tableName) {
	this.tableName = tableName;
}
public String getFileName() {
	return fileName;
}
public void setFileName(String fileName) {
	this.fileName = fileName;
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
public String getErrorMsg() {
	return errorMsg;
}
public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
}
public String getSource() {
	return source;
}
public void setSource(String source) {
	this.source = source;
}
public String getDestination() {
	return destination;
}
public void setDestination(String destination) {
	this.destination = destination;
}
public int getDataCount() {
	return dataCount;
}
public void setDataCount(int dataCount) {
	this.dataCount = dataCount;
}
public String getFileSize() {
	return fileSize;
}
public void setFileSize(String fileSize) {
	this.fileSize = fileSize;
}
   
   
	  
}
