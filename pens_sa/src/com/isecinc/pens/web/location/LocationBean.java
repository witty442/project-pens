package com.isecinc.pens.web.location;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.isecinc.pens.report.salesanalyst.helper.Utils;

public class LocationBean implements Serializable{
	private static final long serialVersionUID = -4104083172800233911L;
	
	private String typeSearch;
	private String year;
	private String day;
	private String dayTo;
	private String[] chkMonth;

	private String province;
	private String district;
	private String customerCode;
	private String customerName;
	private String salesChannelCode;
	
	private List<LocationBean> itemsList;

	
	public String getTypeSearch() {
		return typeSearch;
	}

	public void setTypeSearch(String typeSearch) {
		this.typeSearch = typeSearch;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getDayTo() {
		return dayTo;
	}

	public void setDayTo(String dayTo) {
		this.dayTo = dayTo;
	}

	public String[] getChkMonth() {
		return chkMonth;
	}

	public void setChkMonth(String[] chkMonth) {
		this.chkMonth = chkMonth;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
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

	public String getSalesChannelCode() {
		return salesChannelCode;
	}

	public void setSalesChannelCode(String salesChannelCode) {
		this.salesChannelCode = salesChannelCode;
	}

	public List<LocationBean> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<LocationBean> itemsList) {
		this.itemsList = itemsList;
	}
  
   
}
