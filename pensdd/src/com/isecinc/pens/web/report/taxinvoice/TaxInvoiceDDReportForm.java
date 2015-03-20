package com.isecinc.pens.web.report.taxinvoice;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceDDReport;

/**
 * Remittance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReportForm.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceDDReportForm extends I_Form{

	private static final long serialVersionUID = 9178825548990142310L;

	private TaxInvoiceDDReportCriteria criteria = new TaxInvoiceDDReportCriteria();

	public TaxInvoiceDDReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TaxInvoiceDDReportCriteria criteria) {
		this.criteria = criteria;
	}

	public TaxInvoiceDDReport getTaxInvoiceDDReport() {
		return criteria.getTaxInvoiceDDReport();
	}

	public void setTaxInvoiceDDReport(TaxInvoiceDDReport taxInvoiceDDReport) {
		criteria.setTaxInvoiceDDReport(taxInvoiceDDReport);
	}
}
