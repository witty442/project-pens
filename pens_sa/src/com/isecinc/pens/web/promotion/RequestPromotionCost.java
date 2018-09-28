package com.isecinc.pens.web.promotion;

import java.io.Serializable;
import java.math.BigDecimal;

public class RequestPromotionCost implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6579123993095404390L;
	private String requestNo ;
	private int lineNo;
	private String  costDetail; 
	private BigDecimal costAmount;
	private String createdBy;
	
	public String getRequestNo() {
		return requestNo;
	}
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	public int getLineNo() {
		return lineNo;
	}
	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}
	public String getCostDetail() {
		return costDetail;
	}
	public void setCostDetail(String costDetail) {
		this.costDetail = costDetail;
	}
	public BigDecimal getCostAmount() {
		return costAmount;
	}
	public void setCostAmount(BigDecimal costAmount) {
		this.costAmount = costAmount;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	

}
