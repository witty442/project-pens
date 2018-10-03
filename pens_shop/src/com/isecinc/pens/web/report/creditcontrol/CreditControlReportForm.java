package com.isecinc.pens.web.report.creditcontrol;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;

/**
 * Detailed Sales Report Form.
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportForm.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditControlReportForm extends I_Form{

	private static final long serialVersionUID = -4562564368284660315L;

	private CreditControlReportCriteria criteria = new CreditControlReportCriteria();

	public CreditControlReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CreditControlReportCriteria criteria) {
		this.criteria = criteria;
	}

	public CreditControlReport getCreditControlReport() {
		return criteria.getCreditControlReport();
	}

	public void setCreditControlReport(CreditControlReport creditControlsReport) {
		criteria.setCreditControlReport(creditControlsReport);
	}
}
