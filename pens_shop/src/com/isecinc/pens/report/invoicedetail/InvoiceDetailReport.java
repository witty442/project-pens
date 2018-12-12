package com.isecinc.pens.report.invoicedetail;

import java.io.Serializable;
import java.util.Date;



/**
 * InvoiceDetailReport Report
 * 
 * @author WITTY
 * @version $Id: PerformanceReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoiceDetailReport implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1454412685103136347L;
	private int groupId;
	private String id;
	private String orderNo;
	private String invoiceNo;
	private Date orderDate;
	private String orderDateStr;
	private String productCode;
	private String productName;
	private String fullUom;
	private String qtyString;
    private String priceString;
    private String lineAmount;
    private String discountAmount;
	private String totalAmount;
	private String shippingDate;
	private String requestDate;
	private String promotion;
	private String isHeader;
	private String customerName;
	private String customerCode;
	private String orderNoFrom;
	private String orderDateFrom;
	private String orderNoTo;
	private String orderDateTo;
    private String status;
    private Double vatAmount;
    private Double lineAmount1;
    private Double totalAmount1;
    
    private Double orderAmt;
	private Double orderVATAmt;
    private Double orderAmtIncludeVAT;
    
    private String productCodeFrom;
    private String productCodeTo;
    private double totalDiscountAmt;
	
    
	public double getTotalDiscountAmt() {
		return totalDiscountAmt;
	}

	public void setTotalDiscountAmt(double totalDiscountAmt) {
		this.totalDiscountAmt = totalDiscountAmt;
	}

    
	public String getOrderDateStr() {
		return orderDateStr;
	}
	public void setOrderDateStr(String orderDateStr) {
		this.orderDateStr = orderDateStr;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public Double getTotalAmount1() {
		return totalAmount1;
	}
	public void setTotalAmount1(Double totalAmount1) {
		this.totalAmount1 = totalAmount1;
	}
	public Double getLineAmount1() {
		return lineAmount1;
	}
	public void setLineAmount1(Double lineAmount1) {
		this.lineAmount1 = lineAmount1;
	}
	public Double getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getOrderNoFrom() {
		return orderNoFrom;
	}
	public void setOrderNoFrom(String orderNoFrom) {
		this.orderNoFrom = orderNoFrom;
	}
	public String getOrderNoTo() {
		return orderNoTo;
	}
	public void setOrderNoTo(String orderNoTo) {
		this.orderNoTo = orderNoTo;
	}
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public String getIsHeader() {
		return isHeader;
	}
	public void setIsHeader(String isHeader) {
		this.isHeader = isHeader;
	}
    
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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
	public String getFullUom() {
		return fullUom;
	}
	public void setFullUom(String fullUom) {
		this.fullUom = fullUom;
	}
	
	public String getPriceString() {
		return priceString;
	}
	public void setPriceString(String priceString) {
		this.priceString = priceString;
	}
	public String getQtyString() {
		return qtyString;
	}
	public void setQtyString(String qtyString) {
		this.qtyString = qtyString;
	}
	public String getLineAmount() {
		return lineAmount;
	}
	public void setLineAmount(String lineAmount) {
		this.lineAmount = lineAmount;
	}
	public String getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(String discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
	public String getPromotion() {
		return promotion;
	}
	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}
	public String getProductCodeFrom() {
		return productCodeFrom;
	}
	public void setProductCodeFrom(String productCodeFrom) {
		this.productCodeFrom = productCodeFrom;
	}
	public String getProductCodeTo() {
		return productCodeTo;
	}
	public void setProductCodeTo(String productCodeTo) {
		this.productCodeTo = productCodeTo;
	}
	public Double getOrderAmt() {
		return orderAmt;
	}
	public void setOrderAmt(Double orderAmt) {
		this.orderAmt = orderAmt;
	}
	public Double getOrderVATAmt() {
		return orderVATAmt;
	}
	public void setOrderVATAmt(Double orderVATAmt) {
		this.orderVATAmt = orderVATAmt;
	}
	public Double getOrderAmtIncludeVAT() {
		return orderAmtIncludeVAT;
	}
	public void setOrderAmtIncludeVAT(Double orderAmtIncludeVAT) {
		this.orderAmtIncludeVAT = orderAmtIncludeVAT;
	}
}
