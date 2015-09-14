package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

import com.isecinc.pens.inf.helper.Utils;

public class ReqFinish implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4852725376802405340L;
	private int no;
	private String requestDate;
	private String confirmDate;
	private String requestNo;
	private String status;
	private String statusDesc;
	private String remark;
	private int totalBox;
	private int totalQty;
	private String wareHouse;

	private String createUser;
	private String updateUser;
	private List<ReqFinish> items;
    
	private int lineId;
	private String boxNo;
	private String jobId;
	private String jobName;
	private String pensItem;
	private String materialMaster;
	private String groupCode;
	private String barcode;
	private String onhandQty;
	private String qtyDisp;
	private String totalBoxQty;
	private int qty;
	private String wholePriceBF;
	private String retailPriceBF;
	private String lineStatus;
	
	//optional
	private boolean canEdit = false;
	private boolean canPrint = false;
	private boolean canCancel = false;
    private String selected ;
    private String typeDisp;
	
    
	public String getWareHouse() {
		return wareHouse;
	}

	public void setWareHouse(String wareHouse) {
		this.wareHouse = wareHouse;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getTypeDisp() {
		return typeDisp;
	}

	public void setTypeDisp(String typeDisp) {
		this.typeDisp = typeDisp;
	}

	public boolean isCanPrint() {
		return canPrint;
	}

	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}

	public boolean isCanCancel() {
		return canCancel;
	}

	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}

	public String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getQtyDisp() {
		return Utils.decimalFormat(getQty(), Utils.format_current_no_disgit);
	}

	public void setQtyDisp(String qtyDisp) {
		this.qtyDisp = qtyDisp;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
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

	public String getOnhandQty() {
		return onhandQty;
	}

	public void setOnhandQty(String onhandQty) {
		this.onhandQty = onhandQty;
	}

	public String getTotalBoxQty() {
		return totalBoxQty;
	}

	public void setTotalBoxQty(String totalBoxQty) {
		this.totalBoxQty = totalBoxQty;
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

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
    
	public int getTotalBox() {
		return totalBox;
	}

	public void setTotalBox(int totalBox) {
		this.totalBox = totalBox;
	}

	public int getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(int totalQty) {
		this.totalQty = totalQty;
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

	public List<ReqFinish> getItems() {
		return items;
	}

	public void setItems(List<ReqFinish> items) {
		this.items = items;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
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

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	
	
}
