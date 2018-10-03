package com.isecinc.pens.web.report.performance;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.performance.PerformanceReport;

/**
 * Performance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class PerformanceReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private PerformanceReportCriteria criteria = new PerformanceReportCriteria();
	
	public PerformanceReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(PerformanceReportCriteria criteria) {
		this.criteria = criteria;
	}

	public PerformanceReport getPerformanceReport() {
		return criteria.getPerformanceReport();
	}

	public void setPerformanceReport(PerformanceReport performanceReport) {
		criteria.setPerformanceReport(performanceReport);
	}
	
}
