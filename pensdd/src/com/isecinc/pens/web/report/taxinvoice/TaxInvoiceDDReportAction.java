package com.isecinc.pens.web.report.taxinvoice;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;

import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceDDReport;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceDDReportProcess;



/**
 * Tax Invoice Report Action
 * 
 * @author Aneak.t
 * @version $Id: TaxInvoiceReportAction.java,v 1.0 01/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceDDReportAction extends I_ReportAction<TaxInvoiceDDReport>{


	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		TaxInvoiceDDReportForm taxInvoiceDDForm = (TaxInvoiceDDReportForm)form;
		taxInvoiceDDForm.setTaxInvoiceDDReport(new TaxInvoiceDDReport());
	}

	/**
	 * Search Report. 
	 */
	protected List<TaxInvoiceDDReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap<String, String> parameterMap, Connection conn) throws Exception {
		TaxInvoiceDDReportProcess process = new TaxInvoiceDDReportProcess();
		TaxInvoiceDDReportForm taxInvoiceDDForm = (TaxInvoiceDDReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		
		//parameterMap.put("receiptNo", taxInvoiceDDForm.getTaxInvoiceDDReport().getReceiptNo());
		
		//parameterMap.put("order_date_from", DateToolsUtil.dateNumToWord(taxInvoiceDDForm.getTaxInvoiceDDReport().getOrderDateFrom()));
		//parameterMap.put("order_date_to", DateToolsUtil.dateNumToWord(taxInvoiceDDForm.getTaxInvoiceDDReport().getOrderDateTo()));
		
		//parameterMap.put("customerCode_from", taxInvoiceDDForm.getTaxInvoiceDDReport().getCustomerCodeFrom());
		//parameterMap.put("customerCode_to", taxInvoiceDDForm.getTaxInvoiceDDReport().getCustomerCode());
		
		
		setFileType(taxInvoiceDDForm.getCriteria().getFileType());
		setFileName("tax_invoice_DD_report");
		List<TaxInvoiceDDReport> reportItem = process.doReport(taxInvoiceDDForm.getTaxInvoiceDDReport(), user, conn);
		
		return reportItem;
	}
	


}
