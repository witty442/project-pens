package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class PayBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1011918568953216669L;
	private String dateFrom;
	private String dateTo;
	private String createUser;
	private String updateUser;
	
	private String docNo;
	private int lineId;
	private String docDate;
	private String payToName;
	private String deptId;
	private String deptName;
	private String sectionId;
	private String sectionName;
	private String paymethod;
	private String status;
	
	private String accountName;
	private String detail;
	private String amount;
	private String description;
	
	private String totalAmount;
	private double totalAmountDouble;
	private String totalAmountLetter;
	
	private List<PayBean> items;
	
	
	public double getTotalAmountDouble() {
		return totalAmountDouble;
	}
	public void setTotalAmountDouble(double totalAmountDouble) {
		this.totalAmountDouble = totalAmountDouble;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTotalAmountLetter() {
		return totalAmountLetter;
	}
	public void setTotalAmountLetter(String totalAmountLetter) {
		this.totalAmountLetter = totalAmountLetter;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDocDate() {
		return docDate;
	}
	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLineId() {
		return lineId;
	}
	public void setLineId(int lineId) {
		this.lineId = lineId;
	}
	public List<PayBean> getItems() {
		return items;
	}
	public void setItems(List<PayBean> items) {
		this.items = items;
	}
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
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
	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	public String getPayToName() {
		return payToName;
	}
	public void setPayToName(String payToName) {
		this.payToName = payToName;
	}
	
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
}
