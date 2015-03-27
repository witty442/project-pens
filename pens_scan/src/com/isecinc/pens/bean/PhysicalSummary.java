package com.isecinc.pens.bean;

import java.io.Serializable;

public class PhysicalSummary implements Serializable{
   /**
	 * 
	 */
	private static final long serialVersionUID = 4282954807769756929L;
/**Criteria **/
	private String countDateFrom;
	private String countDateTo;
	
	private String pensCustCodeFrom;
	private String pensCustCodeTo;
	private String pensCustNameFrom;
	private String pensCustNameTo;
	
	private String item;
	private String countDate;
	private String pensCustCode;
	private String pensCustName;
	private String barcode;
	private String fileName;
	private String createDate;
	private String createUser;
	private String updateDate;
	private String updateUser;
	
	
	
	public String getPensCustName() {
		return pensCustName;
	}
	public void setPensCustName(String pensCustName) {
		this.pensCustName = pensCustName;
	}
	public String getPensCustNameFrom() {
		return pensCustNameFrom;
	}
	public void setPensCustNameFrom(String pensCustNameFrom) {
		this.pensCustNameFrom = pensCustNameFrom;
	}
	public String getPensCustNameTo() {
		return pensCustNameTo;
	}
	public void setPensCustNameTo(String pensCustNameTo) {
		this.pensCustNameTo = pensCustNameTo;
	}
	public String getCountDateFrom() {
		return countDateFrom;
	}
	public void setCountDateFrom(String countDateFrom) {
		this.countDateFrom = countDateFrom;
	}
	public String getCountDateTo() {
		return countDateTo;
	}
	public void setCountDateTo(String countDateTo) {
		this.countDateTo = countDateTo;
	}
	public String getPensCustCodeFrom() {
		return pensCustCodeFrom;
	}
	public void setPensCustCodeFrom(String pensCustCodeFrom) {
		this.pensCustCodeFrom = pensCustCodeFrom;
	}
	public String getPensCustCodeTo() {
		return pensCustCodeTo;
	}
	public void setPensCustCodeTo(String pensCustCodeTo) {
		this.pensCustCodeTo = pensCustCodeTo;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getCountDate() {
		return countDate;
	}
	public void setCountDate(String countDate) {
		this.countDate = countDate;
	}
	public String getPensCustCode() {
		return pensCustCode;
	}
	public void setPensCustCode(String pensCustCode) {
		this.pensCustCode = pensCustCode;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	
}
