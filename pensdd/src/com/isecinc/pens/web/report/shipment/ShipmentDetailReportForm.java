package com.isecinc.pens.web.report.shipment;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.shipment.ShipmentReport;

/**
 * Shipment Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ShipmentDetailReportForm extends I_Form{

	private static final long serialVersionUID = 7880870786572837611L;

	private ShipmentDetailReportCriteria criteria = new ShipmentDetailReportCriteria();
	
	public ShipmentDetailReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShipmentDetailReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ShipmentReport getShipmentReport() {
		return criteria.getShipmentReport();
	}

	public void setShipmentReport(ShipmentReport shipmentReport) {
		criteria.setShipmentReport(shipmentReport);
	}

}