package com.isecinc.pens.web.report.shipmentbenecol;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.shipmentbenecol.ShipmentBenecolReport;

/**
 * Shipment Benecol Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: ShipmentBenecolReportForm.java,v 1.0 17/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ShipmentBenecolReportForm extends I_Form{

	private static final long serialVersionUID = 1989008438705440936L;
	
	private ShipmentBenecolReportCriteria criteria = new ShipmentBenecolReportCriteria();
	
	public ShipmentBenecolReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShipmentBenecolReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ShipmentBenecolReport getShipmentBenecolReport() {
		return criteria.getShipmentBenecolReport();
	}

	public void setShipmentBenecolReport(ShipmentBenecolReport shipmentBenecolReport) {
		criteria.setShipmentBenecolReport(shipmentBenecolReport);
	}

}
