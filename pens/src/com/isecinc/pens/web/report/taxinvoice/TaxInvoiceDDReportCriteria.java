package com.isecinc.pens.web.report.taxinvoice;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceDDReport;


/**
 * Remittance Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReportCriteria.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceDDReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -6984059724190714094L;

	private TaxInvoiceDDReport taxInvoiceDDReport = new TaxInvoiceDDReport();
	
	public TaxInvoiceDDReport getTaxInvoiceDDReport() {
		return taxInvoiceDDReport;
	}

	public void setTaxInvoiceDDReport(TaxInvoiceDDReport taxInvoiceDDReport) {
		this.taxInvoiceDDReport = taxInvoiceDDReport;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String fileType = SystemElements.EXCEL;



	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
