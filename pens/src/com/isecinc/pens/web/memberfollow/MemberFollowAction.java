package com.isecinc.pens.web.memberfollow;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.MemberFollow;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MMemberFollow;

/**
 * Member Follow Action Class
 * 
 * @author Aneak.t
 * @version $Id: MemberFollowAction.java,v 1.0 14/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberFollowAction extends I_Action {

	/**
	 * Save.
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		MemberFollowForm mFollowForm = (MemberFollowForm) form;
		int customerId = 0;

		try {

			customerId = mFollowForm.getMemberFollow().getCustomerId();

			// check Token
			if (!isTokenValid(request)) {
				mFollowForm.setMemberFollow(new MemberFollow());

				return "re-search";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");
			MemberFollow mFollow = mFollowForm.getMemberFollow();

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin transaction.
			conn.setAutoCommit(false);

			// Save Member Follow
			if (new MMemberFollow().save(mFollow, userActive.getId(), conn)) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
				mFollowForm.getMemberFollow().setCustomerId(customerId);
			}
			// Commit transaction.
			conn.commit();

			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return "re-search";
	}

	/**
	 * Search.
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MemberFollowForm mFollowForm = (MemberFollowForm) form;
		// User user = (User) request.getSession().getAttribute("user");
		String fs = (String) request.getParameter("fs");
		String whereCause = "";

		try {
			MemberFollowCriteria criteria = getSearchCriteria(request, mFollowForm.getCriteria(), this.getClass()
					.toString());
			mFollowForm.setCriteria(criteria);
			if (fs != null && !fs.equals("")) {
				mFollowForm.setIsFirst(fs);
			}

			whereCause += " AND ISACTIVE = 'Y' ";
			whereCause += " AND CUSTOMER_ID = " + mFollowForm.getMemberFollow().getCustomerId();

			MemberFollow[] results = new MMemberFollow().search(whereCause);
			mFollowForm.setResults(results);

			if (results != null) {
				mFollowForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			// Save Token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}

		return "search";
	}

	/**
	 * Change status active.
	 */
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Set new criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
	// TODO Auto-generated method stub

	}

}
