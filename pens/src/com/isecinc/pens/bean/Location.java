package com.isecinc.pens.bean;

import java.io.Serializable;

public class Location implements Serializable{

	private static final long serialVersionUID = 5636465513521936555L;
	
	private int id;
	private Customer customer;
	private Address address;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
}
