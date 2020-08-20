package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class StockOnhandBean implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5066534727878051512L;
	private String brand;
	private String brandName;
	private String subBrand;
	private String subBrandName;
	private String productCode;
	private String productName;
	private String uom1;
	private String uom2;
	private String priQtyOnhand;
	private String secQtyOnhand;
	private String priQtyReserve;
	private String secQtyReserve;
	private String priQtyAvailable;
	private String secQtyAvailable;
	private List<StockOnhandBean> itemsList;
	private StringBuffer dataStrBuffer;
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getSubBrand() {
		return subBrand;
	}
	public void setSubBrand(String subBrand) {
		this.subBrand = subBrand;
	}
	public String getSubBrandName() {
		return subBrandName;
	}
	public void setSubBrandName(String subBrandName) {
		this.subBrandName = subBrandName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getUom1() {
		return uom1;
	}
	public void setUom1(String uom1) {
		this.uom1 = uom1;
	}
	public String getUom2() {
		return uom2;
	}
	public void setUom2(String uom2) {
		this.uom2 = uom2;
	}
	public String getPriQtyOnhand() {
		return priQtyOnhand;
	}
	public void setPriQtyOnhand(String priQtyOnhand) {
		this.priQtyOnhand = priQtyOnhand;
	}
	
	public String getPriQtyReserve() {
		return priQtyReserve;
	}
	public void setPriQtyReserve(String priQtyReserve) {
		this.priQtyReserve = priQtyReserve;
	}
	
	public String getPriQtyAvailable() {
		return priQtyAvailable;
	}
	public void setPriQtyAvailable(String priQtyAvailable) {
		this.priQtyAvailable = priQtyAvailable;
	}
	
	public String getSecQtyOnhand() {
		return secQtyOnhand;
	}
	public void setSecQtyOnhand(String secQtyOnhand) {
		this.secQtyOnhand = secQtyOnhand;
	}
	public String getSecQtyReserve() {
		return secQtyReserve;
	}
	public void setSecQtyReserve(String secQtyReserve) {
		this.secQtyReserve = secQtyReserve;
	}
	public String getSecQtyAvailable() {
		return secQtyAvailable;
	}
	public void setSecQtyAvailable(String secQtyAvailable) {
		this.secQtyAvailable = secQtyAvailable;
	}
	public List<StockOnhandBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<StockOnhandBean> itemsList) {
		this.itemsList = itemsList;
	}
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}
	
	
}
