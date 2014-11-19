package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class ReqReturnWacoal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4852725376802405340L;
	private int no;
	private String requestDate;
	private String requestNo;
	private String status;
	private String statusDesc;
	private String remark;
	private int totalBox;
	private int totalQty;

	private String createUser;
	private String updateUser;
	private List<ReqReturnWacoal> items;
    
	private int lineId;
	private String boxNo;
	private String jobId;
	private String jobName;
	private int qty;
	
	//optional
	private boolean canEdit = false;
    private String selected ;
	
	
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

	public List<ReqReturnWacoal> getItems() {
		return items;
	}

	public void setItems(List<ReqReturnWacoal> items) {
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
	
	
}
