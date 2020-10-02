package com.isecinc.pens.web.sales;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnectionApps;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class InterfaceOrderProcess extends I_PO{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7361678384057199067L;
	public static Logger logger = Logger.getLogger("PENS");

	public static void main(String[] a){
		try{
		    //process(9);
		   
			//canOrderAvaliable(new BigDecimal("68"));
			
			reserveOrderCredit(null,29,"");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//Reserve Order (Credit)
	public static Map<String,String> reserveOrderCredit(User user, long orderId,String orderNo) throws Exception{
		Map<String,String> productErrorMap = null;
		try{
			logger.info("---Start reserveOrderCredit---");
			
			//clear interface flag for new  run
			clearInterfaceFlagOrderCredit(orderId);
			
			//Check can Order in Stock Onhand
			canOrderCreditAvaliable(orderId);
			
			//check order line is can Reserve
			//case error :return productErrorList
			productErrorMap = checkStatusOrderCreditReserve(orderId);
			
			logger.info("---end reserveOrderCredit---");
			
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		return productErrorMap;
	}
	
	//Reserve Order EDI (MT)
	public static Map<String,String> reserveOrderEDI(User user, long headerId,String orderNo) throws Exception{
		Map<String,String> productErrorMap = null;
		try{
			logger.info("---Start reserveOrderEDI---");
			
			//clear interface flag for new  run
			clearInterfaceFlagOrderEDI(headerId);
			
			//Check Order EDI in Stock Onhand
			canOrderEDIAvaliable(headerId);
			
			//check order line is can Reserve
			//case error :return productErrorList
			productErrorMap = checkStatusOrderEDIReserve(headerId);
			
			logger.info("---end reserveOrderEDI---");
			
		}catch(Exception e){
			throw e;
		}finally{
			
		}
		return productErrorMap;
	}
		
	public static boolean clearInterfaceFlagOrderCredit(long orderId) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			ps = conn.prepareStatement("update pensso.t_order set check_available =null ,updated=sysdate where order_id="+orderId+"");
			ps.execute();
			
			ps = conn.prepareStatement("update pensso.t_order_line set check_available =null ,updated=sysdate  where order_id="+orderId+"");
			ps.execute();
			
			return true;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
			conn.close();
		}
	}
	public static boolean clearInterfaceFlagOrderEDI(long headerId) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			ps = conn.prepareStatement("update pensso.t_edi set check_available =null where header_id="+headerId+"");
			ps.execute();
			
			ps = conn.prepareStatement("update pensso.t_edi_line set check_available =null  where header_id="+headerId+"");
			ps.execute();
			
			return true;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
			conn.close();
		}
	}
	/*
	 * run after Finish :LOADING
	 */
	
	public static Map<String,String> createOrderTemp(User user) {
		String errorMsg = "";
		Connection conn = null;
		BigDecimal headerId = new BigDecimal("0");
		Map<String,String> productErrorMap = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
	
			//for order doc_status ='CONFRIM PICKING 
			List<Order> orderLoadingList = getOrderLoadingList(conn);
			if(orderLoadingList != null){
				for(Order o:orderLoadingList){
			      //Get Seq 
			      headerId = SequenceProcessAll.getIns().getNextValueBySeq("pensso.so_order_headers_s.nextval");
			
				  //insert order head temp
				  insertOrderHeadTemp(conn,headerId, o.getId(),o.getOrderType());
				
				  //insert order line temp
				  insertOrderDetailTemp(conn,headerId, o.getId(),o.getOrderType());
				}//for
			}
			
			conn.commit();
			
		}catch(Exception e){
			try{
			  conn.rollback();
			}catch(Exception ee){}
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  conn.close();
			}catch(Exception ee){}
		}
		return productErrorMap;
	}
	public static List<Order> getOrderLoadingList(Connection conn) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		List<Order> orderList = new ArrayList<Order>();
		try{
			sql = " select order_id ,'S' from pensso.t_order where doc_status='"+I_PO.STATUS_LOADING+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
			  Order o = new Order();
			  o.setId(rs.getLong("order_id"));
			  o.setOrderType("S");
			  orderList.add(o);
			}
			
			sql = " select header_id as order_id rom pensso.t_edi where doc_status='"+I_PO.STATUS_LOADING+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
			  Order o = new Order();
			  o.setId(rs.getLong("order_id"));
			  o.setOrderType("E");
			  orderList.add(o);
			}
			
			return orderList;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
		}
	}
	
	public static boolean canOrderCreditAvaliable(long orderId) throws Exception{
		boolean r = true;
		CallableStatement  cs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			sql.append("{ call xxpens_om_sales_online_pkg.check_available(?) }");
			cs = conn.prepareCall(sql.toString());
			cs.setLong(1, orderId);
			cs.execute();
			
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			cs.close();
			conn.close();
		}
	}
	public static boolean canOrderEDIAvaliable(long headerId) throws Exception{
		boolean r = true;
		CallableStatement  cs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			sql.append("{ call xxpens_om_sales_online_pkg.check_edi_available(?) }");
			cs = conn.prepareCall(sql.toString());
			cs.setLong(1, headerId);
			cs.execute();
			
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			cs.close();
			conn.close();
		}
	}
	
	public static boolean deleteStockReservation(long reserveId) throws Exception{
		boolean r = true;
		CallableStatement  cs = null;
		Connection conn = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			sql.append("{ call xxpens_om_sales_online_pkg.delete_reservation(?) }");
			cs = conn.prepareCall(sql.toString());
			cs.setLong(1, reserveId);
			cs.execute();
			conn.commit();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			cs.close();
			conn.close();
		}
	}
	public static Map<String,String> checkStatusOrderCreditReserve(long orderId) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		String statusOrderReserve = "";
		String orderNo = "";
		Map<String,String> productErrorMap = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			String sql ="\n select order_id,check_available,order_no from "
					+ "\n pensso.t_order where order_id="+orderId +"" ;
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql);
			rst = ps.executeQuery(sql);
			if(rst.next()){
				statusOrderReserve = Utils.isNull(rst.getString("check_available"));
				orderNo = Utils.isNull(rst.getString("order_no"));;
			}
			
			//for test 
			//statusOrderReserve ="E";
			
			//can reserve stock 
			if("S".equalsIgnoreCase(statusOrderReserve)){
				//update order status = RESERVE
				updateStatusOrderCredit(conn, orderNo, STATUS_RESERVE);
				logger.debug(" result success");
			//error cannot reserve
			}else if("E".equalsIgnoreCase(statusOrderReserve)){
				//update order status = UNAVAILABLE
				updateStatusOrderCredit(conn, orderNo, STATUS_UNAVAILABLE);
				
				//cannot reserver stock  ,display error to sales
				productErrorMap = getOrderCreditLineTempErrorList(conn, orderId);
				
				logger.debug(" result error productErrorMap:"+productErrorMap!=null?productErrorMap.toString():"NULL");
			}
			return productErrorMap;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e2) {}
		}
	}
	public static Map<String,String> checkStatusOrderEDIReserve(long headerId) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		String statusOrderReserve = "";
		String orderNo = "";
		Map<String,String> productErrorMap = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			String sql ="\n select header_id,check_available,cust_po_number as order_no from "
					+ "\n pensso.t_edi where header_id="+headerId +"" ;
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql);
			rst = ps.executeQuery(sql);
			if(rst.next()){
				statusOrderReserve = Utils.isNull(rst.getString("check_available"));
				orderNo = Utils.isNull(rst.getString("order_no"));;
			}
			
			//for test 
			//statusOrderReserve ="E";
			
			//can reserve stock 
			if("S".equalsIgnoreCase(statusOrderReserve)){
				//update order status = RESERVE
				updateStatusOrderEDI(conn, orderNo, STATUS_RESERVE);
				logger.debug(" result success");
			//error cannot reserve
			}else if("E".equalsIgnoreCase(statusOrderReserve)){
				//update order status = UNAVAILABLE
				updateStatusOrderEDI(conn, orderNo, STATUS_UNAVAILABLE);
				
				//cannot reserver stock  ,display error to sales
				productErrorMap = getOrderEDILineTempErrorList(conn, headerId);
				
				logger.debug(" result error productErrorMap:"+productErrorMap!=null?productErrorMap.toString():"NULL");
			}
			return productErrorMap;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e2) {}
		}
	}
	public static boolean updateStatusOrderCredit(Connection conn,String orderNo,String status) throws Exception{
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("update pensso.t_order set doc_status ='"+status+"' where order_no='"+orderNo+"'");
			ps.execute();
			return true;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
		}
	}
	public static boolean updateStatusOrderEDI(Connection conn,String orderNo,String status) throws Exception{
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("update pensso.t_edi set doc_status ='"+status+"' where cust_po_number='"+orderNo+"'");
			ps.execute();
			return true;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
		}
	}
	public static boolean deletePrevTempOrder(Connection conn,String orderNo) throws Exception{
		PreparedStatement ps = null;
		String sql = "";
		logger.debug("deleteTempOrder PrevSubmit orderNo["+orderNo+"]");
		try{
			sql  ="delete from pensso.SO_ORDER_LINES ";
			sql +="where header_id =(select header_id from pensso.SO_ORDER_HEADERS where order_number='"+orderNo+"')" ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			ps = conn.prepareStatement("delete from pensso.SO_ORDER_HEADERS where order_number='"+orderNo+"'");
			ps.execute();
			return true;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
		}
	}
	public static Map<String,String> getOrderCreditLineTempErrorList(Connection conn,long orderId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		Map<String,String> productErrorMap = new HashMap<String, String>();
		try{
			sql = " select product_id from pensso.t_order_line where order_id ="+orderId;
			sql += "and check_available ='E' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				productErrorMap.put(rs.getString("product_id"),rs.getString("product_id"));
			}
			return productErrorMap;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
		}
	}
	
	public static Map<String,String> getOrderEDILineTempErrorList(Connection conn,long headerId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		Map<String,String> productErrorMap = new HashMap<String, String>();
		try{
			sql = " select inventory_item_id from pensso.t_edi_line where header_id ="+headerId;
			sql += "and check_available ='E' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				productErrorMap.put(rs.getString("inventory_item_id"),rs.getString("inventory_item_id"));
			}
			return productErrorMap;
		}catch(Exception e){
			throw e;
		}finally{
			ps.close();
		}
	}
	public static boolean insertOrderHeadTemp(Connection conn,BigDecimal headerId,long orderId,String orderType) throws Exception{
		boolean r= true;
		String str = "";
		StringBuffer sqlIns = new StringBuffer("");
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		ResultSet rs = null;
		int index = 1;
		try{
			if("S".equalsIgnoreCase(orderType)){
				str ="	select 	order_id,			\n"+
						"	t.ORDER_NO	AS	ORDER_NUMBER  	,	\n"+
						"	t.ORDER_TYPE 	AS	ORDER_TYPE 	,	\n"+
						"	t.ORDER_DATE	AS	ORDER_DATE	,	\n"+
						"	t.ORDER_TIME	AS	ORDER_TIME	,	\n"+
						"	t.CUSTOMER_ID	AS	CUSTOMER_ID	,	\n"+
						"	m.CODE	AS	CUSTOMER_NUMBER	,	\n"+
						"	m.NAME	AS	CUSTOMER_NAME	,	\n"+
						"	t.SHIP_ADDRESS_ID	AS	SHIP_TO_SITE_USE_ID ,\n"+
						"	t.BILL_ADDRESS_ID	AS	BILL_TO_SITE_USE_ID	,\n"+
						"	t.PAYMENT_TERM	AS	PAYMENT_TERM	,	\n"+
						"	t.USER_ID	AS	SALESREP_ID	,	\n"+
						"	t.PRICELIST_ID	AS	PRICELIST_ID	,	\n"+
						"	(select max(value) from pensso.c_reference where code ='OrgID') AS ORG_ID	,	\n"+
						"	t.VAT_CODE	AS	VAT_CODE	,	\n"+
						"	t.VAT_RATE	AS	VAT_RATE 	,	\n"+
						"	t.PAYMENT_METHOD	AS	PAYMENT_METHOD	,	\n"+
						"	t.SHIPPING_DAY	AS	SHIPPING_DAY	,	\n"+
						"	t.SHIPPING_TIME	AS	SHIPPING_TIME	,	\n"+
						"	t.TOTAL_AMOUNT	AS	TOTAL_AMOUNT	,	\n"+
						"	t.VAT_AMOUNT	AS	VAT_AMOUNT	,	\n"+
						"	t.NET_AMOUNT	AS	NET_AMOUNT	,	\n"+
						"	t.PAYMENT	AS	PAYMENT	,	\n"+
						"	t.SALES_ORDER_NO	AS	SALES_ORDER_NO	,	\n"+
						"	t.AR_INVOICE_NO	AS	AR_INVOICE_NO	,	\n"+
						"	t.DOC_STATUS	AS	DOC_STATUS	,	\n"+
						"	t.ORA_BILL_ADDRESS_ID 	AS	ORA_BILL_ADDRESS_ID	,	\n"+
						"	t.ORA_SHIP_ADDRESS_ID	AS	ORA_SHIP_ADDRESS_ID	,	\n"+
						"   t.org as ORG ,t.created, t.print_datetime_pick, \n"+
						"   t.po_number ,COALESCE(t.iscash,'N') as iscash \n"+
						"	FROM pensso.t_order t ,pensso.m_customer m	\n"+
						"	where t.CUSTOMER_ID = m.CUSTOMER_ID	\n"+
						"   and t.order_id ="+orderId+
						"   and t.doc_status IN('"+Order.STATUS_LOADING+"') \n"+
						"   ORDER BY t.ORDER_NO \n";
			}else{
				//EDI
				
			}
			//logger.debug("sql:"+str);
			
			//Prepare sql insert
			sqlIns.append("insert into pensso.SO_ORDER_HEADERS ( \n");
			sqlIns.append(" header_id,order_number,order_type,order_date ,\n");
			sqlIns.append(" order_time,customer_id,customer_name,ship_to_site_use_id,\n");
			sqlIns.append(" bill_to_site_use_id,payment_term,salesrep_id,pricelist_id ,\n");
			sqlIns.append(" vat_code,vat_rate,payment_method,shipping_day, \n");
			sqlIns.append(" shipping_time,total_amount,vat_amount,net_amount,\n");
			sqlIns.append(" payment,sales_order_no,ar_invoice_no,doc_status,\n");
			sqlIns.append(" file_name,created_by,creation_date,order_source, \n");
			sqlIns.append(" oracle_bill_to_location_id,oracle_ship_to_location_id,organization_code,last_printed_date,\n");
			sqlIns.append(" cust_po_number,cash_flag ) \n");//34
			sqlIns.append(" values(?,?,?,?,?,?,?,?,  ?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,? ,?,?) \n");//34
			psIns = conn.prepareStatement(sqlIns.toString());
		
			ps = conn.prepareStatement(str);
			rs = ps.executeQuery();
			while(rs.next()){
				psIns.setBigDecimal(index++, headerId);
				psIns.setString(index++,Utils.isNull(rs.getString("ORDER_NUMBER")));
				psIns.setString(index++,Utils.isNull(rs.getString("order_type")));
				psIns.setDate(index++,rs.getDate("order_date"));
				psIns.setString(index++,null);//orderTime
				psIns.setInt(index++,rs.getInt("customer_id"));
				psIns.setString(index++,Utils.isNull(rs.getString("customer_name")));
				psIns.setInt(index++,rs.getInt("SHIP_TO_SITE_USE_ID"));
				psIns.setInt(index++,rs.getInt("BILL_TO_SITE_USE_ID"));
				psIns.setString(index++,Utils.isNull(rs.getString("PAYMENT_TERM")));
				psIns.setInt(index++,rs.getInt("SALESREP_ID"));
				psIns.setInt(index++,rs.getInt("PRICELIST_ID"));
				psIns.setString(index++,Utils.isNull(rs.getString("vat_code")));
				psIns.setString(index++,Utils.isNull(rs.getString("vat_rate")));
				psIns.setString(index++,Utils.isNull(rs.getString("payment_method")));
				psIns.setString(index++,Utils.isNull(rs.getString("shipping_day")));
				psIns.setString(index++,Utils.isNull(rs.getString("shipping_time")));
				psIns.setDouble(index++, rs.getDouble("total_amount"));
				psIns.setDouble(index++, rs.getDouble("vat_amount"));
				psIns.setDouble(index++, rs.getDouble("net_amount"));
				psIns.setString(index++,Utils.isNull(rs.getString("payment")));
				psIns.setString(index++,Utils.isNull(rs.getString("sales_order_no")));
				psIns.setString(index++,Utils.isNull(rs.getString("ar_invoice_no")));
				psIns.setString(index++,Utils.isNull(rs.getString("doc_status")));
				psIns.setString(index++,"");//fileName
				psIns.setInt(index++,rs.getInt("SALESREP_ID"));//createBy
				psIns.setTimestamp(index++, new java.sql.Timestamp(new Date().getTime()));
				psIns.setString(index++,"PENS SALES");
				psIns.setInt(index++,rs.getInt("ORA_BILL_ADDRESS_ID"));
				psIns.setInt(index++,rs.getInt("ORA_SHIP_ADDRESS_ID"));
				psIns.setString(index++,Utils.isNull(rs.getString("ORG")));
				psIns.setDate(index++,rs.getDate("print_datetime_pick"));
				psIns.setString(index++,Utils.isNull(rs.getString("po_number")));
				psIns.setString(index++,Utils.isNull(rs.getString("iscash")));
				
				psIns.addBatch();
			}
			psIns.executeBatch();
			return r;
		}catch(Exception e){
			throw e;
		}
	}
	
	public static boolean insertOrderDetailTemp(Connection conn,BigDecimal headerId,long orderId,String orderType) throws Exception{
		boolean r= true;
		String str = "";
		StringBuffer sqlIns = new StringBuffer("");
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		ResultSet rs = null;
		int index = 1;
		BigDecimal lineId = new BigDecimal("0");
		try{
			if("S".equalsIgnoreCase(orderType)){
			  str = "select h.ORDER_NO	AS	ORDER_NUMBER, \n"+
		            	"	d.LINE_NO	AS	LINE_NO,	\n"+
			            "	d.PRODUCT_ID	AS	PRODUCT_ID,	\n"+
			            "	d.UOM_ID	AS	UOM_ID,	\n"+
			            "	d.QTY	AS	QTY,	\n"+
			            "	d.PRICE	AS	PRICE,	\n"+
			            "	d.LINE_AMOUNT	AS	LINE_AMOUNT, \n"+
			            "	d.DISCOUNT	AS	DISCOUNT,	\n"+
			            "	d.TOTAL_AMOUNT	AS	TOTAL_AMOUNT, \n"+
			            "	(select max(value) from pensso.c_reference where code ='OrgID') AS ORG_ID,	\n"+
			            "	''	AS	WAREHOUSE,	\n"+
			            "	a.CODE	AS	SUBINVENTORY,	\n"+
			            "	d.REQUEST_DATE	AS	REQUEST_DATE,	\n"+
			            "	d.SHIPPING_DATE	AS	SHIPPING_DATE,	\n"+
			            "	d.PROMOTION	AS	PROMOTION,	\n"+
			            "	d.VAT_AMOUNT	AS	VAT_AMOUNT,	\n"+
			            "   d.ORDER_LINE_ID AS ORDER_LINE_ID, \n"+
			            "   d.ORG AS ORG , \n"+
			            "   d.SUB_INV AS SUB_INV,\n"+
			            "   h.USER_ID AS SALESREP_ID \n"+
			            "	FROM pensso.t_order_line d 	\n"+
			            "	inner join pensso.t_order h	\n"+
			            "	on d.ORDER_ID = h.ORDER_ID	\n"+
			            "	left outer join pensso.ad_user a	\n"+
			            "	on h.USER_ID = a.USER_ID	\n"+
			            "   WHERE d.order_id ="+orderId+
			            "   and h.doc_status IN('"+Order.STATUS_LOADING+"') \n"+
			            "   and d.promotion = 'N' \n";
			}else{
				//EDI
				
			}
			//Prepare sql insert
			sqlIns.append("insert into pensso.SO_ORDER_LINES ( \n");
			sqlIns.append(" header_id,line_id,line_number,product_id ,\n");
			sqlIns.append(" uom_code,ordered_quantity,price,line_amount,\n");
			sqlIns.append(" discount,total_amount,warehouse,subinventory ,\n");
			sqlIns.append(" schedule_ship_date,promise_date,promotion,created_by, \n");
			sqlIns.append(" creation_date,org_id,vat_amount,organization_code,\n");
			sqlIns.append(" subinventory_code ) \n");//21
			sqlIns.append(" values(?,?,?,?,?,?,?,?,?,? ,?,?,?,?,?,?,?,?,?,? ,?) \n");//21
			psIns = conn.prepareStatement(sqlIns.toString());
			
			ps = conn.prepareStatement(str);
			rs = ps.executeQuery();
			while(rs.next()){
				//gen Next seq
				lineId = SequenceProcessAll.getIns().getNextValueBySeq("pensso.so_order_lines_s.nextval");
				
				psIns.setBigDecimal(index++, headerId);
				psIns.setBigDecimal(index++, lineId);
				psIns.setInt(index++,rs.getInt("line_no"));
				psIns.setInt(index++,rs.getInt("product_id"));
				psIns.setString(index++,Utils.isNull(rs.getString("uom_id")));
				psIns.setDouble(index++, rs.getDouble("qty"));
				psIns.setDouble(index++, rs.getDouble("price"));
				psIns.setDouble(index++, rs.getDouble("line_amount"));
				psIns.setDouble(index++, rs.getDouble("discount"));
				psIns.setDouble(index++, rs.getDouble("total_amount"));
				psIns.setString(index++,Utils.isNull(rs.getString("warehouse")));
				psIns.setString(index++,Utils.isNull(rs.getString("SUBINVENTORY")));
				psIns.setDate(index++,rs.getDate("REQUEST_DATE"));
				psIns.setDate(index++,rs.getDate("SHIPPING_DATE"));
				psIns.setString(index++,Utils.isNull(rs.getString("promotion")));
				psIns.setInt(index++,rs.getInt("SALESREP_ID"));
				psIns.setTimestamp(index++, new java.sql.Timestamp(new Date().getTime()));
				psIns.setString(index++,Utils.isNull(rs.getString("ORG_ID")));
				psIns.setDouble(index++, rs.getDouble("vat_amount"));
				psIns.setString(index++,Utils.isNull(rs.getString("ORG")));
				psIns.setString(index++,Utils.isNull(rs.getString("SUB_INV")));
				
				psIns.addBatch();
				index=1;//reset loop 
			}
			psIns.executeBatch();
			return r;
		}catch(Exception e){
			throw e;
		}
	}
}
