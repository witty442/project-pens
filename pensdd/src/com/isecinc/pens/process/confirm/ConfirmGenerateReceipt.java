package com.isecinc.pens.process.confirm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hslf.record.TxInteractiveInfoAtom;
import org.hibernate.mapping.Array;

import sun.security.action.GetLongAction;
import util.DateToolsUtil;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.OrderTransaction;
import com.isecinc.pens.bean.PriceList;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptConfirm;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.TaxInvoice;
import com.isecinc.pens.bean.TaxInvoiceLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MTaxInvoice;
import com.isecinc.pens.model.MTaxInvoiceLine;

public class ConfirmGenerateReceipt {

	protected static  Logger logger = Logger.getLogger("PENS");
	
	/*
	 * startTime = new Date().getTime();
						List<Receipt> receiptSuccessList = ConfirmGenerateReceipt.generateReceipt(conn, m,user,confirm);
						logger.debug("**** Debug Time ConfirmGenerateReceipt.generateReceipt:"+(new Date().getTime() - startTime));
	 */
	public static List<Receipt> generateReceipt(Connection conn,OrderTransaction m,User user,ReceiptConfirm confirm) throws Exception{
		List<Receipt> receiptList = null;
		TaxInvoice taxinvoice = null;
		double totalTaxinvoiceAmountGenInProcess = 0;
		try{

			//Gen Tax Invoice 
			long startTime = new Date().getTime();
			taxinvoice = genTaxInvoice(conn,m);
			logger.debug("**** Debug Time OverAll genTaxInvoice:"+(new Date().getTime() - startTime));
			
			totalTaxinvoiceAmountGenInProcess = taxinvoice.getTotalAmt();
			
			//Update TotalTaxInvoice
			m.setTotalTaxinvoiceAmount(m.getTotalTaxinvoiceAmount()+ totalTaxinvoiceAmountGenInProcess);
			
			startTime = new Date().getTime();
			if( !m.isPaymentOnly()){
			  //Gen Receipt
			  receiptList = genReceipt(conn,m,user,confirm,totalTaxinvoiceAmountGenInProcess);
			}else{
			  //Gen Receipt Case payment Only
			  receiptList = genReceiptCasePaymentOnly(conn,m,user,confirm,totalTaxinvoiceAmountGenInProcess);
			}
			logger.debug("**** Debug Time genReceipt:"+(new Date().getTime() - startTime));
			
			/** Check is Pay all in Order and update payment='Y' in t_order */
			startTime = new Date().getTime();
			checkAndUpdatePaymentAllSuccessInOrder(conn,m.getOrderId());
			logger.debug("**** Debug Time checkAndUpdatePaymentAllSuccessInOrder:"+(new Date().getTime() - startTime));
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return receiptList;
	}
	

	/**
	 * @param conn
	 * @param m
	 * @throws Exception
	 */
	private static TaxInvoice genTaxInvoice(Connection conn,OrderTransaction m) throws Exception{
		MTaxInvoice taxinvocieService = new MTaxInvoice();
		MTaxInvoiceLine taxinvoiceLineService = new MTaxInvoiceLine();
		TaxInvoice taxinvoidResult = new TaxInvoice();
		double totalTaxinvoiceGen = 0;
		try{
			//Create TaxInvoice 
			double totalRemainAmount = m.getTotalRemainAmount();
			double tripActNeedBill = m.getTripActNeedBill();
			double tripTotalAmount = m.getTripTotalAmount();
			int currentTripNo = m.getTripNo();
			double totalShipmentAmount = m.getTotalShipmentAmount();
			double totalTaxinvoiceAmount = m.getTotalTaxinvoiceAmount();
			double totalReceiptAmount = m.getTotalReceiptAmount();
			double totalTripActNeedBill = m.getTotalTripActNeedBill();
			
			double diffGenTax = totalTripActNeedBill - totalTaxinvoiceAmount;
			
			// TaxInvoiceLine List
			List<TaxInvoice> taxInvoiceList = new ArrayList<TaxInvoice>();
			
			logger.debug("totalTripActNeedBill="+totalTripActNeedBill);
			logger.debug("totalTaxinvoiceAmount="+totalTaxinvoiceAmount);
			logger.debug("diffGenTax="+diffGenTax);
			/*******************************************************************************************************************/
			
			if(diffGenTax > 0 ){
				logger.debug("********* diffGenTax > 0 *************** ");
				int countTaxinvoice = 1;
				long startTime = new Date().getTime();
				
				int[] r = getTotalLoopForGenTaxinvoice(conn, m.getOrderId());
		        int totalTrip = r[0];
		        int lastTripGenTaxInvoice = r[1];
		        currentTripNo = lastTripGenTaxInvoice+1; //ไม่เอา  order_line ที่ถูกสร้างไปแล้ว

	        	double tempLoop = tripTotalAmount;
				while( tempLoop <= diffGenTax){
					logger.debug(" tempLoop["+tempLoop+"] <= diffGenTax["+diffGenTax+"]");
					
					TaxInvoice taxinvoice = createTaxInvoiceLineFromOrderLine(conn,new TaxInvoice(),m.getOrderId(),currentTripNo);
			
					taxinvoice.setOrderId(m.getOrderId());
					taxinvoice.setTaxInvoiceDate(m.getConfirmReceiptDate());
					taxinvoice.setTaxInvoiceStatus("SV");
					taxinvoice.setTripNo(currentTripNo);
					
					//Create TaxInvoice
					logger.debug("Create Taxinvoice no["+countTaxinvoice+"]");
					taxinvocieService.save(taxinvoice, true, m.getUserId(), conn);
					//TotaltaxinvoiceAmount Gen
					totalTaxinvoiceGen += taxinvoice.getTotalAmt();
					
					taxInvoiceList.add(taxinvoice);
					
					//Temp Check
					tempLoop += tripTotalAmount;
					logger.debug("tempLoop["+(tempLoop)+"] currentTripNo["+currentTripNo+"]");
					currentTripNo++;
					countTaxinvoice++;
				}//while
				
				logger.debug("**** Debug Time createTaxInvoiceLineFromOrderLine:"+(new Date().getTime() - startTime));
				
		        
				startTime = new Date().getTime();
				//Create TaxInvoice Line
				if(taxInvoiceList != null && taxInvoiceList.size() >0){//1
					for(int i=0;i<taxInvoiceList.size();i++){ // for 1
						TaxInvoice tax = (TaxInvoice)taxInvoiceList.get(i);
						if(tax!= null && tax.getTaxInvoiceLineList() != null && tax.getTaxInvoiceLineList().size() >0){ //if 2
							for(int n=0;n<tax.getTaxInvoiceLineList().size();n++){ //for 2
								//Create TaxInvoice Line
								TaxInvoiceLine taxInvoiceLine =(TaxInvoiceLine)tax.getTaxInvoiceLineList().get(n);
								taxInvoiceLine.setTaxInvoiceId(tax.getId());
								taxinvoiceLineService.save(taxInvoiceLine, m.getUserId(), conn);
								
							}//for 2
						}// if 2
					}//for 1
				}//if 1
				
				logger.debug("**** Debug Time createTaxInvoiceLine:"+(new Date().getTime() - startTime));
			}
			
			
			//For Update TotaltaxInvoiceAmount in OrderTransaction
			taxinvoidResult.setTotalAmt(totalTaxinvoiceGen);
			
		}catch(Exception e){
			throw e;
		}
		return taxinvoidResult;
	}
	
	
	
	
	/**
	 * 
	 * @param conn
	 * @param m
	 * @throws Exception
	 */
	private static List<Receipt> genReceipt_V1(Connection conn,OrderTransaction m,User user,ReceiptConfirm confirm ,double totalTaxinvoiceAmountGenInProcess ) throws Exception{
		List<Receipt> receiptList = new ArrayList<Receipt>();
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
	        
			
			/** Check TaxInvoice Group and Gen Receipt By  **/
			double groupAmount = 0;
			double totalTaxinvoiceNoReceipt = 0;
			if(m.getTripNo() == 1){
				totalTaxinvoiceNoReceipt = getSumTotalTaxinvoiceNoReceipt(conn, m.getOrderId());
				if(totalRemainAmount == 0){
					groupAmount = totalTaxinvoiceNoReceipt - tripTotalAmount;
				}else{
					groupAmount = totalTaxinvoiceNoReceipt;
				}  
			}else{
				if(tripActNeedBill >= totalTaxinvoiceAmount){
					groupAmount = totalTaxinvoiceAmount - totalTaxinvoiceAmountGenInProcess;
				}else{
					totalTaxinvoiceNoReceipt = getSumTotalTaxinvoiceNoReceipt(conn, m.getOrderId());
					groupAmount = totalTaxinvoiceNoReceipt;
				}
			}
			
			logger.debug("***** Start process calc GroupAmount *****");
			logger.debug("Param TripNo["+m.getTripNo()+"]");
		    logger.debug("Param totalRemainAmount["+totalRemainAmount+"]");
			logger.debug("Param totalTaxinvoiceAmount["+totalTaxinvoiceAmount+"]");
			logger.debug("Param tripActNeedBill["+tripActNeedBill+"]");
			logger.debug("Param totalTaxinvoiceNoReceipt["+totalTaxinvoiceNoReceipt+"]");
			
			logger.debug("result GroupAmount["+groupAmount+"]");
			
			logger.debug("***** End process calc GroupAmount *****");
			
			 /** Create Receipt **/
			// receiptList = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,groupAmount);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return receiptList;
	}
	
	private static List<Receipt> genReceipt(Connection conn,OrderTransaction m,User user,ReceiptConfirm confirm ,double totalTaxinvoiceAmountGenInProcess ) throws Exception{
		List<Receipt> receiptList = new ArrayList<Receipt>();
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
			
			logger.debug("***************** Start process calc GroupAmount ********************************************");
			
			logger.debug("currentTripNo["+currentTripNo+"]");
			logger.debug("totalAmount["+totalAmount+"]");
			logger.debug("totalRemainAmount["+totalRemainAmount+"]");
			logger.debug("tripActNeedBill["+tripActNeedBill+"]");
			logger.debug("tripTotalAmount["+tripTotalAmount+"]");
			logger.debug("totalShipmentAmount["+totalShipmentAmount+"]");
			logger.debug("totalTaxinvoiceAmount["+totalTaxinvoiceAmount+"]");
			logger.debug("totalReceiptAmount["+totalReceiptAmount+"]");
	        
			/** Gen Receipt By Group **/
			double taxAmount1 = 0;
			double taxAmount2 = 0;
			double remianShipmentNotGenReceipt =  totalShipmentAmount -totalReceiptAmount;
			
			logger.debug("Param tripActNeedBill["+tripActNeedBill+"]["+remianShipmentNotGenReceipt+"]remianShipmentNotGenReceipt");
			
			if(m.getTripNo() ==1 && totalRemainAmount ==0){
				if(tripActNeedBill > remianShipmentNotGenReceipt){
					//Tax1 get TaxInvoice not Gen Receipt   trip <> currentTrip;
					taxAmount1 = tripTotalAmount;
					/** Create Receipt at prevTrip **/
					boolean genPrevTrip = true;
					boolean updatePrepay = false;
					receiptList = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount1,genPrevTrip,updatePrepay);
					
					/** Create Receipt at currentTrip **/
					genPrevTrip = false;
					updatePrepay = true;
					taxAmount2  = tripActNeedBill - taxAmount1;
					List<Receipt> receiptList1 = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount2,genPrevTrip,updatePrepay);
					
					//Add to List Show Summary
					receiptList.addAll(receiptList1);
				    
					logger.debug("Case 1 R =0, Trip ==1 Param taxAmount1["+taxAmount1+"]");
					logger.debug("Case 1 R =0, Trip ==1 Param taxAmount2["+taxAmount2+"]");
				}else{
					taxAmount2  = tripActNeedBill;
					
					logger.debug("Case 2 R<>0, Trip ==1  Param taxAmount2["+taxAmount2+"]");
					
					/** Create Receipt at currentTrip **/
					boolean genPrevTrip = false;
					boolean updatePrepay = true;
					receiptList = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount2,genPrevTrip,updatePrepay);
				}

			}else{
				if(tripActNeedBill > remianShipmentNotGenReceipt){
					//Tax1 get TaxInvoice not Gen Receipt   trip <> currentTrip;
					taxAmount1 = getAmountPrevTrip(conn, m);
					
					if(taxAmount1 != 0){
						/** Create Receipt at prevTrip **/
						boolean genPrevTrip = true;
						boolean updatePrepay = false;
						receiptList = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount1,genPrevTrip,updatePrepay);
						
						/** Create Receipt at currentTrip **/
						genPrevTrip = false;
						updatePrepay = true;
						taxAmount2  = tripActNeedBill - taxAmount1;
						List<Receipt> receiptList1 = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount2,genPrevTrip,updatePrepay);
						
						//Add to List Show Summary
						receiptList.addAll(receiptList1);
						
						logger.debug("Case3 R!=0 taxAmount1!=0  Param taxAmount1["+taxAmount1+"]");
						logger.debug("Case3 R!=0 taxAmount1!=0  Param taxAmount2["+taxAmount2+"]");
						
				    }else{
				    	
				    	/** Create Receipt at currentTrip **/
				    	boolean genPrevTrip = false;
						boolean updatePrepay = true;
						taxAmount2  = tripActNeedBill;
						List<Receipt> receiptList1 = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount2,genPrevTrip,updatePrepay);
						
						//Add to List Show Summary
						receiptList.addAll(receiptList1);
						
						logger.debug("Case4 R!=0 taxAmount1==0  Param taxAmount2["+taxAmount2+"]");
				    }
					
				}else{
					taxAmount2  = tripActNeedBill;
					
					logger.debug("Case5 R!=0  ,tripActNeedBill["+tripActNeedBill+"] > remianShipmentNotGenReceipt["+remianShipmentNotGenReceipt+"]  Param taxAmount2["+taxAmount2+"]");
					
					/** Create Receipt at currentTrip **/
					boolean genPrevTrip = false;
					boolean updatePrepay = true;
					receiptList = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,taxAmount2,genPrevTrip,updatePrepay);
				}
			}

			logger.debug("******************************* End process calc GroupAmount ***********************************");
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return receiptList;
	}
	
	/**
	 * 
	 * @param conn
	 * @param m
	 * @throws Exception
	 */
	private static List<Receipt> genReceiptCasePaymentOnly(Connection conn,OrderTransaction m,User user,ReceiptConfirm confirm ,double totalTaxinvoiceAmountGenInProcess ) throws Exception{
		List<Receipt> receiptList = new ArrayList<Receipt>();
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
	       
			
			/** Check TaxInvoice Group and Gen Receipt By  **/
			double groupAmount = totalTaxinvoiceAmountGenInProcess;
			
			
			logger.debug("***** Start process calc GroupAmount *****");
			logger.debug("Param TripNo["+m.getTripNo()+"]");
		    logger.debug("Param totalRemainAmount["+totalRemainAmount+"]");
			logger.debug("Param totalTaxinvoiceAmount["+totalTaxinvoiceAmount+"]");
			logger.debug("Param tripActNeedBill["+tripActNeedBill+"]");
			logger.debug("result GroupAmount["+groupAmount+"]");
			
			logger.debug("***** End process calc GroupAmount *****");
			
			 /**Create Receipt **/
			 receiptList = createReceiptFromTaxInvoiceNotReceipt(conn,m,user,confirm,groupAmount,false,true);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return receiptList;
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
	private static TaxInvoice createTaxInvoiceLineFromOrderLine(Connection conn, TaxInvoice taxinvoice,int orderId,int tripNo) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<TaxInvoiceLine> taxinvoiceLineList = new ArrayList<TaxInvoiceLine>();
		try{
			PriceList priceList = new MPriceList().getCurrentPriceList("DD");
			BigDecimal headTotalAmt = BigDecimal.ZERO;
			BigDecimal headLineAmt = BigDecimal.ZERO;
			
			BigDecimal lineAmt = BigDecimal.ZERO;
			BigDecimal totalLineAmt = BigDecimal.ZERO;

			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_order_line where order_id ="+orderId+" and trip_no ="+tripNo+" and product_id <> 47036  and iscancel <> 'Y' order by product_id \n");
			
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
				totalLineAmt = new BigDecimal(taxLine.getTotalAmt());
				
				if("Y".equals(rs.getString("promotion"))){
					totalLineAmt = BigDecimal.ZERO;
					lineAmt = BigDecimal.ZERO;
				}
				
				//Total 
				headTotalAmt = headTotalAmt.add(totalLineAmt);
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
	 * @param receipt
	 * @param orderId
	 * @param tripNo
	 * @param confirm
	 * @param taxinvoice
	 * @return
	 * @throws Exception
	 */
	private static List<Receipt> createReceiptFromTaxInvoiceNotReceipt_V1(Connection conn,OrderTransaction m,User user,ReceiptConfirm confirm,double groupAmount) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<Receipt> receiptSuccessList = new ArrayList<Receipt>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_taxinvoice where order_id ="+m.getOrderId()+" \n");
			sql.append(" and taxinvoice_status <>'VO' and taxinvoice_id not in( " );
			sql.append("   select l.taxinvoice_id from t_receipt t ,t_receipt_line l " );
			sql.append("   where t.doc_status <> 'VO' " );
			sql.append("   and t.receipt_id = l.receipt_id and order_id ="+m.getOrderId()+") \n");
			sql.append(" order by taxinvoice_id desc ");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
            
			logger.debug("*** Process Group TaxInvoice  for Gen Receipt groupAmount["+groupAmount+"]");
			List<Receipt> receiptGroupList = new ArrayList<Receipt>();
			List<TaxInvoice> taxInvoiceList = new ArrayList<TaxInvoice>();
			double receiptAmount = 0;
			TaxInvoice taxinvoice =null;
			int no = 1;
			while(rs.next()){
				taxinvoice = new TaxInvoice();
				taxinvoice.setId(rs.getInt("taxinvoice_id"));
				taxinvoice.setTotalAmt(rs.getDouble("total_amount"));
				
				receiptAmount += rs.getDouble("total_amount");
				taxInvoiceList.add(taxinvoice);
				
				logger.debug("no["+no+"]receiptAmount["+receiptAmount+"] groupAmount["+groupAmount+"]");
				if(receiptAmount == groupAmount){
					Receipt receiptTemp = new Receipt();
					receiptTemp.setReceiptAmount(receiptAmount);
					receiptTemp.setTaxInvoiceList(taxInvoiceList);
					receiptGroupList.add(receiptTemp);	
					receiptAmount = 0;
					taxInvoiceList = new ArrayList<TaxInvoice>();
				}
			}//while
			
			//Add Last Record
			if( receiptAmount != 0){
				Receipt receiptTemp = new Receipt();
				receiptTemp.setReceiptAmount(receiptAmount);
				receiptTemp.setTaxInvoiceList(taxInvoiceList);
				receiptGroupList.add(receiptTemp);	
			}
			
			/** Gen Receipt By Group Pay **/
			if(receiptGroupList != null && receiptGroupList.size() > 0){
				for(int i=0;i<receiptGroupList.size();i++){
					Receipt r = (Receipt)receiptGroupList.get(i);
					
					Receipt receipt = new Receipt();
					receipt.setCustomerId(m.getCustomerId());
					receipt.setCustomerName(m.getCustomerName());
					receipt.setOrderType(m.getOrderType());
					receipt.setReceiptDate(m.getConfirmReceiptDate());
					receipt.setInterfaces("N");
					receipt.setDocStatus("SV");
					receipt.setPrepaid("N");
					receipt.setApplyAmount(0d);
					receipt.setExported("N");
					receipt.setInternalBank("002");
					receipt.setSalesRepresent(user);
					receipt.setReceiptAmount(r.getReceiptAmount());
					receipt.setTripNo(m.getTripNo());
					
					//Create Receipt
					new MReceipt().save(receipt, m.getUserId(), conn);
					logger.debug("["+i+"]Create receiptNo:"+receipt.getReceiptNo()+",ReceiptAmount["+receipt.getReceiptAmount()+"]");
					
					receiptSuccessList.add(receipt);
					
					if(r.getTaxInvoiceList() != null && r.getTaxInvoiceList().size()> 0){
						int lineNo = 0;
						for(int j=0;j<r.getTaxInvoiceList().size();j++){
							TaxInvoice taxtemp = (TaxInvoice)r.getTaxInvoiceList().get(j);
							
							//Create Receipt Line
							//lineNo = createReceiptLineFromTaxinvoiceLine(conn, m, receipt, confirm,taxtemp.getId(),lineNo);
						}//for
					}
					
				}//for
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
		return receiptSuccessList;
	}

	
	private static double getAmountPrevTrip(Connection conn,OrderTransaction m) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double totalAmount =0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT sum(total_amount) as total_amount from t_taxinvoice where order_id ="+m.getOrderId()+" \n");
			sql.append(" and trip_no < "+m.getTripNo() +"\n");
			sql.append(" and taxinvoice_status <>'VO' ");
			sql.append(" and taxinvoice_id not in( " );
			sql.append("     select l.taxinvoice_id from t_receipt t ,t_receipt_line l " );
			sql.append("     where t.doc_status <> 'VO' " );
			sql.append("     and t.receipt_id = l.receipt_id and order_id ="+m.getOrderId()+" ) \n");

			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				totalAmount = rs.getDouble("total_amount");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return totalAmount;
	}
	
	/**
	 * 
	 * @param conn
	 * @param receipt
	 * @param orderId
	 * @param tripNo
	 * @param confirm
	 * @param taxinvoice
	 * @return
	 * @throws Exception
	 */
	private static List<Receipt> createReceiptFromTaxInvoiceNotReceipt(Connection conn,OrderTransaction m,User user,ReceiptConfirm confirm,double groupAmount,boolean genPrevTrip,boolean updatePrepay) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<Receipt> receiptSuccessList = new ArrayList<Receipt>();
		try{
			StringBuffer sql = new StringBuffer("");
			if(genPrevTrip){
				sql.append(" SELECT * from t_taxinvoice where order_id ="+m.getOrderId()+" \n");
				sql.append(" and trip_no < "+m.getTripNo()+"\n");
				sql.append(" and taxinvoice_status <>'VO' \n");
				sql.append(" and taxinvoice_id not in( " );
				sql.append("     select l.taxinvoice_id from t_receipt t ,t_receipt_line l " );
				sql.append("     where t.doc_status <> 'VO' " );
				sql.append("     and t.receipt_id = l.receipt_id and order_id ="+m.getOrderId()+") \n");
				sql.append(" order by taxinvoice_id desc ");
			}else{
				sql.append(" SELECT * from t_taxinvoice where order_id ="+m.getOrderId()+" \n");
				sql.append(" and taxinvoice_status <>'VO' \n");
				sql.append(" and taxinvoice_id not in( " );
				sql.append("     select l.taxinvoice_id from t_receipt t ,t_receipt_line l " );
				sql.append("     where t.doc_status <> 'VO' " );
				sql.append("     and t.receipt_id = l.receipt_id and order_id ="+m.getOrderId()+") \n");
				sql.append(" order by taxinvoice_id desc ");
			}
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
            
			logger.debug("*** Process Group TaxInvoice  for Gen Receipt groupAmount["+groupAmount+"]");
			List<Receipt> receiptGroupList = new ArrayList<Receipt>();
			List<TaxInvoice> taxInvoiceList = new ArrayList<TaxInvoice>();
			double receiptAmount = 0;
			TaxInvoice taxinvoice =null;
			int no = 1;
			while(rs.next()){
				taxinvoice = new TaxInvoice();
				taxinvoice.setId(rs.getInt("taxinvoice_id"));
				taxinvoice.setTotalAmt(rs.getDouble("total_amount"));
				
				receiptAmount += rs.getDouble("total_amount");
				taxInvoiceList.add(taxinvoice);
				
				logger.debug("no["+no+"]receiptAmount["+receiptAmount+"] groupAmount["+groupAmount+"]");
				if(receiptAmount == groupAmount){
					Receipt receiptTemp = new Receipt();
					receiptTemp.setReceiptAmount(receiptAmount);
					receiptTemp.setTaxInvoiceList(taxInvoiceList);
					receiptGroupList.add(receiptTemp);	
					receiptAmount = 0;
					taxInvoiceList = new ArrayList<TaxInvoice>();
				}
			}//while
			
			//Add Last Record
			if( receiptAmount != 0){
				Receipt receiptTemp = new Receipt();
				receiptTemp.setReceiptAmount(receiptAmount);
				receiptTemp.setTaxInvoiceList(taxInvoiceList);
				receiptGroupList.add(receiptTemp);	
			}
			
			/** Gen Receipt By Group Pay **/
			if(receiptGroupList != null && receiptGroupList.size() > 0){
				for(int i=0;i<receiptGroupList.size();i++){
					Receipt r = (Receipt)receiptGroupList.get(i);
					
					Receipt receipt = new Receipt();
					receipt.setCustomerId(m.getCustomerId());
					receipt.setCustomerName(m.getCustomerName());
					receipt.setOrderType(m.getOrderType());
					receipt.setReceiptDate(m.getConfirmReceiptDate());
					receipt.setInterfaces("N");
					receipt.setDocStatus("SV");
					receipt.setPrepaid("N");
					receipt.setApplyAmount(0d);
					receipt.setExported("N");
					receipt.setInternalBank("002");
					receipt.setSalesRepresent(user);
					receipt.setReceiptAmount(r.getReceiptAmount());
					receipt.setTripNo(m.getTripNo());
					
					//Create Receipt
					new MReceipt().save(receipt, m.getUserId(), conn);
					logger.debug("["+i+"]Create receiptNo:"+receipt.getReceiptNo()+",ReceiptAmount["+receipt.getReceiptAmount()+"]");
					
					receiptSuccessList.add(receipt);
					
					if(r.getTaxInvoiceList() != null && r.getTaxInvoiceList().size()> 0){
						int lineNo = 0;
						for(int j=0;j<r.getTaxInvoiceList().size();j++){
							TaxInvoice taxtemp = (TaxInvoice)r.getTaxInvoiceList().get(j);
							
							//Create Receipt Line
							lineNo = createReceiptLineFromTaxinvoiceLine(conn, m, receipt, confirm,taxtemp.getId(),lineNo,updatePrepay);
							
						}
					}
					
				}//for

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
		return receiptSuccessList;
	}
	
	/**
	 * 
	 * @param conn
	 * @param receipt
	 * @param orderId
	 * @param tripNo
	 * @param confirm
	 * @return
	 * @throws Exception
	 */
	private static int createReceiptLineFromTaxinvoiceLine(Connection conn,OrderTransaction m ,Receipt receipt,ReceiptConfirm confirm,int taxinvoiceId,int lineNo,boolean updatePrepay) throws Exception{
		PreparedStatement ps =null;
		PreparedStatement psUpdatePrepay =null;
		ResultSet rs = null;
		try{
			/** Case Pay more than trip amount  set Next trip to prepay **/
			if(updatePrepay){
			  psUpdatePrepay = conn.prepareStatement("update t_order_line set prepay ='Y' where cf_ship_date is null and order_id = ? and order_line_id =? ");
			}
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_taxinvoice_line where taxinvoice_id ="+taxinvoiceId+" \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			while(rs.next()){
				lineNo++;
				/****   ReceiptLine ***********************/
				ReceiptLine receiptLine = new ReceiptLine();
				receiptLine.setLineNo(lineNo);
				receiptLine.setCreditAmount(rs.getDouble("total_amount"));
				receiptLine.setInvoiceAmount(rs.getDouble("total_amount"));
				receiptLine.setPaidAmount(rs.getDouble("total_amount"));
				receiptLine.setRemainAmount(0d);
				receiptLine.setReceiptId(receipt.getId());
				receiptLine.setTaxinvoiceId(taxinvoiceId);
				
				Order order = new Order();
				order.setId(m.getOrderId());
				receiptLine.setOrder(order);
				
				OrderLine orderLine = new OrderLine();
				orderLine.setOrderId(m.getOrderId());
				orderLine.setId(rs.getInt("order_line_id"));
				receiptLine.setOrderLine(orderLine);
				
				/** Case Pay more than trip amount  set Next trip to prepay **/
				if(updatePrepay){
					logger.debug("*** Update t_order_line set prepay = 'Y' where cf_ship_date is null and order_id="+orderLine.getOrderId()+" and order_line_id="+orderLine.getId()+"***");
					psUpdatePrepay.setInt(1, orderLine.getOrderId());
					psUpdatePrepay.setInt(2, orderLine.getId());
					
					psUpdatePrepay.executeUpdate();
				}

				new MReceiptLine().save(receiptLine, m.getUserId(), conn);
				
				/******* Receipt By **********************/
				ReceiptBy by = new ReceiptBy();
				by.setPaymentMethod(confirm.getPaymentMethod());

				if ("CR".equals(confirm.getPaymentMethod())) {
					by.setBank(confirm.getBank());
					by.setChequeNo(confirm.getCreditCardNo());
					by.setCreditCardName(confirm.getCreditCardName());
				} else if ("CH".equals(confirm.getPaymentMethod())) {
					by.setBank(confirm.getBank());
					by.setChequeNo(confirm.getChequeNo());
					by.setChequeDate(confirm.getChequeDate());
				}

				by.setReceiptAmount(receiptLine.getPaidAmount());
				by.setPaidAmount(receiptLine.getPaidAmount());
				by.setRemainAmount(0d);
				by.setReceiptId(receipt.getId());
				by.setWriteOff("N");
				
				new MReceiptBy().save(by, m.getUserId(), conn);

				/******** receipt Match ******************/
				ReceiptMatch match = new ReceiptMatch();
				match.setReceiptById(by.getId());
				match.setPaidAmount(receiptLine.getPaidAmount());
				match.setReceiptId(receipt.getId());
				match.setReceiptLineId(receiptLine.getId());
				
				logger.info("Create Receipt Match");
				new MReceiptMatch().save(match, m.getUserId(), conn);
				
				/** if payment in t_order_line is all set payment in r_order  and cf_receipt_date **/
				updatePaymentFlagInOrderLine(conn, m.getOrderId(), orderLine.getId() ,m.getConfirmReceiptDate());
				
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
			if(psUpdatePrepay != null){
				psUpdatePrepay.close();psUpdatePrepay=null;
			}
		}
		return lineNo;
	}
	
	
	private static int[] getTotalLoopForGenTaxinvoice(Connection conn, int orderId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int[] r = new int[2];
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT  sum(A.total_trip) as total_trip , sum(A.taxinvoice_trip) as taxinvoice_trip FROM(   \n");
			sql.append("   select max(trip_no) as total_trip, 0 as taxinvoice_trip  from t_order_line where order_id =? and iscancel <> 'Y' \n");
			
			sql.append("   UNION ALL    \n");
			
			sql.append("   select 0 as total_trip, max(ol.trip_no) as taxinvoice_trip \n");
			sql.append("   from t_order_line ol,t_taxinvoice h,t_taxinvoice_line l  \n");
			sql.append("   where   ol.order_line_id = l.order_line_id \n");
			sql.append("   and h.taxinvoice_id =l.taxinvoice_id   \n");
			sql.append("   and h.order_id = ?   \n");
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
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @throws Exception
	 */
	private static void checkAndUpdatePaymentAllSuccessInOrder(Connection conn, int orderId) throws Exception{
		PreparedStatement ps =null;
		PreparedStatement psUpdate =null;
		ResultSet rs = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT  sum(A.all_record) as all_record , sum(A.payment_record) as payment_record FROM( \n");
			sql.append("   SELECT  count(payment) as all_record ,0 as payment_record from t_order_line where order_id =?  \n");
			sql.append("    UNION ALL  \n");
			sql.append("    SELECT 0 as all_record,  count(payment) as payment_record from t_order_line where order_id =? and payment ='Y' \n");
			sql.append(" )A  \n");
				
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, orderId);
			ps.setInt(2, orderId);
			
			rs = ps.executeQuery();
			
            int allOrderLine  = 0;//1
            int paymentOrderLine = 0;//2

			if(rs.next()){
				allOrderLine = rs.getInt("all_record");
				paymentOrderLine = rs.getInt("payment_record");
				
				if(allOrderLine == paymentOrderLine){
					psUpdate = conn.prepareStatement("update t_order set payment ='Y' ,updated = current_date where order_id =? ");
					psUpdate.setInt(1, orderId);
					psUpdate.executeUpdate();
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
			if(psUpdate != null){
			   psUpdate.close();psUpdate = null;
		    }
		}
	}
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @throws Exception
	 */
	private static double getSumTotalTaxinvoiceNoReceipt(Connection conn, int orderId) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		double totalTaxinvoiceNoReceipt= 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT sum(total_amount) as amt from t_taxinvoice where order_id ="+orderId+" \n");
			sql.append(" and taxinvoice_status <> 'VO' and taxinvoice_id not in( ");
			sql.append("    select l.taxinvoice_id from t_receipt t ,t_receipt_line l  " );
			sql.append("    where t.doc_status <> 'VO' and t.receipt_id = l.receipt_id and order_id ="+orderId+") \n");
			sql.append(" group by order_id ");
				
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			
			if(rs.next()){
				totalTaxinvoiceNoReceipt = rs.getDouble("amt");
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
		return totalTaxinvoiceNoReceipt;
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
	private static Receipt createReceiptLineFromOrderLine(Connection conn, Receipt receipt,int orderId,int tripNo,ReceiptConfirm confirm) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		int lineNo = 0;
		try{
			PriceList priceList = new MPriceList().getCurrentPriceList("DD");
			BigDecimal receiptAmount = BigDecimal.ZERO;

			List<ReceiptLine> receiptLineList = new ArrayList<ReceiptLine>();
			List<ReceiptBy> receiptByList = new ArrayList<ReceiptBy>();
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT * from t_order_line where order_id ="+orderId+" and trip_no ="+tripNo+" order by product_id \n");
			
		    logger.debug("SQL:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while(rs.next()){
				lineNo++;
				/****   ReceiptLine ***********************/
				ReceiptLine line = new ReceiptLine();
				line.setLineNo(lineNo);
				line.setCreditAmount(rs.getDouble("total_amount"));
				line.setInvoiceAmount(rs.getDouble("total_amount"));
				line.setPaidAmount(rs.getDouble("total_amount"));
				line.setRemainAmount(0d);
				line.setReceiptId(receipt.getId());
				//line.setOrder(order);
				receiptAmount = receiptAmount.add(new BigDecimal(line.getPaidAmount()));

				if (!"Y".equals(rs.getString("shipment"))) {
					line.setPrepaid("Y");
				}
				
				receiptLineList.add(line);
				
				/******* Receipt By **********************/
				ReceiptBy by = new ReceiptBy();
				by.setPaymentMethod(confirm.getPaymentMethod());

				if ("CR".equals(confirm.getPaymentMethod())) {
					by.setBank(confirm.getBank());
					by.setChequeNo(confirm.getCreditCardNo());
					by.setCreditCardName(confirm.getCreditCardName());
				} else if ("CH".equals(confirm.getPaymentMethod())) {
					by.setBank(confirm.getBank());
					by.setChequeNo(confirm.getChequeNo());
					by.setChequeDate(confirm.getChequeDate());
				}

				by.setReceiptAmount(line.getPaidAmount());
				by.setPaidAmount(line.getPaidAmount());
				by.setRemainAmount(0d);
				by.setReceiptId(receipt.getId());
				by.setWriteOff("N");
				
				receiptByList.add(by);
			}
			
			receipt.setReceiptLines(receiptLineList);
			receipt.setReceiptBys(receiptByList);
			
			receipt.setReceiptAmount(receiptAmount.doubleValue());
			
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
		return receipt;
	}
	
	/**
	 * 
	 * @param conn
	 * @param orderId
	 * @param orderLineId
	 * @throws Exception
	 */
	public static void updatePaymentFlagInOrderLine(Connection conn,int orderId,int orderLineId,String confirmDate) throws Exception{
		PreparedStatement ps =null;
		try{
			StringBuffer updateSql = new StringBuffer("update t_order_line \n");
			updateSql.append(" set payment ='Y' ,cf_receipt_date = ? \n")
					 .append(" where order_id = ? and order_line_id = ? ");
			logger.debug("sql:"+updateSql.toString());
			logger.debug("order_id["+orderId+"]");
			logger.debug("order_line_id["+orderLineId+"]");
			
			ps = conn.prepareStatement(updateSql.toString());
			//ps.setTimestamp(1, StringUtils.isEmpty(confirmDate)?null:DateToolsUtil.convertToTimeStamp(confirmDate) );
			ps.setTimestamp(1, new java.sql.Timestamp((Utils.convertToTimeStamp(confirmDate, Utils.DD_MM_YYYY_WITH_SLASH, "th")).getTime()));
			ps.setInt(2, orderId);
			ps.setInt(3, orderLineId);
			
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
}
