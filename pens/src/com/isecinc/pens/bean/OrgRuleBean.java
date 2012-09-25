package com.isecinc.pens.bean;

import java.io.Serializable;

public class OrgRuleBean implements Serializable {
 
 private static final long serialVersionUID = 1L;
 private String org;
 private String subInv;
 private String name;
 private String defaultValue;
 private String checkItem;
 
 
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
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDefaultValue() {
	return defaultValue;
}
public void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
}
public String getCheckItem() {
	return checkItem;
}
public void setCheckItem(String checkItem) {
	this.checkItem = checkItem;
}
 
 
}
