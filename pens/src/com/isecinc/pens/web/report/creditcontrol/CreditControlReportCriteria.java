package com.isecinc.pens.web.report.creditcontrol;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;

/**
 * Detailed Sales Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportCriteria.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditControlReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -5396491124912228229L;

	private CreditControlReport creditControlReport = new CreditControlReport();
	
	private String fileType = SystemElements.PDF;

	
	public CreditControlReport getCreditControlReport() {
		return creditControlReport;
	}

	public void setCreditControlReport(CreditControlReport creditControlReport) {
		this.creditControlReport = creditControlReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}
