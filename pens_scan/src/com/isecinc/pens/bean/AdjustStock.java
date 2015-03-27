package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class AdjustStock implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	private int no;
	private String transactionDate;
	private String storeCode;
	private String storeName;
	private String bankNo;
	private String org;
	private String subInv;
	private String documentNo;
	private String ref;
	private String status;
	private String statusDesc;
	private String statusMessage;//Message Oracle
	
	private int seqNo;
	private String itemIssue;
	private String itemIssueDesc;
	private String itemIssueUom;
	private double itemIssueRetailNonVat;
	private int itemIssueQty;
	private String itemReceipt;
	private String itemReceiptDesc;
	private String itemReceiptUom;
	private double itemReceiptRetailNonVat;
	private int itemReceiptQty;
	private double diffCost;
	
	private String createUser;
	private String updateUser;

	//optional
	private List<AdjustStock> items;
	private boolean canEdit = true;
	private boolean canExport = false;
	private boolean disableHead = false;
	
	
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public boolean isDisableHead() {
		return disableHead;
	}
	public void setDisableHead(boolean disableHead) {
		this.disableHead = disableHead;
	}
	public boolean isCanExport() {
		return canExport;
	}
	public void setCanExport(boolean canExport) {
		this.canExport = canExport;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public List<AdjustStock> getItems() {
		return items;
	}
	public void setItems(List<AdjustStock> items) {
		this.items = items;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getDiffCost() {
		return diffCost;
	}
	public void setDiffCost(double diffCost) {
		this.diffCost = diffCost;
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
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getItemIssue() {
		return itemIssue;
	}
	public void setItemIssue(String itemIssue) {
		this.itemIssue = itemIssue;
	}
	public String getItemIssueDesc() {
		return itemIssueDesc;
	}
	public void setItemIssueDesc(String itemIssueDesc) {
		this.itemIssueDesc = itemIssueDesc;
	}
	public String getItemIssueUom() {
		return itemIssueUom;
	}
	public void setItemIssueUom(String itemIssueUom) {
		this.itemIssueUom = itemIssueUom;
	}
	public double getItemIssueRetailNonVat() {
		return itemIssueRetailNonVat;
	}
	public void setItemIssueRetailNonVat(double itemIssueRetailNonVat) {
		this.itemIssueRetailNonVat = itemIssueRetailNonVat;
	}
	public int getItemIssueQty() {
		return itemIssueQty;
	}
	public void setItemIssueQty(int itemIssueQty) {
		this.itemIssueQty = itemIssueQty;
	}
	public String getItemReceipt() {
		return itemReceipt;
	}
	public void setItemReceipt(String itemReceipt) {
		this.itemReceipt = itemReceipt;
	}
	public String getItemReceiptDesc() {
		return itemReceiptDesc;
	}
	public void setItemReceiptDesc(String itemReceiptDesc) {
		this.itemReceiptDesc = itemReceiptDesc;
	}
	public String getItemReceiptUom() {
		return itemReceiptUom;
	}
	public void setItemReceiptUom(String itemReceiptUom) {
		this.itemReceiptUom = itemReceiptUom;
	}
	public double getItemReceiptRetailNonVat() {
		return itemReceiptRetailNonVat;
	}
	public void setItemReceiptRetailNonVat(double itemReceiptRetailNonVat) {
		this.itemReceiptRetailNonVat = itemReceiptRetailNonVat;
	}
	public int getItemReceiptQty() {
		return itemReceiptQty;
	}
	public void setItemReceiptQty(int itemReceiptQty) {
		this.itemReceiptQty = itemReceiptQty;
	}

	
}
