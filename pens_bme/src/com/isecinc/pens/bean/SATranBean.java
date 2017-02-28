package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;


public class SATranBean implements Serializable{

	private static final long serialVersionUID = -3452177400451463302L;
	
	private String empId;
	private String yearMonth;
	
	private String lineId	;
	
	private String region;
	private String regionDesc;
	private String type;
	private String name;
	private String surname;
	private String fullName;
	private String groupStore;
	private String branch;
	private String remark;
	private String oracleRefId;

	//Line
	private String payDate;
	private String countStockDate;
	private String amt;
	private boolean canChange;
	private boolean isExistDB;
	private boolean used;
	
	//optional
	private String createUser;
	private String updateUser;
	private List<SATranBean> items;
	private String disableTextClass;
	private boolean canEdit = false;
	private boolean canCancel = false;
	private String mode;
	private String startBmeYearMonth;
	private String startWacoalYearMonth;
	/** 4Report **/
	private String bmeAmt;
	private String wacoalAmt;
	private String bmePayDate;
	private String wacoalPayDate;
	private String bmeCountStockDate;
	private String wacoalCountStockDate;
	
	
	public String getBmeCountStockDate() {
		return bmeCountStockDate;
	}
	public void setBmeCountStockDate(String bmeCountStockDate) {
		this.bmeCountStockDate = bmeCountStockDate;
	}
	public String getWacoalCountStockDate() {
		return wacoalCountStockDate;
	}
	public void setWacoalCountStockDate(String wacoalCountStockDate) {
		this.wacoalCountStockDate = wacoalCountStockDate;
	}
	public String getBmePayDate() {
		return bmePayDate;
	}
	public void setBmePayDate(String bmePayDate) {
		this.bmePayDate = bmePayDate;
	}
	public String getWacoalPayDate() {
		return wacoalPayDate;
	}
	public void setWacoalPayDate(String wacoalPayDate) {
		this.wacoalPayDate = wacoalPayDate;
	}
	public String getBmeAmt() {
		return bmeAmt;
	}
	public void setBmeAmt(String bmeAmt) {
		this.bmeAmt = bmeAmt;
	}
	public String getWacoalAmt() {
		return wacoalAmt;
	}
	public void setWacoalAmt(String wacoalAmt) {
		this.wacoalAmt = wacoalAmt;
	}
	public boolean isUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public String getCountStockDate() {
		return countStockDate;
	}
	public void setCountStockDate(String countStockDate) {
		this.countStockDate = countStockDate;
	}
	public String getStartBmeYearMonth() {
		return startBmeYearMonth;
	}
	public void setStartBmeYearMonth(String startBmeYearMonth) {
		this.startBmeYearMonth = startBmeYearMonth;
	}
	public String getStartWacoalYearMonth() {
		return startWacoalYearMonth;
	}
	public void setStartWacoalYearMonth(String startWacoalYearMonth) {
		this.startWacoalYearMonth = startWacoalYearMonth;
	}
	public boolean isExistDB() {
		return isExistDB;
	}
	public void setExistDB(boolean isExistDB) {
		this.isExistDB = isExistDB;
	}
	public boolean isCanChange() {
		return canChange;
	}
	public void setCanChange(boolean canChange) {
		this.canChange = canChange;
	}
	public String getRegionDesc() {
		return regionDesc;
	}
	public void setRegionDesc(String regionDesc) {
		this.regionDesc = regionDesc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOracleRefId() {
		return oracleRefId;
	}
	public void setOracleRefId(String oracleRefId) {
		this.oracleRefId = oracleRefId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
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
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	public String getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
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
	public List<SATranBean> getItems() {
		return items;
	}
	public void setItems(List<SATranBean> items) {
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
