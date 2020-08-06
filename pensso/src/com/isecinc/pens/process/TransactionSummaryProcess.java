package com.isecinc.pens.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.TransactionSummary;
import com.isecinc.pens.bean.TransactionSummaryCustomer;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MProduct;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateUtil;

/**
 * Transaction Summary Process Class for VISIT/ORDER/PROMOTION
 * 
 * @author Atiz.b
 * @version $Id: TransactionSummaryProcess.java,v 1.0 8/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class TransactionSummaryProcess {

	Logger logger = Logger.getLogger("PENS");

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

			whereCause += "\n  and customer_id in ";
			whereCause += "\n( ";
			whereCause += "\n select o.customer_id from t_order o where o.DOC_STATUS = '"+I_PO.STATUS_LOADING+"' and o.order_type = '"
					+ criteria.getOrderType() + "' and o.user_id = " + criteria.getUserId();
			if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
				whereCause += "  and o.ORDER_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
			if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
				whereCause += " and o.ORDER_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
						
			if (ConvertNullUtil.convertToString(criteria.getCustomerId()).trim().length() > 0)
				whereCause += "  and o.customer_id = " + criteria.getCustomerId().trim();

			whereCause += "\n union  ";

			whereCause += "\nselect o.customer_id from t_visit o where o.ISACTIVE = 'Y' and o.order_type = '"
					+ criteria.getOrderType() + "' and o.user_id = " + criteria.getUserId();
			if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
				whereCause += "\n  and o.VISIT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
			if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
				whereCause += "\n and o.VISIT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
			if (ConvertNullUtil.convertToString(criteria.getCustomerId()).trim().length() > 0)
				whereCause += "\n  and o.customer_id = " + criteria.getCustomerId().trim();

			whereCause += "\n) ";

			logger.debug("whereCause:"+whereCause);
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
				logger.debug(c);
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
					logger.debug(sc.getAddress().getLineString());
				}
				
				sql = createSQL(criteria, c.getId());
				logger.debug(sql);
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
	private String createSQL(TransactionSummary criteria, long customerId) throws Exception {
		String sql = "";

		sql += "select * from ( \n";

		sql += "select v.VISIT_DATE as DATES,p.PRODUCT_ID, p.NAME, vl.UOM_ID as SUBUOM, \n";
		sql += "sum(vl.AMOUNT+vl.AMOUNT2) as QTY,p.UOM_ID as PUOM,'1_VSIT' as TYPE ,0 AS PRICELIST_ID \n";
		sql += "from t_visit_line vl, t_visit v , m_product p \n";
		sql += "where 1=1 \n";
		sql += "  and vl.PRODUCT_ID  = p.PRODUCT_ID \n";
		sql += "  and vl.VISIT_ID = v.VISIT_ID \n";
		sql += "  and v.ISACTIVE = 'Y' \n";
		sql += "  and v.order_type = '" + criteria.getOrderType() + "' \n";
		sql += "  and v.USER_ID = " + criteria.getUserId() +"\n";
		sql += "  and v.CUSTOMER_ID = " + customerId;
		if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
			sql += "  and v.VISIT_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
		if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
			sql += "  and v.VISIT_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
		sql += " group by v.VISIT_DATE,p.PRODUCT_ID, p.NAME, vl.UOM_ID, p.UOM_ID \n";

		sql += "UNION \n";

		sql += "select o.ORDER_DATE as DATES,p.PRODUCT_ID, p.NAME, ol.UOM_ID as SUBUOM , \n";
		sql += "sum(ol.QTY) as QTY ,p.UOM_ID as PUOM,'2_ORDER' as TYPE ,o.PRICELIST_ID \n";
		sql += "from t_order_line ol ,t_order o , m_product p \n";
		sql += "where ol.promotion = 'N' \n";
		sql += "  and ol.PRODUCT_ID  = p.PRODUCT_ID \n";
		sql += "  and ol.ORDER_ID = o.ORDER_ID \n";
		// Aneak.t 21/01/2011
		sql += " and ol.ISCANCEL = 'N' \n";

		sql += "  and o.DOC_STATUS = '"+I_PO.STATUS_LOADING+"' \n";
		sql += "  and o.order_type = '" + criteria.getOrderType() + "' \n";
		sql += "  and o.USER_ID = " + criteria.getUserId()+"\n";
		sql += "  and o.CUSTOMER_ID = " + customerId +"\n";
		if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
			sql += "  and o.ORDER_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
		if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
			sql += "  and o.ORDER_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
		sql += " group by o.ORDER_DATE,p.PRODUCT_ID, p.NAME,ol.UOM_ID,p.UOM_ID,o.PRICELIST_ID  \n";

		sql += "UNION \n";

		sql += "select o.ORDER_DATE as DATES,p.PRODUCT_ID, p.NAME, ol.UOM_ID as SUBUOM, \n";
		sql += "sum(ol.QTY) as QTY ,p.UOM_ID as PUOM,'3_PROMO' as TYPE, o.PRICELIST_ID \n";
		sql += "from t_order_line ol ,t_order o , m_product p \n";
		sql += "where ol.promotion = 'Y' \n";
		sql += "  and ol.PRODUCT_ID  = p.PRODUCT_ID \n";
		sql += "  and ol.ORDER_ID = o.ORDER_ID \n";
		// Aneak.t 21/01/2011
		sql += " and ol.ISCANCEL = 'N' \n";

		sql += "  and o.DOC_STATUS = '"+I_PO.STATUS_LOADING+"' \n";
		sql += "  and o.order_type = '" + criteria.getOrderType() + "' \n";
		sql += "  and o.USER_ID = " + criteria.getUserId()+"\n";
		sql += "  and o.CUSTOMER_ID = " + customerId +"\n";
		if (ConvertNullUtil.convertToString(criteria.getDateFrom()).trim().length() > 0)
			sql += "  and o.ORDER_DATE >= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
		if (ConvertNullUtil.convertToString(criteria.getDateTo()).trim().length() > 0)
			sql += "  and o.ORDER_DATE <= to_date('"+DateUtil.convBuddhistToChristDate(criteria.getDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy') \n";
		sql += " group by o.ORDER_DATE,p.PRODUCT_ID, p.NAME,ol.UOM_ID,p.UOM_ID,o.PRICELIST_ID  \n";

		sql += ") a \n";

		sql += " order by DATES, NAME, PRODUCT_ID, SUBUOM, TYPE \n";

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
