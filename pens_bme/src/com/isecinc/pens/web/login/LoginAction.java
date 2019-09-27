package com.isecinc.pens.web.login;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.login.LoginProcess;
import com.isecinc.pens.scheduler.utils.DateUtil;
import com.isecinc.pens.web.managepath.ManagePath;
import com.pens.util.DBConnection;
import com.pens.util.EncyptUtils;
import com.pens.util.SIdUtils;
import com.pens.util.Utils;

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

	
	public static void main(String[] s){
		try{
			String pathRedirect = "/jsp/interfacesAction.do?do=prepare$pageAction=new$pageName=_GEN_ORDER_EXCEL";
			String url  = pathRedirect.replaceAll("\\$", "&");
			System.out.println("url:"+url);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
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
			
			//remove session id
			SIdUtils.getInstance().clearInstance();
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			User user = null;
			conn = DBConnection.getInstance().getConnection();
			user = new LoginProcess().login(loginForm.getUserName(), loginForm.getPassword(), conn);
            
			if (user == null) {
				request.setAttribute("errormsg", "ไม่พบชื่อผู้ใช้งาน");
				return mapping.findForward("fail");
			}
			//"Login", "User", "userName", "UserName", "Utilisateur" };
			user.setDateLogon(DateUtil.getCurrentDate());
            user.setTimeLogon(DateUtil.getCurrentTime());
			request.getSession(true).setAttribute("user", user);
		
			//request.getSession().setAttribute("UserName", user.getUserName());
		
			String role = user.getRole().getKey();
			logger.debug("role:"+role);
			if(User.MC.equalsIgnoreCase(role) || User.MT_SALE.equalsIgnoreCase(role)){
				forwordStr = "pass_user_mc";
			}
			
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
			
			request.getSession().setAttribute("User", user.getUserName()+"_"+role);
			request.getSession(true).setAttribute("screenWidth", screenWidth);
			request.getSession(true).setAttribute("screenHeight", screenHeight);
			
			if(role.equalsIgnoreCase(User.NISSINTEAM)){
				forwordStr = "passRoleNissin";
			}else if(role.equalsIgnoreCase(User.PENSTEAM)){
				forwordStr = "passRolePensTeam";
			}
			logger.debug("forwordStr:"+forwordStr);
			
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
	
	public ActionForward loginCrossServer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		LoginForm loginForm = null;
		String forwordStr = "pass_user";
		String url = "";
		try {
			logger.debug("loginCrossServer Locale:"+Locale.getDefault());
			
			//remove session id
			SIdUtils.getInstance().clearInstance();
			
			String serverForm = Utils.isNull(request.getParameter("serverUrl"));
			//payAction|prepare2|new
			String pathRedirect = Utils.isNull(request.getParameter("pathRedirect"));
			logger.debug("url before:"+pathRedirect);
			
		    if( !pathRedirect.equals("")){
		    	url  = pathRedirect.replaceAll("\\$", "&");
		    	request.setAttribute("url",url );
		    }
			logger.debug("serverForm:"+serverForm);
			logger.debug("url after:"+url);
			
			String userName = Utils.isNull(request.getParameter("userName"));
			String password = Utils.isNull(request.getParameter("password"));
			//decode
			password = EncyptUtils.base64decode(password);
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			User user = null;
			conn = DBConnection.getInstance().getConnection();
			user = new LoginProcess().login(userName, password, conn);
            
			if (user == null) {
				request.setAttribute("errormsg", "ไม่พบชื่อผู้ใช้งาน");
				return mapping.findForward("fail");
			}
			
			request.getSession(true).setAttribute("user", user);
			request.getSession(true).setAttribute("username", user.getUserName());
			
			String role = user.getRole().getKey();
			logger.debug("role:"+role);
			
			forwordStr = "pathRedirect";
			
			logger.debug("forwordStr:"+forwordStr);
			
			String screenWidth = Utils.isNull(request.getParameter("screenWidth"));
			if(screenWidth.equals("")){
				screenWidth ="0";
			}
			logger.debug("Before ScreenWidth["+screenWidth+"]");
			if(Integer.parseInt(screenWidth) < 600){
				screenWidth = "0";
			}
			logger.debug("After Calc ScreenWidth:"+screenWidth);
			
			request.getSession(true).setAttribute("screenWidth", screenWidth);
			
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
	
	public ActionForward loginCrossServerOLD(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		LoginForm loginForm = null;
		String forwordStr = "pass_user";
		try {
			logger.debug("loginCrossServer Locale:"+Locale.getDefault());
			//remove session id
			SIdUtils.getInstance().clearInstance();
			
			String serverForm = Utils.isNull(request.getParameter("serverUrl"));
			//payAction|prepare2|new
			String pathRedirect = Utils.isNull(request.getParameter("pathRedirect"));
			String url = "";
		    if( !pathRedirect.equals("")){
		    	String[] p = pathRedirect.split("\\|");
		    	url  ="/jsp/"+p[0]+".do?do="+p[1]+"&action="+p[2];
		    	request.setAttribute("url",url );
		    }
			
			String userName = Utils.isNull(request.getParameter("userName"));
			String password = Utils.isNull(request.getParameter("password"));
			
			logger.debug("serverForm:"+serverForm);
			logger.debug("pathRedirect:"+pathRedirect +",url:"+url);
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			User user = null;
			conn = DBConnection.getInstance().getConnection();
			user = new LoginProcess().login(userName, password, conn);
            
			if (user == null) {
				request.setAttribute("errormsg", "ไม่พบชื่อผู้ใช้งาน");
				return mapping.findForward("fail");
			}
			
			request.getSession(true).setAttribute("user", user);
			request.getSession(true).setAttribute("username", user.getUserName());
			
			String role = user.getRole().getKey();
			logger.debug("role:"+role);
			
			forwordStr = "pathRedirect";
			
			logger.debug("forwordStr:"+forwordStr);
			
			String screenWidth = Utils.isNull(request.getParameter("screenWidth"));
			String screenHeight = Utils.isNull(request.getParameter("screenHeight"));
			if(screenWidth.equals("")){
				screenWidth ="0";
			}
			logger.debug("Before ScreenWidth["+screenWidth+"]");
			if(Integer.parseInt(screenWidth) < 600){
				screenWidth = "0";
			}
			logger.debug("After Calc ScreenWidth:"+screenWidth);
			
			request.getSession(true).setAttribute("screenWidth", screenWidth);
			request.getSession(true).setAttribute("screenHeight", screenHeight);
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
			
			//remove session id
			SIdUtils.getInstance().clearInstance();
			
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
