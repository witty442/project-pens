package com.isecinc.pens.scheduler.manager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


public class ScheduleVO implements Serializable{
    
	
	private String programId ;
	private String programName;
	private String groupId;
    private BigDecimal no;
	private String jobId;
	private String status ="";
	private String type;
    private String startHour;
    private String startMinute;
    private String startDate;
    private String hour;
    private String minute ;
    private String nDay ;
    private String[] days;
	private String createUser;
	private String updateUser;
    private String entity;
    private String product;
    private String sizeOfFile;
    private String noOfRecord;
    private String batchDate;
	private String userId;
	private Date lastRunDate;
	private Date nextRunDate;
	private String crontriggerExp;
    private String everyDay;
    private String localPath;
    private String sourcePath;
    private String destPath;
    private String fileName;
    private String message;
    private String paramRegen;
    private Map<String, String> paramMap;
    
    
    
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	public String getParamRegen() {
		return paramRegen;
	}
	public void setParamRegen(String paramRegen) {
		this.paramRegen = paramRegen;
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
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getEveryDay() {
		return everyDay;
	}
	public void setEveryDay(String everyDay) {
		this.everyDay = everyDay;
	}
	public Date getLastRunDate() {
		return lastRunDate;
	}
	public void setLastRunDate(Date lastRunDate) {
		this.lastRunDate = lastRunDate;
	}
	public Date getNextRunDate() {
		return nextRunDate;
	}
	public void setNextRunDate(Date nextRunDate) {
		this.nextRunDate = nextRunDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getSizeOfFile() {
		return sizeOfFile;
	}
	public void setSizeOfFile(String sizeOfFile) {
		this.sizeOfFile = sizeOfFile;
	}
	public String getNoOfRecord() {
		return noOfRecord;
	}
	public void setNoOfRecord(String noOfRecord) {
		this.noOfRecord = noOfRecord;
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
	public String getBatchDate() {
		return batchDate;
	}
	public void setBatchDate(String batchDate) {
		this.batchDate = batchDate;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public BigDecimal getNo() {
		return no;
	}
	public void setNo(BigDecimal no) {
		this.no = no;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartHour() {
		return startHour;
	}
	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}
	public String getStartMinute() {
		return startMinute;
	}
	public void setStartMinute(String startMinute) {
		this.startMinute = startMinute;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getCrontriggerExp() {
		return crontriggerExp;
	}
	public void setCrontriggerExp(String crontriggerExp) {
		this.crontriggerExp = crontriggerExp;
	}
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public String getNDay() {
		return nDay;
	}
	public void setNDay(String day) {
		nDay = day;
	}
	public String[] getDays() {
		return days;
	}
	public void setDays(String[] days) {
		this.days = days;
	}
	
	
	
}
