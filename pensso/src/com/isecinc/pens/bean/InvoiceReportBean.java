package com.isecinc.pens.bean;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class InvoiceReportBean implements Serializable  {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5066534727878051512L;
	private String transactionDate;
	private String transactionDateFrom;
	private String transactionDateTo;
	private String regionCri;
	private String provinceCri;
	private String amphurCri;
	private String pickingNo;
	private String status;
	private String orderDateFrom;
	private String orderDateTo;
	private String orderNo;
	private String orderDate;
	private String customerCode;
	private String customerName;
	private String amphur;
	private String province;
	private String salesrepCode;
	private String salesrepName;
	private String productCode;
	private String productName;
	private String uom;
	
	private String totalAmount;
	private String vatAmount;
	private String netAmount;
	
	private String remainTotalAmount;
	private String remainVatAmount;
	private String remainNetAmount;
	
	private String invoiceNo;
	private String invoiceFlag;
	private String invoiceDate;
	private String invoiceType;
	private String invoiceRef;
	private String addressShipTo;
	private String addressBillTo;
	private String mobile;
	private StringBuffer dataStrBuffer;
	private StringBuffer rowTotalStrBuffer;
	private String selectedOrderNo;
	private List<InvoiceReportBean> itemsList;
	private String userName;
	private String poNumber;
	private String alternateName;
	
	private String territory;
	private String searchProvince;
	private String district;
	private String dispHaveRemain;
	
	
	public String getDispHaveRemain() {
		return dispHaveRemain;
	}
	public void setDispHaveRemain(String dispHaveRemain) {
		this.dispHaveRemain = dispHaveRemain;
	}
	public String getRemainTotalAmount() {
		return remainTotalAmount;
	}
	public void setRemainTotalAmount(String remainTotalAmount) {
		this.remainTotalAmount = remainTotalAmount;
	}
	public String getRemainVatAmount() {
		return remainVatAmount;
	}
	public void setRemainVatAmount(String remainVatAmount) {
		this.remainVatAmount = remainVatAmount;
	}
	
	public String getRemainNetAmount() {
		return remainNetAmount;
	}
	public void setRemainNetAmount(String remainNetAmount) {
		this.remainNetAmount = remainNetAmount;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getTransactionDateFrom() {
		return transactionDateFrom;
	}
	public void setTransactionDateFrom(String transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}
	public String getTransactionDateTo() {
		return transactionDateTo;
	}
	public void setTransactionDateTo(String transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}
	public String getRegionCri() {
		return regionCri;
	}
	public void setRegionCri(String regionCri) {
		this.regionCri = regionCri;
	}
	public String getProvinceCri() {
		return provinceCri;
	}
	public void setProvinceCri(String provinceCri) {
		this.provinceCri = provinceCri;
	}
	public String getAmphurCri() {
		return amphurCri;
	}
	public void setAmphurCri(String amphurCri) {
		this.amphurCri = amphurCri;
	}
	public String getPickingNo() {
		return pickingNo;
	}
	public void setPickingNo(String pickingNo) {
		this.pickingNo = pickingNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	public String getAmphur() {
		return amphur;
	}
	public void setAmphur(String amphur) {
		this.amphur = amphur;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
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
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
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
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getInvoiceFlag() {
		return invoiceFlag;
	}
	public void setInvoiceFlag(String invoiceFlag) {
		this.invoiceFlag = invoiceFlag;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getInvoiceRef() {
		return invoiceRef;
	}
	public void setInvoiceRef(String invoiceRef) {
		this.invoiceRef = invoiceRef;
	}
	public String getAddressShipTo() {
		return addressShipTo;
	}
	public void setAddressShipTo(String addressShipTo) {
		this.addressShipTo = addressShipTo;
	}
	public String getAddressBillTo() {
		return addressBillTo;
	}
	public void setAddressBillTo(String addressBillTo) {
		this.addressBillTo = addressBillTo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
	}
	public StringBuffer getRowTotalStrBuffer() {
		return rowTotalStrBuffer;
	}
	public void setRowTotalStrBuffer(StringBuffer rowTotalStrBuffer) {
		this.rowTotalStrBuffer = rowTotalStrBuffer;
	}
	public String getSelectedOrderNo() {
		return selectedOrderNo;
	}
	public void setSelectedOrderNo(String selectedOrderNo) {
		this.selectedOrderNo = selectedOrderNo;
	}
	public List<InvoiceReportBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<InvoiceReportBean> itemsList) {
		this.itemsList = itemsList;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getAlternateName() {
		return alternateName;
	}
	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}
	public String getTerritory() {
		return territory;
	}
	public void setTerritory(String territory) {
		this.territory = territory;
	}
	public String getSearchProvince() {
		return searchProvince;
	}
	public void setSearchProvince(String searchProvince) {
		this.searchProvince = searchProvince;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	
	
}
