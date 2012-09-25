package com.isecinc.pens.report.summary;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Benecol Summary Report Process
 * 
 * @author atiz.b
 * 
 */
public class BenecolSummaryReportProcess extends I_ReportProcess<BenecolSummaryReport> {

	/**
	 * 
	 */
	public List<BenecolSummaryReport> doReport(BenecolSummaryReport t, User user, Connection conn) throws Exception {
		List<BenecolSummaryReport> pos = new ArrayList<BenecolSummaryReport>();
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = "";
			if (t.getReportType().equalsIgnoreCase("S")) {
				// Summary Report
				sql = "";
				sql += "select t.ORDER_DATE, sum(t.TOTAL_AMOUNT) total_amount, ";
				sql += "  sum(t.VAT_AMOUNT) vat_amount,sum(t.NET_AMOUNT) net_amount ";
				sql += "from t_order t ";
				sql += "where t.DOC_STATUS = 'SV' ";
				sql += "  and t.ORDER_TYPE = 'DD' ";
				sql += "  and t.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getDateFrom().trim()) + "'";
				sql += "  and t.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getDateTo().trim()) + "'";
				sql += "group by t.ORDER_DATE ";
				sql += "order by t.ORDER_DATE ";
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
				BenecolSummaryReport r;
				while (rst.next()) {
					r = new BenecolSummaryReport();
					r.setOrderDate(DateToolsUtil.convertToString(rst.getTimestamp("ORDER_DATE")));
					r.setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
					r.setVatAmount(rst.getDouble("VAT_AMOUNT"));
					r.setNetAmount(rst.getDouble("NET_AMOUNT"));
					pos.add(r);
				}
			} else {
				// Detail Report
				sql = "";
				sql += "select t.ORDER_DATE,t.ORDER_NO,t.CUSTOMER_NAME, ";
				sql += "  t.TOTAL_AMOUNT,t.VAT_AMOUNT,t.NET_AMOUNT ";
				sql += "from t_order t ";
				sql += "where t.DOC_STATUS = 'SV' ";
				sql += "  and t.ORDER_TYPE = 'DD' ";
				sql += "  and t.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(t.getDateFrom().trim()) + "'";
				sql += "  and t.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(t.getDateTo().trim()) + "'";
				sql += "order by t.ORDER_DATE, t.ORDER_NO ";
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
				BenecolSummaryReport r;
				while (rst.next()) {
					r = new BenecolSummaryReport();
					r.setOrderNo(rst.getString("ORDER_NO"));
					r.setOrderDate(DateToolsUtil.convertToString(rst.getTimestamp("ORDER_DATE")));
					r.setCustomerName(rst.getString("CUSTOMER_NAME"));
					r.setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
					r.setVatAmount(rst.getDouble("VAT_AMOUNT"));
					r.setNetAmount(rst.getDouble("NET_AMOUNT"));
					pos.add(r);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return pos;
	}
}
