package com.isecinc.pens.web.report.receiptplancompare;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receiptplan.ReceiptPlanReport;
import com.isecinc.pens.report.receiptplancompare.ReceiptPlanCompareReport;

public class ReceiptPlanCompareReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private ReceiptPlanCompareReport receiptPlanCompare = new ReceiptPlanCompareReport();
	
	public ReceiptPlanCompareReport getReceiptPlanCompareReport() {
		return receiptPlanCompare;
	}

	public void setReceiptPlanCompareReport(ReceiptPlanCompareReport receiptPlanCompare) {
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
