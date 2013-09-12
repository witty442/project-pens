package com.isecinc.pens.report.performance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MSalesTargetNew;

/**
 * Performance Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class PerformanceReportProcess extends I_ReportProcess<PerformanceReport> {

	/**
	 * Search for performance report.
	 */
	public List<PerformanceReport> doReport(PerformanceReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<PerformanceReport> pos = new ArrayList<PerformanceReport>();
		PerformanceReport p = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT @rownum:=@rownum+1 as no , od.ORDER_DATE, us.CODE, us.NAME, od.ORDER_NO, ");
			sql.append("\n  (SELECT SUM(DISCOUNT) FROM t_order_line ,(SELECT @rownum:=0) r ");
			sql.append("\n  WHERE t_order_line.ORDER_ID = od.ORDER_ID AND t_order_line.ISCANCEL ='N' ) AS DISCOUNT, ");
			// sql.append("\n  CASE od.PAYMENT WHEN 'Y' THEN od.NET_AMOUNT ELSE 0 END AS CASH_AMOUNT, ");
			// sql.append("\n  CASE od.PAYMENT WHEN 'N' THEN  od.NET_AMOUNT ELSE 0 END AS RECEIPT_AMOUNT, ");
			// sql.append("\n  CASE od.PAYMENT WHEN 'Y' THEN od.VAT_AMOUNT ELSE 0 END AS VAT_CASH, ");
			// sql.append("\n  CASE od.PAYMENT WHEN 'N' THEN od.VAT_AMOUNT ELSE 0 END AS VAT_RECEIPT, ");
			sql.append("\n  CASE od.ISCASH WHEN 'Y' THEN od.NET_AMOUNT ELSE 0 END AS CASH_AMOUNT, ");
			sql.append("\n  CASE od.ISCASH WHEN 'N' THEN  od.NET_AMOUNT ELSE 0 END AS RECEIPT_AMOUNT, ");
			sql.append("\n  CASE od.ISCASH WHEN 'Y' THEN od.VAT_AMOUNT ELSE 0 END AS VAT_CASH, ");
			sql.append("\n  CASE od.ISCASH WHEN 'N' THEN od.VAT_AMOUNT ELSE 0 END AS VAT_RECEIPT, ");
			sql.append("\n  od.VAT_AMOUNT, od.NET_AMOUNT, cus.CODE AS CUSTOMER_CODE, cus.NAME AS CUSTOMER_NAME ");
			
			sql.append("\n ,( select min(t_receipt_by.cheque_no) ");
			sql.append("\n    from t_receipt_line  ,t_receipt_match , t_receipt_by ");
			sql.append("\n    where od.order_id = t_receipt_line.order_id ");
			sql.append("\n    and t_receipt_match.RECEIPT_LINE_ID = t_receipt_line.RECEIPT_LINE_ID ");
			sql.append("\n    and t_receipt_by.RECEIPT_BY_ID = t_receipt_match.RECEIPT_BY_ID ");
			sql.append("\n    and t_receipt_by.PAYMENT_METHOD ='CH' ");
			sql.append("\n  ) as cheque_no ");
			
			sql.append("\n  ,od.doc_status as status ");
			sql.append("\n  FROM t_order od ");
			sql.append("\n  INNER JOIN ad_user us ON od.USER_ID = us.USER_ID ");
			sql.append("\n  INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  WHERE cus.CUSTOMER_TYPE = 'CV' ");
			sql.append("\n  AND od.ORDER_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
			sql.append("\n  AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			/** Wit Edit15/08/2554 :show all **/ 
			//sql.append("\n  AND cus.ISACTIVE = 'Y' ");
			//sql.append("\n  AND od.DOC_STATUS = 'SV' ");
			sql.append("\n  AND us.USER_ID = " + user.getId());
			sql.append("\n  ORDER BY od.ORDER_ID ASC ");
			sql.append("\n ");
            
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			while (rst.next()) {
				p = new PerformanceReport();
				p.setId(i++);
				p.setNo(rst.getInt("no"));
				p.setOrderDate(rst.getString("ORDER_DATE"));
				p.setCode(rst.getString("CODE"));
				p.setName(rst.getString("NAME"));
				p.setOrderNo(rst.getString("ORDER_NO"));
				p.setDiscount(rst.getDouble("DISCOUNT"));
				p.setCashAmount(rst.getDouble("CASH_AMOUNT"));
				p.setReceiptAmount(rst.getDouble("RECEIPT_AMOUNT"));
				p.setVatAmount(rst.getDouble("VAT_AMOUNT"));
				p.setNetAmount(rst.getDouble("NET_AMOUNT"));
				p.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				p.setCustomerName(rst.getString("CUSTOMER_NAME"));
				p.setVatCash(rst.getDouble("VAT_CASH"));
				p.setVatReceipt(rst.getDouble("VAT_RECEIPT"));
                p.setChequeNo(rst.getString("cheque_no"));
                p.setStatus(rst.getString("status"));
				pos.add(p);
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

	/**
	 * Get sum all start month to date select.
	 * 
	 * @return
	 */
	public PerformanceReport getSumAll(PerformanceReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		PerformanceReport p = null;
		StringBuilder sql = new StringBuilder();
		String startDate = "";

		try {
			Calendar c = Calendar.getInstance();
			c.setTime(DateToolsUtil.convertStringToDate(t.getOrderDate()));
			c.set(Calendar.DAY_OF_MONTH, 1);
			startDate = DateToolsUtil.convertToString(c.getTime());

			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM((SELECT SUM(DISCOUNT) FROM t_order_line ");
			sql.append("\n  WHERE t_order_line.ORDER_ID = od.ORDER_ID AND t_order_line.ISCANCEL ='N')) AS DISCOUNT, ");
			sql.append("\n  SUM(od.VAT_AMOUNT) AS VAT_AMOUNT, SUM(od.NET_AMOUNT) AS NET_AMOUNT, ");
			sql.append("\n  (SELECT SUM(m_sales_target_new.TARGET_QTY) FROM m_sales_target_new ");
			sql.append("\n  INNER JOIN ad_user ON m_sales_target_new.USER_ID = ad_user.USER_ID ");
			sql.append("\n  WHERE m_sales_target_new.TARGET_FROM >= '" + DateToolsUtil.convertToTimeStamp(startDate)
					+ "' ");
			sql.append("\n  AND m_sales_target_new.TARGET_TO <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate())
					+ "' ");
			sql.append("\n  AND ad_user.USER_ID = us.USER_ID ) AS TARGET_QTY, ");
			// sql.append("\n  SUM(CASE od.PAYMENT WHEN 'Y' THEN od.VAT_AMOUNT ELSE 0 END) AS VAT_CASH_AMT, ");
			// sql.append("\n  SUM(CASE od.PAYMENT WHEN 'N' THEN od.VAT_AMOUNT ELSE 0 END) AS VAT_RECEIPT_AMT ");
			sql.append("\n  SUM(CASE od.ISCASH WHEN 'Y' THEN od.VAT_AMOUNT ELSE 0 END) AS VAT_CASH_AMT, ");
			sql.append("\n  SUM(CASE od.ISCASH WHEN 'N' THEN od.VAT_AMOUNT ELSE 0 END) AS VAT_RECEIPT_AMT ");

			sql.append("\n  FROM t_order od  INNER JOIN ad_user us ON od.USER_ID = us.USER_ID ");
			sql.append("\n  INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  WHERE cus.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "' ");
			sql.append("\n  AND od.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ");
			sql.append("\n  AND od.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
			sql.append("\n  AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			/** Wit Edit 15/08/2554 :Show All record **/
			//sql.append("\n  AND cus.ISACTIVE = 'Y' ");
			sql.append("\n  AND od.DOC_STATUS = 'SV' ");
			sql.append("\n  AND us.USER_ID = " + user.getId());
			sql.append("\n  ORDER BY od.ORDER_ID ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				p = new PerformanceReport();

				p.setAllDiscount(rst.getDouble("DISCOUNT"));
				p.setAllVatAmount(rst.getDouble("VAT_AMOUNT"));
				p.setAllNetAmount(rst.getDouble("NET_AMOUNT"));
				p.setAllVatCashAmount(rst.getDouble("VAT_CASH_AMT"));
				p.setAllVatReceiptAmount(rst.getDouble("VAT_RECEIPT_AMT"));
			}

			// Get cash amount & receipt amount.
			sql.delete(0, sql.length());
			sql.append("\n  SELECT ");
			// sql.append("\n  CASE t_order.PAYMENT WHEN 'Y' THEN t_order.NET_AMOUNT ELSE 0 END AS CASH_AMOUNT, ");
			// sql.append("\n  CASE t_order.PAYMENT WHEN 'N' THEN t_order.NET_AMOUNT ELSE 0 END AS RECEIPT_AMOUNT ");
			sql.append("\n  CASE t_order.ISCASH WHEN 'Y' THEN t_order.NET_AMOUNT ELSE 0 END AS CASH_AMOUNT, ");
			sql.append("\n  CASE t_order.ISCASH WHEN 'N' THEN t_order.NET_AMOUNT ELSE 0 END AS RECEIPT_AMOUNT ");
			sql.append("\n  FROM t_order ");
			sql.append("\n  WHERE t_order.USER_ID = " + user.getId());
			sql.append("\n  AND t_order.DOC_STATUS = 'SV' ");
			sql.append("\n  AND t_order.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ");
			sql.append("\n  AND t_order.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
			sql.append("\n  GROUP BY t_order.ORDER_ID ");

			logger.info("sql receipt :"+sql.toString());
			
			rst = stmt.executeQuery(sql.toString());
			double allCashAmount = 0;
			double allReceiptAmount = 0;
			while (rst.next()) {
				allCashAmount += rst.getDouble("CASH_AMOUNT");
				allReceiptAmount += rst.getDouble("RECEIPT_AMOUNT");
			}

			p.setAllCashAmount(allCashAmount);
			p.setAllReceiptAmount(allReceiptAmount);
			
			/** WIT Edit 15/08/2554 :find totalCancelAmount Today **/
			// Get TotalAmount Cancel Today
			sql.delete(0, sql.length());
			sql.append("\n  SELECT ");
			sql.append("\n  SUM(t_order.net_amount) AS cancel_amount  ");
			sql.append("\n  FROM t_order ");
			sql.append("\n  WHERE t_order.USER_ID = " + user.getId());
			sql.append("\n  AND t_order.DOC_STATUS = 'VO' ");
			sql.append("\n  AND t_order.ORDER_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
            
			logger.debug("sum cancel :"+sql.toString());
			rst = stmt.executeQuery(sql.toString());
			
			while (rst.next()) {
				p.setTotalCancelAmountToday(rst.getDouble("cancel_amount"));
			}
			/********************************************************/

			// Get targer amount.
			sql.delete(0, sql.length());
			double allTargetAmount = 0;
			String whereCause = "";
			whereCause += " AND USER_ID = " + user.getId();

			whereCause += " and ((target_from <= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ";
			whereCause += " and target_from >= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "') ";

			whereCause += " or (target_to <= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ";
			whereCause += " and target_to >= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "') ";

			whereCause += " or (target_from >= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ";
			whereCause += " and target_to <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "') ";

			whereCause += " or (target_from <= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ";
			whereCause += " and target_to >= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "') ";

			whereCause += ")";

			// whereCause += " AND TARGET_FROM >= '" + DateToolsUtil.convertToTimeStamp(startDate) + "' ";
			// whereCause += " AND TARGET_TO <= '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "'";
			SalesTargetNew[] salesTargets = new MSalesTargetNew().search(whereCause);
			if (salesTargets != null) {
				for (SalesTargetNew st : salesTargets) {
					st.calculateTargetAmount();
					allTargetAmount += st.getTargetAmount();
				}
				p.setAllTargetAmount(allTargetAmount);
			} else {
				p.setAllTargetAmount(new Double("0"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}

		return p;
	}

	public int getCountVisit(PerformanceReport t, User user, Connection conn) throws Exception {
		int countVisit = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT COUNT(*) AS COUNT_VISIT ");
			sql.append("\n  FROM t_visit ");
			sql.append("\n  LEFT JOIN ad_user ON t_visit.USER_ID = ad_user.USER_ID ");
			sql.append("\n  WHERE ad_user.USER_ID = " + user.getId());
			sql.append("\n  AND t_visit.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n  AND t_visit.ISACTIVE = 'Y' ");
			sql.append("\n  AND t_visit.VISIT_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				countVisit = rst.getInt("COUNT_VISIT");
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

	public int getCountCustomer(PerformanceReport t, User user, Connection conn) throws Exception {
		int countVisit = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT COUNT(distinct customer_id) AS COUNT_CUSTOMER ");
			sql.append("\n  FROM t_order ");
			sql.append("\n  LEFT JOIN ad_user ON t_order.USER_ID = ad_user.USER_ID ");
			sql.append("\n  WHERE ad_user.USER_ID = " + user.getId());
			//sql.append("\n  AND t_order.DOC_STATUS = 'SV' ");
			sql.append("\n  AND t_order.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n  AND t_order.ORDER_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
            
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
	public int[] getCountOrderItem(PerformanceReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        int r[] = new int[2];
		try {
			sql.delete(0, sql.length());
			 sql.append("\n select SUM(A.COUNT_ALL) as COUNT_ALL ,SUM(A.COUNT_CANCEL) AS COUNT_CANCEL from(");
			sql.append("\n  SELECT COUNT(*)  AS COUNT_ALL  ,0 as COUNT_CANCEL ");
			sql.append("\n  FROM t_order ");
			sql.append("\n  LEFT JOIN ad_user ON t_order.USER_ID = ad_user.USER_ID ");
			sql.append("\n  WHERE ad_user.USER_ID = " + user.getId());
			sql.append("\n  AND t_order.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n  AND t_order.ORDER_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
            sql.append("\n  union all ");
			sql.append("\n  SELECT 0 AS COUNT_ALL , COUNT(*) AS  COUNT_CANCEL  ");
			sql.append("\n  FROM t_order ");
			sql.append("\n  LEFT JOIN ad_user ON t_order.USER_ID = ad_user.USER_ID ");
			sql.append("\n  WHERE ad_user.USER_ID = " + user.getId());
			sql.append("\n  AND t_order.DOC_STATUS = 'VO' ");
			sql.append("\n  AND t_order.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n  AND t_order.ORDER_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
			 
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
