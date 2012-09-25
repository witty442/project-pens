package com.isecinc.pens.web.report.callbefore;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.callbefore.CallBeforeReport;
import com.isecinc.pens.report.callbefore.CallBeforeReportProcess;

/**
 * Call Before Send Report Action
 * 
 * @author Aneak.t
 * @version $Id: CallBeforeReportAction.java,v 1.0 15/03/2010 15:52:00 aneak.t Exp $
 * 
 */

public class CallBeforeReportAction extends I_ReportAction<CallBeforeReport>{

	/**
	 *	Search for report 
	 **/
	protected List<CallBeforeReport> searchReport(ActionForm form, HttpServletRequest request, 
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn)
			throws Exception {
		CallBeforeReportProcess process = new CallBeforeReportProcess();
		CallBeforeReportForm reportForm = (CallBeforeReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		parameterMap.put("shipping_date", DateToolsUtil.dateNumToWord(reportForm.getCallBeforeReport().getShippingDate()));
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("call_before_report");
		
		return process.doReport(reportForm.getCallBeforeReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		CallBeforeReportForm reportForm = (CallBeforeReportForm)form;
		reportForm.setCriteria(new CallBeforeReportCriteria());
		
	}

}
