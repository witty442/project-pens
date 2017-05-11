package com.isecinc.pens.bean;

import java.io.Serializable;

public class Master implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8375625813530169855L;
	private String referenceCode;
	private String interfaceValue;
	private String  interfaceDesc;
	private String  pensValue;
	private String  pensDesc;
	private String  pensDesc2;
	private String  pensDesc3;
	private String  createDate;
	private String  createUser;
	private String  fileName;
	private String sequence;
	private String status;
	
	/** option **/
	private String orderBy;

	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPensDesc3() {
		return pensDesc3;
	}
	public void setPensDesc3(String pensDesc3) {
		this.pensDesc3 = pensDesc3;
	}
	public String getPensDesc2() {
		return pensDesc2;
	}
	public void setPensDesc2(String pensDesc2) {
		this.pensDesc2 = pensDesc2;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public String getInterfaceValue() {
		return interfaceValue;
	}
	public void setInterfaceValue(String interfaceValue) {
		this.interfaceValue = interfaceValue;
	}
	public String getInterfaceDesc() {
		return interfaceDesc;
	}
	public void setInterfaceDesc(String interfaceDesc) {
		this.interfaceDesc = interfaceDesc;
	}
	public String getPensValue() {
		return pensValue;
	}
	public void setPensValue(String pensValue) {
		this.pensValue = pensValue;
	}
	public String getPensDesc() {
		return pensDesc;
	}
	public void setPensDesc(String pensDesc) {
		this.pensDesc = pensDesc;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
}
