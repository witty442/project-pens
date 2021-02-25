package com.isecinc.pens.web.report.canceldetail;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.report.canceldetail.CancelDetailReport;
import com.isecinc.pens.report.canceldetail.CancelDetailReportProcess;
import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.pens.util.DateToolsUtil;
import com.pens.util.Utils;


/**
 * CancelDetailReportAction
 * 
 * @author WITTY
 * @version 1
 * 
 */

public class CancelDetailReportAction extends I_ReportAction<CancelDetailReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<CancelDetailReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		CancelDetailReportProcess process = new CancelDetailReportProcess();
		CancelDetailReportForm reportForm = (CancelDetailReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		//Search Order Detail
		String whereSql = "";

		if( !Utils.isNull(reportForm.getCancelDetailReport().getOrderDateFrom()).equals("")
			&&	!Utils.isNull(reportForm.getCancelDetailReport().getOrderDateTo()).equals("")	){
			
			whereSql += " and order_date >='"+DateToolsUtil.convertToTimeStamp(reportForm.getCancelDetailReport().getOrderDateFrom())+"' \n";
			whereSql += " and order_date <='"+DateToolsUtil.convertToTimeStamp(reportForm.getCancelDetailReport().getOrderDateTo())+"' \n";
		}
		
		Order[] orders = new MOrder().search(whereSql);
		if(orders != null){
			//set Parameter
			parameterMap.put("salesCode", orders[0].getSalesRepresent().getCode());
			parameterMap.put("salesName", orders[0].getSalesRepresent().getName());
			parameterMap.put("startDate", reportForm.getCancelDetailReport().getOrderDateFrom());
			parameterMap.put("endDate",  reportForm.getCancelDetailReport().getOrderDateTo());
			//set Type
			setFileType(reportForm.getCriteria().getFileType());
			setFileName("cancel_detail_report");
	 
			List<CancelDetailReport> reportItem = process.doReport(reportForm.getCancelDetailReport(), user, conn); 
			return reportItem;
		}
		return null;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		CancelDetailReportForm reportForm = (CancelDetailReportForm) form;
		reportForm.setCriteria(new CancelDetailReportCriteria());
	}
}
