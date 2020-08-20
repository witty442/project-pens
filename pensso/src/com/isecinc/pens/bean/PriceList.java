package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;

/**
 * Price List
 * 
 * @author Aneak.t
 * @version $Id: PriceList.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class PriceList extends I_PO implements Serializable {

	private static final long serialVersionUID = 6139236598623563453L;

	/**
	 * Default Constructor
	 */
	public PriceList() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public PriceList(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("PRICELIST_ID"));
		setName(rst.getString("NAME").trim());
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		setIsActive(rst.getString("ISACTIVE").trim());
		setPriceListType(rst.getString("PRICE_LIST_TYPE").trim());
		setEffectiveDate("");
		if (rst.getDate("EFFECTIVE_DATE") != null)
			setEffectiveDate(DateToolsUtil.convertToString(rst.getDate("EFFECTIVE_DATE")));
		setEffectiveToDate("");
		if (rst.getDate("EFFECTIVETO_DATE") != null)
			setEffectiveToDate(DateToolsUtil.convertToString(rst.getDate("EFFECTIVETO_DATE")));

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
	private String name;
	private String description;
	private String isActive;
	private String priceListType;
	private String effectiveDate;
	private String effectiveToDate;

	private ProductPrice[] productPrices = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getPriceListType() {
		return priceListType;
	}

	public void setPriceListType(String priceListType) {
		this.priceListType = priceListType;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getEffectiveToDate() {
		return effectiveToDate;
	}

	public void setEffectiveToDate(String effectiveToDate) {
		this.effectiveToDate = effectiveToDate;
	}

	public ProductPrice[] getProductPrices() {
		return productPrices;
	}

	public void setProductPrices(ProductPrice[] productPrices) {
		this.productPrices = productPrices;
	}

}
