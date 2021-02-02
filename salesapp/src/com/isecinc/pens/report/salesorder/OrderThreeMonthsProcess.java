package com.isecinc.pens.report.salesorder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUser;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;

/**
 * Sales Order 3 Months Report Process
 * 
 * @author atiz.b
 * @version $Id: SalesOrderThreeMonthsProcess.java,v 1.0 17/11/2010 00:00:00 aneak.t Exp $
 * 
 */
public class OrderThreeMonthsProcess extends I_ReportProcess<OrderThreeMonths> {

	/**
	 * Do Report
	 */
	public List<OrderThreeMonths> doReport(OrderThreeMonths t, User user, Connection conn) throws Exception {
		try {
			int customerId = t.getCustomerId();
			if (t.getCustomerCode().trim().length() == 0) customerId = 0;
			return getOrderHeader(user.getCustomerType().getKey(), t.getDateFrom(), t.getDateTo(), user.getId(),
					customerId, conn);
		} catch (Exception e) {
			throw e;
		} finally {

		}
	}

	/**
	 * Get Header of Customer
	 * 
	 * @param customerType
	 * @param dateFrom
	 * @param dateTo
	 * @param userId
	 * @param customerId
	 * @param conn
	 * @return List of Order history
	 * @throws Exception
	 */
	private List<OrderThreeMonths> getOrderHeader(String customerType, String dateFrom, String dateTo, int userId,
			int customerId, Connection conn) throws Exception {
		List<OrderThreeMonths> pos = new ArrayList<OrderThreeMonths>();
		Statement stmt = null;
		ResultSet rst = null;
		try {
			User user = new MUser().find(String.valueOf(userId));
			/*
			 * String sql = "select o.CUSTOMER_ID,o.CUSTOMER_NAME, "; sql +=
			 * "  count(o.order_id) as tot,sum(o.net_amount) as netamt "; sql += "from t_order o, m_customer c "; sql +=
			 * "where o.DOC_STATUS = 'SV' "; sql += "  and o.customer_id = c.customer_id "; sql +=
			 * "  and o.user_id = c.user_id "; sql += "  and c.customer_type = '" + customerType + "' "; sql +=
			 * "  and o.user_id = " + userId; if (customerId != 0) sql += "  and o.customer_id = " + customerId; if
			 * (ConvertNullUtil.convertToString(dateFrom).trim().length() > 0) sql += "  and o.ORDER_DATE >= '" +
			 * DateToolsUtil.convertToTimeStamp(dateFrom.trim()) + "'"; if
			 * (ConvertNullUtil.convertToString(dateTo).trim().length() > 0) sql += " and o.ORDER_DATE <= '" +
			 * DateToolsUtil.convertToTimeStamp(dateTo.trim()) + "'"; sql += "  and o.ORDER_TYPE = '" +
			 * user.getOrderType().getKey() + "' "; sql += " group by o.CUSTOMER_ID, o.CUSTOMER_NAME ";
			 */

			// Aneak.t 21/01/2011
			String sql = " select T.CUSTOMER_ID, T.CUSTOMER_NAME, ";
			sql += " count(T.order_id) as tot, sum(T.netamt) as netamt ";
			sql += " from (select o.CUSTOMER_ID,o.CUSTOMER_NAME, ";

			// Aneak.t 26/01/2011
			// sql += " o.order_id,(sum(l.TOTAL_AMOUNT) - sum(l.DISCOUNT)) as netamt ";
			sql += " o.order_id,(sum(l.TOTAL_AMOUNT)) as netamt ";

			sql += " from t_order o, m_customer c, t_order_line l ";
			sql += " where o.DOC_STATUS = 'SV' ";
			sql += " and o.ORDER_ID = l.ORDER_ID ";
			sql += " and o.customer_id = c.customer_id ";
			sql += " and o.user_id = c.user_id ";
			sql += " and c.customer_type = '" + customerType + "' ";
			if (!customerType.equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
				sql += " and o.user_id = " + userId;
			}
			if (customerId != 0) {
				sql += "  and o.customer_id = " + customerId;
			}
			if (ConvertNullUtil.convertToString(dateFrom).trim().length() > 0)
				sql += "  and o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(dateFrom.trim()) + "'";
			if (ConvertNullUtil.convertToString(dateTo).trim().length() > 0)
				sql += " and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(dateTo.trim()) + "'";
			sql += " and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ";
			sql += " and l.ISCANCEL <> 'Y' ";
			sql += " group by o.CUSTOMER_ID, o.CUSTOMER_NAME, o.ORDER_NO) T ";
			sql += " group by T.CUSTOMER_ID, T.CUSTOMER_NAME ";

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			OrderThreeMonths o;
			while (rst.next()) {
				o = new OrderThreeMonths();
				o.setCustomerId(rst.getInt("CUSTOMER_ID"));
				o.setCustomerName(rst.getString("CUSTOMER_NAME"));
				o.setTotalOrder(rst.getInt("tot"));
				o.setTotalAmount(rst.getDouble("netamt"));
				o.setDateFrom(dateFrom);
				o.setDateTo(dateTo);
				pos.add(o);
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

	/**
	 * Get Product details of Customer
	 * 
	 * @param customerId
	 * @param dateFrom
	 * @param dateTo
	 * @param userId
	 * @param conn
	 * @return List of Order History
	 * @throws Exception
	 */
	private List<OrderThreeMonths> getProductDetail(String customerId, String dateFrom, String dateTo, int userId,
			Connection conn) throws Exception {
		List<OrderThreeMonths> pos = new ArrayList<OrderThreeMonths>();
		Statement stmt = null;
		ResultSet rst = null;
		try {
			User user = new MUser().find(String.valueOf(userId));
			String sql = "select ol.PRODUCT_ID, ol.UOM_ID, sum(ol.QTY) qty, sum(ol.TOTAL_AMOUNT) totamt ";
			sql += "from t_order o, t_order_line ol ";
			sql += "where o.ORDER_ID = ol.ORDER_ID ";
			sql += "  and o.CUSTOMER_ID = " + customerId;
			sql += "  and o.DOC_STATUS = 'SV' ";
			if (!user.getOrderType().getKey().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
				sql += "  and o.USER_ID = " + userId;
			}
			sql += "  and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ";
			sql += "  and ol.promotion = 'N' ";
			// Aneak.t 21/01/2011
			sql += " and ol.ISCANCEL <> 'Y' ";

			if (ConvertNullUtil.convertToString(dateFrom).trim().length() > 0)
				sql += "  and o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(dateFrom.trim()) + "'";
			if (ConvertNullUtil.convertToString(dateTo).trim().length() > 0)
				sql += " and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(dateTo.trim()) + "'";
			sql += " group by product_id, uom_id ";

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			OrderThreeMonths o;
			while (rst.next()) {
				o = new OrderThreeMonths();
				o.setProduct(new MProduct().find(rst.getString("PRODUCT_ID")));
				o.setUom(new MUOM().find(rst.getString("UOM_ID")));
				o.setTotalOrder(rst.getInt("qty"));
				o.setTotalAmount(rst.getDouble("totamt"));

				pos.add(o);
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

	/**
	 * Get Header of Customer
	 * 
	 * @param customerType
	 * @param dateFrom
	 * @param dateTo
	 * @param userId
	 * @param customerId
	 * @return List of Order History
	 * @throws Exception
	 */
	public List<OrderThreeMonths> getHeader(String customerType, String dateFrom, String dateTo, int userId,
			int customerId) throws Exception {
		List<OrderThreeMonths> pos = new ArrayList<OrderThreeMonths>();
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			pos = getOrderHeader(customerType, dateFrom, dateTo, userId, customerId, conn);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}

	/**
	 * Get Product Detail of Customer
	 * 
	 * @param customerId
	 * @param dateFrom
	 * @param dateTo
	 * @param userId
	 * @return List of Order History
	 * @throws Exception
	 */
	public List<OrderThreeMonths> getDetail(String customerId, String dateFrom, String dateTo, int userId)
			throws Exception {
		List<OrderThreeMonths> pos = new ArrayList<OrderThreeMonths>();
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			pos = getProductDetail(customerId, dateFrom, dateTo, userId, conn);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}

	public static void main(String[] args) throws Exception {
		// new OrderThreeMonthsProcess().getHeader("CT", 2);
		new OrderThreeMonthsProcess().getDetail("6", "", "", 2);
	}
}
