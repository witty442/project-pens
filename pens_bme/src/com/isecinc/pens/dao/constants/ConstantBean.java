package com.isecinc.pens.dao.constants;

import java.io.Serializable;

public class ConstantBean implements Serializable {
	
  private static final long serialVersionUID = 5113179498389565433L;
  private String conType;
  private String conCode;
  private String conValue;
  private String conDisp;
  private String isactive;
  private String conDesc;
  
  public ConstantBean(String conType,String conCode,String conValue,String conDisp){
	  this.conType = conType;
	  this.conCode = conCode;
	  this.conValue = conValue;
	  this.conDisp =conDisp;
  }
  public ConstantBean(){
	  
  }
  
public String getConDisp() {
	return conDisp;
}
public void setConDisp(String conDisp) {
	this.conDisp = conDisp;
}
public String getConType() {
	return conType;
}
public void setConType(String conType) {
	this.conType = conType;
}
public String getConCode() {
	return conCode;
}
public void setConCode(String conCode) {
	this.conCode = conCode;
}
public String getConValue() {
	return conValue;
}
public void setConValue(String conValue) {
	this.conValue = conValue;
}
public String getIsactive() {
	return isactive;
}
public void setIsactive(String isactive) {
	this.isactive = isactive;
}
public String getConDesc() {
	return conDesc;
}
public void setConDesc(String conDesc) {
	this.conDesc = conDesc;
}

  
  
}
