package com.isecinc.pens.web.report.vat;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.vat.SalesVATReport;

public class SalesVATReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -8463911228603168679L;

	private SalesVATReport salesVATReport = new SalesVATReport();
	
	public SalesVATReport geSalesVATReport() {
		return salesVATReport;
	}

	public void setSalesVATReport(
			SalesVATReport salesVATReport) {
		this.salesVATReport = salesVATReport;
	}

	private String fileType = SystemElements.PDF;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
