package com.isecinc.pens.web.report.invoicedetail;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InvoiceDetailReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private InvoiceDetailReportCriteria criteria = new InvoiceDetailReportCriteria();
	
	public InvoiceDetailReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(InvoiceDetailReportCriteria criteria) {
		this.criteria = criteria;
	}

	public InvoiceDetailReport getInvoiceDetailReport() {
		return criteria.getInvoiceDetailReport();
	}

	public void setInvoiceDetailReport(InvoiceDetailReport performanceReport) {
		criteria.setInvoiceDetailReport(performanceReport);
	}
	
}
