package com.isecinc.pens.web.report.salestargetsummary;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.salestargetsummary.SalesTargetSummaryReport;

public class SalesTargetSummaryForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private SalesTargetSummaryReportCriteria criteria = new SalesTargetSummaryReportCriteria();

	public SalesTargetSummaryReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SalesTargetSummaryReportCriteria criteria) {
		this.criteria = criteria;
	}

	public SalesTargetSummaryReport getSalesTargetSummaryReport(){
		return criteria.getSalesTargetSummaryReport();
	}

	public void setSalesTargetSummaryReport(SalesTargetSummaryReport salesTargetSum){
		criteria.setSalesTargetSummaryReport(salesTargetSum);
	}
}
