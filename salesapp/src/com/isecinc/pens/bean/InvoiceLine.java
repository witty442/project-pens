package com.isecinc.pens.bean;

public class InvoiceLine {
	private long invoiceId;
	private long invoiceLineId;
	private long lineNumber;
	private String reasonCode;
	private long inventoryItemId;
	private String description;
	private String uomCode;
	private int quantityOrdered;
	private int quantityCredited;
	private int quantityInvoiced;
	private String unitStandardPrice;
	private String unitSellingPrice;
	private String salesOrder;
	private String salesOrderLine;
	private String salesOrderDate;
	private String interfaceLineContext;
	private String interfaceLineAttribute1;
	private String interfaceLineAttribute2;
	private String interfaceLineAttribute6;
	private String totalAmount;
	private String vatAmount;
	private String netAmount;
	
	private String statusMessage;

	
	public String getInterfaceLineAttribute2() {
		return interfaceLineAttribute2;
	}

	public void setInterfaceLineAttribute2(String interfaceLineAttribute2) {
		this.interfaceLineAttribute2 = interfaceLineAttribute2;
	}

	public String getInterfaceLineAttribute6() {
		return interfaceLineAttribute6;
	}

	public void setInterfaceLineAttribute6(String interfaceLineAttribute6) {
		this.interfaceLineAttribute6 = interfaceLineAttribute6;
	}

	public long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public long getInvoiceLineId() {
		return invoiceLineId;
	}

	public void setInvoiceLineId(long invoiceLineId) {
		this.invoiceLineId = invoiceLineId;
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public long getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(long inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUomCode() {
		return uomCode;
	}

	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	public int getQuantityOrdered() {
		return quantityOrdered;
	}

	public void setQuantityOrdered(int quantityOrdered) {
		this.quantityOrdered = quantityOrdered;
	}

	public int getQuantityCredited() {
		return quantityCredited;
	}

	public void setQuantityCredited(int quantityCredited) {
		this.quantityCredited = quantityCredited;
	}

	public int getQuantityInvoiced() {
		return quantityInvoiced;
	}

	public void setQuantityInvoiced(int quantityInvoiced) {
		this.quantityInvoiced = quantityInvoiced;
	}

	

	public String getUnitStandardPrice() {
		return unitStandardPrice;
	}

	public void setUnitStandardPrice(String unitStandardPrice) {
		this.unitStandardPrice = unitStandardPrice;
	}

	public String getUnitSellingPrice() {
		return unitSellingPrice;
	}

	public void setUnitSellingPrice(String unitSellingPrice) {
		this.unitSellingPrice = unitSellingPrice;
	}

	public String getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(String salesOrder) {
		this.salesOrder = salesOrder;
	}

	public String getSalesOrderLine() {
		return salesOrderLine;
	}

	public void setSalesOrderLine(String salesOrderLine) {
		this.salesOrderLine = salesOrderLine;
	}

	public String getSalesOrderDate() {
		return salesOrderDate;
	}

	public void setSalesOrderDate(String salesOrderDate) {
		this.salesOrderDate = salesOrderDate;
	}

	public String getInterfaceLineContext() {
		return interfaceLineContext;
	}

	public void setInterfaceLineContext(String interfaceLineContext) {
		this.interfaceLineContext = interfaceLineContext;
	}

	public String getInterfaceLineAttribute1() {
		return interfaceLineAttribute1;
	}

	public void setInterfaceLineAttribute1(String interfaceLineAttribute1) {
		this.interfaceLineAttribute1 = interfaceLineAttribute1;
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

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	
}
