package com.isecinc.pens.web.report.shipmentbenecol;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.shipmentbenecol.ShipmentBenecolReport;

/**
 * Shipment Benecol Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ShipmentBenecolReportCriteria.java,v 1.0 17/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentBenecolReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 4807517556702451035L;

	private ShipmentBenecolReport shipmentBenecolReport = new ShipmentBenecolReport();

	private String fileType = SystemElements.EXCEL;
	
	public ShipmentBenecolReport getShipmentBenecolReport() {
		return shipmentBenecolReport;
	}

	public void setShipmentBenecolReport(ShipmentBenecolReport shipmentBenecolReport) {
		this.shipmentBenecolReport = shipmentBenecolReport;
	}
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
