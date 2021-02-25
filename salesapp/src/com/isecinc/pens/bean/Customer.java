package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Comparator;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MUser;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;
import com.pens.util.Utils;
import com.isecinc.pens.model.MReceiptSummary;


/**
 * Customer
 * 
 * @author Witty
 * @version $Id: Customer.java,v 1.0 15/02/2021 
 * 
 *      
 * 
 */
public class Customer extends I_PO implements Serializable,Comparable<Customer> {

	private static final long serialVersionUID = -8675517484785667619L;

	public static final String MODERN_TARDE = "MT";
	public static final String CREDIT = "CT";
	public static final String CASH = "CV";
	public static final String DIREC_DELIVERY = "DD";
	

	private int no;
	private long id;
	private long oracleCustId;//=customerId(oracle)
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
	private User salesRepresent = new User();
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
    private Address address = new Address();
	private Contact contact = new Contact();
	 /** api message **/
    private String statusMessage;

	/**
	 * Default Constructor
	 */
	public Customer() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Customer(ResultSet rst) throws Exception {
		no++;
		// Mandatory
		setId(rst.getInt("CUSTOMER_ID"));
		setOracleCustId(rst.getInt("oracle_cust_id"));
		setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
		setTaxNo(ConvertNullUtil.convertToString(rst.getString("TAX_NO")).trim());
		setWebsite(ConvertNullUtil.convertToString(rst.getString("WEBSITE")).trim());
		setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
		setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
		// System.out.println(rst.getString("PARENT_CUSTOMER_ID"));
		/*
		 * if (rst.getInt("PARENT_CUSTOMER_ID") != 0 &&
		 * rst.getString("PARENT_CUSTOMER_ID") != null) { Customer c = new
		 * MCustomer().find(rst.getString("PARENT_CUSTOMER_ID"));
		 * setParentID(c.getId()); setParentCode(c.getCode());
		 * setParentName((c.getName() + " " + c.getName2()).trim()); }
		 */
		setBirthDay("");
		if (rst.getTimestamp("BIRTHDAY") != null) {
			setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
		}
		setCreditCheck(ConvertNullUtil.convertToString(rst.getString("CREDIT_CHECK")).trim());
		setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
		setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
		setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
		setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
		setShippingRoute(ConvertNullUtil.convertToString(rst.getString("SHIPPING_ROUTE")).trim());
		setTransitName(ConvertNullUtil.convertToString(rst.getString("TRANSIT_NAME")).trim());
		setSalesRepresent(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setCreditLimit(rst.getDouble("CREDIT_LIMIT"));
		setIsActive(rst.getString("ISACTIVE").trim());
		setInterfaces(rst.getString("INTERFACES").trim());
		setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
		setExported(rst.getString("EXPORTED"));

		// Total Invoice
		setTotalInvoice( MReceiptSummary.lookCreditAmtByCustomerId(getId()));
		
		// Order Amount
		setOrderAmount(new MOrder().lookUpByCustomer(getId()));

		// set display label
		setDisplayLabel();
		
		//setPint 
		setPrintTax( Utils.isNull(rst.getString("PRINT_TAX")));
		setPrintBranchDesc(Utils.isNull(rst.getString("PRINT_BRANCH_DESC")));//สาขาที่
		setPrintHeadBranchDesc(Utils.isNull(rst.getString("PRINT_HEAD_BRANCH_DESC"))); //"พิมพ์สนญ./สาขาที่"
		setPrintType(Utils.isNull(rst.getString("PRINT_TYPE")));//Y or N
		
		setAirpayFlag(Utils.isNull(rst.getString("AIRPAY_FLAG")));
		//setLocation(Utils.isNull(rst.getString("LOCATION")));//Deprecate
		setLat(Utils.isNull(rst.getString("lat")));
		setLng(Utils.isNull(rst.getString("lng")));
		if( !Utils.isNull(getLat()).equals("") && !Utils.isNull(getLng()).equals("")){
		  setLocation(getLat()+","+getLng());
		}
		
		//setImageFileBlob(rst.getBlob("image_file"));
		setImageFileName(Utils.isNull(rst.getString("image_file_name")));
		
		setTripDay( Utils.isNull(rst.getString("TRIP_DAY")));
		setTripDay2( Utils.isNull(rst.getString("TRIP_DAY2")));
		setTripDay3( Utils.isNull(rst.getString("TRIP_DAY3")));
		setCustGroup( Utils.isNull(rst.getString("cust_group")));
	}

	/**
	 * Set Display Label
	 */
	public void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
		setCreditLimitLabel(new DecimalFormat("#,##0.00").format(getCreditLimit()));
		setTotalInvoiceLabel(new DecimalFormat("#,##0.00").format(getTotalInvoice()));
	}

	
	public long getOracleCustId() {
		return oracleCustId;
	}

	public void setOracleCustId(long oracleCustId) {
		this.oracleCustId = oracleCustId;
	}

	public String toString() {
		return String.format("Customer[%s] %s %s", getId(), getName(), getName2());
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
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

	public String getCustGroup() {
		return custGroup;
	}

	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
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
	public String getDispHaveTrip() {
		return dispHaveTrip;
	}

	public void setDispHaveTrip(String dispHaveTrip) {
		this.dispHaveTrip = dispHaveTrip;
	}

	public String getDispTotalInvoice() {
		return dispTotalInvoice;
	}

	public void setDispTotalInvoice(String dispTotalInvoice) {
		this.dispTotalInvoice = dispTotalInvoice;
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

	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	/*public Blob getImageFileBlob() {
		return imageFileBlob;
	}

	public void setImageFileBlob(Blob imageFileBlob) {
		this.imageFileBlob = imageFileBlob;
	}*/

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAirpayFlag() {
		return airpayFlag;
	}

	public void setAirpayFlag(String airpayFlag) {
		this.airpayFlag = airpayFlag;
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

	public String getAddressSummary() {
		return addressSummary;
	}

	public void setAddressSummary(String addressSummary) {
		this.addressSummary = addressSummary;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
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

	public User getSalesRepresent() {
		return salesRepresent;
	}

	public void setSalesRepresent(User salesRepresent) {
		this.salesRepresent = salesRepresent;
	}

	public String getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
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

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public boolean isCanActionEditCust() {
		return canActionEditCust;
	}

	public void setCanActionEditCust(boolean canActionEditCust) {
		this.canActionEditCust = canActionEditCust;
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
	
	 @Override
	 public int compareTo(Customer o) {
	     return Comparators.TRIP_ASC.compare(this, o);
	  }
	 
	 public static class Comparators {
		public static Comparator<Customer> TRIP_ASC = new Comparator<Customer>() {
	        @Override
	        public int compare(Customer o1, Customer o2) {
	            return Integer.parseInt(o1.getTripDay())-(Integer.parseInt(o2.getTripDay()));
	        }
	    };
	 }

}
