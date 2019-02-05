package com.isecinc.pens.web.report.transfer;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class BankTransferReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private BankTransferReportCriteria criteria = new BankTransferReportCriteria();
	
	public BankTransferReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(BankTransferReportCriteria criteria) {
		this.criteria = criteria;
	}

	public BankTransferReport getPerformanceReport() {
		return criteria.getPerformanceReport();
	}

	public void setPerformanceReport(BankTransferReport performanceReport) {
		criteria.setPerformanceReport(performanceReport);
	}
	
}
