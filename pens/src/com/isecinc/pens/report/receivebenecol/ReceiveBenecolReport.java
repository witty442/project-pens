package com.isecinc.pens.report.receivebenecol;

import java.io.Serializable;

/**
 * Receive Benecol Report
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBenecolReport.java,v 1.0 02/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiveBenecolReport implements Serializable{

	private static final long serialVersionUID = -7931901408287591271L;

	private int id;
	private String orderDate;
	private String customerCode;
	private String customerName;
	private String address;
	private String mobile;
	private String phone;
	private int orangeQty;
	private int berryQty;
	private int mixQty;
	private int lineAmount;
	private String shippingDate;
	private String requestDate;
	private String paymentDate;
	private double ramainAmount;
	private String recommentedBy;
	
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getOrangeQty() {
		return orangeQty;
	}
	public void setOrangeQty(int orangeQty) {
		this.orangeQty = orangeQty;
	}
	public int getBerryQty() {
		return berryQty;
	}
	public void setBerryQty(int berryQty) {
		this.berryQty = berryQty;
	}
	public int getMixQty() {
		return mixQty;
	}
	public void setMixQty(int mixQty) {
		this.mixQty = mixQty;
	}
	public int getLineAmount() {
		return lineAmount;
	}
	public void setLineAmount(int lineAmount) {
		this.lineAmount = lineAmount;
	}
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public double getRamainAmount() {
		return ramainAmount;
	}
	public void setRamainAmount(double ramainAmount) {
		this.ramainAmount = ramainAmount;
	}
	public String getRecommentedBy() {
		return recommentedBy;
	}
	public void setRecommentedBy(String recommentedBy) {
		this.recommentedBy = recommentedBy;
	}
	
}
