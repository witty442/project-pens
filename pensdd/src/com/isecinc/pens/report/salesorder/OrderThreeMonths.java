package com.isecinc.pens.report.salesorder;

import java.io.Serializable;

import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;

/**
 * Sales Order 3 months Report
 * 
 * @author atiz.b
 * @version $Id: SalesOrderThreeMonths.java,v 1.0 17/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderThreeMonths implements Serializable {

	private static final long serialVersionUID = -735502887439729074L;

	/** Customer_Type */
	private String customerType;

	/** User_id */
	private int userId;

	/** Customer_ID */
	private int customerId;

	/** Customer Code */
	private String customerCode;

	/** Customer_Name */
	private String customerName;

	/** Total_Order */
	private int totalOrder;

	/** Total_Amount */
	private double totalAmount;

	/** Product */
	private Product product;

	/** UOM */
	private UOM uom;

	/** FROM */
	private String dateFrom;

	/** TO */
	private String dateTo;

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getTotalOrder() {
		return totalOrder;
	}

	public void setTotalOrder(int totalOrder) {
		this.totalOrder = totalOrder;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public UOM getUom() {
		return uom;
	}

	public void setUom(UOM uom) {
		this.uom = uom;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

}
