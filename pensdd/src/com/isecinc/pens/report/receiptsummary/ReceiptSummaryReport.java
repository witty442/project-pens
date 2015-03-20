package com.isecinc.pens.report.receiptsummary;

import java.io.Serializable;
import java.math.BigDecimal;

import util.ConvertNullUtil;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptSummaryReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -755081146232445764L;

	// Report Parameters
	private String receiptDateFrom;
	private String receiptDateTo;
	
	private String paymentMethod;
	
	private String receiptType;
	private String receiptDate;
	private String receiptNo;
	private String customerCode;
	private String customerName;
	private BigDecimal taxBaseAmt;
	private BigDecimal vatAmt;
	private BigDecimal totalAmt;
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
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getReceiptType() {
		return receiptType;
	}
	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
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
	public BigDecimal getTaxBaseAmt() {
		return taxBaseAmt;
	}
	public void setTaxBaseAmt(BigDecimal taxBaseAmt) {
		this.taxBaseAmt = taxBaseAmt;
	}
	public BigDecimal getVatAmt() {
		return vatAmt;
	}
	public void setVatAmt(BigDecimal vatAmt) {
		this.vatAmt = vatAmt;
	}
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
}
