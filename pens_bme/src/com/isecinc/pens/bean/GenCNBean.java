package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GenCNBean implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	//Head
	private String cnNo;

	private String jobId;
	private String jobName;
	private String storeCode;//CustomerNo
	private String storeName;
	private String subInv;
	private String storeNo;
	private String warehouse;
	private String remark;
	private String invoiceDate;
	//Line
	private int lineId;
	private String boxNo;
	private String groupCode;
	private String pensItem;
	private String barcode;
	private String materialMaster;
	private String wholePriceBF;
	private String retailPriceBF;
	private String qty;
	private String totalQty;
	
    private String createUser;
    private String updateUser;

	//optional
	private boolean canEdit = false;
    private String selected ;
	
	private List<GenCNBean> items;

	
	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
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

	public String getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(String totalQty) {
		this.totalQty = totalQty;
	}

	public String getCnNo() {
		return cnNo;
	}

	public void setCnNo(String cnNo) {
		this.cnNo = cnNo;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getPensItem() {
		return pensItem;
	}

	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
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

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public List<GenCNBean> getItems() {
		return items;
	}

	public void setItems(List<GenCNBean> items) {
		this.items = items;
	}
	

	
}
