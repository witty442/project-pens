package com.isecinc.pens.bean;

import java.io.Serializable;

public class ModifierDiscount implements Serializable{

	private static final long serialVersionUID = -3173925729617127666L;

	private int id;
	private String applicationMethod;
	private int value;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getApplicationMethod() {
		return applicationMethod;
	}
	public void setApplicationMethod(String applicationMethod) {
		this.applicationMethod = applicationMethod;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}
