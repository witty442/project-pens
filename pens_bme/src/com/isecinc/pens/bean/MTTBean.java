package com.isecinc.pens.bean;

import java.util.List;


public class MTTBean {

	private int lineId;
	private int no;
	private String saleDateFrom;
	private String saleDateTo;
	
	private String saleDate;
	private String docNo;
	private String custGroup;
	private String custGroupName;
	
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;


	private String groupCode;
	private String barcode;
	private String materialMaster;
	private String pensItem;
	
	private String wholePriceBF;
	private String retailPriceBF;
	
	private String barcodeStyle;
	private boolean barcodeReadonly;
	private int qty;
	private int totalQty;
	
	private String createUser;
	private String updateUser;
	private String status;
	private String statusDesc;
	private List<MTTBean> items;
	
	private String docDate;
	private String dispType;
	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	
	
	public String getDocDate() {
		return docDate;
	}
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}
	public String getDispType() {
		return dispType;
	}
	public void setDispType(String dispType) {
		this.dispType = dispType;
	}
	public String getCustGroupName() {
		return custGroupName;
	}
	public void setCustGroupName(String custGroupName) {
		this.custGroupName = custGroupName;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public boolean isBarcodeReadonly() {
		return barcodeReadonly;
	}
	public void setBarcodeReadonly(boolean barcodeReadonly) {
		this.barcodeReadonly = barcodeReadonly;
	}
	public String getBarcodeStyle() {
		return barcodeStyle;
	}
	public void setBarcodeStyle(String barcodeStyle) {
		this.barcodeStyle = barcodeStyle;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
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
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getSaleDateFrom() {
		return saleDateFrom;
	}
	public void setSaleDateFrom(String saleDateFrom) {
		this.saleDateFrom = saleDateFrom;
	}
	public String getSaleDateTo() {
		return saleDateTo;
	}
	public void setSaleDateTo(String saleDateTo) {
		this.saleDateTo = saleDateTo;
	}
	public List<MTTBean> getItems() {
		return items;
	}
	public void setItems(List<MTTBean> items) {
		this.items = items;
	}
	
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public String getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}
	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getMaterialMaster() {
		return materialMaster;
	}
	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
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
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	
	
	
}
