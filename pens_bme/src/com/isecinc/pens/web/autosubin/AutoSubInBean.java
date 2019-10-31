package com.isecinc.pens.web.autosubin;

import java.io.Serializable;
import java.util.List;

import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoSubInBean implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2316757033707273221L;
	private String jobId;
	private String jobName;
	private String custGroup;
	private String storeCode;
	private String subInv;
	private String storeName;
	private String cuttOffDate;
	private String jobStatus;
	private String boxNo;
	private String totalBox;
	private String totalQty;
	private String totalAmount;
	private String status;
	private String pensItem;
	private String inventoryItemId;
	private String itemName;
	private String unitPrice;
	private String qty;
	private String amount;
	private String userName;
	private String keyData;
	private List<AutoSubInBean> items;
	private String rmaOrder;
	private String cnNo;
	private String cnDate;
	private String refInv;
	private String seq;
	
	private String mode;
	private boolean canSave;
	private boolean canCancel;
	private String forwarder;
	private String forwarderBox;
	
	public String getForwarderBox() {
		return forwarderBox;
	}
	public void setForwarderBox(String forwarderBox) {
		this.forwarderBox = forwarderBox;
	}
	public String getForwarder() {
		return forwarder;
	}
	public void setForwarder(String forwarder) {
		this.forwarder = forwarder;
	}
	
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getRmaOrder() {
		return rmaOrder;
	}
	public void setRmaOrder(String rmaOrder) {
		this.rmaOrder = rmaOrder;
	}
	public String getCnNo() {
		return cnNo;
	}
	public void setCnNo(String cnNo) {
		this.cnNo = cnNo;
	}
	public String getCnDate() {
		return cnDate;
	}
	public void setCnDate(String cnDate) {
		this.cnDate = cnDate;
	}
	public String getRefInv() {
		return refInv;
	}
	public void setRefInv(String refInv) {
		this.refInv = refInv;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
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
	
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
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
	public List<AutoSubInBean> getItems() {
		return items;
	}
	public void setItems(List<AutoSubInBean> items) {
		this.items = items;
	}
	
	
	
}
