package com.isecinc.pens.web.report.invoicepayment_all;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentAllReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private InvoicePaymentAllReportCriteria criteria = new InvoicePaymentAllReportCriteria();
	
	public InvoicePaymentAllReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(InvoicePaymentAllReportCriteria criteria) {
		this.criteria = criteria;
	}

	public InvoicePaymentAllReport getInvoicePaymentAllReport() {
		return criteria.getInvoicePaymentAllReport();
	}

	public void setInvoicePaymentAllReport(InvoicePaymentAllReport performanceReport) {
		criteria.setInvoicePaymentAllReport(performanceReport);
	}
	
}
