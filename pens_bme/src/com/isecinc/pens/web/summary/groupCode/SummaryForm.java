package com.isecinc.pens.web.summary.groupCode;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SummaryForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private Order order = new Order();

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
    
}
