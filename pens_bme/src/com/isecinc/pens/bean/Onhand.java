package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class Onhand implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
    
	private String boxNo;
	private String pensItem;
	private String materialMaster;
	private String groupCode;
	private String barcode;
	private String qty;
	private String onhandQty;
	private String issueQty;
	private String wholePriceBF;
	private String retailPriceBF;
	private String wareHouse;
	
	private List<Onhand> items;
	private String createUser;
	private String updateUser;

	//optional
	private boolean canEdit;
	private boolean canCancel;
	
	
	public String getWareHouse() {
		return wareHouse;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}
	public String getIssueQty() {
		return issueQty;
	}
	public void setIssueQty(String issueQty) {
		this.issueQty = issueQty;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
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
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
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
	public List<Onhand> getItems() {
		return items;
	}
	public void setItems(List<Onhand> items) {
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
