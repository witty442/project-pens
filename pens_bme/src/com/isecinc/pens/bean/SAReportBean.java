package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;



public class SAReportBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6502539679062998657L;
	private String empId;
	private String month;
	private String year;
	private String lineId;
	private String region;
	private String regionDesc;
	private String groupStore;
	private String branch;
	private String name;
	private String surname;
	private String type;
	private String fullName;
	private String oracleRefId;
	private String invRefwal;
	
	private String tranDate;
	private String payType;
	private String payDate;
	private String payAmt;
	private String delayPayAmt;
    private String totalDamage;
    
    private String asOfMonth;
    private String suretyBondAmt;
    private String rewardAmt;
    private String netDamageAmt;
    private String netSuretyBondAmt;
    
    private List<SAReportBean> items;
    private String totalPayment;
    private String totalDelayPayment;
    
    
	public String getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(String totalPayment) {
		this.totalPayment = totalPayment;
	}
	public String getTotalDelayPayment() {
		return totalDelayPayment;
	}
	public void setTotalDelayPayment(String totalDelayPayment) {
		this.totalDelayPayment = totalDelayPayment;
	}
	public String getDelayPayAmt() {
		return delayPayAmt;
	}
	public void setDelayPayAmt(String delayPayAmt) {
		this.delayPayAmt = delayPayAmt;
	}
	public String getAsOfMonth() {
		return asOfMonth;
	}
	public void setAsOfMonth(String asOfMonth) {
		this.asOfMonth = asOfMonth;
	}
	public String getSuretyBondAmt() {
		return suretyBondAmt;
	}
	public void setSuretyBondAmt(String surtyBondAmt) {
		this.suretyBondAmt = surtyBondAmt;
	}
	public String getRewardAmt() {
		return rewardAmt;
	}
	public void setRewardAmt(String rewardAmt) {
		this.rewardAmt = rewardAmt;
	}
	public String getNetDamageAmt() {
		return netDamageAmt;
	}
	public void setNetDamageAmt(String netDamageAmt) {
		this.netDamageAmt = netDamageAmt;
	}
	public String getNetSuretyBondAmt() {
		return netSuretyBondAmt;
	}
	public void setNetSuretyBondAmt(String netSurtyBondAmt) {
		this.netSuretyBondAmt = netSurtyBondAmt;
	}
	public String getInvRefwal() {
		return invRefwal;
	}
	public void setInvRefwal(String invRefwal) {
		this.invRefwal = invRefwal;
	}
	public List<SAReportBean> getItems() {
		return items;
	}
	public void setItems(List<SAReportBean> items) {
		this.items = items;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRegionDesc() {
		return regionDesc;
	}
	public void setRegionDesc(String regionDesc) {
		this.regionDesc = regionDesc;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getOracleRefId() {
		return oracleRefId;
	}
	public void setOracleRefId(String oracleRefId) {
		this.oracleRefId = oracleRefId;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
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
	public String getTotalDamage() {
		return totalDamage;
	}
	public void setTotalDamage(String totalDamage) {
		this.totalDamage = totalDamage;
	}

	//optional
	
	
}
