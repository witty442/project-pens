/*
 * Created on Jun 30, 2005
 *
 */
package com.isecinc.pens.scheduler.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Witty
 * 
 */
public class BatchTaskDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2191836144668901756L;
	private BigDecimal no;
	private BigDecimal id;
	private String name;
	private String status;
	private Date createDate;
	private String module;
	private String asOfDate;
	private BigDecimal scheduleRefId;
	private String type;
	private String programId;
	private Date updateDate;
	private Date executeDate;
	private String batchDateTime;
	private String noOfRecord;
	private String sizeOfFile;
	private String lastRunDate;
	private String nextRunDate;
	private String cencel;
    private String sourcePath;
    private String destPath;
    private String fileName;
    private String message;
    private String paramRegen;
	private BigDecimal transactionId;
    
	
	public BigDecimal getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(BigDecimal transactionId) {
		this.transactionId = transactionId;
	}
	public String getParamRegen() {
		return paramRegen;
	}
	public void setParamRegen(String paramRegen) {
		this.paramRegen = paramRegen;
	}
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getLastRunDate() {
		return lastRunDate;
	}
	public void setLastRunDate(String lastRunDate) {
		this.lastRunDate = lastRunDate;
	}
	public String getNextRunDate() {
		return nextRunDate;
	}
	public void setNextRunDate(String nextRunDate) {
		this.nextRunDate = nextRunDate;
	}
	public String getCencel() {
		return cencel;
	}
	public void setCencel(String cencel) {
		this.cencel = cencel;
	}
	
	public String getNoOfRecord() {
		return noOfRecord;
	}
	public void setNoOfRecord(String noOfRecord) {
		this.noOfRecord = noOfRecord;
	}
	public String getSizeOfFile() {
		return sizeOfFile;
	}
	public void setSizeOfFile(String sizeOfFile) {
		this.sizeOfFile = sizeOfFile;
	}
	
	public BigDecimal getNo() {
		return no;
	}
	public void setNo(BigDecimal no) {
		this.no = no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	
	public String getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getBatchDateTime() {
		return batchDateTime;
	}
	public void setBatchDateTime(String batchDateTime) {
		this.batchDateTime = batchDateTime;
	}
	public BigDecimal getScheduleRefId() {
		return scheduleRefId;
	}
	public void setScheduleRefId(BigDecimal scheduleRefId) {
		this.scheduleRefId = scheduleRefId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getExecuteDate() {
		return executeDate;
	}
	public void setExecuteDate(Date executeDate) {
		this.executeDate = executeDate;
	}
	
    
}
