package com.isecinc.pens.web.popup;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesanalyst.ConditionFilterBean;
import com.isecinc.pens.report.salesanalyst.SABean;

/**
WITTY
 * 
 */
public class SearchValuePopupForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;
	private SearchValuePopupCriteria criteria = new SearchValuePopupCriteria();
    private String navigation;
    private String curNavigation;
    
    
	public String getCurNavigation() {
		return curNavigation;
	}

	public void setCurNavigation(String curNavigation) {
		this.curNavigation = curNavigation;
	}

	public String getNavigation() {
		return navigation;
	}

	public void setNavigation(String navigation) {
		this.navigation = navigation;
	}

	public SABean getSalesBean() {
		return criteria.getSalesBean();
	}

	public void setSalesBean(SABean salesBean) {
		criteria.setSalesBean(salesBean);
	}
	
	public ConditionFilterBean getFilterBean() {
		return criteria.getFilterBean();
	}

	public void setFilterBean(ConditionFilterBean filterBean) {
		criteria.setFilterBean(filterBean);
	}


	public SearchValuePopupCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SearchValuePopupCriteria criteria) {
		this.criteria = criteria;
	}

}
