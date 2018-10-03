package com.isecinc.pens.web.report.creditcontrol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: CreditControlReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditControlPDPAID_NO_ReportProcess extends I_ReportProcess<CreditControlReport>{

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
            //No Save ad pd receipt
			sql.append("\n  select A.* FROM(");
			sql.append("\n  select ");
			sql.append("\n   o.order_no  ");
			sql.append("\n  ,o.order_date  ");
			sql.append("\n  ,cus.CODE AS CUSTOMER_CODE ");
			sql.append("\n  ,cus.NAME AS CUSTOMER_NAME ");
			sql.append("\n  ,'SV' as doc_status   ");
			sql.append("\n  ,null as pdpaid_date  ");
			sql.append("\n  , '' as PD_PAYMENTMETHOD ");
			sql.append("\n  ,SUM(o.NET_AMOUNT)  AS RECEIPT_AMOUNT ");
			sql.append("\n  FROM t_order o , m_customer cus  ");
			sql.append("\n  WHERE 1=1 ");
			sql.append("\n  AND o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n  AND o.ISCASH ='N'  ");
			sql.append("\n  AND o.DOC_STATUS ='SV'  ");
			sql.append("\n  AND o.USER_ID ='"+user.getId()+"'  ");
			sql.append("\n  AND o.order_id not in( select order_id from t_receipt_line)   ");
			sql.append("\n  AND o.order_no not in( select receipt_no from t_receipt)   ");
			sql.append("\n  AND o.order_no not in( select order_no from t_receipt_pdpaid_no)   ");
			sql.append("\n  AND DATE(o.ORDER_DATE) >= '" + Utils.stringValue(Utils.parseToBudishDate(t.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE) +"'");
			sql.append("\n  AND DATE(o.ORDER_DATE) <= '" + Utils.stringValue(Utils.parseToBudishDate(t.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE) +"'");
			sql.append("\n  group by o.order_no,o.order_date ,cus.CODE ,cus.NAME ");
			
			sql.append("\n  UNION ALL ");
			
			// Save ad pd receipt
			sql.append("\n  SELECT  ");
			sql.append("\n   o.order_no  ");
			sql.append("\n  ,o.order_date  ");
			sql.append("\n  ,cus.CODE AS CUSTOMER_CODE ");
			sql.append("\n  ,cus.NAME AS CUSTOMER_NAME ");
			sql.append("\n  ,'SV' as doc_status   ");
			sql.append("\n  ,o.pdpaid_date  ");
			sql.append("\n  ,o.PD_PAYMENTMETHOD ");
			sql.append("\n  ,SUM(o.RECEIPT_AMOUNT)  AS RECEIPT_AMOUNT ");
			sql.append("\n   from t_receipt_pdpaid_no o, m_customer cus  ");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n   and o.CREATED_BY = " + user.getId());
			sql.append("\n   and DATE(o.ORDER_DATE) >= '" + Utils.stringValue(Utils.parseToBudishDate(t.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE) +"'");
			sql.append("\n   and DATE(o.ORDER_DATE) <= '" + Utils.stringValue(Utils.parseToBudishDate(t.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE) +"'");
			sql.append("\n   group by o.order_no,o.order_date ,cus.CODE ,cus.NAME ,o.pdpaid_date ,o.PD_PAYMENTMETHOD ");
			sql.append("\n )A ORDER BY A.order_no  ");
			
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
				if("CS".equals(Utils.isNull(rs.getString("PD_PAYMENTMETHOD")))){
				   detailedSales.setPaymentMethod("à§Ô¹Ê´");
				}else if("CH".equals(Utils.isNull(rs.getString("PD_PAYMENTMETHOD")))){
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
			sql.append("\n  SELECT SUM(o.RECEIPT_AMOUNT)  AS RECEIPT_AMOUNT ");
			sql.append("\n   from t_receipt_pdpaid_no o, m_customer cus  ");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n   and o.CREATED_BY = " + user.getId());
		
			sql.append("\n   and DATE(o.PDPAID_DATE) >= '" + Utils.stringValue(Utils.parseToBudishDate(t.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE) +"'" );
			sql.append("\n   and DATE(o.PDPAID_DATE) <= '" + Utils.stringValue(Utils.parseToBudishDate(t.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE)  +"'");
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
			sql.append("\n  SELECT COUNT(distinct A.customer_id) AS COUNT_CUSTOMER ");
			sql.append("\n  FROM (");
			sql.append("\n   SELECT o.customer_id FROM  t_receipt_pdpaid_no o ");;
			sql.append("\n   WHERE o.created_by = " + user.getId());
			sql.append("\n   and DATE(o.ORDER_DATE) >= '" + Utils.stringValue(Utils.parseToBudishDate(t.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE)  +"'");
			sql.append("\n   and DATE(o.ORDER_DATE) <= '" + Utils.stringValue(Utils.parseToBudishDate(t.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE)  +"'");
			sql.append("\n   UNION");
			sql.append("\n   SELECT o.customer_id FROM t_order o ");
			sql.append("\n   WHERE 1=1 ");
			sql.append("\n   AND ISCASH ='N' ");
			sql.append("\n   AND DOC_STATUS ='SV' ");
			sql.append("\n   AND USER_ID ='"+user.getId()+"' ");
			sql.append("\n   AND order_id not in( select order_id from t_receipt_line)  ");
			sql.append("\n   AND order_no not in( select receipt_no from t_receipt) ");
			sql.append("\n   AND order_no not in( select order_no from t_receipt_pdpaid_no) ");
            sql.append("\n   and DATE(o.ORDER_DATE) >= '" + Utils.stringValue(Utils.parseToBudishDate(t.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE)  +"'");
			sql.append("\n   and DATE(o.ORDER_DATE) <= '" + Utils.stringValue(Utils.parseToBudishDate(t.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH) ,Utils.YYYY_MM_DD_WITH_LINE)  +"'");
			sql.append("\n  )A ");
			
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
