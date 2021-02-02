package com.isecinc.pens.web.salestarget;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.SalesTarget;

/**
 * Sales Target Criteria
 * 
 * @author Aneak.t
 * @version $Id: SalesTargetCriteria.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class SalesTargetCriteria extends I_Criteria{

	private static final long serialVersionUID = 1147813316260424486L;
	
	private SalesTarget salesTarget = new SalesTarget();

	public SalesTarget getSalesTarget() {
		return salesTarget;
	}

	public void setSalesTarget(SalesTarget salesTarget) {
		this.salesTarget = salesTarget;
	}
	
}
