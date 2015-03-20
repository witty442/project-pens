package com.isecinc.pens.web.report.shipment;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.shipment.TaxShipmentReport;

/**
 * Shipment Report Criteria
 * 
 * @version $Id: ShipmentReportCriteria.java,v 1.0 10/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class TaxShipmentCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 5052297366952650948L;

	private TaxShipmentReport taxShipment = new TaxShipmentReport();

	private String fileType = SystemElements.EXCEL;
		
	public TaxShipmentReport getTaxShipment() {
		return taxShipment;
	}

	public void setTaxShipment(TaxShipmentReport taxShipment) {
		this.taxShipment = taxShipment;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
