package com.isecinc.pens.web.report.shipment;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.shipment.ShipmentReport;

/**
 * Shipment Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportCriteria.java,v 1.0 10/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ShipmentDetailReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 5052297366952650948L;

	private ShipmentReport shipmentReport = new ShipmentReport();

	private String fileType = SystemElements.EXCEL;
	
	public ShipmentReport getShipmentReport() {
		return shipmentReport;
	}

	public void setShipmentReport(ShipmentReport shipmentReport) {
		this.shipmentReport = shipmentReport;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
