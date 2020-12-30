package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class OrderEDIBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2252606206581569871L;
	private String rType;/** type edit ,M:MANUAL **/
	private String orderDateFrom;
	private String orderDateTo;
	
	private String pickingNo;
	private String orderNo;
	private String orderDate;
	private String deliveryDate;
	private long customerId;
	private String customerCode;
	private String customerName;
	private String totalAmount;
	private String vatAmount;
	private String netAmount;
	private String status;
	private String docStatus;
	private String docStatusDesc;
	private long headerId;
	private String lineId;
	private int no;
	private String productCode;
	private String productId;
	private String productName;
	private String uom;
	private String unitPrice;
	private String lineAmount;
	private String qty;
	private StringBuffer dataStrBuffer;
	private String userName;
	private int userId;
	private List<OrderEDIBean> itemsList;
	private String deleteLineIds;
	private String rowStyle;
	private String rowReadonly;
	private String shipToAddress;
	private String shipToAddressId;
	private String billToAddressId;
	private long shipToSiteUseId;
	private long billToSiteUseId;
	private long reservationId;
	private boolean canEdit;
	private String barcode;
	private String shipToEanLocCode;
	
	 
	public String getShipToEanLocCode() {
		return shipToEanLocCode;
	}
	public void setShipToEanLocCode(String shipToEanLocCode) {
		this.shipToEanLocCode = shipToEanLocCode;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public long getShipToSiteUseId() {
		return shipToSiteUseId;
	}
	public void setShipToSiteUseId(long shipToSiteUseId) {
		this.shipToSiteUseId = shipToSiteUseId;
	}
	public long getBillToSiteUseId() {
		return billToSiteUseId;
	}
	public void setBillToSiteUseId(long billToSiteUseId) {
		this.billToSiteUseId = billToSiteUseId;
	}
	public String getBillToAddressId() {
		return billToAddressId;
	}
	public void setBillToAddressId(String billToAddressId) {
		this.billToAddressId = billToAddressId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	public String getShipToAddressId() {
		return shipToAddressId;
	}
	public void setShipToAddressId(String shipToAddressId) {
		this.shipToAddressId = shipToAddressId;
	}
	public String getrType() {
		return rType;
	}
	public void setrType(String rType) {
		this.rType = rType;
	}
	public String getDocStatusDesc() {
		return docStatusDesc;
	}
	public void setDocStatusDesc(String docStatusDesc) {
		this.docStatusDesc = docStatusDesc;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public String getShipToAddress() {
		return shipToAddress;
	}
	public void setShipToAddress(String shipToAddress) {
		this.shipToAddress = shipToAddress;
	}
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
	public String getRowReadonly() {
		return rowReadonly;
	}
	public void setRowReadonly(String rowReadonly) {
		this.rowReadonly = rowReadonly;
	}
	public String getDeleteLineIds() {
		return deleteLineIds;
	}
	public void setDeleteLineIds(String deleteLineIds) {
		this.deleteLineIds = deleteLineIds;
	}
	public long getHeaderId() {
		return headerId;
	}
	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
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
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getLineAmount() {
		return lineAmount;
	}
	public void setLineAmount(String lineAmount) {
		this.lineAmount = lineAmount;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getVatAmount() {
		return vatAmount;
	}
	public void setVatAmount(String vatAmount) {
		this.vatAmount = vatAmount;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPickingNo() {
		return pickingNo;
	}
	public void setPickingNo(String pickingNo) {
		this.pickingNo = pickingNo;
	}
	public List<OrderEDIBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<OrderEDIBean> items) {
		this.itemsList = items;
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
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
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
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	
	
}
