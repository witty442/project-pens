package com.isecinc.pens.web.shipment;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.ShipmentConfirm;
import com.isecinc.pens.bean.ShipmentSummary;

/**
 * Shipment
 * 
 * @author Pasuwat Wang
 * @version $Id: ShipmentForm.java,v 1.0 14/10/2010 00:00:00 Exp $
 * 
 */
public class ShipmentForm extends I_Form {
	
	public ShipmentForm(){
		Factory factory = new Factory() {
			public Object create() {
				return new ShipmentConfirm();
			}
		};
		confirms = LazyList.decorate(new ArrayList<ShipmentConfirm>(), factory);
	}

	private static final long serialVersionUID = -427868075358525185L;
	
	private ShipmentCriteria shipmentCriteria = new ShipmentCriteria();

	private List<ShipmentConfirm> confirms =  null;

	private ShipmentConfirm[] results = null;
	
	private String confirmDate ;

	private ShipmentSummary summary = null;
	
	private int totalGroup;
	
	
	public int getTotalGroup() {
		return totalGroup;
	}

	public void setTotalGroup(int totalGroup) {
		this.totalGroup = totalGroup;
	}

	public ShipmentSummary getSummary() {
		return summary;
	}

	public void setSummary(ShipmentSummary summary) {
		this.summary = summary;
	}

	public ShipmentConfirm[] getResults() {
		return results;
	}

	public void setResults(ShipmentConfirm[] results) {
		this.results = results;
	}
	
	public ShipmentCriteria getCriteria() {
		return shipmentCriteria;
	}

	public void setCriteria(ShipmentCriteria criteria) {
		this.shipmentCriteria = criteria;
	}

	public ShipmentCriteria getShipmentCriteria() {
		return shipmentCriteria;
	}

	public void setShipmentCriteria(ShipmentCriteria shipmentCriteria) {
		this.shipmentCriteria = shipmentCriteria;
	}
	
	public ShipmentConfirm getShipment(){
		return this.shipmentCriteria.getShipment();
	}
	
	public void setShipment(ShipmentConfirm shipment){
		this.shipmentCriteria.setShipment(shipment);
	}

	public List<ShipmentConfirm> getConfirms() {
		return confirms;
	}

	public void setConfirms(List<ShipmentConfirm> confirms) {
		this.confirms = confirms;
	}

	public String getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
}
