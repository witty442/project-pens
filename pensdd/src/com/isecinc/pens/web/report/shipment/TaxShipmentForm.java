package com.isecinc.pens.web.report.shipment;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.shipment.TaxShipmentReport;

/**
 * Shipment Report Form Class
 * 
 * @author Pasuwat.W
 * 
 */

public class TaxShipmentForm extends I_Form{

	private static final long serialVersionUID = 7880870786572837611L;

	private TaxShipmentCriteria criteria = new TaxShipmentCriteria();

	public TaxShipmentCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TaxShipmentCriteria criteria) {
		this.criteria = criteria;
	}

	public TaxShipmentReport getTaxShipment() {
		return criteria.getTaxShipment();
	}

	public void setTaxShipment(TaxShipmentReport taxShipment) {
		criteria.setTaxShipment(taxShipment);
	}

}
