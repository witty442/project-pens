package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.isecinc.core.model.I_PO;

public class TransferBean implements Serializable{

	
	/**
	 * Default Constructor
	 */
	public TransferBean() {}
	

	private static final long serialVersionUID = 8286170868166006317L;
	private String transferDateFrom ;
	private String transferDateTo; 
	private String transferDate; 
	private String transferTime; 
	private String transferType;
	private String transferTypeLabel;
	private String transferBank; 
	private String transferBankLabel; 
	private String amount; 
	private String chequeNo; 
	private String chequeDate; 
	private String exported; 
	private String exportedLabel; 
	private String createDate;
	private String status;
	private String statusLabel;
	private String no;
	private int lineId;
	private String keyData;
	private String userId;
	private String createdBy;
	private String updateBy;
	private boolean canEdit = false;
	private String message = "";
	private List<TransferBean> items;
	
	
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTransferTypeLabel() {
		return transferTypeLabel;
	}
	public void setTransferTypeLabel(String transferTypeLabel) {
		this.transferTypeLabel = transferTypeLabel;
	}
	public String getTransferBankLabel() {
		return transferBankLabel;
	}
	public void setTransferBankLabel(String transferBankLabel) {
		this.transferBankLabel = transferBankLabel;
	}
	public String getExportedLabel() {
		return exportedLabel;
	}
	public void setExportedLabel(String exportedLabel) {
		this.exportedLabel = exportedLabel;
	}
	public String getStatusLabel() {
		return statusLabel;
	}
	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<TransferBean> getItems() {
		return items;
	}
	public void setItems(List<TransferBean> items) {
		this.items = items;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createBy) {
		this.createdBy = createBy;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getKeyData() {
		return keyData;
	}
	public void setKeyData(String keyData) {
		this.keyData = keyData;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public String getTransferDateFrom() {
		return transferDateFrom;
	}
	public void setTransferDateFrom(String transferDateFrom) {
		this.transferDateFrom = transferDateFrom;
	}
	public String getTransferDateTo() {
		return transferDateTo;
	}
	public void setTransferDateTo(String transferDateTo) {
		this.transferDateTo = transferDateTo;
	}
	public String getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}
	public String getTransferTime() {
		return transferTime;
	}
	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferBank() {
		return transferBank;
	}
	public void setTransferBank(String transferBank) {
		this.transferBank = transferBank;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getChequeDate() {
		return chequeDate;
	}
	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
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
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	
}
