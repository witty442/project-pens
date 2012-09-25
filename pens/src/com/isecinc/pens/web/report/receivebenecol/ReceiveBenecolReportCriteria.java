package com.isecinc.pens.web.report.receivebenecol;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receivebenecol.ReceiveBenecolReport;

/**
 * Receive Benecol Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBenecolReportCriteria.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ReceiveBenecolReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 8974876148373135654L;

	private ReceiveBenecolReport receiveBenecolReport = new ReceiveBenecolReport();
	
	private String fileType = SystemElements.EXCEL;

	public ReceiveBenecolReport getReceiveBenecolReport() {
		return receiveBenecolReport;
	}

	public void setReceiveBenecolReport(ReceiveBenecolReport receiveBenecolReport) {
		this.receiveBenecolReport = receiveBenecolReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
