package com.pens.rest;


public class Customer {

String customerCode;
String customerName;

public Customer(){
}
   
public String getCustomerName() {
	return customerName;
}

public void setCustomerName(String customerName) {
	this.customerName = customerName;
}

public String getCustomerCode() {
	return customerCode;
}

public void setCustomerCode(String customerCode) {
	this.customerCode = customerCode;
}
@Override
public String toString() {
    return new StringBuffer(" Customer Code : ").append(this.customerCode)
            .append(" Customer Name : ").append(this.customerName).toString();
}

}
