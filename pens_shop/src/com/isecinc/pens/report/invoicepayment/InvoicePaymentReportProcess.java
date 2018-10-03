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

/**
 * Invoice Payment Report Process
 * 
 * @author Aneak.t
 * @version $Id: InvoicePaymentReportProcess.java,v 1.0 12/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentReportProcess extends I_ReportProcess<InvoicePaymentReport> {

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
		sql.delete(0, sql.length());
		sql.append("\n  SELECT count(distinct(rc.receipt_no)) as count ");
		sql.append("\n  FROM t_receipt rc ");
		sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
		sql.append("\n  INNER JOIN t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
		sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
		sql.append("\n  INNER JOIN t_order od ON rcl.ORDER_ID = od.ORDER_ID ");
		sql.append("\n  INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
		sql.append("\n  INNER JOIN ad_user us ON rc.USER_ID = us.USER_ID ");
		sql.append("\n  LEFT JOIN m_sub_inventory inv ON inv.NAME = us.CODE ");
		sql.append("\n  WHERE IF(rc.ISPDPAID IS NULL  OR rc.ISPDPAID ='',rcby.PAYMENT_METHOD ,rc.PD_PAYMENTMETHOD) IN('CH','CS')");
		sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
	
		switch (type) {
		case 1: {
			// today receipt, today order
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS )  ");
			//sql.append("\n  AND od.ORDER_DATE = rc.RECEIPT_DATE) ");
		}
			break;
		case 2: {
			// today receipt, post order
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rcl.ORDER_ID IN ( ");
			sql.append("\n  SELECT order_id FROM t_order od  ");
			sql.append("\n  WHERE od.DOC_STATUS = rc.DOC_STATUS )  ");
			//sql.append("\n  AND od.ORDER_DATE <> rc.RECEIPT_DATE) ");
		}
			break;
		case 3: {
			//ALL
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
			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS CASH_AMOUNT, COUNT(rcby.RECEIPT_ID) AS RECEIPT_CNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE IF(rc.ISPDPAID IS NULL  OR rc.ISPDPAID ='' ,rcby.PAYMENT_METHOD , rc.PD_PAYMENTMETHOD ) = 'CS' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
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
	
	public double[] sumChequeReceipt(InvoicePaymentReport t, User user, Connection conn) throws Exception {
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
			sql.append("\n  WHERE IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rcby.PAYMENT_METHOD,rc.PD_PAYMENTMETHOD) = 'CH' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
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
	public double[] sumCashDayBefore(InvoicePaymentReport t, User user, Connection conn)throws Exception{
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
			sql.append("\n  WHERE (rc.ISPDPAID IS NULL  OR rc.ISPDPAID ='') ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
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
	
	public double[] sumCancelReceiptAmount(InvoicePaymentReport t, User user, Connection conn)throws Exception{
		double cancelReceipt[] = new double[2];
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM(rcby.RECEIPT_AMOUNT) AS CANCEL_AMOUNT ,count(*) as CANCEL_COUNT ");
			sql.append("\n  FROM t_receipt rc ");
			sql.append("\n  INNER JOIN t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN t_receipt_by rcby ON rcby.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND IF(rc.ISPDPAID IS NULL OR rc.ISPDPAID ='',rc.receipt_date,rc.PDPAID_DATE) = DATE(rc.RECEIPT_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
			sql.append("\n  AND rc.DOC_STATUS = 'VO' ");
			//sql.append("\n  AND rcl.ORDER_ID IN (SELECT ORDER_ID FROM t_order ");
			//sql.append("\n  WHERE t_order.DOC_STATUS = 'VO' ");
			//sql.append("\n  AND t_order.ORDER_DATE = rc.RECEIPT_DATE) ");
			// Wit Edit 18/05/2011
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
	public List<InvoicePaymentReport> searchReport(InvoicePaymentReport report, User user,Connection conn)throws Exception 
	{ 
		StringBuffer reportSql = new StringBuffer(); 
		reportSql = reportSql.append("\n SELECT inv.name as inv_name , inv.description , rcb.BANK , rcb.CHEQUE_NO, rcb.CHEQUE_DATE , us.CODE, us.NAME ")
						  	.append("\n, cus.NAME AS CUSTOMER_NAME, cus.CODE AS CUSTOMER_CODE, rch.receipt_no  , od.ORDER_NO, rch.receipt_date ")
						  	.append("\n, od.ORDER_DATE, rcb.receipt_amount" +
						  			" ,IF(rch.ISPDPAID IS NULL OR rch.ISPDPAID =''" +
						  			",rcb.payment_method " +
						  			", rch.PD_PAYMENTMETHOD) as PAYMENT_METHOD" +
						  			", IF(rch.ISPDPAID IS NULL OR rch.ISPDPAID =''" +
						  			",IF(od.ORDER_DATE <>rch.receipt_date ,'CREDIT','CASH' ),'CREDIT') as payment_term" +
						  			" ,rch.ISPDPAID " +
						  			", rch.PD_PAYMENTMETHOD ")
						  	.append("\n FROM t_receipt rch ")
						  	.append("\n INNER JOIN t_receipt_line rcl ON rch.receipt_id = rcl.receipt_id ")
						  	.append("\n INNER JOIN t_receipt_by rcb ON rch.receipt_id = rcb.receipt_id ")
						  	.append("\n INNER JOIN t_order od ON rcl.ORDER_ID = od.ORDER_ID ")
						  	.append("\n INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ")
						  	.append("\n INNER JOIN ad_user us ON rch.USER_ID = us.USER_ID ")
						  	.append("\n LEFT JOIN m_sub_inventory inv ON inv.NAME = us.CODE ")
						  	.append("\n WHERE rch.DOC_STATUS = 'SV' ")
						  	.append("\n AND rcb.write_off = 'N' ")
						  	.append("\n AND rch.user_id = ? ")
						  	.append("\n AND IF(rch.ISPDPAID IS NULL OR rch.ISPDPAID ='',rch.receipt_date,rch.PDPAID_DATE) = DATE(?) ")
						  	.append("\n ORDER BY payment_term ");
		
		logger.debug("sql:\n"+reportSql.toString());
		
		PreparedStatement ppstmt = conn.prepareStatement(reportSql.toString());
		ResultSet rset = null;
		List<InvoicePaymentReport> paymentL = new ArrayList<InvoicePaymentReport>();
		InvoicePaymentReport inv = null;
		
		ppstmt.setInt(1,user.getId());
		ppstmt.setTimestamp(2,DateToolsUtil.convertToTimeStamp(report.getReceiptDate()));
		rset = ppstmt.executeQuery();
		
		int i =0;
		
		boolean iscash = false;
		boolean iscredit = false;
		
		while(rset.next()){
			inv = new InvoicePaymentReport();
			
			boolean isPDPaid = "Y".equals(rset.getString("ISPDPAID")) ;
			boolean isPDCheque = "CH".equals(rset.getString("PD_PAYMENTMETHOD")) ;
			
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
			
			if (!inv.getChequeNo().equals("") || (isPDPaid && isPDCheque)) {
				inv.setChequeDate(DateToolsUtil.convertToString(rset.getDate("CHEQUE_DATE")));
				inv.setChequeAmt(rset.getDouble("receipt_amount"));
			}
			
			inv.setReceiptAmount(rset.getDouble("receipt_amount"));
			inv.setPaymentMethod(rset.getString("payment_method"));
			inv.setPaymentTerm(rset.getString("payment_term"));
			
			// Check Need To Add Blank Data To Show Or Not
			if(!iscash && "CASH".equals(rset.getString("payment_term"))){
				iscash = true;
			}
			
			// Check Need To Add Blank Data To Show Or Not
			if(!iscredit && "CREDIT".equals(rset.getString("payment_term"))){
				iscredit = true;
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
		
		// Add Blank Data To Always Show In Credit Group
		if(!iscredit){
			inv = new InvoicePaymentReport();
			inv.setId(i++);
			inv.setCode(user.getCode());
			inv.setName(user.getName());
			inv.setReceiptAmount(0);
			inv.setPaymentTerm("CREDIT");
			resultL.add(inv);
		}
		// END FIXED BUG Always Show Two Group In Report
		
		return resultL;
	}

	public double sumCreditSalesAmtBefore(InvoicePaymentReport t, User user,Connection conn) throws Exception 
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
			sql.append("\n  WHERE rc.PD_PAYMENTMETHOD IN ('CS','CH') ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.ISPDPAID = 'Y' ");
			sql.append("\n  AND rc.PDPAID_DATE >= DATE('" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "') ");
			sql.append("\n  AND rc.PDPAID_DATE < DATE('" + DateToolsUtil.convertToTimeStamp(t.getReceiptDate()) + "') ");
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
