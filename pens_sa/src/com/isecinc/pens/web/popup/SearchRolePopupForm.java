package com.isecinc.pens.web.popup;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesanalyst.SABean;

/**
WITTY
 * 
 */
public class SearchRolePopupForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;
	private SearchRolePopupCriteria criteria = new SearchRolePopupCriteria();
	public SABean getSalesBean() {
		return criteria.getSalesBean();
	}

	public void setSalesBean(SABean salesBean) {
		criteria.setSalesBean(salesBean);
	}

	public SearchRolePopupCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SearchRolePopupCriteria criteria) {
		this.criteria = criteria;
	}

	

}
