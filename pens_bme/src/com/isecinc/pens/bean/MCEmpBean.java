package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;


public class MCEmpBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2984706968964245517L;
	private int lineId;
	private int no;
	//criteria
	private String empIdFrom;
	private String empIdTo;
	
	private String empRefId;
	private String orgEmpId;
	private String empId;
	private String empType;
	private String empTypeDesc;
	private String title;
	private String region;
	private String regionDesc;
	private String name;
	private String surName;
	private String mobile1;
	private String mobile2;
	private String startDate;
	private String endDate;
	private String note;
	private String reasonLeave;
	private String reasonLeaveDesc;
	private String status;
	private String statusDesc;
	
	private String createUser;
	private String updateUser;
	private List<MCEmpBean> items;

	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	
	
	public String getReasonLeaveDesc() {
		return reasonLeaveDesc;
	}
	public void setReasonLeaveDesc(String reasonLeaveDesc) {
		this.reasonLeaveDesc = reasonLeaveDesc;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getEmpTypeDesc() {
		return empTypeDesc;
	}
	public void setEmpTypeDesc(String empTypeDesc) {
		this.empTypeDesc = empTypeDesc;
	}
	public String getRegionDesc() {
		return regionDesc;
	}
	public void setRegionDesc(String regionDesc) {
		this.regionDesc = regionDesc;
	}
	public String getEmpRefId() {
		return empRefId;
	}
	public void setEmpRefId(String empRefId) {
		this.empRefId = empRefId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getEmpIdFrom() {
		return empIdFrom;
	}
	public void setEmpIdFrom(String empIdFrom) {
		this.empIdFrom = empIdFrom;
	}
	public String getEmpIdTo() {
		return empIdTo;
	}
	public void setEmpIdTo(String empIdTo) {
		this.empIdTo = empIdTo;
	}
	public String getOrgEmpId() {
		return orgEmpId;
	}
	public void setOrgEmpId(String orgEmpId) {
		this.orgEmpId = orgEmpId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getEmpType() {
		return empType;
	}
	public void setEmpType(String empType) {
		this.empType = empType;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public String getMobile1() {
		return mobile1;
	}
	public void setMobile1(String mobile1) {
		this.mobile1 = mobile1;
	}
	public String getMobile2() {
		return mobile2;
	}
	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getReasonLeave() {
		return reasonLeave;
	}
	public void setReasonLeave(String reasonLeave) {
		this.reasonLeave = reasonLeave;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public List<MCEmpBean> getItems() {
		return items;
	}
	public void setItems(List<MCEmpBean> items) {
		this.items = items;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
}
