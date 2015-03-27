package com.isecinc.pens.bean;

import java.io.Serializable;

public class ReturnBoxReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String no;
	private String boxNo;
	private String boxNoDisp;
	private String groupCode;
	private String materialMaster;
	private double wholePriceBF;
	private double retailPriceBF;
	private int qty;
	private double wholePriceBFAmount;
	private double retailPriceBFAmount;
	private String jobName;
	private String custGroup;
	private String cnNo;
	private String address;
	
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public String getCnNo() {
		return cnNo;
	}
	public void setCnNo(String cnNo) {
		this.cnNo = cnNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBoxNoDisp() {
		return boxNoDisp;
	}
	public void setBoxNoDisp(String boxNoDisp) {
		this.boxNoDisp = boxNoDisp;
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
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getMaterialMaster() {
		return materialMaster;
	}
	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}
	public double getWholePriceBF() {
		return wholePriceBF;
	}
	public void setWholePriceBF(double wholePriceBF) {
		this.wholePriceBF = wholePriceBF;
	}
	public double getRetailPriceBF() {
		return retailPriceBF;
	}
	public void setRetailPriceBF(double retailPriceBF) {
		this.retailPriceBF = retailPriceBF;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getWholePriceBFAmount() {
		return wholePriceBFAmount;
	}
	public void setWholePriceBFAmount(double wholePriceBFAmount) {
		this.wholePriceBFAmount = wholePriceBFAmount;
	}
	public double getRetailPriceBFAmount() {
		return retailPriceBFAmount;
	}
	public void setRetailPriceBFAmount(double retailPriceBFAmount) {
		this.retailPriceBFAmount = retailPriceBFAmount;
	}
	
	
}
