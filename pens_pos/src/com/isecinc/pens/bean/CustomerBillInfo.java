package com.isecinc.pens.bean;

public class CustomerBillInfo {
	private int customerBillId;
	private String customerName;
	private String addressDesc;
	private String idNo;
	private String passportNo;
	private String createdBy;
	
	
	public int getCustomerBillId() {
		return customerBillId;
	}
	public void setCustomerBillId(int customerBillId) {
		this.customerBillId = customerBillId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getAddressDesc() {
		return addressDesc;
	}
	public void setAddressDesc(String addressDesc) {
		this.addressDesc = addressDesc;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getPassportNo() {
		return passportNo;
	}
	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}
	
	
}
