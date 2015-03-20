package com.isecinc.pens.process.confirm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.OrderTransaction;
import com.isecinc.pens.exception.PrevTripNotConfirmReceiptException;
import com.isecinc.pens.exception.PrevTripNotConfirmShipmentException;
import com.isecinc.pens.inf.helper.SequenceHelper;
import com.isecinc.pens.inf.helper.Utils;

public class ControlOrderTransaction {

	protected static  Logger logger = Logger.getLogger("PENS");

	public static OrderTransaction processOrderTransaction(Connection conn,OrderTransaction m,boolean checkPrevConfirmReceipt) throws Exception{
		int i = 0;
		ControlOrderTransaction mo = new ControlOrderTransaction();
		try{
			
			/** Check Prev Trip Have Confirm Shipment **/
			if(m.getTripNo() > 1){
                boolean prevTripIsConfirmShipment = isPrevTripIsConfirmShipment(conn,m.getOrderId(),m.getTripNo());
				
				if(!prevTripIsConfirmShipment){
					//Error 
					throw new PrevTripNotConfirmShipmentException("Prev Trip not Confirm Shipment");
				}
			}
			
            /** Check prev trip is confirm receipt **/
			if(checkPrevConfirmReceipt){
				logger.debug("checkPrevConfirmReceipt");
				boolean prevTripIsConfirmReceipt = isPrevTripIsConfirmReceipt(conn,m.getOrderId(),m.getTripNo());
				
				if(!prevTripIsConfirmReceipt){
					//Error 
					throw new PrevTripNotConfirmReceiptException("Prev Trip not Confirm Receipt");
				}
			}
		    m.setDocStatus("SV");
			
			m.setTotalAmount(mo.getTotalAmount(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTripTotalAmount(mo.getTripTotalAmount(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTripActNeedBill(mo.getTripActNeedBill(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTotalShipmentAmount(mo.getTotalShipmentAmount(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTotalTaxinvoiceAmount(mo.getTotalTaxinvoiceAmount(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTotalReceiptAmount(mo.getTotalReceiptAmount(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTotalRemainAmount(mo.getTotalRemainAmountPrevTrip(conn, m.getOrderId(), m.getTripNo()));
			
			m.setTotalTripActNeedBill(mo.getPrevTotalTripActNeedBillAmount(conn, m.getOrderId(),m.getTripNo()) + m.getTripActNeedBill());
			
			/** total_remain_amount = total_amount - trip_act_need_bill **/
			if(m.getTripNo() ==1){
			   m.setTotalRemainAmount(m.getTotalAmount()-m.getTripActNeedBill());
			}else{
				if(m.getTotalRemainAmount() > 0){
			       m.setTotalRemainAmount(m.getTotalRemainAmount()- m.getTripActNeedBill());
				}
			}
			
			/** Step 1 Check Trip is Found **/
			boolean orderTripExist = mo.isOrderSummaryTripExist(conn ,m.getOrderId(),m.getTripNo());
			logger.debug("orderTripExist orderId["+m.getOrderId()+"] tripNo["+m.getTripNo()+"] paymentOnly["+m.isPaymentOnly()+"]="+orderTripExist);
			if(orderTripExist ==false){
				mo.insertOrderTransaction(conn,m);
			}else{
				if( !m.isPaymentOnly()){
					//update controlOrderTransaction
					mo.updateOrderTransaction(conn,m);
				}
			}
						
		}catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}finally{
		}
		return m;
	}
	
	public static OrderTransaction processOrderTransactionCaseReceipt(Connection conn,OrderTransaction m,boolean checkPrevConfirmReceipt) throws Exception{
		int i = 0;
		ControlOrderTransaction mo = new ControlOrderTransaction();
		try{
            /** Check prev trip is confirm receipt **/
			if(checkPrevConfirmReceipt){
				logger.debug("checkPrevConfirmReceipt");
				boolean confirmReceipt = isPrevTripIsConfirmReceipt(conn,m.getOrderId(),m.getTripNo());
				
				if(!confirmReceipt){
					//Error 
					throw new PrevTripNotConfirmReceiptException("Prev Trip not Confirm Receipt");
				}
			}
			
			logger.debug("confirmReceiptDate before:"+m.getConfirmReceiptDate());
			/** Select Order Transaction **/
			m = getOrderTransaction(conn, m);
			
			logger.debug("confirmReceiptDate after:"+m.getConfirmReceiptDate());
			
			m.setTotalShipmentAmount(mo.getTotalShipmentAmount(conn, m.getOrderId(), m.getTripNo()));

			m.setTotalTaxinvoiceAmount(mo.getTotalTaxinvoiceAmount(conn, m.getOrderId(), m.getTripNo()));

			m.setTotalReceiptAmount(mo.getTotalReceiptAmount(conn, m.getOrderId(), m.getTripNo()));

			m.setTotalTripActNeedBill(mo.getCurrentTotalTripActNeedBillAmount(conn, m.getOrderId(),m.getTripNo()));
			
			/** Step 1 Check Trip is Found **/
			boolean orderTripExist = mo.isOrderSummaryTripExist(conn ,m.getOrderId(),m.getTripNo());
			logger.debug("orderTripExist orderId["+m.getOrderId()+"] tripNo["+m.getTripNo()+"] paymentOnly["+m.isPaymentOnly()+"]="+orderTripExist);
			if(orderTripExist ==false){
				mo.insertOrderTransaction(conn,m);
			}else{
				mo.updateOrderTransaction(conn,m);
			}
						
		}catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}finally{
		}
		return m;
	}
	
	public OrderTransaction insertOrderTransaction(Connection conn ,OrderTransaction m) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO t_order_transaction(" +
		    " id ,"+
			" order_id," +
			" TRIP_NO," +
			" cf_ship_date ," +
			" TOTAL_AMOUNT ," +
			" TOTAL_REMAIN_AMOUNT  ," +
			" TRIP_TOTAL_AMOUNT ," +
			" TRIP_ACT_NEED_BILL ," +
			" TOTAL_SHIPMENT_AMOUNT ," +
			" TOTAL_TAXINVOICE_AMOUNT ," +
			" TOTAL_RECEIPT_AMOUNT ," +
			" CREATED ," +
			" CREATED_BY," +
			" SHIPPING_DATE ,TOTAL_TRIP_ACT_NEED_BILL,PAYMENT_ONLY,DOC_STATUS )" +
			" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			  
			logger.debug("SQL:"+sql);

			int index = 0;
			ps = conn.prepareStatement(sql);
			
			int id = SequenceHelper.getNextValue("t_order_transaction");
			
			ps.setInt(++index, id);
			ps.setInt(++index, m.getOrderId());
			ps.setInt(++index, m.getTripNo());
			ps.setTimestamp(++index, new java.sql.Timestamp((Utils.parse(m.getConfirmShipDate(), Utils.DD_MM_YYYY_WITH_SLASH, "th")).getTime()));
			ps.setDouble(++index, m.getTotalAmount());
			ps.setDouble(++index,m.getTotalRemainAmount());
			ps.setDouble(++index, m.getTripTotalAmount());
			ps.setDouble(++index, m.getTripActNeedBill());
			ps.setDouble(++index, m.getTotalShipmentAmount());
			ps.setDouble(++index, m.getTotalTaxinvoiceAmount());
			ps.setDouble(++index, m.getTotalReceiptAmount());
			ps.setDate(++index, new java.sql.Date(new Date().getTime()));
			ps.setInt(++index, m.getUserId());
			ps.setTimestamp(++index, new java.sql.Timestamp((Utils.parse(m.getShippingDate(), Utils.DD_MM_YYYY_WITH_SLASH, "th")).getTime()));
			ps.setDouble(++index, m.getTotalTripActNeedBill());
			ps.setString(++index, m.isPaymentOnly()?"Y":"");
			ps.setString(++index, m.getDocStatus());
			
			int ch = ps.executeUpdate();
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return null;
	}
	
	public OrderTransaction cancelOrderTransaction(Connection conn ,OrderTransaction m) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "\n update t_order_transaction " +
			"\n  set " +
			" DOC_STATUS = ? ," +
			" UPDATED = ? ," +
			" UPDATED_BY = ? " +
			"\n WHERE ORDER_ID = ? AND TRIP_NO =? ";
			  
			//logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			
			ps.setString(++index, m.getDocStatus());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setInt(++index, m.getUserId());
			ps.setInt(++index, m.getOrderId());
			ps.setInt(++index, m.getTripNo());
			
			int ch = ps.executeUpdate();
	
			//logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return null;
	}
	
	public OrderTransaction updateOrderTransaction(Connection conn ,OrderTransaction m) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "\n update t_order_transaction " +
			"\n  set " +
			" cf_receipt_date = ? ," +
			" total_taxinvoice_amount = ? ," +
			" total_receipt_amount = ? ," +
			
			//" receipt_amount = ? ," +
			//" remain_shipment_amount = ? ," + // shipment - receipt_amount 
			//" remain_taxinvoice_amount = ? ," + //taxinvoce - receipt_amount
			
			" UPDATED = ? ," +
			" UPDATED_BY = ? " +
			"\n WHERE ORDER_ID = ? AND TRIP_NO =? ";
			  
			//logger.debug("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			
			if( !Utils.isNull(m.getConfirmReceiptDate()).equals("")){
			   logger.debug("ConfirmReceiptDate:"+m.getConfirmReceiptDate());
				
			   ps.setTimestamp(++index, new java.sql.Timestamp((Utils.parse(m.getConfirmReceiptDate(), Utils.DD_MM_YYYY_WITH_SLASH, "th")).getTime()));
			}else{
			   ps.setTimestamp(++index,null);
			}
			ps.setDouble(++index, m.getTotalTaxinvoiceAmount());
			ps.setDouble(++index, m.getTotalReceiptAmount());
			
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setInt(++index, m.getUserId());
			ps.setInt(++index, m.getOrderId());
			ps.setInt(++index, m.getTripNo());
			
			int ch = ps.executeUpdate();
	
			//logger.debug("ins:"+ch);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return null;
	}
	
	public static OrderTransaction getOrderTransaction(Connection conn, OrderTransaction m) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_order_transaction where doc_status ='SV' and order_id ="+m.getOrderId()+" and trip_no ="+(m.getTripNo())+"\n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){

				m.setOrderId(rs.getInt("order_id"));
				m.setTripNo(rs.getInt("trip_no"));
				if(rs.getDate("shipping_date") != null){
				  m.setShippingDate(Utils.stringValue(rs.getDate("shipping_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				}
				
				if(rs.getDate("cf_ship_date") != null){
				  m.setConfirmShipDate(Utils.stringValue(rs.getDate("cf_ship_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				}
				
				if(rs.getDate("cf_receipt_date") != null){
				  m.setConfirmReceiptDate(Utils.stringValue(rs.getDate("cf_receipt_date"), Utils.DD_MM_YYYY_WITH_SLASH, Utils.local_th));
				}
				
				m.setTotalAmount(rs.getDouble("TOTAL_AMOUNT"));
				m.setTotalRemainAmount(rs.getDouble("TOTAL_REMAIN_AMOUNT"));
				m.setTotalTripActNeedBill(rs.getDouble("TOTAL_TRIP_ACT_NEED_BILL"));
				m.setTripTotalAmount(rs.getDouble("TRIP_TOTAL_AMOUNT"));
				m.setTripActNeedBill(rs.getDouble("TRIP_ACT_NEED_BILL"));
				m.setTotalShipmentAmount(rs.getDouble("TOTAL_SHIPMENT_AMOUNT"));
				m.setTotalTaxinvoiceAmount(rs.getDouble("TOTAL_TAXINVOICE_AMOUNT"));
				m.setTotalReceiptAmount(rs.getDouble("TOTAL_TAXINVOICE_AMOUNT"));
				m.setPaymentOnly(Utils.isNull(rs.getString("PAYMENT_ONLY")).equals("Y")?true:false);
				m.setDocStatus(rs.getString("DOC_STATUS"));
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return m;
	}
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @param tripNo
	 * @return
	 * @throws Exception
	 */
	public static boolean isPrevTripIsConfirmReceipt(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean confirm = true;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT cf_receipt_date ,trip_act_need_bill,total_taxinvoice_amount,total_receipt_amount from t_order_transaction where doc_status='SV' and order_id ="+orderId+" and trip_no ="+(tripNo-1)+"\n");
			
		    logger.debug("isPrevTripIsConfirmReceipt:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				Date cfReceiptDate = rs.getDate("cf_receipt_date");
				if(cfReceiptDate != null){
					confirm = true;
				}else{
					//logger.debug("trip_act_need_bill["+rs.getDouble("trip_act_need_bill") +"] total_receipt_amount["+rs.getDouble("total_receipt_amount")+"]");
					//ยอดที่ออก receipt less than tax  no gen Receipt
					if(rs.getDouble("trip_act_need_bill") > 0 && rs.getDouble("total_receipt_amount") < rs.getDouble("total_taxinvoice_amount") ){ //มีการจ่่ายตังในงวดทีแล้ว แหละมียอด receipt ==0 แสดงว่ายังไม่กด ยืนยัน Receipt 
					  confirm = false;	
					}
				}
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return confirm;
	}
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @param tripNo
	 * @return
	 * @throws Exception
	 */
	public static boolean isPrevTripIsConfirmShipment(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean confirm = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT 'x' as confirm from t_order_transaction where doc_status='SV' and order_id ="+orderId+" and trip_no ="+(tripNo-1)+"\n");
			
		    logger.debug("isPrevTripIsConfirmShipment:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				if("x".equalsIgnoreCase(rs.getString("confirm"))){
					confirm = true;
				}
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return confirm;
	}
	
	public boolean isOrderSummaryTripExist(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean exist = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT 'x' as f from t_order_transaction where doc_status='SV' and order_id ="+orderId+" and trip_no ="+tripNo+"\n");
			
		    //logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
			   exist = true;
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return exist;
	}
	
	public double getTotalRemainAmountPrevTrip(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT sum(coalesce(total_remain_amount,0)) as amt FROM t_order_transaction WHERE doc_status='SV' and order_id = "+orderId+"  and trip_no ="+(tripNo-1)+" \n");
		   // logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getTotalAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT sum(coalesce(need_bill,0)) as amt FROM t_order_line WHERE order_id = "+orderId+"  and iscancel <> 'Y' \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getTripTotalAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select sum(coalesce(total_amount,0)) as amt from t_order_line where order_id="+orderId+" and trip_no ="+tripNo+" and iscancel <> 'Y' group by order_id,trip_no \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getTripActNeedBill(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select sum(coalesce(act_need_bill,0)) as amt from t_order_line where order_id="+orderId+" and trip_no ="+tripNo+" and iscancel <> 'Y' group by order_id,trip_no \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getCurrentTotalTripActNeedBillAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select coalesce(total_trip_act_need_bill,0) as amt from t_order_transaction where doc_status='SV' and order_id="+orderId+" and trip_no="+tripNo+"  group by order_id \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getPrevTotalTripActNeedBillAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select coalesce(total_trip_act_need_bill,0) as amt from t_order_transaction where doc_status='SV' and order_id="+orderId+" and trip_no="+(tripNo-1)+" group by order_id \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	
	
	public double getTotalShipmentAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select sum(coalesce(total_amount,0)) as amt from t_shipment where shipment_status ='SV' and order_id="+orderId+" group by order_id \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getTotalTaxinvoiceAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select sum(coalesce(total_amount,0)) as amt from t_taxinvoice where taxinvoice_status ='SV' and order_id="+orderId+" group by order_id \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}
	
	public double getTotalReceiptAmount(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double amt = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select sum(coalesce(paid_amount,0)) as amt from t_receipt h ,t_receipt_line l " +
					"where h.receipt_id = l.receipt_id and l.order_id="+orderId+" and h.doc_status='SV' group by l.order_id \n");
		    //logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				amt =rs.getDouble("amt");
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return amt;
	}

}
