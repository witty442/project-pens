package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class ConfirmReturnWacoal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4852725376802405340L;
	private String requestDate;
	private String requestNo;
	
	private String remark;
	private String returnDate;
	private String returnNo;

	private String cnNo;
	
	private String status;
	private String statusDesc;
	
	private String createUser;
	private String updateUser;
	private List<ConfirmReturnWacoal> items;
    
	private int lineId;
	private String no;;
	private String boxNo;
	private String jobId;
	private String jobName;
	private int qty;
	private int totalBox;
	private int totalQty;
	private int maxQtyLimitReturn;
	//optional
	private boolean canEdit = false;
	private boolean canPrint = false;
	private boolean canCancel = false;
    private String selected ;
    
    //Desprecate
	private String requestStatus;
	private String requestStatusDesc;
	private String returnStatus;
	private String returnStatusDesc;
    
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
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	public String getCnNo() {
		return cnNo;
	}
	public void setCnNo(String cnNo) {
		this.cnNo = cnNo;
	}
	public boolean isCanPrint() {
		return canPrint;
	}
	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}
	
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public int getMaxQtyLimitReturn() {
		return maxQtyLimitReturn;
	}
	public void setMaxQtyLimitReturn(int maxQtyLimitReturn) {
		this.maxQtyLimitReturn = maxQtyLimitReturn;
	}
	public String getReturnStatusDesc() {
		return returnStatusDesc;
	}
	public void setReturnStatusDesc(String returnStatusDesc) {
		this.returnStatusDesc = returnStatusDesc;
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
	public String getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
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
	
	public String getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getRequestStatusDesc() {
		return requestStatusDesc;
	}
	public void setRequestStatusDesc(String requestStatusDesc) {
		this.requestStatusDesc = requestStatusDesc;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getReturnDate() {
		return returnDate;
	}
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
	public String getReturnNo() {
		return returnNo;
	}
	public void setReturnNo(String returnNo) {
		this.returnNo = returnNo;
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
	public List<ConfirmReturnWacoal> getItems() {
		return items;
	}
	public void setItems(List<ConfirmReturnWacoal> items) {
		this.items = items;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
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
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
    
    
    
	
}
