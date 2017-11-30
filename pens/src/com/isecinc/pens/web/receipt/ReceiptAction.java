package com.isecinc.pens.web.receipt;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.NumberToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptCN;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.ReceiptMatchCN;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptCN;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MReceiptMatchCN;
import com.isecinc.pens.model.MTrxHistory;

/**
 * Receipt Action
 * 
 * @author atiz.b
 * @version $Id: ReceiptAction.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 *          atiz.b : search all in order type by dd user
 * 
 */
public class ReceiptAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ReceiptForm receiptForm = (ReceiptForm) form;
		// String action = request.getParameter("action") != null ? (String) request.getParameter("action") : "";
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			int customerId;
			if (request.getParameter("customerId") != null) {
				// go search
				forward = "search";
				customerId = Integer.parseInt(request.getParameter("customerId"));
				receiptForm.setReceipt(new Receipt());
			} else if (request.getParameter("memberId") != null) {
				// go search
				forward = "search";
				customerId = Integer.parseInt(request.getParameter("memberId"));
				receiptForm.setReceipt(new Receipt());
				
				/* Wit Edit: 1307255 :Edit shortcut From CustomerSerach **/
			}else if (request.getParameter("shotcut_customerId") != null) {
				customerId = Integer.parseInt(request.getParameter("shotcut_customerId"));
				//reset CriteriaSearch
				receiptForm.setCriteria(new ReceiptCriteria());
				//set init Object ReceiptForm
				receiptForm.setReceipt(new Receipt());
				// default date time
				receiptForm.getReceipt().setReceiptDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
				// user
				receiptForm.getReceipt().setSalesRepresent(user);
			} else {
				// go add for customer/member
				customerId = receiptForm.getReceipt().getCustomerId();
				receiptForm.setReceipt(new Receipt());
				// default date time
				receiptForm.getReceipt().setReceiptDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
				// user
				receiptForm.getReceipt().setSalesRepresent(user);
			}

			if (!user.getCustomerType().getKey().equalsIgnoreCase(Customer.DIREC_DELIVERY)) {
				// TT & VAN
				// Default from customer
				Customer customer = new MCustomer().find(String.valueOf(customerId));
				receiptForm.getReceipt().setCustomerId(customer.getId());
				receiptForm.getReceipt().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				receiptForm.getReceipt().setPaymentMethod(customer.getPaymentMethod());
			} else {
				
			}
			receiptForm.getReceipt().setOrderType(user.getOrderType().getKey());

			// get Customer/Member Search Key
			if (request.getParameter("key") != null) {
				request.getSession(true).setAttribute("CMSearchKey", request.getParameter("key"));
			}

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ReceiptForm receiptForm = (ReceiptForm) form;
		Receipt receipt = null;

		try {
			receipt = new MReceipt().find(id);
			logger.debug("receipt:"+receipt+",id:"+id);
			if (receipt == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}

			// Line & By & Cns
			receiptForm.setLines(receipt.getReceiptLines());
			receiptForm.setBys(receipt.getReceiptBys());
			receiptForm.setCns(receipt.getReceiptCNs());
			receiptForm.setReceipt(receipt);

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		if ("edit".equalsIgnoreCase(request.getParameter("action"))) return "prepare";
		return "view";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReceiptForm receiptForm = (ReceiptForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			ReceiptCriteria criteria = getSearchCriteria(request, receiptForm.getCriteria(), this.getClass().toString());
			receiptForm.setCriteria(criteria);
			String whereCause = "";
			if (receiptForm.getReceipt().getReceiptNo() != null && !receiptForm.getReceipt().getReceiptNo().equals("")) {
				whereCause += " AND RECEIPT_NO LIKE '%"
						+ receiptForm.getReceipt().getReceiptNo().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%'";
			}
			if (receiptForm.getReceipt().getDocStatus().length() > 0) {
				whereCause += " AND DOC_STATUS = '" + receiptForm.getReceipt().getDocStatus() + "'";
			}

			if (receiptForm.getReceipt().getReceiptDateFrom().trim().length() > 0) {
				whereCause += " AND RECEIPT_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(receiptForm.getReceipt().getReceiptDateFrom().trim()) + "'";
			}
			if (receiptForm.getReceipt().getReceiptDateTo().trim().length() > 0) {
				whereCause += " AND RECEIPT_DATE <= '"
						+ DateToolsUtil.convertToTimeStamp(receiptForm.getReceipt().getReceiptDateTo().trim()) + "'";
			}

			whereCause += " AND ORDER_TYPE = '" + user.getOrderType().getKey() + "' ";
			whereCause += " AND CUSTOMER_ID = " + receiptForm.getReceipt().getCustomerId() + " ";

			whereCause += " AND USER_ID = " + user.getId();

			if (receiptForm.getReceipt().getSearchInvoiceNo() != null
					&& !receiptForm.getReceipt().getSearchInvoiceNo().equals("")) {
				whereCause += "  AND RECEIPT_ID IN (SELECT RECEIPT_ID FROM t_receipt_line WHERE AR_INVOICE_NO LIKE '"
						+ receiptForm.getReceipt().getSearchInvoiceNo().trim() + "%' )";
			}

			whereCause += " ORDER BY RECEIPT_DATE DESC ,RECEIPT_NO DESC ";
			logger.debug("whereCaluse:"+whereCause);
			
			Receipt[] results = new MReceipt().search(whereCause);
			receiptForm.setResults(results);
			if (results != null) {
				receiptForm.getCriteria().setSearchResult(results.length);
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

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ReceiptForm receiptForm = (ReceiptForm) form;
		int receiptId = 0;
		try {
			// User user = (User) request.getSession().getAttribute("user");
			receiptId = receiptForm.getReceipt().getId();

			// check Token
			if (!isTokenValid(request)) {
				// VAN && TT
				Customer customer = new MCustomer().find(String.valueOf(receiptForm.getReceipt().getCustomerId()));
				receiptForm.setReceipt(new Receipt());
				receiptForm.getReceipt().setCustomerId(customer.getId());
				receiptForm.getReceipt().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
				receiptForm.getReceipt().setPaymentMethod(customer.getPaymentMethod());
				receiptForm.getLines().clear();
				return "prepare";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");

			Receipt receipt = receiptForm.getReceipt();

			// set interfaces & payment & docstatus
			receipt.setInterfaces("N");
			// order.setDocStatus(Order.DOC_SAVE);

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			int i = 1;
			for (ReceiptLine line : receiptForm.getLines()) {
				line.setLineNo(i++);
			}

			// Check Duplicate Cheque No.
			if(!isChequeNoUnique(receiptForm.getBys(),conn)){
				request.setAttribute("JSMsg", InitialMessages.getMessages().get(Messages.CHEQUE_DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}
			
			//Validate User
			if(Utils.isNull(receipt.getSalesRepresent().getCode()).equals("")){
				User user = (User) request.getSession().getAttribute("user");
				receipt.setSalesRepresent(user);
			}
			
			// Save Receipt
			if (!new MReceipt().save(receipt, userActive.getId(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}

			// Delete Line
			if (ConvertNullUtil.convertToString(receiptForm.getDeletedId()).trim().length() > 0) {
				// reverse payment flag to line
				String[] ids = receiptForm.getDeletedId().substring(1).trim().split(",");
				ReceiptLine line;
				List<OrderLine> olines;
				Order order;
				MOrderLine mOrderLine = new MOrderLine();
				for (String id : ids) {
					line = new MReceiptLine().find(id);
					order = new MOrder().find(String.valueOf(line.getOrder().getId()));
					olines = new MOrderLine().lookUp(order.getId());
					for (OrderLine ol : olines) {
						ol.setPayment("Y");
						ol.setExported("N");
						ol.setNeedExport("N");
						ol.setInterfaces("N");
						mOrderLine.save(ol, userActive.getId(), conn);
					}
				}
				
				/** Delete Match Line by receipt_line_id :WIT Edit 07/04/2011**/
				new MReceiptMatch().deleteByReceiptLineId(receiptForm.getDeletedId().substring(1).trim(), conn);
				
				/** Delete t_receipt_line by receipt_line_id **/
				new MReceiptLine().delete(receiptForm.getDeletedId().substring(1).trim(), conn);
			}

			// Save Lines
			Order order;
			i = 1;
			MReceiptLine mReceiptLine = new MReceiptLine();
			MOrder mOrder = new MOrder();
			MOrderLine mOrderLine = new MOrderLine();
			List<OrderLine> olines;
			for (ReceiptLine line : receiptForm.getLines()) {
				line.setLineNo(i++);
				line.setReceiptId(receipt.getId());
				if (receiptId == 0) line.setId(0);
				mReceiptLine.save(line, userActive.getId(), conn);

				order = new MOrder().find(String.valueOf(line.getOrder().getId()));
				/** WIT Edit 11/04/2011 :Add Case Cancel Receipt **/
				if(receiptForm.getReceipt().getDocStatus().equals(Receipt.DOC_VOID)){
				   order.setPayment("N");	
				}else{
				   order.setPayment("Y");
				}
				mOrder.save(order, userActive.getId(), conn);

				olines = new MOrderLine().lookUp(order.getId());
				for (OrderLine ol : olines) {
					/** WIT Edit 11/04/2011 :Add Case Cancel Receipt **/
					if(receiptForm.getReceipt().getDocStatus().equals(Receipt.DOC_VOID)){
					   ol.setPayment("N");
					}else{
					   ol.setPayment("Y");
					}
					ol.setExported("N");
					ol.setNeedExport("N");
					ol.setInterfaces("N");
					mOrderLine.save(ol, userActive.getId(), conn);
				}
			}

			// Delete Match CN
			MReceiptMatchCN mReceiptMatchCN = new MReceiptMatchCN();
			mReceiptMatchCN.deleteByReceiptId(String.valueOf(receipt.getId()), conn);
			// ReceiptCN
			// delete all & insert
			MReceiptCN receiptCN = new MReceiptCN();
			receiptCN.deleteByReceiptId(String.valueOf(receipt.getId()), conn);
			for (ReceiptCN cn : receiptForm.getCns()) {
				cn.setId(0);
				cn.setReceiptId(receipt.getId());
				receiptCN.save(cn, userActive.getId(), conn);
			}

			MReceiptMatch mReceiptMatch = new MReceiptMatch();
			// Delete Match
			mReceiptMatch.deleteByReceiptId(String.valueOf(receipt.getId()), conn);

			// Delete BY
			if (ConvertNullUtil.convertToString(receiptForm.getDeletedRecpById()).trim().length() > 0)
				new MReceiptBy().delete(receiptForm.getDeletedRecpById().substring(1).trim(), conn);

		
			// Receipt By
			MReceiptBy mReceiptBy = new MReceiptBy();
			double receiptAmount = 0;
			double paidAmount = 0;
			double remainAmount = 0;

			ReceiptMatch match;
			ReceiptMatchCN matchCN;
			int idx = 0;
			
			for (ReceiptBy by : receiptForm.getBys()) {
				if (receiptId == 0) by.setId(0);
				paidAmount = 0;
				receiptAmount = by.getReceiptAmount();
				for (String s : by.getAllPaid().trim().split("\\|")) {
					//paidAmount += Double.parseDouble(s); //OLD CODE
					logger.debug("allPaid:"+s);
					paidAmount += NumberToolsUtil.strToDouble(s);//WIT EDIT 25/03/2011
				}
				remainAmount = receiptAmount - paidAmount;
				by.setPaidAmount(paidAmount);
				by.setRemainAmount(remainAmount);
				by.setReceiptId(receipt.getId());
				
				mReceiptBy.save(by, userActive.getId(), conn);				

				// apply match
				idx = 0;
				for (String s : by.getAllBillId().split(",")) {
					for (ReceiptLine line : receiptForm.getLines()) {
						if (s.equalsIgnoreCase(String.valueOf(line.getOrder().getId()))) {
							match = new ReceiptMatch();
							match.setReceiptLineId(line.getId());
							match.setReceiptById(by.getId());
							match.setReceiptId(receipt.getId());
							match.setPaidAmount(Double.parseDouble(by.getAllPaid().trim().split("\\|")[idx]));
							mReceiptMatch.save(match, userActive.getId(), conn);
							break;
						}
					}//for all Receipt line
					idx++;
				}//for all bills

				// apply match CN
				idx = 0;
				for (String s : by.getAllCNId().split(",")) {
					for (ReceiptCN cnLine : receiptForm.getCns()) {
						if (s.equalsIgnoreCase(String.valueOf(cnLine.getCreditNote().getId()))) {
							matchCN = new ReceiptMatchCN();
							matchCN.setReceiptCNId(cnLine.getId());
							matchCN.setReceiptById(by.getId());
							matchCN.setReceiptId(receipt.getId());
							matchCN.setPaidAmount(Double.parseDouble(by.getAllCNPaid().trim().split("\\|")[idx]));
							mReceiptMatchCN.save(matchCN, userActive.getId(), conn);
							break;
						}
					}
					idx++;
				}//for cnALL

			}//for receiptBy

			// Trx History
			TrxHistory trx = new TrxHistory();
			trx.setTrxModule(TrxHistory.MOD_RECEIPT);
			if (receiptId == 0) trx.setTrxType(TrxHistory.TYPE_INSERT);
			else trx.setTrxType(TrxHistory.TYPE_UPDATE);
			trx.setRecordId(receipt.getId());
			trx.setUser(userActive);
			new MTrxHistory().save(trx, userActive.getId(), conn);
			// Trx History --end--

			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			logger.debug("receiptId1:"+receiptId);
			// Save Token
			saveToken(request);
			
			logger.debug("receiptId2:"+receipt.getId());
			// Search Again
			prepare(String.valueOf(receipt.getId()), receiptForm, request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
			receiptForm.getReceipt().setId(receiptId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "view";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		ReceiptForm receiptForm = (ReceiptForm) form;

		int customerId = receiptForm.getReceipt().getCustomerId();
		if (!receiptForm.getReceipt().getOrderType().equalsIgnoreCase(Receipt.DIRECT_DELIVERY)) {
			// TT & VAN
			// Default from customer
			receiptForm.setCriteria(new ReceiptCriteria());
			Customer customer = new MCustomer().find(String.valueOf(customerId));
			receiptForm.getReceipt().setCustomerId(customer.getId());
			receiptForm.getReceipt().setCustomerName((customer.getCode() + "-" + customer.getName()).trim());
			receiptForm.getReceipt().setPaymentMethod(customer.getPaymentMethod());
		} 
	}
	
	public ActionForward cancelReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			User user = (User) request.getSession().getAttribute("user");
			ReceiptForm receiptForm = (ReceiptForm) form;
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
	
			new MReceipt().cancelReceiptByID(conn, receiptForm.getReceipt().getId(), receiptForm.getReceipt().getReason(), user.getId()); 
			conn.commit();
            
			//set actionCancelOrder
			request.setAttribute("actionCancelReceipt", "success");
			
            request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			try{
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
						+ e.getMessage());
				conn.rollback();
			 }catch(Exception ee){}
			 return mapping.findForward("cancelReceiptPopup");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("cancelReceiptPopup");
	}

	private boolean isChequeNoUnique(List<ReceiptBy> bys, Connection conn) throws Exception {
		HashMap cheque = new HashMap();
		MReceiptBy mBy = new MReceiptBy();
		
		for(ReceiptBy by:bys){
			if(by.getChequeNo() != null && by.getChequeNo().length() >0){
				if(cheque.isEmpty())
					cheque.put(by.getChequeNo(), 1);
				else if(cheque.containsKey(by.getChequeNo()))
					return false;
				
				// Validate In Database
				if(!mBy.checkChqueNoDuplicate(mBy.TABLE_NAME, mBy.COLUMN_ID, "cheque_no", by.getChequeNo(), by.getReceiptId(), conn))
					return false;
			}
		}
		
		return true;
	}
}
