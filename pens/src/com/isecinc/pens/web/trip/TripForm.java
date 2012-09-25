package com.isecinc.pens.web.trip;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Trip;

/**
 * Trip Form
 * 
 * @author Witty.B
 * @version $Id: TripForm.java,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */

public class TripForm extends I_Form {

	private static final long serialVersionUID = 9066506758859129582L;

	private TripCriteria criteria = new TripCriteria();

	private Trip[] results = null;

	private String[] ids;
	
	private String[] customerId;
	
	public TripCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TripCriteria criteria) {
		this.criteria = criteria;
	}

	public Trip getTrip() {
		return criteria.getTrip();
	}

	public void setTrip(Trip trip) {
		criteria.setTrip(trip);
	}

	public Trip[] getResults() {
		return results;
	}

	public void setResults(Trip[] results) {
		this.results = results;
	}

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String[] getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String[] customerId) {
		this.customerId = customerId;
	}

}
