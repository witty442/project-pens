package com.isecinc.pens.web.report.shipmenttemp;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.shipmenttemp.ShipmentTempReport;

/**
 * Shipment Temporary Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ShipmentTempReportCriteria.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ShipmentTempReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -1132847752468586372L;

	private ShipmentTempReport shipmentTempReport = new ShipmentTempReport();

	private String fileType = SystemElements.EXCEL;
	
	public ShipmentTempReport getShipmentTempReport() {
		return shipmentTempReport;
	}

	public void setShipmentTempReport(ShipmentTempReport shipmentTempReport) {
		this.shipmentTempReport = shipmentTempReport;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
