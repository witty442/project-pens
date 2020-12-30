package com.isecinc.pens.web.customernissin;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.CustomerNissin;

/**
 * CustomerNissin Criteria
 * 
 * @author Wittaya
 * @version 1.0 $
 * 
 */

public class CustomerNissinCriteria extends I_Criteria{

	private static final long serialVersionUID = 714056610377438393L;

	private CustomerNissin customer = new CustomerNissin();

	public CustomerNissin getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerNissin customer) {
		this.customer = customer;
	}
	
}
