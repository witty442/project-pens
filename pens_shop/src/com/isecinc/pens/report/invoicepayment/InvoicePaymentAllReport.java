package com.isecinc.pens.report.invoicepayment;

import java.io.Serializable;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentAllReport implements Serializable {

	private static final long serialVersionUID = 6227629026635056978L;

	private int id;
	private int no;
	private String code;
	private String name;
	private String receiptDate;
	private String receiptNo;
	private double receiptAmount;
	private double vatAmount;
	private String customerCode;
	private String customerName;
	private double vatCash;
	private double vatReceipt;
	private String chequeNo;
	private String paymentMethod;
	private String status;
	private String isPDPaid;
	
	private String writeOff;

	/** For sum all start month to date selected. **/
	private double allDiscount;
	private double allNetAmount;
	private double allCashAmount;
	private double allReceiptAmount;
	private double allVatAmount;
	private double allTargetAmount;
	private double allVatCashAmount;
	private double allVatReceiptAmount;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReceiptNo() {
		return receiptNo;
	}
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	public double getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public double getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
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
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
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
	public double getAllReceiptAmount() {
		return allReceiptAmount;
	}
	public void setAllReceiptAmount(double allReceiptAmount) {
		this.allReceiptAmount = allReceiptAmount;
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

	public String getWriteOff() {
		return writeOff;
	}
	public void setWriteOff(String writeOff) {
		this.writeOff = writeOff;
	}
	public String getIsPDPaid() {
		return isPDPaid;
	}
	public void setIsPDPaid(String isPDPaid) {
		this.isPDPaid = isPDPaid;
	}

}
