package com.isecinc.pens.report.shipmentbenecol;

import java.io.Serializable;

/**
 * Shipment Benecol Report
 * 
 * @author Aneak.t
 * @version $Id: ShipmentBenecolReport.java,v 1.0 17/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentBenecolReport implements Serializable {

	private static final long serialVersionUID = -1473882809749643114L;

	private int id;
	private String shippingDate;
	private String shippingUser;
	private String customerCode;
	private String customerName;
	private int qty;
	private String payment;
	private double lineAmount;
	private String receiveUser;
	private String bankAccount;
	private String address;

	// atiz.b
	private String tripComment;
	private int tripNo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}

	public String getShippingUser() {
		return shippingUser;
	}

	public void setShippingUser(String shippingUser) {
		this.shippingUser = shippingUser;
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

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public double getLineAmount() {
		return lineAmount;
	}

	public void setLineAmount(double lineAmount) {
		this.lineAmount = lineAmount;
	}

	public String getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(String receiveUser) {
		this.receiveUser = receiveUser;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTripComment() {
		return tripComment;
	}

	public void setTripComment(String tripComment) {
		this.tripComment = tripComment;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

}
