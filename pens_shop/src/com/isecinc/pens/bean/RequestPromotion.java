package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestPromotion implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5404843212950669118L;
	/** Criteria **/
	private String requestDateFrom;
	private String requestDateTo;
    
	private String no;    
	private String requestNo ;       
	private String requestDate ;
	private String printDate ;
	private String productCatagory ;
	private String productCatagoryDesc ;
	private String productType;
	private String promotionStartDate ;
	private String promotionEndDate ;
    private String status;
    private String statusDesc;
    private String name;
    private String remark;
    private String customerCode;
    private String customerName;
    private String phone;
    
    private String created; 
	private String createdBy; 
	private String updated; 
	private String updateBy;
	private String userId;
	private String territory;
	private String exported;
	private User user = new User();;
	
	private boolean canEdit = false;
	private boolean canGenFile = false;
	private boolean canCancel = false;
	
	private List<RequestPromotionLine> promotionLineList = new ArrayList<RequestPromotionLine>();
	private List<RequestPromotionCost> costLineList = new ArrayList<RequestPromotionCost>();
	
    private String priceListId;

    
	
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public String getProductCatagoryDesc() {
		return productCatagoryDesc;
	}
	public void setProductCatagoryDesc(String productCatagoryDesc) {
		this.productCatagoryDesc = productCatagoryDesc;
	}
	public String getTerritory() {
		return territory;
	}
	public void setTerritory(String territory) {
		this.territory = territory;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public boolean isCanGenFile() {
		return canGenFile;
	}
	public void setCanGenFile(boolean canGenFile) {
		this.canGenFile = canGenFile;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	public String getPriceListId() {
		return priceListId;
	}
	public void setPriceListId(String priceListId) {
		this.priceListId = priceListId;
	}
	public List<RequestPromotionCost> getCostLineList() {
		return costLineList;
	}
	public void setCostLineList(List<RequestPromotionCost> costLineList) {
		this.costLineList = costLineList;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getProductCatagory() {
		return productCatagory;
	}
	public void setProductCatagory(String productCatagory) {
		this.productCatagory = productCatagory;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
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
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public String getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
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
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getPromotionStartDate() {
		return promotionStartDate;
	}
	public void setPromotionStartDate(String promotionStartDate) {
		this.promotionStartDate = promotionStartDate;
	}
	public String getPromotionEndDate() {
		return promotionEndDate;
	}
	public void setPromotionEndDate(String promotionEndDate) {
		this.promotionEndDate = promotionEndDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public List<RequestPromotionLine> getPromotionLineList() {
		return promotionLineList;
	}
	public void setPromotionLineList(List<RequestPromotionLine> promotionLineList) {
		this.promotionLineList = promotionLineList;
	}
	
}
