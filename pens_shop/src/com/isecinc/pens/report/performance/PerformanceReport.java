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
	private double aliAmount;
	private double weAmount;
	private double qrAmount;
	private double airpayAmount;
	private double vatAmount;
	private String customerCode;
	private String customerName;
	private double vatCash;
	private double vatReceipt;
	private double vatAli;
	private double vatWe;
	private double vatQr;
	private String chequeNo;
	private String status;
	private String airpayNo;
	private String creditCardNo;
	
	/** For sum all start month to date selected. **/
	private double allDiscount;
	private double allNetAmount;
	private double allCashAmount;
	private double allReceiptAmount;
	private double allVatAmount;
	private double allTargetAmount;
	private double allVatCashAmount;
	private double allVatReceiptAmount;
	private double totalCancelAmountToday;
	private double allAirpayAmount;
	private double allAliAmount;
	private double allWeAmount;
	private double allQrAmount;
	private double allVatAliAmount;
	private double allVatWeAmount;
	private double allVatQrAmount;
	
	
	public double getQrAmount() {
		return qrAmount;
	}

	public void setQrAmount(double qrAmount) {
		this.qrAmount = qrAmount;
	}

	public double getVatQr() {
		return vatQr;
	}

	public void setVatQr(double vatQr) {
		this.vatQr = vatQr;
	}

	public double getAllQrAmount() {
		return allQrAmount;
	}

	public void setAllQrAmount(double allQrAmount) {
		this.allQrAmount = allQrAmount;
	}

	public double getAllVatQrAmount() {
		return allVatQrAmount;
	}

	public void setAllVatQrAmount(double allVatQrAmount) {
		this.allVatQrAmount = allVatQrAmount;
	}

	public double getVatAli() {
		return vatAli;
	}

	public void setVatAli(double vatAli) {
		this.vatAli = vatAli;
	}

	public double getVatWe() {
		return vatWe;
	}

	public void setVatWe(double vatWe) {
		this.vatWe = vatWe;
	}

	public double getAliAmount() {
		return aliAmount;
	}

	public void setAliAmount(double aliAmount) {
		this.aliAmount = aliAmount;
	}

	public double getWeAmount() {
		return weAmount;
	}

	public void setWeAmount(double weAmount) {
		this.weAmount = weAmount;
	}

	public double getAllVatAliAmount() {
		return allVatAliAmount;
	}

	public void setAllVatAliAmount(double allVatAliAmount) {
		this.allVatAliAmount = allVatAliAmount;
	}

	public double getAllVatWeAmount() {
		return allVatWeAmount;
	}

	public void setAllVatWeAmount(double allVatWeAmount) {
		this.allVatWeAmount = allVatWeAmount;
	}

	public double getAllAliAmount() {
		return allAliAmount;
	}

	public void setAllAliAmount(double allAliAmount) {
		this.allAliAmount = allAliAmount;
	}

	public double getAllWeAmount() {
		return allWeAmount;
	}

	public void setAllWeAmount(double allWeAmount) {
		this.allWeAmount = allWeAmount;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
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
