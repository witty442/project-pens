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
import com.isecinc.pens.inf.helper.Utils;
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
		
		parameterMap.put("salesCode",user.getUserName());
		parameterMap.put("salesName",user.getName());
		
		/***** Current Day select *****************************************************************************/
		InvoicePaymentReport dataReport = process.searchReport(reportForm.getInvoicePaymentReport(), user, conn);
		List<InvoicePaymentReport> lstReport = dataReport.getItemsList();
		parameterMap.put("curCashAmt", dataReport.getTotalCashAmt());
		parameterMap.put("curCreditCardAmt", dataReport.getTotalCreditCardAmt());
		parameterMap.put("curAliAmt", dataReport.getTotalAliAmt());
		parameterMap.put("curWeAmt", dataReport.getTotalWeAmt());
		
		parameterMap.put("curCashCnt", process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 1));//currentCashCnt
		parameterMap.put("curCreditCardCnt",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 2));//curCreditcasrCnt
		parameterMap.put("curAliCnt",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 6));//curAliCnt
		parameterMap.put("curWeCnt",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 7));//curWechatCnt
		
		logger.debug("curAliCnt:"+parameterMap.get("curAliCnt"));
		logger.debug("curWeCnt:"+parameterMap.get("curWeCnt"));
		
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
		double aliAmtBefore = process.sumAliAmtBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("aliAmtBefore",aliAmtBefore);
		double weAmtBefore = process.sumWeAmtBefore(reportForm.getInvoicePaymentReport(), user, conn);
		parameterMap.put("weAmtBefore",weAmtBefore);
		
		parameterMap.put("cashCntBefore", process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 3));
		parameterMap.put("creditCardCntBefore",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 4));
		parameterMap.put("aliCntBefore",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 9));
		parameterMap.put("weCntBefore",process.countReport(reportForm.getInvoicePaymentReport(), user, conn, 10));
		/*******************************************************************************************************/		
		
		parameterMap.put("reportPath", BeanParameter.getReportPath());
		parameterMap.put("receipt_date", reportForm.getInvoicePaymentReport().getReceiptDate());
		
		setFileType(reportForm.getCriteria().getFileType());
		setFileName("invoice_payment_new_report");  

		logger.debug("curCashCnt:"+parameterMap.get("curCashCnt"));
		logger.debug("curCreditCardCnt:"+parameterMap.get("curCreditCardCnt"));
		logger.debug("curAliCnt:"+parameterMap.get("curAliCnt"));
		logger.debug("curWeCnt:"+parameterMap.get("curWeCnt"));
		
		logger.debug("cashCntBefore:"+parameterMap.get("cashCntBefore"));
		logger.debug("creditCardCntBefore:"+parameterMap.get("creditCardCntBefore"));
		logger.debug("aliCntBefore:"+parameterMap.get("aliCntBefore"));
		logger.debug("weCntBefore:"+parameterMap.get("weCntBefore"));
       
		double p_sum_all_cnt = (Integer)parameterMap.get("curCashCnt");
		      p_sum_all_cnt += (Integer)parameterMap.get("curCreditCardCnt");
		      p_sum_all_cnt += (Integer)parameterMap.get("curAliCnt");
		      p_sum_all_cnt += (Integer)parameterMap.get("curWeCnt");
		      p_sum_all_cnt += (Integer)parameterMap.get("cashCntBefore");
		      p_sum_all_cnt += (Integer)parameterMap.get("creditCardCntBefore");
		      p_sum_all_cnt += (Integer)parameterMap.get("aliCntBefore");
		      p_sum_all_cnt += (Integer)parameterMap.get("weCntBefore");
		

      double p_sum_all_amt = (Double)parameterMap.get("curCashAmt");
		      p_sum_all_amt += (Double)parameterMap.get("curCreditCardAmt");
		      p_sum_all_amt += (Double)parameterMap.get("curAliAmt");
		      p_sum_all_amt += (Double)parameterMap.get("curWeAmt");
		      
		      p_sum_all_amt += (Double)parameterMap.get("cashAmtBefore");
		      p_sum_all_amt += (Double)parameterMap.get("creditCardAmtBefore");
		      p_sum_all_amt += (Double)parameterMap.get("aliAmtBefore");
		      p_sum_all_amt += (Double)parameterMap.get("weAmtBefore");
		      
		parameterMap.put("p_sum_all_cnt",p_sum_all_cnt);
		parameterMap.put("p_sum_all_amt",p_sum_all_amt);
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
