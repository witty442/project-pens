package com.isecinc.pens.web.report.receipttemp;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.receipttemp.ReceiptTempReport;
import com.isecinc.pens.report.receipttemp.ReceiptTempReportProcess;

/**
 * Receipt Temporary Report Action
 * 
 * @author Aneak.t
 * @version $Id: ReceiptTempReportAction.java,v 1.0 17/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiptTempReportAction extends I_ReportAction<ReceiptTempReport>{
	/**
	 *	Search for report 
	 **/
	protected List<ReceiptTempReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		
		ReceiptTempReportProcess process = new ReceiptTempReportProcess();
		ReceiptTempReportForm reportForm = (ReceiptTempReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("receipt_temp_report");
		
		return process.doReport(reportForm.getReceiptTempReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiptTempReportForm reportForm = (ReceiptTempReportForm)form;
		reportForm.setCriteria(new ReceiptTempReportCriteria());
	}

}
