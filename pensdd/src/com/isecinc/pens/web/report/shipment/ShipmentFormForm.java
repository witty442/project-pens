package com.isecinc.pens.web.report.shipment;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.shipment.FormShipmentReport;

/**
 * Shipment Report Form Class
 * 
 * @author Pasuwat.W
 * 
 */

public class ShipmentFormForm extends I_Form{

	private static final long serialVersionUID = 7880870786572837611L;

	private ShipmentFormCriteria criteria = new ShipmentFormCriteria();
	
	public ShipmentFormCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShipmentFormCriteria criteria) {
		this.criteria = criteria;
	}

	public FormShipmentReport getFormShipment() {
		return criteria.getShipmentForm();
	}

	public void setFormShipment(FormShipmentReport formShipment) {
		criteria.setShipmentForm(formShipment);
	}

}
