package com.isecinc.pens.web.report.receiptplan;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receiptplan.ReceiptPlanReport;

public class ReceiptPlanReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private ReceiptPlanReport receiptPlan = new ReceiptPlanReport();
	
	public ReceiptPlanReport getReceiptPlanReport() {
		return receiptPlan;
	}

	public void setReceiptPlanReport(
			ReceiptPlanReport receiptPlan) {
		this.receiptPlan = receiptPlan;
	}

	private String fileType = SystemElements.PDF;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
