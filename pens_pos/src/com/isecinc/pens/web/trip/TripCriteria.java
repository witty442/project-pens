package com.isecinc.pens.web.trip;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Trip;

/**
 * Trip Criteria
 * 
 * @author Witty.B
 * @version $Id: TripCriteria.java,v 1.0 19/10/2010 00:00:00 Witty.B Exp $
 * 
 */


public class TripCriteria extends I_Criteria{

	private static final long serialVersionUID = 714056610377438393L;

	private Trip trip = new Trip();

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	

	
	
}
