package com.isecinc.pens.web.stock;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.MoveOrder;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.Stock;
import com.isecinc.pens.bean.Summary;

/**
 * Summary Criteria
 * 
 * @author atiz.b
 * @version $Id: ReceiptCriteria.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class StockCriteria extends I_Criteria {

	private static final long serialVersionUID = -189773759780203365L;
	
	private Stock bean = new Stock();
	private Customer customer = new Customer();

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	

	public Stock getBean() {
		return bean;
	}

	public void setBean(Stock bean) {
		this.bean = bean;
	}

	

}
