package com.isecinc.pens.web.customervisit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Visit;
import com.isecinc.pens.bean.VisitLine;

/**
 * Visit Form
 * 
 * @author Aneak.t
 * @version $Id: VisitForm.java,v 1.0 22/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class VisitForm extends I_Form{

	private static final long serialVersionUID = -2171520706050115028L;

	private VisitCriteria criteria = new VisitCriteria();
	
	private Visit[] results = null;
	
	List<VisitLine> lines = null;
	
	private String deletedId = "";
	
	@SuppressWarnings("unchecked")
	public VisitForm(){
		Factory factory = new Factory() {
			public Object create() {
				return new VisitLine();
			}
		};
		lines = LazyList.decorate(new ArrayList<VisitLine>(), factory);
	}

	public VisitCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(VisitCriteria criteria) {
		this.criteria = criteria;
	}

	public Visit[] getResults() {
		return results;
	}

	public void setResults(Visit[] results) {
		this.results = results;
	}

	public Visit getVisit() {
		return criteria.getVisit();
	}

	public void setVisit(Visit visit) {
		criteria.setVisit(visit);
	}

	public List<VisitLine> getLines() {
		return lines;
	}

	public void setLines(List<VisitLine> lines) {
		this.lines = lines;
	}

	public String getDeletedId() {
		return deletedId;
	}

	public void setDeletedId(String deletedId) {
		this.deletedId = deletedId;
	}

}
