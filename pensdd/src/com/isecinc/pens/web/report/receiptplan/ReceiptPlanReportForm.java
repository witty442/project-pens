package com.isecinc.pens.web.report.receiptplan;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receiptplan.ReceiptPlanReport;

public class ReceiptPlanReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private ReceiptPlanReportCriteria criteria = new ReceiptPlanReportCriteria();

	public ReceiptPlanReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReceiptPlanReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiptPlanReport getReceiptPlanReport(){
		return criteria.getReceiptPlanReport();
	}

	public void ReceiptPlanReport(ReceiptPlanReport receiptPlan){
		criteria.setReceiptPlanReport(receiptPlan);
	}
}
