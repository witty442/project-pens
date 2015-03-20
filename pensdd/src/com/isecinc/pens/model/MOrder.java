package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.Database;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.OrderDocumentProcess;

/**
 * MOrder Class
 * 
 * @author atiz.b
 * @version $Id: MOrder.java,v 1.0 07/10/2010 00:00:00 atiz.b Exp $
 * 
 */

public class MOrder extends I_Model<Order> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "t_order";
	public static String COLUMN_ID = "ORDER_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "ORDER_NO", "ORDER_DATE", "ORDER_TYPE", "CUSTOMER_ID", "CUSTOMER_NAME",
			"BILL_ADDRESS_ID", "SHIP_ADDRESS_ID", "PRICELIST_ID", "PAYMENT_TERM", "VAT_CODE", "VAT_RATE",
			"PAYMENT_METHOD", "SHIPPING_DAY", "SHIPPING_TIME", "TOTAL_AMOUNT", "VAT_AMOUNT", "NET_AMOUNT",
			"INTERFACES", "PAYMENT", "SALES_ORDER_NO", "AR_INVOICE_NO", "USER_ID", "DOC_STATUS", "CREATED_BY",
			"UPDATED_BY", "ISCASH", "ORDER_TIME", "REMARK", "CALL_BEFORE_SEND","ORA_BILL_ADDRESS_ID","ORA_SHIP_ADDRESS_ID" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Order find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Order.class);
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
	public Order[] search(String whereCause) throws Exception {
		List<Order> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Order.class);
		if (pos.size() == 0) return null;
		Order[] array = new Order[pos.size()];
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
	public boolean save(Order order, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (order.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			String prefix = "";
			order.setOrderNo(new OrderDocumentProcess().getNextDocumentNo(activeUserID, conn));
		} else {
			id = order.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "ORDER_NO", order.getOrderNo(), id, conn)) return false;

		if (order.getVatCode() != null && !order.getVatCode().equals("")) {
			order.setVatRate(new Double(order.getVatCode()));
		}

		Object[] values = { id, ConvertNullUtil.convertToString(order.getOrderNo()).trim(),
				DateToolsUtil.convertToTimeStamp(order.getOrderDate()),
				ConvertNullUtil.convertToString(order.getOrderType()).trim(), order.getCustomerId(),
				ConvertNullUtil.convertToString(order.getCustomerName()).trim(), order.getBillAddressId(),
				order.getShipAddressId(), order.getPriceListId(),
				ConvertNullUtil.convertToString(order.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(order.getVatCode()).trim(), order.getVatRate(),
				ConvertNullUtil.convertToString(order.getPaymentMethod()).trim(),
				ConvertNullUtil.convertToString(order.getShippingDay()).trim(),
				ConvertNullUtil.convertToString(order.getShippingTime()).trim(), order.getTotalAmount(),
				order.getVatAmount(), order.getNetAmount(), order.getInterfaces(), order.getPayment(),
				ConvertNullUtil.convertToString(order.getSalesOrderNo()),
				ConvertNullUtil.convertToString(order.getArInvoiceNo()), order.getSalesRepresent().getId(),
				order.getDocStatus(), activeUserID, activeUserID,
				ConvertNullUtil.convertToString(order.getIsCash()).length() > 0 ? order.getIsCash() : "N",
				ConvertNullUtil.convertToString(order.getOrderTime()),
				ConvertNullUtil.convertToString(order.getRemark()).trim(),
				ConvertNullUtil.convertToString(order.getCallBeforeSend()),
				order.getOraBillAddressID(),
				order.getOraShipAddressID()
				};
		if (super.save(TABLE_NAME, columns, values, order.getId(), conn)) {
			order.setId(id);
			order.setCreated(getCreated(order.getId(), conn));
		}
		return true;
	}

	/**
	 * Save Import Order
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean saveImportOrder(Order order, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (order.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = order.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "ORDER_NO", order.getOrderNo(), id, conn)) return false;

		if (order.getVatCode() != null && !order.getVatCode().equals("")) {
			order.setVatRate(new Double(order.getVatCode()));
		}

		Object[] values = { id, ConvertNullUtil.convertToString(order.getOrderNo()).trim(),
				DateToolsUtil.convertToTimeStamp(order.getOrderDate()),
				ConvertNullUtil.convertToString(order.getOrderType()).trim(), order.getCustomerId(),
				ConvertNullUtil.convertToString(order.getCustomerName()).trim(), order.getBillAddressId(),
				order.getShipAddressId(), order.getPriceListId(),
				ConvertNullUtil.convertToString(order.getPaymentTerm()).trim(),
				ConvertNullUtil.convertToString(order.getVatCode()).trim(), order.getVatRate(),
				ConvertNullUtil.convertToString(order.getPaymentMethod()).trim(),
				ConvertNullUtil.convertToString(order.getShippingDay()).trim(),
				ConvertNullUtil.convertToString(order.getShippingTime()).trim(), order.getTotalAmount(),
				order.getVatAmount(), order.getNetAmount(), order.getInterfaces(), order.getPayment(),
				ConvertNullUtil.convertToString(order.getSalesOrderNo()),
				ConvertNullUtil.convertToString(order.getArInvoiceNo()), order.getSalesRepresent().getId(),
				order.getDocStatus(), activeUserID, activeUserID,
				ConvertNullUtil.convertToString(order.getIsCash()).length() > 0 ? order.getIsCash() : "N",
				ConvertNullUtil.convertToString(order.getOrderTime()),
				ConvertNullUtil.convertToString(order.getRemark()).trim(),
				ConvertNullUtil.convertToString(order.getCallBeforeSend())};
		if (super.save(TABLE_NAME, columns, values, order.getId(), conn)) {
			order.setId(id);
			order.setCreated(getCreated(order.getId(), conn));
		}
		return true;
	}

	/**
	 * Re-Calculate
	 * 
	 * @param order
	 * @param lines
	 * @throws Exception
	 */
	public void reCalculate(Order order, List<OrderLine> lines) throws Exception {
		DecimalFormat df = new DecimalFormat("###0.00");
		double totalAmount = 0;
		for (OrderLine l : lines) {
			totalAmount += l.getTotalAmount();
		}
		double vat = order.getVatRate() * totalAmount / 100;
		double netAmount = totalAmount + vat;
		order.setTotalAmount(new Double(df.format(totalAmount)));
		order.setVatAmount(new Double(df.format(vat)));
		order.setNetAmount(new Double(df.format(netAmount)));
	}

	/**
	 * LookUp
	 * 
	 * @param userId
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public List<Order> lookUp(int userId, int customerId, String orderType, String operator, String selected)
			throws Exception {
		List<Order> pos = new ArrayList<Order>();
		if (operator.equalsIgnoreCase("in") && selected.equalsIgnoreCase("")) return pos;
		String whereCause = "  and interfaces = 'Y' ";
		whereCause += "  and ar_invoice_no is not null ";
		whereCause += "  and ar_invoice_no <> '' ";
		whereCause += "  and order_type = '" + orderType + "' ";
		//whereCause += "  and user_id = " + userId;
		whereCause += "  and customer_id = " + customerId;
		/** Wit:Edit 12/05/2011 Add doc_status <> VO **/
		whereCause += "  and doc_status <> 'VO' ";
		if (selected.length() > 0) whereCause += "  and order_id " + operator + " (" + selected + ") ";

		pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Order.class);
		return pos;
	}


	public List<Order> lookUpByOrderAR(int userId, int customerId, String orderType, String operator, String selected)
			throws Exception {
		List<Order> pos = new ArrayList<Order>();
		if (operator.equalsIgnoreCase("in") && selected.equalsIgnoreCase("")) return pos;
		String whereCause = "  and interfaces = 'Y' ";
		whereCause += "  and ar_invoice_no is not null ";
		whereCause += "  and ar_invoice_no <> '' ";
		whereCause += "  and order_type = '" + orderType + "' ";
		//whereCause += "  and user_id = " + userId; Comment Out Because 
		whereCause += "  and customer_id = " + customerId;
		/** Wit:Edit 12/05/2011 Add doc_status <> VO **/
		whereCause += "  and doc_status <> 'VO' ";
		
		if (selected.length() > 0) whereCause += "  and order_id " + operator + " (" + selected + ") ";
		whereCause += "  order by Ar_invoice_no asc ";
		pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Order.class);
		return pos;
}
	
	/**
	 * LookUp
	 * 
	 * @param userId
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public List<Order> lookUpPrepaid(int userId, int customerId, String orderType, String operator, String selected)
			throws Exception {
		List<Order> pos = new ArrayList<Order>();
		if (operator.equalsIgnoreCase("in") && selected.equalsIgnoreCase("")) return pos;
		String whereCause = " and order_type = '" + orderType + "' ";
		whereCause += "  and user_id = " + userId;
		whereCause += "  and customer_id = " + customerId;
		if (selected.length() > 0) whereCause += "  and order_id " + operator + " (" + selected + ") ";

		pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Order.class);
		return pos;
	}

	/**
	 * LookUp
	 * 
	 * @param userId
	 * @param orderType
	 * @return
	 * @throws Exception
	 */
	public int lookUpByCustomer(int customerId) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		int tot = 0;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			String sql = "select count(*) as tot from t_order where customer_id = " + customerId;
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if (rst.next()) tot = rst.getInt("tot");
			return tot;
		} catch (Exception e) {
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
	}

	/**
	 * get Created
	 * 
	 * @param orderId
	 * @param conn
	 * @return
	 */
	private String getCreated(int orderId, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String created = "";
		try {
			String sql = "select created from t_order where order_id = " + orderId;
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if (rst.next()) created = DateToolsUtil.convertFromTimestamp(rst.getTimestamp("created"));
			return created;
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

	/**
	 * Look Up for Member
	 * 
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public List<Order> lookUpForMember(String memberId, String selected) throws Exception {
		List<Order> pos = new ArrayList<Order>();
		String whereCause = "";
		whereCause += "  and order_type = '" + Order.DIRECT_DELIVERY + "' ";
		whereCause += "  and customer_id = '" + memberId + "' ";
		Order[] orders = search(whereCause);

		List<OrderLine> lines;
		List<OrderLine> rmLines;
		List<Order> rmOrders = new ArrayList<Order>();
		String[] ss = selected.split(",");
		if(orders != null){
		for (Order r : orders) {
			lines = new MOrderLine().lookUp(r.getId());
			rmLines = new ArrayList<OrderLine>();
			for (OrderLine l : lines) {
				// remove paid
				if (l.getPayment().equalsIgnoreCase("Y")) rmLines.add(l);
				// remove selected
				for (String s : ss) {
					if (s.trim().length() > 0) {
						if (l.getId() == Integer.parseInt(s)) {
							rmLines.add(l);
						}
					}
				}
			}
			for (OrderLine l : rmLines) {
				lines.remove(l);
			}
			if (lines.size() == 0) rmOrders.add(r);
		}
		for (Order r : orders) {
			pos.add(r);
		}
		for (Order r : rmOrders) {
			pos.remove(r);
		}
		}
		return pos;
	}
	
	
	/**
	 * cancelOrderByID
	 * @param conn
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public int cancelOrderByID(Connection conn,int orderId,String remark,int userId,String payment) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "+TABLE_NAME+" SET  DOC_STATUS = ? ,REASON = ? ,UPDATED_BY =? ,UPDATED = CURRENT_TIMESTAMP WHERE order_id = ? ";
			logger.debug("order_id:"+orderId);
			logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index,Order.DOC_VOID);
			ps.setString(++index, remark);
			ps.setInt(++index, userId);
			ps.setInt(++index, orderId);
			
			//case payment update t_receipt =VO 
			if("Y".equals(payment)){
				//find receipt by order_id
				List  rList = new MReceiptLine().findByCondition(" and order_id = "+orderId);
				if(rList != null && rList.size() > 0){
				   ReceiptLine r = (ReceiptLine)rList.get(0);
				   new MReceipt().cancelReceiptByID(conn,r.getReceiptId(),remark,userId);
				}
			}
			
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}

	public Order[] setSummaryNeedBill(Order[] orders) throws Exception  {
		Connection conn = null;
		PreparedStatement ppstmt = null;
		String sql = "SELECT sum(coalesce(need_bill,0)) as summary FROM t_order_line WHERE order_id = ?  and iscancel <> 'Y' ";
		
		List<Order> orderL = new ArrayList<Order>();
		
		try {
			if(orders != null){
				conn = new DBCPConnectionProvider().getConnection(conn);
				ppstmt = conn.prepareStatement(sql);
				
				for(Order order : orders){
					ppstmt.setInt(1, order.getId());
					ResultSet rset =  ppstmt.executeQuery();
					double sumNeedBill = 0d;
					if(rset.next()){
						sumNeedBill = rset.getDouble("summary");
					}
					order.setTotalNeedBill(sumNeedBill);
					
					orderL.add(order);
					rset.close();
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(conn != null){
				   conn.close();
				   conn = null;
				}
				if(ppstmt != null){
					ppstmt.close();
					ppstmt = null;
				}
			} catch (Exception e2) {}
		}
		
		Order[] ret = new Order[orderL.size()];
		return orderL.toArray(ret);
	}

	public int reSchedule(int orderLineId, Timestamp newScheduleDate, Connection conn,int userId) throws Exception {
		if(conn == null || conn.isClosed())
			new Exception("No DB Connection Available");
		
		int no_of_updated_records = 0;
		MOrderLine oLineService = new MOrderLine();
		
		OrderLine oline = oLineService.find(""+orderLineId);
		Order oHeader = new MOrder().find(""+oline.getOrderId());
		Member member = new MMember().find(""+oHeader.getCustomerId());
		
		int currentTrip = oline.getTripNo();
		int orderId = oline.getOrderId();
		String roundtrip = member.getRoundTrip();
		
		int days_between_trip = 7;
		if(Member.ROUND_TRIP_15.equals(roundtrip))
			days_between_trip = 14;
		else if(Member.ROUND_TRIP_30.equals(roundtrip))
			days_between_trip = 28;
				
		
		// Update New Schedule Date to Order Line that have the same trip
		oline.setShippingDate(DateToolsUtil.convertToString(newScheduleDate));
		oline.setRequestDate(DateToolsUtil.convertToString(newScheduleDate));
		if(!oLineService.save(oline, userId, conn))
			throw new Exception("Cannot Update New Schedule Date to Order Line[id:"+oline.getId()+"]");
		
		no_of_updated_records++;
		StringBuffer whereClause = new StringBuffer(" and order_id = "+ orderId);
		whereClause.append("\n and trip_no >= "+currentTrip)
				   .append("\n and order_line_id <> "+orderLineId)
				   .append("\n and shipment = 'N' ")
				   .append("\n and iscancel = 'N' ")
				   .append("\n order by trip_no ");
		
		OrderLine[] olines = oLineService.search(whereClause.toString());
		int multiply = 0;
		int tripNo = currentTrip;
		if(olines != null){
			for(OrderLine line : olines){
				if(line.getTripNo() != tripNo){
					// Next Trip
					multiply++;
					tripNo = line.getTripNo();
				}
				
				Date newDate = DateToolsUtil.addDays(newScheduleDate, multiply*days_between_trip);
				
				line.setShippingDate(DateToolsUtil.convertToString(newDate));
				line.setRequestDate(DateToolsUtil.convertToString(newDate));
				if(!oLineService.save(line, userId, conn))
					throw new Exception("Cannot Update New Schedule Date to Order Line[id:"+oline.getId()+"]");
				
				//no_of_updated_records++;
			}
		}
		
		return no_of_updated_records;
	}
}
