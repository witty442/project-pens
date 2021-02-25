package com.isecinc.pens.bean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class RequisitionProduct {
    
	/** Criteria **/
	private String requestDateFrom;
	private String requestDateTo;
    
    
	/** properties**/
	private String priceListId;
	private double totalAmount;
	
	private String no;    
	private String requestNumber ;       
	private String requestDate ;
	private String currentDate ;
	private String organizationId ;
	private String salesCode;
	private String salesDesc;
	private String reasonCode ;
	private String remark ;
	
	private boolean requestDateDisabled = false;
	private boolean pdCodeReadonly = false;
	
	private String cancelReason ;
	private String status ;
	private String statusLabel ;
	private String exported; 
	private String exportedLabel;
	private String userId;
	private String createdBy; 
	private String updateBy; 
	
	private String updated; 
	private String created; 
	
	private BigDecimal updatedLong; 
	private BigDecimal createdLong;
	
	private boolean canEdit = false;
	private boolean showSaveBtn = false;
	private boolean showPrintBtn =  false;
	private boolean showCancelBtn = false;
	
	private List<RequisitionProductLine> requisitionProductLineList;
    private List<String> lineNoDeleteList;
	
	
	public String getCurrentDate() {
		try{
			currentDate = DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		}catch(Exception e){
			
		}
		return currentDate;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
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

	public boolean isPdCodeReadonly() {
		return pdCodeReadonly;
	}

	public void setPdCodeReadonly(boolean pdCodeReadonly) {
		this.pdCodeReadonly = pdCodeReadonly;
	}

	public boolean isShowSaveBtn() {
		return showSaveBtn;
	}

	public void setShowSaveBtn(boolean showSaveBtn) {
		this.showSaveBtn = showSaveBtn;
	}

	public boolean isShowPrintBtn() {
		return showPrintBtn;
	}

	public void setShowPrintBtn(boolean showPrintBtn) {
		this.showPrintBtn = showPrintBtn;
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

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

    
	public String getSalesCode() {
		return salesCode;
	}

	public void setSalesCode(String salesCode) {
		this.salesCode = salesCode;
	}

	public String getSalesDesc() {
		return salesDesc;
	}

	public void setSalesDesc(String salesDesc) {
		this.salesDesc = salesDesc;
	}

	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
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

	public BigDecimal getUpdatedLong() {
		return updatedLong;
	}

	public void setUpdatedLong(BigDecimal updatedLong) {
		this.updatedLong = updatedLong;
	}

	public BigDecimal getCreatedLong() {
		return createdLong;
	}

	public void setCreatedLong(BigDecimal createdLong) {
		this.createdLong = createdLong;
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


	public List<RequisitionProductLine> getRequisitionProductLineList() {
		return requisitionProductLineList;
	}


	public void setRequisitionProductLineList(
			List<RequisitionProductLine> requisitionProductLineList) {
		this.requisitionProductLineList = requisitionProductLineList;
	}

}
