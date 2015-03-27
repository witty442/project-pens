package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class RenewBox implements Serializable{

	private static final long serialVersionUID = 9211619557079034456L;
	
	private int lineId;
	private String jobId;
	private String jobName;
	private String boxNo;
	private String boxNoRef;
	private String qty;
	private String remainQty;
	private String totalBoxQty;
	private int qtyInt;
	
	private List<RenewBox> items;
	private String createUser;
	private String updateUser;

	//optional
	private boolean canEdit;

	private String lineItemErrorStyle ;
	private boolean resultProcess;
	private boolean modeConfirm;
	private boolean modeEdit;
	private String selected ;
	private Map<String,String> pensItemMapAll;
	
	
	public String getBoxNoRef() {
		return boxNoRef;
	}
	public void setBoxNoRef(String boxNoRef) {
		this.boxNoRef = boxNoRef;
	}
	public String getRemainQty() {
		return remainQty;
	}
	public void setRemainQty(String remainQty) {
		this.remainQty = remainQty;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
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
	public String getTotalBoxQty() {
		return totalBoxQty;
	}
	public void setTotalBoxQty(String totalBoxQty) {
		this.totalBoxQty = totalBoxQty;
	}
	public int getQtyInt() {
		return qtyInt;
	}
	public void setQtyInt(int qtyInt) {
		this.qtyInt = qtyInt;
	}
	public List<RenewBox> getItems() {
		return items;
	}
	public void setItems(List<RenewBox> items) {
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
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getLineItemErrorStyle() {
		return lineItemErrorStyle;
	}
	public void setLineItemErrorStyle(String lineItemErrorStyle) {
		this.lineItemErrorStyle = lineItemErrorStyle;
	}
	public boolean isResultProcess() {
		return resultProcess;
	}
	public void setResultProcess(boolean resultProcess) {
		this.resultProcess = resultProcess;
	}
	public boolean isModeConfirm() {
		return modeConfirm;
	}
	public void setModeConfirm(boolean modeConfirm) {
		this.modeConfirm = modeConfirm;
	}
	public boolean isModeEdit() {
		return modeEdit;
	}
	public void setModeEdit(boolean modeEdit) {
		this.modeEdit = modeEdit;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public Map<String, String> getPensItemMapAll() {
		return pensItemMapAll;
	}
	public void setPensItemMapAll(Map<String, String> pensItemMapAll) {
		this.pensItemMapAll = pensItemMapAll;
	}

	
	
	
	
}
