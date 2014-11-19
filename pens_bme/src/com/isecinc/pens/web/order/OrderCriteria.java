package com.isecinc.pens.web.order;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Order;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private Order order = new Order();

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}
