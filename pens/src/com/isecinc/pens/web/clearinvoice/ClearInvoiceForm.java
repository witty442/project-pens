package com.isecinc.pens.web.clearinvoice;

import java.util.List;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Summary;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ClearInvoiceForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private ClearInvoiceCriteria criteria = new ClearInvoiceCriteria();


	private List<ClearInvoice> results;


	public ClearInvoiceCriteria getCriteria() {
		return criteria;
	}


	public void setCriteria(ClearInvoiceCriteria criteria) {
		this.criteria = criteria;
	}


	public List<ClearInvoice> getResults() {
		return results;
	}


	public void setResults(List<ClearInvoice> results) {
		this.results = results;
	}


	public ClearInvoice getBean() {
		return criteria.getBean();
	}

	public void setBean(ClearInvoice summary) {
		criteria.setBean(summary);
	}

	
    
}
