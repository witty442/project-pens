package com.isecinc.pens.dao;

public class StockBeanUtils {
	double priAllQty;
	double priQty;
	double subQty;
	private double remainAmount;
	
	
	public double getRemainAmount() {
		return remainAmount;
	}
	public void setRemainAmount(double remainAmount) {
		this.remainAmount = remainAmount;
	}
	public double getPriAllQty() {
		return priAllQty;
	}
	public void setPriAllQty(double priAllQty) {
		this.priAllQty = priAllQty;
	}
	public double getPriQty() {
		return priQty;
	}
	public void setPriQty(double priQty) {
		this.priQty = priQty;
	}
	public double getSubQty() {
		return subQty;
	}
	public void setSubQty(double subQty) {
		this.subQty = subQty;
	}
}
