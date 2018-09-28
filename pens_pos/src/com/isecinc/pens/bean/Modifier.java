package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;

/**
 * Modifier Class
 * 
 * @author Atiz.b
 * @version $Id: Modifier.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Modifier extends I_PO implements Serializable {

	private static final long serialVersionUID = -3043278483712732219L;

	/**
	 * Default Constructor
	 */
	public Modifier() {}

	/**
	 * Default Constructor with rst
	 */
	public Modifier(ResultSet rst) throws Exception {
		setId(rst.getInt("MODIFIER_ID"));
		setType(rst.getString("TYPE").trim());
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setDescription(ConvertNullUtil.convertToString(rst.getString("DESCRIPTION")).trim());
		setStartDate("");
		if (rst.getTimestamp("START_DATE") != null) {
			setStartDate(DateToolsUtil.convertToString(rst.getTimestamp("START_DATE")));
		}
		setEndDate("");
		if (rst.getTimestamp("END_DATE") != null) {
			setEndDate(DateToolsUtil.convertToString(rst.getTimestamp("END_DATE")));
		}
		setIsActive(rst.getString("ISACTIVE").trim());
		setIsAutomatic(rst.getString("ISAUTOMATIC").trim());

		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {
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
		return "Modifier [" + getId() + "] Type[" + getType() + "] Code[" + getCode() + "] Name[" + getName()
				+ "] Desc[" + getDescription() + "], Start[" + getStartDate() + "] End[" + getEndDate() + "] Active["
				+ isActive + "]";
	}

	/** ID */
	private int id;

	/** TYPE */
	private String type;

	/** CODE */
	private String code;

	/** NAME */
	private String name;

	/** DESCRIPTION */
	private String description;

	/** START DATE */
	private String startDate;

	/** END DATE */
	private String endDate;

	/** ACTIVE */
	private String isActive;

	/** AUTOMATIC */
	private String isAutomatic;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getIsAutomatic() {
		return isAutomatic;
	}

	public void setIsAutomatic(String isAutomatic) {
		this.isAutomatic = isAutomatic;
	}

}
