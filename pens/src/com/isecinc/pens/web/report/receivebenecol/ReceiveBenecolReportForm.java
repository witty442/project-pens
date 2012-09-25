package com.isecinc.pens.web.report.receivebenecol;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receivebenecol.ReceiveBenecolReport;

/**
 * Receive Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBenecolReportForm.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ReceiveBenecolReportForm extends I_Form{

	private static final long serialVersionUID = 2934493004564713297L;

	private ReceiveBenecolReportCriteria criteria = new ReceiveBenecolReportCriteria();

	public ReceiveBenecolReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReceiveBenecolReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiveBenecolReport getReceiveBenecolReport() {
		return criteria.getReceiveBenecolReport();
	}

	public void setReceiveBenecolReport(ReceiveBenecolReport receiveBenecolReport) {
		criteria.setReceiveBenecolReport(receiveBenecolReport);
	}
}
