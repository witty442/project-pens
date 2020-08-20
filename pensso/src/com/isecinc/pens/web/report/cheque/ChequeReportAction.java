package com.isecinc.pens.web.report.cheque;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.cheque.ChequeReport;
import com.isecinc.pens.report.cheque.ChequeReportProcess;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReportProcess;
import com.pens.util.DateToolsUtil;

/**
 * InvoicePaymentAllReportAction
 * 
 * @author WITTY
 * @version $Id: InvoicePaymentAllReportAction,v 1.0 11/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ChequeReportAction extends I_ReportAction<ChequeReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<ChequeReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		ChequeReportProcess process = new ChequeReportProcess();
		ChequeReportForm reportForm = (ChequeReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		//set Parameter
		parameterMap.put("order_date_from", DateToolsUtil.dateNumToWord(reportForm.getChequeReport().getOrderDateFrom()));
		parameterMap.put("order_date_to", DateToolsUtil.dateNumToWord(reportForm.getChequeReport().getOrderDateTo()));
		parameterMap.put("sale_code",user.getCode());
		parameterMap.put("sale_name",user.getName());
		
		//set Type
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("cheque_report");
 
		List<ChequeReport> reportItem = process.doReport(reportForm.getChequeReport(), user, conn); 

		return reportItem;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ChequeReportForm reportForm = (ChequeReportForm) form;
		reportForm.setCriteria(new ChequeReportCriteria());
	}
}
