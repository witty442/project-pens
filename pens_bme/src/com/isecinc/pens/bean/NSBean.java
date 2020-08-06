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
	private String nissinOrderDate;
	private String completeDate;
	
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
	private String customerSubType;
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
	private String cupNQty;
	
	private String poohQty;
	private String poohNQty;
	
	private String pac6CTNQty;//BAG6(CTN) =PAC_QTY
	private String pac6Qty;//BAG6 (BAG) = PAC_QTY_N
	
	private String interCTNQty;//BAG INTER (CTN)
	private String interBAGQty;//BAG INTER (BAG)
	
	private String pac10CTNQty;//BAG10(CTN) =PAC_QTY_CTN_10
	private String pac10Qty;// BAG10 (BAG) = PAC_QTY_10

	private String cup20CTNQty;//CUP20 72 CTN 
	private String cup20CUPQty;//CUP20 72 CUP
	
	private String kasi72CTNQty;//Kasi 72 CTN 
	private String kasi72BAGQty;//Kasi 72 BAG
	
	private String status;
	private String statusDesc;

	private String mode;
	private boolean canSave;
	private boolean canPensSave;
	private boolean canCancel;
	private String noPicRcv;
	private String orderType;
	private String salesZone;
	private String salesZoneDesc;
	
	private List<NSBean> items;
    private NSBean summary;
	
    
	public String getCup20CTNQty() {
		return cup20CTNQty;
	}

	public void setCup20CTNQty(String cup20ctnQty) {
		cup20CTNQty = cup20ctnQty;
	}

	public String getCup20CUPQty() {
		return cup20CUPQty;
	}

	public void setCup20CUPQty(String cup20cupQty) {
		cup20CUPQty = cup20cupQty;
	}

	public String getKasi72CTNQty() {
		return kasi72CTNQty;
	}

	public void setKasi72CTNQty(String kasi72ctnQty) {
		kasi72CTNQty = kasi72ctnQty;
	}

	public String getKasi72BAGQty() {
		return kasi72BAGQty;
	}

	public void setKasi72BAGQty(String kasi72bagQty) {
		kasi72BAGQty = kasi72bagQty;
	}

	public String getInterCTNQty() {
		return interCTNQty;
	}

	public void setInterCTNQty(String interCTNQty) {
		this.interCTNQty = interCTNQty;
	}

	public String getInterBAGQty() {
		return interBAGQty;
	}

	public void setInterBAGQty(String interBAGQty) {
		this.interBAGQty = interBAGQty;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	public String getNissinOrderDate() {
		return nissinOrderDate;
	}

	public void setNissinOrderDate(String nissinOrderDate) {
		this.nissinOrderDate = nissinOrderDate;
	}

	public String getSalesZoneDesc() {
		return salesZoneDesc;
	}

	public void setSalesZoneDesc(String salesZoneDesc) {
		this.salesZoneDesc = salesZoneDesc;
	}

	public String getSalesZone() {
		return salesZone;
	}

	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}

	public String getPac10CTNQty() {
		return pac10CTNQty;
	}

	public void setPac10CTNQty(String pac10ctnQty) {
		pac10CTNQty = pac10ctnQty;
	}

	public String getPac10Qty() {
		return pac10Qty;
	}

	public void setPac10Qty(String pac10Qty) {
		this.pac10Qty = pac10Qty;
	}

	public String getCustomerSubType() {
		return customerSubType;
	}

	public void setCustomerSubType(String customerSubType) {
		this.customerSubType = customerSubType;
	}

	public NSBean getSummary() {
		return summary;
	}

	public void setSummary(NSBean summary) {
		this.summary = summary;
	}

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

	public String getPac6Qty() {
		return pac6Qty;
	}

	public void setPac6Qty(String pac6Qty) {
		this.pac6Qty = pac6Qty;
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

	

	public String getPac6CTNQty() {
		return pac6CTNQty;
	}

	public void setPac6CTNQty(String pac6ctnQty) {
		pac6CTNQty = pac6ctnQty;
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
