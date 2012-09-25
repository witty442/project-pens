package com.isecinc.pens.bean;

import java.io.Serializable;

public class OrgRuleItem implements Serializable{

	private static final long serialVersionUID = 1L;
	private String org;
	private String subInv;
	private String item;
	
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	 
	 
}
