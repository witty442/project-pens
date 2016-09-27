package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class AdjustStockSA implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	private int no;
	private String transactionDate;
	private String storeCode;
	private String storeName;
	private String documentNo;
	private String status;
	private String statusDesc;
	private String statusMessage;//Message Oracle
	
	private int seqNo;
	private String itemAdjust;
	private String groupCode;
	
	private String itemAdjustUom;
	private String itemAdjustQty;
	
	private String createUser;
	private String updateUser;

	//optional
	private List<AdjustStockSA> items;
	private boolean canEdit = true;
	private boolean canCancel = false;
	private boolean disableHead = false;
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
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
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getItemAdjust() {
		return itemAdjust;
	}
	public void setItemAdjust(String itemAdjust) {
		this.itemAdjust = itemAdjust;
	}
    
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getItemAdjustUom() {
		return itemAdjustUom;
	}
	public void setItemAdjustUom(String itemAdjustUom) {
		this.itemAdjustUom = itemAdjustUom;
	}
	public String getItemAdjustQty() {
		return itemAdjustQty;
	}
	public void setItemAdjustQty(String itemAdjustQty) {
		this.itemAdjustQty = itemAdjustQty;
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
	public List<AdjustStockSA> getItems() {
		return items;
	}
	public void setItems(List<AdjustStockSA> items) {
		this.items = items;
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
	public boolean isDisableHead() {
		return disableHead;
	}
	public void setDisableHead(boolean disableHead) {
		this.disableHead = disableHead;
	}
	
	
	
	
	
}
