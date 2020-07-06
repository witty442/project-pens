package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import util.ConvertNullUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
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
		setTaxable( Utils.isNull(rst.getString("taxable")));
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
	private String uom1;
	private String uom2;
	private String conversionRate;
	private String taxable;
	
	//optional
	private String uom1Pac;
	private String uom2Pac;
	private String uom1Price;
	private String uom1ConvRate;
	private String uom2ConvRate;
	
	/** Product Price **/
	private ProductPrice[] productPrices = null;

	
   
	public String getUom1ConvRate() {
		return uom1ConvRate;
	}

	public void setUom1ConvRate(String uom1ConvRate) {
		this.uom1ConvRate = uom1ConvRate;
	}

	public String getUom2ConvRate() {
		return uom2ConvRate;
	}

	public void setUom2ConvRate(String uom2ConvRate) {
		this.uom2ConvRate = uom2ConvRate;
	}

	public String getUom1Price() {
		return uom1Price;
	}

	public void setUom1Price(String uom1Price) {
		this.uom1Price = uom1Price;
	}

	public String getUom1Pac() {
		return uom1Pac;
	}

	public void setUom1Pac(String uom1Pac) {
		this.uom1Pac = uom1Pac;
	}

	public String getUom2Pac() {
		return uom2Pac;
	}

	public void setUom2Pac(String uom2Pac) {
		this.uom2Pac = uom2Pac;
	}

	public String getTaxable() {
		return taxable;
	}

	public void setTaxable(String taxable) {
		this.taxable = taxable;
	}

	public String getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(String conversionRate) {
		this.conversionRate = conversionRate;
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
