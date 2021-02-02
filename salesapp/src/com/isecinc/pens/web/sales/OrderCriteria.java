package com.isecinc.pens.web.sales;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;

/**
 * Order Criteria
 * 
 * @author atiz.b
 * @version $Id: OrderCriteria.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderCriteria extends I_Criteria {

	private static final long serialVersionUID = 6220881218635143101L;

	private Order order = new Order();
	private Receipt autoReceipt = new Receipt();
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Receipt getAutoReceipt() {
		return autoReceipt;
	}

	public void setAutoReceipt(Receipt autoReceipt) {
		this.autoReceipt = autoReceipt;
	}

	
}
