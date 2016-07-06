package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class ScanCheckBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9008978294153751220L;
	private int no;
	
	private String issueReqNo;
	private String issueReqDate;
	private String checkOutDate;
	private String boxNo;
	private String groupCode;
	private String materialMaster;
	private String pensItem;
	private String wareHouseDesc;
	private String wareHouse;
	private String createUser;
	private String updateUser;
	private String status;
	private String statusDesc;
	private String custGroup;
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;
	private String summaryType;
	private List<ScanCheckBean> items;
	private int lineId;
	private int totalReqQty;
	private int totalQty;
	private String remark;
	private String barcode;
	private boolean barcodeReadonly;
	private String barcodeStyle;
	private String requestor;
	private int totalBox;
	

	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	private boolean canPrint = false;
	
	
	public boolean isCanPrint() {
		return canPrint;
	}
	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}
	public int getTotalBox() {
		return totalBox;
	}
	public void setTotalBox(int totalBox) {
		this.totalBox = totalBox;
	}
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	public boolean isBarcodeReadonly() {
		return barcodeReadonly;
	}
	public void setBarcodeReadonly(boolean barcodeReadonly) {
		this.barcodeReadonly = barcodeReadonly;
	}
	public String getBarcodeStyle() {
		return barcodeStyle;
	}
	public void setBarcodeStyle(String barcodeStyle) {
		this.barcodeStyle = barcodeStyle;
	}
	public String getIssueReqDate() {
		return issueReqDate;
	}
	public void setIssueReqDate(String issueReqDate) {
		this.issueReqDate = issueReqDate;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getIssueReqNo() {
		return issueReqNo;
	}
	public void setIssueReqNo(String issueReqNo) {
		this.issueReqNo = issueReqNo;
	}
	public String getCheckOutDate() {
		return checkOutDate;
	}
	public void setCheckOutDate(String checkOutDate) {
		this.checkOutDate = checkOutDate;
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
	public String getMaterialMaster() {
		return materialMaster;
	}
	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getWareHouseDesc() {
		return wareHouseDesc;
	}
	public void setWareHouseDesc(String wareHouseDesc) {
		this.wareHouseDesc = wareHouseDesc;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
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
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
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
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
	public List<ScanCheckBean> getItems() {
		return items;
	}
	public void setItems(List<ScanCheckBean> items) {
		this.items = items;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public int getTotalReqQty() {
		return totalReqQty;
	}
	public void setTotalReqQty(int totalReqQty) {
		this.totalReqQty = totalReqQty;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
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
