package com.isecinc.pens.bean;

import java.sql.ResultSet;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.model.I_PO;

/**
 * Qualifier Class
 * 
 * @author Atiz.b
 * @version $Id: Qualifier.java,v 1.0 21/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Qualifier extends I_PO {

	private static final long serialVersionUID = -6754339414388974940L;

	public Qualifier() {}

	public Qualifier(ResultSet rst) throws Exception {
		setId(rst.getInt("QUALIFIER_ID"));
		setQualifierContext(rst.getString("QUALIFIER_CONTEXT"));
		setQualifierType(rst.getString("QUALIFIER_TYPE"));
		setQualifierValue(rst.getString("QUALIFIER_VALUE"));
		setOperator(rst.getString("OPERATOR"));
		setIsExclude(rst.getString("ISEXCLUDE"));
		setIsActive(rst.getString("ISACTIVE"));
		setStartDate("");
		if (rst.getTimestamp("START_DATE") != null) {
			setStartDate(DateToolsUtil.convertToString(rst.getTimestamp("START_DATE")));
		}
		setEndDate("");
		if (rst.getTimestamp("END_DATE") != null) {
			setEndDate(DateToolsUtil.convertToString(rst.getTimestamp("END_DATE")));
		}
		setValueFrom(ConvertNullUtil.convertToString(rst.getString("VALUE_FROM")));
		setValueTo(ConvertNullUtil.convertToString(rst.getString("VALUE_TO")));

		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {

	}

	public String toString() {
		return String.format("Qualifier[%s] [%s-%s] %s", getId(), getQualifierContext(), getQualifierType(),
				getQualifierValue());
	}

	/** QUALIFIER_ID */
	private int id;

	/** QUALIFIER_CONTEXT */
	private String qualifierContext;

	/** QUALIFIER_TYPE */
	private String qualifierType;

	/** QUALIFIER_VALUE */
	private String qualifierValue;

	/** OPERATOR */
	private String operator;

	/** ISEXCLUDE */
	private String isExclude;

	/** START_DATE */
	private String startDate;

	/** END_DATE */
	private String endDate;

	/** ISACTIVE */
	private String isActive;

	/** VALUE_FROM */
	private String valueFrom;

	/** VALUE_TO */
	private String valueTo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQualifierContext() {
		return qualifierContext;
	}

	public void setQualifierContext(String qualifierContext) {
		this.qualifierContext = qualifierContext;
	}

	public String getQualifierType() {
		return qualifierType;
	}

	public void setQualifierType(String qualifierType) {
		this.qualifierType = qualifierType;
	}

	public String getQualifierValue() {
		return qualifierValue;
	}

	public void setQualifierValue(String qualifierValue) {
		this.qualifierValue = qualifierValue;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getValueFrom() {
		return valueFrom;
	}

	public void setValueFrom(String valueFrom) {
		this.valueFrom = valueFrom;
	}

	public String getValueTo() {
		return valueTo;
	}

	public void setValueTo(String valueTo) {
		this.valueTo = valueTo;
	}

}
