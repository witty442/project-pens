package com.isecinc.pens.web.user;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SalesInventory;
import com.isecinc.pens.bean.SubInventory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MPD;
import com.isecinc.pens.model.MSalesInventory;
import com.isecinc.pens.model.MSubInventory;
import com.isecinc.pens.model.MUser;
import com.pens.util.DBCPConnectionProvider;
 
/**
 * User Action Class
 * 
 * @author Atiz.b
 * @version $Id: UserAction.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class UserAction extends I_Action {

	/**
	 * Profile Page
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward profile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Profile ");
		UserForm userForm = (UserForm) form;
		try {
			String id = (String) request.getParameter("id");
			if(id ==null)
				id = userForm.getId();
			
			User user = null;
			user = new MUser().find(id);
			if (user == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			userForm.setUser(user);

			//
			userForm.setPdList(new MPD().getPDList(user));
			
			/*userForm.setSubInventories(new MSubInventory().lookUp());

			// selected invs
			List<SalesInventory> invs = new MSalesInventory().lookUp(userForm.getUser().getId());
			if (invs.size() != 0) {
				for (SubInventory s : userForm.getSubInventories()) {
					for (SalesInventory ss : invs) {
						if (ss.getSubInventoryId() == s.getId()) {
							s.setSelected("Y");
							break;
						}
					}
				}
			}
			if (userForm.getSubInventories().size() == 0) userForm.setSubInventories(null);*/
			
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}
		return mapping.findForward("profile");
	}

	/**
	 * Profile Save
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward profileSave(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("Profile Save");
		Connection conn = null;
		UserForm userForm = (UserForm) form;
		User user = null;
		MUser mUser = new MUser();
		
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			user = mUser.find(String.valueOf(userForm.getUser().getId()));
			user.setPdPaid(userForm.getUser().getPdPaid());
			mUser.save(user, user.getId(), conn);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}
		
		// Set New User In Session
		if(user != null){
		  request.getSession().setAttribute("user", user);
		  userForm.getCriteria().setUser(user);
		  userForm.setId(""+user.getId());
		}
		return profile(mapping, form, request,response);
	}

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm userForm = (UserForm) form;
		try {
			User user = null;
			user = new MUser().find(id);
			if (user == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			userForm.setUser(user);
			//
			userForm.setSubInventories(new MSubInventory().lookUp());

			// selected invs
			List<SalesInventory> invs = new MSalesInventory().lookUp(userForm.getUser().getId());
			if (invs.size() != 0) {
				for (SubInventory s : userForm.getSubInventories()) {
					for (SalesInventory ss : invs) {
						if (ss.getSubInventoryId() == s.getId()) {
							s.setSelected("Y");
							break;
						}
					}
				}
			}
			if (userForm.getSubInventories().size() == 0) userForm.setSubInventories(null);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserForm userForm = (UserForm) form;
		try {
			// get SearchCriteria
			UserCriteria criteria = getSearchCriteria(request, userForm.getCriteria(), this.getClass().toString());
			userForm.setCriteria(criteria);
			String whereCause = "";
			// CAUSE
			if (userForm.getUser().getCode().trim().length() > 0)
				whereCause += "  AND CODE LIKE '%"
						+ userForm.getUser().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' ";
			if (userForm.getUser().getName().trim().length() > 0)
				whereCause += "  AND NAME LIKE '%"
						+ userForm.getUser().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' ";
			// A-neak.t 27/12/2010
			if(userForm.getUser().getUserName().trim().length() > 0)
				whereCause += " AND USER_NAME LIKE '%"
						+ userForm.getUser().getUserName().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%'";
			if (userForm.getUser().getType().trim().length() > 0)
				whereCause += "  AND ROLE = '" + userForm.getUser().getType().trim() + "' ";
			if (userForm.getUser().getActive().trim().length() > 0)
				whereCause += "  AND ISACTIVE = '" + userForm.getUser().getActive().trim() + "' ";
			//
			User[] results = new MUser().search(whereCause);
			userForm.setResults(results);
			if (results != null) {
				userForm.getCriteria().setSearchResult(results.length);
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
		UserForm userForm = (UserForm) form;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			new MUser().save(userForm.getUser(), userActive.getId(), conn);

			// delete sales inventory
			new MSalesInventory().deleteByUserId(String.valueOf(userForm.getUser().getId()), conn);

			// save sales inventory
			if (userForm.getSubinvids() != null)
				new MSalesInventory().save(userForm.getSubinvids(), userForm.getUser().getId(), conn);

			// Commit Transaction
			conn.commit();
			//
			userForm.setSubInventories(new MSubInventory().lookUp());

			// selected invs
			List<SalesInventory> invs = new MSalesInventory().lookUp(userForm.getUser().getId());
			if (invs.size() != 0) {
				for (SubInventory s : userForm.getSubInventories()) {
					for (SalesInventory ss : invs) {
						if (ss.getSubInventoryId() == s.getId()) {
							s.setSelected("Y");
							break;
						}
					}
				}
			}

			if (userForm.getSubInventories().size() == 0) userForm.setSubInventories(null);

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "prepare";
	}

	/**
	 * Change Active
	 */
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Connection conn = null;
		UserForm userForm = (UserForm) form;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			new MUser().changeActive(userForm.getStatus(), userForm.getIds(), userActive.getId(), conn);
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
		return "re-search";
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		UserForm userForm = (UserForm) form;
		userForm.setCriteria(new UserCriteria());
	}

}
