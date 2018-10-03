package com.isecinc.pens.bean;

import java.sql.ResultSet;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MDistrict;

/**
 * DeliveryRoute
 * 
 * @author atiz.b
 * @version $Id: DeliveryRoute.ava,v 1.0 16/11/2010 00:00:00 atiz.b Exp $
 * 
 */
public class DeliveryRoute extends I_PO {

	private static final long serialVersionUID = -8420419749126331083L;

	/**
	 * Default Constructor
	 */
	public DeliveryRoute() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public DeliveryRoute(ResultSet rst) throws Exception {
		setId(rst.getInt("DELIVERY_ROUTE_ID"));
		setDays(rst.getString("DAYS"));
		setDistrictId(rst.getInt("DISTRICT_ID"));
		setDistrict(new MDistrict().find(rst.getString("DISTRICT_ID")));

		setDisplayLabel();
	}

	protected void setDisplayLabel() throws Exception {

	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("DD Route[%s] [%s-%s]", getId(), getDays(), getDistrict());
	}

	/** DELIVERY_ROUTE_ID */
	private int id;

	/** DAYS */
	private String days;

	/** DISTRICT_ID */
	private int districtId;

	/** DISTRICT */
	private District district = new District();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

}
