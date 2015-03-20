package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.OrderTransaction;
import com.isecinc.pens.bean.Shipment;
import com.isecinc.pens.bean.ShipmentConfirm;
import com.isecinc.pens.bean.ShipmentLine;
import com.isecinc.pens.bean.ShipmentSummary;
import com.isecinc.pens.bean.TaxInvoice;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.exception.PrevTripNotConfirmReceiptException;
import com.isecinc.pens.exception.PrevTripNotConfirmShipmentException;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.process.confirm.ConfirmGenerateTaxInvoice;
import com.isecinc.pens.process.confirm.ControlOrderTransaction;

public class MShipmentConfirm  {
	public static Logger logger = Logger.getLogger("PENS");
	
	private MShipmentConfirm() throws Exception{
	}
	
	public static MShipmentConfirm getInstance() throws Exception{
		MShipmentConfirm shipment = new MShipmentConfirm();
		return shipment;
	}

	public ShipmentConfirm getShipmentList(String memberCode , String shippingDate,String isShipped ,boolean searchCancel) throws Exception
	{
		Connection conn = null;
		PreparedStatement ppstmt = null;
		ResultSet rset = null;
		ShipmentConfirm shipmentConfirm = new ShipmentConfirm();
		List<ShipmentConfirm> shipL = new ArrayList<ShipmentConfirm>();
		StringBuffer sql = new StringBuffer("");
		       sql.append("select a.* from( \n")
				    .append("SELECT '2' as o,ol.* ,od.order_no , cus.name as FIRST_NAME ,cus.name2 as LAST_NAME , cus.code as memberCode ,cus.delivery_group \n")
					.append(" FROM t_order_line ol , t_order od , m_customer cus where ol.order_id = od.order_id \n")
					.append(" and cus.customer_id = od.customer_id \n")
					.append(" and cus.code = coalesce(?,cus.code) and ol.shipping_date = coalesce( ?,ol.shipping_date) \n ")
					.append(" and ol.iscancel='N' and coalesce(ol.shipment,'N') = ? and ol.exported='N' and ol.qty > 0 \n ")
		            .append(" and ol.product_id <> 47036 \n");//Product 99999
		           if(searchCancel){
	                   sql.append(" and ( ol.cf_ship_date is not null    \n");
	                   sql.append("     and (   (ol.cf_receipt_date is not null and prepay ='Y')  OR (ol.cf_receipt_date is null) ) \n");
	                   sql.append(" ) \n");
					}else{
					   sql.append(" and ol.cf_ship_date is null  \n");
					}
					//Test
					//.append(" and cus.code in('51','8335') \n")
					
		            sql.append(" union \n")
		            /** Product 99999 Set for payment**/
		            .append(" SELECT  '1' as o, ol.*, od.order_no, cus.name as FIRST_NAME ,cus.name2 as LAST_NAME , cus.code as memberCode ,cus.delivery_group \n")
					.append(" FROM t_order_line ol , t_order od , m_customer cus where ol.order_id = od.order_id \n")
					.append(" and cus.customer_id = od.customer_id \n")
					.append(" and cus.code = coalesce(?,cus.code) and ol.shipping_date = coalesce( ?,ol.shipping_date)\n ")
					.append(" and ol.iscancel='N' and coalesce(ol.shipment,'N') = ? and ol.exported='N' \n ")
					.append(" and ol.product_id = 47036 \n");///Product 99999 payment only
					if(searchCancel){
					   sql.append(" and ( ol.cf_ship_date is not null    \n");
		               sql.append("     and (   (ol.cf_receipt_date is not null and prepay ='Y')  OR (ol.cf_receipt_date is null) ) \n");
		               sql.append(" ) \n");
					}else{
					   sql.append(" and ol.cf_ship_date is null  \n");
					}
					sql.append(" ) a order by a.o,convert(a.memberCode,SIGNED) , a.order_no , a.trip_no , a.product_id ");
		
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			ppstmt = conn.prepareStatement(sql.toString());
			
			ppstmt.setString(1, StringUtils.isEmpty(memberCode)?null:memberCode);
			ppstmt.setTimestamp(2, StringUtils.isEmpty(shippingDate)?null:DateToolsUtil.convertToTimeStamp(shippingDate));
			ppstmt.setString(3, isShipped);
			ppstmt.setString(4, StringUtils.isEmpty(memberCode)?null:memberCode);
			ppstmt.setTimestamp(5, StringUtils.isEmpty(shippingDate)?null:DateToolsUtil.convertToTimeStamp(shippingDate));
			ppstmt.setString(6, isShipped);
		
			logger.debug("SQL "+sql.toString());
			logger.debug("Parameter [ member code => "+memberCode+" , shipment date => "+shippingDate+" ]");
			
			rset = ppstmt.executeQuery();
			
			int no = 0;
			int noTemp = 0;
			String tempOrder = "";
			String classLine = "";
			
			while(rset.next()){
				ShipmentConfirm shipment = new ShipmentConfirm(rset);
				
				if( !tempOrder.equals(shipment.getOrderNo())){ 
					tempOrder= shipment.getOrderNo();
					no++;
				}
				if(no%2==0){
					classLine ="lineE";
				}else{
					classLine = "lineO";
				}
				shipment.setClassLine(classLine);
				
				if(no != noTemp){
				  shipment.setNo(no+"");
				}
				noTemp = no;
				//Find OrderLine
				shipment.setOrderLine(new OrderLine(rset));
				
				shipL.add(shipment);
			}
			logger.info("Query Result Size :"+shipL.size());
			
			shipmentConfirm.setTotalGroup(no);
			shipmentConfirm.setShipmentConfirmList(shipL);
			
		}catch(Exception ex){
			throw ex;
		}finally{
		  if(rset !=null)
			 rset.close();
		  if( ppstmt != null){
		     ppstmt.close();
		  }
		  if(conn != null){
		     conn.close();
		  }
		}
		return shipmentConfirm;
	}
    
	
	public ShipmentSummary confirmShipments(List<ShipmentConfirm> confirmList,String confirmDate,User user) throws Exception {
		ShipmentSummary summary = new ShipmentSummary();
		int no_of_confirm = 0;
		int no_of_updated = 0;
		int no_of_updatedReqDate = 0;
		int no_of_reScheduleRecord = 0;
		
		boolean checkPrevTripConfirmReceipt = true;//For check in process OrderTranasction
		Map<String, OrderTransaction> rescheduleNoDupMap = new HashMap<String, OrderTransaction>();
		
		/** Update SQL to Update Order Line Information
		 *  1 Statement for Confirm Shipment That Payment Term Is Not Cash 
		 *  2 Statement for Confirm Shipment That Payment Term Is Cash
		 *  3 Statement for Postpone Shipment 
		 *  */
		StringBuffer updateSqlCR = new StringBuffer("update t_order_line \n");
		updateSqlCR.append(" set shipment_comment = ?, cf_ship_date = ? \n")
				 .append("  , shipment = 'Y' , actual_qty = ? ,  act_need_bill = ? \n")
				 .append(" where order_line_id = ? ");
		
		StringBuffer updateSqlCH = new StringBuffer("update t_order_line \n");
		updateSqlCH.append(" set shipment_comment = ?, cf_ship_date = ? \n")
				 .append("  , shipment = 'Y' , actual_qty = ? ,  need_bill = ? \n")
				 .append(" where order_line_id = ? ");
		
		StringBuffer updateSqlCS = new StringBuffer("update t_order_line \n");
		updateSqlCS.append(" set shipment_comment = ?, cf_ship_date = ? \n")
				 .append("  , shipment = 'Y' , actual_qty = ? ,  act_need_bill = ? \n")
				 .append(" where order_line_id = ? ");
		
		StringBuffer postponeDateSql = new StringBuffer("update t_order_line \n");
		postponeDateSql.append(" set shipment_comment = ?, need_bill = ? \n")
				 .append("  , shipping_date = ? \n")
				 .append("  , request_date = COALESCE(?,request_date) \n")
				 .append(" where order_line_id = ? ");
		
		Connection conn = null;
		Connection connUpdate = null;
		PreparedStatement ppstmtCR = null;
		PreparedStatement ppstmtCH = null;
		PreparedStatement ppstmtCS = null;
		PreparedStatement ppstmt3 = null;
		try{
			connUpdate = new DBCPConnectionProvider().getConnection(connUpdate);
			ppstmt3 = connUpdate.prepareStatement(postponeDateSql.toString());
			
			MOrderLine orderLineService = new MOrderLine();
			MShipment shipmentService = new MShipment();
			MShipmentLine shipLineService = new MShipmentLine();
			
			String orderNo = "";
			int tripNo = -1; 
			
			Shipment shipment = null;
			BigDecimal headTotalAmt = BigDecimal.ZERO;
			BigDecimal headLineAmt = BigDecimal.ZERO;
			
			List<OrderTransaction> orderTransList = new ArrayList<OrderTransaction>();
			List<ShipmentLine> shipmentLineList = new ArrayList<ShipmentLine>();
			int g = 0;
			for(ShipmentConfirm confirm : confirmList){
				
				if("Y".equals(confirm.getIsConfirm())){

					OrderLine orderLine = orderLineService.find(""+confirm.getLineId());
					String pCode = orderLine.getProduct().getCode();
					
					BigDecimal confirmQty = BigDecimal.valueOf(confirm.getConfirmQty());
					BigDecimal qty = BigDecimal.valueOf(orderLine.getQty());
					// Line Amount After VAT
					BigDecimal totalLineAmt = BigDecimal.valueOf(orderLine.getTotalAmount());
					// Line Amount Before VAT 
					BigDecimal lineAmt = BigDecimal.valueOf(orderLine.getLineAmount());
					
					BigDecimal unitPrice = BigDecimal.ZERO;
					// Unit Price Included VAT
					try{
						if(qty.compareTo(BigDecimal.ZERO) != 0){
						  unitPrice = totalLineAmt.divide(qty, 2, BigDecimal.ROUND_HALF_UP);
						}
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
					
					if("Y".equals(orderLine.getPromotion1())){
						totalLineAmt = BigDecimal.ZERO;
						lineAmt = BigDecimal.ZERO;
					}
					
					if(confirmQty.compareTo(qty) != 0){
						// Recalculate Line
						totalLineAmt = unitPrice.multiply(confirmQty);
						lineAmt = totalLineAmt.multiply(BigDecimal.valueOf(100d)).divide(BigDecimal.valueOf(107d),5,BigDecimal.ROUND_HALF_UP);
					}
					
					//  New Order ,trip
					if(!orderNo.equals(confirm.getOrderNo()) 
						|| (orderNo.equals(confirm.getOrderNo()) && orderLine.getTripNo() != tripNo)){
						
						logger.debug("orderNo["+orderNo+"]tripNo["+tripNo+"] new Shipment new shipmentLineList");
						
						shipment = new Shipment();
						shipment.setOrderId(orderLine.getOrderId());
						shipment.setShipmentDate(orderLine.getShippingDate());
						shipment.setShipmentStats("SV");
						shipment.setTotalAmt(headTotalAmt.doubleValue());
						shipment.setLineAmt(headLineAmt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
						shipment.setVatAmt((headTotalAmt.setScale(2,2).subtract(headLineAmt.setScale(2,BigDecimal.ROUND_HALF_UP))).doubleValue());
						
						/** Set ShipmentLineList to prev List **/
						
						if(g > 0){
							logger.debug("update Prev index in List");
							OrderTransaction m = orderTransList.get(g-1);
							Shipment s = m.getShipment();
							s.setTotalAmt(headTotalAmt.doubleValue());
							s.setLineAmt(headLineAmt.doubleValue());
							s.setVatAmt((headTotalAmt.setScale(2,2).subtract(headLineAmt.setScale(2,BigDecimal.ROUND_HALF_UP))).doubleValue());
							s.setShipmentLineList(shipmentLineList);
							m.setShipment(s);
							
							/** Set to Prev index **/
							orderTransList.set(g-1, m);
						}
						
						//For next step Order OrderTransaction
						OrderTransaction m = new OrderTransaction();
						m.setOrderId(orderLine.getOrderId());
						m.setTripNo(orderLine.getTripNo());
						m.setShippingDate(orderLine.getShippingDate());
						m.setConfirmShipDate(confirmDate);
						m.setUserId(user.getId());
                        m.setOrderNo(confirm.getOrderNo());
                        m.setCustomerName(confirm.getMemberName());
                        
						logger.debug("PCode:"+pCode);
						if("999999".equals(pCode)){
							logger.debug("99999 PCode:"+pCode);
							m.setPaymentOnly(true);
						}
						m.setShipment(shipment);
						
						
						headTotalAmt = BigDecimal.ZERO;
						headLineAmt = BigDecimal.ZERO;
						shipmentLineList = new ArrayList<ShipmentLine>();
						
						orderTransList.add(m);
					    g++;
					}
					
					/** Save Line **/
					ShipmentLine shipmentLine = null;
					
					shipmentLine = new ShipmentLine(shipment);
					shipmentLine.setLinesAmt(lineAmt.doubleValue());
					shipmentLine.setTotalAmt(totalLineAmt.doubleValue());
					shipmentLine.setVatAmt();
					shipmentLine.setQty(confirmQty.intValue());
					shipmentLine.setOrderLineId(orderLine.getId());
					shipmentLine.setProductId(orderLine.getProduct().getId());
					shipmentLine.setUomId(orderLine.getUom().getId());
					
					shipmentLine.setConfirmLine(confirm);
					
					headTotalAmt = headTotalAmt.add(totalLineAmt);
					headLineAmt = headLineAmt.add(lineAmt);
					
					//add to list
					shipmentLineList.add(shipmentLine);
					
					orderNo = confirm.getOrderNo();
					tripNo = orderLine.getTripNo();
				    
					
			    //shif date
				}else if("Y".equals(confirm.getIsPostponeShipDate()) || "Y".equals(confirm.getIsPostponeReqDate())){
					
					ppstmt3.setString(1, StringUtils.isEmpty(confirm.getComment())?null:confirm.getComment());
					ppstmt3.setDouble(2, confirm.getOrderLine().getNeedBill());
					ppstmt3.setTimestamp(3, StringUtils.isEmpty(confirmDate)?null:DateToolsUtil.convertToTimeStamp(confirm.getPostponeDate()));
					if("Y".equals(confirm.getIsPostponeReqDate())){//เลื่อนแผนส่ง
						ppstmt3.setTimestamp(4, StringUtils.isEmpty(confirmDate)?null:DateToolsUtil.convertToTimeStamp(confirm.getPostponeDate()));
					}else{
						ppstmt3.setTimestamp(4, null);
					}
					ppstmt3.setInt(5, confirm.getLineId());
					
					if("Y".equals(confirm.getIsPostponeReqDate())){
						no_of_updatedReqDate +=ppstmt3.executeUpdate();
					}else{
						no_of_updated +=ppstmt3.executeUpdate();
					}
					
				//Replan
				}else if("Y".equals(confirm.getIsReSchedule())){
					Timestamp newDate = StringUtils.isEmpty(confirm.getPostponeDate())?null:DateToolsUtil.convertToTimeStamp(confirm.getPostponeDate());
					int orderLineId = confirm.getLineId();
					
					no_of_reScheduleRecord = no_of_reScheduleRecord+ new MOrder().reSchedule(orderLineId, newDate,connUpdate,user.getId());
					
					//summary
					OrderTransaction mm = new OrderTransaction();
					mm.setOrderNo(confirm.getOrderNo());
					mm.setTripNo(confirm.getTripNo());
					mm.setCustomerName(confirm.getMemberName());
					
					if(rescheduleNoDupMap.get(mm.getOrderNo()+"-"+mm.getTripNo()) == null){
					  summary.addRescheduleShipDate(mm);
			     	}
					/** Check Dup By OrderNo ,TripNo */
					rescheduleNoDupMap.put(mm.getOrderNo()+"-"+mm.getTripNo(), mm);
				}
				
			}//for
			
			logger.debug("shipmentLineList size:"+shipmentLineList.size());
			
			if(orderTransList != null && orderTransList.size() > 0){
				OrderTransaction m = orderTransList.get(orderTransList.size()-1);
				Shipment s = m.getShipment();
				s.setTotalAmt(headTotalAmt.doubleValue());
				s.setLineAmt(headLineAmt.doubleValue());
				s.setVatAmt((headTotalAmt.setScale(2,2).subtract(headLineAmt.setScale(2,BigDecimal.ROUND_HALF_UP))).doubleValue());
				s.setShipmentLineList(shipmentLineList);
				m.setShipment(s);
				
				/** Set to Prev index **/
				orderTransList.set(orderTransList.size()-1, m);
			}

			logger.debug("orderTransList["+orderTransList.size()+"]");
			
			/** Process GenShipment and taxInvoice **/
			if(orderTransList != null && orderTransList.size() > 0){
				//conn = new DBCPConnectionProvider().getConnection(null);
				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				ppstmtCR = conn.prepareStatement(updateSqlCR.toString());
				ppstmtCH = conn.prepareStatement(updateSqlCH.toString());
				ppstmtCS = conn.prepareStatement(updateSqlCS.toString());
				
				for(int i=0;i<orderTransList.size();i++){
					OrderTransaction m = null;
					try{
						logger.debug("*********************************************************************************************************");
						m = (OrderTransaction)orderTransList.get(i);
					    logger.debug("*** Start save orderId["+m.getOrderId()+"] tripNo["+m.getTripNo()+"] payonly["+m.isPaymentOnly()+"]");
						   
						/** Gen shipment **/
						Shipment shipSave = m.getShipment();
						if( !m.isPaymentOnly()){ 
						   shipmentService.save(shipSave, user.getId(), conn);
						}
						logger.debug("shipmentNo["+shipSave.getShipmentNo()+"] getShipmentLineList["+shipSave.getShipmentLineList().size());
						
						/** Gen shipment line **/
						if(shipSave.getShipmentLineList() != null && shipSave.getShipmentLineList().size() >0){
							for(int n=0;n<shipSave.getShipmentLineList().size();n++){
								ShipmentLine shipLine = (ShipmentLine)shipSave.getShipmentLineList().get(n);
								shipLine.setShipmentId(shipSave.getId());
								if( !m.isPaymentOnly()){ 
							       shipLineService.save(shipLine, user.getId(), conn);
								}
								
							    /** Update t_order_line flag **/
								if("CR".equals(shipLine.getConfirmLine().getOrderLine().getPaymentMethod())){
									logger.debug("update ppstmt CR (Credit)");
									ppstmtCR.setString(1, StringUtils.isEmpty(shipLine.getConfirmLine().getComment())?null:shipLine.getConfirmLine().getComment());
									ppstmtCR.setTimestamp(2, StringUtils.isEmpty(confirmDate)?null:DateToolsUtil.convertToTimeStamp(confirmDate) );
									ppstmtCR.setInt(3, shipLine.getConfirmLine().getConfirmQty());
									ppstmtCR.setDouble(4, shipLine.getConfirmLine().getOrderLine().getNeedBill());
									ppstmtCR.setInt(5, shipLine.getConfirmLine().getLineId());
									
									no_of_confirm++;
									ppstmtCR.execute();
									
								}else if("CH".equals(shipLine.getConfirmLine().getOrderLine().getPaymentMethod())){
									
									logger.debug("update ppstmt CH(Cheque)");
									ppstmtCH.setString(1, StringUtils.isEmpty(shipLine.getConfirmLine().getComment())?null:shipLine.getConfirmLine().getComment());
									ppstmtCH.setTimestamp(2, StringUtils.isEmpty(confirmDate)?null:DateToolsUtil.convertToTimeStamp(confirmDate) );
									ppstmtCH.setInt(3, shipLine.getConfirmLine().getConfirmQty());
									ppstmtCH.setDouble(4, shipLine.getConfirmLine().getOrderLine().getNeedBill());
									ppstmtCH.setInt(5, shipLine.getConfirmLine().getLineId());
									
									no_of_confirm++;
									ppstmtCH.execute();
								}else{
									logger.debug("update ppstmt2 CS");
									ppstmtCS.setString(1, StringUtils.isEmpty(shipLine.getConfirmLine().getComment())?null:shipLine.getConfirmLine().getComment());
									ppstmtCS.setTimestamp(2, StringUtils.isEmpty(confirmDate)?null:DateToolsUtil.convertToTimeStamp(confirmDate) );
									ppstmtCS.setInt(3, shipLine.getConfirmLine().getConfirmQty());
									ppstmtCS.setDouble(4, shipLine.getConfirmLine().getOrderLine().getNeedBill());
									ppstmtCS.setInt(5, shipLine.getConfirmLine().getLineId());
									
									no_of_confirm++;
									ppstmtCS.execute();
								}
							}//for 2
						}//if
						
						//Re Set shipment to shipmentId
						m.setShipment(shipSave);
						
						/** Gen Order Transaction **/
						ControlOrderTransaction.processOrderTransaction(conn, m,checkPrevTripConfirmReceipt);
						
						/** Gen Tax Invoice **/
						ConfirmGenerateTaxInvoice.processGenerateTaxInvoice(conn, m);
	
						/** Update Order Transaction **/
						ControlOrderTransaction.processOrderTransaction(conn, m,false);
						
						logger.debug("Conn commit");
						conn.commit();
						
						//Add to display Success 
						summary.addSucess(m);
						logger.debug("*********************************************************************************************************");
						
					   }catch(PrevTripNotConfirmShipmentException e){
						   e.printStackTrace();
							logger.debug("Conn Rollback");
							summary.addFailOrder(m, "ข้อมูลการส่งครั้งก่อนหน้านี้ ยังไม่ยืนยันการรับสินค้า");
							conn.rollback();
					   }catch(PrevTripNotConfirmReceiptException e){
						   e.printStackTrace();
							logger.debug("Conn Rollback");
							summary.addFailOrder(m, "ข้อมูลการส่งครั้งก่อนหน้านี้ ยังไม่ยืนยันการรับเงินจากลูกค้า");
							conn.rollback();
				
					   }catch(Exception e){
						   e.printStackTrace();
							logger.debug("Conn Rollback");
							summary.addFailOrder(m, e.getMessage());
							conn.rollback();
					  }
				}//for
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}finally{
			if(ppstmtCS != null ){
				ppstmtCS.close();
				ppstmtCS = null;
			}
			if(ppstmtCR != null){
				ppstmtCR.close();
				ppstmtCR = null;
			}
			if(ppstmtCH != null){
				ppstmtCH.close();
				ppstmtCH = null;
			}
			if(ppstmt3 != null){
			   ppstmt3.close();
			   ppstmt3 =null;
			}
			if(conn != null){
			   conn.close();
			   conn = null;
			}
			if(connUpdate != null){
		      connUpdate.close();
		      connUpdate = null;
			}
		}
		
		summary.setNo_of_confirm(no_of_confirm);
		summary.setNo_of_updated(no_of_updatedReqDate);
		summary.setNo_of_updatedReqDate(no_of_updatedReqDate);
		summary.setNo_of_reScheduleRecord(no_of_reScheduleRecord);

		return summary;
	}
	
	/**
	 * 
	 * @param confirmList
	 * @param confirmDate
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public ShipmentSummary cancelConfirmShipments(List<ShipmentConfirm> confirmList,User user) throws Exception {
		ShipmentSummary summary = new ShipmentSummary();
		MShipment shipmentService = new MShipment();
		MTaxInvoice taxService = new MTaxInvoice();
		MOrderLine orderLineService = new MOrderLine();
		Connection conn = null;
		PreparedStatement ppstmt = null;
		StringBuffer updateSql = new StringBuffer("UPDATE t_order_line SET CF_SHIP_DATE = null ,act_need_bill = null, shipment = 'N' , actual_qty = null , shipment_comment = null WHERE order_id = ? and trip_no = ? ");
		OrderTransaction m = null;
		try{
			String orderNo = "";
			int tripNo = -1; 
			Shipment shipment = null;
			conn = new DBCPConnectionProvider().getConnection(conn);
 			ppstmt = conn.prepareStatement(updateSql.toString());
 			
			for(ShipmentConfirm confirm : confirmList){
				
				if("Y".equals(confirm.getIsCancel())){

					OrderLine orderLine = orderLineService.find(""+confirm.getLineId());
					String pCode = orderLine.getProduct().getCode();
					
					//  New Order ,trip
					if(!orderNo.equals(confirm.getOrderNo()) 
						|| (orderNo.equals(confirm.getOrderNo()) && orderLine.getTripNo() != tripNo)){
						
						logger.debug("orderNo["+orderNo+"]tripNo["+tripNo+"] new Shipment new shipmentLineList");
						
						shipment = new Shipment();
						shipment.setOrderId(orderLine.getOrderId());
						shipment.setShipmentDate(orderLine.getShippingDate());
						shipment.setShipmentStats("VO");
						
                        /** Update t_order_line flag **/
        				ppstmt.setInt(1, orderLine.getOrderId());
        				ppstmt.setInt(2, orderLine.getTripNo());
        			    ppstmt.executeUpdate();
        			   
        			    /** update shipment status **/
        				shipment = shipmentService.findShipmentByOrderLineId(orderLine.getId(), conn);
        				if(shipment != null){
        					shipment.setShipmentStats("VO");
        					shipmentService.save(shipment,user.getId() , conn);
        					
        					 /** update taxinvoice status **/
        					if(shipment.getTaxInvoiceId() > 0){
        						TaxInvoice taxinvoice = taxService.getTaxInvoice(shipment.getTaxInvoiceId(), conn);
        						if(taxinvoice != null){
        							taxinvoice.setTaxInvoiceStatus("VO");
        							taxService.save(taxinvoice, true, user.getId(), conn);
        						}
        					}
        				}
        				
        				/** Update Order Transaction  **/
        				m = new OrderTransaction();
						m.setOrderId(orderLine.getOrderId());
						m.setTripNo(orderLine.getTripNo());
                        m.setDocStatus("VO");
                        m.setOrderNo(confirm.getOrderNo());
                        m.setCustomerName(confirm.getMemberName());
                        if("999999".equals(pCode)){
							logger.debug("99999 PCode:"+pCode);
							m.setPaymentOnly(true);
						}
                        
                        new ControlOrderTransaction().cancelOrderTransaction(conn, m);
                        
                        summary.addSucess(m);
                        
					}//if group
					
					orderNo = confirm.getOrderNo();
					tripNo = orderLine.getTripNo();
				}//if
				
			}//for
			
		}catch(Exception ex){
			summary.addFailOrder(m, ex.getMessage());
			throw ex;
		}finally{
			if(ppstmt != null){
				ppstmt.close();ppstmt=null;
			}
			if(conn != null){
			  conn.close();conn = null;
			}
		}
		return summary;
	}
	
	public String cancelLines(List<Integer> lineIdL,User user) throws Exception{
		String ret = "";
		int records = 0;
		
		MShipment shipmentService = new MShipment();
		MTaxInvoice taxService = new MTaxInvoice();
		Connection conn = null;
		PreparedStatement ppstmt = null;
		StringBuffer updateSql = new StringBuffer("UPDATE t_order_line SET CF_SHIP_DATE = null ,act_need_bill = null, shipment = 'N' , actual_qty = null , shipment_comment = null WHERE order_id = ? and trip_no = ? ");
		logger.debug(updateSql);
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			ppstmt = conn.prepareStatement(updateSql.toString());
			for(int id :lineIdL ){
				OrderLine line = new MOrderLine().find(""+id);
				
				ppstmt.setInt(1, line.getOrderId());
				ppstmt.setInt(2, line.getTripNo());
				records += ppstmt.executeUpdate();
				
				Shipment shipment = shipmentService.findShipmentByOrderLineId(id, conn);
				if(shipment != null){
					shipment.setShipmentStats("VO");
					shipmentService.save(shipment,user.getId() , conn);
					
					if(shipment.getTaxInvoiceId() > 0){
						TaxInvoice taxinvoice = taxService.getTaxInvoice(shipment.getTaxInvoiceId(), conn);
						if(taxinvoice != null){
							taxinvoice.setTaxInvoiceStatus("VO");
							taxService.save(taxinvoice, true, user.getId(), conn);
						}
					}
				}
			}
		}catch(Exception ex){
			
			throw ex;
		}finally{
			if(ppstmt != null){
			   ppstmt.close();
			   ppstmt =null;
			}
			if(conn != null){
			   conn.close();
			   conn = null;
			}
		}

		if(records == 0)
			ret = "No Record Update!";
		else
			ret = records+" Records Was Updated.";

		return ret;
	}
	
	public String saveConfirmDate(int lineId , String shipDate) throws Exception{
		String ret = "";
		int records = 0;
		PreparedStatement ppstmt = null;
		Connection conn = null;
		StringBuffer updateSql = new StringBuffer("UPDATE t_order_line SET CF_SHIP_DATE = ? WHERE order_line_id = ? ");
		logger.debug(updateSql);
		try{
			conn = new DBCPConnectionProvider().getConnection(conn);
			ppstmt = conn.prepareStatement(updateSql.toString());
			ppstmt.setTimestamp(1,  (shipDate != null ? DateToolsUtil.convertToTimeStamp(shipDate): null));
			ppstmt.setInt(2, lineId);
		
			records += ppstmt.executeUpdate();
		}catch(Exception ex){
			throw ex;
		}finally{
			if(ppstmt != null){
			   ppstmt.close();
			   ppstmt =null;
			}
			if(conn != null){
			   conn.close();
			   conn = null;
			}
		}
		
		if(records == 0)
			ret = "No Record Update!";
		else
			ret = "Records Was Updated.";
		
		return ret;
	}
}
