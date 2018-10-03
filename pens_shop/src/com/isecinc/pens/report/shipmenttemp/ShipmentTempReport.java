package com.isecinc.pens.report.shipmenttemp;

/**
 * Shipment Temporary Report
 * 
 * @author Aneak.t
 * @version $Id: ShipmentTempReport.java,v 1.0 11/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentTempReport {
	private int id;
	private String orderNo;
	private String shipAddress;
	private String lineNo;
	private String memberCode;
	private String memberName;
	private String phone;
	private String phone2;
	private String mobile;
	private String mobile2;
	private int orangeQty;
	private int berryQty;
	private int mixQty;
	private String shipmentDate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getShipAddress() {
		return shipAddress;
	}
	public void setShipAddress(String shipAddress) {
		this.shipAddress = shipAddress;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getMobile2() {
		return mobile2;
	}
	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}
	public int getOrangeQty() {
		return orangeQty;
	}
	public void setOrangeQty(int orangeQty) {
		this.orangeQty = orangeQty;
	}
	public int getBerryQty() {
		return berryQty;
	}
	public void setBerryQty(int berryQty) {
		this.berryQty = berryQty;
	}
	public int getMixQty() {
		return mixQty;
	}
	public void setMixQty(int mixQty) {
		this.mixQty = mixQty;
	}
	public String getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	
}
