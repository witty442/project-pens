package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class MoveStockWarehouseBean implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	private String warehouseFrom;
	private String warehouseTo;
	private String dateFrom;
	private String dateTo;
	private String groupCodeSearch;
	
	private String wareHouse;
	private String groupCode;
	private String materialMaster;
	private String barcode;
	private String onhandQty;
	private String transferQty;
	private String pensItem;
	
	private int totalTransferQty;
	private int totalOnhandQty;
	private String createUser;
	private String createDate;
	private String updateUser;

	//optional
	private boolean canEdit;
	private boolean canCancel;
	private String lineItemStyle ;
	private String lineItemId ;
	private List<MoveStockWarehouseBean> items;

	
	
	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getPensItem() {
		return pensItem;
	}

	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}

	public String getWarehouseFrom() {
		return warehouseFrom;
	}

	public void setWarehouseFrom(String warehouseFrom) {
		this.warehouseFrom = warehouseFrom;
	}

	public String getWarehouseTo() {
		return warehouseTo;
	}

	public void setWarehouseTo(String warehouseTo) {
		this.warehouseTo = warehouseTo;
	}

	public String getGroupCodeSearch() {
		return groupCodeSearch;
	}

	public void setGroupCodeSearch(String groupCodeSearch) {
		this.groupCodeSearch = groupCodeSearch;
	}

	public String getLineItemStyle() {
		return lineItemStyle;
	}

	public void setLineItemStyle(String lineItemStyle) {
		this.lineItemStyle = lineItemStyle;
	}

	public String getLineItemId() {
		return lineItemId;
	}

	public void setLineItemId(String lineItemId) {
		this.lineItemId = lineItemId;
	}

	public int getTotalTransferQty() {
		return totalTransferQty;
	}

	public void setTotalTransferQty(int totalTransferQty) {
		this.totalTransferQty = totalTransferQty;
	}

	public int getTotalOnhandQty() {
		return totalOnhandQty;
	}

	public void setTotalOnhandQty(int totalOnhandQty) {
		this.totalOnhandQty = totalOnhandQty;
	}

	public String getWareHouse() {
		return wareHouse;
	}

	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getMaterialMaster() {
		return materialMaster;
	}

	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
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

	public String getTransferQty() {
		return transferQty;
	}

	public void setTransferQty(String transferQty) {
		this.transferQty = transferQty;
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

	public List<MoveStockWarehouseBean> getItems() {
		return items;
	}

	public void setItems(List<MoveStockWarehouseBean> items) {
		this.items = items;
	}
	
	
}
