package com.isecinc.pens.web.autosubb2b;

import java.io.Serializable;
import java.util.List;

import com.isecinc.pens.bean.LockItemOrderBean;

public class AutoSubB2BBean implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2316757033707273221L;
	private String refNo;
	private String refType;
	private String refTypeDesc;
	private String fromCustGroup;
	private String fromStoreCode;
	private String fromSubInv;
	private String fromStoreName;
	private String toCustGroup;
	private String toStoreCode;
	private String toStoreNo;
	private String toSubInv;
	private String toStoreName;
	private String reason;
	
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
	private List<AutoSubB2BBean> items;
	private String rmaOrder;
	private String cnNo;
	private String cnDate;
	private String refInv;
	private String seq;
	
	private String mode;
	private boolean canApprove;
	private boolean canSave;
	private boolean canCancel;
	private String forwarder;
	private String forwarderBox;
	private String intFlag;
	private String intMessage;
	
	
	public String getToStoreNo() {
		return toStoreNo;
	}
	public void setToStoreNo(String toStoreNo) {
		this.toStoreNo = toStoreNo;
	}
	public String getIntMessage() {
		return intMessage;
	}
	public void setIntMessage(String intMessage) {
		this.intMessage = intMessage;
	}
	public String getIntFlag() {
		return intFlag;
	}
	public void setIntFlag(String intFlag) {
		this.intFlag = intFlag;
	}
	public String getRefTypeDesc() {
		return refTypeDesc;
	}
	public void setRefTypeDesc(String refTypeDesc) {
		this.refTypeDesc = refTypeDesc;
	}
	public boolean isCanApprove() {
		return canApprove;
	}
	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}
	public String getRefNo() {
		return refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public String getFromCustGroup() {
		return fromCustGroup;
	}
	public void setFromCustGroup(String fromCustGroup) {
		this.fromCustGroup = fromCustGroup;
	}
	public String getFromStoreCode() {
		return fromStoreCode;
	}
	public void setFromStoreCode(String fromStoreCode) {
		this.fromStoreCode = fromStoreCode;
	}
	public String getFromSubInv() {
		return fromSubInv;
	}
	public void setFromSubInv(String fromSubInv) {
		this.fromSubInv = fromSubInv;
	}
	public String getFromStoreName() {
		return fromStoreName;
	}
	public void setFromStoreName(String fromStoreName) {
		this.fromStoreName = fromStoreName;
	}
	public String getToCustGroup() {
		return toCustGroup;
	}
	public void setToCustGroup(String toCustGroup) {
		this.toCustGroup = toCustGroup;
	}
	public String getToStoreCode() {
		return toStoreCode;
	}
	public void setToStoreCode(String toStoreCode) {
		this.toStoreCode = toStoreCode;
	}
	public String getToSubInv() {
		return toSubInv;
	}
	public void setToSubInv(String toSubInv) {
		this.toSubInv = toSubInv;
	}
	public String getToStoreName() {
		return toStoreName;
	}
	public void setToStoreName(String toStoreName) {
		this.toStoreName = toStoreName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
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
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
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
	public List<AutoSubB2BBean> getItems() {
		return items;
	}
	public void setItems(List<AutoSubB2BBean> items) {
		this.items = items;
	}
	
	
	
}
