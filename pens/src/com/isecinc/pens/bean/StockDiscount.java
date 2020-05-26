package com.isecinc.pens.bean;

import java.util.Date;
import java.util.List;

import com.isecinc.pens.inf.helper.Utils;

public class StockDiscount {
    
	/** Criteria **/
	private String requestDateFrom;
	private String requestDateTo;
	private String inventoryItemId; 
	private String haveStock;
    private String backAvgMonth;
    private String backDate;
    private String vatRate;
	/** properties**/
    private String totalLineAmount;
	private String totalVatAmount;
	private String totalNetAmount;
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

	/** Customer */
	private int customerId;
	private String customerCode; 
	private String customerName;
	private String customerAddress; 
	
	private boolean canEdit = false;
	private boolean showSaveBtn = false;
	private boolean showPrintBtn = false;
	private boolean showCancelBtn = false;
	
	private List<StockDiscountLine> lineList;
    private List<String> lineNoDeleteList;

    
	public String getVatRate() {
		return vatRate;
	}

	public void setVatRate(String vatRate) {
		this.vatRate = vatRate;
	}

	public boolean isShowPrintBtn() {
		return showPrintBtn;
	}

	public void setShowPrintBtn(boolean showPrintBtn) {
		this.showPrintBtn = showPrintBtn;
	}

	public String getBackDate() {
		return backDate;
	}

	public void setBackDate(String backDate) {
		this.backDate = backDate;
	}

	

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getBackAvgMonth() {
		return backAvgMonth;
	}

	public void setBackAvgMonth(String backAvgMonth) {
		this.backAvgMonth = backAvgMonth;
	}

	public String getCurrentDate() {
		try{
			currentDate = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		}catch(Exception e){
			
		}
		return currentDate;
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
    


	public String getTotalVatAmount() {
		return totalVatAmount;
	}

	public void setTotalVatAmount(String totalVatAmount) {
		this.totalVatAmount = totalVatAmount;
	}

	

	public String getTotalLineAmount() {
		return totalLineAmount;
	}

	public void setTotalLineAmount(String totalLineAmount) {
		this.totalLineAmount = totalLineAmount;
	}

	public String getTotalNetAmount() {
		return totalNetAmount;
	}

	public void setTotalNetAmount(String totalNetAmount) {
		this.totalNetAmount = totalNetAmount;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
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


	public List<StockDiscountLine> getLineList() {
		return lineList;
	}


	public void setLineList(List<StockDiscountLine> lineList) {
		this.lineList = lineList;
	}

	
}
