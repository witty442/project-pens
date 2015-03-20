package com.isecinc.pens.report.receiptplan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReceiptPlanReport implements Serializable /*, Comparable*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3891917242278626563L;
	// Defind Parameter
    private String dateFrom;
    private String dateTo;
    private String param_PaymentMethod;
    
    //Define Report Field
    private String memberCode ;
    private String memberName ;
    private String memberTel ;
    private Timestamp shipDate;
    private String orderNo;
    private String paymentMethod;
    private String paymentMethodCode;
    private int tripNo;
    private BigDecimal planBillAmt ;
    
    private String creditCardNo;
    private String creditCardExpireDate;
    private String creditCardBank;
    private String creditCardName;
    
	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
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

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
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
