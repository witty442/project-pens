package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ImportSummary implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -475577800289303209L;
	private int row;
	private String salesDate;
	private String storeNo;
	private String storeName;
	private String description;
	private String qty;
	private String message;
	private String materialMaster;
	private String barcode;
	private OnhandSummary onhandSummary;
	private List<Message> errorMsgList;
	
	private String storeType;
	private String boxNo;
	private Date importDate;
	private String lineArr;
	
	//option
	private String groupCode;
	private String pensItem;
	private String itemWacoal;
	
	
	public String getItemWacoal() {
		return itemWacoal;
	}
	public void setItemWacoal(String itemWacoal) {
		this.itemWacoal = itemWacoal;
	}
	public String getLineArr() {
		return lineArr;
	}
	public void setLineArr(String lineArr) {
		this.lineArr = lineArr;
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
	public String getStoreType() {
		return storeType;
	}
	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public Date getImportDate() {
		return importDate;
	}
	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}
	public List<Message> getErrorMsgList() {
		return errorMsgList;
	}
	public void setErrorMsgList(List<Message> errorMsgList) {
		this.errorMsgList = errorMsgList;
	}
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public OnhandSummary getOnhandSummary() {
		return onhandSummary;
	}
	public void setOnhandSummary(OnhandSummary onhandSummary) {
		this.onhandSummary = onhandSummary;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getSalesDate() {
		return salesDate;
	}
	public void setSalesDate(String salesDate) {
		this.salesDate = salesDate;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
