package com.isecinc.pens.web.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberRenew;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MCreditNote;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberProduct;
import com.isecinc.pens.model.MMemberRenew;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.process.modifier.ModifierProcess;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceReport;

/**
 * Order Action
 * 
 * @author atiz.b
 * @version $Id: OrderAction.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 *          Modifier : A-neak.t Add : method addDate, calShippingDate, calWeekByRoundtrip, generateLine Edit: method
 *          save
 * 
 *          atiz.b : set current sales reps
 */
public class OrderAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		OrderForm orderForm = (OrderForm) form;
		String action = request.getParameter("action") != null ? (String) request.getParameter("action") : "";
		logger.debug("prepare no id");
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			Customer customer = null;
			Member member = null;
			int orderId = orderForm.getOrder().getId();

			int customerId = 0;
			if (request.getParameter("customerId") != null) {
				// go search
				forward = "search";
				customerId = Integer.parseInt(request.getParameter("customerId"));
				// orderForm.setOrder(new Order());
			} else if (request.getParameter("memberId") != null) {
				// go search
				forward = "search";
				customerId = Integer.parseInt(request.getParameter("memberId"));
				// orderForm.setOrder(new Order());
			} else {
				// go add for customer/member
				customerId = orderForm.getOrder().getCustomerId();
			}

			orderForm.setOrder(new Order());
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			// default date time
			orderForm.getOrder().setOrderDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
			orderForm.getOrder().setOrderTime(new SimpleDateFormat("HH:mm", new Locale("th", "TH")).format(new Date()));

			
			// GET PRICELIST
			if(user.getOrderType().getKey().equalsIgnoreCase("DD")){
				
				member = new MMember().find(String.valueOf(customerId));
				//logger.debug("member.getRegisterDate()--->"+member.getRegisterDate());
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
				Date regDate = formatter.parse(member.getRegisterDate());
				String regDateString = DateToolsUtil.convertToStringEng(regDate);		
			
				//Edit 10/12/2557  On product 02/01/2557 
				if("Y".equalsIgnoreCase(member.getOldPriceFlag())){
					//check from register date
					orderForm.getOrder().setPriceListId(new MPriceList().getCurrentPriceListByCustomer(user.getOrderType().getKey(), regDateString).getId());
				
				}else{
				  //New code check from sysdate
				   String currentDate = Utils.stringValue(new Date(), Utils.YYYY_MM_DD_WITH_LINE);
				   orderForm.getOrder().setPriceListId(new MPriceList().getCurrentPriceListByCustomerNewPrice(user.getOrderType().getKey(),currentDate).getId());
				}
				
				logger.debug("priceListId:"+orderForm.getOrder().getPriceListId());
				
			}else{
				orderForm.getOrder().setPriceListId(new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId());
			}
			
			// user
			orderForm.getOrder().setSalesRepresent(user);

			orderForm.getOrder().setOrderType(user.getOrderType().getKey());
			// default prepare forward

			//Calculate PaymentType
			//BigDecimal sumTotalAmount = new BigDecimal(0);
						
			// Product from C4
			if (orderForm.getProductIds() != null) {
				Product product;
				OrderLine lines;
				MProduct mProduct = new MProduct();
				MProductPrice mProductPrice = new MProductPrice();
				orderForm.setLines(new ArrayList<OrderLine>());
				List<ProductPrice> pps;
				int i = 1;
				boolean baseUOM = false;
				for (String id : orderForm.getProductIds().split(",")) {
					baseUOM = false;
					lines = new OrderLine();
					product = mProduct.find(id);
					lines.setLineNo(i);
					lines.setProduct(product);
					pps = mProductPrice.lookUp(product.getId(), orderForm.getOrder().getPriceListId());
					for (ProductPrice pp : pps) {
						if (pp.getUom().getId().equalsIgnoreCase(product.getUom().getId())) {
							// base uom
							baseUOM = true;
							lines.setPrice(pp.getPrice());
							break;
						}
					}
					lines.setUom(product.getUom());
					lines.setQty(1);
					lines.setLineAmount(lines.getQty() * lines.getPrice());
					lines.setTotalAmount(lines.getLineAmount() - lines.getDiscount());
					
					//sumTotalAmount = new BigDecimal(lines.getTotalAmount());
					//sumTotalAmount.add(sumTotalAmount); 
						
					if (baseUOM) {
						lines.setPrice1(lines.getPrice());
						lines.setUom1(lines.getUom());
						lines.setQty1(lines.getQty());
						lines.setLineAmount1(lines.getLineAmount());
						lines.setTotalAmount1(lines.getTotalAmount());
					} else {
						lines.setPrice2(lines.getPrice());
						lines.setUom2(lines.getUom());
						lines.setQty2(lines.getQty());
						lines.setLineAmount2(lines.getLineAmount());
						lines.setTotalAmount2(lines.getTotalAmount());
					}

					lines.setFullUom(ConvertNullUtil.convertToString(lines.getUom1().getCode()) + "/"
							+ ConvertNullUtil.convertToString(lines.getUom2().getCode()));
					lines.setPromotion("N");
					lines.setShippingDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
					lines.setRequestDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
					orderForm.getLines().add(lines);
					i++;
				}
				forward = "prepare";
			}
			
			if (!user.getCustomerType().getKey().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
				// TT & VAN
				// Default from customer
				customer = new MCustomer().find(String.valueOf(customerId));
				orderForm.getOrder().setCustomerId(customer.getId());
				orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
				orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
				orderForm.getOrder().setVatCode(customer.getVatCode());
				request.getSession().setAttribute("memberVIP", "N");
			} else {
				// DD
				// Default from member
				member = new MMember().find(String.valueOf(customerId));
				member.setMemberProducts(new MMemberProduct().lookUp(member.getId()));
				if (orderId != 0) {
					orderForm.setOrder(new MOrder().find(String.valueOf(orderId)));
				}
				orderForm.getOrder().setCustomerId(member.getId());
				orderForm.getOrder().setCustomerName((member.getCode() + "-"+member.getName() + " " + member.getName2()).trim());
				//orderForm.getOrder().setCustomerName(
				//		(member.getCode() + "-" + member.getName() + " " + member.getName2()).trim());
				// from customer or member
				orderForm.getOrder().setPaymentTerm(member.getPaymentTerm());
				orderForm.getOrder().setPaymentMethod(member.getPaymentMethod());
				orderForm.getOrder().setVatCode(member.getVatCode());
				orderForm.getOrder().setShippingDay(member.getShippingDate());
				orderForm.getOrder().setShippingTime(member.getShippingTime());
				if (!StringUtils.isEmpty(member.getRoundTrip())) {
					orderForm.getOrder().setRoundTrip(Integer.parseInt(member.getRoundTrip()));
				}
				if("Y".equals(member.getIsFreeOfChart())){
					request.getSession().setAttribute("freeOfChart", member.getIsFreeOfChart());
				}else{
					request.getSession().setAttribute("freeOfChart", "N");
				}
				
				request.getSession().setAttribute("memberVIP", member.getIsvip());
				if (!"add".equals(action) && !"search".equals(action)) {
						orderForm.setLines(new OrderProcess().generateLine(member, orderForm.getOrder(), ""));
				}
			}

			request.getSession().removeAttribute("isAdd");
			if (((String) request.getSession().getAttribute("memberVIP")).equals("N")) {
				// non-vip
				if (user.getCustomerType().getKey().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
					// Check order date in register date or renew date and expire date.
					// if return false is not found , return true is found.
					if (new OrderProcess().isWithinDateRange(member)) {
						request.getSession(true).setAttribute("isAdd", "N");
					}
				}
			}

			// orderForm.getOrder().setOrderType(user.getOrderType().getKey());
			// get Customer/Member Search Key
			if (request.getParameter("key") != null) {
				request.getSession(true).setAttribute("CMSearchKey", request.getParameter("key"));
			}

			// No Pricelist msg
			
			if (orderForm.getOrder().getPriceListId() == 0) {
				orderForm.getOrder().setPricelistLabel(
						InitialMessages.getMessages().get(Messages.NO_PRICELIST).getDesc());
			}
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			return forward;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession(true).getAttribute("user");
		Order order = null;
		int roundTrip = 0;
        logger.debug("prepare id:"+id);
		try {
			roundTrip = orderForm.getOrder().getRoundTrip();

			MOrder orderService = new MOrder();
			order = orderService.find(id);
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return "view";
			}
			// orderForm.setLines(new MOrderLine().lookUp(order.getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			if (!user.getRole().getKey().equals(User.DD)) {
				//logger.debug("fillLinesShow No DD");
				List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
				orderForm.setLines(lines);
			} else {
				/*if (((String) request.getSession().getAttribute("memberVIP")).equalsIgnoreCase("Y")) {
					List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
					orderForm.setLines(lines);
				} else {
					orderForm.setLines(lstLines);
				}*/
				
				orderForm.setLines(lstLines);
			}
			order.setRoundTrip(roundTrip);
			
			Order[] orders = new Order[1];
			orders[0]=order;
			order = orderService.setSummaryNeedBill(orders)[0];
			
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		if (request.getParameter("action").equalsIgnoreCase("edit")) return "prepare";
		return "view";
	}

	/**
	 * Pre-Save
	 */
	public ActionForward preSave(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			conn = new DBCPConnectionProvider().getConnection(conn);
			User userActive = (User) request.getSession().getAttribute("user");
			Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
			if (!userActive.getType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
				/** Promotion Process add add to Lines */
				// remove promotion line
				List<OrderLine> promotionLine = new ArrayList<OrderLine>();
				orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
						.getSession().getAttribute("memberVIP")));

				for (OrderLine line : orderForm.getLines()) {
					if (line.getPromotion().equalsIgnoreCase("Y")) {
						promotionLine.add(line);
					}
				}
				for (OrderLine line : promotionLine) {
					orderForm.getLines().remove(line);
				}

				// Call Modifier Process
				ModifierProcess modProcess = new ModifierProcess(ConvertNullUtil.convertToString(
						customer.getTerritory()).trim());
				modProcess.findModifier(orderForm.getLines(), userActive, conn);

				// Add New Price Line
				// if (modProcess.getAddLines().size() > 0) {
				// // Line in Add Lines flag Promotion = 'Y'
				// orderForm.getLines().addAll(modProcess.getAddLines());
				// }

				// Set for web display.
				if (!userActive.getRole().getKey().equals(User.DD)) {
					List<OrderLine> odLines = new OrderProcess().fillLinesShow(orderForm.getLines());
					List<OrderLine> promotionLines = new OrderProcess().fillLinesShow(modProcess.getAddLines());
					odLines.addAll(promotionLines);
					orderForm.setLines(odLines);
				}
			}

			// Save Lines
			int i = 1;
			for (OrderLine line : orderForm.getLines()) {
				line.setLineNo(i++);
			}
			// Re-Calculate
			// new MOrder().reCalculate(orderForm.getOrder(), orderForm.getLines());

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			return mapping.findForward("prepare");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("pre-save");
	}

	/**
	 * Pre-Save
	 */
	public ActionForward backToAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			OrderForm orderForm = (OrderForm) form;
			conn = new DBCPConnectionProvider().getConnection(conn);
			User userActive = (User) request.getSession().getAttribute("user");
			if (!userActive.getType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
				/** Promotion Process add add to Lines */
				// remove promotion line
				List<OrderLine> promotionLine = new ArrayList<OrderLine>();
				for (OrderLine line : orderForm.getLines()) {
					if (line.getPromotion().equalsIgnoreCase("Y")) {
						promotionLine.add(line);
					}
				}
				for (OrderLine line : promotionLine) {
					orderForm.getLines().remove(line);
				}
			}

			// Save Lines
			int i = 1;
			for (OrderLine line : orderForm.getLines()) {
				// Clear Discount
				line.setLineNo(i++);
				line.setDiscount(0);
				line.setProduct(new MProduct().find(String.valueOf(line.getProduct().getId())));
				line.setTotalAmount(line.getLineAmount());
			}

			if (!userActive.getRole().getKey().equals(User.DD)) {
				orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
						.getSession().getAttribute("memberVIP")));
				orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));
			}

			// Re-Calculate
			// new MOrder().reCalculate(orderForm.getOrder(), orderForm.getLines());

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			return mapping.findForward("prepare");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepare");
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		OrderForm orderForm = (OrderForm) form;
		String tps = request.getParameter("tps");
		String pds = request.getParameter("pds");
		String ids = request.getParameter("ids");

		
		//System.out.println("xxx"+orderForm.getLines().size());
		
		/*if(tps == null){
			
			//Order o = new Order()
			Order order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			List<OrderLine> lineList = new MOrderLine().lookUp(order.getId());
			
			for (OrderLine line1 : lineList) {
				if(line1.getPromotion1().equalsIgnoreCase("Y")){
					
					
					
				}
			}

			
		}*/
			
			/*for (OrderLine line : orderForm.getLines()) {
				//double a = line.getNeedBill();
				line.setPromotion1("N");
				for(String tpStr:tps.split("[,]")){
					if(String.valueOf(line.getTripNo()).equalsIgnoreCase(tpStr)){
						line.setDiscount(line.getLineAmount());
						line.setPromotion1("Y");
						line.setNeedBill(0);
					}
				}
				
			}*/
		
		
		int orderId = 0;
		String msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
		try {
			orderId = orderForm.getOrder().getId();

			// check Token
			if (!isTokenValid(request)) {
				if (!orderForm.getOrder().getOrderType().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
					// VAN && TT
					Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
					orderForm.setOrder(new Order());
					orderForm.getOrder().setCustomerId(customer.getId());
					orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
					// from customer or member
					orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
					orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
					orderForm.getOrder().setVatCode(customer.getVatCode());
					
				} else {
					// DD
					Member member = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));
					
					//paymentMethod = member.getPaymentMethod();
					
					orderForm.setOrder(new Order());
					orderForm.getOrder().setCustomerId(member.getId());
					orderForm.getOrder().setCustomerName(
							(member.getCode() + "-" + member.getName() + " " + member.getName2()).trim());
					// from customer or member
					orderForm.getOrder().setPaymentTerm(member.getPaymentTerm());
					orderForm.getOrder().setPaymentMethod(member.getPaymentMethod());
					orderForm.getOrder().setVatCode(member.getVatCode());
					orderForm.getOrder().setShippingDay(member.getShippingDate());
					orderForm.getOrder().setShippingTime(member.getShippingTime());
				}
				orderForm.getLines().clear();
				return "prepare";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");

			Order order = orderForm.getOrder();
			
			BigDecimal tt = new BigDecimal(order.getTotalAmount());
			BigDecimal vt = new BigDecimal(order.getVatAmount());
			BigDecimal nt = new BigDecimal(order.getNetAmount());
			tt = tt.setScale(2,BigDecimal.ROUND_HALF_UP);
			vt = vt.setScale(2,BigDecimal.ROUND_HALF_UP);
			nt = nt.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			order.setTotalAmount(tt.doubleValue());
			order.setVatAmount(vt.doubleValue());
			order.setNetAmount(nt.doubleValue());
			
			//System.out.println("kkk"+order.getVatAmount());
			// order.setOrderType(userActive.getOrderType().getKey());

			// set interfaces & payment & docstatus
			order.setInterfaces("N");
			order.setPayment("N");
			// order.setDocStatus(Order.DOC_SAVE);
			
			Address billAddr = new MAddress().find(""+order.getBillAddressId());
			if(billAddr!=null)
				order.setOraBillAddressID(billAddr.getReferenceId());
			
			Address shipAddr = new MAddress().find(""+order.getShipAddressId());
			if(shipAddr!=null)
				order.setOraShipAddressID(shipAddr.getReferenceId());

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			int i = 1;

			if (!order.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
				orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
						.getSession().getAttribute("memberVIP")));
			}
			if (((String) request.getSession().getAttribute("memberVIP")).equalsIgnoreCase("Y")) {
				orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
						.getSession().getAttribute("memberVIP")));
			}



			// Sales Reps --current user who's create/edit record--
			order.setSalesRepresent(userActive);

			// Save Order
			if (!new MOrder().save(order, userActive.getId(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}

			// Delete Promotion
			new MOrderLine().deletePromotion(orderForm.getOrder().getId(), conn);

			// Delete Line
			if (ConvertNullUtil.convertToString(orderForm.getDeletedId()).trim().length() > 0)
				new MOrderLine().delete(orderForm.getDeletedId().substring(1).trim(), conn);

			// Delete All Lines if Member Edit
			if (order.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)
					&& ((String) request.getSession().getAttribute("memberVIP")).equals("N")) {
				new MOrderLine().deleteAllLines(orderForm.getOrder().getId(), conn);
			}
			// Save Lines
			Member member1 = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));
			
			i = 1;
			String lastShipDate = "";
			for (OrderLine line : orderForm.getLines()) {
				if (!order.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
					line.setLineNo(i++);
				}
				line.setPayment("N");
				line.setOrderId(order.getId());
				line.setNeedExport("N");
				line.setExported("N");
				line.setInterfaces("N");
				line.setShipment("N");
				//line.setReceipt("N");
				line.setPaymentMethod(member1.getPaymentMethod());
				
				
/*				//edit saleOrder
				if(line.getId() != 0){
					logger.debug("xxx"+tps.length());
					if(tps.length() == 0){
						OrderLine lineDb = new MOrderLine().find(String.valueOf(line.getId()));
						if(lineDb.getPromotion1().equalsIgnoreCase("Y")){
							line.setDiscount(0);
							line.setPromotion1("N");
						}else{
							line.setPromotion1("N");
						}
						
					}else{
						//if(tps.split("[,]").)
						boolean isFirst = true;
						for(String idStr:ids.split("[,]")){
							if(line.getId() == Integer.valueOf(idStr)){
								line.setDiscount(line.getLineAmount());
								line.setPromotion1("Y");
								line.setNeedBill(0);
								isFirst = false;
							}else{
								if(isFirst){
									line.setDiscount(0);
									line.setPromotion1("N");
								}
							}
						}
					}
				}*/
				
				new MOrderLine().save(line, userActive.getId(), conn);
			}
			// atiz.b 20110520 --member re-expired date with last order date
			if (orderForm.getLines().size() > 0) {
				lastShipDate = orderForm.getLines().get(orderForm.getLines().size() - 1).getShippingDate();
			}

			// Re-Calculate
			// new MOrder().reCalculate(orderForm.getOrder(), orderForm.getLines());

			// Re-Save
			new MOrder().save(order, userActive.getId(), conn);

			Member member = null;
			if (orderForm.getOrder().getOrderType().equalsIgnoreCase(Receipt.DIRECT_DELIVERY)) {
				// VAN && TT
				member = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));

				// atiz.b 20110520 --member re-expired date with last order date
				member.setExpiredDate(lastShipDate);
				new MMember().save(member, userActive.getId(), conn);
			}

			// auto create receipt with member
			if (orderForm.getAutoReceiptFlag().equalsIgnoreCase("Y")) {
				String creditCardExpired = "";
				if (member != null) {
					// credit card
					creditCardExpired = member.getCreditcardExpired();
				}
				new OrderProcess().createAutoReceipt(orderForm.getAutoReceipt(), order, orderForm.getLines(), orderForm
						.getBys(), creditCardExpired, userActive, conn);
			}

			// Trx History
			TrxHistory trx = new TrxHistory();
			trx.setTrxModule(TrxHistory.MOD_ORDER);
			if (orderId == 0) trx.setTrxType(TrxHistory.TYPE_INSERT);
			else trx.setTrxType(TrxHistory.TYPE_UPDATE);
			trx.setRecordId(order.getId());
			trx.setUser(userActive);
			new MTrxHistory().save(trx, userActive.getId(), conn);
			// Trx History --end--

			
            
			/** WIT Edit 20110804 ****************************************/
			/** Case Van and User choose payCashNow -> createAutoReceipt**/
			if (userActive.getRole().getKey().equals(User.VAN) && orderForm.getOrder().isPaymentCashNow()) {
				 orderForm.getAutoReceipt().setReceiptNo(order.getOrderNo());
				 orderForm.getAutoReceipt().setReceiptAmount(order.getNetAmount());
				 orderForm.getAutoReceipt().setInternalBank("002");
				 orderForm.getAutoReceipt().setReceiptDate(orderForm.getOrder().getOrderDate());
				 //orderForm.getAutoReceipt().setInternalBank("CASH");
				 
				 /** Set ReceiptBy Manual **/
				 List<ReceiptBy> receiptByList = new ArrayList<ReceiptBy>();
				 ReceiptBy receiptBy = new ReceiptBy();
				 receiptBy.setId(0);
				 //receiptBy.setPaymentMethod("PD");
				 receiptBy.setPaymentMethod("CS");
				 receiptBy.setCreditCardType("");
				 receiptBy.setBank("");
				 receiptBy.setChequeNo("");
				 receiptBy.setChequeDate("");
				 receiptBy.setReceiptAmount(order.getNetAmount());
				 receiptBy.setSeedId("");
				 receiptBy.setAllBillId(String.valueOf(order.getId()));
				 receiptBy.setAllPaid(String.valueOf(order.getNetAmount()));
				 receiptByList.add(receiptBy);
				 //process auto receipt cash
				 new OrderProcess().createAutoReceipt(orderForm.getAutoReceipt(), order, orderForm.getLines(), receiptByList, null, userActive, conn);
				 //set msg 
				 msg = SystemMessages.getCaption("SaveOrderReceiptSuccess", Utils.local_th);
			}
			/** WIT Edit 20110804 ****************************************/
			
			if (!userActive.getRole().getKey().equals(User.DD)) {
				orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));
			}
			
			// Commit Transaction
			conn.commit();
			// set msg save success
			request.setAttribute("Message",msg );
            
			logger.debug("isPaymentCashNow:"+orderForm.getOrder().isPaymentCashNow());
			logger.debug("orderForm.getAutoReceiptFlag():"+orderForm.getAutoReceiptFlag());
			
			/********* WIT EDIT:20110804 :case not auto receipt cash  -> popup page AutoPay *****/
			if(userActive.getRole().getKey().equals(User.VAN) &&  !orderForm.getOrder().isPaymentCashNow()){
				request.setAttribute("popup_autoreceipt", "true");
			}
			/**************************************************************/
			// save token
			saveToken(request);
		} catch (Exception e) {
			orderForm.getOrder().setId(orderId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
			return "prepare";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "view";
	}

	/**
	 * Create Auto Receipt
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward createAutoReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession(true).getAttribute("user");
		Order order = null;
		int roundTrip = 0;
		Connection conn = null;
		try {

			// check Token
			if (!isTokenValid(request)) {
				if (!orderForm.getOrder().getOrderType().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
					// VAN && TT
					Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
					orderForm.setOrder(new Order());
					orderForm.getOrder().setCustomerId(customer.getId());
					orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
					// from customer or member
					orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
					orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
					orderForm.getOrder().setVatCode(customer.getVatCode());
				} else {
					// DD
					Member member = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));
					orderForm.setOrder(new Order());
					orderForm.getOrder().setCustomerId(member.getId());
					orderForm.getOrder().setCustomerName(
							(member.getCode() + "-" + member.getName() + " " + member.getName2()).trim());
					// from customer or member
					orderForm.getOrder().setPaymentTerm(member.getPaymentTerm());
					orderForm.getOrder().setPaymentMethod(member.getPaymentMethod());
					orderForm.getOrder().setVatCode(member.getVatCode());
					orderForm.getOrder().setShippingDay(member.getShippingDate());
					orderForm.getOrder().setShippingTime(member.getShippingTime());
				}
				orderForm.getLines().clear();
				return mapping.findForward("prepare");
			}

			conn = new DBCPConnectionProvider().getConnection(conn);

			conn.setAutoCommit(false);
			roundTrip = orderForm.getOrder().getRoundTrip();

			order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return mapping.findForward("view");
			}

			// Get Lines to Create Receipt
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());

			Member member = null;
			if (orderForm.getOrder().getOrderType().equalsIgnoreCase(Receipt.DIRECT_DELIVERY)) {
				// VAN && TT
				member = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));
			}
			String creditCardExpired = "";
			if (member != null) {
				// credit card
				creditCardExpired = member.getCreditcardExpired();
			}

			if (user.getType().equalsIgnoreCase(User.VAN)) {
				// assign order no to receipt no
				orderForm.getAutoReceipt().setReceiptNo(order.getOrderNo());
			}

			if (!new OrderProcess().createAutoReceipt(orderForm.getAutoReceipt(), order, lstLines, orderForm.getBys(),
					creditCardExpired, user, conn)) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return mapping.findForward("view");
			}

			// Set Lines to Show
			if (!user.getRole().getKey().equals(User.DD)) {
				List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
				orderForm.setLines(lines);
			} else {
				if (((String) request.getSession().getAttribute("memberVIP")).equalsIgnoreCase("Y")) {
					List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
					orderForm.setLines(lines);
				} else {
					orderForm.setLines(lstLines);
				}
			}
			conn.commit();
			//
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			// Refresh Order
			order = new MOrder().find(String.valueOf(orderForm.getOrder().getId()));
			order.setRoundTrip(roundTrip);
			orderForm.setOrder(order);
			// save token
			saveToken(request);
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("view");
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {

			request.getSession().removeAttribute("isAdd");
			if (user.getCustomerType().getKey().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
				// Check order date in register date or renew date and expire date.
				// if return false is not found , return true is found.
				Member member = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				if (new OrderProcess().isWithinDateRange(member)) {
					request.getSession().setAttribute("isAdd", "N");
				}
			}

			OrderCriteria criteria = getSearchCriteria(request, orderForm.getCriteria(), this.getClass().toString());
			orderForm.setCriteria(criteria);
			String whereCause = "";
			if (orderForm.getOrder().getOrderNo() != null && !orderForm.getOrder().getOrderNo().equals("")) {
				whereCause += " AND ORDER_NO LIKE '%"
						+ orderForm.getOrder().getOrderNo().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%'";
			}
			if (orderForm.getOrder().getSalesOrderNo() != null && !orderForm.getOrder().getSalesOrderNo().equals("")) {
				whereCause += " AND SALES_ORDER_NO LIKE '%"
						+ orderForm.getOrder().getSalesOrderNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%'";
			}
			if (orderForm.getOrder().getArInvoiceNo() != null && !orderForm.getOrder().getArInvoiceNo().equals("")) {
				whereCause += " AND AR_INVOICE_NO LIKE '%"
						+ orderForm.getOrder().getArInvoiceNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%'";
			}
			if (orderForm.getOrder().getDocStatus().length() > 0) {
				whereCause += " AND DOC_STATUS = '" + orderForm.getOrder().getDocStatus() + "'";
			}

			if (orderForm.getOrder().getOrderDateFrom().trim().length() > 0) {
				whereCause += " AND ORDER_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(orderForm.getOrder().getOrderDateFrom().trim()) + "'";
			}
			if (orderForm.getOrder().getOrderDateTo().trim().length() > 0) {
				whereCause += " AND ORDER_DATE <= '"
						+ DateToolsUtil.convertToTimeStamp(orderForm.getOrder().getOrderDateTo().trim()) + "'";
			}

			whereCause += " AND ORDER_TYPE = '" + user.getOrderType().getKey() + "' ";
			whereCause += " AND CUSTOMER_ID = " + orderForm.getOrder().getCustomerId() + " ";

			//if (!user.getType().equalsIgnoreCase(User.DD)) whereCause += " AND USER_ID = " + user.getId();

			whereCause += " ORDER BY ORDER_DATE DESC,ORDER_NO DESC ";
			
			MOrder orderService = new MOrder();
			Order[] results = orderService.search(whereCause);
			results = orderService.setSummaryNeedBill(results);
			
			results = checkCreditNote(results);

			orderForm.setResults(results);
			if (results != null && results.length >  0) {
				orderForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	private Order[] checkCreditNote(Order[] results) {
		// TODO Auto-generated method stub
		Order[] orders = new Order[results.length];
		int i = 0;
		for(Order order : results){
			order.setHasCreditNote(false);
			if(!StringUtils.isEmpty(order.getArInvoiceNo())){
				double creditNoteAmt = new MCreditNote().getTotalCreditNoteAmt(order.getArInvoiceNo());
				if(creditNoteAmt < 0)
					order.setHasCreditNote(true);
			}
			
			orders[i] = order;
			i++;
		}
		
		return orders;
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * New Criteria
	 * 
	 * @throws Exception
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		OrderForm orderForm = (OrderForm) form;
		String pricelistlabel = orderForm.getOrder().getPricelistLabel();
		int pricelistid = orderForm.getOrder().getPriceListId();
		
		logger.debug("orderForm.getOrder().getCustomerId():"+orderForm.getOrder().getCustomerId());
		// DD
		Member member = new MMember().find(String.valueOf(orderForm.getOrder().getCustomerId()));
		orderForm.setCriteria(new OrderCriteria());
		if(member != null){
			orderForm.getOrder().setCustomerId(member.getId());
			orderForm.getOrder().setCustomerName(
					(member.getCode() + "-" + member.getName() + " " + member.getName2()).trim());
			orderForm.getOrder().setPaymentTerm(member.getPaymentTerm());
			orderForm.getOrder().setPaymentMethod(member.getPaymentMethod());
		}
		
		orderForm.getOrder().setPricelistLabel(pricelistlabel);
		orderForm.getOrder().setPriceListId(pricelistid);

		// No Pricelist msg
		if (orderForm.getOrder().getPriceListId() == 0) {
			orderForm.getOrder().setPricelistLabel(InitialMessages.getMessages().get(Messages.NO_PRICELIST).getDesc());
		}
	}

	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Search for report : " + this.getClass());
		OrderForm reportForm = (OrderForm) form;
		// TaxInvoiceReportProcess process = new TaxInvoiceReportProcess();
		User user = (User) request.getSession().getAttribute("user");
		TaxInvoiceReport taxInvoice = new TaxInvoiceReport();
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		List<TaxInvoiceReport> lstData = null;
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		Order order = null;
		List<OrderLine> lines = null;
		Customer customer = null;
		Member member = null;
		Address address = null;
		List<Address> addresses = new ArrayList<Address>();
		try {
			boolean baddr = false;
			String fileType = request.getParameter("fileType");
			String orderId = request.getParameter("id");
			String visitDate = request.getParameter("visitDate");
            
			logger.debug("fileType:"+fileType);
			
			// Re-set value to order form.
			// order = new MOrder().find(String.valueOf(reportForm.getOrder().getId()));
			order = new MOrder().find(orderId);
			reportForm.setOrder(order);
			lines = new MOrderLine().lookUp(reportForm.getOrder().getId());
			reportForm.setLines(new OrderProcess().fillLinesShow(lines));
			lines = reportForm.getLines();
			String receiptNo = new MReceipt().getLastestReceiptFromOrder(order.getId());

			if (user.getType().equalsIgnoreCase(User.DD)) {
				member = new MMember().find(String.valueOf(order.getCustomerId()));
				addresses = new MAddress().lookUp(member.getId());
			} else {
				customer = new MCustomer().find(String.valueOf(order.getCustomerId()));
				addresses = new MAddress().lookUp(customer.getId());
			}
			for (Address addr : addresses) {
				if (addr.getPurpose().equalsIgnoreCase("S")) {
					address = addr;
					baddr = true;
					break;
				}
			}
			if (!baddr) {
				for (Address addr : addresses) {
					if (addr.getPurpose().equalsIgnoreCase("B")) {
						address = addr;
						baddr = true;
						break;
					}
				}
			}

			parameterMap.put("p_receiptNo", receiptNo.length() != 0 ? receiptNo : order.getOrderNo());
			parameterMap.put("p_vatcode", order.getVatCode());
			parameterMap.put("p_address", address.getLineString());
			parameterMap.put("p_orderDate", order.getOrderDate());
			parameterMap.put("p_code", user.getCode());
			parameterMap.put("p_name", user.getName());
			parameterMap.put("p_taxNo", BeanParameter.getPensTaxNo());
			if (user.getType().equalsIgnoreCase(User.DD)) {
				parameterMap.put("p_customerCode", member.getCode());
				parameterMap.put("p_customerName", member.getName() + " "
						+ ConvertNullUtil.convertToString(member.getName2()));
			} else {
				parameterMap.put("p_customerCode", customer.getCode());
				parameterMap.put("p_customerName", customer.getName());
			}

			lstData = new ArrayList<TaxInvoiceReport>();
			int id = 1;
			for (OrderLine line : lines) {
				taxInvoice = new TaxInvoiceReport();
				taxInvoice.setOrderID(String.valueOf(order.getId()));
				taxInvoice.setId(id++);
				taxInvoice.setAddress(address.getLineString());
				taxInvoice.setOrderDate(order.getOrderDate());
				taxInvoice.setTaxNo("Tax No in Line");
				taxInvoice.setCode(user.getCode());
				taxInvoice.setName(user.getName());

				if (user.getType().equalsIgnoreCase(User.DD)) {
					taxInvoice.setCustomerCode(member.getCode());
					taxInvoice.setCustomerName(member.getName() + " "
							+ ConvertNullUtil.convertToString(member.getName2()));
				} else {
					taxInvoice.setCustomerCode(customer.getCode());
					taxInvoice.setCustomerName(customer.getName());
				}

				taxInvoice.setProductCode(line.getProduct().getCode());
				taxInvoice.setProductName(line.getProduct().getName());
				taxInvoice.setUomId(line.getProduct().getUom().getId());
				taxInvoice.setMainQty(new Double(line.getQty1()).intValue());
				taxInvoice.setSubQty(new Double(line.getQty2()).intValue());
				taxInvoice.setAddQty(new Double(line.getQty2()).intValue());
				taxInvoice.setSalePrice(line.getLineAmount());
				taxInvoice.setPercentDiscount(0);
				taxInvoice.setDiscount(line.getDiscount());
				taxInvoice.setLineAmount(line.getLineAmount() - line.getDiscount());
				// Summary
				taxInvoice.setTotalAmount(line.getLineAmount() - line.getDiscount());
				taxInvoice.setVatAmount(line.getVatAmount());
				//taxInvoice.setNetAmount(line.getTotalAmount());
							
				
				//BigDecimal totalAmount = new BigDecimal(taxInvoice.getTotalAmount());
				//BigDecimal vatAmount = new BigDecimal(taxInvoice.getVatAmount());
				//BigDecimal netAmount = totalAmount.add(vatAmount);
				
				taxInvoice.setNetAmount(taxInvoice.getTotalAmount()+taxInvoice.getVatAmount());
				//taxInvoice.setNetAmount(netAmount.doubleValue());

				lstData.add(taxInvoice);
			}
			// parameterMap.put("p_next_visit", request.getParameter("nextVisitDate"));
			parameterMap.put("p_next_visit", visitDate);

			String fileName = "tax_invoice_report";
			//WIT Edit :21/07/2011 :Special Case Show PDF user another report tax_invoice_pdf_report 
			//beacuse : print and pdf not support same fonts
			if (fileType.equals(SystemElements.PDF)) {
				fileName = "tax_invoice_pdf_report";
			}
			String fileJasper = BeanParameter.getReportPath() + fileName;

			// for sale user.
			if (request.getParameter("i") != null && request.getParameter("i").equals("0")) {
				parameterMap.put("report_title", bundle.getString("Receipt_TaxInvoice"));
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
			} else {
				// for customer.
				parameterMap.put("report_title", bundle.getString("Shipment_TaxInvoice"));
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}

	/**
	 * Prepare Popup
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward preparePopup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		OrderForm orderForm = (OrderForm) form;
		User user = (User) request.getSession(true).getAttribute("user");
		Order order = null;
		try {
			String id = request.getParameter("id");

			order = new MOrder().find(id);
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			// orderForm.setLines(new MOrderLine().lookUp(order.getId()));
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			if (!user.getRole().getKey().equals(User.DD)) {
				// credit van
				List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
				orderForm.setLines(lines);
			} else {
				Member member = new MMember().find(String.valueOf(order.getCustomerId()));
				if (member.getIsvip().equalsIgnoreCase("Y")) {
					List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
					orderForm.setLines(lines);
				} else {
					orderForm.setLines(lstLines);
				}
			}
			orderForm.setOrder(order);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		}
		return mapping.findForward("viewPopup");
	}
	
	/**
	 * cancelOrder
	 */
	public ActionForward cancelOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			User user = (User) request.getSession().getAttribute("user");
			OrderForm orderForm = (OrderForm) form;
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
	
			new MOrder().cancelOrderByID(conn,orderForm.getOrder().getId(),orderForm.getOrder().getReason(),user.getId(),orderForm.getOrder().getPayment());
            
			conn.commit();
            
			//set actionCancelOrder
			request.setAttribute("actionCancelOrder", "success");
			
            request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			try{
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
						+ e.getMessage());
				conn.rollback();
			 }catch(Exception ee){}
			 return mapping.findForward("cancelOrderPopup");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("cancelOrderPopup");
	}
}
