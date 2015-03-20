package com.isecinc.pens.web.receipt;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Receipt;

/**
 * Receipt Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private Receipt receipt = new Receipt();

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

}
