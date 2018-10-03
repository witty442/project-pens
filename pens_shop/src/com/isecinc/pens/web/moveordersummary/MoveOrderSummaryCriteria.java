package com.isecinc.pens.web.moveordersummary;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MoveOrderSummary;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MoveOrderSummaryCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private MoveOrderSummary summary = new MoveOrderSummary();

	public MoveOrderSummary getSummary() {
		return summary;
	}

	public void setSummary(MoveOrderSummary summary) {
		this.summary = summary;
	}

	
}
