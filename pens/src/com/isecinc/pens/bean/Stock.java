package com.isecinc.pens.bean;

import java.util.Date;
import java.util.List;

import com.isecinc.pens.inf.helper.Utils;

public class Stock {
    
	/** Criteria **/
	private String requestDateFrom;
	private String requestDateTo;
	private String  inventoryItemId; 
	private String haveStock;
    
	/** properties**/
	private String priceListId;
	private double totalAmount;
	
	private String no;    
	private String requestNumber ;       
	private String requestDate ;
	private String currentDate ;
	
	private boolean requestDateDisabled = false;
	
	private String description ;
	private String status ;
	private String statusLabel ;
	private String exported; 
	private String exportedLabel;
	private String userId;
	private String createdBy; 
	private String updateBy; 
	
	private String printDate; 
	private String updated; 
	private String created; 
	private String backAvgMonth;
	
	/** Customer */
	private int customerId;
	private String customerName;
	private int billAddressId;
	private int shipAddressId;

	private boolean canEdit = false;
	private boolean showSaveBtn = false;
	private boolean showCancelBtn = false;
	
	private List<StockLine> lineList;
    private List<String> lineNoDeleteList;

	
	public String getCurrentDate() {
		try{
			currentDate = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		}catch(Exception e){
			
		}
		return currentDate;
	}


	public String getBackAvgMonth() {
		return backAvgMonth;
	}


	public void setBackAvgMonth(String backAvgMonth) {
		this.backAvgMonth = backAvgMonth;
	}


	public String getHaveStock() {
		return haveStock;
	}


	public void setHaveStock(String haveStock) {
		this.haveStock = haveStock;
	}


	public int getCustomerId() {
		return customerId;
	}


	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public int getBillAddressId() {
		return billAddressId;
	}


	public void setBillAddressId(int billAddressId) {
		this.billAddressId = billAddressId;
	}


	public int getShipAddressId() {
		return shipAddressId;
	}


	public void setShipAddressId(int shipAddressId) {
		this.shipAddressId = shipAddressId;
	}


	public String getInventoryItemId() {
		return inventoryItemId;
	}


	public void setInventoryItemId(String inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}


	public boolean isRequestDateDisabled() {
		return requestDateDisabled;
	}


	public void setRequestDateDisabled(boolean requestDateDisabled) {
		this.requestDateDisabled = requestDateDisabled;
	}


	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}


	public List<String> getLineNoDeleteList() {
		return lineNoDeleteList;
	}

	public void setLineNoDeleteList(List<String> lineNoDeleteList) {
		this.lineNoDeleteList = lineNoDeleteList;
	}


	public boolean isShowSaveBtn() {
		return showSaveBtn;
	}

	public void setShowSaveBtn(boolean showSaveBtn) {
		this.showSaveBtn = showSaveBtn;
	}

	public boolean isShowCancelBtn() {
		return showCancelBtn;
	}

	public void setShowCancelBtn(boolean showCancelBtn) {
		this.showCancelBtn = showCancelBtn;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public String getExportedLabel() {
		return exportedLabel;
	}

	public void setExportedLabel(String exportedLabel) {
		this.exportedLabel = exportedLabel;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	public String getPriceListId() {
		return priceListId;
	}

	public void setPriceListId(String priceListId) {
		this.priceListId = priceListId;
	}


	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getRequestDateFrom() {
		return requestDateFrom;
	}

	public void setRequestDateFrom(String requestDateFrom) {
		this.requestDateFrom = requestDateFrom;
	}

	public String getRequestDateTo() {
		return requestDateTo;
	}

	public void setRequestDateTo(String requestDateTo) {
		this.requestDateTo = requestDateTo;
	}

	public String getRequestNumber() {
		return requestNumber;
	}

	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
    
	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

    
	public String getPrintDate() {
		return printDate;
	}

	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}


	public List<StockLine> getLineList() {
		return lineList;
	}


	public void setLineList(List<StockLine> lineList) {
		this.lineList = lineList;
	}

	
}
