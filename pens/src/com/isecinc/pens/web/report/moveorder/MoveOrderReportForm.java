package com.isecinc.pens.web.report.moveorder;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.moveorder.MoveOrderReport;

/**
 * Performance Report Form Class
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportForm.java,v 1.0 11/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MoveOrderReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private MoveOrderReportCriteria criteria = new MoveOrderReportCriteria();
	
	public MoveOrderReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MoveOrderReportCriteria criteria) {
		this.criteria = criteria;
	}

	public MoveOrderReport getMoveOrderReport() {
		return criteria.getMoveOrderReport();
	}

	public void setMoveOrderReport(MoveOrderReport performanceReport) {
		criteria.setMoveOrderReport(performanceReport);
	}
	
}
