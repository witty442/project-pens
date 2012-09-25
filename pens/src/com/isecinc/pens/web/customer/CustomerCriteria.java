package com.isecinc.pens.web.customer;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Customer;

/**
 * Customer Criteria
 * 
 * @author Aneak.t
 * @version $Id: CustomerCriteria.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class CustomerCriteria extends I_Criteria{

	private static final long serialVersionUID = 714056610377438393L;

	private Customer customer = new Customer();

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}
