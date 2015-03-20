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
			sql.append(" \n  SELECT T.SHIPPING_DATE, T.CODE, T.FullName, T.MOBILE, T.LINE_NO, ");
			sql.append(" \n  T.TOTAL_LINE, SUM(T.ORANGE_QTY) AS ORANGE_QTY, SUM(T.BERRY_QTY) AS BERRY_QTY, ");
			sql.append(" \n  SUM(T.MIX_QTY) AS MIX_QTY, T.REMARK ");
			sql.append(" \n  FROM ( ");
			sql.append(" \n  SELECT odline.SHIPPING_DATE, cus.CODE, cus.NAME AS FullName ");
			sql.append(", concat(  \n")
		       .append(" case when cmc.phone is not null and trim(cmc.phone) <>'' and trim(cmc.phone) <>'-' then cmc.phone else '' end, \n")
			   .append(" case when cmc.phone_sub1 is not null and trim(cmc.phone_sub1) <>'' and trim(cmc.phone_sub1) <>'-' then concat('ต่อ',cmc.phone_sub1) else '' end, \n")
			   .append(" case when cmc.phone2 is not null and trim(cmc.phone2) <>'' and trim(cmc.phone2) <>'-' then concat(', ', cmc.phone2) else '' end, \n")
			   .append(" case when cmc.phone_sub2 is not null and trim(cmc.phone_sub2) <>''  and trim(cmc.phone_sub2) <>'-' then   concat('ต่อ',cmc.phone_sub2) else '' end, \n")
			   .append(" case when cmc.mobile is not null and trim(cmc.mobile) <>'' and trim(cmc.mobile) <>'-' then concat(', ', cmc.mobile) else '' end, \n")
			   .append(" case when cmc.mobile2 is not null and trim(cmc.mobile2) <>'' and trim(cmc.mobile2) <>'-' then concat(', ', cmc.mobile2) else '' end  \n")
			   .append(")as MOBILE  \n");
			sql.append(" \n  , odline.trip_no as LINE_NO, ");
			sql.append(" \n  (SELECT MAX(odline.trip_no) ");
			sql.append(" \n  FROM t_order od ");
			sql.append(" \n  INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" \n  LEFT JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" \n  LEFT JOIN m_product pro ON odline.PRODUCT_ID = pro.PRODUCT_ID ");
			sql.append(" \n  WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append(" \n  AND cus.ISACTIVE = 'Y' ");
			sql.append(" \n  AND odline.ISCANCEL = 'N' ");
			sql.append(" \n  AND od.DOC_STATUS = 'SV' ");
			sql.append(" \n  AND od.ORDER_TYPE = 'DD' ");
			sql.append(" \n  AND odline.CALL_BEFORE_SEND = 'Y' ");
			sql.append(" \n  AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShippingDate()) + "' ");
			sql.append(" \n  ORDER BY odline.LINE_NO DESC ) AS TOTAL_LINE, ");
			sql.append(" \n  CASE WHEN pro.NAME LIKE 'BCSO%' THEN odline.QTY ELSE 0 END AS ORANGE_QTY, ");
			sql.append(" \n  CASE WHEN pro.NAME LIKE 'BCSS%' THEN odline.QTY ELSE 0 END AS BERRY_QTY, ");
			sql.append(" \n  CASE WHEN pro.NAME LIKE 'BCSB%' THEN odline.QTY ELSE 0 END AS MIX_QTY, ");
			sql.append(" \n  CASE WHEN cmt.Trip_comment IS NULL THEN  od.remark  ");
			sql.append(" \n       ELSE CONCAT(od.remark ,CONCAT(' ',cmt.TRIP_COMMENT))   ");
			sql.append(" \n  END  AS REMARK ");
			sql.append(" \n  FROM t_order od ");
			sql.append(" \n  INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" \n  LEFT JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" \n  LEFT JOIN m_contact cmc ON cus.CUSTOMER_ID = cmc.CUSTOMER_ID ");
			sql.append(" \n  LEFT JOIN m_product pro ON odline.PRODUCT_ID = pro.PRODUCT_ID ");
			sql.append(" \n  LEFT OUTER JOIN t_member_trip_comment cmt ON od.ORDER_ID = cmt.ORDER_ID AND odline.TRIP_NO = cmt.TRIP_NO ");
			sql.append(" \n  WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append(" \n  AND cus.ISACTIVE = 'Y' ");
			sql.append(" \n  AND cmc.ISACTIVE = 'Y'  ");
			sql.append(" \n  AND odline.ISCANCEL = 'N' ");
			sql.append(" \n  AND od.DOC_STATUS = 'SV' ");
			sql.append(" \n  AND od.ORDER_TYPE = 'DD' ");
			sql.append(" \n  AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShippingDate()) + "' ");
			sql.append(" \n  AND odline.CALL_BEFORE_SEND = 'Y' ");
			sql.append(" \n  ORDER BY cus.DELIVERY_GROUP, cast(cus.code as unsigned), odline.trip_no ");
			sql.append(" \n  ) T ");
			sql.append(" \n  GROUP BY T.SHIPPING_DATE, T.CODE, T.FullName ");
			sql.append(" \n  ORDER BY T.SHIPPING_DATE, T.LINE_NO ");

			logger.debug("sql \n"+sql.toString());
			
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
				if(rst != null){
				  rst.close(); rst =null;
				}
				if(stmt != null){
				  stmt.close();stmt = null;
				}
			} catch (Exception e) {}
		}

		return pos;
	}

}
