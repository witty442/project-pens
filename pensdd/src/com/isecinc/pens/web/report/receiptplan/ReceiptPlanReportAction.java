package com.isecinc.pens.web.report.receiptplan;

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
import com.isecinc.pens.report.receiptplan.ReceiptPlanReport;
import com.isecinc.pens.report.receiptplan.ReceiptPlanReportProcess;


public class ReceiptPlanReportAction extends I_ReportAction<ReceiptPlanReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<ReceiptPlanReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<ReceiptPlanReport> resultL = null;
		
		// Check Date Range
		ReceiptPlanReportForm reportForm = (ReceiptPlanReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("receipt_plan");
		
		ReceiptPlanReportProcess process = new ReceiptPlanReportProcess();
		resultL = process.doReport(reportForm.getReceiptPlanReport(), user, conn);
		
		return resultL;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiptPlanReportForm reportForm = (ReceiptPlanReportForm) form;
		reportForm.setCriteria(new ReceiptPlanReportCriteria());
	}
	
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		return mapping.findForward("prepare");
	}
}
