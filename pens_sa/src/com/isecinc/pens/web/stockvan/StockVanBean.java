package com.isecinc.pens.web.stockvan;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.isecinc.pens.bean.User;

public class StockVanBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;

	private String pdType;
	private String pdCode;
	private String pdCodeIntransit;
	private String pdDesc;
	private String salesChannelNo;
	private String salesChannelName;

	private String brand;
	private String brandName;
	private String productCode;
	private String productName;
	private String dispType;
	private String dispPlan;
	private String dispHaveQty;
	private String pdQty;
	private String pdIntQty;
	private String createUser;
	private String updateUser;
	
	//option for display
	private List<StockVanBean> rowColumnDataList;
	
	
	public String getDispHaveQty() {
		return dispHaveQty;
	}
	public void setDispHaveQty(String dispHaveQty) {
		this.dispHaveQty = dispHaveQty;
	}
	public String getPdCodeIntransit() {
		return pdCodeIntransit;
	}
	public void setPdCodeIntransit(String pdCodeIntransit) {
		this.pdCodeIntransit = pdCodeIntransit;
	}
	public String getPdQty() {
		return pdQty;
	}
	public void setPdQty(String pdQty) {
		this.pdQty = pdQty;
	}
	
	public String getPdIntQty() {
		return pdIntQty;
	}
	public void setPdIntQty(String pdIntQty) {
		this.pdIntQty = pdIntQty;
	}
	public String getDispType() {
		return dispType;
	}
	public void setDispType(String dispType) {
		this.dispType = dispType;
	}
	public String getDispPlan() {
		return dispPlan;
	}
	public void setDispPlan(String dispPlan) {
		this.dispPlan = dispPlan;
	}
	public String getPdType() {
		return pdType;
	}
	public void setPdType(String pdType) {
		this.pdType = pdType;
	}
	public String getPdCode() {
		return pdCode;
	}
	public void setPdCode(String pdCode) {
		this.pdCode = pdCode;
	}
	public String getPdDesc() {
		return pdDesc;
	}
	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
	}
	public String getSalesChannelNo() {
		return salesChannelNo;
	}
	public void setSalesChannelNo(String salesChannelNo) {
		this.salesChannelNo = salesChannelNo;
	}
	public String getSalesChannelName() {
		return salesChannelName;
	}
	public void setSalesChannelName(String salesChannelName) {
		this.salesChannelName = salesChannelName;
	}
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
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public List<StockVanBean> getRowColumnDataList() {
		return rowColumnDataList;
	}
	public void setRowColumnDataList(List<StockVanBean> rowColumnDataList) {
		this.rowColumnDataList = rowColumnDataList;
	}
	
	
}
