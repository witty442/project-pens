package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.isecinc.core.model.I_PO;

public class StockLine extends I_PO implements Serializable{

	
	/**
	 * Default Constructor
	 */
	public StockLine() {}
	

	private static final long serialVersionUID = 8286170868166006317L;
	private String requestNumber ;
	private String inventoryItemId; 
	    
	private String status ;
	private String statusLabel;
	private String exported; 
	private String userId;
	
	private String created; 
	private String createdBy; 
	private String updated; 
	private String updateBy;
	private String actionDate;
	private String expireDate;
	private String expireDate2;
	private String expireDate3;
	
	private boolean canEdit = false;

	private int no;
	
	/** ID */
	private int id;

	/** Line No */
	private int lineId;

	/** Order Id */
	private int orderId;

	/** Product */
	private Product product = new Product();
    private String productCode;
    private String productName;
    
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
	private double pack;
	private String qty;
	private String qty2;
	private String qty3;
	
	private String sub;
	private String sub2;
	private String sub3;

	/** Amount */
	private double amount;
	private double amount1;
	private double amount2;

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

	/** NEED_EXPORT */
	private String needExport;

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
	private String createDate;

	
	protected void setDisplayLabel() throws Exception {

	}
	
	
	public String getExpireDate2() {
		return expireDate2;
	}


	public void setExpireDate2(String expireDate2) {
		this.expireDate2 = expireDate2;
	}


	public String getExpireDate3() {
		return expireDate3;
	}


	public void setExpireDate3(String expireDate3) {
		this.expireDate3 = expireDate3;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
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


	public double getPack() {
		return pack;
	}


	public void setPack(double pack) {
		this.pack = pack;
	}


	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}
	public String getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getLineId() {
		return lineId;
	}


	public void setLineId(int lineId) {
		this.lineId = lineId;
	}


	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
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
	public String getFullUom() {
		return fullUom;
	}
	public void setFullUom(String fullUom) {
		this.fullUom = fullUom;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
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
	
	public String getQty() {
		return qty;
	}


	public void setQty(String qty) {
		this.qty = qty;
	}


	public String getQty2() {
		return qty2;
	}


	public void setQty2(String qty2) {
		this.qty2 = qty2;
	}


	public String getQty3() {
		return qty3;
	}


	public void setQty3(String qty3) {
		this.qty3 = qty3;
	}


	public String getSub() {
		return sub;
	}


	public void setSub(String sub) {
		this.sub = sub;
	}


	public String getSub2() {
		return sub2;
	}


	public void setSub2(String sub2) {
		this.sub2 = sub2;
	}


	public String getSub3() {
		return sub3;
	}


	public void setSub3(String sub3) {
		this.sub3 = sub3;
	}


	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getAmount1() {
		return amount1;
	}

	public void setAmount1(double amount1) {
		this.amount1 = amount1;
	}

	public double getAmount2() {
		return amount2;
	}

	public void setAmount2(double amount2) {
		this.amount2 = amount2;
	}

	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
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
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
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
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(String inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}
    
}
