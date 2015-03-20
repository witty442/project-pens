package com.isecinc.pens.web.report.receiptsummary;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.receiptsummary.ReceiptSummaryReport;
import com.isecinc.pens.report.receiptsummary.ReceiptSummaryReportProcess;

public class ReceiptSummaryReportAction extends I_ReportAction<ReceiptSummaryReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<ReceiptSummaryReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<ReceiptSummaryReport> resultL = null;
		
		// Check Date Range
		ReceiptSummaryReportForm reportForm = (ReceiptSummaryReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("receipt_summary");
		
		ReceiptSummaryReportProcess process = new ReceiptSummaryReportProcess();
		resultL = process.doReport(reportForm.getReceiptSummaryReport(), user, conn);
		
		return resultL;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiptSummaryReportForm reportForm = (ReceiptSummaryReportForm) form;
		reportForm.setCriteria(new ReceiptSummaryReportCriteria());
	}
	
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		return mapping.findForward("prepare");
	}
}
