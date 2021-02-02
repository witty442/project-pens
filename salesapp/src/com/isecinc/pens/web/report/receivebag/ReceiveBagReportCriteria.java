package com.isecinc.pens.web.report.receivebag;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.receivebag.ReceiveBagReport;

/**
 * Receive Bag Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBagReportCriteria.java,v 1.0 03/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiveBagReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = 877089486970984711L;

	private ReceiveBagReport receiveBagReport = new ReceiveBagReport();
	
	private String fileType = SystemElements.EXCEL;

	public ReceiveBagReport getReceiveBagReport() {
		return receiveBagReport;
	}

	public void setReceiveBagReport(ReceiveBagReport receiveBagReport) {
		this.receiveBagReport = receiveBagReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}
