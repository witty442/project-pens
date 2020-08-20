package com.isecinc.pens.web.report.moveorder;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.report.canceldetail.CancelDetailReport;
import com.isecinc.pens.report.canceldetail.CancelDetailReportProcess;
import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.isecinc.pens.report.moveorder.MoveOrderReport;
import com.isecinc.pens.report.moveorder.MoveOrderReportProcess;
import com.pens.util.DateToolsUtil;


/**
 * CancelDetailReportAction
 * 
 * @author WITTY
 * @version 1
 * 
 */

public class MoveOrderReportAction extends I_ReportAction<MoveOrderReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<MoveOrderReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		MoveOrderReportProcess process = new MoveOrderReportProcess();
		MoveOrderReportForm reportForm = (MoveOrderReportForm) form;
		User user = (User) request.getSession().getAttribute("user");


		//set Parameter
		parameterMap.put("salesCode", user.getCode());
		parameterMap.put("salesName", user.getName());
		parameterMap.put("startDate", reportForm.getMoveOrderReport().getOrderDateFrom());
		parameterMap.put("endDate",  reportForm.getMoveOrderReport().getOrderDateTo());
		//set Type
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("move_order_summary_report");
 
		List<MoveOrderReport> reportItem = process.doReport(reportForm.getMoveOrderReport(), user, conn); 
		if(reportItem != null && reportItem.size() >0){
		   return reportItem;
		}
		return null;
		
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		MoveOrderReportForm reportForm = (MoveOrderReportForm) form;
		reportForm.setCriteria(new MoveOrderReportCriteria());
	}
}
