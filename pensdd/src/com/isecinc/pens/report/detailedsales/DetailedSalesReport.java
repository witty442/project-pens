package com.isecinc.pens.report.detailedsales;

import java.io.Serializable;

import util.ConvertNullUtil;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class DetailedSalesReport implements Serializable{

	private static final long serialVersionUID = 9117495907654379254L;

	private String orderDate;
	private String orderNo;
	private String name;
	private String name2;
	private double totalAmount;
	private String payment;
	private String interfaces;
	private String exported;
	private String docStatus;
	
	private String fullName;
	private String startDate;
	private String endDate;
	private int sortType = 1;
	
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getPayment() {
		return payment;
	}
	public void setPayment(String payment) {
		this.payment = payment;
	}
	public String getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public int getSortType() {
		return sortType;
	}
	public void setSortType(int sortType) {
		this.sortType = sortType;
	}
	public String getFullName() throws Exception{
		try {
			fullName = ConvertNullUtil.convertToString(this.name) + " " + ConvertNullUtil.convertToString(this.name2);
		} catch (Exception e) {
			throw e;
		}
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
