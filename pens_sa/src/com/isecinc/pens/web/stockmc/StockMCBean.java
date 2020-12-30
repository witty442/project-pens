package com.isecinc.pens.web.stockmc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.isecinc.pens.bean.User;

public class StockMCBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 122668609503507874L;
	private int id;
	private String stockDate;
	private String stockDateFrom;
	private String stockDateTo;
	private String productCodeFrom;
	private String productCodeTo;
	private String customerCode;
	private String customerName;
	private String storeCode;
	private String storeName;
	private String mcName;
	
	private String createUser;
	private String updateUser;
	private String brand;
	private String brandName;
	private String brandFrom;
	private String brandTo;
	private List<StockMCBean> items;
	private long lineId;
	private long no;
	private String status;
	private String productCode;
	private String barcode;
	private String productName;
	private String productPackSize;
	private String productAge;
	private String retailPriceBF;
	
	private String promotionPrice;
	private String legQty;
	private String inStoreQty;
	private String backendQty;
	private String uom;
	
	private String frontendQty1;
	private String uom1;
	private String expireDate1;
	
	private String frontendQty2;
	private String uom2;
	private String expireDate2;
	
	private String frontendQty3;
	private String uom3;
	private String expireDate3;
	
	//control
	private boolean canEdit;
	//MC_ITEM
	private String itemCust;
	private String startDate;
	//for delete lineIdArr
	private String lineIdDeletes;
    private String endDate;
    private String dispHaveCheckStock;
	
    
	public String getBrandFrom() {
		return brandFrom;
	}
	public void setBrandFrom(String brandFrom) {
		this.brandFrom = brandFrom;
	}
	public String getBrandTo() {
		return brandTo;
	}
	public void setBrandTo(String brandTo) {
		this.brandTo = brandTo;
	}
	public String getProductCodeFrom() {
		return productCodeFrom;
	}
	public void setProductCodeFrom(String productCodeFrom) {
		this.productCodeFrom = productCodeFrom;
	}
	public String getProductCodeTo() {
		return productCodeTo;
	}
	public void setProductCodeTo(String productCodeTo) {
		this.productCodeTo = productCodeTo;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getStockDateFrom() {
		return stockDateFrom;
	}
	public void setStockDateFrom(String stockDateFrom) {
		this.stockDateFrom = stockDateFrom;
	}
	public String getStockDateTo() {
		return stockDateTo;
	}
	public void setStockDateTo(String stockDateTo) {
		this.stockDateTo = stockDateTo;
	}
	public String getDispHaveCheckStock() {
		return dispHaveCheckStock;
	}
	public void setDispHaveCheckStock(String dispHaveCheckStock) {
		this.dispHaveCheckStock = dispHaveCheckStock;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getLineIdDeletes() {
		return lineIdDeletes;
	}
	public void setLineIdDeletes(String lineIdDeletes) {
		this.lineIdDeletes = lineIdDeletes;
	}
	public String getProductPackSize() {
		return productPackSize;
	}
	public void setProductPackSize(String productPackSize) {
		this.productPackSize = productPackSize;
	}
	
	public String getProductAge() {
		return productAge;
	}
	public void setProductAge(String productAge) {
		this.productAge = productAge;
	}
	public String getItemCust() {
		return itemCust;
	}
	public void setItemCust(String itemCust) {
		this.itemCust = itemCust;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public long getNo() {
		return no;
	}
	public void setNo(long no) {
		this.no = no;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getStockDate() {
		return stockDate;
	}
	public void setStockDate(String stockDate) {
		this.stockDate = stockDate;
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
	public String getMcName() {
		return mcName;
	}
	public void setMcName(String mcName) {
		this.mcName = mcName;
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
	public List<StockMCBean> getItems() {
		return items;
	}
	public void setItems(List<StockMCBean> items) {
		this.items = items;
	}
	public long getLineId() {
		return lineId;
	}
	public void setLineId(long lineId) {
		this.lineId = lineId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getRetailPriceBF() {
		return retailPriceBF;
	}
	public void setRetailPriceBF(String retailPriceBF) {
		this.retailPriceBF = retailPriceBF;
	}
	public String getPromotionPrice() {
		return promotionPrice;
	}
	public void setPromotionPrice(String promotionPrice) {
		this.promotionPrice = promotionPrice;
	}
	public String getLegQty() {
		return legQty;
	}
	public void setLegQty(String legQty) {
		this.legQty = legQty;
	}
	public String getBackendQty() {
		return backendQty;
	}
	public void setBackendQty(String backendQty) {
		this.backendQty = backendQty;
	}
	public String getInStoreQty() {
		return inStoreQty;
	}
	public void setInStoreQty(String inStoreQty) {
		this.inStoreQty = inStoreQty;
	}
	
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getFrontendQty1() {
		return frontendQty1;
	}
	public void setFrontendQty1(String frontendQty1) {
		this.frontendQty1 = frontendQty1;
	}
	public String getUom1() {
		return uom1;
	}
	public void setUom1(String uom1) {
		this.uom1 = uom1;
	}
	public String getExpireDate1() {
		return expireDate1;
	}
	public void setExpireDate1(String expireDate1) {
		this.expireDate1 = expireDate1;
	}
	public String getFrontendQty2() {
		return frontendQty2;
	}
	public void setFrontendQty2(String frontendQty2) {
		this.frontendQty2 = frontendQty2;
	}
	public String getUom2() {
		return uom2;
	}
	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}
	public String getExpireDate2() {
		return expireDate2;
	}
	public void setExpireDate2(String expireDate2) {
		this.expireDate2 = expireDate2;
	}
	public String getFrontendQty3() {
		return frontendQty3;
	}
	public void setFrontendQty3(String frontendQty3) {
		this.frontendQty3 = frontendQty3;
	}
	public String getUom3() {
		return uom3;
	}
	public void setUom3(String uom3) {
		this.uom3 = uom3;
	}
	public String getExpireDate3() {
		return expireDate3;
	}
	public void setExpireDate3(String expireDate3) {
		this.expireDate3 = expireDate3;
	}
	
	
}
