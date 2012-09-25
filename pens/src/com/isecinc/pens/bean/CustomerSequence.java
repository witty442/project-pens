package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;

/**
 * CustomerSequence Class
 * 
 * @author Atiz.b
 * @version $Id: CustomerSequence.java,v 1.0 22/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class CustomerSequence extends I_PO {

	private static final long serialVersionUID = -4289768952955259830L;

	public CustomerSequence() {}

	public CustomerSequence(ResultSet rst) throws Exception {
		setId(rst.getInt("CUSTOMER_SEQUENCE_ID"));
		setTerritory(rst.getString("TERRITORY"));
		setProvince(rst.getString("PROVINCE"));
		setDistrict(rst.getString("DISTRICT"));
		setCurrentNext(rst.getInt("CURRENT_NEXT"));
	}

	protected void setDisplayLabel() throws Exception {}

	public String toString() {
		return String.format("Customer Seq[%s] %s %s %s next:%s", getId(), getTerritory(), getProvince(),
				getDistrict(), getCurrentNext());
	}

	/** CUSTOMER_SEQUENCE_ID */
	private int id;

	/** TERRITORY */
	private String territory;

	/** PROVINCE */
	private String province;

	/** DISTRICT */
	private String district;

	/** CURRENT_NEXT */
	private int currentNext;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public int getCurrentNext() {
		return currentNext;
	}

	public void setCurrentNext(int currentNext) {
		this.currentNext = currentNext;
	}

}
