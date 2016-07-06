package com.isecinc.pens.bean;

import java.io.Serializable;

public class BMEControlBean implements Serializable{
	
 /**
	 * 
	 */
private static final long serialVersionUID = 1L;

 private String caseType;
 private String startDate;
 private String endDate;
 private String yearMonth;
 
 
public String getYearMonth() {
	return yearMonth;
}
public void setYearMonth(String yearMonth) {
	this.yearMonth = yearMonth;
}
public String getCaseType() {
	return caseType;
}
public void setCaseType(String caseType) {
	this.caseType = caseType;
}
public String getStartDate() {
	return startDate;
}
public void setStartDate(String startDate) {
	this.startDate = startDate;
}
public String getEndDate() {
	return endDate;
}
public void setEndDate(String endDate) {
	this.endDate = endDate;
}
 
 
}
