package com.isecinc.pens.web.report.detailedsales;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;

/**
 * Detailed Sales Report Form.
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportForm.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class DetailedSalesReportForm extends I_Form{

	private static final long serialVersionUID = -4562564368284660315L;

	private DetailedSalesReportCriteria criteria = new DetailedSalesReportCriteria();

	public DetailedSalesReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(DetailedSalesReportCriteria criteria) {
		this.criteria = criteria;
	}

	public DetailedSalesReport getDetailedSalesReport() {
		return criteria.getDetailedSalesReport();
	}

	public void setDetailedSalesReport(DetailedSalesReport detailedSalesReport) {
		criteria.setDetailedSalesReport(detailedSalesReport);
	}
}
