package com.isecinc.pens.web.report.shipmentsummary;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.receiptplancompare.ReceiptPlanCompareReport;
import com.isecinc.pens.report.shipmentsummary.ShipmentSummaryReport;

public class ShipmentSummaryReportForm extends I_Form{

	private static final long serialVersionUID = 7909221604586705705L;

	private ShipmentSummaryReportCriteria criteria = new ShipmentSummaryReportCriteria();

	public ShipmentSummaryReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ShipmentSummaryReportCriteria criteria) {
		this.criteria = criteria;
	}

	public ShipmentSummaryReport getShipmentSummaryReport(){
		return criteria.getShipmentSummaryReport();
	}

	public void ShipmentSummaryReport(ShipmentSummaryReport receiptPlan){
		criteria.setShipmentSummaryReport(receiptPlan);
	}
}
