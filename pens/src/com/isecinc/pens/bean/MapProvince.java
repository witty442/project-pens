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
public class MapProvince implements Serializable {

	private static final long serialVersionUID = -8945104487055563962L;

	/**
	 * Default Constructor
	 */
	public MapProvince() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public MapProvince(ResultSet rst) throws Exception {
		setProvinceId(rst.getInt("PROVINCE_ID"));
		setReferenceId(rst.getInt("REFERENCE_ID"));
	}

	private int provinceId;
	private int referenceId;

	public int getProvinceId() {
		return provinceId;
	}
	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}
	public int getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}

}
