package com.isecinc.pens.web.prodshow;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.isecinc.pens.bean.User;

public class ProdShowBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;

	private String periodType;
	private String period;
	private String periodDesc;
	private String startDate;
	private String endDate;
	private String custCatNo;
	private String salesChannelNo;
	private String salesChannelName;
	private String salesrepCode;
	private String salesrepName;
	
	private String brand;
	private String brandName;
	private String customerCode;
	private String customerName;
	private String createUser;
	private String updateUser;
	private List<ProdShowBean> items;
	
	private long lineNumber;
	private String orderNo;
	private String showDate;
	private String pic1;
	private String pic2;
	private String pic3;
	private String salesZone;
	
	
	
	public String getSalesZone() {
		return salesZone;
	}
	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}
	public String getShowDate() {
		return showDate;
	}
	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}
	public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getPeriodDesc() {
		return periodDesc;
	}
	public void setPeriodDesc(String periodDesc) {
		this.periodDesc = periodDesc;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCustCatNo() {
		return custCatNo;
	}
	public void setCustCatNo(String custCatNo) {
		this.custCatNo = custCatNo;
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
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
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
	public List<ProdShowBean> getItems() {
		return items;
	}
	public void setItems(List<ProdShowBean> items) {
		this.items = items;
	}
	public long getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPic1() {
		return pic1;
	}
	public void setPic1(String pic1) {
		this.pic1 = pic1;
	}
	public String getPic2() {
		return pic2;
	}
	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}
	public String getPic3() {
		return pic3;
	}
	public void setPic3(String pic3) {
		this.pic3 = pic3;
	}
	
	
}
