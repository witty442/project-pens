package com.isecinc.pens.report.shipment;

import java.io.Serializable;
import java.math.BigDecimal;

public class FormShipmentReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7738534548445114120L;
	
	private String shipmentNoFrom;
	private String shipmentNoTo;
	private String shipmentDateFrom;
	private String shipmentDateTo;
	private String customerCode;

	private String shipmentNo;
	private String shipmentDate;
	private String customerName;
	private String customerAddress;
	private String orderNo;
	private int tripNo;
	private String productName;
	private BigDecimal planQty;
	private BigDecimal actualQty;
	
	private String shipmentStatus;
	
	public String getShipmentNoFrom() {
		return shipmentNoFrom;
	}
	public void setShipmentNoFrom(String shipmentNoFrom) {
		this.shipmentNoFrom = shipmentNoFrom;
	}
	public String getShipmentNoTo() {
		return shipmentNoTo;
	}
	public void setShipmentNoTo(String shipmentNoTo) {
		this.shipmentNoTo = shipmentNoTo;
	}
	public String getShipmentDateFrom() {
		return shipmentDateFrom;
	}
	public void setShipmentDateFrom(String shipmentDateFrom) {
		this.shipmentDateFrom = shipmentDateFrom;
	}
	public String getShipmentDateTo() {
		return shipmentDateTo;
	}
	public void setShipmentDateTo(String shipmentDateTo) {
		this.shipmentDateTo = shipmentDateTo;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getShipmentNo() {
		return shipmentNo;
	}
	public void setShipmentNo(String shipmentNo) {
		this.shipmentNo = shipmentNo;
	}
	public String getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public int getTripNo() {
		return tripNo;
	}
	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getPlanQty() {
		return planQty;
	}
	public void setPlanQty(BigDecimal planQty) {
		this.planQty = planQty;
	}
	public BigDecimal getActualQty() {
		return actualQty;
	}
	public void setActualQty(BigDecimal actualQty) {
		this.actualQty = actualQty;
	}
	public String getShipmentStatus() {
		return shipmentStatus;
	}
	public void setShipmentStatus(String shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}
}
