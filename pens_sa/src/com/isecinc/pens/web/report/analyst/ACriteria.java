package com.isecinc.pens.web.report.analyst;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.report.salesanalyst.SABean;
import com.isecinc.pens.web.report.analyst.bean.ABean;

/**
 WITTY
 * 
 */
public class ACriteria extends I_Criteria {

	private static final long serialVersionUID = -3460983827667841344L;
	
	private ABean salesBean = new ABean();
    
	public ABean getSalesBean() {
		return salesBean;
	}
	public void setSalesBean(ABean salesBean) {
		this.salesBean = salesBean;
	}
    
    
	

}
