package com.isecinc.pens.web.report.creditpaid;

import java.io.Serializable;
import java.util.List;

import util.ConvertNullUtil;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditPaidReport implements Serializable{

	private static final long serialVersionUID = 9117495907654379254L;

	private String month;
	private String no;
	private int id;
	private String orderDate;
	private String orderNo;

	private String customerCode;
	private String customerName;
	private double orderAmount;
	private String pdDate;
	private String paymentMethod;
	private String status;
	private double totalOrderAmt;
	
	private List<CreditPaidReport> items;
	
	
	public double getTotalOrderAmt() {
		return totalOrderAmt;
	}
	public void setTotalOrderAmt(double totalOrderAmt) {
		this.totalOrderAmt = totalOrderAmt;
	}
	public List<CreditPaidReport> getItems() {
		return items;
	}
	public void setItems(List<CreditPaidReport> items) {
		this.items = items;
	}
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public double getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(double orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getPdDate() {
		return pdDate;
	}
	public void setPdDate(String pdDate) {
		this.pdDate = pdDate;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
}
