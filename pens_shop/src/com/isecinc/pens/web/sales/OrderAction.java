package com.isecinc.pens.web.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.type.LineDirectionEnum;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.Constants;
import util.ControlCode;
import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.Debug;
import util.NumberToolsUtil;
import util.ReportHelper;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.CustomerBillInfo;
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
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.process.modifier.ModifierProcess;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.report.listOrderProduct.ListOrderProductReport;
import com.isecinc.pens.report.listOrderProduct.ListOrderProductReportProcess;
import com.isecinc.pens.report.taxinvoice.TaxInvoiceReport;


/**
 * Order Action
 * 
 * @author witty 09/2561
 * @version 
 * 
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
		int customerId = 0;
		try {
			//prepare Connection 
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//Set Debug Mode
			logger.debug("debug.isDebugEnable():"+debug.isDebugEnable());
			request.getSession().setAttribute("debug_mode", debug.isDebugEnable());
			User user = (User) request.getSession(true).getAttribute("user");
           
			//get default customerId from c_config
			customerId = user.getConfig().getCustomerId();
			logger.debug("customerId config:"+customerId);
			logger.debug("pricelistId config:"+user.getConfig().getPricelistId());
			
			OrderCriteria criteria = new OrderCriteria();
			//criteria.setSearchKey(searchKey)
			orderForm.setCriteria(criteria);

			// get Customer Info
			Customer customer = new MCustomer().find(String.valueOf(customerId));
			Order order = new Order();
			order.setCustomerId(customerId);//Fix Maya all time
			order.setCustomerName(customer.getName());
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			
			// default date time
			orderForm.getOrder().setOrderDate(new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH")).format(new Date()));
			orderForm.getOrder().setOrderTime(new SimpleDateFormat("HH:mm", new Locale("th", "TH")).format(new Date()));

			//PriceListID  Wait for MAYA 
			orderForm.getOrder().setPriceListId(user.getConfig().getPricelistId());// new MPriceList().getMayaPriceList().getId());

            //creditCard Year Expire
			request.getSession().setAttribute("CAREDITCARD_YEAR_EXPIRE_LIST", OrderUtils.getCreditcardYearExpireList());
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
				orderForm.getOrder().setCustomerId(customer.getId());
				orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
				orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
				orderForm.getOrder().setVatCode(customer.getVatCode());
				request.getSession().setAttribute("memberVIP", "N");
			} 

			request.getSession().removeAttribute("isAdd");
			
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

			String whereCause =" where order_id = "+id;
			order = new MOrder().findByWhereCond(whereCause);
			
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
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines()));

			//Calc TotalQty
			orderForm.setTotalQty(new OrderProcess().sumTotalQty(orderForm.getLines()));
			
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
	
			String whereCause =" where order_id = "+id;
			order = new MOrder().findByWhereCond(whereCause);
			
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
			
			//Filter Check Van Can Receipt Cheque Or Credit
			conn = DBConnection.getInstance().getConnection();

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
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines()));

			/** Manage Mode (add,edit,view) **/
			orderForm.setMode("edit");
			
		} catch (Exception e) {
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
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines()));
			
			order.setRoundTrip(roundTrip);
			orderForm.setOrder(order);
			orderForm.setAutoReceipt(new Receipt());
			orderForm.setAutoReceiptFlag("N");
			
			//Filter Check Van Can Receipt Cheque Or Credit
			conn = new DBCPConnectionProvider().getConnection(conn);

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
		logger.debug("preSave");
		try {
			OrderForm orderForm = (OrderForm) form;
			conn = new DBCPConnectionProvider().getConnection(conn);
			User userActive = (User) request.getSession().getAttribute("user");
			Customer customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
			
			// remove promotion line
			List<OrderLine> promotionLine = new ArrayList<OrderLine>();
			List<OrderLine> promotionSLine = new ArrayList<OrderLine>();

			// Set Display To Prepare Save Order Line (no promotion Lines)
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request.getSession().getAttribute("memberVIP")));

			logger.info("********Debug before calc**********");
			new OrderProcess().debug(orderForm.getLines());
			logger.info("***********************************");
			
			// Add Promotion Line only From orderFrom to promotionLine
			for (OrderLine line : orderForm.getLines()) {
				//logger.debug("promotion:["+line.getPromotion()+"]");
				if (line.getPromotion().equalsIgnoreCase("Y") ) {
					promotionLine.add(line);
				}
			}
			
			//remove Promotion line from orderFrom
			for (OrderLine line : promotionLine) {
				orderForm.getLines().remove(line);
			}
			
			/*********************Promotion Special****************************/
			// Add Promotion Line(S) only From orderFrom to promotionLine
			for (OrderLine line : orderForm.getLines()) {
				//logger.debug("promotion:["+line.getPromotion()+"]");
				if (line.getPromotion().equalsIgnoreCase("S") ) {
					/*logger.debug("*****Debug Line Special Before**********");
					logger.debug("productCode:"+line.getProduct().getCode());
					logger.debug("taxable:"+line.getTaxable());
					logger.debug("qty:"+line.getQty()+","+line.getQty1());
					logger.debug("price:"+line.getPrice()+","+line.getPrice1());
					logger.debug("lineAmount:"+line.getLineAmount()+","+line.getLineAmount1());
					logger.debug("discount:"+line.getDiscount()+","+line.getDiscount1());
					logger.debug("totalAmount:"+line.getTotalAmount()+","+line.getTotalAmount1());
					logger.debug("*******************************************");*/
					promotionSLine.add(line);
				}
			}
		
			//remove Promotion line(S) from orderFrom
			for (OrderLine line : promotionSLine) {
				orderForm.getLines().remove(line);
			}
			//Fill Line 1 to 2 Show on screen
			promotionSLine =  orderProcess.fillLinesShowPromotion(promotionSLine);
			
			//SumQtySameProduct
			promotionSLine = orderProcess.sumQtyProductPromotionDuplicate(promotionSLine);
			/*********************Promotion Special****************************/
			
			//Recalculate lineAmount (qty*price) in Lines
			orderForm.setLines(new MOrder().reCalculateLineAmountInLinesBeforeCalcPromotion(orderForm.getLines()));
						

			logger.info("********Debug after calcLineBeforePromotion**********");
			new OrderProcess().debug(orderForm.getLines());
			logger.info("***********************************");
			
			
			// Call Modifier Process
			ModifierProcess modProcess = new ModifierProcess(ConvertNullUtil.convertToString(customer.getTerritory()).trim());
			modProcess.findModifier(orderForm.getLines(), userActive, conn);
		
			logger.info("********Debug after AfterCalcPromotion**********");
			new OrderProcess().debug(orderForm.getLines());
			logger.info("***********************************");
			
			// Set for web display.
			logger.debug("****** Start Set Display order ****************************************************");
	
			//Merge to 1 Line to Show
			logger.info("fillLinesShow LINE NORMAL");
			List<OrderLine> odLines = orderProcess.fillLinesShow(orderForm.getLines());

			//add Line Blank UOM (1 or 2)
			odLines = new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),odLines);
	
			//add Promotion to show
			logger.info("fillLinesShow LINE Promotion");
			List<OrderLine> promotionLinesCalcNew  = orderProcess.fillLinesShowPromotion(modProcess.getAddLines());

			promotionLinesCalcNew = orderProcess.sumQtyProductPromotionDuplicate(promotionLinesCalcNew);
	
			//Sort by product
			try{
				Comparator<OrderLine> comparator = new Comparator<OrderLine>() {
				    public int compare(OrderLine c1, OrderLine c2) {
				        return c2.getProduct().getCode().compareTo(c2.getProduct().getCode()); // use your logic
				    }
				};
				Collections.sort(promotionLinesCalcNew, comparator); 
				logger.info("Debug after Sort Promotion");
				new OrderProcess().debug(promotionLinesCalcNew);
			}catch(Exception e){
				logger.error(e.getMessage()+":Sort Error",e);
			}
			
			//add promotion line(Y)
			odLines.addAll(promotionLinesCalcNew);
			//add Promotion Line(S)
			odLines.addAll(promotionSLine);
			
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
			
			//Set Data to Session to Print ListOrderProduct
			request.getSession().setAttribute("order_from_to_print", orderForm);
			
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
			
			//Merge to 1 Line to Show
			orderForm.setLines(new OrderProcess().fillLinesShow(orderForm.getLines()));
			
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
		//User user = (User) request.getSession(true).getAttribute("user");
		Customer customer = null;
		try {
			//clear Session Temp to Print ListOrderProduct
			request.getSession().setAttribute("order_from_to_print",null);
			
			orderId = orderForm.getOrder().getId();

			// check Token
			if (!isTokenValid(request)) {
			
				// VAN && TT
				customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
				orderForm.setOrder(new Order());
				orderForm.getOrder().setCustomerId(customer.getId());
				orderForm.getOrder().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				// from customer or member
				orderForm.getOrder().setPaymentTerm(customer.getPaymentTerm());
				orderForm.getOrder().setPaymentMethod(customer.getPaymentMethod());
				orderForm.getOrder().setVatCode(customer.getVatCode());
					
				orderForm.getLines().clear();
				return "prepare";
			}else{
				customer = new MCustomer().find(String.valueOf(orderForm.getOrder().getCustomerId()));
			}

			User userActive = (User) request.getSession(true).getAttribute("user");
			Order order = orderForm.getOrder();
			
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

		    /** Fill line to Save **/
			orderForm.setLines(new OrderProcess().fillLinesSave(orderForm.getLines(), userActive, (String) request
					.getSession().getAttribute("memberVIP")));
	
			logger.info("Debug Order Lines");
            new OrderProcess().debug(orderForm.getLines());

			for (OrderLine line : orderForm.getLines()) {
				line.setLineNo(i++);
				line.setPayment("N");
				
				/** Case promotion =S(special promotion) set line_amount =0,total_amount =0 */
				//logger.debug("line promotion:"+Utils.isNull(line.getPromotion()));
				/*if (line.getPromotion().equalsIgnoreCase("S") ) {
					logger.debug("Debug Line Special Before Save**********");
					logger.debug("productCode:"+line.getProduct().getCode());
					logger.debug("taxable:"+line.getTaxable());
					logger.debug("qty:"+line.getQty()+","+line.getQty1());
					logger.debug("price:"+line.getPrice()+","+line.getPrice1());
					logger.debug("lineAmount:"+line.getLineAmount()+","+line.getLineAmount1());
					logger.debug("discount:"+line.getDiscount()+","+line.getDiscount1());
					logger.debug("totalAmount:"+line.getTotalAmount()+","+line.getTotalAmount1());
					logger.debug("uom:"+line.getUom()+","+line.getUom1()+","+line.getUom2());
				}*/
			}//for

			// Sales Reps --current user who's create/edit record--
			order.setSalesRepresent(userActive);

			//Recalculate lineAmount (qty*price) ,total_amount = lineAmount-discount in Lines
			orderForm.setLines(new MOrder().reCalculateLineAmountInLinesAfterCalcPromotion(orderForm.getLines()));
			
			// Re-Calculate totalAmount vatAmount netAmount From lines
			order = new MOrder().reCalculateHeadAmount(order, orderForm.getLines());
			            
			//For compare after save 
			double beforeSave_NetAmount = order.getNetAmount(); 
	
			//CASH ,ALI,WECHAT,GOV
			if(Constants.PAYMT_CASH.equalsIgnoreCase(Utils.isNull(order.getPaymentMethod()))
				|| Constants.PAYMT_ALI.equalsIgnoreCase(Utils.isNull(order.getPaymentMethod()))
				|| Constants.PAYMT_WE.equalsIgnoreCase(Utils.isNull(order.getPaymentMethod()))
				|| Constants.PAYMT_GOV.equalsIgnoreCase(Utils.isNull(order.getPaymentMethod()))){
				order.setCreditCardType("");
				order.setCreditCardNo("");
				order.setCreditCardExpireDate("");
			}else{
				order.setCreditCardExpireDate(order.getCreditcardMonthExpire()+"/"+order.getCreditcardYearExpire());
			}
			
			//Case CustomerBillName is null use default customerName(Maya)
			
			if(Utils.isNull(order.getCustomerBillName()).equals("")){
				order.setCustomerBillName(customer!=null?customer.getName():"");
			}
			
			// Save Order
			if (!new MOrder().save(order, userActive.getId(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}
            
			// Delete Promotion =Y
			new MOrderLine().deletePromotion(orderForm.getOrder().getId(), conn);

			// Delete Line
			if (ConvertNullUtil.convertToString(orderForm.getDeletedId()).trim().length() > 0){
				new MOrderLine().delete(orderForm.getDeletedId().substring(1).trim(), conn);
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
				
				logger.debug("modifier_line_id["+line.getModifierLineId()+"]");
			
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
			
			logger.debug("beforeSave_NetAmount:"+beforeSave_NetAmount+",afterSave_NetAmount:"+afterSave_NetAmount);
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

			/***** Save Receipt ******************************************/
			/** Check old receipt is exist and delete Case Edit Order */
			int receiptIdExist  = new OrderProcess().deleteReceiptExistByOrderNo(conn, order.getOrderNo());
			logger.debug("(0=NoExist)Delet Old Receipt receiptIdExist:"+receiptIdExist);
			
			logger.debug("CreateAutoReceipt NEW");
			logger.debug("PaymentMethod:"+orderForm.getOrder().getPaymentMethod());

			orderForm.getAutoReceipt().setReceiptNo(order.getOrderNo());
			orderForm.getAutoReceipt().setReceiptAmount(order.getNetAmount());
			String bankCode = InitialReferences.getRefByKey(InitialReferences.PAYMENT_METHOD_BANK,orderForm.getOrder().getPaymentMethod(),"").get(0).getKey();
			orderForm.getAutoReceipt().setInternalBank(bankCode);//KBANK
			orderForm.getAutoReceipt().setReceiptDate(orderForm.getOrder().getOrderDate());
			 
			/** Set ReceiptBy Manual **/
			List<ReceiptBy> receiptByList = new ArrayList<ReceiptBy>();
			ReceiptBy receiptBy = new ReceiptBy();
			receiptBy.setId(0);
			receiptBy.setPaymentMethod(Utils.isNull(order.getPaymentMethod()));
			receiptBy.setCreditCardType(Utils.isNull(order.getCreditCardType()));
			receiptBy.setCreditcardNo(Utils.isNull(order.getCreditCardNo()));
			receiptBy.setCreditcardExpired(Utils.isNull(order.getCreditCardExpireDate()));
			
			receiptBy.setBank("");
			receiptBy.setChequeNo("");
			receiptBy.setChequeDate("");
			receiptBy.setReceiptAmount(order.getNetAmount());
			receiptBy.setSeedId("");
			receiptBy.setAllBillId(String.valueOf(order.getId()));
			receiptBy.setAllPaid(String.valueOf(order.getNetAmount()));
			receiptByList.add(receiptBy);
			 
			//set order isCash =Y
			order.setIsCash("Y");
			 
			new OrderProcess().createAutoReceipt(orderForm.getAutoReceipt(), order, orderForm.getLines(), receiptByList, null, userActive, conn);
			//set msg 
			msg = SystemMessages.getCaption("SaveSucess", Utils.local_th);

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
			// set msg save success
			request.setAttribute("Message",msg );
			
			//add Line Blank UOM (1 or 2)
			orderForm.setLines(new OrderProcess().fillLinesShowBlankUOM(conn,String.valueOf(orderForm.getOrder().getPriceListId()),orderForm.getLines()));
			//Calc TotalQty
			orderForm.setTotalQty(new OrderProcess().sumTotalQty(orderForm.getLines()));
			
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
		int customerId = 0;
		try {
			logger.debug("action:"+action);
			if( "new".equalsIgnoreCase(action)){
				//get from m_customer by user_id
				Customer custByUserId = new MCustomer().findByWhereCond(" where user_id = "+user.getId());// Integer.parseInt(CUSTOMER_ID_MEYA_FIXED);
				if(custByUserId != null){
				   customerId = custByUserId.getId();
				}else{
					throw new Exception("Cannot found Customer Default");
				}
				
				OrderCriteria criteria = new OrderCriteria();
				//criteria.setSearchKey(searchKey)
				orderForm.setCriteria(criteria);

				// get Customer Info
				Customer customer = new MCustomer().find(String.valueOf(customerId));
				Order order = new Order();
				order.setCustomerId(customerId);//Fix Maya all time
				order.setCustomerName(customer.getCode()+"-"+customer.getName());
				order.setOrderDateFrom(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				//order.setOrderDateTo(Utils.stringValue(new Date(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				orderForm.setOrder(order);
			}else{
				request.getSession().removeAttribute("isAdd");
	           
				OrderCriteria criteria = getSearchCriteria(request, orderForm.getCriteria(), this.getClass().toString());
				orderForm.setCriteria(criteria);
				
				Order order = orderForm.getOrder();
				String whereCause = "";
				if ( !Utils.isNull(order.getOrderNo()).equals("")) {
					whereCause += " AND ORDER_NO LIKE '%"
							+ order.getOrderNo().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' \n";
				}
				if ( !Utils.isNull(order.getSalesOrderNo()).equals("")) {
					whereCause += " AND SALES_ORDER_NO LIKE '%"
							+ order.getSalesOrderNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
							+ "%' \n";
				}
				if ( !Utils.isNull(order.getArInvoiceNo()).equals("")) {
					whereCause += " AND AR_INVOICE_NO LIKE '%"
							+ order.getArInvoiceNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
							+ "%' \n";
				}
				if ( !Utils.isNull(order.getDocStatus()).equals("")) {
					whereCause += " AND DOC_STATUS = '" + order.getDocStatus() + "' \n";
				}
	
				if ( !Utils.isNull(order.getOrderDateFrom()).equals("") && !Utils.isNull(order.getOrderDateTo()).equals("")) {
					whereCause += " AND ORDER_DATE >= '"
							+ DateToolsUtil.convertToTimeStamp(order.getOrderDateFrom().trim()) + "' \n";
					whereCause += " AND ORDER_DATE <= '"
							+ DateToolsUtil.convertToTimeStamp(order.getOrderDateTo().trim()) + "' \n";
				}else if ( !Utils.isNull(order.getOrderDateFrom()).equals("") && Utils.isNull(order.getOrderDateTo()).equals("")) {
					whereCause += " AND ORDER_DATE = '"
							+ DateToolsUtil.convertToTimeStamp(order.getOrderDateFrom().trim()) + "' \n";
				}
	
				whereCause += " AND ORDER_TYPE = '" + user.getOrderType().getKey() + "' \n";
				whereCause += " AND CUSTOMER_ID = " + order.getCustomerId() + " \n";
				whereCause += " AND USER_ID = " + user.getId() +" \n";

				 if ( !Utils.isNull(order.getCustomerBillName()).equalsIgnoreCase("")){
					  whereCause += " AND CUSTOMER_BILL_NAME LIKE '%"+Utils.isNull(order.getCustomerBillName())+"%' \n";
				 }
				 if ( !Utils.isNull(order.getAddressDesc()).equalsIgnoreCase("")){
					  whereCause += " AND ADDRESS_DESC LIKE '%"+Utils.isNull(order.getAddressDesc())+"%' \n";
				 }
				 if ( !Utils.isNull(order.getIdNo()).equalsIgnoreCase("")){
					  whereCause += " AND ID_NO ='"+Utils.isNull(order.getIdNo())+"' \n";
				 }
				 if ( !Utils.isNull(order.getPassportNo()).equalsIgnoreCase("")){
					  whereCause += " AND PASSPORT_NO ='"+Utils.isNull(order.getPassportNo())+"' \n";
				 }

				whereCause += " ORDER BY ORDER_DATE DESC,ORDER_NO DESC ";
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
		String fileName = "tax_invoice_summary_report";
		String productNameShort = "";
		try {
			conn = DBConnection.getInstance().getConnection();
			
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
	
			if("CS".equals(order.getPaymentMethod())){
				pReceiptByMsg = "เงินสด";	
			}else if("ALI".equals(order.getPaymentMethod())){
				pReceiptByMsg = "AliPay";	
			}else if("WE".equals(order.getPaymentMethod())){
				pReceiptByMsg = "WeChat";	
			}else if("QR".equals(order.getPaymentMethod())){
				pReceiptByMsg = "QRCODE";
			}else{
				String creditCardNo = "";
				if( !Utils.isNull(order.getCreditCardNo()).equals("")){
				   creditCardNo = Utils.isNull(order.getCreditCardNo());
				   creditCardNo = creditCardNo.substring(creditCardNo.length()-4,creditCardNo.length());
				}
				pReceiptByMsg = "บัตรเครดิต ****"+creditCardNo;
			}

			//Address Pens
			//String addessPens ="662/19-20 ถนนพระราม 3 แขวงบางโพงพาง เขตยานนาวา กรุงเทพฯ 10120  โทร. 0-2294-7300 โทรสาร. 0-2294-7560 สำนักงานใหญ่";
			String addressConfig = InitialReferences.getRef(conn,InitialReferences.ADDRESS_MAYA).get(0).getKey();
			logger.debug("addressConfig:\n"+addressConfig);
			parameterMap.put("p_address_pens", addressConfig);
			
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
			}

			parameterMap.put("p_report_title", pReportTitle);
			parameterMap.put("p_receiptNo", receiptNo.length() != 0 ? ReportHelper.convertOrderNoForReport(receiptNo) : ReportHelper.convertOrderNoForReport(order.getOrderNo()));
			parameterMap.put("p_vatcode", order.getVatCode());
			
			//Convert Order Date thai toChrist
			Date createDateTemp = Utils.parse(order.getOrderDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String createDateChrist = Utils.stringValue(createDateTemp, Utils.DD_MM_YYYY_WITH_SLASH);
			parameterMap.put("p_orderDate", createDateChrist);
			
			parameterMap.put("p_code", user.getCode());
			parameterMap.put("p_name", user.getName());
			parameterMap.put("p_taxNo", BeanParameter.getPensTaxNo());
			parameterMap.put("p_receipt_by_msg", pReceiptByMsg);
			
			//Convert Thai to Christ date format
			createDateTemp = Utils.parse(order.getCreated(), Utils.DD_MM_YYYY_HH_mm_WITH_SLASH,Utils.local_th);
			createDateChrist = Utils.stringValue(createDateTemp, Utils.DD_MM_YYYY_HH_mm_WITH_SLASH);
			parameterMap.put("p_create_date", createDateChrist);
			
			conn = DBConnection.getInstance().getConnection();
			
			//
			String cusTaxNoMsg = "";
			
			//"".equals(Utils.isNull(customer.getTaxNo()))?null:Utils.isNull(customer.getTaxNo())
			if( !Utils.isNull(order.getIdNo()).equals("") && !Utils.isNull(order.getPassportNo()).equals("") ){
				cusTaxNoMsg ="เลขบัตรประชาชน:"+Utils.isNull(order.getIdNo())+" Passport:"+Utils.isNull(order.getPassportNo()); 
			}else{
				if( !Utils.isNull(order.getIdNo()).equals("")){
					cusTaxNoMsg ="เลขบัตรประชาชน:"+Utils.isNull(order.getIdNo());
				}else if( !Utils.isNull(order.getPassportNo()).equals("")){
					cusTaxNoMsg ="Passport:"+Utils.isNull(order.getPassportNo()); 
				}
			}
			
			String addressDesc = Utils.isNull(order.getAddressDesc());
			String custAddressArr1 = "";
			String custAddressArr2 = "";
			int fixNewLineLen = 50;
			int findNewLineIndex = 0;
			//case AddressDesc >200
			if(addressDesc.length()>=fixNewLineLen){
				String temp = addressDesc.substring(fixNewLineLen-1,fixNewLineLen);
				logger.debug("address:"+temp);
				if("".equals(temp)){
				   custAddressArr1 = addressDesc.substring(0,fixNewLineLen);
				   custAddressArr2 = addressDesc.substring(fixNewLineLen+1,addressDesc.length());
				   
				}else{
					//find blank position < fixNewLineLen
					String addressTemp = addressDesc.substring(0,fixNewLineLen);
					for(int i=addressTemp.length();i<=addressTemp.length();i--){
			            if(i>=1){
							String t = addressTemp.substring(i-1,i);
							System.out.println("i["+i+"]t["+t+"]");
							if(" ".equals(t)){
								findNewLineIndex = i;
								break;
							}
			            }
					}//for
					custAddressArr1 = addressDesc.substring(0,findNewLineIndex);
					custAddressArr2 = addressDesc.substring(findNewLineIndex,addressDesc.length());
				}
			}else{
				custAddressArr1 = Utils.isNull(order.getAddressDesc());
			}
			
			parameterMap.put("custName"," "+ order.getCustomerBillName());
			parameterMap.put("custAddress1", custAddressArr1);
			parameterMap.put("custAddress2", custAddressArr2);
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
			
				taxInvoice.setCustomerCode("");
				taxInvoice.setCustomerName(order.getCustomerBillName());
				
				if("Y".equalsIgnoreCase(line.getPromotion())){
				  taxInvoice.setProductCode("*"+no+")"+line.getProduct().getCode());
				}else{
				  taxInvoice.setProductCode(no+")"+line.getProduct().getCode());	
				}
				//show product 10 letter
				productNameShort = Utils.isNull(line.getProduct().getName());
				productNameShort = productNameShort.length()>10?productNameShort.substring(0,10):productNameShort;
				taxInvoice.setProductName(productNameShort);
				taxInvoice.setUomId(line.getProduct().getUom().getId());
				taxInvoice.setMainQty(new Double(line.getQty1()).intValue());
				taxInvoice.setSubQty(new Double(line.getQty2()).intValue());
				taxInvoice.setAddQty(new Double(line.getQty2()).intValue());
				taxInvoice.setSalePrice(line.getLineAmount());
				taxInvoice.setPercentDiscount(0);
				taxInvoice.setDiscount(line.getDiscount());
				
				logger.debug("productNameShort:"+productNameShort);
				logger.debug("line.getLineAmount():"+line.getLineAmount());
				logger.debug("line.getDiscount():"+line.getDiscount());
				if("S".equalsIgnoreCase(line.getPromotion())){
					//OLD 
				   //taxInvoice.setLineAmount(line.getLineAmount() + line.getDiscount());
				   //WIT EDIT:25/12/2020
					taxInvoice.setLineAmount(line.getLineAmount() - line.getDiscount());
				}else{
				   taxInvoice.setLineAmount(line.getLineAmount() - line.getDiscount());
				}
				// Summary
				taxInvoice.setTotalAmount(line.getLineAmount() - line.getDiscount());
				taxInvoice.setVatAmount(line.getVatAmount());
			
				taxInvoice.setNetAmount(taxInvoice.getTotalAmount()+taxInvoice.getVatAmount());

				lstData.add(taxInvoice);
				no++;
			}
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
			logger.info("Report Name:"+fileName);
			
			String fileJasper = BeanParameter.getReportPath() + fileName;
  
			reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, lstData);
			
			//reportServlet.runReport(request, response, conn, fileJasper, SystemElements.PDF, parameterMap, fileName, lstData);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
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
