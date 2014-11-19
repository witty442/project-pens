package com.isecinc.pens.web.login;

import java.sql.Connection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.process.login.LoginProcess;
import com.isecinc.pens.web.managepath.ManagePath;

/**
 * Login Action Class
 * 
 * @author Atiz.b
 * @version $Id: LoginAction.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public class LoginAction extends DispatchAction {

	/** Logger */
	private Logger logger = Logger.getLogger("PENS");

	/**
	 * Login
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		LoginForm loginForm = null;
		String forwordStr = "pass_user";
		try {
			logger.debug("Locale:"+Locale.getDefault());
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			User user = null;
			conn = DBConnection.getInstance().getConnection();
			user = new LoginProcess().login(loginForm.getUserName(), loginForm.getPassword(), conn);
            
			if (user == null) {
				request.setAttribute("errormsg", "��辺���ͼ����ҹ");
				return mapping.findForward("fail");
			}
			
			request.getSession(true).setAttribute("user", user);
			request.getSession(true).setAttribute("username", user.getUserName());
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			request.setAttribute("errormsg", e.getMessage());
			return mapping.findForward("fail");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward(forwordStr);
	}
	
	public ActionForward logoff(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String forwordStr = "logoff";
		try {
			logger.debug("logoff");
			//Manage Path
			if(request.getSession().getAttribute("user") != null){
			  User user = (User)request.getSession().getAttribute("user") ;
			  ManagePath.savePath(user, "logoff");
			}
			
			request.getSession().invalidate();
		} catch (Exception e) {
			logger.error(e.getMessage());
			request.setAttribute("errormsg", e.getMessage());
			return mapping.findForward("fail");
		} finally {
			
		}
		return mapping.findForward(forwordStr);
	}
}