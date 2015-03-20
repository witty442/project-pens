package com.isecinc.pens.web.report.detailedsales;

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

public class DetailedSalesReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -5396491124912228229L;

	private DetailedSalesReport detailedSalesReport = new DetailedSalesReport();
	
	private String fileType = SystemElements.PDF;

	public DetailedSalesReport getDetailedSalesReport() {
		return detailedSalesReport;
	}

	public void setDetailedSalesReport(DetailedSalesReport detailedSalesReport) {
		this.detailedSalesReport = detailedSalesReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}
