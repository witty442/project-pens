package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.jcraft.jsch.Logger;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;

/**
 * OrderLine
 * 
 * @author atiz.b
 * @version $Id: OrderLine.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderLine extends I_PO implements Serializable {

	private static final long serialVersionUID = 8089396696205085436L;

	/**
	 * Default Constructor
	 */
	public OrderLine() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public OrderLine(ResultSet rst) throws Exception {
		setId(rst.getLong("ORDER_LINE_ID"));
		setLineNo(rst.getInt("LINE_NO"));
		setOrderId(rst.getLong("ORDER_ID"));
		if(rst.getString("PRODUCT_ID")==null || Utils.isNull(rst.getString("PRODUCT_ID")).equals("")){
		   System.out.println("OrderId["+rst.getInt("ORDER_ID")+"] Find ProductId:"+rst.getString("PRODUCT_ID")+" IS NULL");
		}
		setProduct(new MProduct().findOpt(rst.getString("PRODUCT_ID")));
		setUom(new MUOM().findOpt(rst.getString("UOM_ID")));
		setPrice(rst.getDouble("PRICE"));
		setQty(rst.getDouble("QTY"));
		setLineAmount(rst.getDouble("LINE_AMOUNT"));
		setDiscount(rst.getDouble("DISCOUNT"));
		setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
		setShippingDate("");
		setRequestDate("");
		setPromotion(ConvertNullUtil.convertToString(rst.getString("PROMOTION")).trim());
		if (rst.getTimestamp("SHIPPING_DATE") != null)
			setShippingDate(DateToolsUtil.convertToString(rst.getTimestamp("SHIPPING_DATE")));
		if (rst.getTimestamp("REQUEST_DATE") != null)
			setRequestDate(DateToolsUtil.convertToString(rst.getTimestamp("REQUEST_DATE")));
		setPayment(rst.getString("PAYMENT"));
		setArInvoiceNo(ConvertNullUtil.convertToString(rst.getString("AR_INVOICE_NO")));
		setVatAmount(rst.getDouble("VAT_AMOUNT"));
		setIscancel(ConvertNullUtil.convertToString(rst.getString("ISCANCEL")));
		setTripNo(rst.getInt("TRIP_NO"));
		setPromotionFrom(ConvertNullUtil.convertToString(rst.getString("PROMOTION_FROM")));
		setTaxable(ConvertNullUtil.convertToString(rst.getString("TAXABLE")));
		try {
			setNeedExport(rst.getString("NEED_EXPORT"));
		} catch (Exception e) {}
		try {
			setExported(rst.getString("EXPORTED"));
		} catch (Exception e) {}
		try {
			setInterfaces(rst.getString("INTERFACES"));
			setCallBeforeSend(rst.getString("CALL_BEFORE_SEND"));
		} catch (Exception e) {}
		
		try {
			setOrg(rst.getString("ORG"));
			setSubInv(rst.getString("SUB_INV"));
		} catch (Exception e) {}
		
		setReservationId(rst.getLong("reservation_id"));
	}

	protected void setDisplayLabel() throws Exception {

	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format(
				"Order Line[%s] Product[%s] UOM[%s] Price[%s] Qty[%s] Amount[%s] Discount[%s] Total[%s] Promotion[%s]",
				getId(), getProduct(), getUom(), getPrice(), getQty(), getLineAmount(), getDiscount(),
				getTotalAmount(), getPromotion());
	}

	/** ID */
	private long id;

	/** Line No */
	private int lineNo;

	/** Order Id */
	private long orderId;

	/** Product */
	private Product product = new Product();

	/** UOM */
	private UOM uom = new UOM();
	private UOM uom1 = new UOM();
	private UOM uom2 = new UOM();
	private String fullUom;

	/** Price */
	private double price;
	private double price1;
	private double price2;

	/** Qty */
	private double qty;
	private double qty1;
	private double qty2;

	/** Amount */
	private double lineAmount;
	private double lineAmount1;
	private double lineAmount2;

	/** Discount */
	private double discount;
	private double discount1;
	private double discount2;

	/** Total */
	private double totalAmount;
	private double totalAmount1;
	private double totalAmount2;
	

	/** Shipping */
	private String shippingDate;

	/** Request */
	private String requestDate;

	/** Promotion Flag */
	private String promotion;

	/** Payment Flag */
	private String payment;

	/** AR Invoice No */
	private String arInvoiceNo;

	/** VAT_AMOUNT */
	private double vatAmount;
	private double vatAmount1;
	private double vatAmount2;

	/** ISCANCEL */
	private String iscancel;

	/** TRIP_NO */
	private int tripNo;

	/** PROMOTION_FROM */
	private String promotionFrom;

	/** Best Discount */
	private double bestDiscount;
	private double storeBestDiscount;

	/** NEED_EXPORT */
	private String needExport;

	/** EXPORTED */
	private String exported;

	/** INTERFACES */
	private String interfaces;
	
	/** CALL BEFORE SEND **/
	private String callBeforeSend;
    
	/** Wit Edit:15/05/2555 **/
	private String org;
	private String subInv;
	
	/** optional for report **/
	private String orderNo;
	private String customerName;
	private String customerCode;
	private Date orderDate;
	private String status;
	
	private String cancelDate;
	private String taxable;
	private double sellingPrice;
	private String rowStyle;
	private long reservationId;
	
	
	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}

	public String getRowStyle() {
		return rowStyle;
	}

	public void setRowStyle(String rowStyle) {
		this.rowStyle = rowStyle;
	}

	public double getStoreBestDiscount() {
		return storeBestDiscount;
	}

	public void setStoreBestDiscount(double storeBestDiscount) {
		this.storeBestDiscount = storeBestDiscount;
	}

	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getTaxable() {
		return taxable;
	}

	public void setTaxable(String taxable) {
		this.taxable = taxable;
	}

	public String getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getSubInv() {
		return subInv;
	}

	public void setSubInv(String subInv) {
		this.subInv = subInv;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFullUom() {
		return fullUom;
	}

	public void setFullUom(String fullUom) {
		this.fullUom = fullUom;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public double getLineAmount() {
		return lineAmount;
	}

	public void setLineAmount(double lineAmount) {
		this.lineAmount = lineAmount;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public UOM getUom() {
		return uom;
	}

	public void setUom(UOM uom) {
		this.uom = uom;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getArInvoiceNo() {
		return arInvoiceNo;
	}

	public void setArInvoiceNo(String arInvoiceNo) {
		this.arInvoiceNo = arInvoiceNo;
	}

	public double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(double vatAmount) {
		this.vatAmount = vatAmount;
	}

	public UOM getUom1() {
		return uom1;
	}

	public void setUom1(UOM uom1) {
		this.uom1 = uom1;
	}

	public UOM getUom2() {
		return uom2;
	}

	public void setUom2(UOM uom2) {
		this.uom2 = uom2;
	}

	public double getPrice1() {
		return price1;
	}

	public void setPrice1(double price1) {
		this.price1 = price1;
	}

	public double getPrice2() {
		return price2;
	}

	public void setPrice2(double price2) {
		this.price2 = price2;
	}

	public double getQty1() {
		return qty1;
	}

	public void setQty1(double qty1) {
		this.qty1 = qty1;
	}

	public double getQty2() {
		return qty2;
	}

	public void setQty2(double qty2) {
		this.qty2 = qty2;
	}

	public double getLineAmount1() {
		return lineAmount1;
	}

	public void setLineAmount1(double lineAmount1) {
		this.lineAmount1 = lineAmount1;
	}

	public double getLineAmount2() {
		return lineAmount2;
	}

	public void setLineAmount2(double lineAmount2) {
		this.lineAmount2 = lineAmount2;
	}

	public double getDiscount1() {
		return discount1;
	}

	public void setDiscount1(double discount1) {
		this.discount1 = discount1;
	}

	public double getDiscount2() {
		return discount2;
	}

	public void setDiscount2(double discount2) {
		this.discount2 = discount2;
	}

	public double getTotalAmount1() {
		return totalAmount1;
	}

	public void setTotalAmount1(double totalAmount1) {
		this.totalAmount1 = totalAmount1;
	}

	public double getTotalAmount2() {
		return totalAmount2;
	}

	public void setTotalAmount2(double totalAmount2) {
		this.totalAmount2 = totalAmount2;
	}

	public String getIscancel() {
		return iscancel;
	}

	public void setIscancel(String iscancel) {
		this.iscancel = iscancel;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public double getVatAmount1() {
		return vatAmount1;
	}

	public void setVatAmount1(double vatAmount1) {
		this.vatAmount1 = vatAmount1;
	}

	public double getVatAmount2() {
		return vatAmount2;
	}

	public void setVatAmount2(double vatAmount2) {
		this.vatAmount2 = vatAmount2;
	}

	public String getPromotionFrom() {
		return promotionFrom;
	}

	public void setPromotionFrom(String promotionFrom) {
		this.promotionFrom = promotionFrom;
	}

	public double getBestDiscount() {
		return bestDiscount;
	}

	public void setBestDiscount(double bestDiscount) {
		this.bestDiscount = bestDiscount;
	}

	public String getNeedExport() {
		return needExport;
	}

	public void setNeedExport(String needExport) {
		this.needExport = needExport;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getCallBeforeSend() {
		return callBeforeSend;
	}

	public void setCallBeforeSend(String callBeforeSend) {
		this.callBeforeSend = callBeforeSend;
	}

}
