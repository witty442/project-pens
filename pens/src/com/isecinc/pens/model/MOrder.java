package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
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
			"UPDATED_BY", "ISCASH", "ORDER_TIME", "REMARK", "CALL_BEFORE_SEND","ORA_BILL_ADDRESS_ID","ORA_SHIP_ADDRESS_ID","org"};

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
			order.setOrderNo(new OrderDocumentProcess().getNextDocumentNo(order.getSalesRepresent().getCode(), prefix,
					activeUserID, conn));
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
				order.getOraShipAddressID(),
				order.getOrg()
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
	public Order reCalculateHeadAmount(Order order, List<OrderLine> lines) throws Exception {
		DecimalFormat df = new DecimalFormat("###0.00");
		double totalAmount = 0;
		for (OrderLine l : lines) {
			totalAmount += l.getLineAmount()-l.getDiscount();
		}
		double vat = order.getVatRate() * totalAmount / 100;
		double netAmount = totalAmount + vat;
		order.setTotalAmount(new Double(df.format(totalAmount)));
		order.setVatAmount(new Double(df.format(vat)));
		order.setNetAmount(new Double(df.format(netAmount)));
		
		return order;
	}
	
	
	public Order reCalculateHeadAmountDB(Connection conn, Order order) {
		try{
			//get OrderLine 
			List<OrderLine> newlines = new MOrderLine().lookUp(conn,order.getId());
			
			DecimalFormat df = new DecimalFormat("###0.00");
			double totalAmount = 0;
			for (OrderLine l : newlines) {
				totalAmount += l.getLineAmount()-l.getDiscount();
			}
			double vat = order.getVatRate() * totalAmount / 100;
			double netAmount = totalAmount + vat;
			order.setTotalAmount(new Double(df.format(totalAmount)));
			order.setVatAmount(new Double(df.format(vat)));
			order.setNetAmount(new Double(df.format(netAmount)));
			
		}catch(Exception e){
		   logger.debug(e.getMessage(),e);
		}finally{
			
		}
		return order;
	}
	

	public List<OrderLine> reCalculateLineAmountInLinesBeforeCalcPromotion(List<OrderLine> lines) throws Exception {
		List<OrderLine> newRecallines = new ArrayList<OrderLine>();
		for (OrderLine l : lines) {
			//recalc LineAmount
			l.setLineAmount(l.getPrice()*l.getQty());
			logger.info("recal :product["+l.getProduct().getCode()+"],qty["+l.getQty()+"]price["+l.getPrice()+"] after LineAmount["+l.getLineAmount()+"]");
			newRecallines.add(l);
		}
		return newRecallines;
	}

	
	public List<OrderLine> reCalculateLineAmountInLinesAfterCalcPromotion(List<OrderLine> lines) throws Exception {
		List<OrderLine> newRecallines = new ArrayList<OrderLine>();
		for (OrderLine l : lines) {
			//recalc LineAmount
			l.setLineAmount(l.getPrice()*l.getQty());
			l.setTotalAmount(l.getLineAmount()-l.getDiscount());
			logger.info("recal :product["+l.getProduct().getCode()+"]qty["+l.getQty()+"]price["+l.getPrice()+"]LineAmount["+l.getLineAmount()+"]Disc["+l.getDiscount()+"]total["+l.getTotalAmount()+"]");
			newRecallines.add(l);
		}
		return newRecallines;
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
	
	
	public List<Order> getOrderVanNotReceipt(Connection conn,User user) throws Exception {

		Statement stmt = null;
		ResultSet rst = null;
		List<Order> orderList = new ArrayList<Order>();
		try{
			String sql =""+
			 "select * "+
			 "from t_order where 1=1 \n"+
			 "and doc_status ='SV' \n"+
			 "and payment ='N' \n"+
			 "and user_id = '"+user.getId()+"' \n"+
			 "and date(created) = date( sysdate()) \n"+
			 "and order_no like '"+user.getCode()+"%' \n"+
			 "and order_no not in( \n"+
			    "select receipt_no from t_receipt where 1=1 \n"+
			    "and user_id = '"+user.getId()+"' \n"+
			    "and doc_status ='SV' \n"+
			 ") \n";
			 
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				Order o = new Order(rst);
				
				orderList.add(o);
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
		
		return orderList;
	}
	
	public int updatePaymentByOrderId(Connection conn,int orderId,String payment) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE "+TABLE_NAME+" SET  PAYMENT = ?  ,UPDATED_BY =? ,UPDATED = CURRENT_TIMESTAMP WHERE order_id = ? ";
			logger.debug("order_id:"+orderId);
			logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setString(++index,payment);
			ps.setInt(++index, 9999);
			ps.setInt(++index, orderId);
			
			return ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	/**
    
	 * @param conn
	 * @param order
	 */
	public void updatePrintPickStamp(Connection conn ,Order order) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updatePrintPickStamp ");
		try{
			String sql = "UPDATE "+TABLE_NAME+" SET print_DateTime_Pick = ? ,print_Count_Pick = ? WHERE order_id = ? ";
			int index = 0;
			ps = conn.prepareStatement(sql);
			
		    java.util.Date pickDate = Utils.isNull(order.getPrintDateTimePick()).equals("")?null:Utils.parse(order.getPrintDateTimePick(), Utils.DD_MM_YYYY_HH_mm_WITHOUT_SLASH, Utils.local_th);
			BigDecimal pickDateBig = new BigDecimal(0);
			if(pickDate !=null){
				pickDateBig = new BigDecimal(pickDate.getTime()); 
			}
			logger.debug("orderId:"+order.getId()+",pickDateBig:"+pickDateBig);
		  
		    ps.setBigDecimal(++index, pickDateBig.setScale(6));//updated
			ps.setInt(++index, order.getPrintCountPick());
			ps.setInt(++index, order.getId());
				
			ps.execute();
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public void updatePrintRcpStamp(int orderId,String dateStr,int count)  throws Exception{
		PreparedStatement ps = null;
		Connection conn =null;
		logger.debug("updatePrintRcpStamp ");
		try{
			String sql = "UPDATE "+TABLE_NAME+" SET print_DateTime_Rcp = ? ,print_Count_rcp = ? WHERE order_id = ? ";
			int index = 0;
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql);
			
			java.util.Date pickDate = null;
			pickDate = Utils.isNull(dateStr).equals("")?null:Utils.parse(dateStr, Utils.DD_MM_YYYY_HH_mm_WITHOUT_SLASH, Utils.local_th);
            BigDecimal pickDateBig = new BigDecimal(pickDate.getTime());
            
            logger.debug("pickDateBig:"+pickDateBig);
            
		    ps.setBigDecimal(++index, pickDateBig.setScale(6));//updated
			ps.setInt(++index, count);
			ps.setInt(++index, orderId);
				
			ps.execute();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
	}
	
}
