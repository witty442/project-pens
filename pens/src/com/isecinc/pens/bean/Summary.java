package com.isecinc.pens.bean;

import java.math.BigDecimal;
import java.util.List;

import com.isecinc.pens.web.summary.SummaryForm;

public class Summary {

	//Criteria
	private String type;
	private String productCodeFrom;
	private String productCodeTo;
	private String orderDateFrom;
	private String orderDateTo;
	

	//results
	private String productCode;
    private String orderDate;
    private String productName;
    private String qty;
    private String fullUOM;
    private String qtyPromotion;
    
    
    
	public String getQtyPromotion() {
		return qtyPromotion;
	}
	public void setQtyPromotion(String qtyPromotion) {
		this.qtyPromotion = qtyPromotion;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getProductCodeFrom() {
		return productCodeFrom;
	}
	public void setProductCodeFrom(String productCodeFrom) {
		this.productCodeFrom = productCodeFrom;
	}
	public String getProductCodeTo() {
		return productCodeTo;
	}
	public void setProductCodeTo(String productCodeTo) {
		this.productCodeTo = productCodeTo;
	}
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getFullUOM() {
		return fullUOM;
	}
	public void setFullUOM(String fullUOM) {
		this.fullUOM = fullUOM;
	}
	
    
    
    
}
