package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;


public class SADamageBean implements Serializable{

	private static final long serialVersionUID = -3452177400451463302L;
	
	private String id;
	private String empId;
	private String type;
	private String invRefwal;
	
	private String tranDate;
	private String oracleRefId;
	private String oracleRefName;
	private String name;
	private String surname;
	private String fullName;
	private String groupStore;
	private String branch;
	private String checkStockDate;
	private String totalDamage;
	private String remark;

	//Line
	private String lineId	;
	private String payType	;
	private String payDate;
	private String payAmt;
	
	//optional
	private String createUser;
	private String updateUser;
	private List<SADamageBean> items;
	private String disableTextClass;
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getOracleRefId() {
		return oracleRefId;
	}
	public void setOracleRefId(String oracleRefId) {
		this.oracleRefId = oracleRefId;
	}
	public String getOracleRefName() {
		return oracleRefName;
	}
	public void setOracleRefName(String oracleRefName) {
		this.oracleRefName = oracleRefName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
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
	public String getCheckStockDate() {
		return checkStockDate;
	}
	public void setCheckStockDate(String checkStockDate) {
		this.checkStockDate = checkStockDate;
	}
	public String getTotalDamage() {
		return totalDamage;
	}
	public void setTotalDamage(String totalDamage) {
		this.totalDamage = totalDamage;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInvRefwal() {
		return invRefwal;
	}
	public void setInvRefwal(String invRefwal) {
		this.invRefwal = invRefwal;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	public String getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(String payAmt) {
		this.payAmt = payAmt;
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
	public List<SADamageBean> getItems() {
		return items;
	}
	public void setItems(List<SADamageBean> items) {
		this.items = items;
	}
	public String getDisableTextClass() {
		return disableTextClass;
	}
	public void setDisableTextClass(String disableTextClass) {
		this.disableTextClass = disableTextClass;
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
