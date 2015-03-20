package com.isecinc.pens.process.order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.Debug;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.model.MUOM;
import com.isecinc.pens.model.MUOMConversion;

/**
 * Order Process Class
 * 
 * @author Atiz.b
 * @version $Id: OrderProcess.java,v 1.0 24/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderProcessNewEditcasePromotionsameproduct {
	private Logger logger = Logger.getLogger("PENS");
    public Debug debug = new Debug(true);
	

	/**
	 * Cal Shipment Date
	 * 
	 * @param day
	 * @param registerDate
	 * @param week
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private String calShippingDate(String day, String registerDate, int week) throws Exception {
		String shippingDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
		int shipDay = 0;
		int regDay = 0;
		int diffDay = 0;

		try {
			// 0 = Sunday, 1 = Monday,...
			Date regDate = formatter.parse(registerDate);
			Calendar c1 = Calendar.getInstance();
			c1.setTime(regDate);
			regDay = regDate.getDay();
			shipDay = DateToolsUtil.getNumOfDay(day);
			diffDay = regDay;
			if (regDay == shipDay) {
				shippingDate = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(regDate);
				return shippingDate;
			}
			boolean isOk = true;
			int count = 0;
			while (isOk) {
				diffDay++;
				if (diffDay == shipDay) {
					isOk = false;
				}
				if (diffDay == 6) {
					diffDay = -1; // set num of day to 0 (sun).
				}
				count++;
			}

			if (count < 3) {
				c1.add(Calendar.DATE, (count + 7) * (week + 1));
			} else {
				c1.add(Calendar.DATE, count * (week + 1));
			}

			// Check when shipping day is sun (day_of_week=1) add day to next day.
			if (c1.get(Calendar.DAY_OF_WEEK) == 1) {
				c1.set(Calendar.DAY_OF_WEEK, c1.get(Calendar.DAY_OF_WEEK) + 1);
			}
			shippingDate = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(c1.getTime());
		} catch (Exception e) {
			throw e;
		}

		return shippingDate;
	}

	/**
	 * Cal week round Trip
	 * 
	 * @param roundTrip
	 * @return
	 * @throws Exception
	 */
	private int calWeekByRoundtrip(String roundTrip) throws Exception {
		int numRoundTrip = 0;
		int week = 0;
		numRoundTrip = (new Double(roundTrip).intValue());
		week = numRoundTrip / 7;

		return week;
	}

	/**
	 * Add Date String
	 * 
	 * @param date
	 * @param dayOfDate
	 * @return
	 * @throws Exception
	 */
	private String addDate(String date, int dayOfDate) throws Exception {
		String newDate = "";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));

		try {
			Date dt = formatter.parse(date);
			Calendar c1 = Calendar.getInstance();
			c1.setTime(dt);
			c1.add(Calendar.DATE, dayOfDate);
			newDate = formatter.format(c1.getTime());
		} catch (Exception e) {
			throw e;
		}

		return newDate;
	}

	/**
	 * Filter list from display for save.
	 * 
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	public List<OrderLine> fillLinesSave(List<OrderLine> lines, User user, String isVIP) throws Exception {
		List<OrderLine> newLines = new ArrayList<OrderLine>();
		OrderLine odLine = null;
		OrderLine line = null;
		int i = 0;

		try {
			while (i < lines.size()) {
				odLine = lines.get(i);
				// Generate 2 lines.
	
					if (odLine.getQty1() != 0) {
						// Line 1
						line = new OrderLine();
						line.setId(odLine.getId());
						line.setProduct(new MProduct().find(String.valueOf(odLine.getProduct().getId())));
						line.setActiveLabel(odLine.getActiveLabel());
						line.setArInvoiceNo(odLine.getArInvoiceNo());
						line.setDiscount(odLine.getDiscount1());
						line.setLineAmount(odLine.getLineAmount1());
						line.setLineNo(odLine.getLineNo());
						line.setOrderId(odLine.getOrderId());
						line.setPayment(odLine.getPayment());
						line.setPrice(odLine.getPrice1());
						line.setPromotion(odLine.getPromotion());
						line.setQty(odLine.getQty1());
						line.setRequestDate(odLine.getRequestDate());
						line.setShippingDate(odLine.getShippingDate());
						line.setTotalAmount(odLine.getTotalAmount1());
						line.setUom(odLine.getUom1());
						line.setVatAmount(odLine.getVatAmount1());
						line.setTripNo(odLine.getTripNo());
						line.setFullUom(odLine.getFullUom());
						
						newLines.add(line);
					}
					if (odLine.getQty2() != 0) {
						// Line 2
						line = new OrderLine();
						line.setId(odLine.getId());
						line.setProduct(new MProduct().find(String.valueOf(odLine.getProduct().getId())));
						line.setActiveLabel(odLine.getActiveLabel());
						line.setArInvoiceNo(odLine.getArInvoiceNo());
						line.setDiscount(odLine.getDiscount2());
						line.setLineAmount(odLine.getLineAmount2());
						line.setLineNo(odLine.getLineNo());
						line.setOrderId(odLine.getOrderId());
						line.setPayment(odLine.getPayment());
						line.setPrice(odLine.getPrice2());
						line.setPromotion(odLine.getPromotion());
						line.setQty(odLine.getQty2());
						line.setRequestDate(odLine.getRequestDate());
						line.setShippingDate(odLine.getShippingDate());
						line.setTotalAmount(odLine.getTotalAmount2());
						line.setUom(odLine.getUom2());
						line.setVatAmount(odLine.getVatAmount2());
						line.setTripNo(odLine.getTripNo());
						line.setFullUom(odLine.getFullUom());
						newLines.add(line);
					}
				i++;
			}
		} catch (Exception e) {
			throw e;
		}
		return newLines;
	}

	/**
	 * Filter list for web display.
	 * 
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	public List<OrderLine> fillLinesShow(List<OrderLine> lines) throws Exception {
		List<OrderLine> newLines = new ArrayList<OrderLine>();
		OrderLine odLine = null;
		OrderLine line = null;
		int i = 0;
		int prvIndex = 0;
		boolean firstAdd = true;

		try {

			while (i < lines.size()) {
				odLine = lines.get(i);
				//logger.debug("line p["+odLine.getProduct()+"]q["+odLine.getQty()+"]uom["+odLine.getUom()+"]");
				debug.info("*** Line product["+odLine.getProduct().getCode()+"]" +
				" fulluom["+odLine.getFullUom()+"]" +
				" UOM["+odLine.getUom().getCode()+"]" +
				" UOM1["+odLine.getUom1().getCode()+"]" +
				" UOM2["+odLine.getUom2().getCode()+"]" +
				" qty["+odLine.getQty()+"]" +
				" qty1["+odLine.getQty1()+"]" +
				" qty2["+odLine.getQty2()+"]***");
				
				// First add.
				if (firstAdd) {
					// Aneak.t 24/01/2011
					if (odLine.getIscancel() != null && odLine.getIscancel().equals("Y")) {
						continue;
					}
					logger.info("first Add line no["+i+"]");
					
					line = new OrderLine();
					line.setId(odLine.getId());
					line.setActiveLabel(odLine.getActiveLabel());
					line.setArInvoiceNo(odLine.getArInvoiceNo());
					line.setLineNo(odLine.getLineNo());
					line.setOrderId(odLine.getOrderId());
					line.setPayment(odLine.getPayment());
					line.setProduct(new MProduct().find(String.valueOf(odLine.getProduct().getId())));
					line.setPromotion(odLine.getPromotion());
					line.setRequestDate(odLine.getRequestDate());
					line.setShippingDate(odLine.getShippingDate());
					line.setTripNo(odLine.getTripNo());

					line.setVatAmount(odLine.getVatAmount());
					line.setPrice(odLine.getPrice());
					line.setUom(odLine.getUom());
					line.setQty(odLine.getQty());
					line.setLineAmount(odLine.getLineAmount());
					line.setDiscount(odLine.getDiscount());
					line.setTotalAmount(odLine.getTotalAmount());
					
					
					//debug.info("product.uom["+odLine.getProduct().getUom().getId()+"]order.uom["+odLine.getUom().getId()+"]");
					
					if (odLine.getProduct().getUom().getId().equals(odLine.getUom().getId())) {
						line.setVatAmount1(odLine.getVatAmount());
						line.setUom1(odLine.getUom());
						odLine.setUom1(odLine.getUom());
						line.setPrice1(odLine.getPrice());
						line.setQty1(line.getQty1() + odLine.getQty());
						line.setLineAmount1(line.getLineAmount1() + odLine.getLineAmount());
						line.setDiscount1(line.getDiscount1() + odLine.getDiscount());
						line.setTotalAmount1(line.getTotalAmount1() + odLine.getTotalAmount());
					} else {
						line.setVatAmount2(odLine.getVatAmount());logger.info("xxx1");
						line.setUom2(odLine.getUom());
						odLine.setUom2(odLine.getUom());
						line.setPrice2(odLine.getPrice());
						line.setQty2(line.getQty2() + odLine.getQty());
						line.setLineAmount2(line.getLineAmount2() + odLine.getLineAmount());
						line.setDiscount2(line.getDiscount2() + odLine.getDiscount());
						line.setTotalAmount2(line.getTotalAmount2() + odLine.getTotalAmount());
					}
					if (odLine.getFullUom() == null) {
						if (odLine.getProduct().getUom().getId().equals(odLine.getUom().getId())) line
								.setFullUom(ConvertNullUtil.convertToString(odLine.getUom().getCode() + "/"));
						else line.setFullUom(ConvertNullUtil.convertToString("/" + odLine.getUom().getCode()));
					} else {
						line.setFullUom(odLine.getFullUom());
					}
					/**option **/
					line.setOrderNo(odLine.getOrderNo());
					line.setCustomerName(odLine.getCustomerName());
					line.setCustomerCode(odLine.getCustomerCode());
					line.setOrderDate(odLine.getOrderDate());
					line.setStatus(odLine.getStatus());

					
					newLines.add(line);
					firstAdd = false;
					prvIndex = 0;
				} else {
					// Update previous line if repeat product code & promotion.
                    logger.info("line no["+i+"]");
					if (odLine.getIscancel() != null && odLine.getIscancel().equals("Y")) {
						continue;
					}
					line = new OrderLine();
					if (odLine.getProduct().getCode().equals(lines.get(i - 1).getProduct().getCode())
							&& odLine.getPromotion().equals(lines.get(i - 1).getPromotion())
							&& odLine.getShippingDate().equals(lines.get(i - 1).getShippingDate())
							&& odLine.getRequestDate().equals(lines.get(i - 1).getRequestDate())) {
						
						//product code== prevLine.productCode
						if (odLine.getPromotion().equals("Y") ){
							logger.info("xxx2");
							logger.info("newLines.get(prvIndex).getQty()["+newLines.get(prvIndex).getQty()+"]");
							logger.info("odLine.getQty()["+odLine.getQty()+"]");
						    //Wit Edit 	
							line.setQty(newLines.get(prvIndex).getQty() + odLine.getQty());
							//Wit Edit
							if( !Utils.isNull(odLine.getUom2().getCode()).equals("")){
							   line.setQty2(newLines.get(prvIndex).getQty() + odLine.getQty());
							}
						} else {
							logger.info("xxx3");
							line.setQty(odLine.getQty());
							line.setQty2(odLine.getQty());
						}

						line.setId(odLine.getId());
						line.setUom2(odLine.getUom());
						line.setUom(odLine.getUom());
						line.setPrice2(odLine.getPrice());
						
						
						line.setLineAmount2(odLine.getLineAmount());
						line.setDiscount2(odLine.getDiscount());
						line.setTotalAmount2(odLine.getTotalAmount());
						line.setActiveLabel(odLine.getActiveLabel());
						line.setArInvoiceNo(odLine.getArInvoiceNo());
						line.setLineNo(odLine.getLineNo());
						line.setTripNo(odLine.getTripNo());
						line.setOrderId(odLine.getOrderId());
						line.setPayment(odLine.getPayment());
						line.setProduct(new MProduct().find(String.valueOf(odLine.getProduct().getId())));
						line.setPromotion(odLine.getPromotion());
						line.setRequestDate(odLine.getRequestDate());
						line.setShippingDate(odLine.getShippingDate());
						line.setVatAmount(odLine.getVatAmount() + newLines.get(prvIndex).getVatAmount());
						line.setVatAmount2(odLine.getVatAmount());
						line.setLineAmount(odLine.getLineAmount() + newLines.get(prvIndex).getLineAmount());
						line.setDiscount(odLine.getDiscount() + newLines.get(prvIndex).getDiscount());
						line.setTotalAmount(odLine.getTotalAmount() + newLines.get(prvIndex).getTotalAmount());

						// Set value to previous
						line.setUom1(newLines.get(prvIndex).getUom1());
						line.setPrice1(newLines.get(prvIndex).getPrice1());
						
						//Edit 30/10/2557
						//line.setQty1(line.getQty1() + newLines.get(prvIndex).getQty1()); 
						line.setQty1(newLines.get(prvIndex).getQty1()+ odLine.getQty1());
						
						line.setLineAmount1(newLines.get(prvIndex).getLineAmount1());
						line.setDiscount1(newLines.get(prvIndex).getDiscount1());
						line.setTotalAmount1(newLines.get(prvIndex).getTotalAmount1());
						line.setVatAmount1(newLines.get(prvIndex).getVatAmount1());

						if (checkFullUOM(newLines.get(prvIndex).getFullUom())) line.setFullUom(newLines.get(prvIndex).getFullUom());
						else line.setFullUom(newLines.get(prvIndex).getFullUom() + odLine.getUom().getCode());
						
						/**option **/
						line.setOrderNo(odLine.getOrderNo());
						line.setCustomerName(odLine.getCustomerName());
						line.setCustomerCode(odLine.getCustomerCode());
						line.setOrderDate(odLine.getOrderDate());
						line.setStatus(odLine.getStatus());
						
						
						
						newLines.set(prvIndex, line);
					} else {
						// Aneak.t 24/01/2011
						if (odLine.getIscancel() != null && odLine.getIscancel().equals("Y")) {
							continue;
						}
						line.setId(odLine.getId());
						line.setActiveLabel(odLine.getActiveLabel());
						line.setArInvoiceNo(odLine.getArInvoiceNo());
						line.setDiscount(odLine.getDiscount1());
						line.setLineNo(odLine.getLineNo());
						line.setTripNo(odLine.getTripNo());
						line.setOrderId(odLine.getOrderId());
						line.setPayment(odLine.getPayment());
						line.setProduct(new MProduct().find(String.valueOf(odLine.getProduct().getId())));
						line.setPromotion(odLine.getPromotion());
						line.setRequestDate(odLine.getRequestDate());
						line.setShippingDate(odLine.getShippingDate());
						line.setVatAmount(odLine.getVatAmount());
						line.setUom(odLine.getUom());
						line.setPrice(odLine.getPrice());
						line.setFullUom(odLine.getFullUom());
						line.setQty(line.getQty() + odLine.getQty());
						line.setLineAmount(odLine.getLineAmount());
						line.setDiscount(odLine.getDiscount());
						line.setTotalAmount(odLine.getTotalAmount());
						
						
						if (odLine.getProduct().getUom().getId().equals(odLine.getUom().getId())) {
							line.setVatAmount1(odLine.getVatAmount());
							line.setUom1(odLine.getUom());
							odLine.setUom1(odLine.getUom());
							line.setPrice1(odLine.getPrice());
							line.setQty1(line.getQty1() + odLine.getQty());
							line.setLineAmount1(line.getLineAmount1() + odLine.getLineAmount());
							line.setDiscount1(line.getDiscount1() + odLine.getDiscount());
							line.setTotalAmount1(line.getTotalAmount1() + odLine.getTotalAmount());
						} else {
							logger.info("xxx4");
							line.setVatAmount2(odLine.getVatAmount());
							line.setUom2(odLine.getUom());
							odLine.setUom2(odLine.getUom());
							line.setPrice2(odLine.getPrice());
							line.setQty2(line.getQty2() + odLine.getQty());
							line.setLineAmount2(line.getLineAmount2() + odLine.getLineAmount());
							line.setDiscount2(line.getDiscount2() + odLine.getDiscount());
							line.setTotalAmount2(line.getTotalAmount2() + odLine.getTotalAmount());
						}
						if (odLine.getFullUom() == null) {
							line.setFullUom(ConvertNullUtil.convertToString(odLine.getUom1().getCode()) + "/"
									+ ConvertNullUtil.convertToString(odLine.getUom2().getCode()));
						} else {
							line.setFullUom(odLine.getFullUom());
						}
						
						/**option **/
						line.setOrderNo(odLine.getOrderNo());
						line.setCustomerName(odLine.getCustomerName());
						line.setCustomerCode(odLine.getCustomerCode());
						line.setOrderDate(odLine.getOrderDate());
						line.setStatus(odLine.getStatus());
						
					
						newLines.add(line);
						prvIndex++;
					}
				}//if
				
				debug.info("*** result product["+line.getProduct().getCode()+"]" +
						" fullUOM["+line.getFullUom()+"]" +
						" UOM1["+line.getUom1().getCode()+"]" +
						" UOM2["+line.getUom2().getCode()+"]" +
						" qty["+line.getQty()+"]" +
						" qty1["+line.getQty1()+"]" +
						" qty2["+line.getQty2()+"]***");

				i++;
			}//while
		} catch (Exception e) {
			throw e;
		}
		return newLines;
	}
	
	private OrderLine getUOM(Connection conn,String pricelistID,String pID ){
	
		Statement stmt = null;
		ResultSet rst = null;
		String uom1 = "";
		String uom2 = "";
		double priceUom1 = 0;
		double priceUom2 = 0;
		
		OrderLine lines = new OrderLine();
		try{
			String sql = "SELECT um.UOM_ID, pp.PRICE " +
						" FROM m_product_price pp " +
						" LEFT JOIN m_uom um ON pp.UOM_ID = um.UOM_ID " +
						" WHERE pp.PRODUCT_ID = " + pID + 
						" AND pp.PRICELIST_ID = " + pricelistID +
						" AND pp.ISACTIVE = 'Y' " +
						" AND um.ISACTIVE = 'Y' " +
						" GROUP BY pp.UOM_ID ";
			
			debug.debug("UOM sql:"+sql);
			
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			Product product = new MProduct().find(pID);
			
			while(rst.next()){
				if(product.getUom().getId().equals(rst.getString("UOM_ID"))){
					uom1 = rst.getString("UOM_ID");
					priceUom1 = rst.getDouble("PRICE");
				}else{
					uom2 = rst.getString("UOM_ID");
					priceUom2 = rst.getDouble("PRICE");
				}
			}
			
			UOM uomM1 = new UOM();
			uomM1.setId(uom1);
			uomM1.setCode(uom1);
			
			UOM uomM2 = new UOM();
			uomM2.setId(uom2);
			uomM2.setCode(uom2);
			
			lines.setUom1(uomM1);
			lines.setUom2(uomM2);
			lines.setPrice1(priceUom1);
			lines.setPrice2(priceUom2);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				rst.close();
			}catch(Exception e2){}
			try{
				stmt.close();
			}catch(Exception e2){}
			
		}
		return lines;
	}

	public List<OrderLine> fillLinesShowBlankUOM(Connection conn,String pricelistID, List<OrderLine> lines){
		List<OrderLine> newLines = new ArrayList<OrderLine>();
		try{
            debug.debug(" ****Start fillLinesShowBlankUOM****");
			for(OrderLine l:lines){
				debug.debug("uom1["+l.getUom1().getCode()+"]uom2["+Utils.isNull(l.getUom1().getCode())+"]");
				
				if( Utils.isNull(l.getUom1().getCode()).equals("") ||  Utils.isNull(l.getUom2().getCode()).equals("") ){
				   debug.debug("getUom product:"+l.getProduct().getCode());
				   
				   OrderLine lineUom = getUOM(conn,pricelistID ,String.valueOf(l.getProduct().getId()));
				   
				   if(Utils.isNull(l.getUom1().getCode()).equals("")){
					 if(lineUom != null){
						 l.setUom1(lineUom.getUom1());
						 l.setPrice1(lineUom.getPrice1());
					 }
				   }
					
                   if(Utils.isNull(l.getUom2().getCode()).equals("")){
                	 if(lineUom != null){
  						l.setUom2(lineUom.getUom2());
  						l.setPrice2(lineUom.getPrice2());
  					  }	
				   }
                    l.setFullUom(l.getUom1().getCode()+"/"+l.getUom2().getCode());
				    newLines.add(l);
				}else{
					newLines.add(l);
				}
			}
			
			debug.debug(" **** end fillLinesShowBlankUOM****");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
	
		}
		return newLines;
	}

	private boolean checkFullUOM(String fullUOM) throws Exception {
		if (fullUOM == null || fullUOM.equals("")) { return false; }

		try {
			String[] arrFullUOM = fullUOM.split("/");

			if (arrFullUOM.length == 2) { return true; }
		} catch (Exception e) {
			throw e;
		}
		return false;
	}


	/**
	 * Create Auto Receipt
	 * 
	 * @param receipt
	 * @param order
	 * @param lines
	 * @param bys
	 * @param creditCardExpired
	 * @param user
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean createAutoReceipt(Receipt receipt, Order order, List<OrderLine> lines, List<ReceiptBy> bys,
			String creditCardExpired, User user, Connection conn) throws Exception {

		MReceipt mReceipt = new MReceipt();

		// Generate Receipt Header
		
		// Check Set Receipt Date Before Default for Key In Delay Case
		if(receipt.getReceiptDate() == null || receipt.getReceiptDate().trim().length()==0)
			receipt.setReceiptDate(DateToolsUtil.convertToString(new Date()));// Current Date
		
		// Order Type
		receipt.setOrderType(order.getOrderType());
		// Customer
		receipt.setCustomerId(order.getCustomerId());
		receipt.setCustomerName(order.getCustomerName());
		// Receipt Amount
		receipt.setReceiptAmount(0);
		// Sales Reps --current user who's create record--
		receipt.setSalesRepresent(user);
		// Status
		receipt.setInterfaces("N");
		receipt.setDocStatus(Receipt.DOC_SAVE);
		// Prepaid Flag
		receipt.setPrepaid("Y");

		// Apply
		receipt.setApplyAmount(0);

		// desc
		receipt.setDescription("Generate from Order " + order.getOrderNo());

		mReceipt.saveWOCheckDup(receipt, user.getId(), conn);

		// re-calculate receipt & apply
		double receiptAmount = 0;
		double applyAmount = 0;
		for (ReceiptBy by : bys) {
			receiptAmount += by.getReceiptAmount();
			applyAmount += Double.parseDouble(by.getAllPaid());
		}

		// Receipt Amount
		receipt.setReceiptAmount(receiptAmount);
		// Apply
		receipt.setApplyAmount(applyAmount);

		// re-save
		mReceipt.saveWOCheckDup(receipt, user.getId(), conn);

		// Generate Line (one Line)
		ReceiptLine rLine = new ReceiptLine();
		// Status
		rLine.setLineNo(1);
		rLine.setReceiptId(receipt.getId());
		rLine.setOrder(order);
		// Order No
		rLine.setArInvoiceNo("");
		rLine.setSalesOrderNo("");
		// desc
		rLine.setDescription("Generate from Order " + order.getOrderNo());
		// Amount
		rLine.setInvoiceAmount(order.getNetAmount());
		rLine.setCreditAmount(order.getNetAmount());
		rLine.setPaidAmount(applyAmount);
		rLine.setRemainAmount(order.getNetAmount() - applyAmount);

		new MReceiptLine().save(rLine, user.getId(), conn);

		// Generate Receipt By
		MReceiptBy mReceiptBy = new MReceiptBy();
		receiptAmount = 0;
		double paidAmount = 0;
		double remainAmount = 0;

		MReceiptMatch mReceiptMatch = new MReceiptMatch();
		ReceiptMatch match;
		for (ReceiptBy by : bys) {
			paidAmount = 0;
			receiptAmount = by.getReceiptAmount();
			paidAmount = Double.parseDouble(by.getAllPaid());
			remainAmount = receiptAmount - paidAmount;
			by.setPaidAmount(paidAmount);
			by.setRemainAmount(remainAmount);
			by.setReceiptId(receipt.getId());
			if (by.getPaymentMethod().equalsIgnoreCase("CR")) {
				by.setCreditcardExpired(creditCardExpired);
			} else {
				by.setCreditcardExpired("");
			}
			mReceiptBy.save(by, user.getId(), conn);

			// apply match
			match = new ReceiptMatch();
			match.setReceiptLineId(rLine.getId());
			match.setReceiptById(by.getId());
			match.setReceiptId(receipt.getId());
			match.setPaidAmount(Double.parseDouble(by.getAllPaid()));
			mReceiptMatch.save(match, user.getId(), conn);
		}

		// re-save
		for (OrderLine ol : lines) {
			ol.setPayment("Y");
			ol.setExported("N");
			ol.setNeedExport("N");
			ol.setInterfaces("N");
			new MOrderLine().save(ol, user.getId(), conn);
		}
		// re-save
		order.setPayment("Y");
		
		if("N".equals(receipt.getIsPDPaid()))
			order.setIsCash("N");
		else
			order.setIsCash("Y");
		
		new MOrder().save(order, user.getId(), conn);

		// Trx History
		TrxHistory trx = new TrxHistory();
		trx.setTrxModule(TrxHistory.MOD_RECEIPT);
		trx.setTrxType(TrxHistory.TYPE_INSERT);
		trx.setRecordId(receipt.getId());
		trx.setUser(user);
		new MTrxHistory().save(trx, user.getId(), conn);
		// Trx History --end--
		return true;
	}

}
