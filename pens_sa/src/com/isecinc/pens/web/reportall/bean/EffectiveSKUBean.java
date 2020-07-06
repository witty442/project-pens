package com.isecinc.pens.web.reportall.bean;

import java.io.Serializable;

public class EffectiveSKUBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8592699909107800386L;
	private String custCatNo;
	private String salesZone;
	private String salesZoneName;
	private String customerCode;
	private String customerName;
	private String brand;
	private String brandGroup;
	private String brandName;
	private String salesrepCode;
	private StringBuffer dataStrBuffer;
	private String qty;
	private String yearMonthChk;
	    
	public String getYearMonthChk() {
		return yearMonthChk;
	}
	public void setYearMonthChk(String yearMonthChk) {
		this.yearMonthChk = yearMonthChk;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getCustCatNo() {
		return custCatNo;
	}
	public void setCustCatNo(String custCatNo) {
		this.custCatNo = custCatNo;
	}
	public String getSalesZone() {
		return salesZone;
	}
	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}
	public String getSalesZoneName() {
		return salesZoneName;
	}
	public void setSalesZoneName(String salesZoneName) {
		this.salesZoneName = salesZoneName;
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
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrandGroup() {
		return brandGroup;
	}
	public void setBrandGroup(String brandGroup) {
		this.brandGroup = brandGroup;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	
}
