package com.isecinc.pens.web.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.isecinc.pens.inf.helper.DBConnection;
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
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.process.modifier.ModifierProcess;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.report.listOrderProduct.ListOrderProductReport;
import com.isecinc.pens.report.listOrderProduct.ListOrderProductReportProcess;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceReport;
import com.isecinc.pens.web.externalprocess.ProcessAfterAction;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.ControlCode;
import com.pens.util.ConvertNullUtil;
import com.pens.util.CustomerReceiptFilterUtils;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;
import com.pens.util.Debug;
import com.pens.util.NumberToolsUtil;
import com.pens.util.ReportHelper;
import com.pens.util.ReportUtilServlet;


/**
 * Order Action
 * Wittaya 13/07/2020
 */
public class OrderAction extends I_Action {

	public Debug debug = new Debug(true);
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		OrderForm orderForm = (OrderForm) form;
		String action = request.getParameter("action") != null ? (String) request.getParameter("action") : "";
		logger.debug("prepare 0");
		Connection conn = null;
		User user = (User) request.getSession(true).getAttribute("user");
		try {
			//Set Debug Mode
			logger.debug("debug.isDebugEnable():"+debug.isDebugEnable());
			request.getSession().setAttribute("debug_mode", debug.isDebugEnable());
		
			long customerId = 0;
			if (request.getParameter("customerId") != null) {
				// go search
				forward = "search";
				customerId = Utils.convertStrToLong(request.getParameter("customerId"));
		
			} else if (request.getParameter("memberId") != null) {
				// go search
				forward = "search";
				customerId = Utils.convertStrToLong(request.getParameter("memberId"));
				
			/* Wit Edit: 13072558 :Edit shortcut From CustomerSerach **/
			}else if (request.getParameter("shotcut_customerId") != null) {
				customerId = Utils.convertStrToLong(request.getParameter("shotcut_customerId"));
				
				OrderCriteria criteria = new OrderCriteria();
				orderForm.setCriteria(criteria);
			} else {
				// go add for customer/member
				customerId = orderForm.getOrder().getCustomerId();
			}
			//prepare Connection 
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			// get Customer Info
			Customer customer = new MCustomer().findByWhereCond("where customer_id ="+customerId);
			
			//Check Case VanSale Old order(PayCredit) No Pay disable Pay Credit ALL
			boolean canSaveCasePayPrevBill = true;
			if(User.VAN.equals(user.getType())){
				canSaveCasePayPrevBill = OrderUtils.canSaveCreditVan(conn,user, customerId);
			    logger.info("canSave :"+canSaveCasePayPrevBill);
				
				//Filter Check Van Can Receipt Cheque Or Credit Flag
				//1:can, 0: cannot, -1:no pay prev bill
				String canReceiptChequeFlag = CustomerReceiptFilterUtils.canReceiptCheque(conn,customerId);
				String canReceiptCreditFlag = CustomerReceiptFilterUtils.canReceiptCredit(conn,customerId);
				
				if("Y".equalsIgnoreCase(canReceiptChequeFlag) || "Y".equalsIgnoreCase(canReceiptCreditFlag)){
					//Check Prev bill is pay
					if(canSaveCasePayPrevBill==false){
				       orderForm.setReceiptCreditFlag("-1");//no pay prev bill
					}else{
					   orderForm.setReceiptCreditFlag("1");//Can pay Credit
					}
				}else{
				   orderForm.setReceiptCreditFlag("0");//cannot pay credit
				}
				
				//wit edit VAN :default receipt Cash 
				 orderForm.getOrder().setVanPaymentMethod("CASH");
				 
				//set creditLimit from customer
				 //logger.debug("CustCreditLimit:"+customer.getCreditLimit());
				 orderForm.setCustCreditLimit(customer.getCreditLimit());
			}//if
			
			orderForm.setOrder(new Order());
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			
			// default date time
			orderForm.getOrder().setOrderDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
			orderForm.getOrder().setOrderTime(new SimpleDateFormat("HH:mm", new Locale("th", "TH")).format(new Date()));

			//PriceListID
			orderForm.getOrder().setPriceListId(new MPriceList().getCurrentPriceList(user.getOrderType().getKey()).getId());

			// user
			orderForm.getOrder().setSalesRepresent(user);

			orderForm.getOrder().setOrderType(user.getOrderType().getKey());
			// default prepare forward
			
			logger.debug("1:order.customerName:"+orderForm.getOrder().getCustomerName());

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
				orderForm.getOrder().setId(0);
				orderForm.getOrder().setCustomerId(customer.getId());
				orderForm.getOrder().setCustomerName(Utils.isNull(customer.getName()));
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
				orderForm.getOrder().setPricelistLabel(InitialMessages.getMessages().get(Messages.NO_PRICELIST).getDesc());
			}
			
			//Set Prev Step Action
			ControlOrderPage.setPrevOrderStepAction(request, ControlOrderPage.STEP_ORDER_1);
			
			logger.debug("2:order.customerName:"+orderForm.getOrder().getCustomerName());
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return forward;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		OrderForm orderForm = (OrderForm) form;
		Order order = null;
		int roundTrip = 0;
		Connection conn = null;
        logger.info("prepare have orderId ");
		try {
			roundTrip = orderForm.getOrder().getRoundTrip();

			order = new MOrder().find(id);
			
			logger.debug("OrderDate:"+order.getOrderDate());
			
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return "view";
			}
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			//add Line Blank UOM (1 or 2)
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines(),null));

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
		}finally{
			try{
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if (request.getParameter("action").equalsIgnoreCase("edit")) return "prepare";
		return "view";
	}
	
	/**
	 * Prepare to Edit order
	 */
	public ActionForward prepareEditOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		User user = (User) request.getSession(true).getAttribute("user");
		Order order = null;
		int roundTrip = 0;
		debug.info("prepareEditOrder orderId["+Utils.isNull(request.getParameter("id"))+"]");
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
			logger.debug("canCancel:"+order.isCanCancel());
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
			order.setRoundTrip(roundTrip);
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");

			//Filter Check Van Can Receipt Cheque Or Credit
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			if(User.VAN.equals(user.getType())){
				boolean canSaveCasePayPrevBill = OrderUtils.canSaveCreditVan(conn,user, order.getCustomerId());
				String canReceiptChequeFlag = CustomerReceiptFilterUtils.canReceiptCheque(conn,orderForm.getOrder().getCustomerId());
				String canReceiptCreditFlag = CustomerReceiptFilterUtils.canReceiptCredit(conn,orderForm.getOrder().getCustomerId());
				
				logger.debug("canSaveCasePayPrevBill:"+canSaveCasePayPrevBill);
				logger.debug("canReceiptChequeFlag:"+canReceiptChequeFlag+",canReceiptCreditFlag:"+canReceiptCreditFlag);
				
				if("Y".equalsIgnoreCase(canReceiptChequeFlag) || "Y".equalsIgnoreCase(canReceiptCreditFlag)){
					//Check Prev bill is pay
					if(canSaveCasePayPrevBill==false){
				       orderForm.setReceiptCreditFlag("-1");//no pay prev bill
					}else{
					   orderForm.setReceiptCreditFlag("1");//Can pay Credit
					}
				}else{
				   orderForm.setReceiptCreditFlag("0");//cannot pay credit
				}
				
				// get Customer Info
				Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				//set creditLimit from customer
				orderForm.setCustCreditLimit(customer.getCreditLimit());
				 
			}//if van
			
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
            
			//Split 2 Line
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), user, (String) request.getSession().getAttribute("memberVIP")));
			
			//Merge to 1 Line show
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));

			//add Line Blank UOM (1 or 2)
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines(),null));

			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
			//Set Prev Step Action
			ControlOrderPage.setPrevOrderStepAction(request, ControlOrderPage.STEP_ORDER_1);
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			return mapping.findForward("prepare");
		} finally {
			try{
				if(conn != null){
			       conn.close();conn=null;
				}
			}catch(Exception e){
				
			}
		}
		return mapping.findForward("prepareEditOrder");
	}
	
	

	/**
	 * prepareEditOrderCaseCannotReserve
	 * For user edit order 
	 */
	public OrderForm prepareEditOrderCaseCannotReserve(OrderForm orderForm,User user,Map<String,String> productErrorMap) {
		Connection conn = null;
		Order order = null;
		int roundTrip = 0;
		debug.info("prepareEditOrderCaseCannotReserve orderId["+orderForm.getOrder().getId()+"]");
		try {
			//Find Order by order_id
			String id = String.valueOf(orderForm.getOrder().getId());
			roundTrip = orderForm.getOrder().getRoundTrip();
			order = new MOrder().find(id);
			
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
			order.setRoundTrip(roundTrip);
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");

			//Filter Check Van Can Receipt Cheque Or Credit
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			if(User.VAN.equals(user.getType())){
				boolean canSaveCasePayPrevBill = OrderUtils.canSaveCreditVan(conn,user, order.getCustomerId());
				String canReceiptChequeFlag = CustomerReceiptFilterUtils.canReceiptCheque(conn,orderForm.getOrder().getCustomerId());
				String canReceiptCreditFlag = CustomerReceiptFilterUtils.canReceiptCredit(conn,orderForm.getOrder().getCustomerId());
				
				logger.debug("canSaveCasePayPrevBill:"+canSaveCasePayPrevBill);
				logger.debug("canReceiptChequeFlag:"+canReceiptChequeFlag+",canReceiptCreditFlag:"+canReceiptCreditFlag);
				
				if("Y".equalsIgnoreCase(canReceiptChequeFlag) || "Y".equalsIgnoreCase(canReceiptCreditFlag)){
					//Check Prev bill is pay
					if(canSaveCasePayPrevBill==false){
				       orderForm.setReceiptCreditFlag("-1");//no pay prev bill
					}else{
					   orderForm.setReceiptCreditFlag("1");//Can pay Credit
					}
				}else{
				   orderForm.setReceiptCreditFlag("0");//cannot pay credit
				}
				
				// get Customer Info
				Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				//set creditLimit from customer
				orderForm.setCustCreditLimit(customer.getCreditLimit());
				 
			}//if van
			
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
            
			//Split 2 Line
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), user, ""));
			
			//Merge to 1 Line show
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));

			//add Line Blank UOM (1 or 2)
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines(),productErrorMap));

			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				if(conn != null){
			       conn.close();conn=null;
				}
			}catch(Exception e){
				
			}
		}
		return orderForm;
	}
	/**
	 * Prepare to Edit receipt
	 */
	public ActionForward prepareEditReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		OrderForm orderForm = (OrderForm) form;
		Order order = null;
		int roundTrip = 0;
		User user = (User) request.getSession(true).getAttribute("user");
        logger.info("prepareEditReceipt ");
		try {
			roundTrip = orderForm.getOrder().getRoundTrip();
			
			String id = Utils.isNull(request.getParameter("id"));
			
			order = new MOrder().find(id);
			if (order == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				return mapping.findForward("view");
			}
			
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
			//add Line Blank UOM (1 or 2)
			conn = new DBCPConnectionProvider().getConnection(conn);
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines(),null));
			
			order.setRoundTrip(roundTrip);
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			
			//Filter Check Van Can Receipt Cheque Or Credit
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			if(User.VAN.equals(user.getType())){
				boolean canSaveCasePayPrevBill = OrderUtils.canSaveCreditVan(conn,user, order.getCustomerId());
				String canReceiptChequeFlag = CustomerReceiptFilterUtils.canReceiptCheque(conn,orderForm.getOrder().getCustomerId());
				String canReceiptCreditFlag = CustomerReceiptFilterUtils.canReceiptCredit(conn,orderForm.getOrder().getCustomerId());
				
				logger.debug("canSaveCasePayPrevBill:"+canSaveCasePayPrevBill);
				logger.debug("canReceiptChequeFlag:"+canReceiptChequeFlag+",canReceiptCreditFlag:"+canReceiptCreditFlag);
				
				if("Y".equalsIgnoreCase(canReceiptChequeFlag) || "Y".equalsIgnoreCase(canReceiptCreditFlag)){
					//Check Prev bill is pay
					if(canSaveCasePayPrevBill==false){
				       orderForm.setReceiptCreditFlag("-1");//no pay prev bill
					}else{
					   orderForm.setReceiptCreditFlag("1");//Can pay Credit
					}
				}else{
				   orderForm.setReceiptCreditFlag("0");//cannot pay credit
				}
				
				// get Customer Info
				Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				//set creditLimit from customer
				orderForm.setCustCreditLimit(customer.getCreditLimit());
			}
			
			/*//wit edit VAN :default Auto receipt Cash 
			if(User.VAN.equals(user.getType())){
			  orderForm.getOrder().setVanPaymentMethod("CASH");
			}*/
			
			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}finally{
			try{
				if(conn != null){
				  conn.close();conn=null;
				}
			}catch(Exception e){
				
			}
		}
		return mapping.findForward("prepareEditReceipt");
	}
	
	/**
	 * Pre-Save 
	 * :Calculate Promotion 
	 */
	public ActionForward preSave(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		OrderProcess orderProcess = new OrderProcess();
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//Validate Step Order 
			if( !ControlOrderPage.stepIsValid(request, ControlOrderPage.STEP_ORDER_2)){
				request.setAttribute("Message", "ไม่สามารถทำรายการต่อได้  เนื่องจากมีการกดปุ่ม Back อาจทำให้ข้อมูลเสียหายได้  กรุณาไปที่ หน้าจัดการข้อมูลลูกค้า อีกครั้ง");
				return mapping.findForward("prepare"); 
			}
			
			OrderForm orderForm = (OrderForm) form;
			User userActive = (User) request.getSession().getAttribute("user");
			Customer customer = new MCustomer().find(conn,String.valueOf(orderForm.getOrder().getCustomerId()));
			
			/** Case User back action init new OrderDate to CurrentDate*/
			if(orderForm.getOrder().getId() == 0){
			   logger.info("set Order Date new To  CurrentDate");
			   orderForm.getOrder().setOrderDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
			   orderForm.getOrder().setOrderTime(new SimpleDateFormat("HH:mm", new Locale("th", "TH")).format(new Date()));
			}
			
			/** Promotion Process  add to Lines */
			
			// remove promotion line
			List<OrderLine> promotionLine = new ArrayList<OrderLine>();
			
			// Set Display To Prepare Save Order Line (no promotion Lines)
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request.getSession().getAttribute("memberVIP")));
            
			// Add Promotion Line only From orderFrom to promotionLine
			for (OrderLine line : orderForm.getLines()) {
				if (line.getPromotion().equalsIgnoreCase("Y")) {
					promotionLine.add(line);
				}
			}
			
			//remove Promotion line from orderFrom
			for (OrderLine line : promotionLine) {
				orderForm.getLines().remove(line);
			}
			
			//Recalculate lineAmount (qty*price) in Lines
			orderForm.setLines(new MOrder().reCalculateLineAmountInLinesBeforeCalcPromotion(orderForm.getLines()));
			
			//Get custGroup 
			orderForm.getOrder().setCustGroup(new MCustomer().getCustGroup(conn,orderForm.getOrder().getCustomerId()));
			//Get ProvinceGroup
			orderForm.getOrder().setProvinceGroup(new MCustomer().getProvinceGroupModel(conn,orderForm.getOrder().getCustomerId()));
			
			// Call Modifier Process (Promotion)
			ModifierProcess modProcess = new ModifierProcess(ConvertNullUtil.convertToString(customer.getTerritory()).trim());
			modProcess.findModifier(orderForm.getLines(), userActive, conn,orderForm.getOrder().getCustGroup(),orderForm.getOrder().getProvinceGroup());
			
			/*************DEBUG *********************************/
			logger.info("Debug after promotion");
			new OrderProcess().debug(orderForm.getLines());
			
			// Set for web display.
			logger.debug("****** Start Set Display order ****************************************************");
	
			//Merge to 1 Line to Show
			logger.info("fillLinesShow LINE NORMAL");
			List<OrderLine> odLines = orderProcess.fillLinesShow(orderForm.getLines());

			//add Line Blank UOM (1 or 2)
			odLines = new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),odLines,null);
			
			/*************DEBUG *********************************/
			//logger.info("Debug before promotion");
			//new OrderProcess().debug(modProcess.getAddLines());
			
			//add Promotion to show
			logger.info("fillLinesShow LINE Promotion");
			List<OrderLine> promotionLines = orderProcess.fillLinesShowPromotion(modProcess.getAddLines());
			
			/** sum(qty1,qty2) Duplicate product promotion **/
	        promotionLines = orderProcess.sumQtyProductPromotionDuplicate(promotionLines);
	        
	        /*************DEBUG *********************************/
			//logger.info("Debug after promotion");
			//new OrderProcess().debug(promotionLines);
			
			//Sort by product
			try{
				Comparator<OrderLine> comparator = new Comparator<OrderLine>() {
				    public int compare(OrderLine c1, OrderLine c2) {
				        return c2.getProduct().getCode().compareTo(c2.getProduct().getCode()); // use your logic
				    }
				};
				Collections.sort(promotionLines, comparator); 
				logger.info("Debug after Sort Promotion");
				new OrderProcess().debug(promotionLines);
			}catch(Exception e){
				logger.error(e.getMessage()+":Sort Error",e);
			}
			
			//add promotion line
			odLines.addAll(promotionLines);
			
			//set to OrderLines Show
			orderForm.setLines(odLines);
			
			logger.debug("****** End  Set Display order ****************************************************");
			
			// Save Lines
			int i = 1;
			for (OrderLine line : orderForm.getLines()) {
				line.setLineNo(i++);
			}
			
			// Re-Calculate TotalAmount,vatAmount,netAmount
			orderForm.setOrder(new MOrder().reCalculateHeadAmount(orderForm.getOrder(), orderForm.getLines()));
			
			//Validate Case Credit don't have product in different group
			if(User.TT.equals(userActive.getType()) ){
				String productInvalid = new MOrder().validateProductIngroup(conn, orderForm.getLines());
				if(productInvalid.length() >0){
					productInvalid = productInvalid.substring(0,productInvalid.length()-1);
					
					request.setAttribute("Message","ไม่สามารถยันทึกข้อมูลได้     มีบางสินค้าที่ระบุ ต้องบันทึกแยก Order" );
					request.setAttribute("do_not_save", "true");
				}
			}
			
			//Set Data to Session to Print ListOrderProduct
			request.getSession().setAttribute("order_from_to_print", orderForm);
			
			//Set Prev Step Action
			ControlOrderPage.setPrevOrderStepAction(request, ControlOrderPage.STEP_ORDER_2);
			
		} catch (Exception e) {
			e.printStackTrace();
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
			
			if(User.VAN.equals(userActive.getType())){
				boolean canSaveCasePayPrevBill = OrderUtils.canSaveCreditVan(conn,userActive, orderForm.getOrder().getCustomerId());
				String canReceiptChequeFlag = CustomerReceiptFilterUtils.canReceiptCheque(conn,orderForm.getOrder().getCustomerId());
				String canReceiptCreditFlag = CustomerReceiptFilterUtils.canReceiptCredit(conn,orderForm.getOrder().getCustomerId());
				
				logger.debug("canSaveCasePayPrevBill:"+canSaveCasePayPrevBill);
				logger.debug("canReceiptChequeFlag:"+canReceiptChequeFlag+",canReceiptCreditFlag:"+canReceiptCreditFlag);
				
				if("Y".equalsIgnoreCase(canReceiptChequeFlag) || "Y".equalsIgnoreCase(canReceiptCreditFlag)){
					//Check Prev bill is pay
					if(canSaveCasePayPrevBill==false){
				       orderForm.setReceiptCreditFlag("-1");//no pay prev bill
					}else{
					   orderForm.setReceiptCreditFlag("1");//Can pay Credit
					}
				}else{
				   orderForm.setReceiptCreditFlag("0");//cannot pay credit
				}
				// get Customer Info
				Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				//set creditLimit from customer
				orderForm.setCustCreditLimit(customer.getCreditLimit());
			}
			
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

			//Split 2 Line for Save
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
						.getSession().getAttribute("memberVIP")));
			
			//Merg to 1 Line to Show
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));
			
			//Set Prev Step Action
			ControlOrderPage.setPrevOrderStepAction(request, ControlOrderPage.STEP_ORDER_1);
			
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
		long orderId = 0;
		String msg = InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc();
		String org = "";
		String subInv ="";
		OrgRuleBean orgRuleBean;
		User user = (User) request.getSession(true).getAttribute("user");
		Map<String, String> productErrorMap = null;
		try {
			logger.debug("save action");
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//Validate Step Order 
			if( !ControlOrderPage.stepIsValid(request, ControlOrderPage.STEP_ORDER_3)){
				request.setAttribute("Message", "ไม่สามารถทำรายการต่อได้  เนื่องจากมีการกดปุ่ม Back อาจทำให้ข้อมูลเสียหายได้  กรุณาไปที่ หน้าจัดการข้อมูลลูกค้า อีกครั้ง");
				return "view";
			}
			
			//clear Session Temp to Print ListOrderProduct
			request.getSession().setAttribute("order_from_to_print",null);
			
			orderId = orderForm.getOrder().getId();

			// check Token
			if (!isTokenValid(request)) {
			
				// VAN && TT
				Customer customer = new MCustomer().find(conn,String.valueOf(orderForm.getOrder().getCustomerId()));
				orderForm.setOrder(new Order());
				orderForm.getOrder().setCustomerId(customer.getId());
				orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				// from customer or member
				orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
				orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
				orderForm.getOrder().setVatCode(customer.getVatCode());
					
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
				Map<String, String> itemMap = new MOrgRule().getOrgRuleItemMap(conn,org, subInv) ;
				
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
			logger.debug("order.getNetAmount["+order.getNetAmount()+"]");
			
			tt = tt.setScale(2,BigDecimal.ROUND_HALF_UP);
			vt = vt.setScale(2,BigDecimal.ROUND_HALF_UP);
			nt = nt.setScale(2,BigDecimal.ROUND_HALF_UP);
			
			logger.debug("nt["+nt+"]");
			
			order.setTotalAmount(tt.doubleValue());
			order.setVatAmount(vt.doubleValue());
			order.setNetAmount(nt.doubleValue());
			// set interfaces & payment & docstatus
			order.setInterfaces("N");
			order.setPayment("N");
			order.setDocStatus(Order.STATUS_RESERVE);
			
			Address billAddr = new MAddress().find(conn,""+order.getBillAddressId());
			if(billAddr!=null)
				order.setOraBillAddressID(billAddr.getReferenceId());
			
			Address shipAddr = new MAddress().find(conn,""+order.getShipAddressId());
			if(shipAddr!=null)
				order.setOraShipAddressID(shipAddr.getReferenceId());

			
			// Begin Transaction
			conn.setAutoCommit(false);

			int i = 1;

			if (!order.getOrderType().equalsIgnoreCase(Order.DIRECT_DELIVERY)) {
				orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
						.getSession().getAttribute("memberVIP")));
			}
		
			for (OrderLine line : orderForm.getLines()) {
				line.setLineNo(i++);
				line.setPayment("N");
			}

			// Sales Reps --current user who's create/edit record--
			order.setSalesRepresent(userActive);

			//Recalculate lineAmount (qty*price) ,total_amount = lineAmount-discount in Lines
			orderForm.setLines(new MOrder().reCalculateLineAmountInLinesAfterCalcPromotion(orderForm.getLines()));
			
			// Re-Calculate totalAmount vatAmount netAmount From lines
			order = new MOrder().reCalculateHeadAmount(order, orderForm.getLines());
			            
			//For compare after save 
			double beforeSave_NetAmount = order.getNetAmount(); 
			
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
			
			//control code 
			if(ControlCode.canExecuteMethod("Stock", "checkStock")){
				//before reserver stock clear old reserve stock
				List<OrderLine> dbLines = new MOrderLine().lookUp(order.getId());
				//case status ='RESERVE' in LINE  delete reservation in stock onhand
				for (OrderLine line : dbLines) {
					if(line.getReservationId() != 0){
					  logger.info("delete reserve Stock By ReservationId:"+line.getReservationId());
					  InterfaceOrderProcess.deleteStockReservation(line.getReservationId());
					}//if
				}//for
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

			//Recalculate totalAmount ,VatAmount,NetAmount from lines(from db)
			order = new MOrder().reCalculateHeadAmountDB(conn,order);
			//Compare before Save
			double afterSave_NetAmount = order.getNetAmount(); 
			
			//case Error netAmount before <> after save  resave
			if(beforeSave_NetAmount != afterSave_NetAmount){
			   logger.debug("ReSave Order netAmount error: beforeSave_NetAmount["+beforeSave_NetAmount+"]afterSave_NetAmount["+afterSave_NetAmount+"]");
			   // Re-Save
			   new MOrder().save(order, userActive.getId(), conn);
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

			logger.debug("VanPaymentMethod:"+orderForm.getOrder().getVanPaymentMethod());
			/** Case Van and User choose Cash -> createAutoReceipt**/
			if (userActive.getRole().getKey().equals(User.VAN) 
					&& "CASH".equalsIgnoreCase(orderForm.getOrder().getVanPaymentMethod())) {
				 logger.debug("CreateAutoReceipt Case Van pay Cash");
				 
				 orderForm.getAutoReceipt().setReceiptNo(order.getOrderNo());
				 orderForm.getAutoReceipt().setReceiptAmount(order.getNetAmount());
				 
				 logger.debug("PDPAID ="+user.isPDPaid());
				 //case PDPAID =Y use default bank 002
				 if(user.isPDPaid()){
				    orderForm.getAutoReceipt().setInternalBank("002");//SCB-สาขาสาธุประดิษฐ์ 068-2-81805-7
			     }else{
			    	//PD_PAID =N use internal_bank from t_bank_transfer (internal_bank not found ->no export t_receipt)
			    	orderForm.getAutoReceipt().setInternalBank("");
			     }
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
				 
				 //set order isCash =Y
				 order.setIsCash("Y");
				 
				 new OrderProcess().createAutoReceipt(orderForm.getAutoReceipt(), order, orderForm.getLines(), receiptByList, null, userActive, conn);
				 //set msg 
				 msg = SystemMessages.getCaption("SaveSucess", Utils.local_th);
			
			}else if (userActive.getRole().getKey().equals(User.VAN) && user.isPDPaid()
					&& "CREDIT".equalsIgnoreCase(orderForm.getOrder().getVanPaymentMethod())) {
				
				logger.debug("CreateAutoReceipt Case Van pay Credit");
				
				//Case Sale set PDPAID =Y -> Pay Credit set receipt ispdpaid =N
				 orderForm.getAutoReceipt().setIsPDPaid("N");
				 
				//set receipt 
				 orderForm.getAutoReceipt().setReceiptNo(order.getOrderNo());
				 orderForm.getAutoReceipt().setReceiptAmount(order.getNetAmount());
				 orderForm.getAutoReceipt().setInternalBank("002");//SCB-สาขาสาธุประดิษฐ์ 068-2-81805-7
				 orderForm.getAutoReceipt().setReceiptDate(orderForm.getOrder().getOrderDate());
				 
				 /** Set ReceiptBy Manual **/
				 List<ReceiptBy> receiptByList = new ArrayList<ReceiptBy>();
				 ReceiptBy receiptBy = new ReceiptBy();
				 receiptBy.setId(0);
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
				 //process auto receipt 
				 
				 //Case Money_to_pens ='Y'  set exported='Z' no export
				 if("Y".equalsIgnoreCase(user.getMoneyToPens())){
				     new OrderProcess().createAutoReceipt_PDPAID_MONEYTOPENS(orderForm.getAutoReceipt(), order, orderForm.getLines(), receiptByList, null, userActive, conn);
				 }else{
					 //Case Money_to_pens ='N'  set exported ='N' export normal
					 new OrderProcess().createAutoReceipt(orderForm.getAutoReceipt(), order, orderForm.getLines(), receiptByList, null, userActive, conn); 
				 }
				 //set msg 
				 msg = SystemMessages.getCaption("SaveSucess", Utils.local_th);
				
			}else{
				//Case Van PD_PAID=N ->Don't create Receipt
				if(userActive.getRole().getKey().equals(User.VAN)){
				    msg  = SystemMessages.getCaption("SaveSucess", Utils.local_th);
				}
			}
			
			/** WIT Edit 20110804 ****************************************/
            //set Merge to 1 Line to show
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));
			
			// re org case line_no duplicate
			new MOrderLine().reOrgLineNo(orderForm.getOrder().getId(), conn);
			
			//save print pick date
			if (userActive.getRole().getKey().equals(User.VAN)){
			  new MOrder().updatePrintPickStamp(conn, order);
			}
			
			// Commit Transaction
			conn.commit();
			
			//control code 
			if(ControlCode.canExecuteMethod("Stock", "checkStock")){
				
				//Generate Interfaces Order To Oracle Temp
				//return productErrorMap cannot reserve order to display sales
				productErrorMap = InterfaceOrderProcess.reserveStock(user,orderForm.getOrder().getId(),orderForm.getOrder().getOrderNo());
				
				if(productErrorMap != null && !productErrorMap.isEmpty()){
				     orderForm.getOrder().setDocStatus(Order.STATUS_UNAVAILABLE);
				     request.setAttribute("Message","ไม่สามารถจองสินค้าได้ โปรดตรวจสอบสต๊อกสินค้าอีกครั้ง  จากนั้น แก้ไขออเดอร์ให้ถูกต้อง " );
					
				     orderForm = prepareEditOrderCaseCannotReserve(orderForm, user,productErrorMap);
				     
				 	 //Set Prev Step Action :set to StepOrder1 (for edit order)
					 ControlOrderPage.setPrevOrderStepAction(request, ControlOrderPage.STEP_ORDER_1);
					
				     return "prepareEditOrder";//gotoPage SalesOrder.jsp for edit 
				}else{
				   // set msg save success
				   request.setAttribute("Message",msg );
				}
			}else{
				 // set msg save success
				 request.setAttribute("Message",msg );
			}
			
			//add Line Blank UOM (1 or 2)
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines(),productErrorMap));
			
			/********* WIT EDIT:20110804 :case not auto receipt cash  -> popup page AutoPay *****/
			if(userActive.getRole().getKey().equals(User.VAN) &&  !orderForm.getOrder().isPaymentCashNow()){
				request.setAttribute("popup_autoreceipt", "true");
			}
			/**************************************************************/
			
			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
			// save token
			saveToken(request);
			
			//Clear Prev Step Action -success 
			ControlOrderPage.setPrevOrderStepAction(request, "");
			
			/** 
			* Process run after this action 
			* get sql manual script from 'c_after_action_sql' 
			* and run script by action name 
			**/
			ProcessAfterAction.processAfterAction(ProcessAfterAction.SAVE_ORDER,orderForm.getOrder().getOrderNo());

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
				
				// VAN && TT
				Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				orderForm.setOrder(new Order());
				orderForm.getOrder().setCustomerId(customer.getId());
				orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				// from customer or member
				orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
				orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
				orderForm.getOrder().setVatCode(customer.getVatCode());
				
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

			logger.info("action:"+Utils.isNull(request.getParameter("actionSave")));
			
		   //Case Van pd_paid =Y and and payment method ='CR' Create AutoReceipt actionSave='saveAutoReceipt' 
	       if(Utils.isNull(request.getParameter("actionSave")).equalsIgnoreCase("saveAutoReceipt")){
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
	        }else{
	        	//Case Van and pd_paid =N and payment method ='CR' no Create AutoReceipt actionSave ="" **/
	        	
	        }
	       
			// Set Lines to Show
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
			
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
				if(conn != null){conn.rollback();}
			} catch (Exception e2) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(conn != null){
					conn.close();
				}
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
		String action = Utils.isNull(request.getParameter("action"));
		Customer custByUserId = null;
		try {
			logger.debug("action:"+action +"customerId:"+ orderForm.getOrder().getCustomerId());
			if( "new".equalsIgnoreCase(action)){
				if( orderForm.getOrder().getCustomerId()!=0){
				   custByUserId = new MCustomer().findByWhereCond(" where user_id = "+user.getId());// Integer.parseInt(CUSTOMER_ID_MEYA_FIXED);
				}
				OrderCriteria criteria = new OrderCriteria();
				//criteria.setSearchKey(searchKey)
				orderForm.setCriteria(criteria);
				Order order = new Order();
				order.setOrderDateFrom(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				// get Customer Info
				if(custByUserId != null){
					Customer customer = new MCustomer().find(String.valueOf(custByUserId.getId()));
					order.setCustomerId(custByUserId.getId());
					order.setCustomerName(customer.getCode()+"-"+customer.getName());
				}
				orderForm.setOrder(order);
			}else{
				
				request.getSession().removeAttribute("isAdd");
	           
				OrderCriteria criteria = getSearchCriteria(request, orderForm.getCriteria(), this.getClass().toString());
				orderForm.setCriteria(criteria);
				
				Order order = orderForm.getOrder();
				//logger.debug("Teritery:"+order.getTerritory());
				
				String whereCause = "";
				if ( !Utils.isNull(order.getOrderNo()).equals("")) {
					whereCause += " AND o.ORDER_NO LIKE '%"
							+ order.getOrderNo().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' \n";
				}
				if ( !Utils.isNull(order.getSalesOrderNo()).equals("")) {
					whereCause += " AND o.SALES_ORDER_NO LIKE '%"
							+ order.getSalesOrderNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
							+ "%' \n";
				}
				if ( !Utils.isNull(order.getArInvoiceNo()).equals("")) {
					whereCause += " AND o.AR_INVOICE_NO LIKE '%"
							+ order.getArInvoiceNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
							+ "%' \n";
				}
				if ( !Utils.isNull(order.getDocStatus()).equals("")) {
					whereCause += " AND o.DOC_STATUS = '" + order.getDocStatus() + "' \n";
				}
	
				if ( !Utils.isNull(order.getOrderDateFrom()).equals("") && !Utils.isNull(order.getOrderDateTo()).equals("")) {
					whereCause += "AND o.order_date  >= to_date('"+DateUtil.convBuddhistToChristDate(order.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy')  ";
					whereCause += "AND o.order_date  <= to_date('"+DateUtil.convBuddhistToChristDate(order.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy')  ";
					
				}else if ( !Utils.isNull(order.getOrderDateFrom()).equals("") && Utils.isNull(order.getOrderDateTo()).equals("")) {
					whereCause += "AND o.order_date  = to_date('"+DateUtil.convBuddhistToChristDate(order.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/MM/yyyy')  ";
				}
				if( !user.getCode().equalsIgnoreCase("ADMIN")){
				   whereCause += " AND o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' \n";
				   whereCause += " AND o.USER_ID = " + user.getId() +" \n";
				}
				if(order.getCustomerId() !=0){
				   whereCause += " AND o.CUSTOMER_ID = " + order.getCustomerId() + " \n";
				}
				if( !Utils.isNull(order.getCustomerCode()).equals("") || !Utils.isNull(order.getCustomerName()).equals("") 
					|| !"".equals(Utils.isNull(order.getTerritory()))){
					 whereCause += " AND o.CUSTOMER_ID IN ( \n";
					 whereCause += "  select customer_id from pensso.m_customer where 1=1 \n";
					 if( !Utils.isNull(order.getCustomerCode()).equals("")){
					    whereCause += " and code like '%"+Utils.isNull(order.getCustomerCode())+"%'\n";
				     }
					 if( !Utils.isNull(order.getCustomerName()).equals("")){
						whereCause += " and name like '%"+Utils.isNull(order.getCustomerName())+"%'\n";
					 }
					 if ( !"".equals(Utils.isNull(order.getTerritory())) ){
						whereCause += "\n AND TERRITORY = '" + Utils.isNull(order.getTerritory()) + "'";
					 }
					 whereCause += " )\n";
				}
				
				if ( order.getSearchProvince() != 0) {
					whereCause += "\n AND a.province_id = " + order.getSearchProvince();
				}
				if ( !"".equals(Utils.isNull(order.getDistrict())) && !"0".equals(Utils.isNull(order.getDistrict())) ){
					whereCause += "\n AND a.district_id = " + order.getDistrict() + "";
				}
				whereCause += "\n ORDER BY o.ORDER_DATE DESC,o.ORDER_NO DESC ";
				Order[] results = new MOrder().searchOpt(whereCause);
	
				orderForm.setResults(results);
				if (results != null) {
					orderForm.getCriteria().setSearchResult(results.length);
				} else {
					request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				}
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}
	/**
	 * Search
	 */
	protected String search_BK(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			whereCause += " AND USER_ID = " + user.getId();

			whereCause += " ORDER BY ORDER_DATE DESC,ORDER_NO DESC ";
			Order[] results = new MOrder().searchOpt(whereCause);

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
		try{
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
		}catch(Exception e){
			logger.error(e.getMessage(),e);
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
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		try {
			boolean baddr = false;
			String fileType = request.getParameter("fileType");
			String orderId = request.getParameter("orderId");
			String visitDate = request.getParameter("visitDate");
			String i = Utils.isNull(request.getParameter("i"));
			
			logger.debug("fileType:"+fileType);
			logger.debug("orderId:"+orderId);
			logger.debug("visitDate:"+visitDate);
			logger.debug("i:"+i);
			
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

			parameterMap.put("p_receiptNo", receiptNo.length() != 0 ? ReportHelper.convertOrderNoForReport(receiptNo) : ReportHelper.convertOrderNoForReport(order.getOrderNo()));
			
			parameterMap.put("p_vatcode", order.getVatCode());
			parameterMap.put("p_address", address.getLineString());
			parameterMap.put("p_orderDate", order.getOrderDate());
			parameterMap.put("p_code", user.getCode());
			parameterMap.put("p_name", user.getName());
			parameterMap.put("p_taxNo", BeanParameter.getPensTaxNo());
			
			parameterMap.put("p_customerCode", customer.getCode());
			parameterMap.put("p_customerName", customer.getName());
			parameterMap.put("p_custTaxNo", "".equals(Utils.isNull(customer.getTaxNo()))?null:Utils.isNull(customer.getTaxNo()));

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
				taxInvoice.setNetAmount(taxInvoice.getTotalAmount()+taxInvoice.getVatAmount());
				
				totalAmount += line.getTotalAmount();
				totalVatAmount += line.getVatAmount();
				totalNetAmount += taxInvoice.getTotalAmount()+taxInvoice.getVatAmount();
				lstData.add(taxInvoice);
			}
			// parameterMap.put("p_next_visit", request.getParameter("nextVisitDate"));
			parameterMap.put("p_next_visit", visitDate);
			
			parameterMap.put("p_sum_total_amount",Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit));
			parameterMap.put("p_sum_total_vat", Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit));
			parameterMap.put("p_sum_total_net",Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit));
			
			conn = DBConnection.getInstance().getConnection();
			boolean haveVat = new MOrder().isOrderHaveVat(conn, order.getId());
			logger.debug("haveVat:"+haveVat);
			if(haveVat==false){
				parameterMap.put("p_vatcode", "-");
				parameterMap.put("p_sum_total_vat","0.00");
			}

			String fileName = "tax_invoice_report";
			//WIT Edit :21/07/2011 :Special Case Show PDF user another report tax_invoice_pdf_report 
			//because : print and pdf not support same fonts
			if (fileType.equals(SystemElements.PDF)) {
				fileName = "tax_invoice_pdf_report";
			}
			String fileJasper = BeanParameter.getReportPath() + fileName;

			logger.debug("fileName:"+fileName);
			// for sale user.
			if (i.equals("0")) {
				logger.debug("print 1 Receipt_TaxInvoice");
				parameterMap.put("report_title", bundle.getString("Receipt_TaxInvoice"));
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
		
				// set for print report Shipment Report
				request.setAttribute("report_name", "printReport2");
				request.setAttribute("i", "1");
				request.setAttribute("orderId", orderId);
				request.setAttribute("visitDate", visitDate);
				request.setAttribute("fileType", fileType);
			} else {
				// for customer.
				logger.debug("print 2 Shipment_TaxInvoice");
				parameterMap.put("report_title", bundle.getString("Shipment_TaxInvoice"));
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("print2");
	}
	
	public ActionForward printReport2(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Search for report2 : " + this.getClass());
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
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		try {
			boolean baddr = false;
			String fileType = request.getParameter("fileType");
			String orderId = request.getParameter("orderId");
			String visitDate = request.getParameter("visitDate");
			String i = Utils.isNull(request.getParameter("i"));
			
			logger.debug("fileType:"+fileType);
			logger.debug("orderId:"+orderId);
			logger.debug("visitDate:"+visitDate);
			logger.debug("i:"+i);
			
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

			parameterMap.put("p_receiptNo", receiptNo.length() != 0 ? ReportHelper.convertOrderNoForReport(receiptNo) : ReportHelper.convertOrderNoForReport(order.getOrderNo()));
			
			parameterMap.put("p_vatcode", order.getVatCode());
			parameterMap.put("p_address", address.getLineString());
			parameterMap.put("p_orderDate", order.getOrderDate());
			parameterMap.put("p_code", user.getCode());
			parameterMap.put("p_name", user.getName());
			parameterMap.put("p_taxNo", BeanParameter.getPensTaxNo());
			
			parameterMap.put("p_customerCode", customer.getCode());
			parameterMap.put("p_customerName", customer.getName());
			parameterMap.put("p_custTaxNo", "".equals(Utils.isNull(customer.getTaxNo()))?null:Utils.isNull(customer.getTaxNo()));

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
				taxInvoice.setNetAmount(taxInvoice.getTotalAmount()+taxInvoice.getVatAmount());
				
				totalAmount += line.getTotalAmount();
				totalVatAmount += line.getVatAmount();
				totalNetAmount += taxInvoice.getTotalAmount()+taxInvoice.getVatAmount();
				lstData.add(taxInvoice);
			}
			// parameterMap.put("p_next_visit", request.getParameter("nextVisitDate"));
			parameterMap.put("p_next_visit", visitDate);
			
			parameterMap.put("p_sum_total_amount",Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit));
			parameterMap.put("p_sum_total_vat", Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit));
			parameterMap.put("p_sum_total_net",Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit));
			
			conn = DBConnection.getInstance().getConnection();
			boolean haveVat = new MOrder().isOrderHaveVat(conn, order.getId());
			logger.debug("haveVat:"+haveVat);
			if(haveVat==false){
				parameterMap.put("p_vatcode", "-");
				parameterMap.put("p_sum_total_vat","0.00");
			}

			String fileName = "tax_invoice_report";
			//WIT Edit :21/07/2011 :Special Case Show PDF user another report tax_invoice_pdf_report 
			//because : print and pdf not support same fonts
			if (fileType.equals(SystemElements.PDF)) {
				fileName = "tax_invoice_pdf_report";
			}
			String fileJasper = BeanParameter.getReportPath() + fileName;

			logger.debug("fileName:"+fileName);
			
			// for customer.
			logger.debug("print 2 Shipment_TaxInvoice");
			parameterMap.put("report_title", bundle.getString("Shipment_TaxInvoice"));
			reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);

			request.setAttribute("printShipment_TaxInvoiceSuccess", "success");
		} catch (Exception e) {
			//e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return mapping.findForward("print2");
	}
	
	public ActionForward printReportSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("printReportSummary : " + this.getClass());
		OrderForm reportForm = (OrderForm) form;
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
		String fileName = "tax_invoice_summary_report";
		try {
			boolean baddr = false;
			String fileType =  request.getParameter("fileType");
			
			
			String orderId = request.getParameter("orderId");
			String visitDate = request.getParameter("visitDate");
			String reportType = request.getParameter("reportType");
            
			logger.debug("reportType:"+reportType);
			
			order = new MOrder().find(orderId);
			reportForm.setOrder(order);
			lines = new MOrderLine().lookUp(reportForm.getOrder().getId());
			reportForm.setLines(new OrderProcess().fillLinesShow(lines));
			lines = reportForm.getLines();
			
			String receiptNo = new MReceipt().getLastestReceiptFromOrder(order.getId());

			//Check Cash or Cheque
			String pReceiptByMsg = " ";
			String pReportTitle = ""; //default
			List<ReceiptBy> receiptByList = new MReceiptBy().lookUp(receiptNo);
			
			if(receiptByList != null && receiptByList.size() >0){
				Map<String, String> paybyMap = new HashMap<String, String>();
				String pReceiptByMsgTemp = "";
				for(int i=0;i<receiptByList.size();i++){
					ReceiptBy receiptBy = receiptByList.get(i);
					if("CS".equalsIgnoreCase(receiptBy.getPaymentMethod())){
						if("Y".equals(order.getIsCash())){
							pReceiptByMsgTemp = "เงินสด";	
							paybyMap.put("CS",pReceiptByMsgTemp);
						}else{
							pReceiptByMsgTemp = "เงินเชื่อ";
							paybyMap.put("CR",pReceiptByMsgTemp);
						}
					}else if("CH".equalsIgnoreCase(receiptBy.getPaymentMethod())){
						pReceiptByMsgTemp = "เช็ค เลขที่  "+receiptBy.getChequeNo();
						paybyMap.put("CH",pReceiptByMsgTemp);
					}
				}//for
				
				if( !paybyMap.isEmpty()){
					Iterator<String> it = paybyMap.keySet().iterator();
					while(it.hasNext()){
						pReceiptByMsg +=paybyMap.get(it.next())+" ,";
					}
					if(pReceiptByMsg.length()> 0 && pReceiptByMsg.indexOf(",") != -1){
						pReceiptByMsg = pReceiptByMsg.substring(0,pReceiptByMsg.length()-1);
					}
				}
			}
			//default cancel_msg
			//parameterMap.put("p_cancel_msg", "");
			
			//original or copy report
			//Cash
			if("original".equalsIgnoreCase(reportType)){
				pReportTitle ="ใบส่งสินค้า/ใบกำกับภาษี/ใบเสร็จรับเงิน";
				fileName = "tax_invoice_summary_new_report";
				parameterMap.put("p_sign_name_1", "พนักงานขาย");
				parameterMap.put("p_sign_name_2", "ผู้รับสินค้า/ผู้ชำระเงิน");
				parameterMap.put("p_sign_name_3", "ผู้ช่วยพนักงานขาย");
				
			}else if("copy".equalsIgnoreCase(reportType)){ 
				pReportTitle = "ใบส่งสินค้า/ใบเสร็จรับเงินชั่วคราว"; //default
				fileName = "tax_invoice_summary_new_report";
				parameterMap.put("p_sign_name_1", "พนักงานขาย");
				parameterMap.put("p_sign_name_2", "ผู้รับสินค้า/ผู้ชำระเงิน");
				parameterMap.put("p_sign_name_3", "ผู้ช่วยพนักงานขาย");
				
				//case Print cancel Order
				if(Utils.isNull(request.getParameter("statusOrder")).equalsIgnoreCase("VO")){
					pReportTitle = "ใบส่งสินค้า/ใบเสร็จรับเงินชั่วคราว (ยกเลิก)";
				}
			
			//Credit
			}else if("tax".equalsIgnoreCase(reportType)){ 
				pReportTitle = "ใบส่งของ/ใบกำกับภาษี"; //พิมพ์ใบส่งของ/ใบกำกับภาษี
				fileName = "tax_invoice_summary_new_report";
				
				parameterMap.put("p_sign_name_1", "พนักงานขาย");
				parameterMap.put("p_sign_name_2", "ผู้รับสินค้า");
				parameterMap.put("p_sign_name_3", "ผู้ช่วยพนักงานขาย");
			}else if("bill".equalsIgnoreCase(reportType)){ 
				pReportTitle = "ใบเสร็จรับเงิน"; //พิมพ์ใบเสร็จรับเงิน
				fileName = "tax_invoice_summary_report";
			}
			customer = new MCustomer().find(String.valueOf(order.getCustomerId()));

			parameterMap.put("p_report_title", pReportTitle);
			parameterMap.put("p_receiptNo", receiptNo.length() != 0 ? ReportHelper.convertOrderNoForReport(receiptNo) : ReportHelper.convertOrderNoForReport(order.getOrderNo()));
			parameterMap.put("p_vatcode", order.getVatCode());
			parameterMap.put("p_orderDate", order.getOrderDate());
			parameterMap.put("p_code", user.getCode());
			parameterMap.put("p_name", user.getName());
			parameterMap.put("p_taxNo", BeanParameter.getPensTaxNo());
			parameterMap.put("p_receipt_by_msg", pReceiptByMsg);
			parameterMap.put("p_create_date", order.getCreated());
			
			conn = DBConnection.getInstance().getConnection();
			
			//Split address to Show 2 Line
			String[] custAddressArr = new String[2];
			Address addressTemp = new MAddress().findAddressByCustomerId(conn,customer.getId()+"");
			if(addressTemp != null ){
				custAddressArr = splitAddress(addressTemp);
			}
			//
			String cusTaxNoMsg = "";
			
			//"".equals(Utils.isNull(customer.getTaxNo()))?null:Utils.isNull(customer.getTaxNo())
			if(Utils.isNull(customer.getPrintTax()).equals("Y")){
				cusTaxNoMsg ="เลขประจำตัวผู้เสียภาษี "+customer.getTaxNo(); 
			}else{
				//cusTaxNoMsg ="เลขประจำตัวผู้เสียภาษี "+BeanParameter.getPensTaxNo();
			}
			
			if(Utils.isNull(customer.getPrintHeadBranchDesc()).equals("Y")){
				if(Utils.isNull(customer.getPrintType()).equals("H")){
				     cusTaxNoMsg +="  สำนักงานใหญ่";
				}else if(Utils.isNull(customer.getPrintType()).equals("B")){
					 cusTaxNoMsg +=" สาขาที่  "+NumberToolsUtil.decimalFormat(Integer.parseInt(customer.getPrintBranchDesc()),NumberToolsUtil.format_current_five_digit);
				}
			}
			
			parameterMap.put("custName", order.getCustomerName());
			parameterMap.put("custAddress1", custAddressArr[0]);
			parameterMap.put("custAddress2", custAddressArr[1]);
			parameterMap.put("p_custTaxNo", cusTaxNoMsg);
			parameterMap.put("p_next_visit", visitDate);
			
			lstData = new ArrayList<TaxInvoiceReport>();
			int id = 1;
			int no =1;
			for (OrderLine line : lines) {
				taxInvoice = new TaxInvoiceReport();
				taxInvoice.setOrderID(String.valueOf(order.getId()));
				taxInvoice.setId(id++);
				//taxInvoice.setAddress(address.getLineString());
				taxInvoice.setOrderDate(order.getOrderDate());
				taxInvoice.setTaxNo("Tax No in Line");
				taxInvoice.setCode(user.getCode());
				taxInvoice.setName(user.getName());
			
				taxInvoice.setCustomerCode(customer.getCode());
				taxInvoice.setCustomerName(customer.getName());
				
				if("Y".equalsIgnoreCase(line.getPromotion())){
				  taxInvoice.setProductCode("*"+no+")"+line.getProduct().getCode());
				}else{
				  taxInvoice.setProductCode(no+")"+line.getProduct().getCode());	
				}
				
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
				no++;
			}
			
			/*logger.debug("lstData size:"+lstData.size());
			TaxInvoiceReport debug = lstData.get(lstData.size()-1);
			logger.debug("ID:"+debug.getId()+",ProductCode:"+debug.getProductCode());*/

			logger.debug("order.getTotalAmount():"+order.getTotalAmount());
			logger.debug("order.getVatAmount():"+order.getVatAmount());
			logger.debug("order.getNetAmount():"+order.getNetAmount());
			
			parameterMap.put("p_sum_total_amount", order.getTotalAmount());
			parameterMap.put("p_sum_total_vat",Utils.decimalFormat(order.getVatAmount(),Utils.format_current_2_disgit));
			parameterMap.put("p_sum_total_net", order.getNetAmount());
			
			boolean haveVat = new MOrder().isOrderHaveVat(conn, order.getId());
			if(haveVat==false){
				parameterMap.put("p_vatcode", "-");
				parameterMap.put("p_sum_total_vat","0.00");
			}
			
			/*if("copy".equalsIgnoreCase(reportType)){
				if("2".equalsIgnoreCase(caseReport)){
				  fileName = "tax_invoice_summary_2_report";
				}
			}*/
			
			//debug
			//fileName = "tax_invoice_summary_new_pdf_report";//test
			//fileType ="PDF";//test
			
			logger.info("Report Name:"+fileName);
			String fileJasper = BeanParameter.getReportPath() + fileName;
			reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
				
			//stamp dateTime print
			new MOrder().updatePrintTaxInvoiceStamp(conn,order.getOrderNo(), pReportTitle);
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();conn=null;
			} catch (Exception e2) {}
		}
		return mapping.findForward("print");
	}
	
	public String[] splitAddress(Address a) {
		String[] lineString = new String[2];
		String line1 = Utils.isNull(a.getLine1()) + " "+Utils.isNull(a.getLine2()) + " ";
		if ("กรุงเทพฯ".equalsIgnoreCase(a.getProvince().getName())
				|| "กรุงเทพมหานคร".equalsIgnoreCase(a.getProvince().getName())) {
			        line1 += "แขวง"
                          + (Utils.isNull(a.getLine3())) + " ";
						 
			        
			lineString[0] = line1;
			lineString[1] = " เขต"
					  + (Utils.isNull(a.getDistrict().getName())) 
					  + " "+(Utils.isNull(a.getProvince().getName())) + " "+(Utils.isNull(a.getPostalCode()));
			
		} else {
			lineString[0] = line1;
			
			lineString[1] =  " ต." + (Utils.isNull(a.getLine3())) + " อ."
					 + (Utils.isNull(a.getDistrict().getName())) 
					 + " จ."+(Utils.isNull(a.getProvince().getName())) + " "+(Utils.isNull(a.getPostalCode()));
			
		}
		return lineString;
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward printListOrderProductReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Search for report : " + this.getClass());
		OrderForm orderForm = (OrderForm) form;
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		Connection conn = null;
		boolean isBillhaveVat = true;
		try{
			
			orderForm = (OrderForm)request.getSession().getAttribute("order_from_to_print");
			/**Debug **/
			logger.debug("CustomerId:"+orderForm.getOrder().getCustomerId());
			logger.debug("Lines:"+orderForm.getLines().size());
			
		    conn = DBConnection.getInstance().getConnection();
			ListOrderProductReportProcess process = new ListOrderProductReportProcess();
			User user = (User)request.getSession().getAttribute("user");
			
			//Split address to Show 2 Line
			String[] custAddressArr = new String[2];
			Address address = new MAddress().findAddressByCustomerId(conn,request.getParameter("customerId"));
			if(address != null ){
				custAddressArr = splitAddress(address);
			}

			List<ListOrderProductReport> lstData = new ArrayList<ListOrderProductReport>();
			//Disply Item Line
			int no =1;
			for (OrderLine line : orderForm.getLines()) {
				ListOrderProductReport b = new ListOrderProductReport();
				if("Y".equalsIgnoreCase(line.getPromotion())){
				  b.setProduct("*"+no+")"+line.getProduct().getCode()+" "+line.getProduct().getName());
				}else{
				  b.setProduct(no+")"+line.getProduct().getCode()+" "+line.getProduct().getName());
				}
				b.setQty(NumberToolsUtil.decimalFormat(line.getQty1(),NumberToolsUtil.format_current_no_disgit)+"/"+NumberToolsUtil.decimalFormat(line.getQty2(),NumberToolsUtil.format_current_no_disgit));
				b.setUnit(Utils.isNull(line.getUom1().getCode())+"/"+Utils.isNull(line.getUom2().getCode()));
				b.setPrice(NumberToolsUtil.decimalFormat(line.getPrice1(),NumberToolsUtil.format_current_2_disgit)+"/"+NumberToolsUtil.decimalFormat(line.getPrice2(),NumberToolsUtil.format_current_2_disgit));
				no++;
				
				lstData.add(b);
				
				//check bill is have vat or not
				if(Utils.isNull(line.getTaxable()).equalsIgnoreCase("N")){
					isBillhaveVat = false;
				}
				
			}
			logger.debug("List Data Size:"+lstData.size());
			
		    //Param Report
			String fileName = "list_order_product_report";
			String fileType = SystemElements.PRINTER;
			String fileJasper = BeanParameter.getReportPath() + fileName;
			
			//Set Report Parameter Map
			logger.debug("order.getTotalAmount():"+orderForm.getOrder().getTotalAmount());
			logger.debug("order.getVatAmount():"+orderForm.getOrder().getVatAmount());
			logger.debug("order.getNetAmount():"+orderForm.getOrder().getNetAmount());
			
			
			parameterMap.put("p_sum_total_amount", orderForm.getOrder().getTotalAmount());
			parameterMap.put("p_sum_total_vat", orderForm.getOrder().getVatAmount());
			parameterMap.put("p_sum_total_net", orderForm.getOrder().getNetAmount());
			
			//Case No vat
			if(isBillhaveVat==false){
			   parameterMap.put("p_vatcode","-");
			   parameterMap.put("p_sum_total_vat", "0.00");
			}else{
			   //case have vat
			   parameterMap.put("p_vatcode", orderForm.getOrder().getVatCode());
			   parameterMap.put("p_sum_total_vat",Utils.decimalFormat(orderForm.getOrder().getVatAmount(),Utils.format_current_2_disgit));
			}
			parameterMap.put("totalLine", lstData.size()+"");
			parameterMap.put("userName", user.getName());
			parameterMap.put("custName", orderForm.getOrder().getCustomerName());
			parameterMap.put("custAddress1", custAddressArr[0]);
			parameterMap.put("custAddress2", custAddressArr[1]);
			parameterMap.put("report_title", "list_order_product_report");
			
			 
			//start Report
		    reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
			
		    //pdf
			/*parameterMap.put("report_title", "list_order_product_pdf_report");
			fileName = "list_order_product_pdf_report";
			fileType = SystemElements.PDF;
			fileJasper = BeanParameter.getReportPath() + fileName;
		    reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData);*/
		    
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		// return null;
		return mapping.findForward("print");
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
           
			//display order detail
			Order order = new MOrder().find(orderForm.getOrder().getId()+"");
			List<OrderLine> lstLines = new MOrderLine().lookUp(order.getId());
			
			//case status ='RESRVE' in LINE  delete reservation in stock
			for (OrderLine line : lstLines) {
				if(line.getReservationId() != 0){
				  logger.info("delete reserve Stock By ReservationId:"+line.getReservationId());
				  InterfaceOrderProcess.deleteStockReservation(line.getReservationId());
				}
			}
			
			
			List<OrderLine> lines = new OrderProcess().fillLinesShow(lstLines);
			orderForm.setLines(lines);
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
            
			//Split 2 Line
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), user, (String) request.getSession().getAttribute("memberVIP")));
			
			//Merge to 1 Line show
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));

			//add Line Blank UOM (1 or 2)
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines(),null));

            request.setAttribute("Message", "ยกเลิกรายการขายเรียบร้อบแล้ว");
		} catch (Exception e) {
			try{
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
						+ e.getMessage());
				conn.rollback();
			 }catch(Exception ee){}
			 return mapping.findForward("view");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("view");
	}
}
