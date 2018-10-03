package com.isecinc.pens.bean;

import java.io.Serializable;

public class AdjustmentDetail implements Serializable{

	private static final long serialVersionUID = 1194890489823214927L;

	private int id;
	private ModifierLine summary;
	private int valueFrom;
	private int valueTo;
	private String applicationMethod;
	private int value;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ModifierLine getSummary() {
		return summary;
	}
	public void setSummary(ModifierLine summary) {
		this.summary = summary;
	}
	public int getValueFrom() {
		return valueFrom;
	}
	public void setValueFrom(int valueFrom) {
		this.valueFrom = valueFrom;
	}
	public int getValueTo() {
		return valueTo;
	}
	public void setValueTo(int valueTo) {
		this.valueTo = valueTo;
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
