package com.isecinc.pens.web.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;

/**
 * Member Order Action
 * 
 * @author atiz.b
 * @version $Id: MemberOrderAction.java,v 1.0 20/01/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MemberOrderAction extends OrderAction {

	/**
	 * Search MO
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward searchMO(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");
			OrderCriteria criteria = getSearchCriteria(request, orderForm.getCriteria(), this.getClass().toString());
			orderForm.setCriteria(criteria);
			String whereCause = "";
			whereCause += "  and order_type = '" + user.getOrderType().getKey() + "' ";
			whereCause += "  and doc_status = 'SV' ";
			if (orderForm.getOrder().getOrderNo().trim().length() > 0)
				whereCause += "  and order_no like '" + orderForm.getOrder().getOrderNo().trim() + "%' ";
			if (orderForm.getOrder().getMemberCode().trim().length() > 0)
				whereCause += "  and customer_id in (select customer_id from m_customer where code = '"
						+ orderForm.getOrder().getMemberCode().trim() + "') ";
			
			MOrder orderService = new MOrder();
			Order[] ords = orderService.search(whereCause);

			if (ords != null) {
				ords = orderService.setSummaryNeedBill(ords);
				
				orderForm.setResults(ords);
				orderForm.getCriteria().setSearchResult(ords.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}
		return mapping.findForward("mosearch");
	}

	/**
	 * Prepare MO
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward prepareMO(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String id = request.getParameter("id");
			OrderForm orderForm = (OrderForm) form;

			MOrder orderService = new MOrder();
			MCustomer customerService = new MCustomer();
			
			Order order = orderService.find(id);
			Order[] orders = new Order[1];
			orders[0]=order;
			order = orderService.setSummaryNeedBill(orders)[0];
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());
			order.setOldPriceFlag(member.getOldPriceFlag());
			
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return mapping.findForward("prepare");
			}
			List<OrderLine> lstLines = new MOrderLine().lookUpNew(order.getId());
			orderForm.setOrder(order);
			orderForm.setLines(lstLines);
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}
		return mapping.findForward("prepare");
	}

	/**
	 * Save Line
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveLine(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");
			

			OrderLine line = new MOrderLine().find(orderForm.getId());
			Order order = new MOrder().find(String.valueOf(line.getOrderId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			for (OrderLine l : lstLines) {
				if (l.getId() == line.getId()) {
					line = l;
					break;
				}
			}

			// Calculate Line
			List<ProductPrice> pps = new MProductPrice().getCurrentPrice(String.valueOf(line.getProduct().getId()),
					String.valueOf(order.getPriceListId()), line.getUom().getId());
			double price = pps.get(0).getPrice();

			OrderLine orderLineInput = orderForm.getMemberNewLine();
			
			int qty = orderForm.getOrderLineQty();
			double lineAmount = qty * price;
			double vatAmount = lineAmount * Integer.parseInt(order.getVatCode()) / 100;
			double totalAmount = lineAmount + vatAmount;
			
			String isCancel = orderLineInput.getIscancel();
			String paymentMethod = orderLineInput.getPaymentMethod();
			String isPromotion1 = orderLineInput.getPromotion1();
			
			String reSchedule = orderForm.getReSchedule();
			String newDate = orderForm.getNewScheduleDate();
			
			line.setQty(qty);
			line.setLineAmount(lineAmount);
			line.setVatAmount(vatAmount);
			line.setTotalAmount(totalAmount);
			if("Y".equals(isPromotion1)){
				line.setDiscount(lineAmount);
				line.setVatAmount(0d);
				line.setTotalAmount(0d);
			}
			line.setIscancel(isCancel);
			line.setPaymentMethod(paymentMethod);
			line.setPromotion1(isPromotion1);

			conn = new DBCPConnectionProvider().getConnection(conn);

			conn.setAutoCommit(false);
			new MOrderLine().save(line, user.getId(), conn);

			// re-save order
			double tamount = 0;
			double vamount = 0;
			double namount = 0;
			List<OrderLine> cancelLines = new ArrayList<OrderLine>();
			
			for (OrderLine l : lstLines) {
				if(!"Y".equals(l.getPromotion1()) && !"Y".equals(l.getIscancel())){
					//tamount += l.getLineAmount();
					//vamount += l.getVatAmount();
					namount += l.getTotalAmount();
				}
				else if("Y".equals(l.getIscancel())){
					cancelLines.add(line);
				}
			}
			
			// Calculate Back From Net Amount
			tamount = (namount * 100/107);
			vamount = namount - tamount;
			
			order.setTotalAmount(tamount);
			order.setVatAmount(vamount);
			order.setNetAmount(namount);

			new MOrder().save(order, user.getId(), conn);

			conn.commit();

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());

			orderForm.setOrder(order);
			lstLines.removeAll(cancelLines);
			
			orderForm.setLines(lstLines);

			saveToken(request);

			// atiz.b 20110520 --member re-expired date with last order date
			reExpiredDate(order.getId(), order.getCustomerId(), user.getId());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}

	/**
	 * Save Trip
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveTrip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");
			MOrder orderService = new MOrder();
			Order order = orderService.find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			String whereCause = " and order_line_id = " + orderForm.getEditLineId()+ " ";
			whereCause += "  and iscancel = 'N' ";
			whereCause += "  and order_id = " + order.getId();
			OrderLine[] ls = new MOrderLine().search(whereCause);

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			for (OrderLine l : ls) {
				l.setShippingDate(orderForm.getNewShipDate());
				l.setRequestDate(orderForm.getNewReqDate());
				new MOrderLine().save(l, user.getId(), conn);
			}

			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			lstLines = new MOrderLine().lookUp(order.getId());
			
			Order[] orders = new Order[1];
			orders[0]=order;
			order = orderService.setSummaryNeedBill(orders)[0];
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());
			
			orderForm.setOrder(order);
			orderForm.setLines(lstLines);

			saveToken(request);

			// atiz.b 20110520 --member re-expired date with last order date
			reExpiredDate(order.getId(), order.getCustomerId(), user.getId());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}

	/**
	 * Cancel Trip
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward cancelTrip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");

			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			for (OrderLine l : lstLines) {
				if (l.getTripNo() == orderForm.getEditTripNo()) {
					l.setIscancel("Y");
					l.setExported("N");
					l.setNeedExport("N");
					l.setInterfaces("N");
					new MOrderLine().save(l, user.getId(), conn);
				}
			}

			// re-save order
			double tamount = 0;
			double vamount = 0;
			double namount = 0;
			for (OrderLine l : lstLines) {
				if(!"Y".equals(l.getPromotion1()) && !"Y".equals(l.getIscancel())){
					//tamount += l.getLineAmount();
					//vamount += l.getVatAmount();
					namount += l.getTotalAmount();
				}
			}
			
			// Calculate Back From Net Amount
			tamount = (namount * 100/107);
			vamount = namount - tamount;
			
			order.setTotalAmount(tamount);
			order.setVatAmount(vamount);
			order.setNetAmount(namount);

			new MOrder().save(order, user.getId(), conn);

			conn.commit();

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			lstLines = new MOrderLine().lookUp(order.getId());
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());
			
			orderForm.setOrder(order);
			orderForm.setLines(lstLines);

			saveToken(request);

			// atiz.b 20110520 --member re-expired date with last order date
			reExpiredDate(order.getId(), order.getCustomerId(), user.getId());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}

	/**
	 * Save Line
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveNewLine(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");
			
			MOrder orderService = new MOrder();
			Order order = orderService.find(String.valueOf(orderForm.getOrder().getId()));
			
			String reSchedule = orderForm.getReSchedule();
			String newScheduleDate = orderForm.getNewScheduleDate();
			int orderLineId = orderForm.getMemberNewLine().getId();
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(true);
			if("Y".equals(reSchedule)){
				new MOrder().reSchedule(orderLineId,StringUtils.isEmpty(newScheduleDate)?null:DateToolsUtil.convertToTimeStamp(newScheduleDate),conn,user.getId());
			}
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			
			if (orderForm.getMemberNewLine().getId() != 0) {
				// Update Line
				OrderLine editLine = orderForm.getMemberNewLine();

				for (OrderLine dbLine : lstLines) {
					if (dbLine.getId() == editLine.getId()) {
						if (dbLine.getProduct().getId() != editLine.getProduct().getId()) {
							// Change Product
							dbLine.setProduct(new MProduct().find(String.valueOf(editLine.getProduct().getId())));
						}
						// change Qty & re-calculate
						dbLine.setQty(editLine.getQty());
						dbLine.setNeedBill(editLine.getNeedBill());
						dbLine.setIscancel(editLine.getIscancel());
						dbLine.setPromotion1(editLine.getPromotion1());
						if(!StringUtils.isEmpty(editLine.getPaymentMethod()))
							dbLine.setPaymentMethod(editLine.getPaymentMethod());
						
						// Calculate Line
						List<ProductPrice> pps = new MProductPrice().getCurrentPrice(String.valueOf(dbLine.getProduct()
								.getId()), String.valueOf(order.getPriceListId()), dbLine.getUom().getId());
						double price = pps.get(0).getPrice();
						double lineAmt = dbLine.getQty() * price;
						double discount = 0d; 
						double vatAmt = ((lineAmt-discount)*7/100);
						double totalAmt = BigDecimal.valueOf(lineAmt + vatAmt).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
						double needBillAmt = editLine.getNeedBill();
						
						//Promotion
						if("Y".equals(editLine.getPromotion1())){
							discount = lineAmt;
							vatAmt = 0d;
							totalAmt = 0d;
							needBillAmt = 0d;
						}
						
						dbLine.setLineAmount(lineAmt);
						dbLine.setDiscount(discount);
						dbLine.setVatAmount(vatAmt);
						dbLine.setTotalAmount(totalAmt);
						dbLine.setNeedBill(needBillAmt);
						
						new MOrderLine().save(dbLine, user.getId(), conn);

						break;
					}
				}
			} else {
				// New Line
				OrderLine newLine = orderForm.getMemberNewLine();

				// Check on Exported
				List<OrderLine> tempLines = new MOrderLine().lookUp(order.getId());
				OrderLine tempLine = null;
				String payment = "N";
				String prepay = "N";
				String confirmReceiptDate = "";
				
				for (OrderLine l : tempLines) {
					if (l.getTripNo() == orderForm.getMemberNewLine().getTripNo()) {
						if (l.getExported().equalsIgnoreCase("Y")) {
							request.setAttribute("Message", SystemMessages.getCaption(
									SystemMessages.CANT_SAVE_ON_EXPORTED, new Locale("th", "TH")));

							lstLines = new MOrderLine().lookUp(order.getId());
							orderForm.setOrder(order);
							orderForm.setLines(lstLines);
							orderForm.setMemberNewLine(new OrderLine());

							return mapping.findForward("prepare");
						}
						
						if(tempLine == null)
							tempLine = l;
						
						if("Y".equals(newLine.getPromotion1()))
							break;
						
						if("Y".equals(l.getPayment()))
							payment = l.getPayment();
						
						if("Y".equals(l.getPrepay()))
							prepay = l.getPrepay();
						
						if(l.getConfirmReceiptDate() != null)
							confirmReceiptDate = l.getConfirmReceiptDate();
						
						if(tempLine!= null && "Y".equals(payment) && "Y".equals(prepay) && !StringUtils.isEmpty(confirmReceiptDate))
							break;
					}
				}

				// Calculate Line
				List<ProductPrice> pps = new MProductPrice().getCurrentPrice(String.valueOf(newLine.getProduct()
						.getId()), String.valueOf(order.getPriceListId()), newLine.getUom().getId());
				double price = pps.get(0).getPrice();
				double lineAmt = newLine.getQty() * price;
				double discount = 0d; 
				double vatAmt = ((lineAmt-discount)*7/100);
				double totalAmt = BigDecimal.valueOf(lineAmt + vatAmt).setScale(2,BigDecimal.ROUND_HALF_DOWN).doubleValue();
				double needBillAmt = newLine.getNeedBill();

				int max_line_no = new MOrderLine().getMaxLineNo(order.getId());
				newLine.setOrderId(order.getId());
				newLine.setLineNo(max_line_no + 1);
				
				//Promotion
				if("Y".equals(newLine.getPromotion1())){
					discount = lineAmt;
					vatAmt = 0d;
					totalAmt = 0d;
					needBillAmt = 0d;
				}
				
				newLine.setLineAmount(lineAmt);
				newLine.setDiscount(discount);
				newLine.setVatAmount(vatAmt);
				newLine.setTotalAmount(totalAmt);
				newLine.setNeedBill(needBillAmt);
				newLine.setIscancel("N");
				newLine.setPayment("N");
				
				// Adjust to Same 1st Trip Date
				if (tempLine != null) {
					//newLine.setShippingDate(tempLine.getShippingDate());
					//newLine.setRequestDate(tempLine.getRequestDate());
					newLine.setNeedExport(tempLine.getNeedExport());
					newLine.setExported(tempLine.getExported());
					newLine.setInterfaces(tempLine.getInterfaces());
					newLine.setConfirmReceiptDate(confirmReceiptDate);
					newLine.setPayment(payment);
					newLine.setPrepay(prepay);
				}
				
				//Check duplicate By order_id ,trip_No ,product_id
				
				boolean isProductDupInTrip = new MOrderLine().isProductDuplicateInTrip(conn,order.getId(),newLine.getTripNo(),newLine.getProduct().getId(),newLine.getPromotion1());
               
				if(!isProductDupInTrip){
				   new MOrderLine().save(newLine, user.getId(), conn);
				   lstLines.add(newLine);
                }else{
                   logger.debug("isProductDupInTrip =true");
                   conn.rollback();
                }
				
			}
			conn.commit();

			lstLines = new MOrderLine().lookUp(order.getId());
			
			// re-save order
			double tamount = 0;
			double vamount = 0;
			double namount = 0;
			for (OrderLine l : lstLines) {
				if(!"Y".equals(l.getPromotion1()) && !"Y".equals(l.getIscancel())){
					//tamount += l.getLineAmount();
					//vamount += l.getVatAmount();
					namount += l.getTotalAmount();
				}
			}
			
			// Calculate Back From Net Amount
			tamount = (namount * 100/107);
			vamount = namount - tamount;
			
			order.setTotalAmount(tamount);
			order.setVatAmount(vamount);
			order.setNetAmount(namount);

			new MOrder().save(order, user.getId(), conn);

			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			
			Order[] orders = new Order[1];
			orders[0]=order;
			order = orderService.setSummaryNeedBill(orders)[0];
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());

			orderForm.setOrder(order);
			orderForm.setLines(lstLines);
			orderForm.setMemberNewLine(new OrderLine());

			saveToken(request);

			// atiz.b 20110520 --member re-expired date with last order date
			reExpiredDate(order.getId(), order.getCustomerId(), user.getId());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				if(conn != null){
				  conn.close();
				  conn = null;
				}
			} catch (Exception e2) {}
		}
		
		
		return mapping.findForward("prepare");
	}

	/**
	 * Change Need Export
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward changeNeedExport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");

			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}
			String needExport = request.getParameter("needExport");

			String whereCause = " and trip_no = " + orderForm.getEditTripNo() + " ";
			whereCause += "  and iscancel = 'N' ";
			whereCause += "  and order_id = " + order.getId();
			OrderLine[] ls = new MOrderLine().search(whereCause);

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			for (OrderLine l : ls) {
				l.setNeedExport(needExport);
				l.setExported("N");
				l.setInterfaces("N");
				new MOrderLine().save(l, user.getId(), conn);
			}

			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			lstLines = new MOrderLine().lookUp(order.getId());
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());
			
			orderForm.setOrder(order);
			orderForm.setLines(lstLines);

			saveToken(request);

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveCallBeforeHeader(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		OrderForm orderForm = (OrderForm) form;

		try {
			String needCall = request.getParameter("needCall");
			User user = (User) request.getSession().getAttribute("user");

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			order.setCallBeforeSend(needCall);
			new MOrder().save(order, user.getId(), conn);

			String whereCause = " and ORDER_ID = " + order.getId() +" and shipment ='N'";// 
			OrderLine[] ls = new MOrderLine().search(whereCause);

			for (OrderLine l : ls) {
				l.setCallBeforeSend(needCall);
				new MOrderLine().save(l, user.getId(), conn);
			}

			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			lstLines = new MOrderLine().lookUp(order.getId());
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());
			
			orderForm.setOrder(order);
			orderForm.setLines(lstLines);

			saveToken(request);

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("prepare");
	}

	/**
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveCallBeforeLine(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		OrderForm orderForm = (OrderForm) form;

		try {
			String lineId = request.getParameter("lineId");
			String needCall = request.getParameter("needCall");
			User user = (User) request.getSession().getAttribute("user");

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			// Assign 'N' to header when line equals 'N'
			if ("N".equalsIgnoreCase(needCall)) {
				order.setCallBeforeSend(needCall);
				new MOrder().save(order, user.getId(), conn);
			}

			OrderLine line = new MOrderLine().find(lineId);
			line.setCallBeforeSend(needCall);
			new MOrderLine().save(line, user.getId(), conn);

			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			lstLines = new MOrderLine().lookUp(order.getId());
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());
			
			orderForm.setOrder(order);
			orderForm.setLines(lstLines);

			saveToken(request);

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("prepare");
	}

	/**
	 * Save Order Remark
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveOrderRemark(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");

			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			conn = new DBCPConnectionProvider().getConnection(conn);

			conn.setAutoCommit(false);

			order.setRemark(orderForm.getOrder().getRemark());

			new MOrder().save(order, user.getId(), conn);

			conn.commit();

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			
			MCustomer customerService = new MCustomer();
			Customer member = customerService.find(""+order.getCustomerId());
			order.setMemberCode(member.getCode());

			orderForm.setOrder(order);
			orderForm.setLines(lstLines);

			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}

	public ActionForward saveNewLineAuto(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			User user = (User) request.getSession().getAttribute("user");

			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			if (!isTokenValid(request)) {
				orderForm.setOrder(order);
				orderForm.setLines(lstLines);
				resetToken(request);
				saveToken(request);
				return mapping.findForward("prepare");
			}

			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);

			lstLines.addAll(new MOrder().saveNewLineAuto(order, user, conn));
			
			conn.commit();

			//request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		
			orderForm.setLines(lstLines);

			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}
	/**
	 * Re-Expire Date
	 * 
	 * @param orderId
	 * @param memberId
	 * @param userId
	 * @param conn
	 * @throws Exception
	 */
	private void reExpiredDate(int orderId, int memberId, int userId) throws Exception {
		Connection conn = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			List<OrderLine> lines = new MOrderLine().lookUp(orderId);
			Member member = new MMember().find(String.valueOf(memberId));
			String lastShipDate = "";
			if (lines.size() > 0) {
				lastShipDate = lines.get(lines.size() - 1).getShippingDate();
				member.setExpiredDate(lastShipDate);
				new MMember().save(member, userId, conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
}
