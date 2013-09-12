package com.isecinc.pens.report.cheque;

import java.io.Serializable;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ChequeReport implements Serializable {

	private static final long serialVersionUID = 6227629026635056978L;
    //criteria
	String orderDateFrom;
	String orderDateTo;
	
	private int no;
	private String chequeDate;
	private String chequeNo;
	private String orderDate;
	private String orderNo;
	private double chequeAmount;
	private String bankName;
	private String customerCode;
	private String customerName;
	
	
	
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public double getChequeAmount() {
		return chequeAmount;
	}
	public void setChequeAmount(double chequeAmount) {
		this.chequeAmount = chequeAmount;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
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
	
	
	
}
