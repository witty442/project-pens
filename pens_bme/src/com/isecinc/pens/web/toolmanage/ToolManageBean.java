package com.isecinc.pens.web.toolmanage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.isecinc.pens.bean.LockItemOrderBean;

public class ToolManageBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7445206605758027040L;
	private String pageName;
	private String docNo;
	private String docDate;
	private String docType;
	private String refRtn;
	private String custGroup;
	private String custGroupName;
	private String storeCode;
	private String storeName;
	private String status;
	private String remark;
	private List<ToolManageBean> items;
	private String createUser;
	private String updateUser;
	private Date countStkDate;
	
	private long id;
	private String item;
	private String itemName;
	private String itemType;
	private String qty;
	//control
	private String mode;
	private String itemFrom;
	private String itemTo;
	private String reportType;
	private boolean canSave;
	private boolean canCancel;
	
	
	public String getCustGroupName() {
		return custGroupName;
	}
	public void setCustGroupName(String custGroupName) {
		this.custGroupName = custGroupName;
	}
	public Date getCountStkDate() {
		return countStkDate;
	}
	public void setCountStkDate(Date countStkDate) {
		this.countStkDate = countStkDate;
	}
	public String getItemFrom() {
		return itemFrom;
	}
	public void setItemFrom(String itemFrom) {
		this.itemFrom = itemFrom;
	}
	public String getItemTo() {
		return itemTo;
	}
	public void setItemTo(String itemTo) {
		this.itemTo = itemTo;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	public String getDocDate() {
		return docDate;
	}
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getRefRtn() {
		return refRtn;
	}
	public void setRefRtn(String refRtn) {
		this.refRtn = refRtn;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<ToolManageBean> getItems() {
		return items;
	}
	public void setItems(List<ToolManageBean> items) {
		this.items = items;
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
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public boolean isCanSave() {
		return canSave;
	}
	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	
	
	
}
