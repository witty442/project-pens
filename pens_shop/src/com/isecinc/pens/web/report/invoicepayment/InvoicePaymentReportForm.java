package com.isecinc.pens.web.report.invoicepayment;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;

/**
 * Invoice Payment Report Form
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReportForm.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentReportForm extends I_Form{

	private static final long serialVersionUID = -5594905467948006348L;

	private InvoicePaymentReportCriteria criteria = new InvoicePaymentReportCriteria();

	public InvoicePaymentReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(InvoicePaymentReportCriteria criteria) {
		this.criteria = criteria;
	}

	public InvoicePaymentReport getInvoicePaymentReport() {
		return criteria.getInvoicePaymentReport();
	}

	public void setInvoicePaymentReport(InvoicePaymentReport invoicePaymentReport) {
		criteria.setInvoicePaymentReport(invoicePaymentReport);
	}

}
