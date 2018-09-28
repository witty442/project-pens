package com.isecinc.pens.bean;

import java.io.Serializable;

public class CConstantsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String refCode;
	private String refDesc;
	private String code;
	private String value;
	private String value2;
	private String isactive;
	public String getRefCode() {
		return refCode;
	}
	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}
	public String getRefDesc() {
		return refDesc;
	}
	public void setRefDesc(String refDesc) {
		this.refDesc = refDesc;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public String getIsactive() {
		return isactive;
	}
	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}
	
	
}
