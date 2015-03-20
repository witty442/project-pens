package com.isecinc.pens.web.report.shipmentsummary;

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
import com.isecinc.pens.report.shipmentsummary.ShipmentSummaryReport;
import com.isecinc.pens.report.shipmentsummary.ShipmentSummaryReportProcess;


public class ShipmentSummaryReportAction extends I_ReportAction<ShipmentSummaryReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<ShipmentSummaryReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<ShipmentSummaryReport> resultL = null;
		
		// Check Date Range
		ShipmentSummaryReportForm reportForm = (ShipmentSummaryReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("shipment_summary");
		
		ShipmentSummaryReportProcess process = new ShipmentSummaryReportProcess();
		resultL = process.doReport(reportForm.getShipmentSummaryReport(), user, conn);
		
		return resultL;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ShipmentSummaryReportForm reportForm = (ShipmentSummaryReportForm) form;
		reportForm.setCriteria(new ShipmentSummaryReportCriteria());
	}
	
	public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	{
		return mapping.findForward("prepare");
	}
}
