package com.isecinc.pens.web.report.transfer;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.performance.PerformanceReport;
import com.isecinc.pens.report.performance.PerformanceReportProcess;
import com.pens.util.DateToolsUtil;

/**
 * Performance Report Action
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportAction.java,v 1.0 11/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class BankTransferReportAction extends I_ReportAction<BankTransferReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<BankTransferReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {
		

		BankTransferReportProcess process = new BankTransferReportProcess();
		BankTransferReportForm reportForm = (BankTransferReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		List<BankTransferReport> lstData = null;
		try{
		   
			setFileType(reportForm.getCriteria().getFileType());
			setFileName("bank_transfer_report");
			
            BankTransferReport head = process.getData(reportForm.getPerformanceReport(), user, conn);
            parameterMap.put("totalAmount", head.getTotalAmount());
            lstData = head.getLstData();
		}catch(Exception e){
			e.printStackTrace();
		}
		return lstData;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		BankTransferReportForm reportForm = (BankTransferReportForm) form;
		reportForm.setCriteria(new BankTransferReportCriteria());
	}
}
