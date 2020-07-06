package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;

import util.ConvertNullUtil;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.model.MOrgRule;

/**
 * Order
 * 
 * @author atiz.b
 * @version $Id: Order.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Order extends I_PO implements Serializable {

	private static final long serialVersionUID = -136106985704627134L;

	public static final String MODERN_TARDE = "MT";
	public static final String CREDIT = "CR";
	public static final String CASH = "CS";
	public static final String DIRECT_DELIVERY = "DD";

	/**
	 * Default Constructor
	 */
	public Order() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public Order(ResultSet rst) throws Exception {
		setId(rst.getInt("ORDER_ID"));
		setOrderNo(rst.getString("ORDER_NO"));
		setOrderDate(DateToolsUtil.convertToString(rst.getTimestamp("ORDER_DATE")));
		setOrderTime(rst.getString("ORDER_TIME"));
		setOrderType(rst.getString("ORDER_TYPE").trim());
		setCustomerId(rst.getInt("CUSTOMER_ID"));
		setCustomerName(rst.getString("CUSTOMER_NAME").trim());
		//Get custGroup
		setCustGroup(new MCustomer().getCustGroup(getCustomerId()));
		setBillAddressId(rst.getInt("BILL_ADDRESS_ID"));
		setShipAddressId(rst.getInt("SHIP_ADDRESS_ID"));
		setPriceListId(rst.getInt("PRICELIST_ID"));
		setPaymentTerm(rst.getString("PAYMENT_TERM").trim());
		setVatCode(rst.getString("VAT_CODE").trim());
		setVatRate(rst.getDouble("VAT_RATE"));
		setPaymentMethod(rst.getString("PAYMENT_METHOD").trim());
		setShippingDay(ConvertNullUtil.convertToString(rst.getString("SHIPPING_DAY")).trim());
		setShippingTime(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME")).trim());
		setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
		setTotalAmountNonVat(rst.getDouble("TOTAL_AMOUNT_NON_VAT"));
		setVatAmount(rst.getDouble("VAT_AMOUNT"));
		setNetAmount(rst.getDouble("NET_AMOUNT"));
		setInterfaces(rst.getString("INTERFACES").trim());
		setPayment(rst.getString("PAYMENT").trim());
		setSalesOrderNo(ConvertNullUtil.convertToString(rst.getString("SALES_ORDER_NO")).trim());
		setArInvoiceNo(ConvertNullUtil.convertToString(rst.getString("AR_INVOICE_NO")).trim());
		setSalesRepresent(new MUser().find(rst.getString("USER_ID")));
		setDocStatus(rst.getString("DOC_STATUS").trim());
		setCreated(DateToolsUtil.convertFromTimestamp(rst.getTimestamp("CREATED")));
		setExported(rst.getString("EXPORTED"));
		setIsCash(rst.getString("ISCASH"));
		setRemark(ConvertNullUtil.convertToString(rst.getString("remark")).trim());
		// a-neak.t 20110314
		setCallBeforeSend(rst.getString("CALL_BEFORE_SEND"));
		// set display
		setDisplayLabel();
		
		//wit 20110804
		setPaymentCashNow("CS".equals(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD").trim()))?true:false);
		
		// Add Oracle Reference Address ID
		setOraBillAddressID(rst.getInt("ORA_BILL_ADDRESS_ID"));
		setOraShipAddressID(rst.getInt("ORA_SHIP_ADDRESS_ID"));
		
		//Wit Edit 15/05/2012
		setOrg(rst.getString("org"));
		if( !"".equals(ConvertNullUtil.convertToString(getOrg()))){
		   setPlaceOfBilled(new MOrgRule().getOrgRule(getOrg()).getName());
		}
		//System.out.println("print_datetime_pick:"+rst.getBigDecimal("print_datetime_pick"));
		//System.out.println("print_datetime_rcp:"+rst.getBigDecimal("print_datetime_rcp"));
		
		setPrintDateTimePick(Utils.stringValueSpecial2(rst.getLong("print_datetime_pick"),Utils.DD_MM_YYYY_HH_mm_WITHOUT_SLASH,Utils.local_th));
		setPrintCountPick(rst.getInt("print_count_pick"));
		
		setPrintDateTimeRcp(Utils.stringValueSpecial2(rst.getLong("print_datetime_rcp"),Utils.DD_MM_YYYY_HH_mm_WITHOUT_SLASH,Utils.local_th));
		setPrintCountRcp(rst.getInt("print_count_rcp"));
		
		setPoNumber(Utils.isNull(rst.getString("po_number")));
		setVanPaymentMethod(Utils.isNull(rst.getString("van_payment_method")));
	}

	/**
	 * Set display
	 */
	public void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.DOC_STATUS)) {
			if (r.getKey().equalsIgnoreCase(getDocStatus())) {
				setDocStatusLabel(r.getName());
				break;
			}
		}
	}

	/** ID */
	private int id;

	/** Order No */
	private String orderNo;

	/** Order Date */
	private String orderDate;

	/** Order Time */
	private String orderTime;

	/** Order Type */
	private String orderType;

	/** Customer ID */
	private int customerId;

	/** Customer Name */
	private String customerName;

	/** Bill Address */
	private int billAddressId;

	/** Ship Address */
	private int shipAddressId;

	/** Price List */
	private int priceListId;

	/** Payment Term */
	private String paymentTerm;

	/** Vat Code */
	private String vatCode;

	/** Vat Rate */
	private double vatRate;

	/** Payment Method */
	private String paymentMethod;

	/** Shipping Day */
	private String shippingDay;

	/** Shipping Time */
	private String shippingTime;

	/** Total Amount */
	private double totalAmount;
	
	/** Total Amount Non Vat*/
	private double totalAmountNonVat;
	
	/** Vat Amount */
	private double vatAmount;

	/** Net Amount */
	private double netAmount;

	/** Interfaces flag */
	private String interfaces;

	/** Payment Flag */
	private String payment;

	/** Sales Order No */
	private String salesOrderNo;

	/** Invoice No */
	private String arInvoiceNo;

	/** Sales Represent */
	private User salesRepresent = new User();

	/** Doc Status */
	private String docStatus;

	/** Order Date From */
	private String orderDateFrom;

	/** Order Date To */
	private String orderDateTo;

	/** Doc Status Label */
	private String docStatusLabel;

	/** CREATED */
	private String created;

	/** PRICE LIST LABEL */
	private String pricelistLabel;

	// --Transient
	/** CREDIT_AMOUNT */
	private double creditAmount;

	/** PAID_AMOUNT */
	private double paidAmount;

	/** REMAIN_AMOUNT */
	private double remainAmount;

	/** EXPORTED */
	private String exported;

	/** ROUND TRIP **/
	private int roundTrip;

	/** ISCASH */
	private String isCash;

	/** Transient */
	private String memberCode;

	/** Order Remark */
	private String remark;
	
	/** Call Before Send **/
	private String callBeforeSend;

	private String reason;
	
	private boolean paymentCashNow;
	
	private int oraBillAddressID;
	
	private int oraShipAddressID;
	
	private double creditNoteAmt;
	private double adjustAmt;
	
	private double openAmt;
	
	//Display Optional no DB
	private String placeOfBilled;
	private String org;
	private String printDateTimePick;
	private int printCountPick;
	private String printDateTimeRcp;
	private int printCountRcp;
	private String poNumber;
	private String vanPaymentMethod;
    private boolean promotionSP;
	private String custGroup;
   
	
	public String getCustGroup() {
		return custGroup;
	}

	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}

	public boolean isPromotionSP() {
		return promotionSP;
	}

	public void setPromotionSP(boolean promotionSP) {
		this.promotionSP = promotionSP;
	}

	public String getVanPaymentMethod() {
		return vanPaymentMethod;
	}

	public void setVanPaymentMethod(String vanPaymentMethod) {
		this.vanPaymentMethod = vanPaymentMethod;
	}

	public double getTotalAmountNonVat() {
		return totalAmountNonVat;
	}

	public void setTotalAmountNonVat(double totalAmountNonVat) {
		this.totalAmountNonVat = totalAmountNonVat;
	}


	public double getAdjustAmt() {
		return adjustAmt;
	}

	public void setAdjustAmt(double adjustAmt) {
		this.adjustAmt = adjustAmt;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getPrintDateTimePick() {
		return printDateTimePick;
	}

	public void setPrintDateTimePick(String printDateTimePick) {
		this.printDateTimePick = printDateTimePick;
	}

	public int getPrintCountPick() {
		return printCountPick;
	}

	public void setPrintCountPick(int printCountPick) {
		this.printCountPick = printCountPick;
	}

	public String getPrintDateTimeRcp() {
		return printDateTimeRcp;
	}

	public void setPrintDateTimeRcp(String printDateTimeRcp) {
		this.printDateTimeRcp = printDateTimeRcp;
	}

	public int getPrintCountRcp() {
		return printCountRcp;
	}

	public void setPrintCountRcp(int printCountRcp) {
		this.printCountRcp = printCountRcp;
	}

	public String getPlaceOfBilled() {
		return placeOfBilled;
	}

	public void setPlaceOfBilled(String placeOfBilled) {
		this.placeOfBilled = placeOfBilled;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}


	public boolean isPaymentCashNow() {
		return paymentCashNow;
	}

	public void setPaymentCashNow(boolean paymentCashNow) {
		this.paymentCashNow = paymentCashNow;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(int roundTrip) {
		this.roundTrip = roundTrip;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getBillAddressId() {
		return billAddressId;
	}

	public void setBillAddressId(int billAddressId) {
		this.billAddressId = billAddressId;
	}

	public int getShipAddressId() {
		return shipAddressId;
	}

	public void setShipAddressId(int shipAddressId) {
		this.shipAddressId = shipAddressId;
	}

	public int getPriceListId() {
		return priceListId;
	}

	public void setPriceListId(int priceListId) {
		this.priceListId = priceListId;
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getVatCode() {
		return vatCode;
	}

	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}

	public double getVatRate() {
		return vatRate;
	}

	public void setVatRate(double vatRate) {
		this.vatRate = vatRate;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getShippingDay() {
		return shippingDay;
	}

	public void setShippingDay(String shippingDay) {
		this.shippingDay = shippingDay;
	}

	public String getShippingTime() {
		return shippingTime;
	}

	public void setShippingTime(String shippingTime) {
		this.shippingTime = shippingTime;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}

	public double getNetAmount() {
		return netAmount;
	}

	public void setNetAmount(double netAmount) {
		this.netAmount = netAmount;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getSalesOrderNo() {
		return salesOrderNo;
	}

	public void setSalesOrderNo(String salesOrderNo) {
		this.salesOrderNo = salesOrderNo;
	}

	public String getArInvoiceNo() {
		return arInvoiceNo;
	}

	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}

	public User getSalesRepresent() {
		return salesRepresent;
	}

	public void setSalesRepresent(User salesRepresent) {
		this.salesRepresent = salesRepresent;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getOrderDateFrom() {
		return orderDateFrom;
	}

	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}

	public String getOrderDateTo() {
		return orderDateTo;
	}

	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}

	public String getDocStatusLabel() {
		return docStatusLabel;
	}

	public void setDocStatusLabel(String docStatusLabel) {
		this.docStatusLabel = docStatusLabel;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getPricelistLabel() {
		return pricelistLabel;
	}

	public void setPricelistLabel(String pricelistLabel) {
		this.pricelistLabel = pricelistLabel;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public double getRemainAmount() {
		return remainAmount;
	}

	public void setRemainAmount(double remainAmount) {
		this.remainAmount = remainAmount;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getIsCash() {
		return isCash;
	}

	public void setIsCash(String isCash) {
		this.isCash = isCash;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getCallBeforeSend() {
		return callBeforeSend;
	}

	public void setCallBeforeSend(String callBeforeSend) {
		this.callBeforeSend = callBeforeSend;
	}

	public int getOraBillAddressID() {
		return oraBillAddressID;
	}

	public void setOraBillAddressID(int oraBillAddressID) {
		this.oraBillAddressID = oraBillAddressID;
	}

	public int getOraShipAddressID() {
		return oraShipAddressID;
	}

	public void setOraShipAddressID(int oraShipAddressID) {
		this.oraShipAddressID = oraShipAddressID;
	}

	public double getCreditNoteAmt() {
		return creditNoteAmt;
	}

	public void setCreditNoteAmt(double creditNoteAmt) {
		this.creditNoteAmt = creditNoteAmt;
	}

	public void setOpenAmt() {
		this.openAmt = NumberToolsUtil.round((this.getCreditAmount()+(this.getCreditNoteAmt()+this.getAdjustAmt())), 2, BigDecimal.ROUND_HALF_UP); 
		/*System.out.println("this.getCreditAmount():"+this.getCreditAmount());
		System.out.println("this.getCreditNoteAmt():"+this.getCreditNoteAmt());
		System.out.println("this.getAdjustAmt():"+this.getAdjustAmt());*/
		//this.openAmt = this.getCreditAmount()+this.getCreditNoteAmt()+this.getAdjustAmt(); 
		
		//System.out.println("openAmt:"+this.getOpenAmt());
	}
	
	public double getOpenAmt(){
		return this.openAmt;
	}
	
	private boolean hasCreditNote;
	
	public boolean isHasCreditNote() {
		return hasCreditNote;
	}

	public void setHasCreditNote(boolean hasCreditNote) {
		this.hasCreditNote = hasCreditNote;
	}
}
