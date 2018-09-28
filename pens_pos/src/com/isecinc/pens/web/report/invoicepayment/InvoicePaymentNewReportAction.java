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
import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentNewReportProcess;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReportProcess;

/**
 * Invoice Payment Report Action
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReportAction.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentNewReportAction extends I_ReportAction<InvoicePaymentReport> {

	/**
	 * Search for report
	 **/
	@SuppressWarnings("unchecked")
	protected List<InvoicePaymentReport> searchReport(ActionForm form, HttpServletRequest request,
			HttpServletResponse response, HashMap parameterMap, Connection conn) throws Exception {

		InvoicePaymentNewReportProcess process = new InvoicePaymentNewReportProcess();
		InvoicePaymentNewReportForm reportForm = (InvoicePaymentNewReportForm) form;
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
		double[] valueCreditCard = process.sumCreditCardReceipt(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("creditCardReceipt", valueCreditCard[0]);
		
		InvoicePaymentReport dataReport = process.searchReport(reportForm.getInvoicePaymentReport(), user, conn);
		List<InvoicePaymentReport> lstReport = dataReport.getItemsList();
		parameterMap.put("totalCashAmt", dataReport.getTotalCashAmt());
		parameterMap.put("totalCreditcardAmt", dataReport.getTotalCreditCardAmt());
		
		// Sum Amt cash day before.
		double[] value2 = process.sumCashDayBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("cashDayBefore", value2[0]);
		parameterMap.put("receiptCnt2", value2[1]);
		
		// Sum Amt cheque day before.
		/*double[] valueChequeBefore = process.sumChequeDayBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("chequeDayBefore", valueChequeBefore[0]);
		parameterMap.put("receiptCnt2", valueChequeBefore[1]);*/
		
		parameterMap.put("receiptCurrentCnt", process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 1));//current
		parameterMap.put("receiptDayBeforeCnt",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 3));//ALL
		
		//sum cancel amt today =receiptDate
		double[] cancelReceipt = process.sumCancelReceiptAmount(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("cancelReceiptAmount",cancelReceipt[0]);
		parameterMap.put("cancelReceiptCnt",new Double(cancelReceipt[1]).intValue());

		//Credit Card amt
		double creditSalesAmtBefore = process.sumCreditSalesAmtBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("creditSalesAmtBefore",creditSalesAmtBefore);
				
		parameterMap.put("reportPath", BeanParameter.getReportPath());
		parameterMap.put("receipt_date", reportForm.getInvoicePaymentReport().getReceiptDate());
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("invoice_payment_new_report");  

		return lstReport;
	}

	/**
	 * Set new criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		InvoicePaymentNewReportForm reportForm = (InvoicePaymentNewReportForm) form;
		reportForm.setCriteria(new InvoicePaymentNewReportCriteria());
	}
}
