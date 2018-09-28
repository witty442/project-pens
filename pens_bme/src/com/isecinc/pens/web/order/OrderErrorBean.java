package com.isecinc.pens.web.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderErrorBean {
	private String orderNo;
	private String orderDate;
	private String storeType;
	private String storeCode;
	private String storeName;
	private String barcode;
	private String groupCode;
	private String groupCodeDesc;
	private String pensItem ;
	private String qty;
	
	private StringBuffer resultError;
	private List<String> storeErrorList;
	
	
	public List<String> getStoreErrorList() {
		return storeErrorList;
	}
	public void setStoreErrorList(List<String> storeErrorList) {
		this.storeErrorList = storeErrorList;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public StringBuffer getResultError() {
		return resultError;
	}
	public void setResultError(StringBuffer resultError) {
		this.resultError = resultError;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(String storeType) {
		this.storeType = storeType;
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
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getGroupCodeDesc() {
		return groupCodeDesc;
	}
	public void setGroupCodeDesc(String groupCodeDesc) {
		this.groupCodeDesc = groupCodeDesc;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	
}
