package com.isecinc.pens.bean;

import java.io.Serializable;

public class TransactionSummary implements Serializable{
   /**
	 * 
	 */
	private static final long serialVersionUID = -4521639138722838522L;
/**Criteria **/
	private String salesDateFrom;
	private String salesDateTo;
	private String pensCustCodeFrom;
	private String pensCustCodeTo;
	private String pensCustNameFrom;
	private String pensCustNameTo;
	
	private String vendor;
	private String name;//NAME,
	private String apType;//AP_TYPE,
	private String leaseVendorType;//LEASE_VENDOR_TYPE,
	private String storeNo;//STORE_NO,
	private String storeName;//STORE_NAME,
	private String styleNo;//STYLE_NO,
	private String description;//DESCRIPTION,
	private String col;//COL,
	private String sizeType;//SIZE_TYPE,
	private String sizes;//SIZES,
	private String salesDate;//SALES_DATE,
	private String qty;//QTY,
	private String grossSales;//GROSS_SALES,
	private String returnAmt;//RETURN_AMT,
	private String netSalesInclVat;//NET_SALES_INCL_VAT,
	private String vatAmt;//VAT_AMT,
	private String netSalesExcVat;//NET_SALES_EXC_VAT,
	private String gpPercent;//GP_PERCENT,
	private String gpAmount;//GP_AMOUNT,
	private String vatOnGpAmount;//VAT_ON_GP_AMOUNT,
	private String gpAmountInclVat;//GP_AMOUNT_INCL_VAT,
	private String apAmount;//AP_AMOUNT,
	private String totalVatAmt;//TOTAL_VAT_AMT,
	private String apAmountInclVat;//AP_AMOUNT_INCL_VAT,
	private String wholePriceBF;
	private String retailPriceBF;
    private String totalWholePriceBF;
    private String barcode;
    
    
	//Addtional TOPS
	private String supplier;//SUPPLIER,
	private String supplierName;//SUPPLIER_NAME,
	private String item;//ITEM,
	private String itemDesc;//ITEM_DESC,
	private String branchName;//BRANCH_NAME,
	private String groupNo;//GROUP_NO,
	private String groupName;//GROUP_NAME,
	private String dept;//DEPT,
	private String deptName;//DEPT_NAME,
	private String unitCost;//UNIT_COST,
	private String retailPrice;//RETAIL_PRICE,
	private String discount;//DISCOUNT,
	private String cusReturn;//CUS_RETURN,
	private String discountCusReturn;//DISCOUNT_CUS_RETURN,
	private String netCusReturn;//NET_CUS_RETURN,
	private String cogs;//COGS
	
	private String pensItem;
	private String pensCustCode;//PENS_CUST_CODE,
	private String pensCustDesc;//PENS_CUST_DESC,
	private String pensGroup;//PENS_GROUP,
	private String pensGroupType;//PENS_GROUP_TYPE,
	private String salesYear;//SALES_YEAR,
	private String salesMonth;//SALES_MONTH,
	private String fileName;
	private String createDate;
	private String createUser;
	private String updateDate;
	private String updateUser;
	
	
	
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(String unitCost) {
		this.unitCost = unitCost;
	}
	public String getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(String retailPrice) {
		this.retailPrice = retailPrice;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getCusReturn() {
		return cusReturn;
	}
	public void setCusReturn(String cusReturn) {
		this.cusReturn = cusReturn;
	}
	public String getDiscountCusReturn() {
		return discountCusReturn;
	}
	public void setDiscountCusReturn(String discountCusReturn) {
		this.discountCusReturn = discountCusReturn;
	}
	public String getNetCusReturn() {
		return netCusReturn;
	}
	public void setNetCusReturn(String netCusReturn) {
		this.netCusReturn = netCusReturn;
	}
	public String getCogs() {
		return cogs;
	}
	public void setCogs(String cogs) {
		this.cogs = cogs;
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
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getTotalWholePriceBF() {
		return totalWholePriceBF;
	}
	public void setTotalWholePriceBF(String totalWholePriceBF) {
		this.totalWholePriceBF = totalWholePriceBF;
	}
	public String getPensItem() {
		return pensItem;
	}
	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}
	public String getPensCustNameFrom() {
		return pensCustNameFrom;
	}
	public void setPensCustNameFrom(String pensCustNameFrom) {
		this.pensCustNameFrom = pensCustNameFrom;
	}
	public String getPensCustNameTo() {
		return pensCustNameTo;
	}
	public void setPensCustNameTo(String pensCustNameTo) {
		this.pensCustNameTo = pensCustNameTo;
	}
	public String getSalesDateFrom() {
		return salesDateFrom;
	}
	public void setSalesDateFrom(String salesDateFrom) {
		this.salesDateFrom = salesDateFrom;
	}
	public String getSalesDateTo() {
		return salesDateTo;
	}
	public void setSalesDateTo(String salesDateTo) {
		this.salesDateTo = salesDateTo;
	}
	
	public String getPensCustCodeFrom() {
		return pensCustCodeFrom;
	}
	public void setPensCustCodeFrom(String pensCustCodeFrom) {
		this.pensCustCodeFrom = pensCustCodeFrom;
	}
	public String getPensCustCodeTo() {
		return pensCustCodeTo;
	}
	public void setPensCustCodeTo(String pensCustCodeTo) {
		this.pensCustCodeTo = pensCustCodeTo;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getApType() {
		return apType;
	}
	public void setApType(String apType) {
		this.apType = apType;
	}
	public String getLeaseVendorType() {
		return leaseVendorType;
	}
	public void setLeaseVendorType(String leaseVendorType) {
		this.leaseVendorType = leaseVendorType;
	}
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStyleNo() {
		return styleNo;
	}
	public void setStyleNo(String styleNo) {
		this.styleNo = styleNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getSizeType() {
		return sizeType;
	}
	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}
	public String getSizes() {
		return sizes;
	}
	public void setSizes(String sizes) {
		this.sizes = sizes;
	}
	public String getSalesDate() {
		return salesDate;
	}
	public void setSalesDate(String salesDate) {
		this.salesDate = salesDate;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getGrossSales() {
		return grossSales;
	}
	public void setGrossSales(String grossSales) {
		this.grossSales = grossSales;
	}
	public String getReturnAmt() {
		return returnAmt;
	}
	public void setReturnAmt(String returnAmt) {
		this.returnAmt = returnAmt;
	}
	public String getNetSalesInclVat() {
		return netSalesInclVat;
	}
	public void setNetSalesInclVat(String netSalesInclVat) {
		this.netSalesInclVat = netSalesInclVat;
	}
	public String getVatAmt() {
		return vatAmt;
	}
	public void setVatAmt(String vatAmt) {
		this.vatAmt = vatAmt;
	}
	public String getNetSalesExcVat() {
		return netSalesExcVat;
	}
	public void setNetSalesExcVat(String netSalesExcVat) {
		this.netSalesExcVat = netSalesExcVat;
	}
	public String getGpPercent() {
		return gpPercent;
	}
	public void setGpPercent(String gpPercent) {
		this.gpPercent = gpPercent;
	}
	public String getGpAmount() {
		return gpAmount;
	}
	public void setGpAmount(String gpAmount) {
		this.gpAmount = gpAmount;
	}
	public String getVatOnGpAmount() {
		return vatOnGpAmount;
	}
	public void setVatOnGpAmount(String vatOnGpAmount) {
		this.vatOnGpAmount = vatOnGpAmount;
	}
	public String getGpAmountInclVat() {
		return gpAmountInclVat;
	}
	public void setGpAmountInclVat(String gpAmountInclVat) {
		this.gpAmountInclVat = gpAmountInclVat;
	}
	public String getApAmount() {
		return apAmount;
	}
	public void setApAmount(String apAmount) {
		this.apAmount = apAmount;
	}
	public String getTotalVatAmt() {
		return totalVatAmt;
	}
	public void setTotalVatAmt(String totalVatAmount) {
		this.totalVatAmt = totalVatAmount;
	}
	public String getApAmountInclVat() {
		return apAmountInclVat;
	}
	public void setApAmountInclVat(String apAmountInclVat) {
		this.apAmountInclVat = apAmountInclVat;
	}
	public String getPensCustCode() {
		return pensCustCode;
	}
	public void setPensCustCode(String pensCustCode) {
		this.pensCustCode = pensCustCode;
	}
	public String getPensCustDesc() {
		return pensCustDesc;
	}
	public void setPensCustDesc(String pensCustDesc) {
		this.pensCustDesc = pensCustDesc;
	}
	public String getPensGroup() {
		return pensGroup;
	}
	public void setPensGroup(String pensGroup) {
		this.pensGroup = pensGroup;
	}
	public String getPensGroupType() {
		return pensGroupType;
	}
	public void setPensGroupType(String pensGroupType) {
		this.pensGroupType = pensGroupType;
	}
	public String getSalesYear() {
		return salesYear;
	}
	public void setSalesYear(String salesYear) {
		this.salesYear = salesYear;
	}
	public String getSalesMonth() {
		return salesMonth;
	}
	public void setSalesMonth(String salesMonth) {
		this.salesMonth = salesMonth;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	

}
