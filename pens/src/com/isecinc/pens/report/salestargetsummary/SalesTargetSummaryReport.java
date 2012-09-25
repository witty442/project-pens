package com.isecinc.pens.report.salestargetsummary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;



/**
 * Sales Target Summary Report
 * 
 * @author PasuwatW
 * 
 */

public class SalesTargetSummaryReport implements Serializable , Comparable {
	// Defind Parameter
	private String productCodeFrom;
    private String productCodeTo;
    private String dateFrom;
    private String dateTo;
    
    private String salesCode;
    private String salesName;
	private String productCode;
    private String productName;
	private Timestamp tDateFrom;
    private Timestamp tDateTo;
    private String uomId;
    private String uomName;
    private String targetQty;
    private BigDecimal unitPrice;
    private BigDecimal targetAmt;
    private String salesQty;
    private BigDecimal salesAmt;
    private BigDecimal salesCompareTargetPct;
    
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
	public String getUomId() {
		return uomId;
	}
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	public String getUomName() {
		return uomName;
	}
	public void setUomName(String uomName) {
		this.uomName = uomName;
	}
	public String getTargetQty() {
		return targetQty;
	}
	public void setTargetQty(String targetQty) {
		this.targetQty = targetQty;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getSalesQty() {
		return salesQty;
	}
	public void setSalesQty(String salesQty) {
		this.salesQty = salesQty;
	}
	public BigDecimal getSalesAmt() {
		return salesAmt;
	}
	public void setSalesAmt(BigDecimal salesAmt) {
		this.salesAmt = salesAmt;
	}
	public BigDecimal getSalesCompareTargetPct() {
		return salesCompareTargetPct;
	}
	public void setSalesCompareTargetPct(BigDecimal salesCompareTargetPct) {
		this.salesCompareTargetPct = salesCompareTargetPct;
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
	public String getSalesCode() {
		return salesCode;
	}
	public void setSalesCode(String salesCode) {
		this.salesCode = salesCode;
	}
	public String getSalesName() {
		return salesName;
	}
	public void setSalesName(String salesName) {
		this.salesName = salesName;
	}
	public BigDecimal getTargetAmt() {
		return targetAmt;
	}
	public void setTargetAmt(BigDecimal targetAmt) {
		this.targetAmt = targetAmt;
	}
	public Timestamp gettDateFrom() {
		return tDateFrom;
	}
	public void settDateFrom(Timestamp tDateFrom) {
		this.tDateFrom = tDateFrom;
	}
	public Timestamp gettDateTo() {
		return tDateTo;
	}
	public void settDateTo(Timestamp tDateTo) {
		this.tDateTo = tDateTo;
	}
	
	public int compareTo(Object o) {
		if (!(o instanceof SalesTargetSummaryReport))
		      throw new ClassCastException();

		SalesTargetSummaryReport report = (SalesTargetSummaryReport) o;

		return productCode.compareTo(report.getProductCode());
	}
}
