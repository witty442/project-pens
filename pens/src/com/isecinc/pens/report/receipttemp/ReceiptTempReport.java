package com.isecinc.pens.report.receipttemp;

import java.io.Serializable;

/**
 * Receipt Temporary Report
 * 
 * @author Aneak.t
 * @version $Id: ReceiptTempReport.java,v 1.0 17/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptTempReport implements Serializable{

	private static final long serialVersionUID = 3654365263333341007L;
	
	private int id;
	private int customerId;
	private String receiptNo;
	private String customerCode;
	private String customerName;
	private double receiptAmount;
	private String receiptAmountWord;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public double getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public String getReceiptAmountWord() {
		return receiptAmountWord;
	}
	public void setReceiptAmountWord(String receiptAmountWord) {
		this.receiptAmountWord = receiptAmountWord;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
}
