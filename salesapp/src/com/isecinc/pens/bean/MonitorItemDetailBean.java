package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author WITTY
 *
 */



public class MonitorItemDetailBean implements Serializable{
   private   BigDecimal id;
   private   BigDecimal monitorItemId;
   private   String customerCode;
   private   String customerName;
   private   String code ;
   private   String type;
   private   double amount;
   private   String amountStr;
   private   String lineCode ;
   private   String lineType;
   private   double lineAmount;
   
   

public String getAmountStr() {
	return amountStr;
}
public void setAmountStr(String amountStr) {
	this.amountStr = amountStr;
}
public String getLineCode() {
	return lineCode;
}
public void setLineCode(String lineCode) {
	this.lineCode = lineCode;
}
public String getLineType() {
	return lineType;
}
public void setLineType(String lineType) {
	this.lineType = lineType;
}
public double getLineAmount() {
	return lineAmount;
}
public void setLineAmount(double lineAmount) {
	this.lineAmount = lineAmount;
}
public BigDecimal getId() {
	return id;
}
public void setId(BigDecimal id) {
	this.id = id;
}
public BigDecimal getMonitorItemId() {
	return monitorItemId;
}
public void setMonitorItemId(BigDecimal monitorItemId) {
	this.monitorItemId = monitorItemId;
}
public String getCustomerCode() {
	return customerCode;
}
public void setCustomerCode(String customerCode) {
	this.customerCode = customerCode;
}
public String getCustomerName() {
	return customerName;
}
public void setCustomerName(String customerName) {
	this.customerName = customerName;
}
public String getCode() {
	return code;
}
public void setCode(String code) {
	this.code = code;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public double getAmount() {
	return amount;
}
public void setAmount(double amount) {
	this.amount = amount;
}
 
     
}
