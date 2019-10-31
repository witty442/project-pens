package com.isecinc.pens.web.autoorder;

import java.io.Serializable;

public class AutoOrderBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3605260858297129599L;
	private String orderDate;
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;
	private String pensItem;
	private String groupCode;
	private String materialMaster;
    private String onhandQty;
    private String priority;
    private String minQty;
    private String maxQty;
    
    private String createDate;
	private String createUser;
	
	
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
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
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getMaterialMaster() {
		return materialMaster;
	}
	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getMinQty() {
		return minQty;
	}
	public void setMinQty(String minQty) {
		this.minQty = minQty;
	}
	public String getMaxQty() {
		return maxQty;
	}
	public void setMaxQty(String maxQty) {
		this.maxQty = maxQty;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	
}
