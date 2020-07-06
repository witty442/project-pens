package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import util.BeanParameter;
import util.ConvertNullUtil;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductCategory;
import com.isecinc.pens.model.MUOM;
import com.jcraft.jsch.Logger;

/**
 * ModifierAttr Class
 * 
 * @author Atiz.b
 * @version $Id: ModifierAttr.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ModifierAttr extends I_PO implements Serializable {

	private static final long serialVersionUID = 2042513291162070112L;

	/**
	 * Default Constructor
	 */
	public ModifierAttr() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 */
	public ModifierAttr(ResultSet rst) throws Exception {
		setId(rst.getInt("MODIFIER_ATTR_ID"));
		setModifierId(rst.getInt("MODIFIER_ID"));
		setModifierLineId(rst.getInt("MODIFIER_LINE_ID"));
		setProductAttribute(ConvertNullUtil.convertToString(rst.getString("PRODUCT_ATTRIBUTE")).trim());
		setProductAttributeValue(ConvertNullUtil.convertToString(rst.getString("PRODUCT_ATTRIBUTE_VALUE")).trim());
		
		/*System.out.println("PRODUCT_UOM_ID:"+rst.getString("PRODUCT_UOM_ID"));
		System.out.println("VALUE_FROM:"+rst.getDouble("VALUE_FROM"));
		System.out.println("VALUE_TO:"+rst.getDouble("VALUE_TO"));*/
		
		if ( !Utils.isNull(rst.getString("PRODUCT_UOM_ID")).equals("")) {
			setProductUOM(new MUOM().find(rst.getString("PRODUCT_UOM_ID")));
		} else {
			setProductUOM(null);
		}
		setIsExclude(ConvertNullUtil.convertToString(rst.getString("ISEXCLUDE")).trim());
		setValueFrom(rst.getDouble("VALUE_FROM"));
		setValueTo(rst.getDouble("VALUE_TO"));
		setOperator(rst.getString("OPERATOR"));

		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {
		if (getProductAttribute().equalsIgnoreCase(BeanParameter.getModifierItemNumber())) {
			if( !Utils.isNull(getProductAttributeValue()).equals("")){
			  setProduct(new MProduct().find(getProductAttributeValue()));
			  if(getProduct() != null){
			     setAttributeValueLabel(getProduct().getCode() + "-" + getProduct().getName());
			  }
			}
		}
		if (getProductAttribute().equalsIgnoreCase(BeanParameter.getModifierItemCategory())) {
			if( !Utils.isNull(getProductAttributeValue()).equals("")){
			  setProductCategory(new MProductCategory().find(getProductAttributeValue()));
			  if(getProductCategory() != null){
			    setAttributeValueLabel(getProductCategory().getName());
			  }
			}
		}

	}

	/**
	 * To String
	 */
	public String toString() {
		return "";
	/*	return String.format("Modifier Attr[%s] Attr[%s] AttrV[%s] UOM[%s] From[%s] To[%s]", getId(),
				getProductAttribute(), getAttributeValueLabel(), getProductUOM().getName(), getValueFrom(),
				getValueTo());*/
	}

	/** MODIFIER_ATTR_ID */
	private int id;

	/** MODIFIER_ID */
	private int modifierId;

	/** MODIFIER_LINE_ID */
	private int modifierLineId;

	/** PRODUCT_ATTRIBUTE */
	private String productAttribute;

	/** PRODUCT_ATTRIBUTE_VALUE */
	private String productAttributeValue;

	/** PRODUCT_UOM */
	private UOM productUOM = new UOM();

	/** ISEXCLUDE */
	private String isExclude;

	/** VALUE_FROM */
	private double valueFrom;

	/** VALUE_TO */
	private double valueTo;

	/** OPERATOR */
	private String operator;

	/** PRODUCT CATEGORY */
	private ProductCategory productCategory = null;

	/** PRODUCT */
	private Product product = null;

	/** ATTR VALUE LABEL */
	private String attributeValueLabel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getModifierId() {
		return modifierId;
	}

	public void setModifierId(int modifierId) {
		this.modifierId = modifierId;
	}

	public int getModifierLineId() {
		return modifierLineId;
	}

	public void setModifierLineId(int modifierLineId) {
		this.modifierLineId = modifierLineId;
	}

	public String getProductAttribute() {
		return productAttribute;
	}

	public void setProductAttribute(String productAttribute) {
		this.productAttribute = productAttribute;
	}

	public String getProductAttributeValue() {
		return productAttributeValue;
	}

	public void setProductAttributeValue(String productAttributeValue) {
		this.productAttributeValue = productAttributeValue;
	}

	public UOM getProductUOM() {
		return productUOM;
	}

	public void setProductUOM(UOM productUOM) {
		this.productUOM = productUOM;
	}

	public double getValueFrom() {
		return valueFrom;
	}

	public void setValueFrom(double valueFrom) {
		this.valueFrom = valueFrom;
	}

	public double getValueTo() {
		return valueTo;
	}

	public void setValueTo(double valueTo) {
		this.valueTo = valueTo;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getIsExclude() {
		return isExclude;
	}

	public void setIsExclude(String isExclude) {
		this.isExclude = isExclude;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getAttributeValueLabel() {
		return attributeValueLabel;
	}

	public void setAttributeValueLabel(String attributeValueLabel) {
		this.attributeValueLabel = attributeValueLabel;
	}

}
