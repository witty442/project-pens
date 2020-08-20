package com.isecinc.pens.web.report.summary;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.summary.BenecolSummaryReport;
import com.isecinc.pens.report.summary.BenecolSummaryReportProcess;
import com.pens.util.DateToolsUtil;

public class BenecolSummaryReportAction extends I_ReportAction<BenecolSummaryReport> {

	@Override
	protected List<BenecolSummaryReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		BenecolSummaryReportProcess process = new BenecolSummaryReportProcess();
		String dateFrom = request.getParameter("dateFrom");
		String dateTo = request.getParameter("dateTo");
		String reportType = request.getParameter("reportType");

		User user = (User) request.getSession().getAttribute("user");
		setFileType(SystemElements.PDF);
		if (reportType.equalsIgnoreCase("S")) {
			setFileName("benecol_order_summary");
		} else {
			setFileName("benecol_order_detail");
		}

		parameterMap.put("datefrom", dateFrom);
		parameterMap.put("dateto", dateTo);
		
		BenecolSummaryReport report = new BenecolSummaryReport();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);

		return process.doReport(report, user, conn);
	}

	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {}

}
