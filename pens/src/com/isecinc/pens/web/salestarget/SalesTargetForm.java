package com.isecinc.pens.web.salestarget;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.SalesTarget;
import com.isecinc.pens.bean.SalesTargetProduct;

/**
 * Sales Target Form
 * 
 * @author Aneak.t
 * @version $Id: SalesTargetForm.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class SalesTargetForm extends I_Form{

	private static final long serialVersionUID = -3201467769840397353L;

	private SalesTargetCriteria criteria = new SalesTargetCriteria();
	
	private SalesTarget[] results = null;
	
	private SalesTargetProduct[] salesTargetProduct = null;

	public SalesTargetCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SalesTargetCriteria criteria) {
		this.criteria = criteria;
	}

	public SalesTarget[] getResults() {
		return results;
	}

	public void setResults(SalesTarget[] results) {
		this.results = results;
	}

	public SalesTarget getSalesTarget() {
		return criteria.getSalesTarget();
	}

	public void setSalesTarget(SalesTarget salesTarget) {
		criteria.setSalesTarget(salesTarget);
	}

	public SalesTargetProduct[] getSalesTargetProduct() {
		return salesTargetProduct;
	}

	public void setSalesTargetProduct(SalesTargetProduct[] salesTargetProduct) {
		this.salesTargetProduct = salesTargetProduct;
	}

}

