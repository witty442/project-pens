package com.isecinc.pens.bean;

import com.isecinc.core.model.I_PO;

public class ShipmentLine  extends I_PO {

	protected void setDisplayLabel() throws Exception {
		
	}
	
	public ShipmentLine(Shipment shipment){
		this.setShipmentId(shipment.getId());
	}
	
	public ShipmentLine(TaxInvoiceLine line,Shipment shipment){
		this.setShipmentId(shipment.getId());
		this.setDescription(line.getDescription());
		this.setLinesAmt(line.getLinesAmt());
		this.setOrderLineId(line.getOrderLineId());
		this.setProductId(line.getProductId());
		this.setQty(line.getQty());
		this.setTotalAmt(line.getTotalAmt());
		this.setVatAmt(line.getVatAmt());
		this.setUomId(line.getUomId());
	}
	
	private int id ;
	private int shipmentId;
	private double totalAmt;
	private double vatAmt;
	private double linesAmt;
	private double qty;
	private String uomId;
	private int orderLineId;
	private int productId;
	private String description;
	//optional
	private ShipmentConfirm confirmLine;
	
    
	public ShipmentConfirm getConfirmLine() {
		return confirmLine;
	}

	public void setConfirmLine(ShipmentConfirm confirmLine) {
		this.confirmLine = confirmLine;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getShipmentId() {
		return shipmentId;
	}
	public void setShipmentId(int shipmentId) {
		this.shipmentId = shipmentId;
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
	public void setVatAmt() {
		this.vatAmt = totalAmt - linesAmt;
	}
	public double getQty() {
		return qty;
	}
	public void setQty(double qty) {
		this.qty = qty;
	}
	public String getUomId() {
		return uomId;
	}
	public void setUomId(String uomId) {
		this.uomId = uomId;
	}
	public int getOrderLineId() {
		return orderLineId;
	}
	public void setOrderLineId(int orderLineId) {
		this.orderLineId = orderLineId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getLinesAmt() {
		return linesAmt;
	}
	public void setLinesAmt(double linesAmt) {
		this.linesAmt = linesAmt;
	}
}
