package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Order implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240641577785053099L;
	private String orderNo;
	private String orderLotNo;
	private String barOnBox;
	private String orderDate;
	private String storeType;
	private String billType;
	private String region;
	private String storeCode;
	private String groupCode;
	private String groupCodeDesc;
	private String item;
	private String itemDesc;
	private String itemDisp;
	private String barcode;
	private String qty;
	private String validFrom;
	private String validTo;
	
	private String materialMaster;
    private String onhandQty;
    private String remainOnhandQty;
    private String wholePriceBF;
    private String retailPriceBF;
    

	private String exported;
	private String createDate;
	private String createUser;
	private String updateDate;
	private String updateUser;
	
	private List<Order> orderItemList;
	private List<StoreBean> storeItemList;
	private Map<String, StoreBean> orderNoByStoreMap;
//option Order History
	private String pensCustCodeFrom;
	private String pensCustNameFrom;
	private String salesDateFrom;
	private String salesDateTo;
	private String size;
    private String color;
	
	
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
	public String getGroupCodeDesc() {
		return groupCodeDesc;
	}
	public void setGroupCodeDesc(String groupCodeDesc) {
		this.groupCodeDesc = groupCodeDesc;
	}
	public String getSalesDateFrom() {
		return salesDateFrom;
	}
	public void setSalesDateFrom(String salesDateFrom) {
		this.salesDateFrom = salesDateFrom;
	}
	public String getSalesDateTo() {
		return salesDateTo;
	}
	public void setSalesDateTo(String salesDateTo) {
		this.salesDateTo = salesDateTo;
	}
	public String getPensCustNameFrom() {
		return pensCustNameFrom;
	}
	public void setPensCustNameFrom(String pensCustNameFrom) {
		this.pensCustNameFrom = pensCustNameFrom;
	}
	public String getPensCustCodeFrom() {
		return pensCustCodeFrom;
	}
	public void setPensCustCodeFrom(String pensCustCodeFrom) {
		this.pensCustCodeFrom = pensCustCodeFrom;
	}
	public List<Order> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<Order> orderItemList) {
		this.orderItemList = orderItemList;
	}
	/** optional **/
	private int countOrderNoByStoreCode;
	
	
	public int getCountOrderNoByStoreCode() {
		return countOrderNoByStoreCode;
	}
	public void setCountOrderNoByStoreCode(int countOrderNoByStoreCode) {
		this.countOrderNoByStoreCode = countOrderNoByStoreCode;
	}
	public String getOrderLotNo() {
		return orderLotNo;
	}
	public void setOrderLotNo(String orderLotNo) {
		this.orderLotNo = orderLotNo;
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
	public String getRemainOnhandQty() {
		return remainOnhandQty;
	}
	public void setRemainOnhandQty(String remainOnhandQty) {
		this.remainOnhandQty = remainOnhandQty;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getItemDisp() {
		return itemDisp;
	}
	public void setItemDisp(String itemDisp) {
		this.itemDisp = itemDisp;
	}
	public Map<String, StoreBean> getOrderNoByStoreMap() {
		return orderNoByStoreMap;
	}
	public void setOrderNoByStoreMap(Map<String, StoreBean> orderNoByStoreMap) {
		this.orderNoByStoreMap = orderNoByStoreMap;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	
	public List<StoreBean> getStoreItemList() {
		return storeItemList;
	}
	public void setStoreItemList(List<StoreBean> storeItemList) {
		this.storeItemList = storeItemList;
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
	public String getWholePriceBF() {
		return wholePriceBF;
	}
	public void setWholePriceBF(String wholePriceBF) {
		this.wholePriceBF = wholePriceBF;
	}
	public String getRetailPriceBF() {
		return retailPriceBF;
	}
	public void setRetailPriceBF(String retailPriceBF) {
		this.retailPriceBF = retailPriceBF;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
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
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
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
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	
}
