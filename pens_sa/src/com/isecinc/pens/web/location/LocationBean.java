package com.isecinc.pens.web.location;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import util.Utils;

public class LocationBean implements Serializable{
	private static final long serialVersionUID = -4104083172800233911L;
	
	private String typeSearch;
	private String reportType;
	private String year;
	private String day;
	private String dayTo;
	private String[] chkMonth;
    private String dispType;
	private String province;
	private String district;
	private String customerCode;
	private String customerName;
	private String customerType;
	private String custCatNo;
	private String salesChannelNo;
	private String salesrepCode;
	private String salesrepName;
	private String dispAllStore;
	private String dispAllOrder;
	private String dispAllVisit;
	private String dispAllNoOrder;
	
	private String lat;
	private String lng;
	private String locationType;
	private List<LocationBean> itemsList;
	private String address;
	private String trip;
	private String googleAddress;
	private String tripType;
	private String orderDate;
	private String orderNo;
	private String visitDate;
	private String custLat;
	private String custLng;
	
	//option
	private String startDate;
	private String endDate;

	
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

	public String getCustLat() {
		return custLat;
	}

	public void setCustLat(String custLat) {
		this.custLat = custLat;
	}

	public String getCustLng() {
		return custLng;
	}

	public void setCustLng(String custLng) {
		this.custLng = custLng;
	}

	public String getDispAllNoOrder() {
		return dispAllNoOrder;
	}

	public void setDispAllNoOrder(String dispAllNoOrder) {
		this.dispAllNoOrder = dispAllNoOrder;
	}


	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getSalesrepName() {
		return salesrepName;
	}

	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}

	public String getTripType() {
		return tripType;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTrip() {
		return trip;
	}

	public void setTrip(String trip) {
		this.trip = trip;
	}

	public String getGoogleAddress() {
		return googleAddress;
	}

	public void setGoogleAddress(String googleAddress) {
		this.googleAddress = googleAddress;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getDispAllStore() {
		return dispAllStore;
	}

	public void setDispAllStore(String dispAllStore) {
		this.dispAllStore = dispAllStore;
	}

	public String getDispAllOrder() {
		return dispAllOrder;
	}

	public void setDispAllOrder(String dispAllOrder) {
		this.dispAllOrder = dispAllOrder;
	}

	public String getDispAllVisit() {
		return dispAllVisit;
	}

	public void setDispAllVisit(String dispAllVisit) {
		this.dispAllVisit = dispAllVisit;
	}

	public String getDispType() {
		return dispType;
	}

	public void setDispType(String dispType) {
		this.dispType = dispType;
	}

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

	public List<LocationBean> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<LocationBean> itemsList) {
		this.itemsList = itemsList;
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

	public String getSalesrepCode() {
		return salesrepCode;
	}

	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
  
   
}
