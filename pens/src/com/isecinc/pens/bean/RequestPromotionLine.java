package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class RequestPromotionLine implements Serializable{

	/**
	 * Default Constructor
	 */
	public RequestPromotionLine() {}
	private static final long serialVersionUID = 8286170868166006317L;

	private String requestNo ;
	private int lineNo;
	private String productCode; 
	private String productName;
	
	private BigDecimal newCtn;
	private BigDecimal newAmount;
	
	private BigDecimal stockCtn;
	private BigDecimal stockQty;
	
	private BigDecimal borrowCtn;
	private BigDecimal borrowQty;
	private BigDecimal borrowAmount;

	private String created; 
	private String createdBy; 
	private String updated; 
	private String updateBy;
	
	//optional
	private String uom1;
	private String uom2;
	private String price1;
	private String price2;
	private String productId;
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
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public BigDecimal getNewCtn() {
		return newCtn;
	}
	public void setNewCtn(BigDecimal newCtn) {
		this.newCtn = newCtn;
	}
	public BigDecimal getNewAmount() {
		return newAmount;
	}
	public void setNewAmount(BigDecimal newAmount) {
		this.newAmount = newAmount;
	}
	public BigDecimal getStockCtn() {
		return stockCtn;
	}
	public void setStockCtn(BigDecimal stockCtn) {
		this.stockCtn = stockCtn;
	}
	public BigDecimal getStockQty() {
		return stockQty;
	}
	public void setStockQty(BigDecimal stockQty) {
		this.stockQty = stockQty;
	}
	public BigDecimal getBorrowCtn() {
		return borrowCtn;
	}
	public void setBorrowCtn(BigDecimal borrowCtn) {
		this.borrowCtn = borrowCtn;
	}
	public BigDecimal getBorrowQty() {
		return borrowQty;
	}
	public void setBorrowQty(BigDecimal borrowQty) {
		this.borrowQty = borrowQty;
	}
	public BigDecimal getBorrowAmount() {
		return borrowAmount;
	}
	public void setBorrowAmount(BigDecimal borrowAmount) {
		this.borrowAmount = borrowAmount;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getUom1() {
		return uom1;
	}
	public void setUom1(String uom1) {
		this.uom1 = uom1;
	}
	public String getUom2() {
		return uom2;
	}
	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}
	public String getPrice1() {
		return price1;
	}
	public void setPrice1(String price1) {
		this.price1 = price1;
	}
	public String getPrice2() {
		return price2;
	}
	public void setPrice2(String price2) {
		this.price2 = price2;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}

	
	

}
