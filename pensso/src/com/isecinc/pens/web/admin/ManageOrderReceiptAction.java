package com.isecinc.pens.web.admin;

import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.process.administer.ManageOrderReceiptProcess;
import com.pens.util.DBConnectionApps;
import com.pens.util.Debug;

public class ManageOrderReceiptAction extends I_Action {

	public Debug debug = new Debug(true);
	
	@Override
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ManageOrderReceiptForm manageOrderReceiptForm = (ManageOrderReceiptForm) form;
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			
			/*manageOrderReceiptForm.setOrders(new ManageOrderReceiptProcess().getOrders(manageOrderReceiptForm
					.getDocumentDate()));
			//manageOrderReceiptForm.setOrderSize(manageOrderReceiptForm.getOrders().size());
			*/
			
			manageOrderReceiptForm.setReceipts(new ManageOrderReceiptProcess().getReceipt(manageOrderReceiptForm,user));
			
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
				conn = DBConnectionApps.getInstance().getConnection();
				conn.setAutoCommit(false);
				String type = request.getParameter("type");
				String id = request.getParameter("id");
				if (type != null && id != null) {
					if (type.equalsIgnoreCase("OM")) {
						// cancel order
						//Order order = new MOrder().find(id);
						//order.setDocStatus(Order.STATUS_CANCEL);
						//new MOrder().save(order, user.getId(), conn);
						
						// cancel receipt @ order
						/*String sql = "update pensso.t_receipt set doc_status = 'VO' where receipt_id in (";
						sql += "select receipt_id from pensso.t_receipt_line where order_id = " + id + ")";
						stmt = conn.createStatement();
						stmt.execute(sql);*/
					}
					if (type.equalsIgnoreCase("RR")) {
						// cancel receipt
						Receipt receipt = new MReceipt().find(id);
						receipt.setDocStatus("VO");
						new MReceipt().saveWOCheckDup(receipt, user.getId(), conn);

					}
				}
				conn.commit();
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			} catch (Exception e) {
				e.printStackTrace();
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

			/*manageOrderReceiptForm.setOrders(new ManageOrderReceiptProcess().getOrders(manageOrderReceiptForm
					.getDocumentDate()));*/
			//manageOrderReceiptForm.setOrderSize(manageOrderReceiptForm.getOrders().size());
			
			manageOrderReceiptForm.setReceipts(new ManageOrderReceiptProcess().getReceipt(manageOrderReceiptForm,user));
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
