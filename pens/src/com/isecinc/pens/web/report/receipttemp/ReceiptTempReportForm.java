package com.isecinc.pens.web.report.receipttemp;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receipttemp.ReceiptTempReport;

/**
 * Shipment Temporary Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: ReceiptTempReportForm.java,v 1.0 17/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ReceiptTempReportForm extends I_Form{

	private static final long serialVersionUID = -7909770591120511051L;

	private ReceiptTempReportCriteria criteria = new ReceiptTempReportCriteria();

	public ReceiptTempReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReceiptTempReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiptTempReport getReceiptTempReport() {
		return criteria.getReceiptTempReport();
	}

	public void setReceiptTempReport(ReceiptTempReport receiptTempReport) {
		criteria.setReceiptTempReport(receiptTempReport);
	}

}
