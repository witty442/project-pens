package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MoveWarehouse implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	//Head
	private String openDate;
	private String closeDate;
	private String newJobId;
	private String newJobName;
	private String warehouseFrom;
	private String warehouseFromDesc;
	private String warehouseTo;
	
	int no;
	private String jobId;
	private String jobName;
	private String storeCode;//CustomerNo
	private String storeName;
	private String subInv;
	private String storeNo;
	private String remark;
	//Line
	private int lineId;
	private String boxNo;
	private String newBoxNo;
	private String qty;
    private String createUser;
    private String updateUser;
	
	private String totalQty;
	private String totalBox;
	//optional
	private boolean canEdit = false;
    private String selected ;
	
	private List<MoveWarehouse> items;
	
	
	public String getNewBoxNo() {
		return newBoxNo;
	}
	public void setNewBoxNo(String newBoxNo) {
		this.newBoxNo = newBoxNo;
	}
	public String getWarehouseFromDesc() {
		return warehouseFromDesc;
	}
	public void setWarehouseFromDesc(String warehouseFromDesc) {
		this.warehouseFromDesc = warehouseFromDesc;
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
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public List<MoveWarehouse> getItems() {
		return items;
	}
	public void setItems(List<MoveWarehouse> items) {
		this.items = items;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getNewJobId() {
		return newJobId;
	}
	public void setNewJobId(String newJobId) {
		this.newJobId = newJobId;
	}
	public String getNewJobName() {
		return newJobName;
	}
	public void setNewJobName(String newJobName) {
		this.newJobName = newJobName;
	}
	public String getWarehouseFrom() {
		return warehouseFrom;
	}
	public void setWarehouseFrom(String warehouseFrom) {
		this.warehouseFrom = warehouseFrom;
	}
	public String getWarehouseTo() {
		return warehouseTo;
	}
	public void setWarehouseTo(String warehouseTo) {
		this.warehouseTo = warehouseTo;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jonName) {
		this.jobName = jonName;
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
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(String totalQty) {
		this.totalQty = totalQty;
	}
	public String getTotalBox() {
		return totalBox;
	}
	public void setTotalBox(String totalBox) {
		this.totalBox = totalBox;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	
	
	
}
