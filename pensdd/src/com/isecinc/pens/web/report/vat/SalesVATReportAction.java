package com.isecinc.pens.web.report.vat;

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
import com.isecinc.pens.report.vat.SalesVATReport;
import com.isecinc.pens.report.vat.SalesVATReportProcess;


public class SalesVATReportAction extends I_ReportAction<SalesVATReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<SalesVATReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception 
	{
		List<SalesVATReport> resultL = null;
		
		// Check Date Range
		SalesVATReportForm reportForm = (SalesVATReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("sales_vat_report");
		
		SalesVATReportProcess process = new SalesVATReportProcess();
		resultL = process.doReport(reportForm.getSalesVATReport(), user, conn);
		
		return resultL;
	}


	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		SalesVATReportForm reportForm = (SalesVATReportForm) form;
		reportForm.setCriteria(new SalesVATReportCriteria());
	}
	
	
}
