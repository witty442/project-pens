package com.isecinc.pens.process.confirm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.OrderTransaction;
import com.isecinc.pens.bean.PriceList;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.TaxInvoice;
import com.isecinc.pens.bean.TaxInvoiceLine;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MTaxInvoice;
import com.isecinc.pens.model.MTaxInvoiceLine;



public class ConfirmGenerateTaxInvoice {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	
	public static TaxInvoice processGenerateTaxInvoice(Connection conn,OrderTransaction m) throws Exception{
		int i = 0;
		TaxInvoice taxinvoice = null;
		try{
			
			if(!m.isPaymentOnly()){ //No Gen OrderLine is payment only product 99999
			   taxinvoice = generateTaxInvoice(conn,m);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}finally{
		}
		return taxinvoice;
	}
	
	/** Case 1.1 total_remain_amount = 0  **/
	/** Case 1.1 เก็บหมดแล้ว
		IF remain_amount(cur) ="0"{ 
		  T1 = trip_total_amount
		  T2 = trip_act_need_bill - T1
		}
	**/
	public static TaxInvoice generateTaxInvoice(Connection conn,OrderTransaction m) throws Exception{
		MTaxInvoice taxinvocieService = new MTaxInvoice();
		MTaxInvoiceLine taxinvoiceLineService = new MTaxInvoiceLine();
		TaxInvoice taxinvoice = new TaxInvoice();
		try{
			//Create TaxInvoice 
			int currentTripNo = m.getTripNo();
			double totalAmount = m.getTotalAmount();
			double totalRemainAmount = m.getTotalRemainAmount();
			double tripActNeedBill = m.getTripActNeedBill();
			double tripTotalAmount = m.getTripTotalAmount();
			double totalShipmentAmount = m.getTotalShipmentAmount();
			double totalTaxinvoiceAmount = m.getTotalTaxinvoiceAmount();
			double totalReceiptAmount = m.getTotalReceiptAmount();
			
			logger.debug("currentTripNo["+currentTripNo+"]");
			logger.debug("totalAmount["+totalAmount+"]");
			logger.debug("totalRemainAmount["+totalRemainAmount+"]");
			logger.debug("tripActNeedBill["+tripActNeedBill+"]");
			logger.debug("tripTotalAmount["+tripTotalAmount+"]");
			logger.debug("totalShipmentAmount["+totalShipmentAmount+"]");
			logger.debug("totalTaxinvoiceAmount["+totalTaxinvoiceAmount+"]");
			logger.debug("totalReceiptAmount["+totalReceiptAmount+"]");
			
			boolean taxinvoiceTripIsExist = isTripFoundInTaxinvoice(conn,m.getOrderId(),m.getTripNo());
			if( !taxinvoiceTripIsExist){
				/** Get Order Line By orderNo ,tripNo to Taxinvoice **/
				taxinvoice = createTaxInvoiceLineFromOrderLine(conn,new TaxInvoice(),m.getOrderId(),currentTripNo);
				
				taxinvoice.setOrderId(m.getOrderId());
				taxinvoice.setTaxInvoiceDate(m.getShippingDate());
				taxinvoice.setTaxInvoiceStatus("SV");
				taxinvoice.setTripNo(currentTripNo);
				
				//Create Or Update TaxInvoice
				taxinvocieService.save(taxinvoice, true, m.getUserId(), conn);
	            
				//Create TaxInvoice Line
				if(taxinvoice!= null && taxinvoice.getTaxInvoiceLineList() != null && taxinvoice.getTaxInvoiceLineList().size() >0){
					for(int n=0;n<taxinvoice.getTaxInvoiceLineList().size();n++){
						TaxInvoiceLine taxInvoiceLine =(TaxInvoiceLine)taxinvoice.getTaxInvoiceLineList().get(n);
						taxInvoiceLine.setTaxInvoiceId(taxinvoice.getId());
						//Save taxInvoice Line
						taxinvoiceLineService.save(taxInvoiceLine, m.getUserId(), conn);
					}
				}//for
	
				/** Update taxinvoice_id to t_order **/
				updateTaxinvoiceIdInShipment(conn,m.getShipment().getId(),taxinvoice.getId());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return taxinvoice;
	}
	
	
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @param taxinvoiceId
	 * @throws Exception
	 */
	public static void updateTaxinvoiceIdInShipment(Connection conn,int shipmentId,int taxinvoiceId) throws Exception{
		PreparedStatement ps =null;
		try{
			StringBuffer updateSql = new StringBuffer("update t_shipment \n");
			updateSql.append(" set taxinvoice_id = ? \n")
					 .append(" where shipment_id = ? ");
			logger.debug("sql:"+updateSql.toString());
			logger.debug("taxinvoice_id["+taxinvoiceId+"]");
			logger.debug("shipmentId["+shipmentId+"]");
			
			ps = conn.prepareStatement(updateSql.toString());
			ps.setInt(1, taxinvoiceId);
			ps.setInt(2, shipmentId);
			
			ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(ps != null){
				ps.close();
				ps= null;
			}
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param taxinvoice
	 * @param orderId
	 * @param tripNo
	 * @return
	 * @throws Exception
	 */
	public static TaxInvoice createTaxInvoiceLineFromOrderLine(Connection conn, TaxInvoice taxinvoice,int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<TaxInvoiceLine> taxinvoiceLineList = new ArrayList<TaxInvoiceLine>();
		try{
			PriceList priceList = new MPriceList().getCurrentPriceList("DD");
			BigDecimal headTotalAmt = BigDecimal.ZERO;
			BigDecimal headLineAmt = BigDecimal.ZERO;
			
			BigDecimal lineAmt = BigDecimal.ZERO;
			BigDecimal totalAmt = BigDecimal.ZERO;

			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_order_line where order_id ="+orderId+" and trip_no ="+tripNo+" and product_id <> 47036 order by product_id \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				TaxInvoiceLine taxLine = new TaxInvoiceLine(taxinvoice);
				taxLine.setLinesAmt(rs.getDouble("line_amount"));
				taxLine.setTotalAmt(rs.getDouble("total_amount"));
				taxLine.setVatAmt();
				taxLine.setQty(rs.getInt("qty"));
				taxLine.setOrderLineId(rs.getInt("order_line_id"));
				taxLine.setProductId(rs.getInt("product_id"));
				taxLine.setUomId(rs.getString("uom_id"));
				
				List<ProductPrice> pps = new MProductPrice().getCurrentPrice(String.valueOf(taxLine.getProductId())
						,String.valueOf(priceList.getId()), taxLine.getUomId());
				double price = pps.get(0).getPrice();
				taxLine.setPrice(price);
				
				lineAmt = new BigDecimal(taxLine.getLinesAmt());
				totalAmt = new BigDecimal(taxLine.getTotalAmt());
				
				if("Y".equals(rs.getString("promotion"))){
					totalAmt = BigDecimal.ZERO;
					lineAmt = BigDecimal.ZERO;
				}
				
				//Total 
				headTotalAmt = headTotalAmt.add(totalAmt);
				headLineAmt = headLineAmt.add(lineAmt);
				
				taxinvoiceLineList.add(taxLine);
			}
			
			taxinvoice.setTotalAmt(headTotalAmt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			taxinvoice.setLinesAmt(headLineAmt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
			taxinvoice.setVatAmt(headTotalAmt.setScale(2,2).subtract(headLineAmt.setScale(2,2)).doubleValue());
			
			taxinvoice.setTaxInvoiceLineList(taxinvoiceLineList);
			
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
		return taxinvoice;
	}
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @param tripNo
	 * @return
	 * @throws Exception
	 */
	private static boolean isTripFoundInTaxinvoice(Connection conn, int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		boolean r = false;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT taxinvoice_id from t_taxinvoice where order_id = ? and trip_no = ? and taxinvoice_status ='SV'   \n");
				
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, orderId);
			ps.setInt(2, tripNo);
			
			rs = ps.executeQuery();

			if(rs.next()){
			  if(rs.getInt("taxinvoice_id") > 0){
				  r = true;
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
		return r;
	}
	
	private static int[] getTotalLoopForGenTaxinvoice(Connection conn, int orderId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int[] r = new int[2];
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT  sum(A.total_trip) as total_trip , sum(A.taxinvoice_trip) as taxinvoice_trip FROM(   \n");
			sql.append("  select max(trip_no) as total_trip, 0 as taxinvoice_trip  from t_order_line where order_id =?  \n");
			sql.append("   UNION ALL    \n");
			sql.append("   select 0 as total_trip, max(trip_no) as taxinvoice_trip from t_order_line where order_line_id in(  \n");
			sql.append("     select  order_line_id from t_taxinvoice_line where order_id =?  \n");
			sql.append("    )   \n");
			sql.append(")A  \n");
				
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, orderId);
			ps.setInt(2, orderId);
			
			rs = ps.executeQuery();

			if(rs.next()){
				r[0] = rs.getInt("total_trip");
				r[1] = rs.getInt("taxinvoice_trip");
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
		return r;
	}
}
