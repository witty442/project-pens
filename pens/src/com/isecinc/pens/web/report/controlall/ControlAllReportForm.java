package com.isecinc.pens.web.report.controlall;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.detailedsales.DetailedSalesReport;

/**
 * Detailed Sales Report Form.
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportForm.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class ControlAllReportForm extends I_Form{

	private static final long serialVersionUID = -4562564368284660315L;

	private ControlAllReportCriteria criteria = new ControlAllReportCriteria();

	public ControlAllReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ControlAllReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ControlAllReport getControlAllReport() {
		return criteria.getControlAllReport();
	}

	public void setControlAllReport(ControlAllReport creditControlsReport) {
		criteria.setControlAllReport(creditControlsReport);
	}
}
