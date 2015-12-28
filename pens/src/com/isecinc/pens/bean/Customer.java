package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MUser;

/**
 * Customer
 * 
 * @author Aneak.t
 * @version $Id: Customer.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 *          atiz.b : code prefix transient
 * 
 */
public class Customer extends I_PO implements Serializable {

	private static final long serialVersionUID = -8675517484785667619L;

	public static final String MODERN_TARDE = "MT";
	public static final String CREDIT = "CT";
	public static final String CASH = "CV";
	public static final String DIREC_DELIVERY = "DD";

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
		setReferencesID(rst.getInt("REFERENCE_ID"));
		setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
		setTaxNo(ConvertNullUtil.convertToString(rst.getString("TAX_NO")).trim());
		setWebsite(ConvertNullUtil.convertToString(rst.getString("WEBSITE")).trim());
		setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
		setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
		// System.out.println(rst.getString("PARENT_CUSTOMER_ID"));
		if (rst.getInt("PARENT_CUSTOMER_ID") != 0 && rst.getString("PARENT_CUSTOMER_ID") != null) {
			Customer c = new MCustomer().find(rst.getString("PARENT_CUSTOMER_ID"));
			setParentID(c.getId());
			setParentCode(c.getCode());
			setParentName((c.getName() + " " + c.getName2()).trim());
		}
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
		//setTotalInvoice(new MCustomer().getInvoiceAmount(getId()));
		setTotalInvoice( new MReceiptLine().lookCreditAmt(getId()));
		
		// Order Amount
		setOrderAmount(new MOrder().lookUpByCustomer(getId()));

		// set display label
		setDisplayLabel();
		
		//setPint 
		setPrintTax( Utils.isNull(rst.getString("PRINT_TAX")));
		setPrintBranchDesc(Utils.isNull(rst.getString("PRINT_BRANCH_DESC")));//�Ңҷ��
		setPrintHeadBranchDesc(Utils.isNull(rst.getString("PRINT_HEAD_BRANCH_DESC"))); //"�����ʹ�./�Ңҷ��"
		setPrintType(Utils.isNull(rst.getString("PRINT_TYPE")));//Y or N
		
		setAirpayFlag(Utils.isNull(rst.getString("AIRPAY_FLAG")));
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

	public String toString() {
		return String.format("Customer[%s] %s %s", getId(), getName(), getName2());
	}

	private int no;
	/** ID */
	private int id;

	/** Reference from ORCL */
	private int referencesID;

	/** CODE */
	private String code;

	/** NAME */
	private String name;

	/** NAME2 */
	private String name2;

	/** CUSTOMER TYPE */
	private String customerType;

	/** TAX NO */
	private String taxNo;

	/** TERRITORY */
	private String territory;

	/** Web site */
	private String website;

	/** Business Type */
	private String businessType;

	/** Parent */
	private int parentID;
	private String parentCode;
	private String parentName;

	/** Birthday */
	private String birthDay;

	/** Credit Check */
	private String creditCheck;

	/** Payment Term */
	private String paymentTerm;

	/** Payment Method */
	private String paymentMethod;

	/** Vat Code */
	private String vatCode;

	/** Shipping Method */
	private String shippingMethod;

	/** Shipping Route */
	private String shippingRoute;

	/** TRANSIT NAME */
	private String transitName;

	/** CREDIT LIMIT */
	private double creditLimit;

	/** TOTAL INVOICE */
	private double totalInvoice;

	/** Sales Represent */
	private User salesRepresent = new User();

	/** ISACTIVE */
	private String isActive;

	/** CREDIT LIMIT LABEL */
	private String creditLimitLabel;

	/** TOTAL INVOICE LABEL */
	private String totalInvoiceLabel;

	/** Search Province */
	private int searchProvince;
	
	private int searchDistrict;

	/** Order Amount */
	private int orderAmount = 0;

	/** Interfaces */
	private String interfaces;

	/** PARTY_TYPE */
	private String partyType;

	/** EXPORTED */
	private String exported;

	// Transient
	/** Code Prefix */
	private String codePrefix;

	/** PROVINCE */
	private String province;

	/** DISTRICT */
	private String district;
	
	/** TRIP **/
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getReferencesID() {
		return referencesID;
	}

	public void setReferencesID(int referencesID) {
		this.referencesID = referencesID;
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

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
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
	
	

}
