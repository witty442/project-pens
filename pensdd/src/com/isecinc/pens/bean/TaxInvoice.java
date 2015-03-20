package com.isecinc.pens.bean;

import java.util.List;

import com.isecinc.core.model.I_PO;

/************
 * 
 * 
 * */
public class TaxInvoice  extends I_PO {

	protected void setDisplayLabel() throws Exception {
		
	}
	
	private int id;
	private String taxInvoiceNo;
	private String taxInvoiceDate;
	private double totalAmt;
	private double vatAmt;
	private double linesAmt;
	private String taxInvoiceStatus;
	private int orderId;
	private String description;
	private List<TaxInvoice> taxInvoiceList;
	private List<TaxInvoiceLine> taxInvoiceLineList;
	private int tripNo;
	
	
	public int getTripNo() {
		return tripNo;
	}
	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}
	public List<TaxInvoice> getTaxInvoiceList() {
		return taxInvoiceList;
	}
	public void setTaxInvoiceList(List<TaxInvoice> taxInvoiceList) {
		this.taxInvoiceList = taxInvoiceList;
	}
	public List<TaxInvoiceLine> getTaxInvoiceLineList() {
		return taxInvoiceLineList;
	}
	public void setTaxInvoiceLineList(List<TaxInvoiceLine> taxInvoiceLineList) {
		this.taxInvoiceLineList = taxInvoiceLineList;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTaxInvoiceNo() {
		return taxInvoiceNo;
	}
	public void setTaxInvoiceNo(String taxInvoiceNo) {
		this.taxInvoiceNo = taxInvoiceNo;
	}
	public String getTaxInvoiceDate() {
		return taxInvoiceDate;
	}
	public void setTaxInvoiceDate(String taxInvoiceDate) {
		this.taxInvoiceDate = taxInvoiceDate;
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
	public double getLinesAmt() {
		return linesAmt;
	}
	public void setLinesAmt(double linesAmt) {
		this.linesAmt = linesAmt;
	}
	public String getTaxInvoiceStatus() {
		return taxInvoiceStatus;
	}
	public void setTaxInvoiceStatus(String taxInvoiceStatus) {
		this.taxInvoiceStatus = taxInvoiceStatus;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTaxInvoiceDateMonth(){
		if(taxInvoiceDate == null)
			return null;
		return taxInvoiceDate.substring(3, 5);
	}
	public String getTaxInvoiceDateYear(){
		if(taxInvoiceDate == null)
			return null;
		
		return taxInvoiceDate.substring(8);
	}
}
