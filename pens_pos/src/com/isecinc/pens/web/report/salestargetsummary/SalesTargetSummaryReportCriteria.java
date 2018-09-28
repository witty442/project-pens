package com.isecinc.pens.web.report.salestargetsummary;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.salestargetsummary.SalesTargetSummaryReport;

public class SalesTargetSummaryReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private SalesTargetSummaryReport salesTargetSummaryReport = new SalesTargetSummaryReport();
	
	public SalesTargetSummaryReport getSalesTargetSummaryReport() {
		return salesTargetSummaryReport;
	}

	public void setSalesTargetSummaryReport(
			SalesTargetSummaryReport salesTargetSummaryReport) {
		this.salesTargetSummaryReport = salesTargetSummaryReport;
	}

	private String fileType = SystemElements.PDF;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
