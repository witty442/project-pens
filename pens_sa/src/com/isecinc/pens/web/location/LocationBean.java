package com.isecinc.pens.web.location;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class LocationBean implements  Serializable,Comparable<LocationBean>{
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
	private String salesrepId;
	private String salesrepCode;
	private String salesCode;
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
	
	//Trip
	private String tripDay;
	private String tripDay2;
	private String tripDay3;
	
	private String tripDayDB;
	private String tripDayDB2;
	private String tripDayDB3;
	private String customerTypeDB;
	private int totalRec;
	private String createUser;
	private String status;
	
	private long custAccountId;
	private long partySiteId;
	//option
	private String startDate;
	private String endDate;
	private String salesZone;
    private Date checkInDate;
    private Date salesAppDate;
    private Date mergDate;
    private String distance;
    private String fileName;
    
    
	public Date getMergDate() {
		return mergDate;
	}

	public void setMergDate(Date mergDate) {
		this.mergDate = mergDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getSalesAppDate() {
		return salesAppDate;
	}

	public void setSalesAppDate(Date salesAppDate) {
		this.salesAppDate = salesAppDate;
	}

	public String getCustomerTypeDB() {
		return customerTypeDB;
	}

	public void setCustomerTypeDB(String customerTypeDB) {
		this.customerTypeDB = customerTypeDB;
	}

	public String getSalesCode() {
		return salesCode;
	}

	public void setSalesCode(String salesCode) {
		this.salesCode = salesCode;
	}

	public String getSalesZone() {
		return salesZone;
	}

	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}

	public String getSalesrepId() {
		return salesrepId;
	}

	public void setSalesrepId(String salesrepId) {
		this.salesrepId = salesrepId;
	}

	public String getTripDayDB() {
		return tripDayDB;
	}

	public void setTripDayDB(String tripDayDB) {
		this.tripDayDB = tripDayDB;
	}

	public String getTripDayDB2() {
		return tripDayDB2;
	}

	public void setTripDayDB2(String tripDayDB2) {
		this.tripDayDB2 = tripDayDB2;
	}

	public String getTripDayDB3() {
		return tripDayDB3;
	}

	public void setTripDayDB3(String tripDayDB3) {
		this.tripDayDB3 = tripDayDB3;
	}

	public long getCustAccountId() {
		return custAccountId;
	}

	public void setCustAccountId(long custAccountId) {
		this.custAccountId = custAccountId;
	}

	public long getPartySiteId() {
		return partySiteId;
	}

	public void setPartySiteId(long partySiteId) {
		this.partySiteId = partySiteId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getTripDay2() {
		return tripDay2;
	}

	public void setTripDay2(String tripDay2) {
		this.tripDay2 = tripDay2;
	}

	public String getTripDay3() {
		return tripDay3;
	}

	public void setTripDay3(String tripDay3) {
		this.tripDay3 = tripDay3;
	}

	public int getTotalRec() {
		return totalRec;
	}

	public void setTotalRec(int totalRec) {
		this.totalRec = totalRec;
	}

	public String getTripDay() {
		return tripDay;
	}

	public void setTripDay(String tripDay) {
		this.tripDay = tripDay;
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
	 @Override
	 public int compareTo(LocationBean o) {
	     return Comparators.TRIP_ASC.compare(this, o);
	  }
	 public static class Comparators {
			public static Comparator<LocationBean> TRIP_ASC = new Comparator<LocationBean>() {
		        @Override
		        public int compare(LocationBean o1, LocationBean o2) {
		            return Integer.parseInt(o1.getTripDay())-(Integer.parseInt(o2.getTripDay()));
		        }
		    };
		    
		    public static Comparator<LocationBean> CHECKIN_CREATE_DATE_ASC = new Comparator<LocationBean>() {
		        @Override
		        public int compare(LocationBean o1, LocationBean o2) {
		        	//if()
		            return o1.getMergDate().compareTo(o2.getMergDate());
		        }
		    };
		 }
   
}
