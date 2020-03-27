package com.isecinc.pens.web.importall;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
public class ImportAllBean implements  Comparable<ImportAllBean>, Serializable{

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
	private String endDate;
	
	private String salesDate;
	private String pensCustCodeFrom;
	private String pensCustNameFrom;
	private String location;
	
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
    private String onhandAmt;
    
    private String custGroup;
    private String storeCode;
    private String storeName;
    private String custNo;//oracle
    
    private String saleInQty;
    private String saleReturnQty;
    private String saleOutQty;
    private String transInQty;
    private String adjustQty;
    private String stockShortQty;
    private String initSaleQty;
    private String adjustSaleQty;
    private String beginingQty;
	private String dispHaveQty;
    private String initDate;
    
    private String scanQty;
    private String diffAmt;
    private String storeNo;
    
    private List<ImportAllBean> itemsList;
    private List<ImportAllBean> errorList;
    private ImportAllBean summary;
    private String orderLotNo;
    private String wholePriceVat;
    private String totalSaleAmount;
    private String gp;
    private List<ImportAllBean> subList;
    private double totalQtyByCust = 0;
	private double totalAmountByCust = 0;
	private String stockIssueQty;
	private String stockReceiptQty;
   
    private String summaryType;
    private String endSaleDate;
    private String importDate;
    private boolean imported;
    
    
	public boolean isImported() {
		return imported;
	}
	public void setImported(boolean imported) {
		this.imported = imported;
	}
	public String getImportDate() {
		return importDate;
	}
	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public String getSummaryType() {
		return summaryType;
	}
	public void setSummaryType(String summaryType) {
		this.summaryType = summaryType;
	}
	public String getEndSaleDate() {
		return endSaleDate;
	}
	public void setEndSaleDate(String endSaleDate) {
		this.endSaleDate = endSaleDate;
	}
	public String getStockIssueQty() {
		return stockIssueQty;
	}
	public void setStockIssueQty(String stockIssueQty) {
		this.stockIssueQty = stockIssueQty;
	}
	public String getStockReceiptQty() {
		return stockReceiptQty;
	}
	public void setStockReceiptQty(String stockReceiptQty) {
		this.stockReceiptQty = stockReceiptQty;
	}
	public List<ImportAllBean> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<ImportAllBean> errorList) {
		this.errorList = errorList;
	}
	public double getTotalQtyByCust() {
		return totalQtyByCust;
	}
	public void setTotalQtyByCust(double totalQtyByCust) {
		this.totalQtyByCust = totalQtyByCust;
	}
	public double getTotalAmountByCust() {
		return totalAmountByCust;
	}
	public void setTotalAmountByCust(double totalAmountByCust) {
		this.totalAmountByCust = totalAmountByCust;
	}
	public List<ImportAllBean> getSubList() {
		return subList;
	}
	public void setSubList(List<ImportAllBean> subList) {
		this.subList = subList;
	}
	public String getGp() {
		return gp;
	}
	public void setGp(String gp) {
		this.gp = gp;
	}
	public String getOrderLotNo() {
		return orderLotNo;
	}
	public void setOrderLotNo(String orderLotNo) {
		this.orderLotNo = orderLotNo;
	}
	public String getWholePriceVat() {
		return wholePriceVat;
	}
	public void setWholePriceVat(String wholePriceVat) {
		this.wholePriceVat = wholePriceVat;
	}
	
	public String getTotalSaleAmount() {
		return totalSaleAmount;
	}
	public void setTotalSaleAmount(String totalSaleAmount) {
		this.totalSaleAmount = totalSaleAmount;
	}
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public String getOnhandAmt() {
		return onhandAmt;
	}
	public void setOnhandAmt(String onhandAmt) {
		this.onhandAmt = onhandAmt;
	}
	public String getDiffAmt() {
		return diffAmt;
	}
	public void setDiffAmt(String diffAmt) {
		this.diffAmt = diffAmt;
	}
	public String getScanQty() {
		return scanQty;
	}
	public void setScanQty(String scanQty) {
		this.scanQty = scanQty;
	}
	public ImportAllBean getSummary() {
		return summary;
	}
	public void setSummary(ImportAllBean summary) {
		this.summary = summary;
	}
	public List<ImportAllBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<ImportAllBean> itemsList) {
		this.itemsList = itemsList;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getBeginingQty() {
		return beginingQty;
	}
	public void setBeginingQty(String beginingQty) {
		this.beginingQty = beginingQty;
	}
	public String getDispHaveQty() {
		return dispHaveQty;
	}
	public void setDispHaveQty(String dispHaveQty) {
		this.dispHaveQty = dispHaveQty;
	}
	public String getInitDate() {
		return initDate;
	}
	public void setInitDate(String initDate) {
		this.initDate = initDate;
	}
	public String getAdjustSaleQty() {
		return adjustSaleQty;
	}
	public void setAdjustSaleQty(String adjustSaleQty) {
		this.adjustSaleQty = adjustSaleQty;
	}
	public String getInitSaleQty() {
		return initSaleQty;
	}
	public void setInitSaleQty(String initSaleQty) {
		this.initSaleQty = initSaleQty;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	
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
	
	 public int compareTo(ImportAllBean o) {
	     return Comparators.STORE_CODE_GROUP_ASC.compare(this, o);
	  }
	 
	 public static class Comparators {
		  public static Comparator<ImportAllBean> STORE_CODE_GROUP_ASC = new Comparator<ImportAllBean>() {
	            @Override
	            public int compare(ImportAllBean o1, ImportAllBean o2) {
	            	int order1= o1.getStoreCode().compareTo(o2.getStoreCode());
	            	
	            	if (order1 == 0) {
	                    // Strings are equal, sort by Group
	                    return o1.getGroup().compareTo(o2.getGroup());
	                }else {
	                    return order1;
	                }
	            }
	        };
	      
	 }
	
}
