package com.isecinc.pens.bean;

import java.io.Serializable;

public class BoxRef implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -709891804925669249L;
	private String boxNo;
	private String boxNoRef;
	
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getBoxNoRef() {
		return boxNoRef;
	}
	public void setBoxNoRef(String boxNoRef) {
		this.boxNoRef = boxNoRef;
	}

	
}
