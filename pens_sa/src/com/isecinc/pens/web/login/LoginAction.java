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
import com.isecinc.pens.process.login.LoginProcess;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

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
	private static String DEFAULT_PASSWORD = "12345";
	
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
			conn = DBConnection.getInstance().getConnection_();
			user = new LoginProcess().login(loginForm.getUserName(), loginForm.getPassword(), conn);
            
			if (user == null) {
				request.setAttribute("errormsg", "ไม่พบชื่อผู้ใช้งาน");
				return mapping.findForward("fail");
			}else{
				logger.debug("User Group:"+user.getUserGroupId());
				if(user.getPassword().equalsIgnoreCase(DEFAULT_PASSWORD)){
					forwordStr = "change_password";
					request.setAttribute("loginMsg", "กรุณาเปลี่ยนรหัสผ่านใหม่ เนื่องจาก รหัสผ่านคุณเป็นรหัสผ่านชั่วคราว");
				}
			}
			
			String screenWidth = Utils.isNull(request.getParameter("screenWidth"));
			logger.debug("Before ScreenWidth:"+screenWidth);
			if(Integer.parseInt(screenWidth) < 600){
				screenWidth = "0";
			}
			logger.debug("After Calc ScreenWidth:"+screenWidth);
			
			request.getSession(true).setAttribute("screenWidth", screenWidth);
			
			request.getSession(true).setAttribute("user", user);
			request.getSession().setAttribute("User", user.getUserName());//Show in Session tomcat
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
}
