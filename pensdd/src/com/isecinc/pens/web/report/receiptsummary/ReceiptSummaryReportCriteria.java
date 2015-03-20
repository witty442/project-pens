package com.isecinc.pens.web.report.receiptsummary;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receiptsummary.ReceiptSummaryReport;

public class ReceiptSummaryReportCriteria extends I_ReportCriteria{
	/**
	 * 
	 */
	private static final long serialVersionUID = -870951444489921128L;
	
	private ReceiptSummaryReport receiptSummaryReport = new ReceiptSummaryReport();

	public ReceiptSummaryReport getReceiptSummaryReport() {
		return receiptSummaryReport;
	}

	public void setReceiptSummaryReport(ReceiptSummaryReport receiptSummaryReport) {
		this.receiptSummaryReport = receiptSummaryReport;
	}

	private String fileType = SystemElements.EXCEL;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
