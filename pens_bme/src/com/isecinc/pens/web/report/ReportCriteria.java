package com.isecinc.pens.web.report;

import com.isecinc.core.web.I_Criteria;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReportCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;
    private SABean salesBean = new SABean();
    
	public SABean getSalesBean() {
		return salesBean;
	}
	public void setSalesBean(SABean salesBean) {
		this.salesBean = salesBean;
	}
	
}
