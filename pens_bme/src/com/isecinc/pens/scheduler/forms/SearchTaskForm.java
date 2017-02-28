package com.isecinc.pens.scheduler.forms;

import org.apache.struts.action.ActionForm;

public class SearchTaskForm extends ActionForm{
	String programId;          
    String searchId;            
	String searchTaskName;      
	String searchCreateDateFrom;
	String searchCreateDateTo;
	String searchAsOfDateFrom;  
	String searchAsOfDateTo;    
	String[] searchStatus;    
	String[] searchType;   
	String searchTimeFromHour;
	String searchTimeFromMinute;
	String searchTimeToHour;    
	String searchTimeToMinute; 
	String searchReturnCode; 
	String tranDateSchFrom;    
	String tranDateSchTo;       
	String entity;          
	String product;         
	String currentPage;
	
	
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	public String getSearchTaskName() {
		return searchTaskName;
	}
	public void setSearchTaskName(String searchTaskName) {
		this.searchTaskName = searchTaskName;
	}
	public String getSearchCreateDateFrom() {
		return searchCreateDateFrom;
	}
	public void setSearchCreateDateFrom(String searchCreateDateFrom) {
		this.searchCreateDateFrom = searchCreateDateFrom;
	}
	public String getSearchCreateDateTo() {
		return searchCreateDateTo;
	}
	public void setSearchCreateDateTo(String searchCreateDateTo) {
		this.searchCreateDateTo = searchCreateDateTo;
	}
	public String getSearchAsOfDateFrom() {
		return searchAsOfDateFrom;
	}
	public void setSearchAsOfDateFrom(String searchAsOfDateFrom) {
		this.searchAsOfDateFrom = searchAsOfDateFrom;
	}
	public String getSearchAsOfDateTo() {
		return searchAsOfDateTo;
	}
	public void setSearchAsOfDateTo(String searchAsOfDateTo) {
		this.searchAsOfDateTo = searchAsOfDateTo;
	}
	public String[] getSearchStatus() {
		return searchStatus;
	}
	public void setSearchStatus(String[] searchStatus) {
		this.searchStatus = searchStatus;
	}
	public String[] getSearchType() {
		return searchType;
	}
	public void setSearchType(String[] searchType) {
		this.searchType = searchType;
	}
	public String getSearchTimeFromHour() {
		return searchTimeFromHour;
	}
	public void setSearchTimeFromHour(String searchTimeFromHour) {
		this.searchTimeFromHour = searchTimeFromHour;
	}
	public String getSearchTimeFromMinute() {
		return searchTimeFromMinute;
	}
	public void setSearchTimeFromMinute(String searchTimeFromMinute) {
		this.searchTimeFromMinute = searchTimeFromMinute;
	}
	public String getSearchTimeToHour() {
		return searchTimeToHour;
	}
	public void setSearchTimeToHour(String searchTimeToHour) {
		this.searchTimeToHour = searchTimeToHour;
	}
	public String getSearchTimeToMinute() {
		return searchTimeToMinute;
	}
	public void setSearchTimeToMinute(String searchTimeToMinute) {
		this.searchTimeToMinute = searchTimeToMinute;
	}
	public String getSearchReturnCode() {
		return searchReturnCode;
	}
	public void setSearchReturnCode(String searchReturnCode) {
		this.searchReturnCode = searchReturnCode;
	}
	public String getTranDateSchFrom() {
		return tranDateSchFrom;
	}
	public void setTranDateSchFrom(String tranDateSchFrom) {
		this.tranDateSchFrom = tranDateSchFrom;
	}
	public String getTranDateSchTo() {
		return tranDateSchTo;
	}
	public void setTranDateSchTo(String tranDateSchTo) {
		this.tranDateSchTo = tranDateSchTo;
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
	
	

	
}
