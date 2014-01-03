package com.isecinc.pens.report.listOrderProduct;

import java.io.Serializable;

/**
 * Receive Bag Report
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBagReport.java,v 1.0 03/12/2010 15:52:00 aneak.t Exp $
 * 
 */
public class ListOrderProductReport implements Serializable{

	private static final long serialVersionUID = -9078415324336388564L;


	private String product;
	private String qty;
	private String unit;
	private String price;
	
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	
	
}
