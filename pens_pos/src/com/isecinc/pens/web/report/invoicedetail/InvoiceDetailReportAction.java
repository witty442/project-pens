package com.isecinc.pens.web.report.invoicedetail;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.report.invoicedetail.InvoiceDetailReport;
import com.isecinc.pens.report.invoicedetail.InvoiceDetailReportProcess;

/**
 * InvoiceDetailReportAction
 * 
 * @author WITTY
 * @version 1
 * 
 */

public class InvoiceDetailReportAction extends I_ReportAction<InvoiceDetailReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<InvoiceDetailReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		InvoiceDetailReportProcess process = new InvoiceDetailReportProcess();
		InvoiceDetailReportForm reportForm = (InvoiceDetailReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		//Search Order Detail
		String whereSql = "";
		if( !Utils.isNull(reportForm.getInvoiceDetailReport().getOrderNoFrom()).equals("")
				&& !Utils.isNull(reportForm.getInvoiceDetailReport().getOrderNoFrom()).equals("")){
			whereSql += " and order_no >='"+reportForm.getInvoiceDetailReport().getOrderNoFrom()+"' \n";
			whereSql += " and order_no <='"+reportForm.getInvoiceDetailReport().getOrderNoTo()+"' \n";
		}
		if( !Utils.isNull(reportForm.getInvoiceDetailReport().getOrderDateFrom()).equals("")
			&&	!Utils.isNull(reportForm.getInvoiceDetailReport().getOrderDateTo()).equals("")	){
			
			whereSql += " and order_date >='"+DateToolsUtil.convertToTimeStamp(reportForm.getInvoiceDetailReport().getOrderDateFrom())+"' \n";
			whereSql += " and order_date <='"+DateToolsUtil.convertToTimeStamp(reportForm.getInvoiceDetailReport().getOrderDateTo())+"' \n";
		}
		
		Order[] orders = new MOrder().search(whereSql);
		if(orders != null){
			//set Parameter
			parameterMap.put("salesCode", orders[0].getSalesRepresent().getCode());
			parameterMap.put("salesName", orders[0].getSalesRepresent().getName());
			
			//set Type
			setFileType(reportForm.getCriteria().getFileType());
			setFileName("invoice_detail_report");
	 
			List<InvoiceDetailReport> reportItem = process.doReport(reportForm.getInvoiceDetailReport(), user, conn); 
			return reportItem;
		}
		return null;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		InvoiceDetailReportForm reportForm = (InvoiceDetailReportForm) form;
		reportForm.setCriteria(new InvoiceDetailReportCriteria());
	}
}
