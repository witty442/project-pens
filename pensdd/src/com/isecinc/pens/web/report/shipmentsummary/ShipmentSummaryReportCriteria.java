package com.isecinc.pens.web.report.shipmentsummary;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receiptplan.ReceiptPlanReport;
import com.isecinc.pens.report.receiptplancompare.ReceiptPlanCompareReport;
import com.isecinc.pens.report.shipmentsummary.ShipmentSummaryReport;

public class ShipmentSummaryReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private ShipmentSummaryReport receiptPlanCompare = new ShipmentSummaryReport();
	
	public ShipmentSummaryReport getShipmentSummaryReport() {
		return receiptPlanCompare;
	}

	public void setShipmentSummaryReport(ShipmentSummaryReport receiptPlanCompare) {
		this.receiptPlanCompare = receiptPlanCompare;
	}

	private String fileType = SystemElements.PDF;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
