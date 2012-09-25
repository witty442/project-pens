package com.isecinc.pens.report.taxinvoice;

import java.io.Serializable;

/**
 * Tax Invoice Report
 * 
 * @author Aneak.t
 * @version $Id: TaxInvoiceReport.java,v 1.0 01/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceDDReport implements Serializable{

	private static final long serialVersionUID = 8352026959679191245L;

	private int id;
	
	private String receiptNo;
	private String orderNo;
	private String orderDateFrom;
	private String orderDateTo;
	private String receiptDateFrom;
	private String receiptDateTo;
	private String customerCodeFrom;
	private String customerCodeTo;
	
	private String description;
	private double invoiceAmount;
	private double creditAmount;
	private double paidAmount;
	private double remainAmount;
	
	private String address;
	
	private String orderID;
	
	private String orderDate;
	private String receiptDate;
	
	private String taxNo;
	private String code;
	private String name;
	private String customerCode;
	private String customerName;
	private String saleName;


	private double discount;
	
	private double lineAmount;
	private double totalAmount;
	private double vatAmount;
	private double netAmount;
	private double receiptAmount;
	
	private String productCode;
	private String productName;
	private int mainQty;
	private int subQty;
	private double salePrice;
	private String uomId;
	
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
	public int getMainQty() {
		return mainQty;
	}
	public void setMainQty(int mainQty) {
		this.mainQty = mainQty;
	}
	public int getSubQty() {
		return subQty;
	}
	public void setSubQty(int subQty) {
		this.subQty = subQty;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public double getReceiptAmount() {
		return receiptAmount;
	}
	public void setReceiptAmount(double receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	
	public double getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}
	public double getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}
	public double getRemainAmount() {
		return remainAmount;
	}
	public void setRemainAmount(double remainAmount) {
		this.remainAmount = remainAmount;
	}
	

	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getTaxNo() {
		return taxNo;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
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
	
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public double getLineAmount() {
		return lineAmount;
	}
	public void setLineAmount(double lineAmount) {
		this.lineAmount = lineAmount;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}
	public double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public String getCustomerCodeFrom() {
		return customerCodeFrom;
	}
	public void setCustomerCodeFrom(String customerCodeFrom) {
		this.customerCodeFrom = customerCodeFrom;
	}
	public String getCustomerCodeTo() {
		return customerCodeTo;
	}
	public void setCustomerCodeTo(String customerCodeTo) {
		this.customerCodeTo = customerCodeTo;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	public String getSaleName() {
		return saleName;
	}
	public void setSaleName(String saleName) {
		this.saleName = saleName;
	}
	public String getReceiptDateFrom() {
		return receiptDateFrom;
	}
	public void setReceiptDateFrom(String receiptDateFrom) {
		this.receiptDateFrom = receiptDateFrom;
	}
	public String getReceiptDateTo() {
		return receiptDateTo;
	}
	public void setReceiptDateTo(String receiptDateTo) {
		this.receiptDateTo = receiptDateTo;
	}
	public String getUomId() {
		return uomId;
	}
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
}
