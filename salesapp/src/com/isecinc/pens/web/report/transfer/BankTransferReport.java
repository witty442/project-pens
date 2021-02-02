package com.isecinc.pens.web.report.transfer;

import java.io.Serializable;
import java.util.List;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class BankTransferReport implements Serializable {

	private static final long serialVersionUID = 6227629026635056978L;

	private String transferDate; 
	private String transferTime; 
	private String transferType;
	private String transferBank; 
	private String amount; 
	private double amountDouble; 
	private String chequeNo; 
	private String chequeDate;
	private String createDate;
	private String totalAmount;
	private List<BankTransferReport> lstData;
	
	
	
	public double getAmountDouble() {
		return amountDouble;
	}
	public void setAmountDouble(double amountDouble) {
		this.amountDouble = amountDouble;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public List<BankTransferReport> getLstData() {
		return lstData;
	}
	public void setLstData(List<BankTransferReport> lstData) {
		this.lstData = lstData;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}
	public String getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferBank() {
		return transferBank;
	}
	public void setTransferBank(String transferBank) {
		this.transferBank = transferBank;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	} 
	
	
	}
