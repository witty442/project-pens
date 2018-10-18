package com.isecinc.pens.report.invoicepayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jfree.util.Log;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;

/**
 * Invoice Payment Report Process
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReportProcess.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentNewReportProcess extends I_ReportProcess<InvoicePaymentReport> {

	@Override
	public List<InvoicePaymentReport> doReport(InvoicePaymentReport t, User user, Connection conn) throws Exception {
		return null;
	}
	
	public static final int SERACH_CASH_SALES = 1;
	public static final int SERACH_CREDIT_SALES = 2;

	/**
	 * Search for invoice payment report
	 */
	public List<InvoicePaymentReport> searchReport(InvoicePaymentReport t, User user, Connection conn, int type)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<InvoicePaymentReport> pos = new ArrayList<InvoicePaymentReport>();
		InvoicePaymentReport inv = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT inv.NAME AS INV_NAME, inv.DESCRIPTION, ");
			sql.append("\n  rc.RECEIPT_DATE, us.CODE, us.NAME, cus.NAME AS CUSTOMER_NAME, ");
			sql.append("\n  cus.CODE AS CUSTOMER_CODE, od.ORDER_NO, od.ORDER_DATE, ");
			sql.append("\n  rcby.BANK, rcby.CHEQUE_NO, rcby.CHEQUE_DATE, ");
			sql.append("\n  (rcby.RECEIPT_AMOUNT) AS CHEQUE_AMT, ");
			sql.append("\n  (SELECT SUM(rcby.RECEIPT_AMOUNT) FROM t_receipt ");
			sql.append("\n  INNER JOIN t_receipt_line ON t_receipt_line.RECEIPT_ID = t_receipt.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = t_receipt_line.RECEIPT_LINE_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
			sql.append("\n  WHERE rcby.PAYMENT_METHOD = 'CS' ");
			// Wit Edit 18/05/2011
			sql.append("\n  AND t_receipt.user_id = "+user.getId());
			switch (type) {
			case 1: {
				sql.append("\n  AND rcby.WRITE_OFF = 'Y' ");
			}
				break;
			default:
				break;
			}
			sql.append("\n  AND t_receipt.RECEIPT_DATE = rc.RECEIPT_DATE ");
			sql.append("\n  AND t_receipt.DOC_STATUS = rc.DOC_STATUS ");
			sql.append("\n  AND t_receipt_line.ORDER_ID =  rcl.ORDER_ID ");
			sql.append("\n  ) AS CASH_WRITEOFF FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
			sql.append("\n  INNER JOIN t_order od ON rcl.ORDER_ID = od.ORDER_ID ");
			sql.append("\n  INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  INNER JOIN ad_user us ON rc.USER_ID = us.USER_ID ");
			sql.append("\n  LEFT JOIN m_sub_inventory inv ON inv.NAME = us.CODE ");
			sql.append("\n  WHERE rcby.PAYMENT_METHOD ='CH'");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");

			switch (type) {
			case 1: {
				// today receipt, today order
				sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.RECEIPT_DATE,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
				sql.append("\n  AND rcl.ORDER_ID IN ( ");
				sql.append("\n  SELECT order_id FROM t_order od  ");
				sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS  ");
				sql.append("\n  AND od.ORDER_DATE = rc.RECEIPT_DATE) ");
			}
				break;
			case 2: {
				// today receipt, post order
				sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
				sql.append("\n  AND rcl.ORDER_ID IN ( ");
				sql.append("\n  SELECT order_id FROM t_order od  ");
				sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS  ");
				sql.append("\n  AND od.ORDER_DATE < rc.RECEIPT_DATE) ");
			}
				break;
			case 3: {
				sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
				sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) <= DATE('" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "') ");
				sql.append("\n  AND rcl.ORDER_ID IN ( ");
				sql.append("\n  SELECT order_id FROM t_order od  ");
				sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS ) ");
			}
				break;
			default:
				break;
			}
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
			
			logger.debug("sql:"+sql.toString());
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			while (rst.next()) {
				inv = new InvoicePaymentReport();

				inv.setId(i++);
				inv.setInvName(rst.getString("INV_NAME"));
				inv.setDescription(rst.getString("DESCRIPTION"));
				inv.setReceiptDate(DateToolsUtil.convertToString(rst.getDate("RECEIPT_DATE")));
				inv.setCode(rst.getString("CODE"));
				inv.setName(rst.getString("NAME"));
				inv.setCustomerName(rst.getString("CUSTOMER_NAME"));
				inv.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				inv.setOrderNo(rst.getString("ORDER_NO"));
				inv.setOrderDate(DateToolsUtil.convertToString(rst.getDate("ORDER_DATE")));
				inv.setBank(rst.getString("BANK"));
				inv.setChequeNo(rst.getString("CHEQUE_NO"));
				if (!inv.getChequeNo().equals("")) {
					inv.setChequeDate(DateToolsUtil.convertToString(rst.getDate("CHEQUE_DATE")));
				}
				inv.setChequeAmt(rst.getDouble("CHEQUE_AMT"));
				inv.setCashWriteOff(rst.getDouble("CASH_WRITEOFF"));
				inv.setReceiptAmount(inv.getChequeAmt() + inv.getCashWriteOff());
				if (type == 1) inv.setIsCurrent("Y");
				else inv.setIsCurrent("N");
				pos.add(inv);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}

		return pos;
	}

	public List<InvoicePaymentReport> sumPostAmt(InvoicePaymentReport t, User user, Connection conn, int type)throws Exception {
	Statement stmt = null;
	ResultSet rst = null;
	List<InvoicePaymentReport> pos = new ArrayList<InvoicePaymentReport>();
	InvoicePaymentReport inv = null;
	StringBuilder sql = new StringBuilder();
	try {
		sql.delete(0, sql.length());
		sql.append("\n  SELECT inv.NAME AS INV_NAME, inv.DESCRIPTION, ");
		sql.append("\n  rc.RECEIPT_DATE, us.CODE, us.NAME, cus.NAME AS CUSTOMER_NAME, ");
		sql.append("\n  cus.CODE AS CUSTOMER_CODE, od.ORDER_NO, od.ORDER_DATE, ");
		sql.append("\n  rcby.BANK, rcby.CHEQUE_NO, rcby.CHEQUE_DATE, ");
		sql.append("\n  (rcby.RECEIPT_AMOUNT) AS CHEQUE_AMT, ");
		sql.append("\n  (SELECT SUM(rcby.RECEIPT_AMOUNT) FROM t_receipt ");
		sql.append("\n  INNER JOIN t_receipt_line ON t_receipt_line.RECEIPT_ID = t_receipt.RECEIPT_ID ");
		sql.append("\n  INNER JOIN t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = t_receipt_line.RECEIPT_LINE_ID ");
		sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
		sql.append("\n  WHERE rcby.PAYMENT_METHOD = 'CS' ");
		// Wit Edit 18/05/2011
		sql.append("\n  AND t_receipt.user_id = "+user.getId());
		switch (type) {
		case 1: {
			sql.append("\n  AND rcby.WRITE_OFF = 'Y' ");
		}
			break;
		default:
			break;
		}
		sql.append("\n  AND t_receipt.RECEIPT_DATE = rc.RECEIPT_DATE ");
		sql.append("\n  AND t_receipt.DOC_STATUS = rc.DOC_STATUS ");
		sql.append("\n  AND t_receipt_line.ORDER_ID =  rcl.ORDER_ID ");
		sql.append("\n  ) AS CASH_WRITEOFF FROM t_receipt rc ");
		sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
		sql.append("\n  INNER JOIN t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
		sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
		sql.append("\n  INNER JOIN t_order od ON rcl.ORDER_ID = od.ORDER_ID ");
		sql.append("\n  INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
		sql.append("\n  INNER JOIN ad_user us ON rc.USER_ID = us.USER_ID ");
		sql.append("\n  LEFT JOIN m_sub_inventory inv ON inv.NAME = us.CODE ");
		sql.append("\n  WHERE rcby.PAYMENT_METHOD IN('CH','CS')");
		sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
	
		switch (type) {
		case 1: {
			// today receipt, today order
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS  ");
			sql.append("\n  AND od.ORDER_DATE = rc.RECEIPT_DATE) ");
		}
			break;
		case 2: {
			// today receipt, post order
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS  ");
			sql.append("\n  AND od.ORDER_DATE <> rc.RECEIPT_DATE) ");
		}
			break;
		case 3: {
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) <= DATE('" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS ) ");
		}
			break;
		default:
			break;
		}
		sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
		// Wit Edit 18/05/2011
		sql.append("\n  AND rc.user_id = "+user.getId());
		
		logger.debug("sql:"+sql.toString());
		stmt = conn.createStatement();
		rst = stmt.executeQuery(sql.toString());
		int i = 1;
		while (rst.next()) {
			inv = new InvoicePaymentReport();
	
			inv.setId(i++);
			inv.setInvName(rst.getString("INV_NAME"));
			inv.setDescription(rst.getString("DESCRIPTION"));
			inv.setReceiptDate(DateToolsUtil.convertToString(rst.getDate("RECEIPT_DATE")));
			inv.setCode(rst.getString("CODE"));
			inv.setName(rst.getString("NAME"));
			inv.setCustomerName(rst.getString("CUSTOMER_NAME"));
			inv.setCustomerCode(rst.getString("CUSTOMER_CODE"));
			inv.setOrderNo(rst.getString("ORDER_NO"));
			inv.setOrderDate(DateToolsUtil.convertToString(rst.getDate("ORDER_DATE")));
			inv.setBank(rst.getString("BANK"));
			inv.setChequeNo(rst.getString("CHEQUE_NO"));
			if (!inv.getChequeNo().equals("")) {
				inv.setChequeDate(DateToolsUtil.convertToString(rst.getDate("CHEQUE_DATE")));
			}
			inv.setChequeAmt(rst.getDouble("CHEQUE_AMT"));
			inv.setCashWriteOff(rst.getDouble("CASH_WRITEOFF"));
			inv.setReceiptAmount(inv.getChequeAmt() + inv.getCashWriteOff());
			if (type == 1) inv.setIsCurrent("Y");
			else inv.setIsCurrent("N");
			pos.add(inv);
		}
	} catch (Exception e) {
		throw e;
	} finally {
		try {
			rst.close();
			stmt.close();
		} catch (Exception e2) {}
	}
	
	return pos;
	}
	public int countReport(InvoicePaymentReport t, User user, Connection conn, int type)throws Exception {
	Statement stmt = null;
	ResultSet rst = null;
    int count = 0;
	StringBuilder sql = new StringBuilder();
	try {
		sql.append("\n  SELECT count(distinct(rc.receipt_no)) as count ");
		sql.append("\n  FROM t_receipt rc ");
		sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
		sql.append("\n  INNER JOIN t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
		sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
		sql.append("\n  INNER JOIN t_order od ON rcl.ORDER_ID = od.ORDER_ID ");
		sql.append("\n  INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
		sql.append("\n  INNER JOIN ad_user us ON rc.USER_ID = us.USER_ID ");
		sql.append("\n  LEFT JOIN m_sub_inventory inv ON inv.NAME = us.CODE ");
		sql.append("\n  WHERE rcby.PAYMENT_METHOD IN('CS','CR')");
		sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
	
		switch (type) {
		case 1: {
			// today Cash Count
			sql.append("\n  AND rcby.PAYMENT_METHOD ='CS'");
			sql.append("\n  AND rc.receipt_date = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS )  ");
		}
			break;
		case 2: {
			// today CreditCard Count
			sql.append("\n  AND rcby.PAYMENT_METHOD ='CR'");
			sql.append("\n  AND rc.receipt_date = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS )  ");
		}
			break;
		case 3: {
			// Before Cash Count
			sql.append("\n  AND rcby.PAYMENT_METHOD ='CS'");
			sql.append("\n  AND rc.RECEIPT_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n  AND rc.RECEIPT_DATE < '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "' ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS )  ");
		}
			break;
		case 4: {
			// Before Credit Count
			sql.append("\n  AND rcby.PAYMENT_METHOD ='CR'");
			sql.append("\n  AND rc.RECEIPT_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n  AND rc.RECEIPT_DATE < '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "' ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS )  ");
		}
			break;
		case 5: {
			//ALL
			sql.append("\n  AND rc.receipt_date >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
			sql.append("\n  AND rc.receipt_date <= DATE('" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS ) ");
		}
			break;
		default:
			break;
		}
		sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
		sql.append("\n  AND rc.user_id = "+user.getId());
		
		logger.debug("sql:"+sql.toString());
		stmt = conn.createStatement();
		rst = stmt.executeQuery(sql.toString());
		int i = 1;
		while (rst.next()) {
			count = rst.getInt("count");
		}
	} catch (Exception e) {
		throw e;
	} finally {
		try {
			rst.close();
			stmt.close();
		} catch (Exception e2) {}
	}
	
	return count;
	}
	public double[] sumCashReceipt(InvoicePaymentReport t, User user, Connection conn) throws Exception {
		double cashReceipt = 0;
		double receiptCnt = 0;
		double[] value = new double[2];
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS CASH_AMOUNT, COUNT(rcby.RECEIPT_ID) AS RECEIPT_CNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE rc.PD_PAYMENTMETHOD  = 'CS' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.receipt_date = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			sql.append("\n  AND rcl.ORDER_ID IN (SELECT ORDER_ID FROM t_order ");
			sql.append("\n  WHERE t_order.DOC_STATUS = 'SV') ");
			//sql.append("\n  AND t_order.ORDER_DATE = rc.RECEIPT_DATE) ");
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
            
			logger.debug("sql:"+sql.toString());
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				cashReceipt = rst.getDouble("CASH_AMOUNT");
				receiptCnt = rst.getDouble("RECEIPT_CNT");
			}

			value[0] = cashReceipt;
			value[1] = receiptCnt;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		return value;
	}
	
	public double[] sumCreditCardReceipt(InvoicePaymentReport t, User user, Connection conn) throws Exception {
		double cashReceipt = 0;
		double receiptCnt = 0;
		double[] value = new double[2];
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS CASH_AMOUNT, COUNT(rcby.RECEIPT_ID) AS RECEIPT_CNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE rcby.PAYMENT_METHOD = 'CR' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.receipt_date = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			sql.append("\n  AND rcl.ORDER_ID IN (SELECT ORDER_ID FROM t_order ");
			sql.append("\n  WHERE t_order.DOC_STATUS = 'SV' )");
			//sql.append("\n  AND t_order.ORDER_DATE = rc.RECEIPT_DATE) ");
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
            
			logger.debug("sql:"+sql.toString());
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				cashReceipt = rst.getDouble("CASH_AMOUNT");
				receiptCnt = rst.getDouble("RECEIPT_CNT");
			}

			value[0] = cashReceipt;
			value[1] = receiptCnt;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		return value;
	}
	public double[] sumCashAmtBefore(InvoicePaymentReport t, User user, Connection conn)throws Exception{
		double cashDayBefore = 0;
		double receiptCnt = 0;
		double value[] = new double[2];
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS POST_CASH, ");
			sql.append("\n  COUNT(rcby.RECEIPT_ID) AS RECEIPT_CNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			/** Wit Edit :2311/2558 :Not include Credit Card */
			sql.append("\n  AND rcby.PAYMENT_METHOD <> 'CR' ");
			
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			sql.append("\n  AND rc.RECEIPT_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n  AND rc.RECEIPT_DATE < '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "' ");
			
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
			
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT ORDER_ID FROM t_order ");
			sql.append("\n  WHERE t_order.DOC_STATUS = 'SV' ");
			//sql.append("\n  AND t_order.ISCASH = 'Y' ");
			sql.append("\n  AND t_order.ORDER_DATE = rc.RECEIPT_DATE) ");
		
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			logger.debug("sql:"+sql.toString());
			if(rs.next()){
				cashDayBefore = rs.getDouble("POST_CASH");
				receiptCnt = rs.getDouble("RECEIPT_CNT");
			}
			value[0] = cashDayBefore;
			value[1] = receiptCnt;
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return value;
	}
	public double[] sumChequeDayBeforeCaseCredit(InvoicePaymentReport t, User user, Connection conn)throws Exception{
		double cashDayBefore = 0;
		double chequeDayBefore = 0;
		double value[] = new double[2];
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		logger.debug("sumChequeDayBeforeCaseCredit");
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT A.* FROM ( ");
			sql.append("\n  SELECT IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rcby.PAYMENT_METHOD,rc.PD_PAYMENTMETHOD) as PAYMENTMETHOD" );
			sql.append("\n  ,SUM(rcby.RECEIPT_AMOUNT) AS RECEIPT_AMOUNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rcby.PAYMENT_METHOD,rc.PD_PAYMENTMETHOD) = 'CH' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.RECEIPT_DATE,rc.PDPAID_DATE) >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.RECEIPT_DATE,rc.PDPAID_DATE) < DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
			
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n    SELECT ORDER_ID FROM t_order ");
			sql.append("\n    WHERE t_order.DOC_STATUS = 'SV' ");
			sql.append("\n    AND t_order.ISCASH = 'N' ");
			sql.append("\n  ) )A");
			sql.append("\n  GROUP BY A.PAYMENTMETHOD ");
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			if(rs.next()){
				if("CS".equals(rs.getString("PAYMENTMETHOD"))){
				   cashDayBefore = rs.getDouble("RECEIPT_AMOUNT");
				}else{
				   chequeDayBefore = rs.getDouble("RECEIPT_AMOUNT");
				}
			}
			value[0] = cashDayBefore;
			value[1] = chequeDayBefore;
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return value;
	}
	
	public double[] sumChequeDayBefore(InvoicePaymentReport t, User user, Connection conn)throws Exception{
		double cashDayBefore = 0;
		double receiptCnt = 0;
		double value[] = new double[2];
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS POST_CASH, ");
			sql.append("\n  COUNT(rcby.RECEIPT_ID) AS RECEIPT_CNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rcby.PAYMENT_METHOD,rc.PD_PAYMENTMETHOD) = 'CH' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.RECEIPT_DATE,rc.PDPAID_DATE) >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.RECEIPT_DATE,rc.PDPAID_DATE) < DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
			
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT ORDER_ID FROM t_order ");
			sql.append("\n  WHERE t_order.DOC_STATUS = 'SV' ");
			sql.append("\n  AND t_order.ISCASH = 'Y' ");
			sql.append("\n  AND t_order.ORDER_DATE = rc.RECEIPT_DATE) ");
		
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			logger.debug("sql:"+sql.toString());
			if(rs.next()){
				cashDayBefore = rs.getDouble("POST_CASH");
				receiptCnt = rs.getDouble("RECEIPT_CNT");
			}
			value[0] = cashDayBefore;
			value[1] = receiptCnt;
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return value;
	}
	
	public double[] sumCurCancelCashAmt(InvoicePaymentReport t, User user, Connection conn)throws Exception{
		double cancelReceipt[] = new double[2];
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS CANCEL_AMOUNT ,count(*) as CANCEL_COUNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.receipt_date = '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "' ");
			sql.append("\n  AND rc.DOC_STATUS = 'VO' ");
			sql.append("\n  AND rc.PAYMENT_METHOD = 'CS' ");
			sql.append("\n  AND rc.user_id = "+user.getId());
		
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			logger.debug("sql:"+sql.toString());
			if(rs.next()){
				cancelReceipt[0] = rs.getDouble("CANCEL_AMOUNT");
				cancelReceipt[1] = rs.getDouble("CANCEL_COUNT");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		return cancelReceipt;
	}
	
	/* Create New Method to get Receipt Detail */
	public InvoicePaymentReport searchReport(InvoicePaymentReport report, User user,Connection conn)throws Exception { 
		List<InvoicePaymentReport> dataList = new ArrayList<InvoicePaymentReport>();
		InvoicePaymentReport dataReport = new InvoicePaymentReport();
		double totalCashAmt = 0;
		double totalCreditcardAmt = 0;
		try{
		StringBuffer reportSql = new StringBuffer(); 
		reportSql.append("\n SELECT M. * FROM( " );
		reportSql.append("\n  SELECT A. *  FROM ( ");
		reportSql.append("\n    SELECT inv.name as inv_name ");
		reportSql.append("\n    , inv.description " );
		reportSql.append("\n    , rcb.BANK ");
		reportSql.append("\n    , rcb.CHEQUE_NO");
		reportSql.append("\n    , rcb.CHEQUE_DATE ");
		reportSql.append("\n    , us.CODE, us.NAME ");
	  	reportSql.append("\n    , cus.NAME AS CUSTOMER_NAME");
		reportSql.append("\n    , cus.CODE AS CUSTOMER_CODE");
		reportSql.append("\n    , rch.receipt_no  ");
		reportSql.append("\n    , od.ORDER_NO");
		reportSql.append("\n    , rch.receipt_date ");
	  	reportSql.append("\n    , od.ORDER_DATE");
		reportSql.append("\n    , rcb.receipt_amount");
		reportSql.append("\n    , rcb.PAYMENT_METHOD " );
	  	reportSql.append("\n    , rch.ISPDPAID ");
	  	reportSql.append("\n    , rch.PD_PAYMENTMETHOD ");
	    reportSql.append("\n    , (CASE WHEN rcb.payment_method ='CS' THEN '1'" );
	    reportSql.append("\n        ELSE '2' END) as order_p ");
	            
	    reportSql.append("\n   FROM t_receipt rch ");
	  	reportSql.append("\n   INNER JOIN t_receipt_line rcl ON rch.receipt_id = rcl.receipt_id ");
	  	reportSql.append("\n   INNER JOIN t_receipt_by rcb ON rch.receipt_id = rcb.receipt_id ");
	  	reportSql.append("\n   INNER JOIN t_order od ON rcl.ORDER_ID = od.ORDER_ID ");
	  	reportSql.append("\n   INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
	  	reportSql.append("\n   INNER JOIN ad_user us ON rch.USER_ID = us.USER_ID ");
	  	reportSql.append("\n   LEFT JOIN m_sub_inventory inv ON inv.NAME = us.CODE ");
	  	reportSql.append("\n   WHERE rch.DOC_STATUS = 'SV' ");
	  	reportSql.append("\n   AND rcb.write_off = 'N' ");
	  	reportSql.append("\n   AND rch.user_id = ? ");
	  	reportSql.append("\n   AND rch.receipt_date = DATE(?) ");
		reportSql.append("\n  )A ");
	  	reportSql.append("\n )M ORDER BY M.order_p asc ");
		
		logger.debug("sql SearchReport:"+reportSql.toString());
		
		PreparedStatement ppstmt = conn.prepareStatement(reportSql.toString());
		ResultSet rset = null;
		List<InvoicePaymentReport> paymentL = new ArrayList<InvoicePaymentReport>();
		InvoicePaymentReport inv = null;
		
		ppstmt.setInt(1,user.getId());
		ppstmt.setTimestamp(2,DateToolsUtil.convertToTimeStamp(report.getReceiptDate()));
		rset = ppstmt.executeQuery();
		
		int i =0;
		boolean iscash = false;
		
		while(rset.next()){
			inv = new InvoicePaymentReport();
			
			inv.setId(i++);
			inv.setInvName(rset.getString("INV_NAME"));
			inv.setDescription(rset.getString("DESCRIPTION"));
			inv.setReceiptDate(DateToolsUtil.convertToString(rset.getDate("RECEIPT_DATE")));
			inv.setCode(rset.getString("CODE"));
			inv.setName(rset.getString("NAME"));
			inv.setCustomerName(rset.getString("CUSTOMER_NAME"));
			inv.setCustomerCode(rset.getString("CUSTOMER_CODE"));
			inv.setOrderNo(rset.getString("ORDER_NO"));
			inv.setOrderDate(DateToolsUtil.convertToString(rset.getDate("ORDER_DATE")));
			inv.setBank(rset.getString("BANK"));
			inv.setChequeNo(ConvertNullUtil.convertToString(rset.getString("CHEQUE_NO")));
			
			inv.setReceiptAmount(rset.getDouble("receipt_amount"));
			inv.setPaymentMethod(rset.getString("payment_method"));
			if("CS".equalsIgnoreCase(inv.getPaymentMethod())){
			   inv.setPaymentTerm("CASH");
			   iscash = true;
			   totalCashAmt += rset.getDouble("receipt_amount");
			}else{
			   inv.setPaymentTerm("CREDIT");//creditCard
			   inv.setCreditCardAmt(rset.getDouble("receipt_amount"));
			   totalCreditcardAmt += rset.getDouble("receipt_amount");
			   iscash = true;
			}
			paymentL.add(inv);
		}
		
		// Always Display Cash & Credit Group In The Report
		List<InvoicePaymentReport> resultL = new ArrayList<InvoicePaymentReport>();
		
		// FIXED BUG Always Show Two Group In Report
		// Add Blank Data To Always Show In Cash Group
		if(!iscash){
			inv = new InvoicePaymentReport();
			inv.setId(i++);
			inv.setCode(user.getCode());
			inv.setName(user.getName());
			inv.setReceiptAmount(0);
			inv.setPaymentTerm("CASH");
			resultL.add(inv);
		}
		
		 resultL.addAll(paymentL);
		 dataReport.setTotalCashAmt(totalCashAmt);
		 dataReport.setTotalCreditCardAmt(totalCreditcardAmt);
	     dataReport.setItemsList(resultL);
		 return dataReport;
	  }catch(Exception e){
			e.printStackTrace();
			throw e;
	 }
}

	public double sumCreditCardAmtBefore(InvoicePaymentReport t, User user,Connection conn) throws Exception 
	{
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		double creditSalesAmt = 0d;
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS CASH_AMOUNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE rc.PD_PAYMENTMETHOD IN ('CR') ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.RECEIPT_DATE >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
			sql.append("\n  AND rc.RECEIPT_DATE < DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rc.DOC_STATUS = 'SV' ");
			sql.append("\n  AND rcl.ORDER_ID IN (SELECT ORDER_ID FROM t_order ");
			sql.append("\n  WHERE t_order.DOC_STATUS = 'SV' )");
			// Wit Edit 18/05/2011
			sql.append("\n  AND rc.user_id = "+user.getId());
			
			logger.debug("sql:"+sql.toString());
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				creditSalesAmt = rst.getDouble("CASH_AMOUNT");
			}

			//value[0] = cashReceipt;
			//value[1] = receiptCnt;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return creditSalesAmt;
	}
}
