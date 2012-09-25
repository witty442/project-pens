package com.isecinc.pens.report.detailedsales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class DetailedSalesReportProcess extends I_ReportProcess<DetailedSalesReport>{

	/**
	 * Search for report.
	 */
	public List<DetailedSalesReport> doReport(DetailedSalesReport t, User user,
			Connection conn) throws Exception {
		
		List<DetailedSalesReport> lstData = new ArrayList<DetailedSalesReport>();
		DetailedSalesReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			sql.delete(0, sql.length());
			
			// Aneak.t 21/01/2011
			sql.append("SELECT t.* FROM ( ");
			sql.append(" SELECT o.ORDER_DATE, o.ORDER_NO, c.NAME, c.NAME2, o.IsCash , ");
			sql.append(" SUM(l.TOTAL_AMOUNT) AS TOTAL_AMOUNT, ");
			sql.append(" o.PAYMENT, o.INTERFACES, o.EXPORTED, o.DOC_STATUS, ");
			sql.append(" IF(o.ISCASH ='Y',null,(SELECT distinct rh.ISPDPAID FROM T_RECEIPT rh , T_RECEIPT_LINE rl WHERE rh.RECEIPT_ID = rl.RECEIPT_ID AND rl.ORDER_ID = o.ORDER_ID )) as ISPDPAID ");
			sql.append(" FROM t_order o ");
			sql.append(" INNER JOIN t_order_line l ON l.ORDER_ID = o.ORDER_ID ");
			sql.append(" LEFT JOIN m_customer c ON o.CUSTOMER_ID = c.CUSTOMER_ID ");
			sql.append(" WHERE c.CUSTOMER_TYPE = 'CV' ");
			sql.append(" AND l.ISCANCEL <> 'Y' ");
			
			sql.append(" AND o.USER_ID = " + user.getId());	
			sql.append(" AND o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append(" AND o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			
			if(!StringUtils.isEmpty(t.getOrderType()))
				sql.append(" AND o.IsCash = '"+t.getOrderType()+"' ");
			
			//Aneak.t 21/01/2011
			sql.append(" GROUP BY o.ORDER_NO ");
			
			switch (t.getSortType()) {
			case 1:
				sql.append(" ORDER BY o.IsCASH, o.ORDER_DATE, o.ORDER_NO, c.CODE ");
				break;
			case 2:
				sql.append(" ORDER BY o.IsCASH,o.ORDER_NO, o.ORDER_DATE ");
				break;
			case 3:
				sql.append(" ORDER BY o.IsCASH,c.CODE, o.ORDER_DATE, o.ORDER_NO ");
				break;
			default:
				sql.append(" ORDER BY o.IsCASH ");
				break;
			}
			
			sql.append(") t ");
			if(!StringUtils.isEmpty(t.getPdPaid()))
				sql.append("WHERE t.ISPDPAID = '"+t.getPdPaid()+"' ");
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			
			while(rs.next()){
				detailedSales = new DetailedSalesReport();
				detailedSales.setOrderDate(DateToolsUtil.convertToString(rs.getTimestamp("ORDER_DATE")));
				detailedSales.setOrderNo(rs.getString("ORDER_NO"));
				detailedSales.setName(rs.getString("NAME"));
				detailedSales.setName2(rs.getString("NAME2"));
				detailedSales.setTotalAmount(rs.getDouble("TOTAL_AMOUNT"));
				detailedSales.setPayment(rs.getString("PAYMENT"));
				detailedSales.setInterfaces(rs.getString("INTERFACES"));
				detailedSales.setExported(rs.getString("EXPORTED"));
				detailedSales.setDocStatus(rs.getString("DOC_STATUS"));
				detailedSales.setIsCash(rs.getString("IsCash"));
				detailedSales.setIsPDPaid(rs.getString("IsPDPaid"));
				
				lstData.add(detailedSales);
			}
			
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
}
