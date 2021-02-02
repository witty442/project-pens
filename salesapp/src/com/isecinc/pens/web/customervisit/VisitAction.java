package com.isecinc.pens.web.customervisit;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.bean.Visit;
import com.isecinc.pens.bean.VisitLine;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MTrxHistory;
import com.isecinc.pens.model.MVisit;
import com.isecinc.pens.model.MVisitLine;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DateToolsUtil;

/**
 * Visit Action Class
 * 
 * @author Aneak.t
 * @version $Id: VisitAction.java,v 1.0 22/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class VisitAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Customer Visit Prepare Form");
		VisitForm visitForm = (VisitForm) form;
		Visit visit = null;

		try {

			if (visitForm.getCriteria().getFrom() == null) {
				visitForm.getCriteria().setFrom(request.getParameter("from"));
			}
			visit = new MVisit().find(id);
			if (visit == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}

			// Customer customer = new MCustomer().find(String.valueOf(visitForm.getVisit().getCustomerId()));
			// visit.setCustomerLabel(customer.getCode() + " " + customer.getName() + " " + customer.getName2());
			visitForm.setLines(new MVisitLine().lookUp(visit.getId()));
			visitForm.setVisit(visit);

			// get back search key
			if (visitForm.getCriteria().getSearchKey() == null) {
				if (request.getSession(true).getAttribute("CMSearchKey") != null) {
					visitForm.getCriteria().setSearchKey((String) request.getSession(true).getAttribute("CMSearchKey"));
				} else {
					request.getSession(true).removeAttribute("CMSearchKey");
				}
			}

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		if (request.getParameter("action").equalsIgnoreCase("edit")) return "prepare_1";
		return "view";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Customer Visit Prepare Form without ID");
		VisitForm visitForm = (VisitForm) form;
		String customerId = request.getParameter("customerId") != null ? (String) request.getParameter("customerId")
				: "";
		String action = request.getParameter("action") != null ? (String) request.getParameter("action") : "";
		Connection conn = null;
		Visit visit = null;
		Customer customer = null;

		try {
			if (visitForm.getCriteria().getFrom() == null) {
				visitForm.getCriteria().setFrom(request.getParameter("from"));
			}

			User userActive = (User) request.getSession().getAttribute("user");

			conn = new DBCPConnectionProvider().getConnection(conn);
			visit = new Visit();
			if (!customerId.equals("")) {
				customer = new MCustomer().find(customerId);
				visit.setCustomerLabel(customer.getCode() + " " + customer.getName() + " " + customer.getName2());
				visit.setCustomerId(customer.getId());
			} else {
				customerId = String.valueOf(visitForm.getVisit().getCustomerId());
				customer = new MCustomer().find(customerId);
				visit.setVisitDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
				visit.setVisitTime(DateToolsUtil.getCurrentDateTime("HH:mm"));
				visit.setCustomerLabel(customer.getCode() + " " + customer.getName() + " " + customer.getName2());
				visit.setCustomerId(customer.getId());
			}
			visit.setUser(userActive);
			visitForm.setVisit(visit);

			// get Customer/Member Search Key
			if (request.getParameter("key") != null) {
				request.getSession(true).setAttribute("CMSearchKey", request.getParameter("key"));
			}

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		if (action.equals("add")) return "prepare_1";
		else return "prepare_2";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		VisitForm visitForm = (VisitForm) form;
		User user = (User) request.getSession().getAttribute("user");

		try {
			VisitCriteria criteria = getSearchCriteria(request, visitForm.getCriteria(), this.getClass().toString());
			visitForm.setCriteria(criteria);
			String whereCause = "";
			if (visitForm.getVisit().getCustomerId() != 0) {
				whereCause += " AND CUSTOMER_ID = " + visitForm.getVisit().getCustomerId();
			}
			if (visitForm.getVisit().getCode() != null && !visitForm.getVisit().getCode().trim().equals("")) {
				whereCause += " AND CODE LIKE '%"
						+ visitForm.getVisit().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%'";
			}
			if (visitForm.getVisit().getDateFrom() != null && !visitForm.getVisit().getDateFrom().trim().equals("")) {
				whereCause += " AND VISIT_DATE >= '"
						+ DateToolsUtil.convertToTimeStamp(visitForm.getVisit().getDateFrom().trim()) + "'";
			}
			if (visitForm.getVisit().getDateTo() != null && !visitForm.getVisit().getDateTo().trim().equals("")) {
				whereCause += " AND VISIT_DATE <= '"
						+ DateToolsUtil.convertToTimeStamp(visitForm.getVisit().getDateTo().trim()) + "'";
			}
			if (visitForm.getVisit().getIsActive() != null && !visitForm.getVisit().getIsActive().equals("")) {
				whereCause += " AND ISACTIVE = '" + visitForm.getVisit().getIsActive() + "'";
			}

			whereCause += " AND USER_ID = " + user.getId();
			whereCause += " ORDER BY VISIT_DATE DESC, VISIT_TIME DESC ";

			Visit[] results = new MVisit().search(whereCause);
			visitForm.setResults(results);

			if (results != null) {
				visitForm.getCriteria().setSearchResult(results.length);
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
		VisitForm visitForm = (VisitForm) form;
		Connection conn = null;
		int visitId = 0;
		try {
			visitId = visitForm.getVisit().getId();

			// check Token
			if (!isTokenValid(request)) {
				// VAN && TT
				Customer customer = new MCustomer().find(String.valueOf(visitForm.getVisit().getCustomerId()));
				visitForm.setVisit(new Visit());
				visitForm.getVisit().setCustomerId(customer.getId());
				visitForm.getVisit().setCustomerLabel(
						customer.getCode() + " " + customer.getName() + " " + customer.getName2());
				visitForm.getLines().clear();
				return "view";
			}

			User userActive = (User) request.getSession().getAttribute("user");

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			// Transaction
			// Save Visit
			visitForm.getVisit().setOrderType(userActive.getOrderType().getKey());
			if (!new MVisit().save(visitForm.getVisit(), userActive.getCode(), userActive.getId(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare_1";
			}

			int lineNo = 1;
			for (VisitLine line : visitForm.getLines()) {
				line.setVisitId(visitForm.getVisit().getId());
				line.setLineNo(lineNo);
				new MVisitLine().save(line, userActive.getId(), conn);
				lineNo++;
			}

			// Trx History
			TrxHistory trx = new TrxHistory();
			trx.setTrxModule(TrxHistory.MOD_VISIT);
			if (visitId == 0) trx.setTrxType(TrxHistory.TYPE_INSERT);
			else trx.setTrxType(TrxHistory.TYPE_UPDATE);
			trx.setRecordId(visitForm.getVisit().getId());
			trx.setUser(userActive);
			new MTrxHistory().save(trx, userActive.getId(), conn);
			// Trx History --end--

			// Delete Line
			if (ConvertNullUtil.convertToString(visitForm.getDeletedId()).trim().length() > 0)
				new MVisitLine().delete(visitForm.getDeletedId().substring(1).trim(), conn);

			// Commit Transaction
			conn.commit();

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			visitForm.getVisit().setId(visitId);
			conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			return "prepare_1";
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return "view";
	}

	/**
	 * Clear Form
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		VisitForm visitForm = (VisitForm) form;
		String from = visitForm.getCriteria().getFrom();
		Customer customer = new MCustomer().find(String.valueOf(visitForm.getVisit().getCustomerId()));
		visitForm.setCriteria(new VisitCriteria());
		visitForm.getCriteria().setFrom(from);
		visitForm.getVisit().setCustomerId(customer.getId());
		visitForm.getVisit()
				.setCustomerLabel(customer.getCode() + " " + customer.getName() + " " + customer.getName2());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
