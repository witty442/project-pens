package com.isecinc.pens.web.report.shipment;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.shipment.FormShipmentReport;

/**
 * Shipment Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportCriteria.java,v 1.0 10/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ShipmentFormCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 5052297366952650948L;

	private FormShipmentReport shipmentForm = new FormShipmentReport();

	private String fileType = SystemElements.EXCEL;
	
	public FormShipmentReport getShipmentForm() {
		return shipmentForm;
	}

	public void setShipmentForm(FormShipmentReport shipmentForm) {
		this.shipmentForm = shipmentForm;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
