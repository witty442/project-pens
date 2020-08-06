package com.isecinc.pens.bean;

import java.util.List;

public class OrderEDIBean {

	private String loadNo;
	private String orderNo;
	private String orderDate;
	private String customerCode;
	private String customerName;
	private String netAmount;
	private StringBuffer dataStrBuffer;
	private List<OrderEDIBean> items;
	
	
	public List<OrderEDIBean> getItems() {
		return items;
	}
	public void setItems(List<OrderEDIBean> items) {
		this.items = items;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}
	public String getLoadNo() {
		return loadNo;
	}
	public void setLoadNo(String loadNo) {
		this.loadNo = loadNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	
	
}
