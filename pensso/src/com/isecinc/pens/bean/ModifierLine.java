package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MModifierAttr;
import com.isecinc.pens.model.MModifierLine;
import com.isecinc.pens.model.MUOM;

/**
 * Modifier Line Class
 * 
 * @author Atiz.b
 * @version $Id: ModifierLine.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ModifierLine extends I_PO implements Serializable {

	private static final long serialVersionUID = 2948789000965885416L;

	/**
	 * Default Constructor
	 */
	public ModifierLine() {}

	/**
	 * Default Constructor with rst
	 */
	public ModifierLine(ResultSet rst) throws Exception {
		setId(rst.getInt("MODIFIER_LINE_ID"));
		setModifierId(rst.getInt("MODIFIER_ID"));
		setType(ConvertNullUtil.convertToString(rst.getString("TYPE")).trim());
		setStartDate("");
		if (rst.getTimestamp("START_DATE") != null) {
			setStartDate(DateToolsUtil.convertToString(rst.getTimestamp("START_DATE")));
		}
		setEndDate("");
		if (rst.getTimestamp("END_DATE") != null) {
			setEndDate(DateToolsUtil.convertToString(rst.getTimestamp("END_DATE")));
		}
		setLevels(rst.getString("LEVELS").trim());
		setPrecedence(rst.getInt("PRECEDENCE"));
		setVolumeType(ConvertNullUtil.convertToString(rst.getString("VOLUME_TYPE")).trim());
		setBreakType(ConvertNullUtil.convertToString(rst.getString("BREAK_TYPE")).trim());
		setApplicationMethod(ConvertNullUtil.convertToString(rst.getString("APPLICATION_METHOD")).trim());
		setValues(rst.getDouble("VALUE"));
		setPricelistId(rst.getInt("PRICELIST_ID"));
		setBenefitQty(rst.getDouble("BENEFIT_QTY"));
		if (rst.getString("BENEFIT_UOM_ID") != null) {
			setBenefitUOM(new MUOM().find(rst.getString("BENEFIT_UOM_ID")));
		}
		setIsActive(rst.getString("ISACTIVE").trim());
		setIsAutomatic(rst.getString("ISAUTOMATIC").trim());
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());

		// Get Attribute
		attr = new MModifierAttr().getOfLine(getModifierId(),getId());

		// Get Relation Modifier
		relatedModifier = new MModifierLine().getRelateModifier(getId());
	}

	protected void setDisplayLabel() throws Exception {

	}

	/**
	 * To String
	 */
	public String toString() {
		return "Modifier Line[" + getId() + "] Level[" + getLevels() + "] Type[" + getType() + "] Product Attr["
				+ attr.getProductAttribute() + "] Product Attr Value[" + attr.getProductAttributeValue()
				+ "], Precedence[" + getPrecedence() + "] Volume Type[" + getVolumeType() + "] Operator["
				+ attr.getOperator() + "] UOM[" + attr.getProductUOM().getName() + "] From[" + attr.getValueFrom()
				+ "] To[" + attr.getValueTo() + "]";
	}

	/** ID */
	private int id;

	/** MODIFIER_ID */
	private int modifierId;

	/** TYPE */
	private String type;

	/** START_DATE */
	private String startDate;

	/** END_DATE */
	private String endDate;

	/** LEVEL */
	private String levels;

	/** PRECEDENCE */
	private int precedence;

	/** VOLUME_TYPE */
	private String volumeType;

	/** BREAK_TYPE */
	private String breakType;

	/** APPLICATION_METHOD */
	private String applicationMethod;

	/** VALUE */
	private double values;

	/** PRICELIST_ID */
	private int pricelistId;

	/** BENEFIT_QTY */
	private double benefitQty;

	/** BENEFIT_UOM */
	private UOM benefitUOM = new UOM();

	/** ACTIVE */
	private String isActive;

	/** AUTOMATIC */
	private String isAutomatic;

	/** RELATE ATTR */
	private ModifierAttr attr = new ModifierAttr();

	/** RELATE MODIFIER */
	private List<ModifierLine> relatedModifier = null;

	/** DESCRIPTION */
	private String description;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getLevels() {
		return levels;
	}

	public void setLevels(String levels) {
		this.levels = levels;
	}

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}

	public String getVolumeType() {
		return volumeType;
	}

	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}

	public String getBreakType() {
		return breakType;
	}

	public void setBreakType(String breakType) {
		this.breakType = breakType;
	}

	public String getApplicationMethod() {
		return applicationMethod;
	}

	public void setApplicationMethod(String applicationMethod) {
		this.applicationMethod = applicationMethod;
	}

	public double getValues() {
		return values;
	}

	public void setValues(double values) {
		this.values = values;
	}

	public int getPricelistId() {
		return pricelistId;
	}

	public void setPricelistId(int pricelistId) {
		this.pricelistId = pricelistId;
	}

	public double getBenefitQty() {
		return benefitQty;
	}

	public void setBenefitQty(double benefitQty) {
		this.benefitQty = benefitQty;
	}

	public UOM getBenefitUOM() {
		return benefitUOM;
	}

	public void setBenefitUOM(UOM benefitUOM) {
		this.benefitUOM = benefitUOM;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getIsAutomatic() {
		return isAutomatic;
	}

	public void setIsAutomatic(String isAutomatic) {
		this.isAutomatic = isAutomatic;
	}

	public ModifierAttr getAttr() {
		return attr;
	}

	public void setAttr(ModifierAttr attr) {
		this.attr = attr;
	}

	public List<ModifierLine> getRelatedModifier() {
		return relatedModifier;
	}

	public void setRelatedModifier(List<ModifierLine> relatedModifier) {
		this.relatedModifier = relatedModifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
