package com.isecinc.pens.process.order;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class OrderProcess {
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
						line.setTaxable(odLine.getTaxable());
						line.setSellingPrice(odLine.getSellingPrice());
						line.setModifierLineId(odLine.getModifierLineId());
						line.setProductNonBme(odLine.getProductNonBme());
						line.setPriceAfDiscount(odLine.getPriceAfDiscount());
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
						line.setTaxable(odLine.getTaxable());
						line.setSellingPrice(odLine.getSellingPrice());
						line.setModifierLineId(odLine.getModifierLineId());
						line.setProductNonBme(odLine.getProductNonBme());
						line.setPriceAfDiscount(odLine.getPriceAfDiscount());
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
	 * Wit Edit :01/06/2563 
	 * Not check UOM2  -default uom1 all time
	 */
public List<OrderLine> fillLinesShow(List<OrderLine> lines) throws Exception {
		List<OrderLine> newLinesList = new ArrayList<OrderLine>();
		OrderLine chkLine = null;
		OrderLine line = null;
		int i = 0;
		int prvIndex = 0;
		boolean firstAdd = true;
		try {
			while (i < lines.size()) {
				chkLine = lines.get(i);
				logger.debug("totalAmount:"+chkLine.getTotalAmount());
				logger.debug("totalAmount1:"+chkLine.getTotalAmount1());
				
				// First add.
				if (firstAdd) {
					// Aneak.t 24/01/2011
					if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
						continue;
					}
					line = new OrderLine();
					line.setId(chkLine.getId());
					line.setActiveLabel(chkLine.getActiveLabel());
					line.setArInvoiceNo(chkLine.getArInvoiceNo());
					line.setLineNo(chkLine.getLineNo());
					line.setOrderId(chkLine.getOrderId());
					line.setPayment(chkLine.getPayment());
					line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
					line.setPromotion(chkLine.getPromotion());
					line.setRequestDate(chkLine.getRequestDate());
					line.setShippingDate(chkLine.getShippingDate());
					line.setTripNo(chkLine.getTripNo());

					line.setVatAmount(chkLine.getVatAmount());
					line.setPrice(chkLine.getPrice());
					line.setUom(chkLine.getUom());
					line.setQty(chkLine.getQty());
					line.setLineAmount(chkLine.getLineAmount());
					line.setDiscount(chkLine.getDiscount());
					line.setTotalAmount(chkLine.getTotalAmount());
					line.setSellingPrice(chkLine.getSellingPrice());
					line.setModifierLineId(chkLine.getModifierLineId());
					line.setProductNonBme(chkLine.getProductNonBme());
					line.setPriceAfDiscount(chkLine.getPriceAfDiscount());
					
					//set uom1 all time don't use uom2 
					//if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) {
						line.setVatAmount1(chkLine.getVatAmount());
						line.setUom1(chkLine.getUom());
						chkLine.setUom1(chkLine.getUom());
						line.setPrice1(chkLine.getPrice());
						line.setQty1(line.getQty1() + chkLine.getQty());
						line.setLineAmount1(line.getLineAmount1() + chkLine.getLineAmount());
						line.setDiscount1(line.getDiscount1() + chkLine.getDiscount());
						line.setTotalAmount1(line.getTotalAmount1() + chkLine.getTotalAmount());
					/*} else {
						line.setVatAmount2(chkLine.getVatAmount());
						line.setUom2(chkLine.getUom());
						chkLine.setUom2(chkLine.getUom());
						line.setPrice2(chkLine.getPrice());
						line.setQty2(line.getQty2() + chkLine.getQty());
						line.setLineAmount2(line.getLineAmount2() + chkLine.getLineAmount());
						line.setDiscount2(line.getDiscount2() + chkLine.getDiscount());
						line.setTotalAmount2(line.getTotalAmount2() + chkLine.getTotalAmount());
					}*/
					if (chkLine.getFullUom() == null) {
						if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())){
							line.setFullUom(ConvertNullUtil.convertToString(chkLine.getUom().getCode() + "/"));
						}else {
							//line.setFullUom(ConvertNullUtil.convertToString("/" + chkLine.getUom().getCode()));
						}
					} else {
						line.setFullUom(chkLine.getFullUom());
					}
					/**option **/
					line.setOrderNo(chkLine.getOrderNo());
					line.setCustomerName(chkLine.getCustomerName());
					line.setCustomerCode(chkLine.getCustomerCode());
					line.setOrderDate(chkLine.getOrderDate());
					line.setStatus(chkLine.getStatus());
					line.setTaxable(chkLine.getTaxable());
					
					newLinesList.add(line);
					firstAdd = false;
					prvIndex = 0;
				} else {
					// Update previous line if repeat product code & promotion.

					if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
						continue;
					}
					line = new OrderLine();
					
					if (chkLine.getProduct().getCode().equals(lines.get(i - 1).getProduct().getCode())
							&& chkLine.getPromotion().equals(lines.get(i - 1).getPromotion())
							&& chkLine.getShippingDate().equals(lines.get(i - 1).getShippingDate())
							&& chkLine.getRequestDate().equals(lines.get(i - 1).getRequestDate())) {
						
						line.setId(chkLine.getId());
						//line.setUom2(chkLine.getUom());
						line.setUom(chkLine.getUom());
						//line.setPrice2(chkLine.getPrice());
						
						if (chkLine.getPromotion().equals("Y")) {
							//logger.info("add newLines.get(prvIndex).getQty() + chkLine.getQty()");
							line.setQty(newLinesList.get(prvIndex).getQty() + chkLine.getQty());
							line.setQty2(newLinesList.get(prvIndex).getQty() + chkLine.getQty()); /** OLD CODE **/
							 //line.setQty2(newLinesList.get(prvIndex).getQty2() + chkLine.getQty()); /** NEW **/
						} else {
							line.setQty(chkLine.getQty());
							line.setQty2(chkLine.getQty());
						}

						line.setLineAmount2(chkLine.getLineAmount());
						line.setDiscount2(chkLine.getDiscount());
						line.setTotalAmount2(chkLine.getTotalAmount());
						line.setActiveLabel(chkLine.getActiveLabel());
						line.setArInvoiceNo(chkLine.getArInvoiceNo());
						line.setLineNo(chkLine.getLineNo());
						line.setTripNo(chkLine.getTripNo());
						line.setOrderId(chkLine.getOrderId());
						line.setPayment(chkLine.getPayment());
						line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
						line.setPromotion(chkLine.getPromotion());
						line.setRequestDate(chkLine.getRequestDate());
						line.setShippingDate(chkLine.getShippingDate());
						line.setVatAmount(chkLine.getVatAmount() + newLinesList.get(prvIndex).getVatAmount());
						line.setVatAmount2(chkLine.getVatAmount());
						line.setLineAmount(chkLine.getLineAmount() + newLinesList.get(prvIndex).getLineAmount());
						line.setDiscount(chkLine.getDiscount() + newLinesList.get(prvIndex).getDiscount());
						line.setTotalAmount(chkLine.getTotalAmount() + newLinesList.get(prvIndex).getTotalAmount());
						line.setSellingPrice(chkLine.getSellingPrice() + newLinesList.get(prvIndex).getSellingPrice());
						line.setModifierLineId(chkLine.getModifierLineId());
						line.setProductNonBme(chkLine.getProductNonBme());
						line.setPriceAfDiscount(chkLine.getPriceAfDiscount());
						
						// Set value to previous
						line.setUom1(newLinesList.get(prvIndex).getUom1());
						line.setPrice1(newLinesList.get(prvIndex).getPrice1());
						line.setQty1(line.getQty1() + newLinesList.get(prvIndex).getQty1());
						line.setLineAmount1(newLinesList.get(prvIndex).getLineAmount1());
						line.setDiscount1(newLinesList.get(prvIndex).getDiscount1());
						line.setTotalAmount1(newLinesList.get(prvIndex).getTotalAmount1());
						line.setVatAmount1(newLinesList.get(prvIndex).getVatAmount1());

						if (checkFullUOM(newLinesList.get(prvIndex).getFullUom())){
							line.setFullUom(newLinesList.get(prvIndex).getFullUom());
						}else{
							//line.setFullUom(newLinesList.get(prvIndex).getFullUom() + chkLine.getUom().getCode());
						}
						/**option **/
						line.setOrderNo(chkLine.getOrderNo());
						line.setCustomerName(chkLine.getCustomerName());
						line.setCustomerCode(chkLine.getCustomerCode());
						line.setOrderDate(chkLine.getOrderDate());
						line.setStatus(chkLine.getStatus());
						line.setTaxable(chkLine.getTaxable());
						
						newLinesList.set(prvIndex, line);
					} else {
						// Aneak.t 24/01/2011
						if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
							continue;
						}
						line.setId(chkLine.getId());
						line.setActiveLabel(chkLine.getActiveLabel());
						line.setArInvoiceNo(chkLine.getArInvoiceNo());
						line.setDiscount(chkLine.getDiscount1());
						line.setLineNo(chkLine.getLineNo());
						line.setTripNo(chkLine.getTripNo());
						line.setOrderId(chkLine.getOrderId());
						line.setPayment(chkLine.getPayment());
						line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
						line.setPromotion(chkLine.getPromotion());
						line.setRequestDate(chkLine.getRequestDate());
						line.setShippingDate(chkLine.getShippingDate());
						// line.setVatAmount(chkLine.getVatAmount());
						line.setVatAmount(chkLine.getVatAmount());
						line.setUom(chkLine.getUom());
						line.setPrice(chkLine.getPrice());
						line.setFullUom(chkLine.getFullUom());
						line.setQty(line.getQty() + chkLine.getQty());
						line.setLineAmount(chkLine.getLineAmount());
						line.setDiscount(chkLine.getDiscount());
						line.setTotalAmount(chkLine.getTotalAmount());
						line.setSellingPrice(chkLine.getSellingPrice());
						line.setModifierLineId(chkLine.getModifierLineId());
						line.setProductNonBme(chkLine.getProductNonBme());
						line.setPriceAfDiscount(chkLine.getPriceAfDiscount());
						
						//set uom1 all time don't use uom2 
						//if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) {
							line.setVatAmount1(chkLine.getVatAmount());
							line.setUom1(chkLine.getUom());
							chkLine.setUom1(chkLine.getUom());
							line.setPrice1(chkLine.getPrice());
							line.setQty1(line.getQty1() + chkLine.getQty());
							line.setLineAmount1(line.getLineAmount1() + chkLine.getLineAmount());
							line.setDiscount1(line.getDiscount1() + chkLine.getDiscount());
							line.setTotalAmount1(line.getTotalAmount1() + chkLine.getTotalAmount());
						/*} else {
							line.setVatAmount2(chkLine.getVatAmount());
							line.setUom2(chkLine.getUom());
							chkLine.setUom2(chkLine.getUom());
							line.setPrice2(chkLine.getPrice());
							line.setQty2(line.getQty2() + chkLine.getQty());
							line.setLineAmount2(line.getLineAmount2() + chkLine.getLineAmount());
							line.setDiscount2(line.getDiscount2() + chkLine.getDiscount());
							line.setTotalAmount2(line.getTotalAmount2() + chkLine.getTotalAmount());
						}*/
						if (chkLine.getFullUom() == null) {
							line.setFullUom(ConvertNullUtil.convertToString(chkLine.getUom1().getCode()) + "/");
									//+ ConvertNullUtil.convertToString(chkLine.getUom2().getCode()));
						} else {
							line.setFullUom(chkLine.getFullUom());
						}
						/**option **/
						line.setOrderNo(chkLine.getOrderNo());
						line.setCustomerName(chkLine.getCustomerName());
						line.setCustomerCode(chkLine.getCustomerCode());
						line.setOrderDate(chkLine.getOrderDate());
						line.setStatus(chkLine.getStatus());
						line.setTaxable(chkLine.getTaxable());
						
						newLinesList.add(line);
						prvIndex++;
					}
				}
				
				logger.debug("UOM1:"+line.getUom1());
				logger.debug("UOM2:"+line.getUom1());
				logger.debug("productCode:"+line.getProduct().getCode());
				logger.debug("fullUOM:"+line.getFullUom());
				
				i++;
			}
		} catch (Exception e) {
			throw e;
		}
		return newLinesList;
	}

public List<OrderLine> fillLinesShow_BK(List<OrderLine> lines) throws Exception {
	List<OrderLine> newLinesList = new ArrayList<OrderLine>();
	OrderLine chkLine = null;
	OrderLine line = null;
	int i = 0;
	int prvIndex = 0;
	boolean firstAdd = true;
	try {
		while (i < lines.size()) {
			chkLine = lines.get(i);
			logger.debug("totalAmount:"+chkLine.getTotalAmount());
			logger.debug("totalAmount1:"+chkLine.getTotalAmount1());
			
			// First add.
			if (firstAdd) {
				// Aneak.t 24/01/2011
				if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
					continue;
				}
				line = new OrderLine();
				line.setId(chkLine.getId());
				line.setActiveLabel(chkLine.getActiveLabel());
				line.setArInvoiceNo(chkLine.getArInvoiceNo());
				line.setLineNo(chkLine.getLineNo());
				line.setOrderId(chkLine.getOrderId());
				line.setPayment(chkLine.getPayment());
				line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
				line.setPromotion(chkLine.getPromotion());
				line.setRequestDate(chkLine.getRequestDate());
				line.setShippingDate(chkLine.getShippingDate());
				line.setTripNo(chkLine.getTripNo());

				line.setVatAmount(chkLine.getVatAmount());
				line.setPrice(chkLine.getPrice());
				line.setUom(chkLine.getUom());
				line.setQty(chkLine.getQty());
				line.setLineAmount(chkLine.getLineAmount());
				line.setDiscount(chkLine.getDiscount());
				line.setTotalAmount(chkLine.getTotalAmount());
				line.setSellingPrice(chkLine.getSellingPrice());
				line.setModifierLineId(chkLine.getModifierLineId());
				
				if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) {
					line.setVatAmount1(chkLine.getVatAmount());
					line.setUom1(chkLine.getUom());
					chkLine.setUom1(chkLine.getUom());
					line.setPrice1(chkLine.getPrice());
					line.setQty1(line.getQty1() + chkLine.getQty());
					line.setLineAmount1(line.getLineAmount1() + chkLine.getLineAmount());
					line.setDiscount1(line.getDiscount1() + chkLine.getDiscount());
					line.setTotalAmount1(line.getTotalAmount1() + chkLine.getTotalAmount());
				} else {
					line.setVatAmount2(chkLine.getVatAmount());
					line.setUom2(chkLine.getUom());
					chkLine.setUom2(chkLine.getUom());
					line.setPrice2(chkLine.getPrice());
					line.setQty2(line.getQty2() + chkLine.getQty());
					line.setLineAmount2(line.getLineAmount2() + chkLine.getLineAmount());
					line.setDiscount2(line.getDiscount2() + chkLine.getDiscount());
					line.setTotalAmount2(line.getTotalAmount2() + chkLine.getTotalAmount());
				}
				if (chkLine.getFullUom() == null) {
					if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) line
							.setFullUom(ConvertNullUtil.convertToString(chkLine.getUom().getCode() + "/"));
					else line.setFullUom(ConvertNullUtil.convertToString("/" + chkLine.getUom().getCode()));
				} else {
					line.setFullUom(chkLine.getFullUom());
				}
				/**option **/
				line.setOrderNo(chkLine.getOrderNo());
				line.setCustomerName(chkLine.getCustomerName());
				line.setCustomerCode(chkLine.getCustomerCode());
				line.setOrderDate(chkLine.getOrderDate());
				line.setStatus(chkLine.getStatus());
				line.setTaxable(chkLine.getTaxable());
				
				newLinesList.add(line);
				firstAdd = false;
				prvIndex = 0;
			} else {
				// Update previous line if repeat product code & promotion.

				if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
					continue;
				}
				line = new OrderLine();
				
				if (chkLine.getProduct().getCode().equals(lines.get(i - 1).getProduct().getCode())
						&& chkLine.getPromotion().equals(lines.get(i - 1).getPromotion())
						&& chkLine.getShippingDate().equals(lines.get(i - 1).getShippingDate())
						&& chkLine.getRequestDate().equals(lines.get(i - 1).getRequestDate())) {
					
					line.setId(chkLine.getId());
					line.setUom2(chkLine.getUom());
					line.setUom(chkLine.getUom());
					line.setPrice2(chkLine.getPrice());
					
					if (chkLine.getPromotion().equals("Y")) {
						//logger.info("add newLines.get(prvIndex).getQty() + chkLine.getQty()");
						line.setQty(newLinesList.get(prvIndex).getQty() + chkLine.getQty());
						line.setQty2(newLinesList.get(prvIndex).getQty() + chkLine.getQty()); /** OLD CODE **/
						 //line.setQty2(newLinesList.get(prvIndex).getQty2() + chkLine.getQty()); /** NEW **/
					} else {
						line.setQty(chkLine.getQty());
						line.setQty2(chkLine.getQty());
					}

					line.setLineAmount2(chkLine.getLineAmount());
					line.setDiscount2(chkLine.getDiscount());
					line.setTotalAmount2(chkLine.getTotalAmount());
					line.setActiveLabel(chkLine.getActiveLabel());
					line.setArInvoiceNo(chkLine.getArInvoiceNo());
					line.setLineNo(chkLine.getLineNo());
					line.setTripNo(chkLine.getTripNo());
					line.setOrderId(chkLine.getOrderId());
					line.setPayment(chkLine.getPayment());
					line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
					line.setPromotion(chkLine.getPromotion());
					line.setRequestDate(chkLine.getRequestDate());
					line.setShippingDate(chkLine.getShippingDate());
					line.setVatAmount(chkLine.getVatAmount() + newLinesList.get(prvIndex).getVatAmount());
					line.setVatAmount2(chkLine.getVatAmount());
					line.setLineAmount(chkLine.getLineAmount() + newLinesList.get(prvIndex).getLineAmount());
					line.setDiscount(chkLine.getDiscount() + newLinesList.get(prvIndex).getDiscount());
					line.setTotalAmount(chkLine.getTotalAmount() + newLinesList.get(prvIndex).getTotalAmount());
					line.setSellingPrice(chkLine.getSellingPrice() + newLinesList.get(prvIndex).getSellingPrice());
					line.setModifierLineId(chkLine.getModifierLineId());
					
					// Set value to previous
					line.setUom1(newLinesList.get(prvIndex).getUom1());
					line.setPrice1(newLinesList.get(prvIndex).getPrice1());
					line.setQty1(line.getQty1() + newLinesList.get(prvIndex).getQty1());
					line.setLineAmount1(newLinesList.get(prvIndex).getLineAmount1());
					line.setDiscount1(newLinesList.get(prvIndex).getDiscount1());
					line.setTotalAmount1(newLinesList.get(prvIndex).getTotalAmount1());
					line.setVatAmount1(newLinesList.get(prvIndex).getVatAmount1());

					if (checkFullUOM(newLinesList.get(prvIndex).getFullUom())) line.setFullUom(newLinesList.get(prvIndex)
							.getFullUom());
					else line.setFullUom(newLinesList.get(prvIndex).getFullUom() + chkLine.getUom().getCode());
					/**option **/
					line.setOrderNo(chkLine.getOrderNo());
					line.setCustomerName(chkLine.getCustomerName());
					line.setCustomerCode(chkLine.getCustomerCode());
					line.setOrderDate(chkLine.getOrderDate());
					line.setStatus(chkLine.getStatus());
					line.setTaxable(chkLine.getTaxable());
					
					newLinesList.set(prvIndex, line);
				} else {
					// Aneak.t 24/01/2011
					if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
						continue;
					}
					line.setId(chkLine.getId());
					line.setActiveLabel(chkLine.getActiveLabel());
					line.setArInvoiceNo(chkLine.getArInvoiceNo());
					line.setDiscount(chkLine.getDiscount1());
					line.setLineNo(chkLine.getLineNo());
					line.setTripNo(chkLine.getTripNo());
					line.setOrderId(chkLine.getOrderId());
					line.setPayment(chkLine.getPayment());
					line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
					line.setPromotion(chkLine.getPromotion());
					line.setRequestDate(chkLine.getRequestDate());
					line.setShippingDate(chkLine.getShippingDate());
					// line.setVatAmount(chkLine.getVatAmount());
					line.setVatAmount(chkLine.getVatAmount());
					line.setUom(chkLine.getUom());
					line.setPrice(chkLine.getPrice());
					line.setFullUom(chkLine.getFullUom());
					line.setQty(line.getQty() + chkLine.getQty());
					line.setLineAmount(chkLine.getLineAmount());
					line.setDiscount(chkLine.getDiscount());
					line.setTotalAmount(chkLine.getTotalAmount());
					line.setSellingPrice(chkLine.getSellingPrice());
					line.setModifierLineId(chkLine.getModifierLineId());
					
					if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) {
						line.setVatAmount1(chkLine.getVatAmount());
						line.setUom1(chkLine.getUom());
						chkLine.setUom1(chkLine.getUom());
						line.setPrice1(chkLine.getPrice());
						line.setQty1(line.getQty1() + chkLine.getQty());
						line.setLineAmount1(line.getLineAmount1() + chkLine.getLineAmount());
						line.setDiscount1(line.getDiscount1() + chkLine.getDiscount());
						line.setTotalAmount1(line.getTotalAmount1() + chkLine.getTotalAmount());
					} else {
						line.setVatAmount2(chkLine.getVatAmount());
						line.setUom2(chkLine.getUom());
						chkLine.setUom2(chkLine.getUom());
						line.setPrice2(chkLine.getPrice());
						line.setQty2(line.getQty2() + chkLine.getQty());
						line.setLineAmount2(line.getLineAmount2() + chkLine.getLineAmount());
						line.setDiscount2(line.getDiscount2() + chkLine.getDiscount());
						line.setTotalAmount2(line.getTotalAmount2() + chkLine.getTotalAmount());
					}
					if (chkLine.getFullUom() == null) {
						line.setFullUom(ConvertNullUtil.convertToString(chkLine.getUom1().getCode()) + "/"
								+ ConvertNullUtil.convertToString(chkLine.getUom2().getCode()));
					} else {
						line.setFullUom(chkLine.getFullUom());
					}
					/**option **/
					line.setOrderNo(chkLine.getOrderNo());
					line.setCustomerName(chkLine.getCustomerName());
					line.setCustomerCode(chkLine.getCustomerCode());
					line.setOrderDate(chkLine.getOrderDate());
					line.setStatus(chkLine.getStatus());
					line.setTaxable(chkLine.getTaxable());
					
					newLinesList.add(line);
					prvIndex++;
				}
			}
			
			logger.debug("UOM1:"+line.getUom1());
			logger.debug("UOM2:"+line.getUom1());
			logger.debug("productCode:"+line.getProduct().getCode());
			logger.debug("fullUOM:"+line.getFullUom());
			
			i++;
		}
	} catch (Exception e) {
		throw e;
	}
	return newLinesList;
}

//Summary For Line promotion by 
public List<OrderLine> fillLinesShowPromotion(List<OrderLine> lines) throws Exception {
	List<OrderLine> newLinesList = new ArrayList<OrderLine>();
	OrderLine chkLine = null;
	OrderLine line = null;
	int i = 0;
	int prvIndex = 0;
	boolean firstAdd = true;
	logger.debug("*********** fillLinesShowPromotion ******************************");
	try {
		while (i < lines.size()) {
			chkLine = lines.get(i);
			
			logger.debug("chkLine.getTotalAmount():"+chkLine.getTotalAmount());
			
			// First add.
			if (firstAdd) {
				// Aneak.t 24/01/2011
				if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
					continue;
				}
				line = new OrderLine();
				line.setId(chkLine.getId());
				line.setActiveLabel(chkLine.getActiveLabel());
				line.setArInvoiceNo(chkLine.getArInvoiceNo());
				line.setLineNo(chkLine.getLineNo());
				line.setOrderId(chkLine.getOrderId());
				line.setPayment(chkLine.getPayment());
				line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
				line.setPromotion(chkLine.getPromotion());
				line.setRequestDate(chkLine.getRequestDate());
				line.setShippingDate(chkLine.getShippingDate());
				line.setTripNo(chkLine.getTripNo());

				line.setVatAmount(chkLine.getVatAmount());
				line.setPrice(chkLine.getPrice());
				line.setUom(chkLine.getUom());
				line.setQty(chkLine.getQty());
				line.setLineAmount(chkLine.getLineAmount());
				line.setDiscount(chkLine.getDiscount());
				line.setTotalAmount(chkLine.getTotalAmount());
				line.setPriceAfDiscount(chkLine.getPriceAfDiscount());
				
				if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) {
					line.setVatAmount1(chkLine.getVatAmount());
					line.setUom1(chkLine.getUom());
					chkLine.setUom1(chkLine.getUom());
					line.setPrice1(chkLine.getPrice());
					line.setQty1(line.getQty1() + chkLine.getQty());
					line.setLineAmount1(line.getLineAmount1() + chkLine.getLineAmount());
					line.setDiscount1(line.getDiscount1() + chkLine.getDiscount());
					line.setTotalAmount1(line.getTotalAmount1() + chkLine.getTotalAmount());
				} else {
					line.setVatAmount2(chkLine.getVatAmount());
					line.setUom2(chkLine.getUom());
					chkLine.setUom2(chkLine.getUom());
					line.setPrice2(chkLine.getPrice());
					line.setQty2(line.getQty2() + chkLine.getQty());
					line.setLineAmount2(line.getLineAmount2() + chkLine.getLineAmount());
					line.setDiscount2(line.getDiscount2() + chkLine.getDiscount());
					line.setTotalAmount2(line.getTotalAmount2() + chkLine.getTotalAmount());
				}
				if (chkLine.getFullUom() == null) {
					if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())){
						line.setFullUom(ConvertNullUtil.convertToString(chkLine.getUom().getCode() + "/"));
					}else{
						line.setFullUom(ConvertNullUtil.convertToString("/" + chkLine.getUom().getCode()));
					}
				} else {
					line.setFullUom(chkLine.getFullUom());
				}
				/**option **/
				line.setOrderNo(chkLine.getOrderNo());
				line.setCustomerName(chkLine.getCustomerName());
				line.setCustomerCode(chkLine.getCustomerCode());
				line.setOrderDate(chkLine.getOrderDate());
				line.setStatus(chkLine.getStatus());
				line.setTaxable(chkLine.getTaxable());
				
				newLinesList.add(line);
				firstAdd = false;
				prvIndex = 0;
			} else {
				// Update previous line if repeat product code & promotion.

				if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
					continue;
				}
				line = new OrderLine();
				
				//Check Prev  
				if (chkLine.getProduct().getCode().equals(lines.get(i - 1).getProduct().getCode())
						&& chkLine.getPromotion().equals(lines.get(i - 1).getPromotion())
						&& chkLine.getShippingDate().equals(lines.get(i - 1).getShippingDate())
						&& chkLine.getRequestDate().equals(lines.get(i - 1).getRequestDate())) {
					
					line.setId(chkLine.getId());
					line.setUom(chkLine.getUom());
					line.setActiveLabel(chkLine.getActiveLabel());
					line.setArInvoiceNo(chkLine.getArInvoiceNo());
					line.setLineNo(chkLine.getLineNo());
					line.setTripNo(chkLine.getTripNo());
					line.setOrderId(chkLine.getOrderId());
					line.setPayment(chkLine.getPayment());
					line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
					line.setPromotion(chkLine.getPromotion());
					line.setRequestDate(chkLine.getRequestDate());
					line.setShippingDate(chkLine.getShippingDate());
					line.setVatAmount(chkLine.getVatAmount() + newLinesList.get(prvIndex).getVatAmount());
					
					line.setLineAmount(chkLine.getLineAmount() + newLinesList.get(prvIndex).getLineAmount());
					line.setDiscount(chkLine.getDiscount() + newLinesList.get(prvIndex).getDiscount());
					line.setTotalAmount(chkLine.getTotalAmount() + newLinesList.get(prvIndex).getTotalAmount());
					line.setPriceAfDiscount(chkLine.getPriceAfDiscount());
					
					if ( !Utils.isNull(chkLine.getUom2().getCode()).equals("") ) {
						logger.info("set Promotion Uom2");
						
						line.setUom2(chkLine.getUom());
						line.setPrice2(chkLine.getPrice());
						line.setLineAmount2(chkLine.getLineAmount());
						line.setDiscount2(chkLine.getDiscount());
						line.setTotalAmount2(chkLine.getTotalAmount());
						line.setVatAmount2(chkLine.getVatAmount());
						
						//           Prev Qty2                            + chkQty2
						line.setQty2(newLinesList.get(prvIndex).getQty2() + chkLine.getQty2());
						
						//          Prev Qty                            + chkQty2
						line.setQty(newLinesList.get(prvIndex).getQty() + chkLine.getQty2());
						
					}else{
						logger.info("set Promotion Uom1");
						
						line.setUom1(newLinesList.get(prvIndex).getUom1());
						line.setPrice1(newLinesList.get(prvIndex).getPrice1());
						line.setLineAmount1(newLinesList.get(prvIndex).getLineAmount1());
						line.setDiscount1(newLinesList.get(prvIndex).getDiscount1());
						line.setTotalAmount1(newLinesList.get(prvIndex).getTotalAmount1());
						line.setVatAmount1(newLinesList.get(prvIndex).getVatAmount1());
						
						//		     Prev Qty                            + chkQty1
						line.setQty1(newLinesList.get(prvIndex).getQty() + chkLine.getQty1());
						
                       //		   Prev Qty                            + chkQty1
					   line.setQty(newLinesList.get(prvIndex).getQty() + chkLine.getQty1());

					}
					
					if (checkFullUOM(newLinesList.get(prvIndex).getFullUom())) {
						line.setFullUom(newLinesList.get(prvIndex).getFullUom());
					}else{
						line.setFullUom(newLinesList.get(prvIndex).getFullUom() + chkLine.getUom().getCode());
					}
					/**option **/
					line.setOrderNo(chkLine.getOrderNo());
					line.setCustomerName(chkLine.getCustomerName());
					line.setCustomerCode(chkLine.getCustomerCode());
					line.setOrderDate(chkLine.getOrderDate());
					line.setStatus(chkLine.getStatus());
					line.setTaxable(chkLine.getTaxable());
					
					//set prev Qty 
					newLinesList.set(prvIndex, line);
				} else {
					// Aneak.t 24/01/2011
					if (chkLine.getIscancel() != null && chkLine.getIscancel().equals("Y")) {
						continue;
					}
					line.setId(chkLine.getId());
					line.setActiveLabel(chkLine.getActiveLabel());
					line.setArInvoiceNo(chkLine.getArInvoiceNo());
					line.setDiscount(chkLine.getDiscount1());
					line.setLineNo(chkLine.getLineNo());
					line.setTripNo(chkLine.getTripNo());
					line.setOrderId(chkLine.getOrderId());
					line.setPayment(chkLine.getPayment());
					line.setProduct(new MProduct().find(String.valueOf(chkLine.getProduct().getId())));
					line.setPromotion(chkLine.getPromotion());
					line.setRequestDate(chkLine.getRequestDate());
					line.setShippingDate(chkLine.getShippingDate());
					// line.setVatAmount(chkLine.getVatAmount());
					line.setVatAmount(chkLine.getVatAmount());
					line.setUom(chkLine.getUom());
					line.setPrice(chkLine.getPrice());
					line.setFullUom(chkLine.getFullUom());
					line.setQty(line.getQty() + chkLine.getQty());
					line.setLineAmount(chkLine.getLineAmount());
					line.setDiscount(chkLine.getDiscount());
					line.setTotalAmount(chkLine.getTotalAmount());
					line.setPriceAfDiscount(chkLine.getPriceAfDiscount());
					
					if (chkLine.getProduct().getUom().getId().equals(chkLine.getUom().getId())) {
						line.setVatAmount1(chkLine.getVatAmount());
						line.setUom1(chkLine.getUom());
						chkLine.setUom1(chkLine.getUom());
						line.setPrice1(chkLine.getPrice());
						line.setQty1(line.getQty1() + chkLine.getQty());
						line.setLineAmount1(line.getLineAmount1() + chkLine.getLineAmount());
						line.setDiscount1(line.getDiscount1() + chkLine.getDiscount());
						line.setTotalAmount1(line.getTotalAmount1() + chkLine.getTotalAmount());
					} else {
						line.setVatAmount2(chkLine.getVatAmount());
						line.setUom2(chkLine.getUom());
						chkLine.setUom2(chkLine.getUom());
						line.setPrice2(chkLine.getPrice());
						line.setQty2(line.getQty2() + chkLine.getQty());
						line.setLineAmount2(line.getLineAmount2() + chkLine.getLineAmount());
						line.setDiscount2(line.getDiscount2() + chkLine.getDiscount());
						line.setTotalAmount2(line.getTotalAmount2() + chkLine.getTotalAmount());
					}
					if (chkLine.getFullUom() == null) {
						line.setFullUom(ConvertNullUtil.convertToString(chkLine.getUom1().getCode()) + "/"
								+ ConvertNullUtil.convertToString(chkLine.getUom2().getCode()));
					} else {
						line.setFullUom(chkLine.getFullUom());
					}
					/**option **/
					line.setOrderNo(chkLine.getOrderNo());
					line.setCustomerName(chkLine.getCustomerName());
					line.setCustomerCode(chkLine.getCustomerCode());
					line.setOrderDate(chkLine.getOrderDate());
					line.setStatus(chkLine.getStatus());
					line.setTaxable(chkLine.getTaxable());
					
					newLinesList.add(line);
					prvIndex++;
				}
			}
			
			logger.debug("UOM1:"+line.getUom1());
			logger.debug("UOM2:"+line.getUom1());
			logger.debug("productCode:"+line.getProduct().getCode());
			logger.debug("fullUOM:"+line.getFullUom());
			
			i++;
		}
		logger.debug("*********** /fillLinesShowPromotion ******************************");
	} catch (Exception e) {
		throw e;
	}
	return newLinesList;
}


public List<OrderLine> sumQtyProductPromotionDuplicate(List<OrderLine> promotionLines) throws Exception {
	List<OrderLine> orderLineList = new ArrayList<OrderLine>();
	Map<String,OrderLine> orderLineMap = new HashMap<String, OrderLine>();
	String key = "";//productCode+"-'+uom_id
	int i=0;
	logger.info("----------sumQtyProductPromotionDuplicate-----------------------");
	try {
		if(promotionLines != null && promotionLines.size()>0){
			while (i < promotionLines.size()) {
				OrderLine orderLineCurr = promotionLines.get(i);
				
				key = orderLineCurr.getProduct().getCode()+"-"+orderLineCurr.getUom().getCode();
				logger.info("keyMap:"+key);
				if(orderLineMap.get(key)== null){
					orderLineMap.put(key, orderLineCurr);
				}else{
					OrderLine orderLinePrev = orderLineMap.get(key);
					//sum qty
					double qty = orderLinePrev.getQty() + orderLineCurr.getQty();
					double qty1 = orderLinePrev.getQty1() + orderLineCurr.getQty1();
					double qty2 = orderLinePrev.getQty2() + orderLineCurr.getQty2();
					orderLinePrev.setQty(qty);
					orderLinePrev.setQty1(qty1);
					orderLinePrev.setQty2(qty2);
					//Set new Qty
					orderLineMap.put(key, orderLinePrev);
				}
				i++;
			}//while
			
			//Convert ro OrderLineList
			orderLineList = new ArrayList<OrderLine>(orderLineMap.values());
		}
	logger.info("------------sumQtyProductPromotion--------------------");
	} catch (Exception e) {
		throw e;
	}
	return orderLineList;
}


public void debug(List<OrderLine> lines) throws Exception {
	OrderLine chkLine = null;
	int i=0;
	logger.info("----------debug-----------------------");
	try {
		while (i < lines.size()) {
			chkLine = lines.get(i);
			logger.info("********************************************************************************");
			logger.info("productCode["+chkLine.getProduct().getCode()+"] ,isPromotion["+chkLine.getPromotion()+"]");
			logger.info("uom["+chkLine.getUom()+"] ,uom1["+chkLine.getUom1()+"] ,uom2["+chkLine.getUom2()+"]");
			logger.info("qty["+chkLine.getQty()+"] ,qty1["+chkLine.getQty1()+"] ,qty2["+chkLine.getQty2()+"]");
			logger.info("price["+chkLine.getPrice()+"] ,price1["+chkLine.getPrice1()+"] ,price2["+chkLine.getPrice2()+"]");
			logger.info("priceAfDiscount["+chkLine.getPriceAfDiscount()+"] ");
			logger.info("lineAmount["+chkLine.getLineAmount()+"]totalAmount["+chkLine.getTotalAmount()+"]");
			i++;
		}
	logger.info("-------------------------------------");
	} catch (Exception e) {
		throw e;
	}
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
            //debug.debug(" ****Start fillLinesShowBlankUOM****");
			for(OrderLine l:lines){
				//debug.debug("uom1["+l.getUom1().getCode()+"]uom2["+Utils.isNull(l.getUom1().getCode())+"]");
				
				if( Utils.isNull(l.getUom1().getCode()).equals("") ||  Utils.isNull(l.getUom2().getCode()).equals("") ){
				   //debug.debug("getUom product:"+l.getProduct().getCode());
				   
				   OrderLine lineUom = getUOM(conn,pricelistID ,String.valueOf(l.getProduct().getId()));
				   
				   if(Utils.isNull(l.getUom1().getCode()).equals("")){
					 if(lineUom != null){
						 l.setUom1(lineUom.getUom1());
						 l.setPrice1(lineUom.getPrice1());
					 }
				   }
					
                   if(Utils.isNull(l.getUom2().getCode()).equals("")){
                	 if(lineUom != null){
  						//l.setUom2(lineUom.getUom2());
  						//l.setPrice2(lineUom.getPrice2());
  					  }	
				   }
                    l.setFullUom(l.getUom1().getCode()+"/");//+l.getUom2().getCode());
				    newLines.add(l);
				}else{
					newLines.add(l);
				}
			}
			
			//debug.debug(" **** end fillLinesShowBlankUOM****");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
	
		}
		return newLines;
	}
	
	public String sumTotalQty(List<OrderLine> lines) throws Exception {
		int totalQty = 0;
		for(OrderLine l:lines){
			totalQty += l.getQty();
		}
		return totalQty+"";
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
	 * 
	 * @param conn
	 * @param orderNo = receiptNo
	 * @return
	 * @throws Exception
	 */
	public int deleteReceiptExistByOrderNo(Connection conn ,String orderNo) throws Exception{
		int receiptId= 0;
		try{
			receiptId = getReceiptIdByOrderNo(conn,orderNo);
			if(receiptId != 0){
				deleteReceiptByReceiptId(conn,receiptId);
			}
		}catch(Exception e){
			throw e;
		}
		return receiptId;
	}
	
	/** Delete All Receipt**/
	public int deleteReceiptByReceiptId(Connection conn,int receiptId) throws Exception{
		StringBuffer sql = new StringBuffer("");
		int r=0;
		try{
			/** Delete Receipt **/
			sql.append("\n delete from t_receipt_by where receipt_id ="+receiptId +";");
			sql.append("\n delete from t_receipt_match where receipt_id ="+receiptId +";");
			sql.append("\n delete from t_receipt_line where receipt_id ="+receiptId +";");
			sql.append("\n delete from t_receipt where receipt_id ="+receiptId +";");
			
			//logger.debug("sql:"+sql.toString());
			Utils.excUpdateReInt(conn,sql.toString());
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}finally{
			
		}
		return r;
	}
	public int getReceiptIdByOrderNo(Connection conn ,String orderNo) throws Exception{
		int receiptId= 0;
		String whereCause = "";
		try{
			whereCause =" and receipt_no ='"+orderNo+"' and doc_status='SV' ";
			Receipt[] receipt = new MReceipt().search(conn, whereCause);
			if(receipt != null && receipt[0] != null){
				receiptId = receipt[0].getId();
			}
		}catch(Exception e){
			throw e;
		}
		return receiptId;
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
		receipt.setPaymentMethod(order.getPaymentMethod());
		// Customer
		receipt.setCustomerId(order.getCustomerId());
		receipt.setCustomerName(order.getCustomerBillName());
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
		
		//edit 14/03/2561 Case Van no save receipt(credit)
		/*no change is_cash in order */
		/*if("N".equals(receipt.getIsPDPaid())){
			order.setIsCash("N");
		}else{
			order.setIsCash("Y");
		}*/
		
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
