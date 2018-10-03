package com.isecinc.pens.web.report.cheque;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.cheque.ChequeReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ChequeReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private ChequeReportCriteria criteria = new ChequeReportCriteria();
	
	public ChequeReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ChequeReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ChequeReport getChequeReport() {
		return criteria.getChequeReport();
	}

	public void setChequeReport(ChequeReport chequeReport) {
		criteria.setChequeReport(chequeReport);
	}
	
}
