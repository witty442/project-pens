package com.isecinc.pens.report.invoicepayment;

import java.io.Serializable;
import java.util.List;

/**
 * Invoice Payment Report
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReport.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentReport implements Serializable{

	private static final long serialVersionUID = -1341946489410284491L;
	
	private int id;
	private String invName;
	private String description;
	private String receiptDate;
	private String code;
	private String name;
	private String customerName;
	private String customerCode;
	private String orderNo;
	private String orderDate;
	private double receiptAmount;
	private String bank;
	private String chequeNo;
	private String chequeDate;
	private double chequeAmt;
	private double creditCardAmt;
	private double cashWriteOff;
	
	// Add Payment Method To Use In Report for Filter
	// Cash(CS) or Cheque(CH) 
	private String paymentMethod;
	
	// Add Payment Rule to Use In Report Group
	// Credit Sales(CREDIT) or Cash Sales (CASH)
	private String paymentTerm;
	
	private String startDate;
	private String endDate;
	private String isCurrent;
	
	private List<InvoicePaymentReport> itemsList;
	private double totalCreditCardAmt;
	private double totalCashAmt;
	private double totalAliAmt;
	private double totalWeAmt;
	private double totalGovAmt;
	private double totalQrAmt;
	
	
	public double getTotalQrAmt() {
		return totalQrAmt;
	}
	public void setTotalQrAmt(double totalQrAmt) {
		this.totalQrAmt = totalQrAmt;
	}
	public double getTotalGovAmt() {
		return totalGovAmt;
	}
	public void setTotalGovAmt(double totalGovAmt) {
		this.totalGovAmt = totalGovAmt;
	}
	public double getTotalAliAmt() {
		return totalAliAmt;
	}
	public void setTotalAliAmt(double totalAliAmt) {
		this.totalAliAmt = totalAliAmt;
	}
	public double getTotalWeAmt() {
		return totalWeAmt;
	}
	public void setTotalWeAmt(double totalWeAmt) {
		this.totalWeAmt = totalWeAmt;
	}
	public double getTotalCreditCardAmt() {
		return totalCreditCardAmt;
	}
	public void setTotalCreditCardAmt(double totalCreditCardAmt) {
		this.totalCreditCardAmt = totalCreditCardAmt;
	}
	public double getTotalCashAmt() {
		return totalCashAmt;
	}
	public void setTotalCashAmt(double totalCashAmt) {
		this.totalCashAmt = totalCashAmt;
	}
	public List<InvoicePaymentReport> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<InvoicePaymentReport> itemsList) {
		this.itemsList = itemsList;
	}
	public double getCreditCardAmt() {
		return creditCardAmt;
	}
	public void setCreditCardAmt(double creditCardAmt) {
		this.creditCardAmt = creditCardAmt;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getInvName() {
		return invName;
	}
	public void setInvName(String invName) {
		this.invName = invName;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public double getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
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
	public double getChequeAmt() {
		return chequeAmt;
	}
	public void setChequeAmt(double chequeAmt) {
		this.chequeAmt = chequeAmt;
	}
	public double getCashWriteOff() {
		return cashWriteOff;
	}
	public void setCashWriteOff(double cashWriteOff) {
		this.cashWriteOff = cashWriteOff;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getIsCurrent() {
		return isCurrent;
	}
	public void setIsCurrent(String isCurrent) {
		this.isCurrent = isCurrent;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getPaymentTerm() {
		return paymentTerm;
	}
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	
}
