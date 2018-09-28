package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.Date;

public class AutoPOHH implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3217318910429285314L;
	private String orderDate;
	private String custGroup;
	private String customerCode;
	private String refPo;
	private String status;
	private String createUser;
	private Date createDate;
	private boolean canSave;
	
	
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public boolean isCanSave() {
		return canSave;
	}
	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public String getRefPo() {
		return refPo;
	}
	public void setRefPo(String refPo) {
		this.refPo = refPo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}
