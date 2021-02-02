package com.isecinc.pens.web.report.receivebag;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receivebag.ReceiveBagReport;

/**
 * Receive Bag Report Form
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBagReportForm.java,v 1.0 03/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiveBagReportForm extends I_Form{

	private static final long serialVersionUID = 1742588136302262472L;

	private ReceiveBagReportCriteria criteria = new ReceiveBagReportCriteria();

	public ReceiveBagReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReceiveBagReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiveBagReport getReceiveBagReport() {
		return criteria.getReceiveBagReport();
	}

	public void setReceiveBagReport(ReceiveBagReport receiveBagReport) {
		criteria.setReceiveBagReport(receiveBagReport);
	}
}
