package com.isecinc.pens.web.receiptAll;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.OrderLine;

/**
 * Order Criteria
 * 
 * @author atiz.b
 * @version $Id: OrderCriteria.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptAllCriteria extends I_Criteria {

	private static final long serialVersionUID = 6220881218635143101L;

	private OrderLine orderLine = new OrderLine();

	public OrderLine getOrderLine() {
		return orderLine;
	}

	public void setOrderLine(OrderLine orderLine) {
		this.orderLine = orderLine;
	}


}
