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

		/***** Current Day select *****************************************************************************/
		InvoicePaymentReport dataReport = process.searchReport(reportForm.getInvoicePaymentReport(), user, conn);
		List<InvoicePaymentReport> lstReport = dataReport.getItemsList();
		parameterMap.put("curCashAmt", dataReport.getTotalCashAmt());
		parameterMap.put("curCreditCardAmt", dataReport.getTotalCreditCardAmt());
		
		parameterMap.put("curCashCnt", process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 1));//currentCashCnt
		parameterMap.put("curCreditCardCnt",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 2));//curCreditcasrCnt
		
		//sum cancel amt today =receiptDate
		double[] curCancelCash = process.sumCurCancelCashAmt(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("curCancelCashAmt",curCancelCash[0]);
		parameterMap.put("curCancelCashCnt",new Double(curCancelCash[1]).intValue());

		/************* Before Day select **********************************************************************/
		// cash amt before.
		double[] value2 = process.sumCashAmtBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("cashAmtBefore", value2[0]);
		double creditSalesAmtBefore = process.sumCreditCardAmtBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("creditCardAmtBefore",creditSalesAmtBefore);
		
		parameterMap.put("cashCntBefore", process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 3));
		parameterMap.put("creditCardCntBefore",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 4));
		/*******************************************************************************************************/		
		
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
