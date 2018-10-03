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
public class DeliveryGroup implements Serializable {

	private static final long serialVersionUID = -8945104487055563962L;

	/**
	 * Default Constructor
	 */
	public DeliveryGroup() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public DeliveryGroup(ResultSet rst) throws Exception {
		setDistrictId(rst.getInt("DISTRICT_ID"));
		setReferenceId(rst.getInt("REFERENCE_ID"));
	}

	private int districtId;
	private int referenceId;

	public int getDistrictId() {
		return districtId;
	}
	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}
	public int getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}

}
