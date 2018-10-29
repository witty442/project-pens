package com.isecinc.pens.web.admin;

import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.Debug;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductPrice;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MProduct;
import com.isecinc.pens.model.MProductPrice;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.process.administer.ManageOrderReceiptProcess;
import com.isecinc.pens.web.sales.OrderCriteria;
import com.isecinc.pens.web.sales.OrderForm;
import com.isecinc.pens.web.sales.OrderUtils;

public class ManageOrderReceiptAction extends I_Action {

	public Debug debug = new Debug(true);
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ManageOrderReceiptForm manageOrderReceiptForm = (ManageOrderReceiptForm) form;
		//String action = request.getParameter("action") != null ? (String) request.getParameter("action") : "";
		logger.debug("prepare ManageOrder");
		try {
			manageOrderReceiptForm.setDocumentDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			logger.debug("sss:documentDate:"+manageOrderReceiptForm.getDocumentDate());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return forward;
		}finally{
			
		}
		return forward;
	}

	@Override
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ManageOrderReceiptForm manageOrderReceiptForm = (ManageOrderReceiptForm) form;
		try {
			manageOrderReceiptForm.setOrders(new ManageOrderReceiptProcess().getOrders(manageOrderReceiptForm
					.getDocumentDate()));
			manageOrderReceiptForm.setReceipts(new ManageOrderReceiptProcess().getReceipt(manageOrderReceiptForm
					.getDocumentDate()));
			manageOrderReceiptForm.setOrderSize(manageOrderReceiptForm.getOrders().size());
			manageOrderReceiptForm.setReceiptSize(manageOrderReceiptForm.getReceipts().size());

			if (manageOrderReceiptForm.getOrderSize() + manageOrderReceiptForm.getReceiptSize() == 0) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ManageOrderReceiptForm manageOrderReceiptForm = (ManageOrderReceiptForm) form;
		try {
			User user = (User) request.getSession().getAttribute("user");
			Connection conn = null;
			Statement stmt = null;
			try {
				conn = new DBCPConnectionProvider().getConnection(conn);
				conn.setAutoCommit(false);
				String type = request.getParameter("type");
				String id = request.getParameter("id");
				String cancelReason = request.getParameter("cancelReason");
				if (type != null && id != null) {
					if (type.equalsIgnoreCase("OM")) {
						// cancel order
						Order order = new MOrder().find(id);
						order.setDocStatus(Order.DOC_VOID);
						//new MOrder().save(order, user.getId(), conn);
						// cancel order
						String sql = "update t_order set doc_status = 'VO' ,cancel_reason='"+cancelReason+"' where order_id = " + id + "";
						stmt = conn.createStatement();
						stmt.execute(sql);
						
						// cancel receipt @ order
						sql = "update t_receipt set doc_status = 'VO' where receipt_id in (";
						sql += "select receipt_id from t_receipt_line where order_id = " + id + ")";
						stmt = conn.createStatement();
						stmt.execute(sql);
					}
					if (type.equalsIgnoreCase("RR")) {
						// cancel receipt
						Receipt receipt = new MReceipt().find(id);
						receipt.setDocStatus(Order.DOC_VOID);
						new MReceipt().saveWOCheckDup(receipt, user.getId(), conn);

						// unpaid to order
						List<ReceiptLine> lines = new MReceiptLine().lookUp(Integer.parseInt(id));
						for (ReceiptLine l : lines) {
							if (l.getOrder() != null) {
								l.getOrder().setPayment("N");
								l.getOrder().setIsCash("N");
								new MOrder().save(l.getOrder(), user.getId(), conn);
								if (l.getOrderLine() == null || l.getOrderLine().getId() == 0) {
									List<OrderLine> orliLines = new MOrderLine().lookUp(l.getOrder().getId());
									for (OrderLine orliLine : orliLines) {
										orliLine.setPayment("N");
										new MOrderLine().save(orliLine, user.getId(), conn);
									}
								} else {
									if (l.getOrderLine().getId() != 0) {
										l.getOrderLine().setPayment("N");
										new MOrderLine().save(l.getOrderLine(), user.getId(), conn);
									}
								}
							}
						}
					}
				}
				conn.commit();
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			} catch (Exception e) {
				try {
					conn.rollback();
				} catch (Exception e2) {}
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
						+ e.getMessage());
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (Exception e2) {}
				try {
					conn.close();
				} catch (Exception e2) {}
			}

			manageOrderReceiptForm.setOrders(new ManageOrderReceiptProcess().getOrders(manageOrderReceiptForm
					.getDocumentDate()));
			manageOrderReceiptForm.setReceipts(new ManageOrderReceiptProcess().getReceipt(manageOrderReceiptForm
					.getDocumentDate()));
			manageOrderReceiptForm.setOrderSize(manageOrderReceiptForm.getOrders().size());
			manageOrderReceiptForm.setReceiptSize(manageOrderReceiptForm.getReceipts().size());

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

	@Override
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {
	// TODO Auto-generated method stub

	}

}
