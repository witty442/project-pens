package com.isecinc.pens.web.temp;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Summary;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class TempForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private TempCriteria criteria = new TempCriteria();


	private List<Summary> results;


	public TempCriteria getCriteria() {
		return criteria;
	}


	public void setCriteria(TempCriteria criteria) {
		this.criteria = criteria;
	}


	public List<Summary> getResults() {
		return results;
	}


	public void setResults(List<Summary> results) {
		this.results = results;
	}


	public Summary getSummary() {
		return criteria.getSummary();
	}

	public void setSummary(Summary summary) {
		criteria.setSummary(summary);
	}

	
    
}
