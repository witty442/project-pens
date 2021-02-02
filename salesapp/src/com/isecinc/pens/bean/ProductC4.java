package com.isecinc.pens.bean;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.process.modifier.ModifierDescription;

/**
 * Product C4
 * 
 * @author atiz.b
 * @version $Id: ProductC4.java,v 1.0 28/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ProductC4 extends I_PO {

	private static final long serialVersionUID = -2423594576643637999L;

	public ProductC4() {}

	public ProductC4(Product product) {
		this.product = product;
	}

	protected void setDisplayLabel() throws Exception {

	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("\r\nProduct:[%s-%s %s], Price[%s/%s : %s], Promotion: %s\r\n", getProduct().getCode(),
				getProduct().getName(), getProduct().getDescription(), getSubUnitPrice(), getBaseUnitPrice(),
				getCapacity(), getPromotionDescription());
	}

	/** PRODUCT */
	private Product product = new Product();

	/** SUB UNIT PRICE */
	private double subUnitPrice;

	/** BASE UNIT PRICE */
	private double baseUnitPrice;

	/** CAPACITY */
	private int capacity;

	/** PROMOTION DESCRIPTION */
	private String promotionDescription = "";

	/** SALES CHANNEL */
	private String salesChannel;

	/** List Description */
	private List<ModifierDescription> listDescription = new ArrayList<ModifierDescription>();

	/** Description Size */
	private int descriptionSize;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public double getSubUnitPrice() {
		return subUnitPrice;
	}

	public void setSubUnitPrice(double subUnitPrice) {
		this.subUnitPrice = subUnitPrice;
	}

	public double getBaseUnitPrice() {
		return baseUnitPrice;
	}

	public void setBaseUnitPrice(double baseUnitPrice) {
		this.baseUnitPrice = baseUnitPrice;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getPromotionDescription() {
		return promotionDescription;
	}

	public void setPromotionDescription(String promotionDescription) {
		this.promotionDescription = promotionDescription;
	}

	public String getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

	public int getDescriptionSize() {
		return descriptionSize;
	}

	public void setDescriptionSize(int descriptionSize) {
		this.descriptionSize = descriptionSize;
	}

	public List<ModifierDescription> getListDescription() {
		return listDescription;
	}

	public void setListDescription(List<ModifierDescription> listDescription) {
		this.listDescription = listDescription;
	}

}
