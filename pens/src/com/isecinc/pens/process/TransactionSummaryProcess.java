package com.isecinc.pens.process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.TransactionSummaryCustomer;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MProduct;

/**
 * Transaction Summary Process Class for VISIT/ORDER/PROMOTION
 * 
 * @author Atiz.b
 * @version $Id: TransactionSummaryProcess.java,v 1.0 8/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class TransactionSummaryProcess {

	Logger log = Logger.getLogger("PENS");

	/**
	 * Get Summarty
	 * 
	 * @param criteria
	 * @return List of Transaction Summary by Customer
	 * @throws Exception
	 */
	public List<TransactionSummaryCustomer> getSummary(TransactionSummary criteria) throws Exception {
		List<TransactionSummaryCustomer> summaryCustomers = new ArrayList<TransactionSummaryCustomer>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {

			conn = new DBCPConnectionProvider().getConnection(conn);
			// new InitialReferences().init(conn);
			String whereCause = "";

			whereCause += "  and customer_id in ";
			whereCause += "( ";
			whereCause += "select o.customer_id from t_order o where o.DOC_STATUS = 'SV' and o.order_type = '"
					+ criteria.getOrderType() + "' and o.user_id = " + criteria.getUserId();
			if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
				whereCause += "  and o.ORDER_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(criteria.getDateFrom().trim()) + "'";
			if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
				whereCause += " and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateTo().trim())
						+ "' ";
			if (ConvertNullUtil.convertToString(criteria.getCustomerId()).trim().length() > 0)
				whereCause += "  and o.customer_id = " + criteria.getCustomerId().trim();

			whereCause += " union  ";

			whereCause += "select o.customer_id from t_visit o where o.ISACTIVE = 'Y' and o.order_type = '"
					+ criteria.getOrderType() + "' and o.user_id = " + criteria.getUserId();
			if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
				whereCause += "  and o.VISIT_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(criteria.getDateFrom().trim()) + "'";
			if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
				whereCause += " and o.VISIT_DATE <= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateTo().trim())
						+ "' ";
			if (ConvertNullUtil.convertToString(criteria.getCustomerId()).trim().length() > 0)
				whereCause += "  and o.customer_id = " + criteria.getCustomerId().trim();

			whereCause += ") ";

			Customer[] custs = new MCustomer().search(whereCause);
			List<Address> addrs;
			if (custs == null) return summaryCustomers;
			String sql;
			stmt = conn.createStatement();
			TransactionSummaryCustomer sc;
			boolean ba;
			for (Customer c : custs) {
				ba = false;
				sc = new TransactionSummaryCustomer();
				sc.setCustomer(c);
				log.debug(c);
				addrs = new MAddress().lookUp(c.getId());
				for (Address ad : addrs) {
					if (ad.getPurpose().equalsIgnoreCase("S")) {
						sc.setAddress(ad);
						ba = true;
						break;
					}
				}
				if (!ba) {
					for (Address ad : addrs) {
						if (ad.getPurpose().equalsIgnoreCase("B")) {
							sc.setAddress(ad);
							ba = true;
							break;
						}
					}
				}
				
				//debug
				if(sc.getAddress() != null){
				   log.debug(sc.getAddress().getLineString());
				}
				
				sql = createSQL(criteria, c.getId());
				log.debug(sql);
				rst = stmt.executeQuery(sql);
				sc.setSummaries(getResult(rst));

				summaryCustomers.add(sc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return summaryCustomers;
	}

	/**
	 * Create SQL
	 * 
	 * @param criteria
	 * @param customerId
	 * @return
	 */
	private String createSQL(TransactionSummary criteria, int customerId) throws Exception {
		String sql = "";

		sql += "select * from ( ";

		sql += "select v.VISIT_DATE as DATES,p.PRODUCT_ID, p.NAME, vl.UOM_ID as SUBUOM, ";
		sql += "sum(vl.AMOUNT+vl.AMOUNT2) as QTY,p.UOM_ID as PUOM,\"1_VSIT\" as TYPE ,0 AS PRICELIST_ID ";
		sql += "from t_visit_line vl, t_visit v , m_product p ";
		sql += "where 1=1 ";
		sql += "  and vl.PRODUCT_ID  = p.PRODUCT_ID ";
		sql += "  and vl.VISIT_ID = v.VISIT_ID ";
		sql += "  and v.ISACTIVE = 'Y' ";
		sql += "  and v.order_type = '" + criteria.getOrderType() + "' ";
		sql += "  and v.USER_ID = " + criteria.getUserId();
		sql += "  and v.CUSTOMER_ID = " + customerId;
		if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
			sql += "  and v.VISIT_DATE >= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateFrom().trim()) + "' ";
		if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
			sql += "  and v.VISIT_DATE <= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateTo().trim()) + "' ";
		sql += " group by v.VISIT_DATE, p.NAME, vl.UOM_ID, p.UOM_ID ";

		sql += "UNION ";

		sql += "select o.ORDER_DATE as DATES,p.PRODUCT_ID, p.NAME, ol.UOM_ID as SUBUOM , ";
		sql += "sum(ol.QTY) as QTY ,p.UOM_ID as PUOM,\"2_ORDER\" as TYPE ,o.PRICELIST_ID ";
		sql += "from t_order_line ol ,t_order o , m_product p ";
		sql += "where ol.promotion = 'N' ";
		sql += "  and ol.PRODUCT_ID  = p.PRODUCT_ID ";
		sql += "  and ol.ORDER_ID = o.ORDER_ID ";
		// Aneak.t 21/01/2011
		sql += " and ol.ISCANCEL = 'N' ";

		sql += "  and o.DOC_STATUS = 'SV' ";
		sql += "  and o.order_type = '" + criteria.getOrderType() + "' ";
		sql += "  and o.USER_ID = " + criteria.getUserId();
		sql += "  and o.CUSTOMER_ID = " + customerId;
		if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
			sql += "  and o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateFrom().trim()) + "' ";
		if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
			sql += "  and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateTo().trim()) + "' ";
		sql += " group by o.ORDER_DATE, p.NAME,ol.UOM_ID,p.UOM_ID ";

		sql += "UNION ";

		sql += "select o.ORDER_DATE as DATES,p.PRODUCT_ID, p.NAME, ol.UOM_ID as SUBUOM, ";
		sql += "sum(ol.QTY) as QTY ,p.UOM_ID as PUOM,\"3_PROMO\" as TYPE, o.PRICELIST_ID ";
		sql += "from t_order_line ol ,t_order o , m_product p ";
		sql += "where ol.promotion = 'Y' ";
		sql += "  and ol.PRODUCT_ID  = p.PRODUCT_ID ";
		sql += "  and ol.ORDER_ID = o.ORDER_ID ";
		// Aneak.t 21/01/2011
		sql += " and ol.ISCANCEL = 'N' ";

		sql += "  and o.DOC_STATUS = 'SV' ";
		sql += "  and o.order_type = '" + criteria.getOrderType() + "' ";
		sql += "  and o.USER_ID = " + criteria.getUserId();
		sql += "  and o.CUSTOMER_ID = " + customerId;
		if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
			sql += "  and o.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateFrom().trim()) + "' ";
		if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
			sql += "  and o.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(criteria.getDateTo().trim()) + "' ";
		sql += " group by o.ORDER_DATE, p.NAME,ol.UOM_ID,p.UOM_ID ";

		sql += ") a ";

		sql += " order by DATES, NAME, PRODUCT_ID, SUBUOM, TYPE ";

		return sql;
	}

	/**
	 * Get Result
	 * 
	 * @param rst
	 * @return List of Transaction Summary
	 * @throws Exception
	 */
	private List<TransactionSummary> getResult(ResultSet rst) throws Exception {
		List<TransactionSummary> pos = new ArrayList<TransactionSummary>();
		String oldProduct = "";
		TransactionSummary s = null;
		Product product;

		while (rst.next()) {
			if (!oldProduct.equalsIgnoreCase(rst.getString("PRODUCT_ID"))) {
				if (oldProduct.length() > 0) {
					pos.add(s);
				}
				s = new TransactionSummary();
			}
			product = new MProduct().find(rst.getString("PRODUCT_ID"));
			s.setTransactionDate(DateToolsUtil.convertToString(rst.getTimestamp("DATES")));
			s.setTransactionProduct(product.getCode() + " " + product.getName());
			oldProduct = String.valueOf(product.getId());
			// s.setPriceListId(rst.getInt("PRICELIST_ID"));
			if (rst.getString("TYPE").equalsIgnoreCase("1_VSIT")) {
				if (rst.getString("SUBUOM").equalsIgnoreCase(rst.getString("PUOM"))) {
					s.setStockMainUOM(s.getStockMainUOM() + rst.getInt("QTY"));
				} else {
					s.setStockSubUOM(s.getStockSubUOM() + rst.getInt("QTY"));
				}
			}
			if (rst.getString("TYPE").equalsIgnoreCase("2_ORDER")) {
				if (rst.getString("SUBUOM").equalsIgnoreCase(rst.getString("PUOM"))) {
					s.setOrderMainUOM(s.getOrderMainUOM() + rst.getInt("QTY"));
				} else {
					s.setOrderSubUOM(s.getOrderSubUOM() + rst.getInt("QTY"));
				}
			}
			if (rst.getString("TYPE").equalsIgnoreCase("3_PROMO")) {
				if (rst.getString("SUBUOM").equalsIgnoreCase(rst.getString("PUOM"))) {
					s.setPromoMainUOM(s.getPromoMainUOM() + rst.getInt("QTY"));
				} else {
					s.setPromoSubUOM(s.getPromoSubUOM() + rst.getInt("QTY"));
				}
			}
		}
		if (s != null) {
			pos.add(s);
		}
		return pos;
	}

	public static void main(String[] args) throws Exception {
		TransactionSummary criteria = new TransactionSummary();
		criteria.setUserId(2);
		criteria.setOrderType("CR");
		new TransactionSummaryProcess().getSummary(criteria);
	}
}
