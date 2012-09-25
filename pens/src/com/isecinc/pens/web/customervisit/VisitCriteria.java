package com.isecinc.pens.web.customervisit;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Visit;

/**
 * Visit Criteria
 * 
 * @author Aneak.t
 * @version $Id: VisitCriteria.java,v 1.0 22/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class VisitCriteria extends I_Criteria{

	private static final long serialVersionUID = -4923582412562573573L;

	private Visit visit = new Visit();
	
	private String from;

	public Visit getVisit() {
		return visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
}
