package com.isecinc.pens.web.report.callbefore;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.report.callbefore.CallBeforeReport;

/**
 * Call Before Send Form Class
 * 
 * @author Aneak.t
 * @version $Id: CallBeforeReportForm.java,v 1.0 15/03/2010 00:00:00 aneak.t Exp $
 * 
 */

public class CallBeforeReportForm extends I_Form{

	private static final long serialVersionUID = 205132595615736798L;
	
	private CallBeforeReportCriteria criteria = new CallBeforeReportCriteria();

	public CallBeforeReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(CallBeforeReportCriteria criteria) {
		this.criteria = criteria;
	}

	public CallBeforeReport getCallBeforeReport() {
		return criteria.getCallBeforeReport();
	}

	public void setCallBeforeReport(CallBeforeReport callBeforeReport) {
		criteria.setCallBeforeReport(callBeforeReport);
	}

}
