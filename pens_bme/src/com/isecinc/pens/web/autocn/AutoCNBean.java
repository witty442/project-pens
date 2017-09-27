package com.isecinc.pens.web.autocn;

import java.util.List;

import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoCNBean {


	private String jobId;
	private String jobName;
	private String custGroup;
	private String storeCode;
	private String storeName;
	private String cuttOffDate;
	private String jobStatus;
	private String rtnNo;
	private String totalBox;
	private String totalQty;
	private String totalAmount;
	private String status;
	private String pensItem;
	private String lineChk;
	private String inventoryItemId;
	private String itemName;
	private String unitPrice;
	private String qty;
	private String amount;
	private String userName;
	private String keyData;
	private List<AutoCNBean> items;
	
	private String mode;
	private boolean canSave;
	private boolean canCancel;
	
	
	public boolean isCanSave() {
		return canSave;
	}
	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getKeyData() {
		return keyData;
	}
	public void setKeyData(String keyData) {
		this.keyData = keyData;
	}
	public String getLineChk() {
		return lineChk;
	}
	public void setLineChk(String lineChk) {
		this.lineChk = lineChk;
	}
	public String getInventoryItemId() {
		return inventoryItemId;
	}
	public void setInventoryItemId(String inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCuttOffDate() {
		return cuttOffDate;
	}
	public void setCuttOffDate(String cuttOffDate) {
		this.cuttOffDate = cuttOffDate;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
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
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
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
	
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public String getRtnNo() {
		return rtnNo;
	}
	public void setRtnNo(String rtnNo) {
		this.rtnNo = rtnNo;
	}
	public String getTotalBox() {
		return totalBox;
	}
	public void setTotalBox(String totalBox) {
		this.totalBox = totalBox;
	}
	public String getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(String totalQty) {
		this.totalQty = totalQty;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<AutoCNBean> getItems() {
		return items;
	}
	public void setItems(List<AutoCNBean> items) {
		this.items = items;
	}
	
	
	
}
