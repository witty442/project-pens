package com.isecinc.pens.report.receiptplancompare;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReceiptPlanCompareReport implements Serializable /*, Comparable*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3891917242278626563L;
	// Defind Parameter
    private String shippingDateFrom;
    private String shippingDateTo;
    private String confirmDateFrom;
    private String confirmDateTo;
    private String custCode;
    private String custName;
    private String param_PaymentMethod;
    
    //Define Report Field
    private String memberTel ;
    private Timestamp shipDate;
    private String orderNo;
    private String paymentMethod;
    private String paymentMethodCode;
    private int tripNo;
    private BigDecimal planBillAmt ;
    private BigDecimal confirmBillAmt ;
    private BigDecimal diffBillAmt ;
    
    private String creditCardNo;
    private String creditCardExpireDate;
    private String creditCardBank;
    private String creditCardName;
    
	
	
	public BigDecimal getConfirmBillAmt() {
		return confirmBillAmt;
	}

	public void setConfirmBillAmt(BigDecimal confirmBillAmt) {
		this.confirmBillAmt = confirmBillAmt;
	}

	public BigDecimal getDiffBillAmt() {
		return diffBillAmt;
	}

	public void setDiffBillAmt(BigDecimal diffBillAmt) {
		this.diffBillAmt = diffBillAmt;
	}

	public String getShippingDateFrom() {
		return shippingDateFrom;
	}

	public void setShippingDateFrom(String shippingDateFrom) {
		this.shippingDateFrom = shippingDateFrom;
	}

	public String getShippingDateTo() {
		return shippingDateTo;
	}

	public void setShippingDateTo(String shippingDateTo) {
		this.shippingDateTo = shippingDateTo;
	}

	

	public String getConfirmDateFrom() {
		return confirmDateFrom;
	}

	public void setConfirmDateFrom(String confirmDateFrom) {
		this.confirmDateFrom = confirmDateFrom;
	}

	public String getConfirmDateTo() {
		return confirmDateTo;
	}

	public void setConfirmDateTo(String confirmDateTo) {
		this.confirmDateTo = confirmDateTo;
	}

	public String getCustCode() {
		return custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getParam_PaymentMethod() {
		return param_PaymentMethod;
	}

	public void setParam_PaymentMethod(String param_PaymentMethod) {
		this.param_PaymentMethod = param_PaymentMethod;
	}

	public String getMemberTel() {
		return memberTel;
	}

	public void setMemberTel(String memberTel) {
		this.memberTel = memberTel;
	}

	public Timestamp getShipDate() {
		return shipDate;
	}

	public void setShipDate(Timestamp shipDate) {
		this.shipDate = shipDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public BigDecimal getPlanBillAmt() {
		return planBillAmt;
	}

	public void setPlanBillAmt(BigDecimal planBillAmt) {
		this.planBillAmt = planBillAmt;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getCreditCardExpireDate() {
		return creditCardExpireDate;
	}

	public void setCreditCardExpireDate(String creditCardExpireDate) {
		this.creditCardExpireDate = creditCardExpireDate;
	}

	public String getCreditCardBank() {
		return creditCardBank;
	}

	public void setCreditCardBank(String creditCardBank) {
		this.creditCardBank = creditCardBank;
	}

	public String getPaymentMethodCode() {
		return paymentMethodCode;
	}

	public void setPaymentMethodCode(String paymentMethodCode) {
		this.paymentMethodCode = paymentMethodCode;
	}

	public String getCreditCardName() {
		return creditCardName;
	}

	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}
}
