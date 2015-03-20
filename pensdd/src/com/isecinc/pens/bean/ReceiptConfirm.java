package com.isecinc.pens.bean;

import java.io.Serializable;

import com.isecinc.core.model.I_PO;

public class ReceiptConfirm extends I_PO implements Serializable , Comparable<ReceiptConfirm> {
	// Define Answer
	private double confirmAmt;
	private int lineId;
	private String isConfirm;
	
	// Define Payment Method Information that need to used in Receipt Generation
	private String creditCardName;
	private String creditCardNo;
	private String bank;
	private String chequeNo;
	private String chequeDate;
	
	private int orderId;
	private String orderNo;
	private String paymentMethod;
	private double actQty;
	private double qty;
	private double totalAmt;
	private double needBillAmt;
	private int tripNo;
	private String productCode;
	private String productName;
	private String shippingDate;

	
	public double getNeedBillAmt() {
		return needBillAmt;
	}

	public void setNeedBillAmt(double needBillAmt) {
		this.needBillAmt = needBillAmt;
	}

	protected void setDisplayLabel() throws Exception {
		// TODO Auto-generated method stub
	}

	public double getConfirmAmt() {
		return confirmAmt;
	}

	public void setConfirmAmt(double confirmAmt) {
		this.confirmAmt = confirmAmt;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public String getIsConfirm() {
		return isConfirm;
	}

	public void setIsConfirm(String isConfirm) {
		this.isConfirm = isConfirm;
	}

	public String getCreditCardName() {
		return creditCardName;
	}

	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public double getActQty() {
		return actQty;
	}

	public void setActQty(double actQty) {
		this.actQty = actQty;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/* Method for Comparable*/
	public int compareTo(ReceiptConfirm confirm) {
		return orderNo.compareTo(confirm.getOrderNo());
	}
	
	public String getDescription(){
		return "ครั้งที่ "+tripNo+" "+productCode+" "+productName+" "+actQty+" ขวด  วันที่ส่ง "+shippingDate;
	}
}
