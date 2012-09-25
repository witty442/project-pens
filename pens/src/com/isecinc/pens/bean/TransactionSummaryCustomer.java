package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Summary by Customer
 * 
 * @author atiz.b
 * @version $Id: TransactionSummaryCustomer.java,v 1.0 08/12/2010 15:52:00 atiz.b Exp $
 * 
 */
public class TransactionSummaryCustomer implements Serializable {

	private static final long serialVersionUID = -4815705032280126912L;

	private Customer customer = new Customer();

	private Address address = new Address();

	private List<TransactionSummary> summaries = new ArrayList<TransactionSummary>();

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<TransactionSummary> getSummaries() {
		return summaries;
	}

	public void setSummaries(List<TransactionSummary> summaries) {
		this.summaries = summaries;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
