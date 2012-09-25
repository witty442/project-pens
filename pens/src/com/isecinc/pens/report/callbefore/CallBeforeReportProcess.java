package com.isecinc.pens.report.callbefore;

/**
 * Call Before Send Report Process
 * 
 * @author Aneak.t
 * @version $Id: CallBeforeReportProcess.java,v 1.0 15/03/2011 15:52:00 aneak.t Exp $
 * 
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

public class CallBeforeReportProcess extends I_ReportProcess<CallBeforeReport> {

	@Override
	public List<CallBeforeReport> doReport(CallBeforeReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<CallBeforeReport> pos = new ArrayList<CallBeforeReport>();
		CallBeforeReport callBefore = null;
		StringBuilder sql = new StringBuilder();

		try {

			sql.delete(0, sql.length());
			sql.append(" SELECT T.SHIPPING_DATE, T.CODE, T.FullName, T.MOBILE, T.LINE_NO, ");
			sql.append(" T.TOTAL_LINE, SUM(T.ORANGE_QTY) AS ORANGE_QTY, SUM(T.BERRY_QTY) AS BERRY_QTY, ");
			sql.append(" SUM(T.MIX_QTY) AS MIX_QTY, T.REMARK ");
			sql.append(" FROM ( ");
			sql.append(" SELECT odline.SHIPPING_DATE, cus.CODE, cus.NAME AS FullName, ");
			sql.append(" cont.MOBILE, odline.trip_no as LINE_NO, ");
			sql.append(" (SELECT MAX(odline.trip_no) ");
			sql.append(" FROM t_order od ");
			sql.append(" INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" LEFT JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pro ON odline.PRODUCT_ID = pro.PRODUCT_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append(" AND cus.ISACTIVE = 'Y' ");
			sql.append(" AND odline.ISCANCEL = 'N' ");
			sql.append(" AND od.DOC_STATUS = 'SV' ");
			sql.append(" AND od.ORDER_TYPE = 'DD' ");
			sql.append(" AND odline.CALL_BEFORE_SEND = 'Y' ");
			sql.append(" AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShippingDate()) + "' ");
			sql.append(" ORDER BY odline.LINE_NO DESC ) AS TOTAL_LINE, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSO%' THEN odline.QTY ELSE 0 END AS ORANGE_QTY, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSS%' THEN odline.QTY ELSE 0 END AS BERRY_QTY, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSB%' THEN odline.QTY ELSE 0 END AS MIX_QTY, ");
			sql.append(" CONCAT(od.remark ,CONCAT(' ',cmt.TRIP_COMMENT)) AS REMARK FROM t_order od ");
			sql.append(" INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" LEFT JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_contact cont ON cus.CUSTOMER_ID = cont.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pro ON odline.PRODUCT_ID = pro.PRODUCT_ID ");
			sql
					.append(" LEFT JOIN t_member_trip_comment cmt ON od.ORDER_ID = cmt.ORDER_ID AND odline.TRIP_NO = cmt.TRIP_NO ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append(" AND cus.ISACTIVE = 'Y' ");
			sql.append(" AND odline.ISCANCEL = 'N' ");
			sql.append(" AND od.DOC_STATUS = 'SV' ");
			sql.append(" AND od.ORDER_TYPE = 'DD' ");
			sql.append(" AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShippingDate()) + "' ");
			sql.append(" AND odline.CALL_BEFORE_SEND = 'Y' ");
			sql.append(" ORDER BY cus.DELIVERY_GROUP, cast(cus.code as unsigned), odline.trip_no ");
			sql.append(" ) T ");
			sql.append(" GROUP BY T.SHIPPING_DATE, T.CODE, T.FullName ");
			sql.append(" ORDER BY T.SHIPPING_DATE, T.LINE_NO ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 0;
			while (rst.next()) {
				callBefore = new CallBeforeReport();
				callBefore.setId(++i);
				callBefore.setShippingDate(DateToolsUtil.convertToString(rst.getDate("SHIPPING_DATE")));
				callBefore.setCode(rst.getString("CODE"));
				callBefore.setFullName(rst.getString("FullName"));
				callBefore.setMobile(rst.getString("MOBILE"));
				callBefore.setLineNo(rst.getString("LINE_NO"));
				callBefore.setTotalLine(rst.getString("TOTAL_LINE"));
				callBefore.setOrangeQty(rst.getDouble("ORANGE_QTY"));
				callBefore.setBerryQty(rst.getDouble("BERRY_QTY"));
				callBefore.setMixQty(rst.getDouble("MIX_QTY"));
				callBefore.setRemark(rst.getString("REMARK"));

				pos.add(callBefore);
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

}
