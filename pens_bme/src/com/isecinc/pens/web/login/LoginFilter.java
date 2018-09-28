package com.isecinc.pens.web.login;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.isecinc.pens.SystemMessages;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.managepath.ManagePath;

/**
 * LoginFilter Class
 * 
 * @author Atiz.b
 * @version $Id: LoginFilter.java,v 1.0 14/06/2010 00:00:00 atiz.b Exp $
 * 
 */
public class LoginFilter extends HttpServlet implements Filter {

	private static final long serialVersionUID = 5493413465602830286L;

	private Logger logger = Logger.getLogger("PENS");

	private FilterConfig config = null;

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	public void destroy() {
		config = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		// logger.debug("do filter...");
		response.setContentType("text/html;charset=TIS620");

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String[] values;
		String logoff = "";

		HttpSession session = req.getSession();

		values = req.getParameterValues("logoff");
		
		if (values != null) {
			logoff = new String(values[0]);
		}
		//logger.debug("logoff["+req.getParameter("logoff")+"]");
		if (!logoff.equals("")) {
			logger.debug("login off...");

			// for logoff link
			// do when logoff is not null
			session.removeAttribute("user");// remove user from session
			RequestDispatcher rd = config.getServletContext().getRequestDispatcher("/index.jsp");
			rd.forward(req, res);
		} else if (session.getAttribute("user") != null) {
			//logger.debug("have user...");
			// have user
			chain.doFilter(request, response);
		} else {
			logger.debug("no user...");
			// no user in session
			req.setAttribute("errormsg", SystemMessages.getCaption(SystemMessages.CREDENTIAL_REQUIRE, Locale.getDefault()));
			RequestDispatcher rd = config.getServletContext().getRequestDispatcher("/index.jsp");
			rd.forward(req, res);
		}
	}
}
