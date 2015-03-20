package com.isecinc.pens.report.detailedsales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
			sql.append(" SELECT o.ORDER_DATE, o.ORDER_NO, c.NAME, c.NAME2, ");
			sql.append(" SUM(l.TOTAL_AMOUNT) AS TOTAL_AMOUNT, ");
			sql.append(" o.PAYMENT, o.INTERFACES, o.EXPORTED, o.DOC_STATUS ");
			sql.append(" FROM t_order o ");
			sql.append(" INNER JOIN t_order_line l ON l.ORDER_ID = o.ORDER_ID ");
			sql.append(" LEFT JOIN m_customer c ON o.CUSTOMER_ID = c.CUSTOMER_ID ");
			sql.append(" WHERE c.CUSTOMER_TYPE = 'CV' ");
			sql.append(" AND l.ISCANCEL <> 'Y' ");
			
			sql.append(" AND o.USER_ID = " + user.getId());	
			sql.append(" AND o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append(" AND o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			
			//Aneak.t 21/01/2011
			sql.append(" GROUP BY o.ORDER_NO ");
			
			switch (t.getSortType()) {
			case 1:
				sql.append(" ORDER BY o.ORDER_DATE, o.ORDER_NO, c.CODE ");
				break;
			case 2:
				sql.append(" ORDER BY o.ORDER_NO, o.ORDER_DATE ");
				break;
			case 3:
				sql.append(" ORDER BY c.CODE, o.ORDER_DATE, o.ORDER_NO ");
				break;
			default:
				break;
			}
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
				
				lstData.add(detailedSales);
			}
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				if(rs != null){
				  rs.close(); rs =null;
				}
				if(stmt != null){
				  stmt.close();stmt = null;
				}
			} catch (Exception e2) {}
		}
		
		return lstData;
	}
}
