package com.isecinc.pens.web.moveordersummary;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Summary;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MoveOrderSummaryForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private MoveOrderSummaryCriteria criteria = new MoveOrderSummaryCriteria();


	private List<MoveOrderSummary> results;


	public MoveOrderSummaryCriteria getCriteria() {
		return criteria;
	}


	public void setCriteria(MoveOrderSummaryCriteria criteria) {
		this.criteria = criteria;
	}


	public List<MoveOrderSummary> getResults() {
		return results;
	}


	public void setResults(List<MoveOrderSummary> results) {
		this.results = results;
	}


	public MoveOrderSummary getSummary() {
		return criteria.getSummary();
	}

	public void setSummary(MoveOrderSummary summary) {
		criteria.setSummary(summary);
	}

	
    
}
