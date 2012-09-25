package com.isecinc.pens.bean;

import java.sql.ResultSet;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_PO;

/**
 * Member Trip Comment
 * 
 * @author atiz.b
 * @version $Id: MemberTripComment.java,v 1.0 26/01/2011 15:52:00 aneak.t Exp $
 * 
 */
public class MemberTripComment extends I_PO {

	private static final long serialVersionUID = -2722196418765514625L;

	public MemberTripComment() {}

	public MemberTripComment(ResultSet rst) throws Exception {
		setId(rst.getInt("TRIP_COMMENT_ID"));
		setTripNo(rst.getInt("TRIP_NO"));
		setOrderId(rst.getInt("ORDER_ID"));
		setTripComment(ConvertNullUtil.convertToString(rst.getString("TRIP_COMMENT")).trim());
		setDisplayLabel();
	}

	@Override
	protected void setDisplayLabel() throws Exception {}

	/** MEMBER_TRIP_ID */
	private int id;

	/** TRIP_NO */
	private int tripNo;

	/** ORDER_ID */
	private int orderId;

	/** TRIP_COMMENT */
	private String tripComment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getTripComment() {
		return tripComment;
	}

	public void setTripComment(String tripComment) {
		this.tripComment = tripComment;
	}
}
