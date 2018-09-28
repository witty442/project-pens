package com.isecinc.pens.bean;

import java.io.Serializable;

public class GenVanScriptBean implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	private String saleCode;
	private String prefix;
	private String customerCode;
	private String scriptSQL;
	
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSaleCode() {
		return saleCode;
	}
	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getScriptSQL() {
		return scriptSQL;
	}
	public void setScriptSQL(String scriptSQL) {
		this.scriptSQL = scriptSQL;
	}
	

}
