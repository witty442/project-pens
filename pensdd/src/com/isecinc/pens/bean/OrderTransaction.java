package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;


public class OrderTransaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8715052866291829636L;
	private int orderId;
	private int tripNo;
	private boolean paymentOnly;
	private String shippingDate;
	private String confirmShipDate;
	private String confirmReceiptDate;
	private double totalAmount;
	private double totalRemainAmount;
	private double tripActNeedBill;
	private double tripTotalAmount;
	private double totalShipmentAmount;
	private double totalTaxinvoiceAmount;
	private double totalReceiptAmount;
	private double totalTripActNeedBill;
	
	private double remainShipmentAmount;
	private double remainTaxinvoiceAmount;
	private int userId;
	private int shipmentId;
    private int customerId;
    private String customerName;
    private String orderType;
    private Shipment shipment;
    private String orderNo;
    private String receiptNo ;
    private String docStatus;
    
    
    
	public double getRemainShipmentAmount() {
		return remainShipmentAmount;
	}
	public void setRemainShipmentAmount(double remainShipmentAmount) {
		this.remainShipmentAmount = remainShipmentAmount;
	}
	public double getRemainTaxinvoiceAmount() {
		return remainTaxinvoiceAmount;
	}
	public void setRemainTaxinvoiceAmount(double remainTaxinvoiceAmount) {
		this.remainTaxinvoiceAmount = remainTaxinvoiceAmount;
	}
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public double getTotalTripActNeedBill() {
		return totalTripActNeedBill;
	}
	public void setTotalTripActNeedBill(double totalTripActNeedBill) {
		this.totalTripActNeedBill = totalTripActNeedBill;
	}
	public Shipment getShipment() {
		return shipment;
	}
	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getConfirmReceiptDate() {
		return confirmReceiptDate;
	}
	public void setConfirmReceiptDate(String confirmReceiptDate) {
		this.confirmReceiptDate = confirmReceiptDate;
	}
	public int getShipmentId() {
		return shipmentId;
	}
	public void setShipmentId(int shipmentId) {
		this.shipmentId = shipmentId;
	}
	public boolean isPaymentOnly() {
		return paymentOnly;
	}
	public void setPaymentOnly(boolean paymentOnly) {
		this.paymentOnly = paymentOnly;
	}
	public double getTotalShipmentAmount() {
		return totalShipmentAmount;
	}
	public void setTotalShipmentAmount(double totalShipmentAmount) {
		this.totalShipmentAmount = totalShipmentAmount;
	}
	public double getTotalTaxinvoiceAmount() {
		return totalTaxinvoiceAmount;
	}
	public void setTotalTaxinvoiceAmount(double totalTaxinvoiceAmount) {
		this.totalTaxinvoiceAmount = totalTaxinvoiceAmount;
	}
	public double getTotalReceiptAmount() {
		return totalReceiptAmount;
	}
	public void setTotalReceiptAmount(double totalReceiptAmount) {
		this.totalReceiptAmount = totalReceiptAmount;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getTripNo() {
		return tripNo;
	}
	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}
	
	public String getConfirmShipDate() {
		return confirmShipDate;
	}
	public void setConfirmShipDate(String confirmShipDate) {
		this.confirmShipDate = confirmShipDate;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getTotalRemainAmount() {
		return totalRemainAmount;
	}
	public void setTotalRemainAmount(double totalRemainAmount) {
		this.totalRemainAmount = totalRemainAmount;
	}
	public double getTripActNeedBill() {
		return tripActNeedBill;
	}
	public void setTripActNeedBill(double tripActNeedBill) {
		this.tripActNeedBill = tripActNeedBill;
	}
	public double getTripTotalAmount() {
		return tripTotalAmount;
	}
	public void setTripTotalAmount(double tripTotalAmount) {
		this.tripTotalAmount = tripTotalAmount;
	}
	
	
}
