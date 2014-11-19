package com.isecinc.pens.web.report;

import com.isecinc.core.web.I_Form;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReportForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private ReportCriteria criteria = new ReportCriteria();
	public SABean getSalesBean() {
		return criteria.getSalesBean();
	}

	public void setSalesBean(SABean salesBean) {
		criteria.setSalesBean(salesBean);
	}

	public ReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReportCriteria criteria) {
		this.criteria = criteria;
	}
    
}
