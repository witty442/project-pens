package com.isecinc.pens.web.report.invoicepayment;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.BeanParameter;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReportProcess;
import com.pens.util.DateToolsUtil;

/**
 * Invoice Payment Report Action
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReportAction.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentReportAction extends I_ReportAction<InvoicePaymentReport> {

	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<InvoicePaymentReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		InvoicePaymentReportProcess process = new InvoicePaymentReportProcess();
		InvoicePaymentReportForm reportForm = (InvoicePaymentReportForm) form;
		User user = (User) request.getSession().getAttribute("user");

		Calendar cal = Calendar.getInstance(new Locale("th", "TH"));
		cal.setTime(DateToolsUtil.convertStringToDate(reportForm.getInvoicePaymentReport().getReceiptDate()));
		// Set start date.
		cal.set(Calendar.DAY_OF_MONTH, 1);
		reportForm.getInvoicePaymentReport().setStartDate(DateToolsUtil.convertToString(cal.getTime()));
		// Set end date.
		cal.setTime(DateToolsUtil.convertStringToDate(reportForm.getInvoicePaymentReport().getReceiptDate()));
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		reportForm.getInvoicePaymentReport().setEndDate(DateToolsUtil.convertToString(cal.getTime()));

		// Sum current cash receipt
		double[] value = process.sumCashReceipt(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("cashReceipt", value[0]);
		
		// Sum current cheque receipt
		double[] valueCheque = process.sumChequeReceipt(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("chequeReceipt", valueCheque[0]);

		/* Comment Out : 
		// today Cheque
		List<InvoicePaymentReport> lstCurrent = process.searchReport(reportForm.getInvoicePaymentReport(), user, conn,InvoicePaymentReportProcess.SERACH_CASH_SALES );
		// Before
		List<InvoicePaymentReport> lstBefore = process.searchReport(reportForm.getInvoicePaymentReport(), user, conn, InvoicePaymentReportProcess.SERACH_CREDIT_SALES);
		// All
		List<InvoicePaymentReport> lstReport = new ArrayList<InvoicePaymentReport>();
		lstReport.addAll(lstCurrent);
		lstReport.addAll(lstBefore);
		*/
		
		List<InvoicePaymentReport> lstReport = process.searchReport(reportForm.getInvoicePaymentReport(), user, conn);
	
		// Sum Amt cash day before.
		double[] value2 = process.sumCashDayBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("cashDayBefore", value2[0]);
		parameterMap.put("receiptCnt2", value2[1]);
		
		// Sum Amt cheque day before.
		double[] valueChequeBefore = process.sumChequeDayBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("chequeDayBefore", valueChequeBefore[0]);
		parameterMap.put("receiptCnt2", valueChequeBefore[1]);
		
		parameterMap.put("receiptCurrentCnt", process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 1));//current
		parameterMap.put("receiptDayBeforeCnt",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 3));//ALL
		
		//sum cancel amt today =receiptDate
		double[] cancelReceipt = process.sumCancelReceiptAmount(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("cancelReceiptAmount",cancelReceipt[0]);
		parameterMap.put("cancelReceiptCnt",new Double(cancelReceipt[1]).intValue());

		double creditSalesAmtBefore = process.sumCreditSalesAmtBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("creditSalesAmtBefore",creditSalesAmtBefore);
				
		parameterMap.put("reportPath", BeanParameter.getReportPath());
		parameterMap.put("receipt_date", reportForm.getInvoicePaymentReport().getReceiptDate());
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("invoice_payment_report");  

		return lstReport;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		InvoicePaymentReportForm reportForm = (InvoicePaymentReportForm) form;
		reportForm.setCriteria(new InvoicePaymentReportCriteria());
	}
}
