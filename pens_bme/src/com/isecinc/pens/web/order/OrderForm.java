package com.isecinc.pens.web.order;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Order;

/**
 * Receipt Form
 * 
 * @author atiz.b
 * @version $Id: ReceiptForm.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderForm extends I_Form {

	private static final long serialVersionUID = 8932109820314224488L;

	private OrderCriteria criteria = new OrderCriteria();
    private String pageName;
    
	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public OrderCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(OrderCriteria criteria) {
		this.criteria = criteria;
	}

	
	public Order getOrder() {
		return criteria.getOrder();
	}

	public void setOrder(Order order) {
		criteria.setOrder(order);
	}
    
}
