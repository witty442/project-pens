package com.isecinc.pens.report.shipmentsummary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ShipmentSummaryReport implements Serializable /*, Comparable*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3891917242278626563L;
	// Defind Parameter
    private String shippingDateFrom;
    private String shippingDateTo;
    private String confirmDateFrom;
    private String confirmDateTo;
    private String p_memberCode;
    private String p_memberName;
    
    //Define Report Field
    private Timestamp shippingDate;
    private Timestamp cfShipDate;
    private String memberCode;
    private String memberName;
    private String route;
    private int tripNo;
    private int no;
    private String productCode;
    private String uom;
    private String productName;
    private BigDecimal qty;
    private BigDecimal act_qty;
    private BigDecimal need_bill;
    private BigDecimal act_need_bill;
    
    
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getShippingDateFrom() {
		return shippingDateFrom;
	}
	public void setShippingDateFrom(String shippingDateFrom) {
		this.shippingDateFrom = shippingDateFrom;
	}
	public String getShippingDateTo() {
		return shippingDateTo;
	}
	public void setShippingDateTo(String shippingDateTo) {
		this.shippingDateTo = shippingDateTo;
	}
	public String getConfirmDateFrom() {
		return confirmDateFrom;
	}
	public void setConfirmDateFrom(String confirmDateFrom) {
		this.confirmDateFrom = confirmDateFrom;
	}
	public String getConfirmDateTo() {
		return confirmDateTo;
	}
	public void setConfirmDateTo(String confirmDateTo) {
		this.confirmDateTo = confirmDateTo;
	}
	public String getP_memberCode() {
		return p_memberCode;
	}
	public void setP_memberCode(String p_memberCode) {
		this.p_memberCode = p_memberCode;
	}
	public String getP_memberName() {
		return p_memberName;
	}
	public void setP_memberName(String p_memberName) {
		this.p_memberName = p_memberName;
	}
	public Timestamp getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(Timestamp shippingDate) {
		this.shippingDate = shippingDate;
	}
	public Timestamp getCfShipDate() {
		return cfShipDate;
	}
	public void setCfShipDate(Timestamp cfShipDate) {
		this.cfShipDate = cfShipDate;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public int getTripNo() {
		return tripNo;
	}
	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	public BigDecimal getAct_qty() {
		return act_qty;
	}
	public void setAct_qty(BigDecimal act_qty) {
		this.act_qty = act_qty;
	}
	public BigDecimal getNeed_bill() {
		return need_bill;
	}
	public void setNeed_bill(BigDecimal need_bill) {
		this.need_bill = need_bill;
	}
	public BigDecimal getAct_need_bill() {
		return act_need_bill;
	}
	public void setAct_need_bill(BigDecimal act_need_bill) {
		this.act_need_bill = act_need_bill;
	}
    
	
}
