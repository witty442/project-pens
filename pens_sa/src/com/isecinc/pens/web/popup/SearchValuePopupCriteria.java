package com.isecinc.pens.web.popup;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.web.report.analyst.bean.ABean;
import com.isecinc.pens.web.report.analyst.bean.ConditionFilterBean;

/**
 WITTY
 * 
 */
public class SearchValuePopupCriteria extends I_Criteria {

	private static final long serialVersionUID = -3460983827667841344L;
	
	private ABean salesBean = new ABean();
	private ConditionFilterBean filterBean = new ConditionFilterBean();
	
	public ABean getSalesBean() {
		return salesBean;
	}
	public void setSalesBean(ABean salesBean) {
		this.salesBean = salesBean;
	}
	public ConditionFilterBean getFilterBean() {
		return filterBean;
	}
	public void setFilterBean(ConditionFilterBean filterBean) {
		this.filterBean = filterBean;
	}
    
    
	

}
