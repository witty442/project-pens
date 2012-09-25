package com.isecinc.pens.report.receivebag;

import java.io.Serializable;

/**
 * Receive Bag Report
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBagReport.java,v 1.0 03/12/2010 15:52:00 aneak.t Exp $
 * 
 */
public class ReceiveBagReport implements Serializable{

	private static final long serialVersionUID = -9078415324336388564L;

	private int id;
	private String shipmentDate;
	private String deliveryLine;
	private int orangeQty;
	private int berryQty;
	private int mixQty;
	private int bagQty;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getShipmentDate() {
		return shipmentDate;
	}
	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}
	public String getDeliveryLine() {
		return deliveryLine;
	}
	public void setDeliveryLine(String deliveryLine) {
		this.deliveryLine = deliveryLine;
	}
	public int getOrangeQty() {
		return orangeQty;
	}
	public void setOrangeQty(int orangeQty) {
		this.orangeQty = orangeQty;
	}
	public int getBerryQty() {
		return berryQty;
	}
	public void setBerryQty(int berryQty) {
		this.berryQty = berryQty;
	}
	public int getMixQty() {
		return mixQty;
	}
	public void setMixQty(int mixQty) {
		this.mixQty = mixQty;
	}
	public int getBagQty() {
		return bagQty;
	}
	public void setBagQty(int bagQty) {
		this.bagQty = bagQty;
	}
	
}
