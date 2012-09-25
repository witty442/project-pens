package com.isecinc.pens.bean;

import java.io.Serializable;

/**
 * Interface Summary Class
 * 
 * @author Atiz.b
 * @version $Id: InterfaceSummary.java,v 1.0 7/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class InterfaceSummary implements Serializable {
	private static final long serialVersionUID = -519254129804596855L;

	public static String TYPE_ORDER = "Order";
	public static String TYPE_RECEIPT = "Receipt";
	public static String TYPE_VISIT = "Visit";

	private String recordType;
	private String recordNo;
	private String recordTime;
	private String customer;
	private double recordAmount;
	private double recordAmount2;
	private String exported;
	private String interfaces;
	private String recordReference1;
	private String recordReference2;
	private String closed;
	private int userId;
	private String orderType;
	private String status;

	public String toString() {
		return String.format("Summary %s-%s[%s]- %s amount[%s/%s] [exported %s/interfaces %s] reference[%s/%s]",
				getRecordType(), getRecordNo(), getRecordTime(), getCustomer(), getRecordAmount(), getRecordAmount2(),
				getExported(), getInterfaces(), getRecordReference1(), getRecordReference2());
	}

	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getRecordNo() {
		return recordNo;
	}

	public void setRecordNo(String recordNo) {
		this.recordNo = recordNo;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public double getRecordAmount() {
		return recordAmount;
	}

	public void setRecordAmount(double recordAmount) {
		this.recordAmount = recordAmount;
	}

	public double getRecordAmount2() {
		return recordAmount2;
	}

	public void setRecordAmount2(double recordAmount2) {
		this.recordAmount2 = recordAmount2;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getRecordReference1() {
		return recordReference1;
	}

	public void setRecordReference1(String recordReference1) {
		this.recordReference1 = recordReference1;
	}

	public String getRecordReference2() {
		return recordReference2;
	}

	public void setRecordReference2(String recordReference2) {
		this.recordReference2 = recordReference2;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getClosed() {
		return closed;
	}

	public void setClosed(String closed) {
		this.closed = closed;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
