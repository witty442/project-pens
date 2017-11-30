package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class Job implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5099205821083784403L;
	private int no;
	private String jobId;
	private String openDate;
	private String custGroup;
	private String custGroupDesc;
	private String closeDate;
	private String status;
	private String statusDesc;
	private String statusMessage;
	private String name;
	private String createUser;
	private String updateUser;
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;
	private String wareHouse;
	private String wareHouseDesc;
	private String refDoc;
	private String rtnQty;
	private String rtnAmt;
	private String rtnNo;
	private String dispAutoCN;
	private String cnNo;
	private String autoCN;
	private List<Job> items;
    
	//optional
	private boolean canEdit = false;
	private boolean canCancel = false;
	
	
	public String getAutoCN() {
		return autoCN;
	}
	public void setAutoCN(String autoCN) {
		this.autoCN = autoCN;
	}
	
	public String getDispAutoCN() {
		return dispAutoCN;
	}
	public void setDispAutoCN(String dispAutoCN) {
		this.dispAutoCN = dispAutoCN;
	}
	public String getCnNo() {
		return cnNo;
	}
	public void setCnNo(String cnNo) {
		this.cnNo = cnNo;
	}
	public String getRtnNo() {
		return rtnNo;
	}
	public void setRtnNo(String rtnNo) {
		this.rtnNo = rtnNo;
	}
	public String getRtnQty() {
		return rtnQty;
	}
	public void setRtnQty(String rtnQty) {
		this.rtnQty = rtnQty;
	}
	public String getRtnAmt() {
		return rtnAmt;
	}
	public void setRtnAmt(String rtnAmt) {
		this.rtnAmt = rtnAmt;
	}
	public String getRefDoc() {
		return refDoc;
	}
	public void setRefDoc(String refDoc) {
		this.refDoc = refDoc;
	}
	public String getWareHouseDesc() {
		return wareHouseDesc;
	}
	public void setWareHouseDesc(String wareHouseDesc) {
		this.wareHouseDesc = wareHouseDesc;
	}
	public String getWareHouse() {
		return wareHouse;
	}
	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
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
	public String getSubInv() {
		return subInv;
	}
	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}
	public String getStoreNo() {
		return storeNo;
	}
	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getCustGroupDesc() {
		return custGroupDesc;
	}
	public void setCustGroupDesc(String custGroupDesc) {
		this.custGroupDesc = custGroupDesc;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
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
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
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
	public List<Job> getItems() {
		return items;
	}
	public void setItems(List<Job> items) {
		this.items = items;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
