package com.isecinc.pens.bean;

import java.io.Serializable;

public class StoreBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -627774931278376588L;
	private String orderNo;
	private String barOnBox;
	private String storeCode;
	private String storeName;
	private String storeDisp;
	private String storeDispShort;
	private String item;
	private String barcode;
	private String qty;
	private String billType;
	private String validFrom;
	private String validTo;
	private String groupCode;
	private String size;
	private String color;
	
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBarOnBox() {
		return barOnBox;
	}
	public void setBarOnBox(String barOnBox) {
		this.barOnBox = barOnBox;
	}
	public String getBillType() {
		return billType;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public String getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(String validFrom) {
		this.validFrom = validFrom;
	}
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
	public String getStoreDispShort() {
		return storeDispShort;
	}
	public void setStoreDispShort(String storeDispShort) {
		this.storeDispShort = storeDispShort;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getStoreDisp() {
		return storeDisp;
	}
	public void setStoreDisp(String storeDisp) {
		this.storeDisp = storeDisp;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	
	
}
