package com.isecinc.pens.report.invoicepayment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pens.util.Constants;

import com.isecinc.core.model.I_PO;
import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MSalesTargetNew;
import com.isecinc.pens.report.performance.PerformanceReport;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class InvoicePaymentAllReportProcess extends I_ReportProcess<InvoicePaymentAllReport> {

	/**
	 * Search for performance report.
	 */
	public List<InvoicePaymentAllReport> doReport(InvoicePaymentAllReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<InvoicePaymentAllReport> pos = new ArrayList<InvoicePaymentAllReport>();
		StringBuilder sql = new StringBuilder();
		try {
			
			sql.delete(0, sql.length());
			sql.append("\n  select * from( ");
			sql.append("\n  SELECT DISTINCT inv.NAME AS INV_NAME, inv.DESCRIPTION, ");
			sql.append("\n  rc.RECEIPT_DATE, us.CODE, us.NAME, cus.NAME AS CUSTOMER_NAME, ");
			sql.append("\n  cus.CODE AS CUSTOMER_CODE,rcby.WRITE_OFF, ");
			sql.append("\n  rcby.BANK, rcby.CHEQUE_NO, rcby.CHEQUE_DATE, rc.doc_status,");
			sql.append("\n  rc.ISPDPAID , ");
			sql.append("\n  (rcby.RECEIPT_AMOUNT) AS RECEIPT_AMT, ");
			sql.append("\n  rc.receipt_no,rcby.PAYMENT_METHOD ");
			sql.append("\n  FROM pensso.t_receipt rc ");
			sql.append("\n  INNER JOIN pensso.t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
			sql.append("\n  INNER JOIN pensso.m_customer cus ON rc.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  INNER JOIN pensso.ad_user us ON rc.USER_ID = us.USER_ID ");
			sql.append("\n  LEFT JOIN pensso.m_sub_inventory inv ON inv.NAME = us.CODE ");
			sql.append("\n  WHERE 1=1");
			sql.append("\n  AND rc.RECEIPT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			sql.append("\n  AND rc.RECEIPT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			sql.append("\n  AND rcl.INVOICE_ID IN ( ");
			sql.append("\n    SELECT invoice_id FROM pensso.t_invoice  ");
			sql.append("\n   )");
			sql.append("\n  AND rc.user_id = "+user.getId());
			sql.append("\n  AND rcby.PAYMENT_METHOD = 'CS'");
			
			// Art Edit 14/09/2011
			sql.append("\n  UNION ALL");
			
			sql.append("\n  SELECT DISTINCT inv.NAME AS INV_NAME, inv.DESCRIPTION, ");
			sql.append("\n  rc.RECEIPT_DATE, us.CODE, us.NAME, cus.NAME AS CUSTOMER_NAME, ");
			sql.append("\n  cus.CODE AS CUSTOMER_CODE,rcby.WRITE_OFF, ");
			sql.append("\n  rcby.BANK, rcby.CHEQUE_NO, rcby.CHEQUE_DATE, rc.doc_status,");
			sql.append("\n  rc.ISPDPAID , ");
			sql.append("\n  (rcby.RECEIPT_AMOUNT) AS RECEIPT_AMT, ");
			sql.append("\n  rc.receipt_no,rcby.PAYMENT_METHOD ");
			sql.append("\n	FROM pensso.t_receipt rc ");
			sql.append("\n  INNER JOIN pensso.t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
			sql.append("\n  INNER JOIN pensso.m_customer cus ON rc.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  INNER JOIN pensso.ad_user us ON rc.USER_ID = us.USER_ID ");
			sql.append("\n  LEFT JOIN pensso.m_sub_inventory inv ON inv.NAME = us.CODE ");
			sql.append("\n  WHERE 1=1");
			sql.append("\n  AND rc.RECEIPT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			sql.append("\n  AND rc.RECEIPT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			     
			sql.append("\n  AND rcl.INVOICE_ID IN ( ");
			sql.append("\n    SELECT invoice_id FROM pensso.t_invoice  ");
			sql.append("\n   )");
			sql.append("\n  AND rc.user_id = "+user.getId());
			sql.append("\n  AND rcby.PAYMENT_METHOD = 'CH'");
			sql.append("\n ) b");
			sql.append("\n order by b.RECEIPT_NO asc,b.PAYMENT_METHOD desc  ");
			
			logger.debug("sql:"+sql.toString());
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			String tempReceiptNo = "";
			int i = 1;
			while (rst.next()) {
				InvoicePaymentAllReport inv = new InvoicePaymentAllReport();
				
				//inv.setReceiptDate(DateToolsUtil.convertToString(rst.getDate("RECEIPT_DATE")));
				inv.setCode(rst.getString("CODE"));
				inv.setName(rst.getString("NAME"));
				inv.setWriteOff(rst.getString("WRITE_OFF"));
				//inv.setCustomerName(rst.getString("CUSTOMER_NAME"));
				inv.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				
				if(!tempReceiptNo.equalsIgnoreCase(rst.getString("RECEIPT_NO"))){
					inv.setReceiptNo(rst.getString("RECEIPT_NO"));
					inv.setCustomerName(rst.getString("CUSTOMER_NAME"));
					inv.setReceiptDate(DateToolsUtil.convertToString(rst.getDate("RECEIPT_DATE")));
					inv.setId(i++);
					tempReceiptNo = rst.getString("RECEIPT_NO");
				}
				
				//inv.setBank(rst.getString("BANK"));
				inv.setChequeNo(ConvertNullUtil.convertToString(rst.getString("CHEQUE_NO")));
				if (!inv.getChequeNo().equals("")) {
					//inv.setChequeDate(DateToolsUtil.convertToString(rst.getDate("CHEQUE_DATE")));
				}
				
				inv.setPaymentMethod(rst.getString("PAYMENT_METHOD"));
				inv.setReceiptAmount(rst.getDouble("RECEIPT_AMT"));
				
				inv.setStatus(rst.getString("doc_status"));
				
				inv.setIsPDPaid(rst.getString("ISPDPAID"));
				
				pos.add(inv);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}

		return pos;
	}

	public int getCountCustomer(InvoicePaymentAllReport t, User user, Connection conn) throws Exception {
		int countVisit = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT COUNT(distinct customer_id) AS COUNT_CUSTOMER ");
			sql.append("\n  FROM t_receipt ");
			sql.append("\n  LEFT JOIN ad_user ON t_receipt.USER_ID = ad_user.USER_ID ");
			sql.append("\n  WHERE ad_user.USER_ID = " + user.getId());
			sql.append("\n  AND t_receipt.DOC_STATUS = '"+I_PO.STATUS_LOADING+"' ");
			sql.append("\n  AND t_receipt.RECEIPT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			sql.append("\n  AND t_receipt.RECEIPT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
            
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				countVisit = rst.getInt("COUNT_CUSTOMER");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		return countVisit;
	}
	
	public int[] getCountReceiptItem(InvoicePaymentAllReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        int r[] = new int[2];
		try {
			sql.delete(0, sql.length());
			sql.append("\n select SUM(A.COUNT_ALL) as COUNT_ALL ,SUM(A.COUNT_CANCEL) AS COUNT_CANCEL from(");
			sql.append("\n  SELECT  COUNT(distinct (rc.receipt_no))  AS COUNT_ALL  ,0 as COUNT_CANCEL ");
			sql.append("\n  FROM pensso.t_receipt rc ");
			sql.append("\n  INNER JOIN pensso.t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
			sql.append("\n  INNER JOIN pensso.m_customer cus ON rc.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  INNER JOIN pensso.ad_user us ON rc.USER_ID = us.USER_ID ");
			sql.append("\n  LEFT JOIN pensso.m_sub_inventory inv ON inv.NAME = us.CODE ");
			sql.append("\n  WHERE 1=1");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.RECEIPT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			sql.append("\n  AND rc.RECEIPT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			     
			sql.append("\n  AND rcl.INVOICE_ID IN ( ");
			sql.append("\n    SELECT invoice_id FROM pensso.t_invoice  ");
			sql.append("\n   )");
			sql.append("\n  AND rc.user_id = "+user.getId());
			
			sql.append("\n  UNION ALL ");
			
			sql.append("\n  SELECT 0 AS COUNT_ALL , COUNT(distinct (rc.receipt_no)) AS  COUNT_CANCEL  ");
			sql.append("\n  FROM pensso.t_receipt rc ");
			sql.append("\n  INNER JOIN pensso.t_receipt_line rcl ON rcl.RECEIPT_ID = rc.RECEIPT_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_match ON t_receipt_match.RECEIPT_LINE_ID = rcl.RECEIPT_LINE_ID ");
			sql.append("\n  INNER JOIN pensso.t_receipt_by rcby ON rcby.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID  ");
			sql.append("\n  INNER JOIN pensso.m_customer cus ON rc.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  INNER JOIN pensso.ad_user us ON rc.USER_ID = us.USER_ID ");
			sql.append("\n  LEFT JOIN pensso.m_sub_inventory inv ON inv.NAME = us.CODE ");
			sql.append("\n  WHERE 1=1");
			sql.append("\n  AND rc.DOC_STATUS = 'VO' ");
			sql.append("\n  AND rcby.WRITE_OFF = 'N' ");
			sql.append("\n  AND rc.RECEIPT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			sql.append("\n  AND rc.RECEIPT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(t.getReceiptDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n ");
			     
			sql.append("\n  AND rcl.INVOICE_ID IN ( ");
			sql.append("\n    SELECT invoice_id FROM pensso.t_invoice  ");
			sql.append("\n   )");
			sql.append("\n  AND rc.user_id = "+user.getId());
			sql.append("\n  ) A  ");
			
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				r[0] = rst.getInt("COUNT_ALL");
				r[1] = rst.getInt("COUNT_CANCEL");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		return r;
	}
	
}
