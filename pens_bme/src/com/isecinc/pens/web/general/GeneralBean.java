package com.isecinc.pens.web.general;

import java.io.Serializable;

public class GeneralBean implements Serializable{

private static final long serialVersionUID = 5381744265476999095L;
private String productCode;
private String barcode;
 
public String getProductCode() {
	return productCode;
}
public void setProductCode(String productCode) {
	this.productCode = productCode;
}
public String getBarcode() {
	return barcode;
}
public void setBarcode(String barcode) {
	this.barcode = barcode;
}
 
 
}
