package com.isecinc.pens.web.report.invoicepayment;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;

/**
 * Invoice Payment Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReportCriteria.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 7550373570730131526L;

	private InvoicePaymentReport invoicePaymentReport = new InvoicePaymentReport();

	private String fileType = SystemElements.PDF;
	
	public InvoicePaymentReport getInvoicePaymentReport() {
		return invoicePaymentReport;
	}

	public void setInvoicePaymentReport(InvoicePaymentReport invoicePaymentReport) {
		this.invoicePaymentReport = invoicePaymentReport;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
