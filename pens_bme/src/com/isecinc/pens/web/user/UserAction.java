package com.isecinc.pens.web.user;

import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.model.MUser;

/**
 * User Action Class
 * 
 * @author Atiz.b
 * @version $Id: UserAction.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class UserAction extends I_Action {

	
	public ActionForward init(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Init Action");	
		UserForm userForm = (UserForm) form;
		try {
			 
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
		return mapping.findForward("init");
	}
	
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
			logger.debug("ID:"+id);
			User user = null;
			user = new MUser().find(id);
			if (user == null) {
				request.setAttribute("Message", "Record not found");
			}
			userForm.setUser(user);

		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
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
		UserForm userForm = (UserForm) form;
		try {
			User user = null;
			user = new MUser().find(String.valueOf(userForm.getUser().getId()));
			//userForm.getUser().setActive(user.getActive());
			
			save(form, request, response);
		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
		}
		return mapping.findForward("profile");
	}
	
	public ActionForward changePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("changePassword");
		UserForm userForm = (UserForm) form;
		Connection conn = null;
		try {
			if( !Utils.isNull(request.getParameter("action")).equals("init")){
				//validate newPassword = reNewPassord
				if( !Utils.isNull(userForm.getUser().getNewPassword()).equals(Utils.isNull(userForm.getUser().getReNewPassword()))){
					 //request.setAttribute("Message", "New Password not Math Confirm New Password ");
					 request.setAttribute("Message", SystemMessages.getCaption("NewPasswordNotMatch", Locale.getDefault()));
				}else{
					//validate UserName && Password
					User user = new MUser().findByUserName(String.valueOf(userForm.getUser().getUserName()));
					if(user == null){
						request.setAttribute("Message", SystemMessages.getCaption("NoMemberCodeFor", Locale.getDefault())+userForm.getUser().getUserName());
					}else{
						//validate old password
						if( userForm.getUser().getPassword().equals(user.getPassword())){
							conn = DBConnection.getInstance().getConnection();
							conn.setAutoCommit(false);
							new MUser().changePassword(conn, user.getId(), Utils.isNull(userForm.getUser().getNewPassword()));
							//request.setAttribute("Message", "Change Password Success");
							request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
							conn.commit();
						}else{
							//request.setAttribute("Message", "Old password wrong");
							request.setAttribute("Message", SystemMessages.getCaption("OldPassWrong", Locale.getDefault()));
						}
					}
				}
			}
		} catch (Exception e) {
			try{
			   request.setAttribute("Message",  e.getMessage());
			   conn.rollback();
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return mapping.findForward("changePassword");
	}

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		 logger.debug("prepare with id");
		UserForm userForm = (UserForm) form;
		try {
			User user = null;
			user = new MUser().find(id);
			if (user == null) {
				request.setAttribute("Message", "Record not found");
			}
			userForm.setUser(user);
           
		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
			throw e;
		}
		return "prepare";
	}
	
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
    logger.debug("prepare no id");
	UserForm userForm = (UserForm) form;
	try {
		User u = new User();
		u.setUserName("");
		u.setStartDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
		userForm.setUser(u);
		
	} catch (Exception e) {
		request.setAttribute("Message",  e.getMessage());
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
			if (Utils.isNull(userForm.getUser().getCode()).trim().length() > 0)
				whereCause += "  AND A.CODE LIKE '%"
						+ userForm.getUser().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' ";
			if (Utils.isNull(userForm.getUser().getName()).trim().length() > 0)
				whereCause += "  AND A.NAME LIKE '%"
						+ userForm.getUser().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%' ";
			// A-neak.t 27/12/2010
			if(Utils.isNull(userForm.getUser().getUserName()).trim().length() > 0)
				whereCause += " AND A.USER_NAME LIKE '%"
						+ userForm.getUser().getUserName().trim().replace("\'", "\\\'").replace("\"", "\\\"") + "%'";
			
			if(userForm.getUser().getUserGroupId() !=0){
				whereCause += " AND A.USER_GROUP_ID ="+userForm.getUser().getUserGroupId();
			}
			
			if(Utils.isNull(userForm.getUser().getActive()).equals("Y")){
				whereCause  +=" AND(  ( A.START_DATE <= SYSDATE and A.END_DATE >= SYSDATE AND A.END_DATE IS NOT NULL) \n";
				whereCause  +="      OR  \n";
				whereCause  +="       (A.START_DATE <= SYSDATE  AND A.END_DATE IS NULL) \n";
				whereCause  +="     ) \n";
			}else if(Utils.isNull(userForm.getUser().getActive()).equals("N")){
				whereCause  +=" AND(  ( A.END_DATE < SYSDATE AND A.END_DATE IS NOT NULL) \n";
				whereCause  +="     ) \n";
			}
			logger.debug("whereClause:"+whereCause);
			String orderBy = "user_group_id,user_id";
			
			//User[] results = new MUser().searchM(whereCause,orderBy);
			
			/*userForm.setResults(results);
			if (results != null) {
				userForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", "Not found data");
			}*/
		} catch (Exception e) {
			request.setAttribute("Message",  e.getMessage());
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
			conn = DBConnection.getInstance().getConnection();
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			if(userForm.getUser().getId() != 0){
			    new MUser().update(userForm.getUser(), userActive.getId(), conn);
			}else{
				//int id = new MUser().insert(userForm.getUser(), userActive.getId(), conn);
				//userForm.getUser().setId(id);
			}
			request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", e.getMessage());
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
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
		Connection conn = null;
		UserForm userForm = (UserForm) form;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = DBConnection.getInstance().getConnection();
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			new MUser().changeActive(userForm.getStatus(), userForm.getIds(), userActive.getId(), conn);
			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", e.getMessage());
			throw e;
		} finally {
			conn.close();
		}
		return "re-search";
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward changeUserGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("changeUSerGroup Action");
		Connection conn = null;
		UserForm userForm = (UserForm) form;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = DBConnection.getInstance().getConnection();
			// Begin Transaction
			conn.setAutoCommit(false);
			//
			new MUser().changeUserGroup(userForm.getUser().getChangeUserGroup(), userForm.getIds(), userActive.getId(), conn);
			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", SystemMessages.getCaption("SaveSucess", Locale.getDefault()));
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception ex) {}
			request.setAttribute("Message", e.getMessage());
			throw e;
		} finally {
			conn.close();
		}
		return mapping.findForward("re-search");
	}

	public ActionForward backToMain(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("backToMain");
		try {	
			request.getSession().removeAttribute("critriaGroupList");
			request.getSession().removeAttribute("groupList");
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			request.setAttribute("Message",  e.toString());
		}
		return mapping.findForward("mainpage");
	}
	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		UserForm userForm = (UserForm) form;
		userForm.setCriteria(new UserCriteria());
	}

}
