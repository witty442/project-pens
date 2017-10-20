package com.isecinc.pens.web.clearinvoice;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ClearInvoiceCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private ClearInvoice bean = new ClearInvoice();

	public ClearInvoice getBean() {
		return bean;
	}

	public void setBean(ClearInvoice bean) {
		this.bean = bean;
	}

	
	
}
