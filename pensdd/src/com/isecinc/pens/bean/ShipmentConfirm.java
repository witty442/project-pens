package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;

import com.isecinc.core.model.I_PO;

public class ShipmentConfirm extends I_PO implements Serializable {
	public ShipmentConfirm() {
		orderLine = new OrderLine();
	}
	
	public ShipmentConfirm(ResultSet rset) throws Exception {
		setOrderLine(new OrderLine(rset));
		setMemberCode(rset.getString("memberCode"));
		setMemberName(rset.getString("FIRST_NAME")+" "+rset.getString("LAST_NAME"));
		setDeliveryGroup(rset.getString("delivery_group"));
		setOrderNo(rset.getString("order_no"));
		setConfirmQty(rset.getInt("qty")); 
	}

	// Shipment Confirm Criteria
	private String memberCode ;
	private String memberName;
	private String shipDate ;
	private String deliveryGroup;
	private String orderNo;
	private String classLine;
	
	/** optional **/
	private int tripNo;
	private String no;
	private int totalGroup;
	private List<ShipmentConfirm> shipmentConfirmList;
	
	
	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public int getTotalGroup() {
		return totalGroup;
	}

	public void setTotalGroup(int totalGroup) {
		this.totalGroup = totalGroup;
	}

	public List<ShipmentConfirm> getShipmentConfirmList() {
		return shipmentConfirmList;
	}

	public void setShipmentConfirmList(List<ShipmentConfirm> shipmentConfirmList) {
		this.shipmentConfirmList = shipmentConfirmList;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getClassLine() {
		return classLine;
	}

	public void setClassLine(String classLine) {
		this.classLine = classLine;
	}

	private OrderLine orderLine;
	
	public String getIsPostponeShipDate() {
		return isPostponeShipDate;
	}

	public void setIsPostponeShipDate(String isPostponeShipDate) {
		this.isPostponeShipDate = isPostponeShipDate;
	}

	public String getPostponeDate() {
		return postponeDate;
	}

	public void setPostponeDate(String postponeDate) {
		this.postponeDate = postponeDate;
	}

	// Define Answer
	private int confirmQty;
	private int lineId;
	private String isConfirm;
	private String isPostponeShipDate;
	private String postponeDate;
	private String comment; 
	
	private String isPostponeReqDate;
	
	private String isReSchedule;
	
	private String isCancel;

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getShipDate() {
		return shipDate;
	}

	public void setShipDate(String shipDate) {
		this.shipDate = shipDate;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getDeliveryGroup() {
		return deliveryGroup;
	}

	public void setDeliveryGroup(String deliveryGroup) {
		this.deliveryGroup = deliveryGroup;
	}

	public OrderLine getOrderLine() {
		return orderLine;
	}

	public void setOrderLine(OrderLine orderLine) {
		this.orderLine = orderLine;
	}

	protected void setDisplayLabel() throws Exception {
		// TODO Auto-generated method stub
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getConfirmQty() {
		return confirmQty;
	}

	public void setConfirmQty(int confirmQty) {
		this.confirmQty = confirmQty;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public String getIsConfirm() {
		return isConfirm;
	}

	public void setIsConfirm(String isConfirm) {
		this.isConfirm = isConfirm;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(String isCancel) {
		this.isCancel = isCancel;
	}

	public String getIsPostponeReqDate() {
		return isPostponeReqDate;
	}

	public void setIsPostponeReqDate(String isPostponeReqDate) {
		this.isPostponeReqDate = isPostponeReqDate;
	}

	public String getIsReSchedule() {
		return isReSchedule;
	}

	public void setIsReSchedule(String isReSchedule) {
		this.isReSchedule = isReSchedule;
	}
	
}
