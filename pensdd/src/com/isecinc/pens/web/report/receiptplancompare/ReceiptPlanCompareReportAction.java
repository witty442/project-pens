package com.isecinc.pens.web.report.receiptplancompare;

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
import com.isecinc.pens.report.receiptplancompare.ReceiptPlanCompareReport;
import com.isecinc.pens.report.receiptplancompare.ReceiptPlanCompareReportProcess;


public class ReceiptPlanCompareReportAction extends I_ReportAction<ReceiptPlanCompareReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<ReceiptPlanCompareReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<ReceiptPlanCompareReport> resultL = null;
		
		// Check Date Range
		ReceiptPlanCompareReportForm reportForm = (ReceiptPlanCompareReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("receipt_plan_compare");
		
		ReceiptPlanCompareReportProcess process = new ReceiptPlanCompareReportProcess();
		resultL = process.doReport(reportForm.getReceiptPlanCompareReport(), user, conn);
		
		return resultL;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiptPlanCompareReportForm reportForm = (ReceiptPlanCompareReportForm) form;
		reportForm.setCriteria(new ReceiptPlanCompareReportCriteria());
	}
	
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		return mapping.findForward("prepare");
	}
}
