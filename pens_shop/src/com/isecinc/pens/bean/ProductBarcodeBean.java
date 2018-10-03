package com.isecinc.pens.bean;

import java.io.Serializable;

import com.isecinc.pens.web.sales.bean.ProductCatalog;

public class ProductBarcodeBean implements Serializable{
	
  private static final long serialVersionUID = -5062521256898521767L;
  private String barcode;
  private String productCode;
  private String materialMaster;
  private String productName;
  private String json;
  private ProductCatalog productCatalog;
   
  
	public ProductCatalog getProductCatalog() {
		return productCatalog;
	}
	public void setProductCatalog(ProductCatalog productCatalog) {
		this.productCatalog = productCatalog;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getMaterialMaster() {
		return materialMaster;
	}
	public void setMaterialMaster(String materialMaster) {
		this.materialMaster = materialMaster;
	}
  
  
}
