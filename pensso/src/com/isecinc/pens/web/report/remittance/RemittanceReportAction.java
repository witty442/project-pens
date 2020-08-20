package com.isecinc.pens.web.report.remittance;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.remittance.RemittanceReport;
import com.isecinc.pens.report.remittance.RemittanceReportProcess;
import com.pens.util.DateToolsUtil;

/**
 * Remittance Report Action Class
 * 
 * @author Aneak.t
 * @version $Id: RemittanceReportAction.java,v 1.0 02/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class RemittanceReportAction extends I_ReportAction<RemittanceReport>{

	/**
	 * Search for report
	 */
	protected List<RemittanceReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		RemittanceReportProcess process = new RemittanceReportProcess();
		RemittanceReportForm remittanceForm = (RemittanceReportForm)form;
		User user = (User)request.getSession().getAttribute("user");

		if(!remittanceForm.getRemittanceReport().getReceiptDateFrom().isEmpty() || !remittanceForm.getRemittanceReport().getReceiptDateFrom().equalsIgnoreCase("")){
			parameterMap.put("receipt_date_from", DateToolsUtil.dateNumToWord(remittanceForm.getRemittanceReport().getReceiptDateFrom()));
			parameterMap.put("receipt_date_to", DateToolsUtil.dateNumToWord(remittanceForm.getRemittanceReport().getReceiptDateTo()));
		}
		
		parameterMap.put("customerCode_from", remittanceForm.getRemittanceReport().getCustomerCodeFrom());
		parameterMap.put("customerCode_to", remittanceForm.getRemittanceReport().getCustomerCode());
		
		setFileType(remittanceForm.getCriteria().getFileType());
		setFileName("remittance_report");
		
		return process.doReport(remittanceForm.getRemittanceReport(), user, conn);
	}

	/**
	 * Set new criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		RemittanceReportForm remittanceForm = (RemittanceReportForm)form;
		remittanceForm.setRemittanceReport(new RemittanceReport());
	}

}
