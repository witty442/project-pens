package com.isecinc.pens.web.report.cheque;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.cheque.ChequeReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportCriteria.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ChequeReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private ChequeReport chequeReport = new ChequeReport();

	private String fileType = SystemElements.PDF;
	
	public ChequeReport getChequeReport() {
		return chequeReport;
	}

	public void setChequeReport(ChequeReport chequeReport) {
		this.chequeReport = chequeReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
