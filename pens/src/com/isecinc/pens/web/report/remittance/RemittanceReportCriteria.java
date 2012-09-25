package com.isecinc.pens.web.report.remittance;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.remittance.RemittanceReport;

/**
 * Remittance Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReportCriteria.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class RemittanceReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -6984059724190714094L;

	private RemittanceReport remittanceReport = new RemittanceReport();
	
	private String fileType = SystemElements.EXCEL;

	public RemittanceReport getRemittanceReport() {
		return remittanceReport;
	}

	public void setRemittanceReport(RemittanceReport remittanceReport) {
		this.remittanceReport = remittanceReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
