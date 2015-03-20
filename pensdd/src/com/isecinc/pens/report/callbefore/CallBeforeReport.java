package com.isecinc.pens.report.callbefore;

import java.io.Serializable;

/**
 * Call Before Send Report
 * 
 * @author Aneak.t
 * @version $Id: CallBeforeReport.java,v 1.0 15/03/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CallBeforeReport implements Serializable{

	private static final long serialVersionUID = -555788605858505122L;

	private int id;
	private String shippingDate;
	private String code;
	private String fullName;
	private String mobile;
	private String lineNo;
	private String totalLine;
	private double orangeQty;
	private double berryQty;
	private double mixQty;
	private String remark;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
	public double getOrangeQty() {
		return orangeQty;
	}
	public void setOrangeQty(double orangeQty) {
		this.orangeQty = orangeQty;
	}
	public double getBerryQty() {
		return berryQty;
	}
	public void setBerryQty(double berryQty) {
		this.berryQty = berryQty;
	}
	public double getMixQty() {
		return mixQty;
	}
	public void setMixQty(double mixQty) {
		this.mixQty = mixQty;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
