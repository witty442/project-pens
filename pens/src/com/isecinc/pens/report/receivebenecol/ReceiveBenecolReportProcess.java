package com.isecinc.pens.report.receivebenecol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Receive Benecol Report Process
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBenecolReportProcess.java,v 1.0 02/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ReceiveBenecolReportProcess extends I_ReportProcess<ReceiveBenecolReport> {

	/**
	 * Search for report.
	 */
	public List<ReceiveBenecolReport> doReport(ReceiveBenecolReport t, User user, Connection conn) throws Exception {
		List<ReceiveBenecolReport> pos = new ArrayList<ReceiveBenecolReport>();
		ReceiveBenecolReport receive = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append(" SELECT od.ORDER_DATE, cus.CODE AS CUSTOMER_CODE, ");
			sql.append(" cus.NAME AS CUSTOMER_NAME, ");
			sql.append(" CONCAT(ad.LINE1,' ',ad.LINE2,' ',ad.LINE3,' ',ad.LINE4,' ', ");
			sql.append(" ad.PROVINCE_NAME, ' ', ad.POSTAL_CODE) AS ADDRESS, ");
			sql.append(" CONCAT(ct.MOBILE,' ',ct.MOBILE2) AS MOBILE, ");
			sql.append(" CONCAT(ct.PHONE, ' ',ct.PHONE2) AS PHONE, ");
			sql.append(" CASE WHEN pd.NAME LIKE 'BCSO%' THEN mpr.ORDER_QTY ELSE 0 END AS ORANGE_QTY, ");
			sql.append(" CASE WHEN pd.NAME LIKE 'BCSS%' THEN mpr.ORDER_QTY ELSE 0 END AS BERRY_QTY, ");
			sql.append(" CASE WHEN pd.NAME LIKE 'BCSB%' THEN mpr.ORDER_QTY ELSE 0 END AS MIX_QTY, ");
			sql.append(" line.LINE_AMOUNT, line.SHIPPING_DATE, line.REQUEST_DATE, ");
			sql.append(" CASE line.PAYMENT WHEN 'Y' THEN line.SHIPPING_DATE ELSE NULL END AS PAYMENT_DATE, ");
			sql.append(" 0 AS REMAIN_AMOUNT, cus.RECOMMENDED_BY ");
			sql.append(" FROM t_order od ");
			sql.append(" INNER JOIN t_order_line line ON line.ORDER_ID = od.ORDER_ID ");
			sql.append(" LEFT JOIN m_member_product mpr ON mpr.CUSTOMER_ID = od.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pd ON mpr.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append(" INNER JOIN ad_user us ON od.USER_ID = us.USER_ID ");
			sql.append(" INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_address ad ON ad.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_contact ct ON ct.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append(" AND od.DOC_STATUS = 'SV' ");
			sql.append(" AND cus.ISACTIVE = 'Y' ");
			sql.append(" AND ad.PURPOSE = 'S' ");
			sql.append("  AND line.ISCANCEL = 'N' ");
			// sql.append(" AND us.USER_ID = " + user.getId());
			sql.append(" AND od.ORDER_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getOrderDate()) + "' ");
			sql.append(" AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append(" ORDER BY cus.CUSTOMER_ID ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			String cCode = "";
			int sumOrangeQty = 0;
			int sumBerryQty = 0;
			int sumMixQty = 0;
			while (rst.next()) {
				receive = new ReceiveBenecolReport();
				receive.setId(i++);
				if (!cCode.equals(rst.getString("CUSTOMER_CODE"))) {
					receive.setOrderDate(DateToolsUtil.convertToString(rst.getDate("ORDER_DATE")));
					receive.setCustomerCode(rst.getString("CUSTOMER_CODE"));
					receive.setCustomerName(rst.getString("CUSTOMER_NAME"));
					receive.setAddress(rst.getString("ADDRESS"));
					receive.setMobile(rst.getString("MOBILE"));
					receive.setPhone(rst.getString("PHONE"));
					receive.setRecommentedBy(rst.getString("RECOMMENDED_BY"));
					cCode = receive.getCustomerCode();
				}
				sumOrangeQty += rst.getInt("ORANGE_QTY");
				sumBerryQty += rst.getInt("BERRY_QTY");
				sumMixQty += rst.getInt("MIX_QTY");
				receive.setLineAmount(rst.getInt("LINE_AMOUNT"));
				receive.setShippingDate(DateToolsUtil.convertToString(rst.getDate("SHIPPING_DATE")));
				receive.setRequestDate(DateToolsUtil.convertToString(rst.getDate("REQUEST_DATE")));
				receive.setPaymentDate(DateToolsUtil.convertToString(rst.getDate("PAYMENT_DATE")));
				receive.setRamainAmount(rst.getDouble("REMAIN_AMOUNT"));

				pos.add(receive);
			}

			// Set sum quantity by taste.
			for (i = 0; i < pos.size(); i++) {
				if (pos.get(i).getCustomerCode() != null) {
					pos.get(i).setOrangeQty(sumOrangeQty);
					pos.get(i).setBerryQty(sumBerryQty);
					pos.get(i).setMixQty(sumMixQty);
				}
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

}
