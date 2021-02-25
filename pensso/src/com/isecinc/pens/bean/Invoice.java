package com.isecinc.pens.bean;

import java.util.List;

public class Invoice {

	private long invoiceId;
	private String invoiceNo;
	private String invoiceDate;
	private long invoiceTypeId;
	private String ctReference;
	private String orderType;
	private long billToCustomerId;
	private long billToSiteUseId;
	private long shipToCustomerId;
	private long shipToSiteUseId;
	private long termId;
	private String attribute1;
	private String attribute2;
	private String attribute3;
	private String attribute4;
	private String attribute5;
	private String attribute6;
	private String attribute7;
	private String attribute8;
	private String attribute9;
	private String attribute10;
	private String attribute11;
	private String attribute12;
	private String interfaceHeaderContext;
	private String interfaceHeaderAttribute1;
	private String interfaceHeaderAttribute2;
	private String status;
	private String classT;
	private String orderCategoryCode;
	private String refOrder;
	private String custPONumber;
	private String totalAmount;
	private String vatAmount;
	private String netAmount;
	private long primarySalesrepId;
	private List<InvoiceLine> invoiceLineList;
	private String statusMessage;

	
	public List<InvoiceLine> getInvoiceLineList() {
		return invoiceLineList;
	}

	public void setInvoiceLineList(List<InvoiceLine> invoiceLineList) {
		this.invoiceLineList = invoiceLineList;
	}

	public long getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(long invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getCtReference() {
		return ctReference;
	}

	public void setCtReference(String ctReference) {
		this.ctReference = ctReference;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public long getBillToCustomerId() {
		return billToCustomerId;
	}

	public void setBillToCustomerId(long billToCustomerId) {
		this.billToCustomerId = billToCustomerId;
	}

	public long getBillToSiteUseId() {
		return billToSiteUseId;
	}

	public void setBillToSiteUseId(long billToSiteUseId) {
		this.billToSiteUseId = billToSiteUseId;
	}

	public long getShipToCustomerId() {
		return shipToCustomerId;
	}

	public void setShipToCustomerId(long shipToCustomerId) {
		this.shipToCustomerId = shipToCustomerId;
	}

	public long getShipToSiteUseId() {
		return shipToSiteUseId;
	}

	public void setShipToSiteUseId(long shipToSiteUseId) {
		this.shipToSiteUseId = shipToSiteUseId;
	}

	public long getTermId() {
		return termId;
	}

	public void setTermId(long termId) {
		this.termId = termId;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}

	public String getAttribute4() {
		return attribute4;
	}

	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}

	public String getAttribute5() {
		return attribute5;
	}

	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5;
	}

	public String getAttribute6() {
		return attribute6;
	}

	public void setAttribute6(String attribute6) {
		this.attribute6 = attribute6;
	}

	public String getAttribute7() {
		return attribute7;
	}

	public void setAttribute7(String attribute7) {
		this.attribute7 = attribute7;
	}

	public String getAttribute8() {
		return attribute8;
	}

	public void setAttribute8(String attribute8) {
		this.attribute8 = attribute8;
	}

	public String getAttribute9() {
		return attribute9;
	}

	public void setAttribute9(String attribute9) {
		this.attribute9 = attribute9;
	}

	public String getAttribute10() {
		return attribute10;
	}

	public void setAttribute10(String attribute10) {
		this.attribute10 = attribute10;
	}

	public String getAttribute11() {
		return attribute11;
	}

	public void setAttribute11(String attribute11) {
		this.attribute11 = attribute11;
	}

	public String getAttribute12() {
		return attribute12;
	}

	public void setAttribute12(String attribute12) {
		this.attribute12 = attribute12;
	}

	public String getInterfaceHeaderContext() {
		return interfaceHeaderContext;
	}

	public void setInterfaceHeaderContext(String interfaceHeaderContext) {
		this.interfaceHeaderContext = interfaceHeaderContext;
	}

	public String getInterfaceHeaderAttribute1() {
		return interfaceHeaderAttribute1;
	}

	public void setInterfaceHeaderAttribute1(String interfaceHeaderAttribute1) {
		this.interfaceHeaderAttribute1 = interfaceHeaderAttribute1;
	}

	public String getInterfaceHeaderAttribute2() {
		return interfaceHeaderAttribute2;
	}

	public void setInterfaceHeaderAttribute2(String interfaceHeaderAttribute2) {
		this.interfaceHeaderAttribute2 = interfaceHeaderAttribute2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getClassT() {
		return classT;
	}

	public void setClassT(String classT) {
		this.classT = classT;
	}

	public String getOrderCategoryCode() {
		return orderCategoryCode;
	}

	public void setOrderCategoryCode(String orderCategoryCode) {
		this.orderCategoryCode = orderCategoryCode;
	}

	public String getRefOrder() {
		return refOrder;
	}

	public void setRefOrder(String refOrder) {
		this.refOrder = refOrder;
	}

	public String getCustPONumber() {
		return custPONumber;
	}

	public void setCustPONumber(String custPONumber) {
		this.custPONumber = custPONumber;
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

	public long getPrimarySalesrepId() {
		return primarySalesrepId;
	}

	public void setPrimarySalesrepId(long primarySalesrepId) {
		this.primarySalesrepId = primarySalesrepId;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
	
}
