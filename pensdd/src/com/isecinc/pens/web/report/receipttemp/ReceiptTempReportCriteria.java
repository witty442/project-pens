package com.isecinc.pens.web.report.receipttemp;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receipttemp.ReceiptTempReport;

/**
 * Receipt Temporary Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ReceiptTempReportCriteria.java,v 1.0 17/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ReceiptTempReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 706238490790452256L;

	private ReceiptTempReport receiptTempReport = new ReceiptTempReport();

	private String fileType = SystemElements.EXCEL;
	
	public ReceiptTempReport getReceiptTempReport() {
		return receiptTempReport;
	}

	public void setReceiptTempReport(ReceiptTempReport receiptTempReport) {
		this.receiptTempReport = receiptTempReport;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
