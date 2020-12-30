package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.pens.util.ConvertNullUtil;

/**
 * District Class
 * 
 * @author atiz.b
 * @version $Id: District.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class District implements Serializable {

	private static final long serialVersionUID = -6409189545007930555L;

	/**
	 * Default Constructor
	 */
	public District() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public District(ResultSet rst) throws Exception {
		setId(rst.getInt("DISTRICT_ID"));
		setName(rst.getString("NAME").trim());
		setProvinceId(rst.getInt("PROVINCE_ID"));
		setCode(ConvertNullUtil.convertToString(rst.getString("CODE")));
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("District[%s] [%s]", getId(), getName());
	}

	/** ID */
	private int id;

	/** NAME */
	private String name;

	/** PROVINCE ID */
	private int provinceId;

	/** CODE */
	private String code;

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

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
