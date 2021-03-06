package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.SalesTargetNew;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

/**
 * MOrderLine Class
 * 
 * @author atiz.b
 * @version $Id: MOrderLine.java,v 1.0 07/10/2010 00:00:00 atiz.b Exp $
 * 
 */

public class MOrderLine extends I_Model<OrderLine> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_order_line";
	public static String COLUMN_ID = "ORDER_LINE_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "LINE_NO", "ORDER_ID", "PRODUCT_ID", "UOM_ID", "PRICE", "QTY",
			"LINE_AMOUNT", "DISCOUNT", "TOTAL_AMOUNT", "REQUEST_DATE", "SHIPPING_DATE", "PROMOTION", "CREATED_BY",
			"UPDATED_BY", "PAYMENT", "VAT_AMOUNT", "TRIP_NO", "PROMOTION_FROM", "ISCANCEL", "NEED_EXPORT", "EXPORTED",
			"INTERFACES","CALL_BEFORE_SEND","org","sub_inv","taxable" ,"selling_price"};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OrderLine find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, OrderLine.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public OrderLine[] search(String whereCause) throws Exception {
		List<OrderLine> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		if (pos.size() == 0) return null;
		OrderLine[] array = new OrderLine[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(OrderLine line, int activeUserID, Connection conn) throws Exception {
		long id = 0;
		if (line.getId() == 0) {
			id = SequenceProcessAll.getIns().getNextValue("m_order_line.order_line_id").longValue();
		} else {
			id = line.getId();
		}
		Object[] values = { id, line.getLineNo(), line.getOrderId(), line.getProduct().getId(), line.getUom().getId(),
				line.getPrice(), line.getQty(), line.getLineAmount(), line.getDiscount(), line.getTotalAmount(),
				DateToolsUtil.convertToTimeStamp(line.getRequestDate()),
				DateToolsUtil.convertToTimeStamp(line.getShippingDate()), line.getPromotion(), activeUserID,
				activeUserID, line.getPayment(), line.getVatAmount(), line.getTripNo(),
				ConvertNullUtil.convertToString(line.getPromotionFrom()),
				line.getIscancel() != null ? line.getIscancel() : "N",
				line.getNeedExport() != null ? line.getNeedExport() : "N",
				line.getExported() != null ? line.getExported() : "N",
				line.getInterfaces() != null ? line.getInterfaces() : "N" ,
				line.getCallBeforeSend() != null ? line.getCallBeforeSend() : "N",
				line.getOrg(),line.getSubInv(),Utils.isNull(line.getTaxable()) ,
				line.getSellingPrice()
			  };
		if (super.save(TABLE_NAME, columns, values, line.getId(), conn)) {
			line.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<OrderLine> lookUp(long orderId) {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND ISCANCEL='N' ORDER BY TRIP_NO, LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	public List<OrderLine> lookUp(Connection conn,long orderId) {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND ISCANCEL='N' ORDER BY TRIP_NO, LINE_NO ";
			pos = super.search(conn,TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look Up By OrderNo for(report)
	 */
	public List<OrderLine> lookUpByCondition(String whereSQL) {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		try {
			String whereCause = " AND ORDER_ID IN (SELECT ORDER_ID FROM t_order where "+whereSQL+" )  ORDER BY ORDER_ID";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Delete Promotion
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deletePromotion(long orderId, Connection conn) throws Exception {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		String deleteId = "";
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND PROMOTION = 'Y' ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
			for (OrderLine l : pos)
				deleteId += "," + l.getId();
			if (deleteId.length() > 0) {
				deleteId = deleteId.substring(1);
				return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	//Y and S
	public boolean deletePromotionAll(long orderId, Connection conn) throws Exception {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		String deleteId = "";
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND PROMOTION IN('Y','S') ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
			for (OrderLine l : pos)
				deleteId += "," + l.getId();
			if (deleteId.length() > 0) {
				deleteId = deleteId.substring(1);
				return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Delete All Lines
	 * 
	 * @param orderId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean deleteAllLines(int orderId, Connection conn) throws Exception {
		List<OrderLine> pos = new ArrayList<OrderLine>();
		String deleteId = "";
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, OrderLine.class);
			for (OrderLine l : pos)
				deleteId += "," + l.getId();
			if (deleteId.length() > 0) {
				deleteId = deleteId.substring(1);
				return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Check lineNo duplicate  and reorg lineno
	 * @param orderId
	 * @param conn
	 * @throws Exception
	 */
	public void reOrgLineNo(long orderId, Connection conn) throws Exception {
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		ResultSet rst2 = null;
		int lineNo = 1;
		try {
			String sql = "select count(*) as dup from t_order_line  where order_id ="+orderId+" group by line_no having count(*) > 1";
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				// line_no duplicate and reorg line_no 
				sql = "select order_line_id from t_order_line  where order_id ="+orderId+"";
				stmt = conn.createStatement();
				rst2 = stmt.executeQuery(sql);

				//update 
				ps = conn.prepareStatement("update t_order_line set line_no =? where order_line_id =? ");
				while(rst2.next()){
					ps.setInt(1, lineNo);
					ps.setInt(2, rst2.getInt("order_line_id"));
					ps.executeUpdate();
					lineNo++;
				}
			}
		
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e) {}
			try {
				rst2.close();
			} catch (Exception e) {}
			try {
				stmt.close();
			} catch (Exception e) {}
			try {
				ps.close();
			} catch (Exception e) {}
		}
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}

	private static String COMPARE_FOR_AMOUNT = "AMT";
	private static String COMPARE_FOR_BASE_QTY = "QTY";
	private static String COMPARE_FOR_SUB_QTY = "SUBQTY";
	private static String COMPARE_FOR_BASE_PROMO = "PROMO";
	private static String COMPARE_FOR_SUB_PROMO = "SUBPROMO";

	/**
	 * Compare Sales Target
	 * 
	 * @param targets
	 * @throws Exception
	 */
	public void compareSalesTarget(Connection conn,SalesTargetNew[] targets,SalesTargetNew salesTarget) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String dateFrom="";
		String dateTo ="";
		try {
			dateFrom = salesTarget.getSalesStartDate();
			dateTo = salesTarget.getSalesEndDate();
			logger.debug("dateFrom:"+dateFrom);
			logger.debug("dateTo:"+dateTo);
			
			stmt = conn.createStatement();
			String sql = "";
			double totalAmount = 0;
			double percentCompare = 0;
			int qty = 0;
			// List<ProductPrice> pps;
			// MProductPrice mProductPrice = new MProductPrice();
			// UOM baseUOM = null;
			// UOM uom = null;
			// int capacity;
			// int tempB = 0;
			// int tempS = 0;
			for (SalesTargetNew t : targets) {
				// logger.debug(t);
				totalAmount = 0;
				// compare amount
				sql = createCompareQuery(t, COMPARE_FOR_AMOUNT,dateFrom,dateTo);
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					totalAmount += rst.getDouble("amount");
				}
				t.setSoldAmount(totalAmount);
				// calculate %
				percentCompare = 100 / t.getTargetAmount() * t.getSoldAmount();
				t.setPercentCompare(percentCompare);

				// compare qty base
				sql = createCompareQuery(t, COMPARE_FOR_BASE_QTY,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setBaseQty(qty);
				// sub
				sql = createCompareQuery(t, COMPARE_FOR_SUB_QTY,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setSubQty(qty);

				// compare promo base
				sql = createCompareQuery(t, COMPARE_FOR_BASE_PROMO,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setBasePromo(qty);
				// sub
				sql = createCompareQuery(t, COMPARE_FOR_SUB_PROMO,dateFrom,dateTo);
				qty = 0;
				rst = stmt.executeQuery(sql);
				while (rst.next()) {
					qty += rst.getInt("amount");
				}
				t.setSubPromo(qty);

				// uom conversion
				// pps = mProductPrice.lookUp(t.getProduct().getId(), t.getPriceList().getId());
				// for (ProductPrice pp : pps) {
				// if (pp.getUom().getId().equalsIgnoreCase(t.getProduct().getUom().getId())) {
				// baseUOM = pp.getUom();
				// } else {
				// uom = pp.getUom();
				// }
				// }
				// capacity = new Double(new MUOMConversion().getCapacity(baseUOM, uom, t.getProduct())).intValue();
				// // transfer from subQty>baseQty
				// tempB = t.getSubQty() / capacity;
				// tempS = t.getSubQty() % capacity;
				//
				// t.setBaseQty(t.getBaseQty() + tempB);
				// t.setSubQty(tempS);
				//
				// // transfer from subQty>baseQty
				// tempB = t.getSubPromo() / capacity;
				// tempS = t.getSubPromo() % capacity;
				//
				// t.setBasePromo(t.getBasePromo() + tempB);
				// t.setSubPromo(tempS);
				// logger.debug("xxx");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e) {}
			try {
				stmt.close();
			} catch (Exception e) {}
		}
	}

	private String createCompareQuery(SalesTargetNew t, String comparefor,String dateFrom,String dateTo) throws Exception {
		String sql = "";
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_AMOUNT)) {
			sql = "select sum(line_amount) amount from t_order_line \r\n";
		} else {
			sql = "select sum(qty) amount from t_order_line \r\n";
		}
		sql += "where product_id = " + t.getProduct().getId() + " \r\n";
		sql += "  and ISCANCEL = 'N' \r\n";
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_BASE_QTY)) {
			sql += "  and UOM_ID = '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'N' ";
		}
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_SUB_QTY)) {
			sql += "  and UOM_ID <> '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'N' ";
		}
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_BASE_PROMO)) {
			sql += "  and UOM_ID = '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'Y' ";
		}
		if (comparefor.equalsIgnoreCase(COMPARE_FOR_SUB_PROMO)) {
			sql += "  and UOM_ID <> '" + t.getProduct().getUom().getId() + "' ";
			sql += "  and PROMOTION = 'Y' ";
		}
		sql += "  and ORDER_ID in ( \r\n";
		sql += "	select ORDER_ID from t_order where doc_status = 'SV' \r\n";

		sql += "	  and order_date >= '" + (DateToolsUtil.convertToTimeStamp(dateFrom==null?t.getTargetFrom():dateFrom)) + "' \r\n";
		sql += "	  and order_date <= '" + (DateToolsUtil.convertToTimeStamp(dateTo==null?t.getTargetTo():dateTo)) + "' \r\n";
		
		sql += "	  and user_id = " + t.getUserId() + " \r\n";
		sql += "	  and ar_invoice_no is not null \r\n";
		sql += "  ) \r\n";

		logger.debug("sql:\n"+sql.toString());
		return sql;
	}
	
	/** 
	 * updateOrderLineToCancelNoUpdateFlag
	 * @param conn
	 * @param orderId
	 * @param userId
	 * @return
	 * @throws Exception
	 * Description update t_order_line  update_flag <> 'Y'
	 */
	public int updateOrderLineToCancelNoUpdateFlag(Connection conn,String orderId,int userId) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "+TABLE_NAME+" SET  ISCANCEL ='Y' ,update_flag ='N', \n"
					+ "remark ='Line is cancel from oracle' ,UPDATED_BY ="+userId+" ,UPDATED = CURRENT_TIMESTAMP \n "
					+ "WHERE order_id = "+orderId+" and ( update_flag <> 'Y'  or update_flag is null) ";
			logger.debug("SQL:"+sql);
			ps = conn.prepareStatement(sql);
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	public void updatePaymentOrderLine(Connection conn,long orderId,String payment) throws Exception {
		Statement stmt = null;
		try {
			String sql = "update pensonline.t_order_line set payment ='"+payment+"' where order_id ="+orderId;
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {}
			
		}
	}
}
