package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * Province Class
 * 
 * @author atiz.b
 * @version $Id: Province.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Province implements Serializable {

	private static final long serialVersionUID = -8945104487055563962L;

	/**
	 * Default Constructor
	 */
	public Province() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public Province(ResultSet rst) throws Exception {
		setId(rst.getInt("PROVINCE_ID"));
		setName(rst.getString("NAME").trim());
	}

	/** ID */
	private int id;

	/** NAME */
	private String name;

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

}
