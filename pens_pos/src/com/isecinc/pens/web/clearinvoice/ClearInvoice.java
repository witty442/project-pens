package com.isecinc.pens.web.clearinvoice;

import java.io.Serializable;


public class ClearInvoice implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1172118743247700777L;
	//Criteria
	private String orderDateFrom;
	private String orderDateTo;
	private String customerId;
	private String customerCode;
	private String customerName;
	
	//results
	private int no;
	private String orderId;
	private String receiptLineId;
    private String orderDate;
    private String arInvoiceNo;
    private String totalAmount;
    private String vatAmount;
    private String netAmount;
    private String paidAmount;
    private String remainAmount;
    private String remainAmountCalc;
    private String condition;
    
    private String orderIdSqlIn;
    
    
	public String getOrderIdSqlIn() {
		return orderIdSqlIn;
	}
	public void setOrderIdSqlIn(String orderIdSqlIn) {
		this.orderIdSqlIn = orderIdSqlIn;
	}
	public String getReceiptLineId() {
		return receiptLineId;
	}
	public void setReceiptLineId(String receiptLineId) {
		this.receiptLineId = receiptLineId;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getRemainAmountCalc() {
		return remainAmountCalc;
	}
	public void setRemainAmountCalc(String remainAmountCalc) {
		this.remainAmountCalc = remainAmountCalc;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
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
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getArInvoiceNo() {
		return arInvoiceNo;
	}
	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(String vatAmount) {
		this.vatAmount = vatAmount;
	}
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	public String getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}
	public String getRemainAmount() {
		return remainAmount;
	}
	public void setRemainAmount(String remainAmount) {
		this.remainAmount = remainAmount;
	}
    
    
}
