package com.isecinc.pens.web.report.creditcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;
import com.pens.util.DateToolsUtil;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: CreditControlReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditControlReportProcess extends I_ReportProcess<CreditControlReport>{

	/**
	 * Search for report.
	 */
	public List<CreditControlReport> doReport(CreditControlReport t, User user,
			Connection conn) throws Exception {
		
		List<CreditControlReport> lstData = new ArrayList<CreditControlReport>();
		CreditControlReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		int no = 0;
		try {
			
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return lstData;
	}
	
	public CreditControlReport getData(CreditControlReport t, User user,Connection conn) throws Exception {
		
		List<CreditControlReport> lstData = new ArrayList<CreditControlReport>();
		CreditControlReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		int no = 0;
		double totalOrderAmt = 0;
		try {
			sql.delete(0, sql.length());

			sql.append("\n  SELECT  ");
			sql.append("\n  o.order_no  ");
			sql.append("\n  ,o.order_date  ");
			sql.append("\n  ,cus.CODE AS CUSTOMER_CODE ");
			sql.append("\n  ,cus.NAME AS CUSTOMER_NAME ");
			sql.append("\n  ,o.doc_status   ");
			sql.append("\n  ,r.pdpaid_date  ");
			sql.append("\n  ,r.PAYMENT_METHOD2 ");
			sql.append("\n  ,SUM(o.NET_AMOUNT)  AS RECEIPT_AMOUNT ");
			sql.append("\n   from t_order o, m_customer cus  ");
			sql.append("\n   ,( ");
			sql.append("\n     select l.order_id  ");
			sql.append("\n     ,h.payment_method  ");
			sql.append("\n     ,h.PDPAID_DATE ");
			sql.append("\n     ,h.PD_PAYMENTMETHOD  as PAYMENT_METHOD2 ");
			sql.append("\n     from t_receipt h, t_receipt_line l ,t_receipt_match m, t_receipt_by b ");
			sql.append("\n     where h.receipt_id = l.receipt_id  ");
			sql.append("\n     and m.RECEIPT_LINE_ID = l.RECEIPT_LINE_ID  ");
			sql.append("\n     and b.RECEIPT_BY_ID = m.RECEIPT_BY_ID  ");
			sql.append("\n   ) r ");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n   and o.order_id = r.order_id ");
			sql.append("\n   and o.DOC_STATUS ='SV' ");
			sql.append("\n   and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n   and o.USER_ID = " + user.getId());
			sql.append("\n   and o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n   and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			sql.append("\n   and o.IsCash = 'N' ");//Credit Only
			sql.append("\n   group by o.order_no,o.order_date ,cus.CODE ,cus.NAME ,o.doc_status ,r.pdpaid_date ,r.PAYMENT_METHOD2 ");
  
			//	",IF(od.ORDER_DATE <>rch.receipt_date ,'CREDIT','CASH' ),'CREDIT') as payment_term" +
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while(rs.next()){
				detailedSales = new CreditControlReport();
				no++;
				detailedSales.setId(no);
				detailedSales.setOrderNo(rs.getString("ORDER_NO"));
				detailedSales.setStatus(Utils.isNull(rs.getString("doc_status")));
				detailedSales.setOrderDate(DateToolsUtil.convertToString(rs.getTimestamp("ORDER_DATE")));
				detailedSales.setCustomerCode(Utils.isNull(rs.getString("CUSTOMER_CODE")));
				detailedSales.setCustomerName(Utils.isNull(rs.getString("CUSTOMER_NAME")));
				detailedSales.setOrderAmount(rs.getDouble("RECEIPT_AMOUNT"));
				
				if(rs.getTimestamp("pdpaid_date") != null){
				   detailedSales.setPdDate(DateToolsUtil.convertToString(rs.getTimestamp("pdpaid_date")));
				}else{
				   detailedSales.setPdDate("");
				}
				if("CS".equals(Utils.isNull(rs.getString("PAYMENT_METHOD2")))){
				   detailedSales.setPaymentMethod("à§Ô¹Ê´");
				}else if("CH".equals(Utils.isNull(rs.getString("PAYMENT_METHOD2")))){
				   detailedSales.setPaymentMethod("àªç¤");
				}
				
				totalOrderAmt += detailedSales.getOrderAmount();
				
				lstData.add(detailedSales);
			}
			
			t.setTotalOrderAmt(totalOrderAmt);
			t.setItems(lstData);
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return t;
	}


	public double sumPrevTotalOrderAmt(CreditControlReport t, User user,Connection conn) throws Exception 
	{
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		double creditSalesAmt = 0d;
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT SUM(o.NET_AMOUNT)  AS RECEIPT_AMOUNT ");
			sql.append("\n   from t_order o, m_customer cus  ");
			sql.append("\n   ,( ");
			sql.append("\n     select l.order_id  ");
			sql.append("\n     ,h.payment_method  ");
			sql.append("\n     ,h.PDPAID_DATE ");
			sql.append("\n     ,b.PAYMENT_METHOD as PAYMENT_METHOD2 ");
			sql.append("\n     from t_receipt h, t_receipt_line l ,t_receipt_match m, t_receipt_by b ");
			sql.append("\n     where h.receipt_id = l.receipt_id  ");
			sql.append("\n     and m.RECEIPT_LINE_ID = l.RECEIPT_LINE_ID  ");
			sql.append("\n     and b.RECEIPT_BY_ID = m.RECEIPT_BY_ID  ");
			sql.append("\n   ) r ");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and o.order_id = r.order_id ");
			sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n   and o.DOC_STATUS ='SV' ");
			sql.append("\n   and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n   and o.USER_ID = " + user.getId());
			sql.append("\n   and r.PDPAID_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n   and r.PDPAID_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			sql.append("\n   and o.IsCash = 'N' ");//Credit Only
			
			logger.debug("sql:"+sql.toString());
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				creditSalesAmt = rst.getDouble("RECEIPT_AMOUNT");
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
	
	public int getCountCustomer(CreditControlReport t, User user, Connection conn) throws Exception {
		int countVisit = 0;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT COUNT(distinct o.customer_id) AS COUNT_CUSTOMER ");
			sql.append("\n  FROM  t_order o, m_customer cus ");;
			sql.append("\n  WHERE cus.user_id = " + user.getId());
			sql.append("\n  AND o.DOC_STATUS ='SV' ");
			sql.append("\n  AND o.IsCash = 'N' ");
			sql.append("\n  AND o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  AND o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n  AND o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
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
}
