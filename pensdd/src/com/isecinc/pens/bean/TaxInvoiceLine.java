package com.isecinc.pens.bean;

import com.isecinc.core.model.I_PO;

public class TaxInvoiceLine  extends I_PO {

	public TaxInvoiceLine(TaxInvoice taxinvoice) {
		this.setTaxInvoiceId(taxinvoice.getId());
	}
	
	protected void setDisplayLabel() throws Exception {
		
	}
	
	private int id;
	private int taxInvoiceId;
	private double totalAmt;
	private double vatAmt;
	private double linesAmt;
	private int qty;
	private String uomId;
	private int productId;
	private double price;
	private int orderLineId;
	private String description;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTaxInvoiceId() {
		return taxInvoiceId;
	}
	public void setTaxInvoiceId(int taxInvoiceId) {
		this.taxInvoiceId = taxInvoiceId;
	}
	public double getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}
	public double getVatAmt() {
		return vatAmt;
	}
	public void setVatAmt(double vatAmt) {
		this.vatAmt = vatAmt;
	}
	public void setVatAmt(){
		this.vatAmt = totalAmt - linesAmt;
	}
	public double getLinesAmt() {
		return linesAmt;
	}
	public void setLinesAmt(double linesAmt) {
		this.linesAmt = linesAmt;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getUomId() {
		return uomId;
	}
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(int orderLineId) {
		this.orderLineId = orderLineId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
