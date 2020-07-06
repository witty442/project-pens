package com.isecinc.pens.report.remittance;

import java.io.Serializable;


/**
 * Remittance Report
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReport.java,v 1.0 02/12/2010 15:52:00 aneak.t Exp $
 * 
 */
public class RemittanceReport implements Serializable{

	private static final long serialVersionUID = -2205832378956290387L;

	private int id;
	private String receiptDateFrom;
	private String receiptDateTo;
	private String customerCode;
	private String customerName;
	private String lastName;
	private String customerCodeFrom;
	private String customerCodeTo;
	private String receiptDate;
	

	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	private double receiptAmount;
	
	public String getCustomerCodeFrom() {
		return customerCodeFrom;
	}
	public void setCustomerCodeFrom(String customerCodeFrom) {
		this.customerCodeFrom = customerCodeFrom;
	}
	public String getCustomerCodeTo() {
		return customerCodeTo;
	}
	public void setCustomerCodeTo(String customerCodeTo) {
		this.customerCodeTo = customerCodeTo;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
