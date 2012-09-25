package com.isecinc.pens.report.taxinvoice;

import java.io.Serializable;

/**
 * Tax Invoice Report
 * 
 * @author Aneak.t
 * @version $Id: TaxInvoiceReport.java,v 1.0 01/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceReport implements Serializable{

	private static final long serialVersionUID = 8352026959679191245L;

	private int id;
	private String address;
	private String orderID;
	private String orderDate;
	private String taxNo;
	private String code;
	private String name;
	private String customerCode;
	private String customerName;
	private String productCode;
	private String productName;
	private int mainQty;
	private int subQty;
	private int addQty;
	private double salePrice;
	private double percentDiscount;
	private double discount;
	private double lineAmount;
	private double totalAmount;
	private double vatAmount;
	private double netAmount;
	private String uomId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public int getAddQty() {
		return addQty;
	}
	public void setAddQty(int addQty) {
		this.addQty = addQty;
	}
	public double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}
	public double getPercentDiscount() {
		return percentDiscount;
	}
	public void setPercentDiscount(double percentDiscount) {
		this.percentDiscount = percentDiscount;
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
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public double getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}
	public String getUomId() {
		return uomId;
	}
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	
}
