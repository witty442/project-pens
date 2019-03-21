package com.isecinc.pens.bean;

//FOR MST_REF 
public class MasterItemBean {
	private String pensItem;
	private String groupCode;
	private String pensItemName;
	private String barcode;
	
	
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getPensItemName() {
		return pensItemName;
	}
	public void setPensItemName(String pensItemName) {
		this.pensItemName = pensItemName;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	
}
