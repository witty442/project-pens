package com.isecinc.pens.web.receipt;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.ReceiptMatch;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberProduct;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptBy;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MReceiptMatch;
import com.isecinc.pens.model.MTrxHistory;

/**
 * Member Receipt Action
 * 
 * @author atiz.b
 * @version $Id: MemberReceiptAction.java,v 1.0 07/02/2011 00:00:00 atiz.b Exp $
 * 
 */
public class MemberReceiptAction extends ReceiptAction {

	/**
	 * Prepare Member Receipt
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward prepareMR(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ReceiptForm receiptForm = (ReceiptForm) form;
		try {
			String id = "";
			if (request.getParameter("id") != null) id = request.getParameter("id");
			if (id.length() == 0) {
				// add
				prepare(receiptForm, request, response);
			} else {
				// edit
				prepare(id, receiptForm, request, response);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			//
		}
		if (request.getParameter("action").equalsIgnoreCase("view")) return mapping.findForward("viewMR");
		return mapping.findForward("prepareMR");
	}

	/**
	 * Save Member Receipt
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveMemberReceipt(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String retval = "prepareMR";
		try {
			retval = saveMemberReceipt(form, request, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return mapping.findForward(retval);
	}

	/**
	 * Save
	 */
	protected String saveMemberReceipt(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Connection conn = null;
		ReceiptForm receiptForm = (ReceiptForm) form;
		int receiptId = 0;
		try {
			receiptId = receiptForm.getReceipt().getId();

			// check Token
			if (!isTokenValid(request)) {
				// VAN && TT
				Member member = new MMember().find(String.valueOf(receiptForm.getReceipt().getCustomerId()));
				receiptForm.setReceipt(new Receipt());
				member.setMemberProducts(new MMemberProduct().lookUp(member.getId()));
				receiptForm.getReceipt().setCustomerId(member.getId());
				receiptForm.getReceipt().setCustomerName(
						(member.getCode() + "-" + member.getName() + " " + member.getName2()).trim());
				// from customer or member
				receiptForm.getReceipt().setPaymentMethod(member.getPaymentMethod());
				receiptForm.getLines().clear();
				return "prepareMR";
			}

			User userActive = (User) request.getSession().getAttribute("user");

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

			// Save Receipt
			if (!new MReceipt().save(receipt, userActive.getId(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepareMR";
			}

			// Delete Line

			// Delete Line
			if (ConvertNullUtil.convertToString(receiptForm.getDeletedId()).trim().length() > 0) {
				// reverse payment flag to line
				String[] ids = receiptForm.getDeletedId().substring(1).trim().split(",");
				ReceiptLine line;
				OrderLine oLine;
				MOrderLine mOrderLine = new MOrderLine();
				for (String id : ids) {
					line = new MReceiptLine().find(id);
					oLine = new MOrderLine().find(String.valueOf(line.getOrderLine().getId()));
					if (oLine != null) {
						oLine.setPayment("Y");
						oLine.setExported("N");
						oLine.setNeedExport("N");
						oLine.setInterfaces("N");
						mOrderLine.save(oLine, userActive.getId(), conn);
					}
				}
				new MReceiptLine().delete(receiptForm.getDeletedId().substring(1).trim(), conn);
			}

			if (ConvertNullUtil.convertToString(receiptForm.getDeletedId()).trim().length() > 0)
				new MReceiptLine().delete(receiptForm.getDeletedId().substring(1).trim(), conn);

			// Save Lines
			Order order;
			i = 1;
			MReceiptLine mReceiptLine = new MReceiptLine();
			MOrder mOrder = new MOrder();
			MOrderLine mOrderLine = new MOrderLine();
			OrderLine oLine;
			for (ReceiptLine line : receiptForm.getLines()) {
				line.setLineNo(i++);
				line.setReceiptId(receipt.getId());

				// completely paid
				line.setCreditAmount(line.getInvoiceAmount());
				line.setPaidAmount(line.getInvoiceAmount());
				line.setRemainAmount(0);

				if (receiptId == 0) line.setId(0);
				mReceiptLine.save(line, userActive.getId(), conn);

				order = new MOrder().find(String.valueOf(line.getOrder().getId()));
				order.setPayment("Y");
				mOrder.save(order, userActive.getId(), conn);

				oLine = new MOrderLine().find(String.valueOf(line.getOrderLine().getId()));
				if (oLine != null) {
					oLine.setPayment("Y");
					oLine.setExported("N");
					oLine.setNeedExport("N");
					oLine.setInterfaces("N");
					mOrderLine.save(oLine, userActive.getId(), conn);
				}
			}

			// Delete Match
			MReceiptMatch mReceiptMatch = new MReceiptMatch();
			mReceiptMatch.deleteByReceiptId(String.valueOf(receipt.getId()), conn);

			// Delete BY
			if (ConvertNullUtil.convertToString(receiptForm.getDeletedRecpById()).trim().length() > 0)
				new MReceiptBy().delete(receiptForm.getDeletedRecpById().substring(1).trim(), conn);

			Member member = null;
			if (receipt.getOrderType().equalsIgnoreCase(Receipt.DIRECT_DELIVERY)) {
				// VAN && TT
				member = new MMember().find(String.valueOf(receiptForm.getReceipt().getCustomerId()));
			}

			// Receipt By
			MReceiptBy mReceiptBy = new MReceiptBy();

			ReceiptMatch match;
			for (ReceiptBy by : receiptForm.getBys()) {
				if (receiptId == 0) by.setId(0);
				// completely paid
				by.setPaidAmount(by.getReceiptAmount());
				by.setRemainAmount(0);
				by.setReceiptId(receipt.getId());
				if (member != null) {
					// credit card
					if (by.getPaymentMethod().equalsIgnoreCase("CR"))
						by.setCreditcardExpired(member.getCreditcardExpired());
				}
				mReceiptBy.save(by, userActive.getId(), conn);

				// apply match
				for (ReceiptLine line : receiptForm.getLines()) {
					match = new ReceiptMatch();
					match.setReceiptLineId(line.getId());
					match.setReceiptById(by.getId());
					match.setReceiptId(receipt.getId());
					match.setPaidAmount(line.getInvoiceAmount() / receiptForm.getBys().size());
					mReceiptMatch.save(match, userActive.getId(), conn);
				}
			}

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

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			receiptForm.getReceipt().setId(receiptId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			return "prepareMR";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "viewMR";
	}
}
