package com.isecinc.pens.web.report.callbefore;

import com.isecinc.core.report.I_ReportCriteria;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.report.callbefore.CallBeforeReport;

/**
 * Call Before Report Criteria
 * 
 * @author Aneak.t
 * @version $Id: CallBeforeReportCriteria.java,v 1.0 16/03/2010 00:00:00 aneak.t Exp $
 * 
 */

public class CallBeforeReportCriteria extends I_ReportCriteria{

	private static final long serialVersionUID = -492254441820224973L;
	
	private CallBeforeReport callBeforeReport = new CallBeforeReport();
	
	private String fileType = SystemElements.EXCEL;

	public CallBeforeReport getCallBeforeReport() {
		return callBeforeReport;
	}

	public void setCallBeforeReport(CallBeforeReport callBeforeReport) {
		this.callBeforeReport = callBeforeReport;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	

}
