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

import util.AppversionVerify;
import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.SessionGen;

import com.isecinc.core.bean.Messages;
import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.inf.manager.batchwork.AppversionVerifyWorker;
import com.isecinc.pens.inf.manager.batchwork.DownloadSalesAppWorker;
import com.isecinc.pens.inf.manager.batchwork.BatchImportWorker;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.process.login.LoginProcess;

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
		try {
			//remove session id
			SessionGen.getInstance().clearInstance();
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			User user = null;
			conn = new DBCPConnectionProvider().getConnection(conn);
			user = new LoginProcess().login(loginForm.getUserName(), loginForm.getPassword(), conn);
			if (user == null) {
				request.setAttribute("errormsg", SystemMessages.getCaption(SystemMessages.INVALID_CREDENTIAL, Locale
						.getDefault()));
				return mapping.findForward("fail");
			}
			if (ConvertNullUtil.convertToString(user.getType()).length() == 0) {
				request.setAttribute("errormsg", InitialMessages.getMessages().get(Messages.NO_ACCESS_PRIVILEGE)
						.getDesc());
				return mapping.findForward("fail");
			}
			request.getSession(true).setAttribute("user", user);
			
			String screenWidth = Utils.isNull(request.getParameter("screenWidth"));
			if(screenWidth.equals("")){
				screenWidth ="0";
			}
			String screenHeight = Utils.isNull(request.getParameter("screenHeight"));
			if(screenHeight.equals("")){
				screenHeight ="0";
			}
			logger.debug("Before ScreenWidth["+screenWidth+"]");
			if(Integer.parseInt(screenWidth) < 600){
				screenWidth = "0";
			}
			logger.debug("After Calc ScreenWidth:"+screenWidth);
			
			request.getSession(true).setAttribute("screenWidth", screenWidth);
			request.getSession(true).setAttribute("screenHeight", screenHeight);
			
			//clear check version
			request.getSession().setAttribute("appVersionCheckMsg",null);
			request.getSession().setAttribute("massageToSales",null);
			request.getSession().setAttribute("appVersionMassageToSales",null);
			
		
			// Start init Appversion Versity
			new AppversionVerifyWorker().start();
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			request.setAttribute("errormsg", e.getMessage());
			return mapping.findForward("fail");
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("pass");
	}
	
	public ActionForward logoff(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		String forwordStr = "logoff";
		try {
			logger.debug("logoff");
			//Manage Path
			if(request.getSession().getAttribute("user") != null){
			 // User user = (User)request.getSession().getAttribute("user") ;
			 // ManagePath.savePath(user, "logoff");
			}
			
			//remove session id
			SessionGen.getInstance().clearInstance();
			
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
