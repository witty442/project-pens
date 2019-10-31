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

	private String cashFlag = "";
	private String chequeFlag = "";
	
	private String DR_AC_NO ;
	private String DR_DESC ;
	private String DR_AMOUNT;
	private String DR_INPUT_TAX_AMOUNT ;
	private String DR_TOTAL ;
	
	private String CR_AC_NO ;
	private String CR_DESC ;
	private String CR_AMOUNT ;
	private String CR_ACC_WT_TAX_AMOUNT;
	private String CR_TOTAL;

	private boolean canPrint;
	private List<PayBean> items;
	private String employeeName;
	
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public String getCashFlag() {
		return cashFlag;
	}
	public void setCashFlag(String cashFlag) {
		this.cashFlag = cashFlag;
	}
	public String getChequeFlag() {
		return chequeFlag;
	}
	public void setChequeFlag(String chequeFlag) {
		this.chequeFlag = chequeFlag;
	}
	public boolean isCanPrint() {
		return canPrint;
	}
	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}
	public String getDR_AC_NO() {
		return DR_AC_NO;
	}
	public void setDR_AC_NO(String dR_AC_NO) {
		DR_AC_NO = dR_AC_NO;
	}
	public String getDR_DESC() {
		return DR_DESC;
	}
	public void setDR_DESC(String dR_DESC) {
		DR_DESC = dR_DESC;
	}
	public String getDR_AMOUNT() {
		return DR_AMOUNT;
	}
	public void setDR_AMOUNT(String dR_AMOUNT) {
		DR_AMOUNT = dR_AMOUNT;
	}
	public String getDR_INPUT_TAX_AMOUNT() {
		return DR_INPUT_TAX_AMOUNT;
	}
	public void setDR_INPUT_TAX_AMOUNT(String dR_INPUT_TAX_AMOUNT) {
		DR_INPUT_TAX_AMOUNT = dR_INPUT_TAX_AMOUNT;
	}
	public String getDR_TOTAL() {
		return DR_TOTAL;
	}
	public void setDR_TOTAL(String dR_TOTAL) {
		DR_TOTAL = dR_TOTAL;
	}
	public String getCR_AC_NO() {
		return CR_AC_NO;
	}
	public void setCR_AC_NO(String cR_AC_NO) {
		CR_AC_NO = cR_AC_NO;
	}
	public String getCR_DESC() {
		return CR_DESC;
	}
	public void setCR_DESC(String cR_DESC) {
		CR_DESC = cR_DESC;
	}
	public String getCR_AMOUNT() {
		return CR_AMOUNT;
	}
	public void setCR_AMOUNT(String cR_AMOUNT) {
		CR_AMOUNT = cR_AMOUNT;
	}
	public String getCR_ACC_WT_TAX_AMOUNT() {
		return CR_ACC_WT_TAX_AMOUNT;
	}
	public void setCR_ACC_WT_TAX_AMOUNT(String cR_ACC_WT_TAX_AMOUNT) {
		CR_ACC_WT_TAX_AMOUNT = cR_ACC_WT_TAX_AMOUNT;
	}
	public String getCR_TOTAL() {
		return CR_TOTAL;
	}
	public void setCR_TOTAL(String cR_TOTAL) {
		CR_TOTAL = cR_TOTAL;
	}
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
