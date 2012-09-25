package com.isecinc.pens.web.report.salesanalyst;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salesorder.OrderThreeMonths;
import com.isecinc.pens.web.user.UserCriteria;

/**
WITTY
 * 
 */
public class SalesAnalystReportForm extends I_Form {

	private static final long serialVersionUID = 3636927584638032550L;
	private SalesAnalystCriteria criteria = new SalesAnalystCriteria();
	
	

//	public SalesAnalystBean getSalesBean() {
//		return criteria.getSalesBean();
//	}
//
//	public void setSalesBean(SalesAnalystBean salesBean) {
//		criteria.setSalesBean(salesBean);
//	}

	public SalesAnalystCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SalesAnalystCriteria criteria) {
		this.criteria = criteria;
	}
	
	
	

}
