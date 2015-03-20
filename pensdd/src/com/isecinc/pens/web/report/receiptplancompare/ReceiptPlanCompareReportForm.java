package com.isecinc.pens.web.report.receiptplancompare;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receiptplancompare.ReceiptPlanCompareReport;

public class ReceiptPlanCompareReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private ReceiptPlanCompareReportCriteria criteria = new ReceiptPlanCompareReportCriteria();

	public ReceiptPlanCompareReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ReceiptPlanCompareReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ReceiptPlanCompareReport getReceiptPlanCompareReport(){
		return criteria.getReceiptPlanCompareReport();
	}

	public void ReceiptPlanCompareReport(ReceiptPlanCompareReport receiptPlan){
		criteria.setReceiptPlanCompareReport(receiptPlan);
	}
}
