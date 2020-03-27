package com.isecinc.pens.web;

import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.isecinc.core.init.I_Initial;
import com.isecinc.pens.dao.InterfaceDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialParameter;
import com.pens.util.DBConnection;

/**
 * WebInitialServlet Class for Initial Web Parameter and Configuration
 * 
 * @author atiz.b
 * @version $Id: WebInitialServlet.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 * @modifier A-neak.t 10/11/2010
 * 
 */
public class WebInitialServlet extends HttpServlet {
	private static final long serialVersionUID = 7711018056252168383L;

	private Logger logger = Logger.getLogger("PENS");

	public void init() throws ServletException {
		//logger.debug("Initial PENS...");
		Connection conn = null;
		I_Initial initial;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			logger.info("Initial System Parameter");
			InitialParameter initParam = new InitialParameter();
			initParam.init(getInitParameter("parameterfile"), getServletContext());
			
			logger.info("Initial System Message");
			initial = new InitialMessages();
			initial.init(conn);
			
			logger.info("Clear Task All");
			InterfaceDAO.clearTaskControlMonitorAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if(conn != null){
					conn.close();
				}
			} catch (Exception e) {}
		}
	}
}
