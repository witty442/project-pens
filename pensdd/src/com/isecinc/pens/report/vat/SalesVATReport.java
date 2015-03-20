package com.isecinc.pens.report.vat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;



/**
 * Sales Target Summary Report
 * 
 * @author PasuwatW
 * 
 */

public class SalesVATReport implements Serializable {
	
	public SalesVATReport(){
		//this.isShowCancel = "Y";
	}
	
	// Defind Parameter
	private String period;
	private String isShowCancel;
	
	private String paramDisplay;
    
    private String taxinvoiceDate;
    private String taxinvoiceNo;
    private String customerName;
    private String taxinvoiceStatus;
    private BigDecimal taxbaseAmt;
    private BigDecimal vatAmt;
    private BigDecimal totalAmt;
    
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getIsShowCancel() {
		return isShowCancel;
	}
	public void setIsShowCancel(String isShowCancel) {
		this.isShowCancel = isShowCancel;
	}
	public String getTaxinvoiceDate() {
		return taxinvoiceDate;
	}
	public void setTaxinvoiceDate(String taxinvoiceDate) {
		this.taxinvoiceDate = taxinvoiceDate;
	}
	public String getTaxinvoiceNo() {
		return taxinvoiceNo;
	}
	public void setTaxinvoiceNo(String taxinvoiceNo) {
		this.taxinvoiceNo = taxinvoiceNo;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public BigDecimal getTaxbaseAmt() {
		return taxbaseAmt;
	}
	public void setTaxbaseAmt(BigDecimal taxbaseAmt) {
		this.taxbaseAmt = taxbaseAmt;
	}
	public BigDecimal getVatAmt() {
		return vatAmt;
	}
	public void setVatAmt(BigDecimal vatAmt) {
		this.vatAmt = vatAmt;
	}
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
	public String getTaxinvoiceStatus() {
		return taxinvoiceStatus;
	}
	public void setTaxinvoiceStatus(String taxinvoiceStatus) {
		this.taxinvoiceStatus = taxinvoiceStatus;
	}
	public String getParamDisplay() {
		return paramDisplay;
	}
	public void setParamDisplay(String paramDisplay) {
		this.paramDisplay = paramDisplay;
	}
}
