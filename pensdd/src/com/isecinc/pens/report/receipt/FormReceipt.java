package com.isecinc.pens.report.receipt;

import java.io.Serializable;
import java.math.BigDecimal;

public class FormReceipt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7738534548445114120L;
	
	private String receiptDateFrom;
	private String receiptDateTo;
	private String customerCode;

	private String receiptNo;
	private String receiptDate;
	private String customerName;
	private String customerAddress;
	
	private BigDecimal lineAmt;
	private BigDecimal sumLinesAmt;
	/*private BigDecimal vatAmt;
	private BigDecimal sumTotalAmt;*/
	
	private String chequeNo;
	private String creditCardNo;
	private String receiptStatus;
	private String paymentMethod;
	
	public String getReceiptDateFrom() {
		return receiptDateFrom;
	}
	public void setReceiptDateFrom(String receiptDateFrom) {
		this.receiptDateFrom = receiptDateFrom;
	}
	public String getReceiptDateTo() {
		return receiptDateTo;
	}
	public void setReceiptDateTo(String receiptDateTo) {
		this.receiptDateTo = receiptDateTo;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public BigDecimal getLineAmt() {
		return lineAmt;
	}
	public void setLineAmt(BigDecimal lineAmt) {
		this.lineAmt = lineAmt;
	}
	public BigDecimal getSumLinesAmt() {
		return sumLinesAmt;
	}
	public void setSumLinesAmt(BigDecimal sumLinesAmt) {
		this.sumLinesAmt = sumLinesAmt;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getCreditCardNo() {
		return creditCardNo;
	}
	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}
	public String getReceiptStatus() {
		return receiptStatus;
	}
	public void setReceiptStatus(String receiptStatus) {
		this.receiptStatus = receiptStatus;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
