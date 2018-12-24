package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;


public class PickReportBean implements Serializable{

	private static final long serialVersionUID = 8886978969117315419L;
	private int no;
	private String issueReqDateFrom;
	private String issueReqDateTo;
	private String custGroup;
	private String custGroupName;
	private String storeCode;
	private String storeName;
	private String subInv;
	private String storeNo;
	private String issueReqNo;
	private String issueReqDate;
	private String status;
	private String reqQty;
	private String userRequest;
	private String remark;
	private String pensItem;
	private String materialMaster;
	private String groupCode;
	private String barcode;
	private String issueQty;
	private String boxNo;
	private String wareHouse;
	private List<PickReportBean> items;
	private String dispType;
    //option for filter select
	private String issueReqNoArr;
	
	
	public String getIssueReqNoArr() {
		return issueReqNoArr;
	}

	public void setIssueReqNoArr(String issueReqNoArr) {
		this.issueReqNoArr = issueReqNoArr;
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

	public String getReqQty() {
		return reqQty;
	}

	public void setReqQty(String reqQty) {
		this.reqQty = reqQty;
	}

	public String getPensItem() {
		return pensItem;
	}

	public void setPensItem(String pensItem) {
		this.pensItem = pensItem;
	}

	public String getMaterialMaster() {
		return materialMaster;
	}

	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getIssueQty() {
		return issueQty;
	}

	public void setIssueQty(String issueQty) {
		this.issueQty = issueQty;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getWareHouse() {
		return wareHouse;
	}

	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}
   

	public String getIssueReqDateFrom() {
		return issueReqDateFrom;
	}

	public void setIssueReqDateFrom(String issueReqDateFrom) {
		this.issueReqDateFrom = issueReqDateFrom;
	}

	public String getIssueReqDateTo() {
		return issueReqDateTo;
	}

	public void setIssueReqDateTo(String issueReqDateTo) {
		this.issueReqDateTo = issueReqDateTo;
	}

	public String getCustGroup() {
		return custGroup;
	}

	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}

	public String getCustGroupName() {
		return custGroupName;
	}

	public void setCustGroupName(String custGroupName) {
		this.custGroupName = custGroupName;
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

	public String getIssueReqNo() {
		return issueReqNo;
	}

	public void setIssueReqNo(String issueReqNo) {
		this.issueReqNo = issueReqNo;
	}

	public String getIssueReqDate() {
		return issueReqDate;
	}

	public void setIssueReqDate(String issueReqDate) {
		this.issueReqDate = issueReqDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(String userRequest) {
		this.userRequest = userRequest;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<PickReportBean> getItems() {
		return items;
	}

	public void setItems(List<PickReportBean> items) {
		this.items = items;
	}

	public String getDispType() {
		return dispType;
	}

	public void setDispType(String dispType) {
		this.dispType = dispType;
	}
	
	
}
