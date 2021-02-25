package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class BillPlan implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Criteria **/
	private String billPlanDateFrom;
	private String billPlanDateTo;
    private String noBillPlan;
    
	/** properties**/
	private String no; 
	private int lineNo;
	private String billPlanNo ;  
	private String productId ;  
	private String billPlanDate ;
	private String billPlanRequestDate ;   
	private String salesCode;
	private String salesDesc;
	private String pdCode ;
	private String pdDesc ;
	
	private String productCode ;
	private String productName ;
	
	private String fullQTY;
	private double qty1;
	private double qty2;
	
	private String fullUOM;
	private String uom1;
	private String uom2;
	
	private String description ;
	private String status ;
	private String statusLabel ;
	private String exported; 
	private String exportedLabel;
	
	private boolean canEdit;
	private boolean canCancel;
	
	private String userId;
	private String createdBy; 
	private String updateBy; 
	private String updated; 
	private String created; 
	private String currentDate;
	
	private List<BillPlan> billPlanLineList;

    
	public String getNoBillPlan() {
		return noBillPlan;
	}


	public void setNoBillPlan(String noBillPlan) {
		this.noBillPlan = noBillPlan;
	}


	public int getLineNo() {
		return lineNo;
	}


	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}


	public String getCurrentDate() {
		try{
			currentDate = DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		}catch(Exception e){
			
		}
		return currentDate;
	}


	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}


	public String getBillPlanRequestDate() {
		return billPlanRequestDate;
	}


	public void setBillPlanRequestDate(String billPlanRequestDate) {
		this.billPlanRequestDate = billPlanRequestDate;
	}


	public boolean isCanCancel() {
		return canCancel;
	}


	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}


	public String getFullQTY() {
		return fullQTY;
	}


	public void setFullQTY(String fullQTY) {
		this.fullQTY = fullQTY;
	}


	public String getFullUOM() {
		return fullUOM;
	}


	public void setFullUOM(String fullUOM) {
		this.fullUOM = fullUOM;
	}


	public boolean isCanEdit() {
		return canEdit;
	}


	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}


	public double getQty1() {
		return qty1;
	}


	public void setQty1(double qty1) {
		this.qty1 = qty1;
	}


	public double getQty2() {
		return qty2;
	}


	public void setQty2(double qty2) {
		this.qty2 = qty2;
	}


	public String getUom1() {
		return uom1;
	}


	public void setUom1(String uom1) {
		this.uom1 = uom1;
	}


	public String getUom2() {
		return uom2;
	}


	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}


	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}


	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getBillPlanDate() {
		return billPlanDate;
	}


	public void setBillPlanDate(String billPlanDate) {
		this.billPlanDate = billPlanDate;
	}


	public String getBillPlanDateFrom() {
		return billPlanDateFrom;
	}


	public void setBillPlanDateFrom(String billPlanDateFrom) {
		this.billPlanDateFrom = billPlanDateFrom;
	}


	public String getBillPlanDateTo() {
		return billPlanDateTo;
	}


	public void setBillPlanDateTo(String billPlanDateTo) {
		this.billPlanDateTo = billPlanDateTo;
	}


	public String getNo() {
		return no;
	}


	public void setNo(String no) {
		this.no = no;
	}


	public String getBillPlanNo() {
		return billPlanNo;
	}


	public void setBillPlanNo(String billPlanNo) {
		this.billPlanNo = billPlanNo;
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


	public String getPdCode() {
		return pdCode;
	}


	public void setPdCode(String pdCode) {
		this.pdCode = pdCode;
	}


	public String getPdDesc() {
		return pdDesc;
	}


	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
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


	public String getStatusLabel() {
		return statusLabel;
	}


	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}


	public String getExported() {
		return exported;
	}


	public void setExported(String exported) {
		this.exported = exported;
	}


	public String getExportedLabel() {
		return exportedLabel;
	}


	public void setExportedLabel(String exportedLabel) {
		this.exportedLabel = exportedLabel;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
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


	public List<BillPlan> getBillPlanLineList() {
		return billPlanLineList;
	}


	public void setBillPlanLineList(List<BillPlan> billPlanLineList) {
		this.billPlanLineList = billPlanLineList;
	}

    
	
	
}
