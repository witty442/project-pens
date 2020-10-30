package com.isecinc.core.bean;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * References
 * 
 * @author Atiz.b
 * @version $Id: References.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public class References implements Serializable {

	private static final long serialVersionUID = 4133660991116726735L;
	public References() {
	}
	/**
	 * Constructor
	 * 
	 * @param code
	 * @param key
	 * @param name
	 */
	public References(String code, String key, String name) {
		this.code = code;
		this.name = name;
		this.key = key;
	}

	/**
	 * Constructor
	 * 
	 * @param key
	 * @param name
	 */
	public References(String key, String name) {
		this.name = name;
		this.key = key;
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public References(ResultSet rst) throws Exception {
		setCode(rst.getString("CODE"));
		setKey(rst.getString("VALUE"));
		setName(rst.getString("NAME"));
		setDesc(rst.getString("DESCRIPTION"));
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("References[%s]-[%s] [%s]", getCode(), getKey(), getName());
	}

	/** CODE */
	private String code;

	/** KEY VALUE */
	private String key;

	/** NAME */
	private String name;

	/** DESC */
	private String desc;
	
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
