package com.isecinc.pens.web.popup;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesanalyst.SABean;

/**
WITTY
 * 
 */
public class SearchValuePopupForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;
	private SearchValuePopupCriteria criteria = new SearchValuePopupCriteria();
	public SABean getSalesBean() {
		return criteria.getSalesBean();
	}

	public void setSalesBean(SABean salesBean) {
		criteria.setSalesBean(salesBean);
	}

	public SearchValuePopupCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SearchValuePopupCriteria criteria) {
		this.criteria = criteria;
	}
	
	
	

}
