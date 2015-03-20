package com.isecinc.pens.bean;

import java.util.List;

import com.isecinc.core.model.I_PO;

/************
 * 
 * 
 * */
public class Shipment  extends I_PO {

	protected void setDisplayLabel() throws Exception {
		
	}
	
	public Shipment(){
		
	}
	
	public Shipment(TaxInvoice tax){
		this.setShipmentNo(tax.getTaxInvoiceNo());
		this.setTaxInvoiceId(tax.getId());
		this.setOrderId(tax.getOrderId());
		this.setShipmentDate(tax.getTaxInvoiceDate());
		this.setShipmentStats(tax.getTaxInvoiceStatus());
		this.setLineAmt(tax.getLinesAmt());
		this.setTotalAmt(tax.getTotalAmt());
		this.setVatAmt(tax.getVatAmt());
		this.setDescription(tax.getDescription());
	}
	
	private int id ;
	private String shipmentNo ;
	private String shipmentDate;
	private double totalAmt;
	private double vatAmt;
	private double lineAmt;
	private String shipmentStats;
	private int orderId;
	private int taxInvoiceId;
	private String description;
	private List<ShipmentLine> shipmentLineList;
	
	public List<ShipmentLine> getShipmentLineList() {
		return shipmentLineList;
	}

	public void setShipmentLineList(List<ShipmentLine> shipmentLineList) {
		this.shipmentLineList = shipmentLineList;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getShipmentNo() {
		return shipmentNo;
	}
	public void setShipmentNo(String shipmentNo) {
		this.shipmentNo = shipmentNo;
	}
	public String getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
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
	public double getLineAmt() {
		return lineAmt;
	}
	public void setLineAmt(double lineAmt) {
		this.lineAmt = lineAmt;
	}
	public String getShipmentStats() {
		return shipmentStats;
	}
	public void setShipmentStats(String shipmentStats) {
		this.shipmentStats = shipmentStats;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getTaxInvoiceId() {
		return taxInvoiceId;
	}
	public void setTaxInvoiceId(int taxInvoiceId) {
		this.taxInvoiceId = taxInvoiceId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getShipmentDateMonth(){
		if(shipmentDate == null)
			return null;
		return shipmentDate.substring(3, 5);
	}
	public String getShipmentDateYear(){
		if(shipmentDate == null)
			return null;
		
		return shipmentDate.substring(8);
	}
}
