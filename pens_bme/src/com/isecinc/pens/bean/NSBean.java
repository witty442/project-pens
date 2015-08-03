package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.List;

public class NSBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1011918568953216669L;

	private String orderId;
	private String orderDate;
	
	private String orderDateFrom;
	private String orderDateTo;
	
	private String createUser;
	private String updateUser;
	private String channelId;
	private String provinceId;
	private String channelName;
	private String provinceName;
	
	private String customerCode;
	private String customerType;
	private String customerName;
	private String addressLine1;
	private String addressLine2;
	private String phone;
	private String remark;
	private String pendingReason;
	
	private String invoiceDate;
	private String invoiceNo;
	private String saleCode;

	private String cupQty;
	private String pacQty;
	private String poohQty;
	
	private String cupNQty;
	private String pacNQty;
	private String poohNQty;
	
	private String status;
	private String statusDesc;

	private String mode;
	private boolean canSave;
	private boolean canPensSave;
	private boolean canCancel;
	private String noPicRcv;
	private String orderType;
	
	private List<NSBean> items;

	
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getPendingReason() {
		return pendingReason;
	}

	public void setPendingReason(String pendingReason) {
		this.pendingReason = pendingReason;
	}

	public String getCupNQty() {
		return cupNQty;
	}

	public void setCupNQty(String cupNQty) {
		this.cupNQty = cupNQty;
	}

	public String getPacNQty() {
		return pacNQty;
	}

	public void setPacNQty(String pacNQty) {
		this.pacNQty = pacNQty;
	}

	public String getPoohNQty() {
		return poohNQty;
	}

	public void setPoohNQty(String poohNQty) {
		this.poohNQty = poohNQty;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getSaleCode() {
		return saleCode;
	}

	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}

	public String getCupQty() {
		return cupQty;
	}

	public void setCupQty(String cupQty) {
		this.cupQty = cupQty;
	}

	public String getPacQty() {
		return pacQty;
	}

	public void setPacQty(String pacQty) {
		this.pacQty = pacQty;
	}

	public String getPoohQty() {
		return poohQty;
	}

	public void setPoohQty(String poohQty) {
		this.poohQty = poohQty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public boolean isCanSave() {
		return canSave;
	}

	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}

	public boolean isCanPensSave() {
		return canPensSave;
	}

	public void setCanPensSave(boolean canPensSave) {
		this.canPensSave = canPensSave;
	}

	public boolean isCanCancel() {
		return canCancel;
	}

	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}

	public String getNoPicRcv() {
		return noPicRcv;
	}

	public void setNoPicRcv(String noPicRcv) {
		this.noPicRcv = noPicRcv;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public List<NSBean> getItems() {
		return items;
	}

	public void setItems(List<NSBean> items) {
		this.items = items;
	}

	
}
