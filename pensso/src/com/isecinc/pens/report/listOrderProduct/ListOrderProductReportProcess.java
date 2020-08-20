package com.isecinc.pens.report.listOrderProduct;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.pens.util.DateToolsUtil;

/**
 * Receive Bag Report Process
 * 
 * @author Aneak.t
 * @version $Id: ReceiveBagReportProcess.java,v 1.0 03/12/2010 15:52:00 aneak.t Exp $
 * 
 */
public class ListOrderProductReportProcess {

	/**
	 * Search for report.
	 */
	public List<ListOrderProductReport> doReport( User user, Connection conn) throws Exception {
		List<ListOrderProductReport> pos = new ArrayList<ListOrderProductReport>();
		ListOrderProductReport b = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			/*sql.append(" SELECT T.SHIPPING_DATE, T.DELIVERY_GROUP, ");
			sql.append(" SUM(ORANGE_QTY) AS ORANGE_QTY, SUM(BERRY_QTY) AS BERRY_QTY, ");
			sql.append(" SUM(MIX_QTY) AS MIX_QTY, SUM(BAG_QTY) AS BAG_QTY ");
			sql.append(" FROM (SELECT line.SHIPPING_DATE, cus.CODE, cus.DELIVERY_GROUP, ");
			sql.append(" SUM(CASE WHEN pd.NAME LIKE 'BCSO%' THEN line.QTY ELSE 0 END) AS ORANGE_QTY, ");
			sql.append(" SUM(CASE WHEN pd.NAME LIKE 'BCSS%' THEN line.QTY ELSE 0 END) AS BERRY_QTY, ");
			sql.append(" SUM(CASE WHEN pd.NAME LIKE 'BCSB%' THEN line.QTY ELSE 0 END) AS MIX_QTY, ");
			sql.append(" (SUM(line.QTY)/15) AS BAG_QTY ");
			sql.append(" FROM t_order_line line ");
			sql.append(" INNER JOIN t_order od ON line.ORDER_ID = od.ORDER_ID ");
			sql.append(" INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pd ON line.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = 'DD' ");
			sql.append("   AND od.DOC_STATUS = 'SV' ");
			sql.append("   AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("   AND line.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShipmentDate()) + "' ");
			sql.append("  AND line.ISCANCEL = 'N' ");
			sql.append(" GROUP BY cus.CODE, cus.DELIVERY_GROUP ");
			sql.append(" ORDER BY cus.DELIVERY_GROUP) T ");
			sql.append(" GROUP BY T.DELIVERY_GROUP ");
			sql.append(" ORDER BY T.DELIVERY_GROUP ");*/

			//stmt = conn.createStatement();
			//rst = stmt.executeQuery(sql.toString());
			int i = 1;
			if (true) {
				b = new ListOrderProductReport();

				b.setProduct("Product 111111");
				b.setQty("1/1");
				b.setUnit("CTN/PAC");
				b.setPrice("1500/150");
				
				pos.add(b);
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
