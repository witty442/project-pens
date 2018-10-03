package com.isecinc.pens.web.report.remittance;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.remittance.RemittanceReport;

/**
 * Remittance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReportForm.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class RemittanceReportForm extends I_Form{

	private static final long serialVersionUID = 9178825548990142310L;

	private RemittanceReportCriteria criteria = new RemittanceReportCriteria();

	public RemittanceReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(RemittanceReportCriteria criteria) {
		this.criteria = criteria;
	}

	public RemittanceReport getRemittanceReport() {
		return criteria.getRemittanceReport();
	}

	public void setRemittanceReport(RemittanceReport remittanceReport) {
		criteria.setRemittanceReport(remittanceReport);
	}
}
