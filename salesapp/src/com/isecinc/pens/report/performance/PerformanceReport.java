package com.isecinc.pens.report.performance;

import java.io.Serializable;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class PerformanceReport implements Serializable {

	private static final long serialVersionUID = 6227629026635056978L;

	private int id;
	private int no;
	private String code;
	private String name;
	private String orderDate;
	private String orderNo;
	private double discount;
	private double netAmount;
	private double cashAmount;
	private double receiptAmount;
	private double airpayAmount;
	private double vatAmount;
	private String customerCode;
	private String customerName;
	private double vatCash;
	private double vatReceipt;
	private String chequeNo;
	private String status;
	private String airpayNo;

	/** For sum all start month to date selected. **/
	private double allDiscount;
	private double allNetAmount;
	private double allNetAmountNoDisNonVat;
	private double allCashAmount;
	private double allReceiptAmount;
	private double allVatAmount;
	private double allTargetAmount;
	private double allVatCashAmount;
	private double allVatReceiptAmount;
	private double totalCancelAmountToday;
	private double allAirpayAmount;
	
	
	public double getAllNetAmountNoDisNonVat() {
		return allNetAmountNoDisNonVat;
	}

	public void setAllNetAmountNoDisNonVat(double allNetAmountNoDisNonVat) {
		this.allNetAmountNoDisNonVat = allNetAmountNoDisNonVat;
	}

	public double getAllAirpayAmount() {
		return allAirpayAmount;
	}

	public void setAllAirpayAmount(double allAirpayAmount) {
		this.allAirpayAmount = allAirpayAmount;
	}

	public double getAirpayAmount() {
		return airpayAmount;
	}

	public void setAirpayAmount(double airpayAmount) {
		this.airpayAmount = airpayAmount;
	}

	public String getAirpayNo() {
		return airpayNo;
	}

	public void setAirpayNo(String airpayNo) {
		this.airpayNo = airpayNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getTotalCancelAmountToday() {
		return totalCancelAmountToday;
	}

	public void setTotalCancelAmountToday(double totalCancelAmountToday) {
		this.totalCancelAmountToday = totalCancelAmountToday;
	}

	public int getId() {
		return id;
	}
    
	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}
	

	public void setCode(String code) {
		this.code = code;
	}

	
	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
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

	public double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}

	public double getAllDiscount() {
		return allDiscount;
	}

	public void setAllDiscount(double allDiscount) {
		this.allDiscount = allDiscount;
	}

	public double getAllNetAmount() {
		return allNetAmount;
	}

	public void setAllNetAmount(double allNetAmount) {
		this.allNetAmount = allNetAmount;
	}

	public double getAllCashAmount() {
		return allCashAmount;
	}

	public void setAllCashAmount(double allCashAmount) {
		this.allCashAmount = allCashAmount;
	}

	public double getAllVatAmount() {
		return allVatAmount;
	}

	public void setAllVatAmount(double allVatAmount) {
		this.allVatAmount = allVatAmount;
	}

	public double getAllTargetAmount() {
		return allTargetAmount;
	}

	public void setAllTargetAmount(double allTargetAmount) {
		this.allTargetAmount = allTargetAmount;
	}

	public double getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public double getAllReceiptAmount() {
		return allReceiptAmount;
	}

	public void setAllReceiptAmount(double allReceiptAmount) {
		this.allReceiptAmount = allReceiptAmount;
	}

	public double getVatCash() {
		return vatCash;
	}

	public void setVatCash(double vatCash) {
		this.vatCash = vatCash;
	}

	public double getVatReceipt() {
		return vatReceipt;
	}

	public void setVatReceipt(double vatReceipt) {
		this.vatReceipt = vatReceipt;
	}

	public double getAllVatCashAmount() {
		return allVatCashAmount;
	}

	public void setAllVatCashAmount(double allVatCashAmount) {
		this.allVatCashAmount = allVatCashAmount;
	}

	public double getAllVatReceiptAmount() {
		return allVatReceiptAmount;
	}

	public void setAllVatReceiptAmount(double allVatReceiptAmount) {
		this.allVatReceiptAmount = allVatReceiptAmount;
	}

}
