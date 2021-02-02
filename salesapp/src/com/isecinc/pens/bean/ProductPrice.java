package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;

/**
 * Product Price
 * 
 * @author Aneak.t
 * @version $Id: ProductPrice.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ProductPrice extends I_PO implements Serializable {

	private static final long serialVersionUID = -1857230264362810536L;

	/**
	 * Default Constructor
	 */
	public ProductPrice() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public ProductPrice(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("PRICELIST_ID"));
		setProduct(new MProduct().find(String.valueOf(rst.getInt("PRODUCT_ID"))));
		setPriceList(new MPriceList().find(String.valueOf(rst.getInt("PRICELIST_ID"))));
		setPrice(rst.getDouble("PRICE"));
		setIsActive(rst.getString("ISACTIVE").trim());
		setUom(new MUOM().find(rst.getString("UOM_ID")));

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	private int id;
	private PriceList priceList = new PriceList();
	private Product product = new Product();
	private UOM uom = new UOM();
	private double price;
	private String isActive;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public UOM getUom() {
		return uom;
	}

	public void setUom(UOM uom) {
		this.uom = uom;
	}

}
