package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MUser;

/**
 * Trip
 * 
 * @author Witty.B
 * @version $Id: Trip.java,v 1.0 19/10/2010 15:52:00 Witty.B Exp $
 * 
 *          Modifier : A-neak.t 21/10/2010 Edit variable name
 * 
 */
public class Trip extends I_PO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5689787843380138413L;

	public Trip() {

	}

	public Trip(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("TRIP_ID"));
		setYear(rst.getString("YEAR").trim());
		setMonth(rst.getString("MONTH").trim());
		setDay(rst.getString("DAY").trim());
		setCustomer(new MCustomer().find(String.valueOf(rst.getInt("CUSTOMER_ID"))));
		setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setLineNo(ConvertNullUtil.convertToString(rst.getString("LINE_NO")).trim());
		setTripDateFrom(rst.getString("DAY").trim() + "/" + rst.getString("MONTH").trim() + "/"
				+ rst.getString("YEAR").trim());
		setCreated(rst.getTimestamp("CREATED"));
		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
		setUpdated(rst.getTimestamp("UPDATED"));
		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));
	}

	protected void setDisplayLabel() throws Exception {

	}

	private int Id;
	private String year;
	private String month;
	private String day;
	private Customer customer = new Customer();
	private User user = new User();
	private String lineNo;
	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();

	private String tripDateFrom;
	private String tripDateTo;
	private String tripNo;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	public String getTripDateFrom() {
		return tripDateFrom;
	}

	public void setTripDateFrom(String tripDateFrom) {
		this.tripDateFrom = tripDateFrom;
	}

	public String getTripDateTo() {
		return tripDateTo;
	}

	public void setTripDateTo(String tripDateTo) {
		this.tripDateTo = tripDateTo;
	}

	public String getTripNo() {
		return tripNo;
	}

	public void setTripNo(String tripNo) {
		this.tripNo = tripNo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}
}
