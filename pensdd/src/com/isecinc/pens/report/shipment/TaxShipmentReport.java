package com.isecinc.pens.report.shipment;

import java.io.Serializable;
import java.math.BigDecimal;

public class TaxShipmentReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7738534548445114120L;
	
	private String taxinvoiceDateFrom;
	private String taxinvoiceDateTo;
	private String customerCode;

	private String taxinvoiceNo;
	private String taxinvoiceDate;
	private String customerName;
	private String customerAddress;
	private String orderNo;
	private int tripNo;
	private String productName;
	private BigDecimal planQty;
	private BigDecimal actualQty;
	
	private String status;
	private BigDecimal lineAmt;
	private BigDecimal sumLinesAmt;
	private BigDecimal vatAmt;
	private BigDecimal sumTotalAmt;
	

	public String getTaxinvoiceDateFrom() {
		return taxinvoiceDateFrom;
	}

	public void setTaxinvoiceDateFrom(String taxinvoiceDateFrom) {
		this.taxinvoiceDateFrom = taxinvoiceDateFrom;
	}

	public String getTaxinvoiceDateTo() {
		return taxinvoiceDateTo;
	}

	public void setTaxinvoiceDateTo(String taxinvoiceDateTo) {
		this.taxinvoiceDateTo = taxinvoiceDateTo;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getTaxinvoiceNo() {
		return taxinvoiceNo;
	}

	public void setTaxinvoiceNo(String taxinvoiceNo) {
		this.taxinvoiceNo = taxinvoiceNo;
	}

	public String getTaxinvoiceDate() {
		return taxinvoiceDate;
	}

	public void setTaxinvoiceDate(String taxinvoiceDate) {
		this.taxinvoiceDate = taxinvoiceDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getPlanQty() {
		return planQty;
	}

	public void setPlanQty(BigDecimal planQty) {
		this.planQty = planQty;
	}

	public BigDecimal getActualQty() {
		return actualQty;
	}

	public void setActualQty(BigDecimal actualQty) {
		this.actualQty = actualQty;
	}

	public BigDecimal getLineAmt() {
		return lineAmt;
	}

	public void setLineAmt(BigDecimal lineAmt) {
		this.lineAmt = lineAmt;
	}

	public BigDecimal getSumLinesAmt() {
		return sumLinesAmt;
	}

	public void setSumLinesAmt(BigDecimal sumLinesAmt) {
		this.sumLinesAmt = sumLinesAmt;
	}

	public BigDecimal getVatAmt() {
		return vatAmt;
	}

	public void setVatAmt(BigDecimal vatAmt) {
		this.vatAmt = vatAmt;
	}

	public BigDecimal getSumTotalAmt() {
		return sumTotalAmt;
	}

	public void setSumTotalAmt(BigDecimal sumTotalAmt) {
		this.sumTotalAmt = sumTotalAmt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
