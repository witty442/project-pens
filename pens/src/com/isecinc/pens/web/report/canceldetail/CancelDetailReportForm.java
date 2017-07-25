package com.isecinc.pens.web.report.canceldetail;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.canceldetail.CancelDetailReport;
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

public class CancelDetailReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private CancelDetailReportCriteria criteria = new CancelDetailReportCriteria();
	
	public CancelDetailReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CancelDetailReportCriteria criteria) {
		this.criteria = criteria;
	}

	public CancelDetailReport getCancelDetailReport() {
		return criteria.getCancelDetailReport();
	}

	public void setCancelDetailReport(CancelDetailReport performanceReport) {
		criteria.setCancelDetailReport(performanceReport);
	}
	
}
