package com.isecinc.pens.web.popup;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.report.salesanalyst.SABean;

/**
 WITTY
 * 
 */
public class SearchRolePopupCriteria extends I_Criteria {

	private static final long serialVersionUID = -3460983827667841344L;
	
	private SABean salesBean = new SABean();
    
	public SABean getSalesBean() {
		return salesBean;
	}
	public void setSalesBean(SABean salesBean) {
		this.salesBean = salesBean;
	}
    
    
	

}
