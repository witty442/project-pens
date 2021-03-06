package com.isecinc.pens.process.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberProduct;
import com.isecinc.pens.bean.MemberRenew;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MMemberRenew;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.model.MUOM;

/**
 * Order Process Class
 * 
 * @author Atiz.b
 * @version $Id: OrderProcess.java,v 1.0 24/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderProcess {
	private Logger logger = Logger.getLogger("PENS");

	/*
	 * Generate Sale Order Line from member
	 */
	public List<OrderLine> generateLine(Member member, Order order, String defineFirstRound) throws Exception {
		logger.debug("Generate Member Lines...");

		//Fixed TotalQtyBenical From C_refernce
		int maxQtyBenical = 0;
		List<References> maxQtyBenicalList = InitialReferences.getReferenes().get(InitialReferences.MAX_QTY_BENICAL);
		if(maxQtyBenicalList != null && maxQtyBenicalList.size() > 0){
			maxQtyBenical = Utils.convertStrToInt(((References)maxQtyBenicalList.get(0)).getKey());
		}
		
		List<OrderLine> listOrderLine = new ArrayList<OrderLine>();

		// int months = Integer.parseInt(member.getMemberType()) * 3;
		int months = Integer.parseInt(member.getMemberType());
		int days = months * 30;
		int rounds = days / (new Double(member.getRoundTrip()).intValue());
		// int qty = rounds * member.getOrderAmountPeriod();

		OrderLine orderLine = null;
		int lineNo = 1;
		int tripNo = 1;
		//int tripNoTemp = 1;
		int j = -1;
		//int k = -1;
		int size = member.getMemberProducts().size();
		MProductPrice productPrice = new MProductPrice();
		List<ProductPrice> pps;
		List<MemberRenew> mRenews = new MMemberRenew().lookUp(member.getId());
		List<MemberRenew> mRenewsMax = new MMemberRenew().lookUpLast(member.getId());
		
		MUOM muom = new MUOM();
		MProduct mProduct = new MProduct();

		BigDecimal sumTotalAmount = new BigDecimal(0);
		BigDecimal sumTotal = new BigDecimal(0);
		
		int totalQty = 0;
		
		for (int i = 0; i < rounds; i++) {
			for (MemberProduct mp : member.getMemberProducts()) {
				pps = null;
				//boolean ckFirst;
				
				orderLine = new OrderLine();
				orderLine.setPromotion("N");
				orderLine.setPromotion1("N");
				orderLine.setTripNo(tripNo);
				orderLine.setProduct(mProduct.find(String.valueOf(mp.getProduct().getId())));
				orderLine.setQty(mp.getOrderQty());
				orderLine.setUom(muom.find(String.valueOf(mp.getUomId())));

				//count totalQty
				totalQty += orderLine.getQty();
				
				// get Product Price
				pps = productPrice.getCurrentPrice(String.valueOf(orderLine.getProduct().getId()), String.valueOf(order
						.getPriceListId()), orderLine.getUom().getId());

				for (ProductPrice pp : pps) {
					orderLine.setPrice(pp.getPrice());
				}
				
				if(!"Y".equalsIgnoreCase(member.getIsFreeOfChart())){
					orderLine.setLineAmount(orderLine.getQty() * orderLine.getPrice());
					orderLine.setDiscount(0);
					orderLine.setTotalAmount(orderLine.getLineAmount() - orderLine.getDiscount());
					
					if(member.getPaymentType() == 2 ){
						
						orderLine.setNeedBill(new BigDecimal(orderLine.getTotalAmount()).multiply(new BigDecimal(1.07)).setScale(0, RoundingMode.HALF_UP).doubleValue());
					}
					
					sumTotalAmount = sumTotalAmount.add(new BigDecimal(orderLine.getTotalAmount()).setScale(2, RoundingMode.HALF_UP));
				}else{
					orderLine.setLineAmount(orderLine.getQty() * orderLine.getPrice());
					orderLine.setDiscount(orderLine.getQty() * orderLine.getPrice());
					orderLine.setTotalAmount(0);
					orderLine.setNeedBill(0);
					orderLine.setPromotion1("Y");
				}
				
				orderLine.setPaymentMethod(member.getPaymentMethod());
				
				if (i == 0) {

					if (ConvertNullUtil.convertToString(defineFirstRound).length() > 0) {
						logger.debug("Case 1 Define First Round Day");
						// Define First Round Day
						orderLine.setShippingDate(defineFirstRound);
						orderLine.setRequestDate(defineFirstRound);
					} else {
						// Common Calculate on Register Date or Renew Date
						if (mRenews.size() == 0) {
							logger.debug("Case 2 renew size =0");
							if(!member.getFirstDeliverlyDate().isEmpty()){
								
								logger.debug("Case 2.1 FirstDeliverlyDate not null");
								
								orderLine.setShippingDate(calShippingDate(order.getShippingDay(), member.getFirstDeliverlyDate(),
										i));
								orderLine.setRequestDate(calShippingDate(order.getShippingDay(), member.getFirstDeliverlyDate(),
										i));
							}else{
								logger.debug("Case 2.2 FirstDeliverlyDate null");
								orderLine.setShippingDate(calShippingDate(order.getShippingDay(), member.getRegisterDate(),
								i));
								
								orderLine.setRequestDate(calShippingDate(order.getShippingDay(), member.getRegisterDate(),
								i));
							}
							
						} else {							
							logger.debug("Case 3 renew size != 0");
							logger.debug("renewDate:"+ mRenews.get(mRenewsMax.size() - 1).getRenewedDate());
							
							orderLine.setShippingDate(calShippingDate(order.getShippingDay(), mRenews.get(
									mRenewsMax.size() - 1).getRenewedDate(), i));
							
							orderLine.setRequestDate(calShippingDate(order.getShippingDay(), mRenews.get(
									mRenewsMax.size() - 1).getRenewedDate(), i));
						}
					}
				} else {
					orderLine.setShippingDate(addDate(listOrderLine.get(j).getShippingDate(),7 * calWeekByRoundtrip(member.getRoundTrip())));
					orderLine.setRequestDate(addDate(listOrderLine.get(j).getRequestDate(),7 * calWeekByRoundtrip(member.getRoundTrip())));
				}
				orderLine.setLineNo(lineNo++);
				listOrderLine.add(orderLine);

			}//for 2
			j = j + size;
			tripNo++;
			
			//** Check maxQty Benical not over 90 **/
			if(maxQtyBenical !=0 && totalQty >= maxQtyBenical){
				logger.debug("totalQty["+totalQty+"]maxQtyBenical["+maxQtyBenical+"]");
				break;
			}
			
		}//for 1

		//int paymentType = member.getPaymentType();
		if(!"Y".equals(member.getIsFreeOfChart())){
			if(member.getPaymentType() == 1 ){
	
				BigDecimal totalVat = sumTotalAmount.multiply(new BigDecimal(Double.valueOf(member.getVatCode()))).divide(new BigDecimal(100));
				sumTotal = sumTotalAmount.add(totalVat.setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
							
				listOrderLine.get(0).setNeedBill(sumTotal.setScale(0, RoundingMode.HALF_UP).doubleValue());
				//orderLine.setNeedBill(new BigDecimal(orderLine.getTotalAmount()).setScale(0, RoundingMode.HALF_UP).doubleValue());
			}
		}
		
		return listOrderLine;
	}

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
	public String calShippingDate(String day, String registerDate, int week) throws Exception {
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
	public int calWeekByRoundtrip(String roundTrip) throws Exception {
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
	public String addDate(String date, int dayOfDate) throws Exception {
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
				if (user.getRole().getKey().equals(User.DD)) {
					if (isVIP.equalsIgnoreCase("N")) {
						// Line default.
						line = new OrderLine();
						line.setId(odLine.getId());
						line.setProduct(new MProduct().find(String.valueOf(odLine.getProduct().getId())));
						line.setActiveLabel(odLine.getActiveLabel());
						line.setArInvoiceNo(odLine.getArInvoiceNo());
						line.setDiscount(odLine.getDiscount());
						line.setLineAmount(odLine.getLineAmount());
						line.setLineNo(odLine.getLineNo());
						line.setOrderId(odLine.getOrderId());
						line.setPayment(odLine.getPayment());
						line.setPrice(odLine.getPrice());
						line.setPromotion(odLine.getPromotion());
						line.setQty(odLine.getQty());
						line.setRequestDate(odLine.getRequestDate());
						line.setShippingDate(odLine.getShippingDate());
						line.setTotalAmount(odLine.getTotalAmount());
						line.setUom(odLine.getUom());
						line.setVatAmount(odLine.getVatAmount());
						line.setTripNo(odLine.getTripNo());
						line.setFullUom(odLine.getFullUom());
						line.setPromotion1(odLine.getPromotion1());
						line.setNeedBill(odLine.getNeedBill());
						line.setPaymentMethod(odLine.getPaymentMethod());
						newLines.add(line);
					} else {
						// VIP Line default 1.
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
						line.setTripNo(1);
						line.setFullUom(odLine.getFullUom());
						line.setPromotion1(odLine.getPromotion1());
						line.setNeedBill(odLine.getNeedBill());
						newLines.add(line);
					}

				} else {
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
						line.setPromotion1(odLine.getPromotion1());
						line.setNeedBill(odLine.getNeedBill());
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
						line.setPromotion1(odLine.getPromotion1());
						line.setNeedBill(odLine.getNeedBill());
						newLines.add(line);
					}
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
				logger.debug("odLine fullUom:"+odLine.getFullUom());
				// First add.
				if (firstAdd) {
					// Aneak.t 24/01/2011
					if (odLine.getIscancel() != null && odLine.getIscancel().equals("Y")) {
						continue;
					}
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

					// Aneak.t 24/01/2011
					if (odLine.getIscancel() != null && odLine.getIscancel().equals("Y")) {
						continue;
					}
					line = new OrderLine();
					if (odLine.getProduct().getCode().equals(lines.get(i - 1).getProduct().getCode())
							&& odLine.getPromotion().equals(lines.get(i - 1).getPromotion())
							&& odLine.getShippingDate().equals(lines.get(i - 1).getShippingDate())
							&& odLine.getRequestDate().equals(lines.get(i - 1).getRequestDate())) {
						line.setId(odLine.getId());
						line.setUom2(odLine.getUom());
						line.setUom(odLine.getUom());
						line.setPrice2(odLine.getPrice());
						if (odLine.getPromotion().equals("Y")) {
							line.setQty(newLines.get(prvIndex).getQty() + odLine.getQty());
							line.setQty2(newLines.get(prvIndex).getQty() + odLine.getQty());
						} else {
							line.setQty(odLine.getQty());
							line.setQty2(odLine.getQty());
						}

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
						line.setQty1(line.getQty1() + newLines.get(prvIndex).getQty1());
						line.setLineAmount1(newLines.get(prvIndex).getLineAmount1());
						line.setDiscount1(newLines.get(prvIndex).getDiscount1());
						line.setTotalAmount1(newLines.get(prvIndex).getTotalAmount1());
						line.setVatAmount1(newLines.get(prvIndex).getVatAmount1());

						if (checkFullUOM(newLines.get(prvIndex).getFullUom())) line.setFullUom(newLines.get(prvIndex)
								.getFullUom());
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
						// line.setVatAmount(odLine.getVatAmount());
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
				}
				
				logger.debug(line.getUom1());
				logger.debug(line.getUom1());
				logger.debug(line.getProduct().getCode());
				logger.debug("fullUOM:"+line.getFullUom());
				i++;
			}
		} catch (Exception e) {
			throw e;
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
	 * Check sale order in register date or renew date and member expire date.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isWithinDateRange(Member member) throws Exception {
		boolean result = false;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String regisDate = "";
		String expireDate = "";

		try {
			conn = new DBCPConnectionProvider().getConnection(conn);

			// check renew date, get register date and expire date from m_member_renew
			sql.delete(0, sql.length());
			sql.append(" SELECT MAX(mr.RENEWED_DATE) AS RENEWED_DATE ");
			sql.append(" FROM m_member_renew mr ");
			sql.append(" WHERE mr.CUSTOMER_ID = " + member.getId());

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if (rst.getDate("RENEWED_DATE") != null)
					regisDate = DateToolsUtil.convertToString(rst.getDate("RENEWED_DATE"));
			}

			// if not have renew date, get register date and expire date from m_customer
			if (regisDate.equals("")) {
				regisDate = member.getRegisterDate();
			}
			expireDate = member.getExpiredDate();

			int count = 0;
			sql.delete(0, sql.length());
			sql.append(" SELECT COUNT(*) AS COUNT ");
			sql.append(" FROM t_order t ");
			sql.append(" WHERE t.ORDER_DATE >= '" + DateToolsUtil.convertToTimeStamp(regisDate) + "'");
			sql.append(" AND t.ORDER_DATE <= '" + DateToolsUtil.convertToTimeStamp(expireDate) + "'");
			sql.append(" AND t.CUSTOMER_ID = " + member.getId());

			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				count = rst.getInt("COUNT");
				if (count > 0) {
					result = true;
				}
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
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return result;
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
