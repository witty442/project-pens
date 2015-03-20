package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import util.ConvertNullUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MProductCategory;
import com.isecinc.pens.model.MUOM;

/**
 * Product
 * 
 * @author Aneak.t
 * @version $Id: Product.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */
public class Product extends I_PO implements Serializable {

	private static final long serialVersionUID = -1689986626040521385L;

	/**
	 * Default Constructor
	 */
	public Product() {}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Product(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("PRODUCT_ID"));
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		setUom(new MUOM().find(rst.getString("UOM_ID")));
		setIsActive(rst.getString("ISACTIVE").trim());
		setProductCategory(new MProductCategory().find(String.valueOf(rst.getInt("PRODUCT_CATEGORY_ID"))));

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

	/**
	 * To String
	 */
	public String toString() {
		return String.format("Product[%s] [%s]-[%s] [%s]", getId(), getCode(), getName(), getDescription());
	}

	private int id;
	private String code;
	private String name;
	private String description;
	private UOM uom = new UOM();
	private String isActive;
	private ProductCategory productCategory = new ProductCategory();

	/** Product Price **/
	private ProductPrice[] productPrices = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UOM getUom() {
		return uom;
	}

	public void setUom(UOM uom) {
		this.uom = uom;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public ProductPrice[] getProductPrices() {
		return productPrices;
	}

	public void setProductPrices(ProductPrice[] productPrices) {
		this.productPrices = productPrices;
	}

}
