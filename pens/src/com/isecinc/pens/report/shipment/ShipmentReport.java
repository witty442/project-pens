package com.isecinc.pens.report.shipment;

import java.io.Serializable;

/**
 * Shipment Report
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReport.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentReport implements Serializable {

	private static final long serialVersionUID = 4790331833376701723L;

	private int id;
	private String shipmentDate;
	private String memberCode;
	private String memberName;
	private String lineNo;
	private String totalLine;
	private int orangeQty;
	private int berryQty;
	private int mixQty;
	private String remark;
	private String deliveryGroup;
	private double amt;

	public String getDeliveryGroup() {
		return deliveryGroup;
	}

	public void setDeliveryGroup(String deliveryGroup) {
		this.deliveryGroup = deliveryGroup;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getTotalLine() {
		return totalLine;
	}

	public void setTotalLine(String totalLine) {
		this.totalLine = totalLine;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShipmentDate() {
		return shipmentDate;
	}

	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	public double getAmt() {
		return amt;
	}

	public void setAmt(double amt) {
		this.amt = amt;
	}

}
