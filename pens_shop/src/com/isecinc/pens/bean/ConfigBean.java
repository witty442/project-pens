package com.isecinc.pens.bean;

import java.io.Serializable;

public class ConfigBean implements Serializable{
  
	private int userId;
	private String customerCode;
	private String customerName;
	private int customerId;
	private int pricelistId;
	private String qualifier;
	
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getPricelistId() {
		return pricelistId;
	}
	public void setPricelistId(int pricelistId) {
		this.pricelistId = pricelistId;
	}
	
	
}
