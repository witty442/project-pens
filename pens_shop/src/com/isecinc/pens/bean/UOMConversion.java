package com.isecinc.pens.bean;

import java.sql.ResultSet;

import util.DateToolsUtil;

import com.isecinc.core.model.I_PO;

/**
 * UOM Conversion
 * 
 * @author atiz.b
 * @version $Id: UOMConversion.java,v 1.0 28/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class UOMConversion extends I_PO {

	private static final long serialVersionUID = -6263375971642841422L;

	public UOMConversion() {}

	public UOMConversion(ResultSet rst) throws Exception {
		setProductId(rst.getInt("PRODUCT_ID"));
		setUomId(rst.getString("UOM_ID"));
		setConversionRate(rst.getDouble("CONVERSION_RATE"));
		setDisableDate("");
		if (rst.getTimestamp("DISABLE_DATE") != null)
			setDisableDate(DateToolsUtil.convertToString(rst.getTimestamp("DISABLE_DATE")));
	}

	protected void setDisplayLabel() throws Exception {

	}

	public String toString() {
		return String.format("UOM Conversion[%s]:[%s]-[%s]", getProductId(), getUomId(), getConversionRate());
	}

	private int productId;
	private String uomId;
	private double conversionRate;
	private String disableDate;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getDisableDate() {
		return disableDate;
	}

	public void setDisableDate(String disableDate) {
		this.disableDate = disableDate;
	}

}
