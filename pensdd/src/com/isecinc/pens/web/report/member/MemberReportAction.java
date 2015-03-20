package com.isecinc.pens.web.report.member;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.report.I_ReportAction;
//import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.member.MemberExpirationReport;
import com.isecinc.pens.report.member.MemberExpirationReportProcess;
import com.isecinc.pens.report.salestargetsummary.SalesTargetSummaryReport;
import com.isecinc.pens.report.salestargetsummary.SalesTargetSummaryReportProcess;
import com.isecinc.pens.web.report.invoicedetail.InvoiceDetailReportForm;


public class MemberReportAction extends I_ReportAction<MemberExpirationReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<MemberExpirationReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<MemberExpirationReport> resultL = null;
		
		// Check Date Range
		MemberReportForm reportForm = (MemberReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("member_expiration");
		
		MemberExpirationReportProcess process = new MemberExpirationReportProcess();
		resultL = process.doReport(reportForm.getMemberExpirationReport(), user, conn);
		
		return resultL;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		MemberReportForm reportForm = (MemberReportForm) form;
		reportForm.setCriteria(new MemberReportCriteria());
	}
	
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		return mapping.findForward("prepare");
	}
}
