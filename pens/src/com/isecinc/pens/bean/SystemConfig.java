package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * Config
 * 
 * @author Atiz.b
 * @version $Id: SystemConfig.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SystemConfig implements Serializable {

	private static final long serialVersionUID = -6408213739000945447L;

	/**
	 * Constructor
	 */
	public SystemConfig() {}

	/**
	 * Constructor with Param
	 * 
	 * @param id
	 * @param name
	 * @param value
	 */
	public SystemConfig(int id, String name, String value) {
		this.id = id;
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public SystemConfig(ResultSet rst) throws Exception {
		setId(rst.getInt("SYSCONFIG_ID"));
		setName(rst.getString("NAME"));
		setValue(rst.getString("VALUE"));
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("System Config[%s]-[%s] [%s]", getId(), getName(), getValue());
	}

	/** ID */
	private int id;

	/** NAME */
	private String name;

	/** VALUE */
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
