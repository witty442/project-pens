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
    private String asOfDate;
    private String suretyBondAmt;
    private String rewardAmt;
    private String netDamageAmt;
    private String netSuretyBondAmt;
    private String totalSuretyBondAmt;
    
    private List<SAReportBean> items;
    private String summaryType;
    private String empType;
    private String invoiceNo;
    private String totalPayment;
    private String totalInvoiceAmt;
    private String totalDelayPayment;
	private String totalRewardBme;
	private String totalRewardWacoal;
	private String countStockDate;
	private String rewardMonth;
	private String rewardMonthCount;
	
	//Criteria
	private String countStockDateFrom;
	private String countStockDateTo;
	private String payDateFrom;
	private String payDateTo;
	private String invoiceDateFrom;
	private String invoiceDateTo;
   
	private String payType1Amt;
	private String payType2Amt;
	private String payType3Amt;
	private String payType4Amt;
	
	
	
	public String getInvoiceDateFrom() {
		return invoiceDateFrom;
	}
	public void setInvoiceDateFrom(String invoiceDateFrom) {
		this.invoiceDateFrom = invoiceDateFrom;
	}
	public String getInvoiceDateTo() {
		return invoiceDateTo;
	}
	public void setInvoiceDateTo(String invoiceDateTo) {
		this.invoiceDateTo = invoiceDateTo;
	}
	public String getPayType1Amt() {
		return payType1Amt;
	}
	public void setPayType1Amt(String payType1Amt) {
		this.payType1Amt = payType1Amt;
	}
	public String getPayType2Amt() {
		return payType2Amt;
	}
	public void setPayType2Amt(String payType2Amt) {
		this.payType2Amt = payType2Amt;
	}
	public String getPayType3Amt() {
		return payType3Amt;
	}
	public void setPayType3Amt(String payType3Amt) {
		this.payType3Amt = payType3Amt;
	}
	public String getPayType4Amt() {
		return payType4Amt;
	}
	public void setPayType4Amt(String payType4Amt) {
		this.payType4Amt = payType4Amt;
	}
	public String getRewardMonthCount() {
		return rewardMonthCount;
	}
	public void setRewardMonthCount(String rewardMonthCount) {
		this.rewardMonthCount = rewardMonthCount;
	}
	public String getCountStockDateFrom() {
		return countStockDateFrom;
	}
	public void setCountStockDateFrom(String countStockDateFrom) {
		this.countStockDateFrom = countStockDateFrom;
	}
	public String getCountStockDateTo() {
		return countStockDateTo;
	}
	public void setCountStockDateTo(String countStockDateTo) {
		this.countStockDateTo = countStockDateTo;
	}
	public String getPayDateFrom() {
		return payDateFrom;
	}
	public void setPayDateFrom(String payDateFrom) {
		this.payDateFrom = payDateFrom;
	}
	public String getPayDateTo() {
		return payDateTo;
	}
	public void setPayDateTo(String payDateTo) {
		this.payDateTo = payDateTo;
	}
	public String getTotalSuretyBondAmt() {
		return totalSuretyBondAmt;
	}
	public void setTotalSuretyBondAmt(String totalSuretyBondAmt) {
		this.totalSuretyBondAmt = totalSuretyBondAmt;
	}
	public String getRewardMonth() {
		return rewardMonth;
	}
	public void setRewardMonth(String rewardMonth) {
		this.rewardMonth = rewardMonth;
	}
	public String getCountStockDate() {
		return countStockDate;
	}
	public void setCountStockDate(String countStockDate) {
		this.countStockDate = countStockDate;
	}
	public String getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getTotalRewardBme() {
		return totalRewardBme;
	}
	public void setTotalRewardBme(String totalRewardBme) {
		this.totalRewardBme = totalRewardBme;
	}
	public String getTotalRewardWacoal() {
		return totalRewardWacoal;
	}
	public void setTotalRewardWacoal(String totalRewardWacoal) {
		this.totalRewardWacoal = totalRewardWacoal;
	}
	public String getTotalInvoiceAmt() {
		return totalInvoiceAmt;
	}
	public void setTotalInvoiceAmt(String totalInvoiceAmt) {
		this.totalInvoiceAmt = totalInvoiceAmt;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getEmpType() {
		return empType;
	}
	public void setEmpType(String empType) {
		this.empType = empType;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
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
