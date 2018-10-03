package com.isecinc.pens.web.report.invoicepayment_all;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportCriteria.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentAllReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private InvoicePaymentAllReport performanceReport = new InvoicePaymentAllReport();

	private String fileType = SystemElements.PDF;
	
	public InvoicePaymentAllReport getInvoicePaymentAllReport() {
		return performanceReport;
	}

	public void setInvoicePaymentAllReport(InvoicePaymentAllReport performanceReport) {
		this.performanceReport = performanceReport;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
