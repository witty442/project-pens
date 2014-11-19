package com.isecinc.pens.bean;

import java.io.Serializable;
public class OnhandSummary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7240087722957778271L;
	//Criteria
	private String type;
	private String importNo;
	private String itemCodeFrom;
	private String itemCodeTo;
	private String asOfDateFrom;
	private String asOfDateTo;
	private String pensItemFrom;
	private String pensItemTo;
	private String dispZeroStock;
	
	
	private String salesDate;
	private String pensCustCodeFrom;
	private String pensCustNameFrom;
	
	//results
	private String subInv;
	private String group;
	private String groupDesc;
	private String asOfDate;
	private String fileName;
	private String materialMaster;
    private String barcode;
    private String onhandQty;
    private String wholePriceBF;
    private String retailPriceBF;
    private String item;
    private String itemDesc;
    private String createDate;
    private String createUser;
    private String status;
    private String message;
    private String pensItem;
    
    private String storeCode;
    private String storeName;
    private String saleInQty;
    private String saleReturnQty;
    private String saleOutQty;
    private String transInQty;
    private String adjustQty;
    private String stockShortQty;
    
    
	public String getAdjustQty() {
		return adjustQty;
	}
	public void setAdjustQty(String adjustQty) {
		this.adjustQty = adjustQty;
	}
	public String getStockShortQty() {
		return stockShortQty;
	}
	public void setStockShortQty(String stockShortQty) {
		this.stockShortQty = stockShortQty;
	}
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getTransInQty() {
		return transInQty;
	}
	public void setTransInQty(String transInQty) {
		this.transInQty = transInQty;
	}
	public String getSaleInQty() {
		return saleInQty;
	}
	public void setSaleInQty(String saleInQty) {
		this.saleInQty = saleInQty;
	}
	public String getSaleReturnQty() {
		return saleReturnQty;
	}
	public void setSaleReturnQty(String salesReturnQty) {
		this.saleReturnQty = salesReturnQty;
	}
	public String getSaleOutQty() {
		return saleOutQty;
	}
	public void setSaleOutQty(String saleOutQty) {
		this.saleOutQty = saleOutQty;
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
	public String getSalesDate() {
		return salesDate;
	}
	public void setSalesDate(String salesDate) {
		this.salesDate = salesDate;
	}
	public String getPensCustCodeFrom() {
		return pensCustCodeFrom;
	}
	public void setPensCustCodeFrom(String pensCustCodeFrom) {
		this.pensCustCodeFrom = pensCustCodeFrom;
	}
	public String getPensCustNameFrom() {
		return pensCustNameFrom;
	}
	public void setPensCustNameFrom(String pensCustNameFrom) {
		this.pensCustNameFrom = pensCustNameFrom;
	}
	public String getGroupDesc() {
		return groupDesc;
	}
	public void setGroupDesc(String groupDesc) {
		this.groupDesc = groupDesc;
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
	
	public String getDispZeroStock() {
		return dispZeroStock;
	}
	public void setDispZeroStock(String dispZeroStock) {
		this.dispZeroStock = dispZeroStock;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getItemCodeFrom() {
		return itemCodeFrom;
	}
	public void setItemCodeFrom(String itemCodeFrom) {
		this.itemCodeFrom = itemCodeFrom;
	}
	public String getItemCodeTo() {
		return itemCodeTo;
	}
	public void setItemCodeTo(String itemCodeTo) {
		this.itemCodeTo = itemCodeTo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getImportNo() {
		return importNo;
	}
	public void setImportNo(String importNo) {
		this.importNo = importNo;
	}
	public String getAsOfDateFrom() {
		return asOfDateFrom;
	}
	public void setAsOfDateFrom(String asOfDateFrom) {
		this.asOfDateFrom = asOfDateFrom;
	}
	public String getAsOfDateTo() {
		return asOfDateTo;
	}
	public void setAsOfDateTo(String asOfDateTo) {
		this.asOfDateTo = asOfDateTo;
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
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getOnhandQty() {
		return onhandQty;
	}
	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
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
	
	
}
