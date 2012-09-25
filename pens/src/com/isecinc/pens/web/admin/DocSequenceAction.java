package com.isecinc.pens.web.admin;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MDocSequence;

/**
 * DocSeq Action Class
 * 
 * @author Atiz.b
 * @version $Id: DocSequenceAction.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 *          atiz.b : edit for dd sequence
 * 
 */
public class DocSequenceAction extends I_Action {

	/**
	 * Generate New Seq
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward genSeq(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			new MDocSequence().genSeq(user);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		}
		return mapping.findForward("re-search");
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocSequenceForm docSequenceForm = (DocSequenceForm) form;
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			String whereCause = "";
			if (user.getType().equalsIgnoreCase(User.TT) || user.getType().equalsIgnoreCase(User.VAN)) {
				whereCause = "AND SALES_CODE = '" + user.getCode().trim() + "' ";
			}
			if (user.getType().equalsIgnoreCase(User.DD)) {
				whereCause = "AND SALES_CODE = '' ";
			}
			whereCause += " ORDER BY SALES_CODE, DOCTYPE_ID, CURRENT_YEAR DESC, CURRENT_MONTH DESC";
			DocSequence[] results = new MDocSequence().search(whereCause);
			docSequenceForm.setResults(results);
			if (results != null) {
				docSequenceForm.getCriteria().setSearchResult(results.length);
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
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DocSequenceForm docSequenceForm = (DocSequenceForm) form;
		try {
			DocSequence docSequence = null;
			docSequence = new MDocSequence().find(id);
			if (docSequence == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			docSequenceForm.setDocSequence(docSequence);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		DocSequenceForm docSequenceForm = (DocSequenceForm) form;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			new MDocSequence().save(docSequenceForm.getDocSequence(), userActive.getId(), conn);
			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			throw e;
		} finally {
			conn.close();
		}
		return "prepare";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewCriteria(ActionForm form) {
	// TODO Auto-generated method stub

	}

}
