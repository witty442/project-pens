package com.isecinc.pens.web.report.salestargetsummary;

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

import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.report.I_ReportAction;
//import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salestargetsummary.SalesTargetSummaryReport;
import com.isecinc.pens.report.salestargetsummary.SalesTargetSummaryReportProcess;
import com.isecinc.pens.web.report.invoicedetail.InvoiceDetailReportForm;


public class SalesTargetSummaryAction extends I_ReportAction<SalesTargetSummaryReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<SalesTargetSummaryReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<SalesTargetSummaryReport> resultL = null;
		
		// Check Date Range
		SalesTargetSummaryForm reportForm = (SalesTargetSummaryForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("sales_target_summary");
		
		SalesTargetSummaryReportProcess process = new SalesTargetSummaryReportProcess();
		resultL = process.doReport(reportForm.getSalesTargetSummaryReport(), user, conn);
		
		return resultL;
	}


	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		SalesTargetSummaryForm reportForm = (SalesTargetSummaryForm) form;
		reportForm.setCriteria(new SalesTargetSummaryReportCriteria());
	}
	
	
}
