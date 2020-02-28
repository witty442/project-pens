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
	private String storeType;
	private String subInv;
	private String storeNo;
	private String pensItem;
	private String groupCode;
	private String sizeColor;
	private String materialMaster;
    private String shopOnhandQty;
    private String wacoalOnhandQty;
    private String orderQty;
    private String priority;
    private String minQty;
    private String maxQty;
    
    private String createDate;
	private String createUser;
	private String backSalesDay;
	private String dayCover;
	private String salesQty;
	private String salesQtyPerDay;
	private String stockByCoverDay;
	private String recommendCalcQty;
	private String recommendQty;
	private String retailPriceBF;
	
	private boolean canSave;
	private String status;
	private String userName;
	
	
	public String getRecommendCalcQty() {
		return recommendCalcQty;
	}
	public void setRecommendCalcQty(String recommendCalcQty) {
		this.recommendCalcQty = recommendCalcQty;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isCanSave() {
		return canSave;
	}
	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}
	public String getSizeColor() {
		return sizeColor;
	}
	public void setSizeColor(String sizeColor) {
		this.sizeColor = sizeColor;
	}
	public String getSalesQty() {
		return salesQty;
	}
	public void setSalesQty(String salesQty) {
		this.salesQty = salesQty;
	}
	public String getRetailPriceBF() {
		return retailPriceBF;
	}
	public void setRetailPriceBF(String retailPriceBF) {
		this.retailPriceBF = retailPriceBF;
	}
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	public String getWacoalOnhandQty() {
		return wacoalOnhandQty;
	}
	public void setWacoalOnhandQty(String wacoalOnhandQty) {
		this.wacoalOnhandQty = wacoalOnhandQty;
	}
	public String getOrderQty() {
		return orderQty;
	}
	public void setOrderQty(String orderQty) {
		this.orderQty = orderQty;
	}
	public String getBackSalesDay() {
		return backSalesDay;
	}
	public void setBackSalesDay(String backSalesDay) {
		this.backSalesDay = backSalesDay;
	}
	public String getDayCover() {
		return dayCover;
	}
	public void setDayCover(String dayCover) {
		this.dayCover = dayCover;
	}
	public String getSalesQtyPerDay() {
		return salesQtyPerDay;
	}
	public void setSalesQtyPerDay(String salesQtyPerDay) {
		this.salesQtyPerDay = salesQtyPerDay;
	}
	public String getStockByCoverDay() {
		return stockByCoverDay;
	}
	public void setStockByCoverDay(String stockByCoverDay) {
		this.stockByCoverDay = stockByCoverDay;
	}
	public String getRecommendQty() {
		return recommendQty;
	}
	public void setRecommendQty(String recommendQty) {
		this.recommendQty = recommendQty;
	}
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
	
	public String getShopOnhandQty() {
		return shopOnhandQty;
	}
	public void setShopOnhandQty(String shopOnhandQty) {
		this.shopOnhandQty = shopOnhandQty;
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
