package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;


public class SAEmpBean implements Serializable{

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
	private String mobile;
	private String startDate;
	private String endDate;

	//sa only
	private String branch;
	private String groupStore;
	private String oracleRefId;
	private String email;
	private String bankAccount;
	private String idCard;
	private String leaveDate;
	private String leaveReason;
	private String rewardBme;
	private String startRewardBmeDate;
	private String rewardWacoal;
	private String startRewardWacoalDate;
	private String suretyBond	;
	private String startSuretyBondDate;
	

	
	private String createUser;
	private String updateUser;
	private List<SAEmpBean> items;
	private String disableTextClass;

	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	
	
    
	public String getDisableTextClass() {
		return disableTextClass;
	}
	public void setDisableTextClass(String disableTextClass) {
		this.disableTextClass = disableTextClass;
	}
	public String getRewardBme() {
		return rewardBme;
	}
	public void setRewardBme(String rewardBme) {
		this.rewardBme = rewardBme;
	}
	public String getStartRewardBmeDate() {
		return startRewardBmeDate;
	}
	public void setStartRewardBmeDate(String startRewardBmeDate) {
		this.startRewardBmeDate = startRewardBmeDate;
	}
	public String getRewardWacoal() {
		return rewardWacoal;
	}
	public void setRewardWacoal(String rewardWacoal) {
		this.rewardWacoal = rewardWacoal;
	}
	public String getStartRewardWacoalDate() {
		return startRewardWacoalDate;
	}
	public void setStartRewardWacoalDate(String startRewardWacoalDate) {
		this.startRewardWacoalDate = startRewardWacoalDate;
	}
	public String getSuretyBond() {
		return suretyBond;
	}
	public void setSuretyBond(String suretyBond) {
		this.suretyBond = suretyBond;
	}
	public String getStartSuretyBondDate() {
		return startSuretyBondDate;
	}
	public void setStartSuretyBondDate(String startSuretyBondDate) {
		this.startSuretyBondDate = startSuretyBondDate;
	}
	public String getLeaveDate() {
		return leaveDate;
	}
	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}
	public String getLeaveReason() {
		return leaveReason;
	}
	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getOracleRefId() {
		return oracleRefId;
	}
	public void setOracleRefId(String oracleRefId) {
		this.oracleRefId = oracleRefId;
	}
	public String getGroupStore() {
		return groupStore;
	}
	public void setGroupStore(String groupStore) {
		this.groupStore = groupStore;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
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
	public List<SAEmpBean> getItems() {
		return items;
	}
	public void setItems(List<SAEmpBean> items) {
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
