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

import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.process.login.LoginProcess;
import com.pens.util.ControlCode;
import com.pens.util.DBConnection;
import com.pens.util.SIdUtils;
import com.pens.util.UserUtils;
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
		boolean user2Role =false;
		User user = null;
		try {
			//logger.debug("Locale:"+Locale.getDefault());
			
			/** control code redirect to new version SalesAnalysis **/
			if(ControlCode.canExecuteMethod("SalesAnalysis", "NewVersion")){
				forwordStr = "pass_user_new";
			}
			
			//remove session id
			SIdUtils.getInstance().clearInstance();
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			
			conn = DBConnection.getInstance().getConnection();
			user = new LoginProcess().login(loginForm.getUserName(), loginForm.getPassword(), conn);
			
			//Case Van Login dummy
			if(user ==null && loginForm.getUserName().startsWith("V")){
				user = loginDummyVan(loginForm.getUserName(), loginForm.getPassword());
			 
			//Case user creditSale not found in c_user_info login by dummy
			}else if(user ==null && loginForm.getUserName().startsWith("S")){
				user = loginDummyCredit(loginForm.getUserName(), loginForm.getPassword());
			}
           
            
			if (user == null) {
				request.setAttribute("errormsg", "ไม่พบชื่อผู้ใช้งาน หรือ รหัสผ่านไม่ถูกต้อง");
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
			if(Double.parseDouble(screenWidth) < 600){
				screenWidth = "0";
			}
			logger.debug("After Calc ScreenWidth:"+(Double.parseDouble(screenWidth)-50));
			
			request.getSession(true).setAttribute("screenWidth", ""+(Double.parseDouble(screenWidth)-100));
			
			//set mobile device
			String mobile = Utils.isNull(request.getParameter("mobile"));
			user.setMobile("true".equalsIgnoreCase(mobile)?true:false);
			user.setScreenWidth((Double.parseDouble(screenWidth)-100));
			
			request.getSession(true).setAttribute("user", user);
			request.getSession().setAttribute("User", user.getUserName());//Show in Session tomcat
			
			/** Role SA ,Admin --<**/
			if( !UserUtils.userInRole("ROLE_SA",user, new String[]{User.ADMIN,User.SA})){
				forwordStr = "pass_salestarget";
			}
			
			logger.debug("UserRoleCount:"+UserUtils.userIsMultiRoleCount(user));
			
			/** check User is more 1 Role **/
			if( !user.getUserName().equalsIgnoreCase("admin") 
					&& UserUtils.userIsMultiRoleCount(user) ==1
					&& UserUtils.userInRole("ROLE_MC",user, new String[]{User.MC_ENTRY})){
				forwordStr = "true".equalsIgnoreCase(mobile)?"pass_mcentryMobile": "pass_mcentry";
			}
			
			/** hard code for SOFIAN user MC force to Mobile device ??? wait for debug IPHONE**/
			/*if(user.getUserName().equalsIgnoreCase("sofian")){
				user.setMobile(true);
				forwordStr = "pass_mcentryMobile";
				request.getSession(true).setAttribute("user", user);
			}*/
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
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
		//Connection conn = null;
		LoginForm loginForm = null;
		String forwordStr = "pathRedirect";
		String url = "";
		try {
			logger.debug("PENS_SA loginCrossServer Locale:"+Locale.getDefault());
			
			//remove session id
			//SessionIdUtils.getInstance().clearInstance();
			
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
			String pageName = Utils.isNull(request.getParameter("pageName"));
			
			//decode
			password = com.pens.util.EncyptUtils.base64decode(password);
			
			request.getSession(true).removeAttribute("user");
			loginForm = (LoginForm) form;
			
			//conn = DBConnection.getInstance().getConnection();
			//user = new LoginProcess().login(userName, password, conn);
			/*
            
			if (user == null) {
				request.setAttribute("errormsg", "ไม่พบชื่อผู้ใช้งาน");
				return mapping.findForward("fail");
			}*/
			User user = new User();
			user.setUserName(userName);
			user.setPassword(password);
			if(userName.startsWith("S") || userName.startsWith("V")){
				if(userName.startsWith("S")){
				   user.setUserGroupName("Credit Sales");
				}else if(userName.startsWith("V")){
				   user.setUserGroupName("Van Sales");
				}
				SalesrepBean salesrepBean =  SalesrepDAO.getSalesrepBeanByCode(user.getUserName());
				if(salesrepBean != null){
					user.setName(user.getUserName() +" "+salesrepBean.getSalesrepFullName());
					user.setId(Utils.convertStrToInt(salesrepBean.getSalesrepId()));
					user.setCode(salesrepBean.getCode());
					user.setSalesrepFullName(salesrepBean.getSalesrepFullName());
				}
			}else{
				user = null;
			}
			request.getSession(true).setAttribute("user", user);
			request.getSession(true).setAttribute("username", user.getUserName());
			
			/*String role = user.getRole().getKey();
			logger.debug("role:"+role);
			*/
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
			request.getSession(true).setAttribute("GEN_PDF_SUCCESS", null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			request.setAttribute("errormsg", e.getMessage());
			return mapping.findForward("fail");
		} finally {
			try {
				//conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward(forwordStr);
	}
	
	private User loginDummyVan(String userName,String password){
		User user = null;
		try{
			SalesrepBean salesrepBean =  SalesrepDAO.getSalesrepBeanByCode(userName);
			if(salesrepBean != null && password.equalsIgnoreCase("1234")){
				user = new User();
				user.setUserName(userName);
				user.setName(user.getUserName() +" "+salesrepBean.getSalesrepFullName());
				user.setPassword(password);
				user.setUserGroupName("Van Sales");
				//user.setRoleVanSales(User.VANSALES);
				user.setId(Utils.convertStrToInt(salesrepBean.getSalesrepId()));
				user.setCode(salesrepBean.getCode());
				user.setSalesrepFullName(salesrepBean.getSalesrepFullName());
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return user;
	}
	private User loginDummyCredit(String userName,String password){
		User user = null;
		try{
			SalesrepBean salesrepBean =  SalesrepDAO.getSalesrepBeanByCode(userName);
			if(salesrepBean != null && password.equalsIgnoreCase("1234")){
				user = new User();
				user.setUserName(userName);
				user.setName(user.getUserName() +" "+salesrepBean.getSalesrepFullName());
				user.setPassword(password);
				user.setUserGroupName("Credit Sales");
				//user.setRoleCreditSales(User.CREDITSALES);
				user.setId(Utils.convertStrToInt(salesrepBean.getSalesrepId()));
				user.setCode(salesrepBean.getCode());
				user.setSalesrepFullName(salesrepBean.getSalesrepFullName());
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return user;
	}
}
