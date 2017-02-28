package com.isecinc.pens.scheduler.forms;

import com.isecinc.core.web.I_Form;

public class ScheduleForm extends I_Form{

	String taskName;
	String run;
	String startHour;
	String startMinute;
	String startDate;
	String hour;
	String minute;			
	String[] day;
	String nDay;
	String nMonth;					
	String nthDay;
	String[] month;
	org.apache.struts.upload.FormFile uploadFile;
	String programId;
	String currentPage;
	String entity;
	String product;
	String everyDay;
	
	
	public String getEveryDay() {
		return everyDay;
	}
	public void setEveryDay(String everyDay) {
		this.everyDay = everyDay;
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
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	public String getTaskName() {
		return taskName;
	}
    
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
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
	public String[] getDay() {
		return day;
	}
	public void setDay(String[] day) {
		this.day = day;
	}
	public String getNDay() {
		return nDay;
	}
	public void setNDay(String day) {
		nDay = day;
	}
	public String getNMonth() {
		return nMonth;
	}
	public void setNMonth(String month) {
		nMonth = month;
	}
	public String getNthDay() {
		return nthDay;
	}
	public void setNthDay(String nthDay) {
		this.nthDay = nthDay;
	}
	public String[] getMonth() {
		return month;
	}
	public void setMonth(String[] month) {
		this.month = month;
	}
	public org.apache.struts.upload.FormFile getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(org.apache.struts.upload.FormFile uploadFile) {
		this.uploadFile = uploadFile;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	
	
	
}
