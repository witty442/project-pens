package com.isecinc.pens.web.report.invoicepayment_all;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReportProcess;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;

/**
 * InvoicePaymentAllReportAction
 * 
 * @author WITTY
 * @version $Id: InvoicePaymentAllReportAction,v 1.0 11/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentAllReportAction extends I_ReportAction<InvoicePaymentAllReport> {
	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<InvoicePaymentAllReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		InvoicePaymentAllReportProcess process = new InvoicePaymentAllReportProcess();
		InvoicePaymentAllReportForm reportForm = (InvoicePaymentAllReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		
		//set Parameter
		//logger.debug("receiptDateFrom:"+reportForm.getInvoicePaymentAllReport().getReceiptDateFrom());
		//String receiptDateFrom =DateToolsUtil.dateNumToWord(reportForm.getInvoicePaymentAllReport().getReceiptDateFrom());
		
		String receiptDate = reportForm.getInvoicePaymentAllReport().getReceiptDateFrom()+" - "+reportForm.getInvoicePaymentAllReport().getReceiptDateTo();
		
		parameterMap.put("receipt_date",receiptDate );
		parameterMap.put("total_customer", process.getCountCustomer(reportForm.getInvoicePaymentAllReport(), user, conn));
		
		//set Type
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("invoice_payment_all_report");
 
		List<InvoicePaymentAllReport> reportItem = process.doReport(reportForm.getInvoicePaymentAllReport(), user, conn); 
		
		int countRecord[] = process.getCountReceiptItem(reportForm.getInvoicePaymentAllReport(), user, conn);
		parameterMap.put("total_record_item",countRecord[0] );
		parameterMap.put("total_record_cancel",countRecord[1] );
		
		return reportItem;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		InvoicePaymentAllReportForm reportForm = (InvoicePaymentAllReportForm) form;
		reportForm.setCriteria(new InvoicePaymentAllReportCriteria());
	}
}
