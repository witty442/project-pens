package com.isecinc.pens.web.report.shipmenttemp;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.shipmenttemp.ShipmentTempReport;

/**
 * Shipment Temporary Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: ShipmentTempReportForm.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ShipmentTempReportForm extends I_Form{

	private static final long serialVersionUID = 6991626688629445942L;

	private ShipmentTempReportCriteria criteria = new ShipmentTempReportCriteria();

	public ShipmentTempReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShipmentTempReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ShipmentTempReport getShipmentTempReport() {
		return criteria.getShipmentTempReport();
	}

	public void setShipmentTempReport(ShipmentTempReport shipmentTempReport) {
		criteria.setShipmentTempReport(shipmentTempReport);
	}

}
