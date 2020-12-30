package com.isecinc.pens.web.ordernissin;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderNissin;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderNissinCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;

	private OrderNissin bean = new OrderNissin();

	public OrderNissin getBean() {
		return bean;
	}

	public void setBean(OrderNissin bean) {
		this.bean = bean;
	}

    
}
