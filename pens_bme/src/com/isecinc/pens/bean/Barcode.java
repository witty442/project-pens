package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class Barcode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9008978294153751220L;
	private int no;
	private String transactionDate;
	private String boxNo;
	private String boxNoRef;
	private String jobId;
	private String name;
	private String status;
	private String statusDesc;
	private String remark;
	private String wareHouseDesc;
	private String wareHouse;
	private String grNo;
	private String createUser;
	private String updateUser;
	private List<Barcode> items;
    
	private int lineId;
	private String barcodeReadonly;
	private String barcodeStyle ;
	
	private String groupCode;
	private String barcode;
	private String materialMaster;
	private String pensItem;
	private String wholePriceBF;
	private String retailPriceBF;
	
	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	private boolean canEditGrNo = false;
	private int qty;
	private int qtyTemp;
	private int totalQty;
	private String totalQtyDisp;
	
	private String pensItemFrom;
	private String pensItemTo;
	private String groupCodeFrom;
	private String groupCodeTo;
	private String boxNoFrom;
	private String boxNoTo;
	private String summaryType;
	
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;
	private List<BoxRef> boxRefItems;
	private String includeCancel;
	private String type;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTotalQtyDisp() {
		return totalQtyDisp;
	}
	public void setTotalQtyDisp(String totalQtyDisp) {
		this.totalQtyDisp = totalQtyDisp;
	}
	public boolean isCanEditGrNo() {
		return canEditGrNo;
	}
	public void setCanEditGrNo(boolean canEditGrNo) {
		this.canEditGrNo = canEditGrNo;
	}
	public String getGrNo() {
		return grNo;
	}
	public void setGrNo(String grNo) {
		this.grNo = grNo;
	}
	public int getQtyTemp() {
		return qtyTemp;
	}
	public void setQtyTemp(int qtyTemp) {
		this.qtyTemp = qtyTemp;
	}
	public String getIncludeCancel() {
		return includeCancel;
	}
	public void setIncludeCancel(String includeCancel) {
		this.includeCancel = includeCancel;
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
	public String getBoxNoRef() {
		return boxNoRef;
	}
	public void setBoxNoRef(String boxNoRef) {
		this.boxNoRef = boxNoRef;
	}
	public List<BoxRef> getBoxRefItems() {
		return boxRefItems;
	}
	public void setBoxRefItems(List<BoxRef> boxRefItems) {
		this.boxRefItems = boxRefItems;
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
	public int getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getBoxNoFrom() {
		return boxNoFrom;
	}
	public void setBoxNoFrom(String boxNoFrom) {
		this.boxNoFrom = boxNoFrom;
	}
	public String getBoxNoTo() {
		return boxNoTo;
	}
	public void setBoxNoTo(String boxNoTo) {
		this.boxNoTo = boxNoTo;
	}
	public String getPensItemFrom() {
		return pensItemFrom;
	}
	public void setPensItemFrom(String pensItemFrom) {
		this.pensItemFrom = pensItemFrom;
	}
	public String getPensItemTo() {
		return pensItemTo;
	}
	public void setPensItemTo(String pensItemTo) {
		this.pensItemTo = pensItemTo;
	}
	public String getGroupCodeFrom() {
		return groupCodeFrom;
	}
	public void setGroupCodeFrom(String groupCodeFrom) {
		this.groupCodeFrom = groupCodeFrom;
	}
	public String getGroupCodeTo() {
		return groupCodeTo;
	}
	public void setGroupCodeTo(String groupCodeTo) {
		this.groupCodeTo = groupCodeTo;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getBarcodeReadonly() {
		return barcodeReadonly;
	}
	public void setBarcodeReadonly(String barcodeReadonly) {
		this.barcodeReadonly = barcodeReadonly;
	}
	public String getBarcodeStyle() {
		return barcodeStyle;
	}
	public void setBarcodeStyle(String barcodeStyle) {
		this.barcodeStyle = barcodeStyle;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
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
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
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
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
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
	public List<Barcode> getItems() {
		return items;
	}
	public void setItems(List<Barcode> items) {
		this.items = items;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
