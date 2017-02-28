package com.isecinc.pens.web.salesanalyst;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesanalyst.SABean;

/**
WITTY
 * 
 */
public class SAReportForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;
	private SACriteria criteria = new SACriteria();
	public SABean getSalesBean() {
		return criteria.getSalesBean();
	}

	public void setSalesBean(SABean salesBean) {
		criteria.setSalesBean(salesBean);
	}

	public SACriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SACriteria criteria) {
		this.criteria = criteria;
	}

}
