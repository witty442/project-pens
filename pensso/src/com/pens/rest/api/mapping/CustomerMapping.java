package com.pens.rest.api.mapping;



/**
 * CustomerMapping
 * 
 * @author Witty
 * @version $Id: CustomerMapping.java,v 1.0 15/02/2021 
 * 
 *      
 * 
 */
public class CustomerMapping  {

	private int no;
	private long id;
	private int referencesID;
	private String lat;
	private String lng;
	private String dispTotalInvoice;
	private String tripDay;
	private String tripDay2;
	private String tripDay3;
	private String custGroup;
	private int shipToAddressId;
	private int billToAddressId;
	private String code;
	private String name;
	private String name2;
	private String customerType;
	private String taxNo;
	private String territory;
	private String website;
	private String businessType;
	private long parentID;
	private String parentCode;
	private String parentName;
	private String birthDay;
	private String creditCheck;
	private String paymentTerm;
	private String paymentMethod;
	private String vatCode;
	private String shippingMethod;
	private String shippingRoute;
	private String transitName;
	private double creditLimit;
	private double totalInvoice;
	private String isActive;
	private String creditLimitLabel;
	private String totalInvoiceLabel;
	private int searchProvince;
	private int searchDistrict;
	private int orderAmount = 0;
	private String interfaces;
	private String partyType;
	private String exported;
	private String codePrefix;
	private String province;
	private String district;
	private String trip;
	public String addressSummary;
	public String displayExported;
	public String displayInterfaces;
	public String displayActionEditCust;
	public boolean canActionEditCust;
	public String displayActionEditCust2;
	public boolean canActionEditCust2;
	public String displayActionReceipt;
	public String displayActionView;
	public String displayActionEdit;
	private String printType;
	private String printBranchDesc;
	private String printHeadBranchDesc;
	private String printTax;
	private String airpayFlag;
	private String location;
	private String dispHaveTrip;
    private String imageFileName;
    private String preOrderFlag;
	 /** api message **/
    private String statusMessage;
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getReferencesID() {
		return referencesID;
	}
	public void setReferencesID(int referencesID) {
		this.referencesID = referencesID;
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
	public String getDispTotalInvoice() {
		return dispTotalInvoice;
	}
	public void setDispTotalInvoice(String dispTotalInvoice) {
		this.dispTotalInvoice = dispTotalInvoice;
	}
	public String getTripDay() {
		return tripDay;
	}
	public void setTripDay(String tripDay) {
		this.tripDay = tripDay;
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
	public String getCustGroup() {
		return custGroup;
	}
	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
	public int getShipToAddressId() {
		return shipToAddressId;
	}
	public void setShipToAddressId(int shipToAddressId) {
		this.shipToAddressId = shipToAddressId;
	}
	public int getBillToAddressId() {
		return billToAddressId;
	}
	public void setBillToAddressId(int billToAddressId) {
		this.billToAddressId = billToAddressId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getTaxNo() {
		return taxNo;
	}
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	public String getTerritory() {
		return territory;
	}
	public void setTerritory(String territory) {
		this.territory = territory;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public long getParentID() {
		return parentID;
	}
	public void setParentID(long parentID) {
		this.parentID = parentID;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getCreditCheck() {
		return creditCheck;
	}
	public void setCreditCheck(String creditCheck) {
		this.creditCheck = creditCheck;
	}
	public String getPaymentTerm() {
		return paymentTerm;
	}
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getVatCode() {
		return vatCode;
	}
	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}
	public String getShippingMethod() {
		return shippingMethod;
	}
	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}
	public String getShippingRoute() {
		return shippingRoute;
	}
	public void setShippingRoute(String shippingRoute) {
		this.shippingRoute = shippingRoute;
	}
	public String getTransitName() {
		return transitName;
	}
	public void setTransitName(String transitName) {
		this.transitName = transitName;
	}
	public double getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}
	public double getTotalInvoice() {
		return totalInvoice;
	}
	public void setTotalInvoice(double totalInvoice) {
		this.totalInvoice = totalInvoice;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getCreditLimitLabel() {
		return creditLimitLabel;
	}
	public void setCreditLimitLabel(String creditLimitLabel) {
		this.creditLimitLabel = creditLimitLabel;
	}
	public String getTotalInvoiceLabel() {
		return totalInvoiceLabel;
	}
	public void setTotalInvoiceLabel(String totalInvoiceLabel) {
		this.totalInvoiceLabel = totalInvoiceLabel;
	}
	public int getSearchProvince() {
		return searchProvince;
	}
	public void setSearchProvince(int searchProvince) {
		this.searchProvince = searchProvince;
	}
	public int getSearchDistrict() {
		return searchDistrict;
	}
	public void setSearchDistrict(int searchDistrict) {
		this.searchDistrict = searchDistrict;
	}
	public int getOrderAmount() {
		return orderAmount;
	}
	public void setOrderAmount(int orderAmount) {
		this.orderAmount = orderAmount;
	}
	public String getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
	public String getPartyType() {
		return partyType;
	}
	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public String getCodePrefix() {
		return codePrefix;
	}
	public void setCodePrefix(String codePrefix) {
		this.codePrefix = codePrefix;
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
	public String getTrip() {
		return trip;
	}
	public void setTrip(String trip) {
		this.trip = trip;
	}
	public String getAddressSummary() {
		return addressSummary;
	}
	public void setAddressSummary(String addressSummary) {
		this.addressSummary = addressSummary;
	}
	public String getDisplayExported() {
		return displayExported;
	}
	public void setDisplayExported(String displayExported) {
		this.displayExported = displayExported;
	}
	public String getDisplayInterfaces() {
		return displayInterfaces;
	}
	public void setDisplayInterfaces(String displayInterfaces) {
		this.displayInterfaces = displayInterfaces;
	}
	public String getDisplayActionEditCust() {
		return displayActionEditCust;
	}
	public void setDisplayActionEditCust(String displayActionEditCust) {
		this.displayActionEditCust = displayActionEditCust;
	}
	public boolean isCanActionEditCust() {
		return canActionEditCust;
	}
	public void setCanActionEditCust(boolean canActionEditCust) {
		this.canActionEditCust = canActionEditCust;
	}
	public String getDisplayActionEditCust2() {
		return displayActionEditCust2;
	}
	public void setDisplayActionEditCust2(String displayActionEditCust2) {
		this.displayActionEditCust2 = displayActionEditCust2;
	}
	public boolean isCanActionEditCust2() {
		return canActionEditCust2;
	}
	public void setCanActionEditCust2(boolean canActionEditCust2) {
		this.canActionEditCust2 = canActionEditCust2;
	}
	public String getDisplayActionReceipt() {
		return displayActionReceipt;
	}
	public void setDisplayActionReceipt(String displayActionReceipt) {
		this.displayActionReceipt = displayActionReceipt;
	}
	public String getDisplayActionView() {
		return displayActionView;
	}
	public void setDisplayActionView(String displayActionView) {
		this.displayActionView = displayActionView;
	}
	public String getDisplayActionEdit() {
		return displayActionEdit;
	}
	public void setDisplayActionEdit(String displayActionEdit) {
		this.displayActionEdit = displayActionEdit;
	}
	public String getPrintType() {
		return printType;
	}
	public void setPrintType(String printType) {
		this.printType = printType;
	}
	public String getPrintBranchDesc() {
		return printBranchDesc;
	}
	public void setPrintBranchDesc(String printBranchDesc) {
		this.printBranchDesc = printBranchDesc;
	}
	public String getPrintHeadBranchDesc() {
		return printHeadBranchDesc;
	}
	public void setPrintHeadBranchDesc(String printHeadBranchDesc) {
		this.printHeadBranchDesc = printHeadBranchDesc;
	}
	public String getPrintTax() {
		return printTax;
	}
	public void setPrintTax(String printTax) {
		this.printTax = printTax;
	}
	public String getAirpayFlag() {
		return airpayFlag;
	}
	public void setAirpayFlag(String airpayFlag) {
		this.airpayFlag = airpayFlag;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDispHaveTrip() {
		return dispHaveTrip;
	}
	public void setDispHaveTrip(String dispHaveTrip) {
		this.dispHaveTrip = dispHaveTrip;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	public String getPreOrderFlag() {
		return preOrderFlag;
	}
	public void setPreOrderFlag(String preOrderFlag) {
		this.preOrderFlag = preOrderFlag;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

    
}
