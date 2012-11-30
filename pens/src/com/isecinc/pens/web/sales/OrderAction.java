package com.isecinc.pens.web.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.OrgRuleBean;
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
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MOrgRule;
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
		logger.debug("prepare 0");
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			Customer customer = null;
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
				
			/* Wit Edit: 1307255 :Edit shotcut From CustomerSerach **/
			}else if (request.getParameter("shotcut_customerId") != null) {
				customerId = Integer.parseInt(request.getParameter("shotcut_customerId"));
				
				OrderCriteria criteria = new OrderCriteria();
				//criteria.setSearchKey(searchKey)
				orderForm.setCriteria(criteria);
			} else {
				// go add for customer/member
				customerId = orderForm.getOrder().getCustomerId();
			}

			orderForm.setOrder(new Order());
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			// default date time
			orderForm.getOrder().setOrderDate(
					new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
			orderForm.getOrder().setOrderTime(new SimpleDateFormat("HH:mm", new Locale("th", "TH")).format(new Date()));

			//PriceListID
			orderForm.getOrder().setPriceListId(new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId());

			// user
			orderForm.getOrder().setSalesRepresent(user);

			orderForm.getOrder().setOrderType(user.getOrderType().getKey());
			// default prepare forward

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
					lines.setShippingDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"))
									.format(new Date()));
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
			} 

			request.getSession().removeAttribute("isAdd");
			if (((String) request.getSession().getAttribute("memberVIP")).equals("N")) {
				// non-vip
			}

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
        logger.info("prepare have orderId ");
		try {
			roundTrip = orderForm.getOrder().getRoundTrip();

			order = new MOrder().find(id);
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return "view";
			}
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
			order.setRoundTrip(roundTrip);
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
	 * Prepare to Edit order
	 */
	public ActionForward prepareEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		User user = (User) request.getSession(true).getAttribute("user");
		Order order = null;
		int roundTrip = 0;
		logger.info("prepareEdit orderId["+Utils.isNull(request.getParameter("id"))+"]");
		try {
			OrderForm orderForm = (OrderForm) form;
			
			//Find Order by order_id
			String id = Utils.isNull(request.getParameter("id"));
			roundTrip = orderForm.getOrder().getRoundTrip();
			order = new MOrder().find(id);
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return mapping.findForward("prepare");
			}
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
			order.setRoundTrip(roundTrip);
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");

			// Prepare order line to Edit
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
			
			// Save Lines
			int i = 1;
			for (OrderLine line : orderForm.getLines()) {
				// Clear Discount
				line.setLineNo(i++);
				line.setDiscount(0);
				line.setProduct(new MProduct().find(String.valueOf(line.getProduct().getId())));
				line.setTotalAmount(line.getLineAmount());
			}

			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), user, (String) request.getSession().getAttribute("memberVIP")));
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));

			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			return mapping.findForward("prepare");
		} finally {
		}
		return mapping.findForward("prepareEdit");
	}

	/**
	 * Pre-Save 
	 * :Caculate Promotion 
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
				// Set Disply To Prepare Save Order Line
				orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request.getSession().getAttribute("memberVIP")));
                
				// Add Promotion Line only From irderFrom to promotionLine
				for (OrderLine line : orderForm.getLines()) {
					if (line.getPromotion().equalsIgnoreCase("Y")) {
						promotionLine.add(line);
					}
				}
				
				//remove Protion line from orderFrom
				for (OrderLine line : promotionLine) {
					orderForm.getLines().remove(line);
				}

				// Call Modifier Process
				ModifierProcess modProcess = new ModifierProcess(ConvertNullUtil.convertToString(customer.getTerritory()).trim());
				modProcess.findModifier(orderForm.getLines(), userActive, conn);


				// Set for web display.
				logger.info("****** Start Set Display order ****************************************************");
				if (!userActive.getRole().getKey().equals(User.DD)) {
					List<OrderLine> odLines = new OrderProcess().fillLinesShow(orderForm.getLines());
					List<OrderLine> promotionLines = new OrderProcess().fillLinesShow(modProcess.getAddLines());
					odLines.addAll(promotionLines);
					orderForm.setLines(odLines);
				}
				logger.info("****** End  Set Display order ****************************************************");
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
		int orderId = 0;
		String msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
		String org = "";
		String subInv ="";
		OrgRuleBean orgRuleBean;
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
					
				} 
				orderForm.getLines().clear();
				return "prepare";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");
			Order order = orderForm.getOrder();
			
			/*** Wit Edit:15/05/2555  Obj:Case User TT(CreditSale) Validate W1 Check PlaceOfBilled ****/
			if(User.TT.equals(userActive.getType()) && !"".equals(order.getPlaceOfBilled())){
				StringBuffer errorItemMsg = new StringBuffer("");
				logger.debug("placeOfBilled["+order.getPlaceOfBilled()+"]");
				String[] placeOfBilled = order.getPlaceOfBilled().split("\\|");
				org = placeOfBilled[0];
				subInv = placeOfBilled[1];
				
				//Set to Save
				order.setOrg(org);
				logger.debug("org["+org+"]subInv["+subInv+"]");
				orgRuleBean = new MOrgRule().getOrgRule(org);
				Map<String, String> itemMap = new MOrgRule().getOrgRuleItemMap(org, subInv) ;
				
				//Check item t_order_line has in W2 item
				if("Y".equals(orgRuleBean.getCheckItem()) && itemMap != null){
					for (OrderLine line : orderForm.getLines()) {
						logger.debug("productCode ["+line.getProduct().getCode()+"]check["+itemMap.get(line.getProduct().getCode())+"]");
						if(itemMap.get(line.getProduct().getCode()) == null){
							errorItemMsg.append("Item["+line.getProduct().getCode()+"] \n");
							request.setAttribute("Message", SystemMessages.getCaption("checkProductW2Item", Utils.local_th));
							break;
						}
					}//for
					
					if(errorItemMsg != null && errorItemMsg.length() > 1){
						return "pre-save";
					}
				}//check item
				
				//Case Default UE not save SUB_INV 
				if("G00".equals(orgRuleBean.getOrg())){
					subInv = "";
				}
			}//TT
			/*************************************************************************/
			
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

			for (OrderLine line : orderForm.getLines()) {
				line.setLineNo(i++);
				line.setPayment("N");
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
			if (ConvertNullUtil.convertToString(orderForm.getDeletedId()).trim().length() > 0){
				new MOrderLine().delete(orderForm.getDeletedId().substring(1).trim(), conn);
			}
			
			// Delete All Lines if Member Edit
			if (order.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)
					&& ((String) request.getSession().getAttribute("memberVIP")).equals("N")) {
				new MOrderLine().deleteAllLines(orderForm.getOrder().getId(), conn);
			}
			
			// Save Lines all new
			i = 1;
			String lastShipDate = "";
			for (OrderLine line : orderForm.getLines()) {
				line.setLineNo(i++);
				
				line.setPayment("N");
				line.setOrderId(order.getId());
				line.setNeedExport("N");
				line.setExported("N");
				line.setInterfaces("N");
				
				//Wit Edit 15/05/2555 Obj:Case PlaceOfBilled
				line.setOrg(org);
				line.setSubInv(subInv);
				
				//Save Line to DB
				new MOrderLine().save(line, userActive.getId(), conn);
			}
			// atiz.b 20110520 --member re-expired date with last order date
			if (orderForm.getLines().size() > 0) {
				lastShipDate = orderForm.getLines().get(orderForm.getLines().size() - 1).getShippingDate();
			}

			// Re-Calculate totalAmount vatAmount netAmount From line
			new MOrder().reCalculate(orderForm.getOrder(), orderForm.getLines());

			// Re-Save
			new MOrder().save(order, userActive.getId(), conn);

			// auto create receipt with member
			if (orderForm.getAutoReceiptFlag().equalsIgnoreCase("Y")) {
				String creditCardExpired = "";
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
				 orderForm.getAutoReceipt().setInternalBank("002");//SCB- “¢“ “∏ÿª√–¥‘…∞Ï 068-2-81805-7
				 orderForm.getAutoReceipt().setReceiptDate(orderForm.getOrder().getOrderDate());
				 
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
			}else{
				if(userActive.getRole().getKey().equals(User.VAN)){
				    msg  = SystemMessages.getCaption("SaveSucessAndCreateReceipt", Utils.local_th);
				}
			}
			/** WIT Edit 20110804 ****************************************/
			
			if (!userActive.getRole().getKey().equals(User.DD)) {
				orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));
			}
			
			// re org case line_no duplicate
			new MOrderLine().reOrgLineNo(orderForm.getOrder().getId(), conn);
			
			// Commit Transaction
			conn.commit();
			// set msg save success
			request.setAttribute("Message",msg );
            		
			/********* WIT EDIT:20110804 :case not auto receipt cash  -> popup page AutoPay *****/
			if(userActive.getRole().getKey().equals(User.VAN) &&  !orderForm.getOrder().isPaymentCashNow()){
				request.setAttribute("popup_autoreceipt", "true");
			}
			/**************************************************************/
			
			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
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
		boolean isCash = "Y".equals(orderForm.getOrder().getIsCash());
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

			String creditCardExpired = "";
		
			if (user.getType().equalsIgnoreCase(User.VAN)) {
				// assign order no to receipt no
				orderForm.getAutoReceipt().setReceiptNo(order.getOrderNo());
				
				if(user.isPDPaid() && !isCash)
					orderForm.getAutoReceipt().setIsPDPaid("N");
			}
			
			logger.info("internalBank:"+orderForm.getAutoReceipt().getInternalBank());
			
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
			} 
			conn.commit();
			//
			request.setAttribute("Message", SystemMessages.getCaption("SaveReceiptSuccess", Utils.local_th));
			
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
			Order[] results = new MOrder().search(whereCause);

			// results = fillLinesShow(results);
			if(results != null)
				results = checkCreditNote(results);

			orderForm.setResults(results);
			if (results != null) {
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
		if (!orderForm.getOrder().getOrderType().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
			// VAN && TT
			Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
			orderForm.setCriteria(new OrderCriteria());
			orderForm.getOrder().setCustomerId(customer.getId());
			orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
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

			customer = new MCustomer().find(String.valueOf(order.getCustomerId()));
			addresses = new MAddress().lookUp(customer.getId());
			
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
			
			parameterMap.put("p_customerCode", customer.getCode());
			parameterMap.put("p_customerName", customer.getName());
			

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

			
				taxInvoice.setCustomerCode(customer.getCode());
				taxInvoice.setCustomerName(customer.getName());
				
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
