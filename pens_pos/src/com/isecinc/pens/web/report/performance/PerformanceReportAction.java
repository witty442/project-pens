package com.isecinc.pens.web.report.performance;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.performance.PerformanceReport;
import com.isecinc.pens.report.performance.PerformanceReportProcess;

/**
 * Performance Report Action
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportAction.java,v 1.0 11/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class PerformanceReportAction extends I_ReportAction<PerformanceReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<PerformanceReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {
		

		PerformanceReportProcess process = new PerformanceReportProcess();
		PerformanceReportForm reportForm = (PerformanceReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try{
		   // get sum all.
			PerformanceReport p = process.getSumAll(reportForm.getPerformanceReport(), user, conn);
			if (p != null) {
				parameterMap.put("order_date", DateToolsUtil.dateNumToWord(reportForm.getPerformanceReport().getOrderDate()));
				parameterMap.put("total_discount", p.getAllDiscount());
				parameterMap.put("total_amount", p.getAllCashAmount());
				parameterMap.put("total_ap_amount", p.getAllAirpayAmount());
				parameterMap.put("total_receipt", p.getAllReceiptAmount());
				parameterMap.put("total_vat", p.getAllVatAmount());
				parameterMap.put("total_net", p.getAllNetAmount());
				parameterMap.put("target_amount", p.getAllTargetAmount());
				parameterMap.put("total_vat_cash", p.getAllVatCashAmount());
				parameterMap.put("total_vat_receipt", p.getAllVatReceiptAmount());
				parameterMap.put("total_cancel_amount", p.getTotalCancelAmountToday());
				
			}
			parameterMap.put("total_visit", 0);//process.getCountVisit(reportForm.getPerformanceReport(), user, conn));
			parameterMap.put("total_customer", process.getCountCustomer(reportForm.getPerformanceReport(), user, conn));
			
			int countRecord[] = process.getCountOrderItem(reportForm.getPerformanceReport(), user, conn);
			parameterMap.put("total_record_item",countRecord[0] );
			parameterMap.put("total_record_cancel",countRecord[1] );
			
			setFileType(reportForm.getCriteria().getFileType());
			setFileName("performance_report");

		}catch(Exception e){
			e.printStackTrace();
		}
		return process.doReport(reportForm.getPerformanceReport(), user, conn);
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		PerformanceReportForm reportForm = (PerformanceReportForm) form;
		reportForm.setCriteria(new PerformanceReportCriteria());
	}
}
